package org.orcas;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.OpaqueExpression;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.util.UMLUtil;
import org.orcas.ocl.OCLUtil;
import org.orcas.uml2.UML2Util;

public class TransformationTool {

	public TransformationTool() {
		_init();
	}

	private void _init() {

		_oclUtil = new OCLUtil();
		_uml2Util = new UML2Util();

		_profileName = "KobrA2Profile";
		_inputModelPath = "model/Data.uml";
		_outputModelPath = "profile/" + _profileName + ".uml";

		_inputModelURI = URI.createURI(_inputModelPath);
		_outputModelURI = URI.createURI(_outputModelPath);

		_profile = _uml2Util.createProfile(_profileName);
	}

	public void transform() throws Exception {
		
		Package package_ = _uml2Util.load(_inputModelURI);

		_processResource(package_.getOwnedElements());

		_uml2Util.defineProfile(_profile);
		_uml2Util.save(_profile, _outputModelURI);
	}
	
	private void _processResource(EList<Element> elements) throws Exception {

		for (Element element : elements) {
			if (element instanceof Package) {
				Package tmp = (Package) element;
				
				// Merge dependencies
				UMLUtil.merge(tmp, null);
				
				if (!tmp.getNestedPackages().isEmpty()) {
					_processResource(tmp.getOwnedElements());
				} else {
					_processPackage(tmp);
				}
			}
		}
	}

	private void _processPackage(Package package_) throws Exception {

		List<Type> types = package_.getOwnedTypes();

		for (Type type : types) {

			Stereotype stereotype = null;

			if (type instanceof Association) {
				
				Association association = (Association) type;
				
				EList<Property> ownedEnds = association.getOwnedEnds();
				
				for (Property property : ownedEnds) {
					stereotype =
						_uml2Util.createOrRetrieveStereotype(
							_profile, property.getType().getQualifiedName(), false);
				}

			} else if (type instanceof Class) {
				
				stereotype = 
					_uml2Util.createOrRetrieveStereotype(_profile, type.getQualifiedName(), false);
				
				List<Element> elements = type.getOwnedElements();
				
				for (Element element : elements) {
					
					// Handle Property Element
					if (element instanceof Property ){
						
						Property property = (Property) element;
						
						Constraint constraint = 
							_oclUtil.convertAssociation2Constraint(	_oclUtil.getOCLHelper(), property);
						
						Classifier propertyType = (Classifier) property.getType();

						if (constraint != null && stereotype != null) {
							_oclUtil.getOCLHelper().getOCL().validate(constraint);
							propertyType.getOwnedRules().add(constraint);
							_uml2Util.addConstraint2Stereotype(constraint,stereotype);
						}
					
					}
					// Handle SuperClass Element
					else if (element instanceof Generalization) {

						Generalization generalizationElement = (Generalization) element;
						Classifier classifier = generalizationElement.getGeneral();
						String genericQualifiedName = classifier.getQualifiedName();
						
						// Do not create stereotypes from UML metaclasses instead create extensions
						if (genericQualifiedName != null && genericQualifiedName.contains("UML")) {
							//_uml2Util.referenceMetaclass(_profile, classifier);
							//_uml2Util.createExtension( (org.eclipse.uml2.uml.Class) classifier, stereotype, false);
						}
						else {
							
							Stereotype genericStereotype =
								_uml2Util.createOrRetrieveStereotype(_profile, genericQualifiedName, false);

							_uml2Util.createGeneralization(stereotype, genericStereotype);
						}
					}
					// Handle Constraint Element
					else if (element instanceof Constraint &&
							((Constraint) element).getSpecification() instanceof OpaqueExpression){
						
						Constraint constraint = (Constraint) element;
						OpaqueExpression opaqueExpression = (OpaqueExpression) constraint.getSpecification();
						
						EList<String> bodies = opaqueExpression.getBodies();
						
						for (String body : bodies) {
							List<Constraint> constraints = 
								_oclUtil.parseOCL(_uml2Util.getResourceSet(), (Classifier) type, constraint.getName(), body);
							
							_oclUtil.transformOCL((Classifier) type, constraints, _uml2Util.getStereotypes());
							
						}
					}
				}
			}
		}
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
	private String _outputModelPath;
}
