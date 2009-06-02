package org.orcas.uml2.handlers;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.OpaqueExpression;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.orcas.progenese.uml2.UML2Util;

public class ClassHandler implements ElementHandler {

	
	
	@Override
	public void handleElement(EObject element, UML2Util uml2Util) {
		Class c = (Class) element;
		Stereotype stereotype = uml2Util.createOrRetrieveStereotype((Profile)c.getPackage(), c.getName(), c.isAbstract());
		Class c2 = uml2Util.referenceMetaclass((Profile)c.getOwner(), c.getName());
		uml2Util.createExtension(c2, stereotype, false);
		List<Element> elements = c.getOwnedElements();
		for (Element e : elements) {
			
			// Handle Property Element
			if (e instanceof Property ){
				Property property = (Property) e;
			}
			// Handle SuperClass Element
			else if (e instanceof Generalization) {

				Generalization generalizationElement = (Generalization) e;
				Classifier classifier = generalizationElement.getGeneral();
				String genericName = classifier.getName();
				String genericQualifiedName = classifier.getQualifiedName();
				
				// Do not create stereotypes from UML generalizations instead create extensions
				if (!genericQualifiedName.contains("UML")) {
												
					Stereotype genericStereotype = uml2Util.createOrRetrieveStereotype(classifier.getPackage(), genericName, false);
					uml2Util.createGeneralization(stereotype, genericStereotype);
		
				}
			}
			// Handles Constraint Element
			else if (element instanceof Constraint && ((Constraint) element).getSpecification() instanceof OpaqueExpression){
				
				Constraint constraint = (Constraint) e;
				OpaqueExpression opaqueExpression = (OpaqueExpression) constraint.getSpecification();
				opaqueExpression.getBodies().add(opaqueExpression.getBodies().get(0));
				opaqueExpression.getLanguages().add("OCL");
				uml2Util.addConstraint2Stereotype(constraint, stereotype);
				
				//for (String body : bodies) {
					//List<Constraint> constraints = 
					//	_oclUtil.parseInvariantOCL(_uml2Util.getResourceSet(), (Classifier) type, constraint.getName(), body);
					
					//_oclUtil.transformOCL((Classifier) type, constraints, _uml2Util.getStereotypes());
					
				//}
			}
		}
	}
}