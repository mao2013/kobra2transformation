package org.orcas;

import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.uml.ExpressionInOCL;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.ValueSpecification;
import org.orcas.ocl.OCLUtil;
import org.orcas.uml2.UML2Util;

public class TransformationTool {

	public TransformationTool() {
		_init();
	}

	private void _init() {

		_oclUtil = new OCLUtil();
		_uml2Util = new UML2Util();

		_profileName = "KobrA2";
		_inputModelPath = "model/Data.uml";
		_inputConstraintsPath = "model/ocl/K2MM.ocl";
		_outputModelPath = "profile/" + _profileName + ".profile.uml";

		_inputModelURI = URI.createURI(_inputModelPath);
		_outputModelURI = URI.createURI(_outputModelPath);
		_profile = _uml2Util.createProfile(_profileName);
		_processedStereotypes = new HashMap<String, Stereotype>();
		_constraintMap = new HashMap<String, Constraint>();
	}

	public void transform() throws Exception {
		

		// process constraints
		resourceSet = UML2Util.getResourceSet();
		//UML2Util.registerPathmaps(_inputModelURI);
		//UML2Util.registerExtensions();
		//UML2Util.registerPackages(resourceSet);
		
		Package package_ = _uml2Util.load(_inputModelURI);
		//OCLUtil.configureOCL(resourceSet);
		List<Constraint> constraints = _oclUtil.parseOCL(resourceSet, _inputConstraintsPath);

		for (Constraint constraint : constraints) {
			
			ValueSpecification valueSpecification = constraint.getSpecification();

			if (valueSpecification instanceof ExpressionInOCL) {
				ExpressionInOCL expressionInOCL = (ExpressionInOCL) valueSpecification;
				System.out.printf("%s: %s%n", constraint.getName(),
						expressionInOCL.getBodyExpression());

				_constraintMap.put(constraint.getName(), constraint);
			}
		}

		_processResource(package_.getOwnedElements());
		_uml2Util.defineProfile(_profile);
		//_uml2Util.referenceMetaclass(_profile, "uml");
		_uml2Util.save(_profile, _outputModelURI);

	}
	
	private void _processResource(EList<Element> elements) {

		for (Element element : elements) {

			if (element instanceof Package) {

				Package tmp = (Package) element;
				if (!tmp.getNestedPackages().isEmpty()) {
					_processResource(tmp.getOwnedElements());
				} else {
					_processPackage(tmp);
				}
			}
		}
	}

	private void _processPackage(Package package_) {

		Constraint constraint = null;
		List<Type> types = package_.getOwnedTypes();

		for (Type type : types) {
			Association a = null;
			boolean isAssociation = (type instanceof Association);

			List<Element> elements = null;
			Stereotype stereotype = null;

			if (isAssociation) {
				a = (Association) type;

				stereotype = _uml2Util.createStereotype(_profile,
						_buildQualifiedName(package_.getQualifiedName(), a
								.getOwnedEnds().get(0).getType().getName()),
						false);
				_processedStereotypes.put(stereotype.getName(), stereotype);

				stereotype = _uml2Util.createStereotype(_profile,
						_buildQualifiedName(package_.getQualifiedName(), a
								.getOwnedEnds().get(1).getType().getName()),
						false);

				_processedStereotypes.put(stereotype.getName(), stereotype);
			} else {
				stereotype = _uml2Util.createStereotype(_profile,
						_buildQualifiedName(package_.getQualifiedName(), type
								.getName()), false);
				_processedStereotypes.put(stereotype.getName(), stereotype);
			}

			elements = type.getOwnedElements();

			for (Element element : elements) {

				if (element instanceof Property) {

					Property property = (Property) element;

					try {
						constraint = _oclUtil.convertAssociation2Constraint(
								OCLUtil.getOCLHelper(), property);
						Classifier propertyType = (Classifier) property
								.getType();

						if (constraint != null && stereotype != null) {
							OCLUtil.getOCLHelper().getOCL().validate(constraint);
							propertyType.getOwnedRules().add(constraint);
							_constraintMap.put(constraint.getName(), constraint);
							_uml2Util.addConstraint2Stereotype(constraint,stereotype);

						}
					} catch (ParserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// handle superclasses
				if (element instanceof Generalization) {
					Generalization genericType = (Generalization) element;

					Classifier classifier = genericType.getGeneral();

					if (classifier.eIsProxy()) {
						EObject proxy = classifier;
						URI proxyURI = org.eclipse.emf.ecore.util.EcoreUtil
								.getURI(proxy);
						Resource referencedResource = UML2Util.getResourceSet()
								.getResource(proxyURI.trimFragment(), true);
						EObject referencedObject = referencedResource
								.getResourceSet().getEObject(
										proxyURI.resolve(proxyURI, true), true);
					}
					String genericFullQualifiedName = _buildQualifiedName(
							package_.getQualifiedName(), genericType
									.getGeneral().getName());

					if (!_processedStereotypes
							.containsKey(genericFullQualifiedName)) {
						Stereotype genericStereotype = _uml2Util
								.createStereotype(_profile,
										genericFullQualifiedName, false);

						if (genericStereotype != null)
							_processedStereotypes.put(genericFullQualifiedName,
									genericStereotype);
					}

					if (stereotype != null)
						_uml2Util.createGeneralization(stereotype,
								_processedStereotypes
										.get(genericFullQualifiedName));
				}
			}
		}
	}

	// }

	private String _buildQualifiedName(String prefix, String name) {
		return prefix.replaceAll("\\.", "\\::") + "::" + name;
	}

	public static void main(String[] args) throws Exception {
		new TransformationTool().transform();
	}

	private Profile _profile;
	private String _profileName;
	private OCLUtil _oclUtil;
	private UML2Util _uml2Util;

	private URI _inputModelURI;
	private URI _outputModelURI;
	private String _inputModelPath;
	private String _inputConstraintsPath;
	private String _outputModelPath;
	private ResourceSet resourceSet;

	private HashMap<String, Stereotype> _processedStereotypes;
	private HashMap<String, Constraint> _constraintMap;
}
