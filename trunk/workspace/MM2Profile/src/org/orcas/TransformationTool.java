package org.orcas;

import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.URIConverterImpl;
import org.eclipse.ocl.uml.ExpressionInOCL;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageMerge;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.ValueSpecification;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.util.UMLUtil;
import org.orcas.ecore.EcoreUtil;
import org.orcas.ocl.OCLUtil;
import org.orcas.uml2.UML2Util;

public class TransformationTool {

	public TransformationTool() {
		_init();
	}

	private void _init() {

		//_ecoreUtil = new EcoreUtil();
		_oclUtil = new OCLUtil();
		_uml2Util = new UML2Util();

		_profileName = "KobrA2";
		_inputModelPath = "model/Kobra2MM.uml";
		_inputConstraintsPath = "model/ocl/K2Elements.ocl";
		_outputModelPath = "profile/" + _profileName + ".uml";

		_inputModelURI = URI.createURI(_inputModelPath);
		_outputModelURI = URI.createURI(_outputModelPath);

		_profile = _uml2Util.createProfile(_profileName);

		_processedStereotypes = new HashMap<String, Stereotype>();
		_constraintMap = new HashMap<String, Constraint>();
	}

	public void transform() throws Exception {

		Package package_ = _uml2Util.load(_inputModelURI);

		// process constraints
		resourceSet = UML2Util.getResourceSet();
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

		List<Type> types = package_.getOwnedTypes();
		List<PackageMerge> merges = package_.getPackageMerges();

		if (merges.size() > 0) {
			Package receivingPackage = merges.get(0).getMergedPackage();
			if (receivingPackage != null) {
				for (PackageableElement element : receivingPackage
						.getPackagedElements()) {
					System.out
							.println("element: " + element.getQualifiedName());
				}
			}
		}

		for (Type type : types) {

			Stereotype stereotype = _uml2Util.createStereotype(_profile,
					_buildQualifiedName(package_.getQualifiedName(), type
							.getName()), false);
			_processedStereotypes.put(stereotype.getName(), stereotype);
			List<Element> elements = type.getOwnedElements();

			for (Element element : elements) {

				// handle superclasses
				if (element instanceof Generalization) {
					Generalization genericType = (Generalization) element;

					//List<EObject> references = genericType.eCrossReferences();
					//EReference eref = (EReference) references.get(0);

					//for (EObject object : references) {
					//	if (object instanceof EReference)
					//		System.out.println("Reference: " + object);
					//}

					Classifier classifier = genericType.getGeneral();
					
					if (classifier.eIsProxy()){
						EObject proxy = classifier;
						URI proxyURI = org.eclipse.emf.ecore.util.EcoreUtil.getURI(proxy);
						Resource referencedResource = UML2Util.getResourceSet().getResource(proxyURI.trimFragment(), true);
						EObject referencedObject = referencedResource.getResourceSet().getEObject(proxyURI.resolve(proxyURI, true), true);
					}
					String genericFullQualifiedName = _buildQualifiedName(package_.getQualifiedName(), genericType.getGeneral().getName());

					if (!_processedStereotypes
							.containsKey(genericFullQualifiedName)) {
						Stereotype genericStereotype = _uml2Util
								.createStereotype(_profile,
										genericFullQualifiedName, false);
						_processedStereotypes.put(genericFullQualifiedName,
								genericStereotype);
					}
					_uml2Util
							.createGeneralization(stereotype,
									_processedStereotypes
											.get(genericFullQualifiedName));
				}
			}
		}
	}

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
