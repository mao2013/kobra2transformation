package org.orcas.progenese;

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
import org.orcas.progenese.ocl.OCLUtil;
import org.orcas.progenese.uml2.UML2Util;

public class ProfileGenerator {

	public ProfileGenerator(String metamodelPath, String profilePath) {
		_init(metamodelPath, profilePath);
	}

	private void _init(String metamodelPath, String profilePath) {
		_oclUtil = new OCLUtil();
		_uml2Util = new UML2Util();
		_inputModelPath = metamodelPath;
		_inputModelURI = URI.createURI(_inputModelPath);
		_mainMMPackage = _uml2Util.load(_inputModelURI);
		_mainProfile = _uml2Util.createOrRetrieveProfile(_mainMMPackage
				.getName());
		_uml2Util.registerPathmaps(_inputModelURI);
	}

	public void transform(String outputPath) throws Exception {
		_processResource(_mainMMPackage.getOwnedElements());
		_outputModelPath = outputPath;
		_outputModelURI = URI.createURI(_outputModelPath);
		_uml2Util.defineProfile(_mainProfile);
		_uml2Util.save(_mainProfile, _outputModelURI);
	}

	private void _processResource(EList<Element> elements) throws Exception {

		Stereotype stereotype = null;
		Package tmp = null;
		
		for (Element element : elements) {

			if (element instanceof Package) {

				tmp = (Package) element;

				Package nestingPackage = tmp.getNestingPackage();

				Profile profileTmp = _uml2Util.createOrRetrieveProfile(tmp
						.getName());
				Profile nestingProfile = _uml2Util
						.createOrRetrieveProfile(nestingPackage.getName());
				nestingProfile.define();

				if (!nestingProfile.getNestedPackages().contains(profileTmp)) {
					nestingProfile.getNestedPackages().add(profileTmp);
				}

				// Merge dependencies
				//UMLUtil.merge(tmp, null);

				if (!tmp.getNestedPackages().isEmpty()) {
					_processResource(tmp.getOwnedElements());
				} else {
					processPackage(tmp);
				}

			} else if (element instanceof Association) {

				Association association = (Association) element;

				System.out.println(association.getName());

				EList<Property> ownedEnds = association.getOwnedEnds();

				// Create stereotypes

				for (Property property : ownedEnds) {
					stereotype = _uml2Util.createOrRetrieveStereotype(tmp,
							property.getType().getQualifiedName(), false);
				}

				Constraint constraint = _oclUtil.convertAssociation2Constraint(association);

				Classifier propertyType = (Classifier) ownedEnds.get(0).getType();

				if (constraint != null && stereotype != null) {
					_oclUtil.getOCLHelper().getOCL().validate(constraint);
					propertyType.getOwnedRules().add(constraint);
					_uml2Util.addConstraint2Stereotype(constraint, stereotype);
				}

			} else if (element instanceof Class) {

				stereotype = _uml2Util.createOrRetrieveStereotype(
						(Package) element.getOwner(), ((Class) element)
								.getName(), false);

			} else if (element instanceof Property) {

				Property property = (Property) element;

			}

			// Handle SuperClass Element
			else if (element instanceof Generalization) {

				Generalization generalizationElement = (Generalization) element;
				Classifier classifier = generalizationElement.getGeneral();
				String genericName = classifier.getName();
				String genericQualifiedName = classifier.getQualifiedName();

				// Do not create stereotypes from UML generalizations
				// instead create extensions
				if (genericQualifiedName != null
						&& !genericQualifiedName.contains("UML")) {

					Stereotype genericStereotype = _uml2Util
							.createOrRetrieveStereotype(
									classifier.getPackage(), genericName, false);

					_uml2Util.createGeneralization(stereotype,
							genericStereotype);
				}
			}

			// Handle Constraint Element
			else if (element instanceof Constraint
					&& ((Constraint) element).getSpecification() instanceof OpaqueExpression) {

				Constraint constraint = (Constraint) element;
				OpaqueExpression opaqueExpression = (OpaqueExpression) constraint
						.getSpecification();

				EList<String> bodies = opaqueExpression.getBodies();

				for (String body : bodies) {
					// List<Constraint> constraints =
					// _oclUtil.parseInvariantOCL(_uml2Util.getResourceSet(),
					// (Classifier) type, constraint.getName(), body);

					// _oclUtil.transformOCL((Classifier) type,
					// constraints, _uml2Util.getStereotypes());

				}
			}
		}
	}

	private void processPackage(Package package_) throws Exception {

		List<Type> types = package_.getOwnedTypes();

		for (Type type : types) {

			Stereotype stereotype = null;

			if (type instanceof Association) {

				Association association = (Association) type;

				System.out.println(association.getName());

				EList<Property> ownedEnds = association.getOwnedEnds();

				// Create stereotypes
				/*
				 * for (Property property : ownedEnds) { stereotype =
				 * _uml2Util.createOrRetrieveStereotype( _profile,
				 * property.getType().getQualifiedName(), false); }
				 * 
				 * Constraint constraint =
				 * _oclUtil.convertAssociation2Constraint(
				 * _oclUtil.getOCLHelper(), property);
				 * 
				 * Classifier propertyType = (Classifier) property.getType();
				 * 
				 * if (constraint != null && stereotype != null) {
				 * _oclUtil.getOCLHelper().getOCL().validate(constraint);
				 * propertyType.getOwnedRules().add(constraint);
				 * _uml2Util.addConstraint2Stereotype(constraint,stereotype); }
				 */
			} else if (type instanceof Class) {

				stereotype = _uml2Util.createOrRetrieveStereotype(type
						.getPackage(), type.getName(), false);

				List<Element> elements = type.getOwnedElements();

				for (Element element : elements) {

					// Handle Property Element
					if (element instanceof Property) {

						Property property = (Property) element;

					}
					// Handle SuperClass Element
					else if (element instanceof Generalization) {

						Generalization generalizationElement = (Generalization) element;
						Classifier classifier = generalizationElement
								.getGeneral();
						String genericName = classifier.getName();
						String genericQualifiedName = classifier
								.getQualifiedName();

						// Do not create stereotypes from UML generalizations
						// instead create extensions
						if (genericQualifiedName != null
								&& !genericQualifiedName.contains("UML")) {

							Stereotype genericStereotype = _uml2Util
									.createOrRetrieveStereotype(classifier
											.getPackage(), genericName, false);

							_uml2Util.createGeneralization(stereotype,
									genericStereotype);
						}
					}
					// Handle Constraint Element
					else if (element instanceof Constraint
							&& ((Constraint) element).getSpecification() instanceof OpaqueExpression) {

						Constraint constraint = (Constraint) element;
						OpaqueExpression opaqueExpression = (OpaqueExpression) constraint
								.getSpecification();

						EList<String> bodies = opaqueExpression.getBodies();

						for (String body : bodies) {
							// List<Constraint> constraints =
							// _oclUtil.parseInvariantOCL(_uml2Util.getResourceSet(),
							// (Classifier) type, constraint.getName(), body);

							// _oclUtil.transformOCL((Classifier) type,
							// constraints, _uml2Util.getStereotypes());

						}
					}
				}
			}
		}
	}

	private Package _mainMMPackage;
	private Profile _mainProfile;

	private OCLUtil _oclUtil;
	private UML2Util _uml2Util;

	private URI _inputModelURI;
	private URI _outputModelURI;
	private String _inputModelPath;
	private String _outputModelPath;
}
