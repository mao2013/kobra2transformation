package org.orcas.uml2.handlers;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.ProfileApplication;

public class ElementHandlerFactory {

	public static ElementHandlerFactory newInstance() {
		return new ElementHandlerFactory();
	}

	/**
	 * Factory method used to create specific ElementHandlers
	 * 
	 * @param element
	 *        The EObject element to be handled
	 *        
	 * @return The <code>ElementHandler</code> implementation according to <code>element</code> type
	 */
	public ElementHandler createElementHandler(EObject element) {

		if (element != null) {
			if (element instanceof org.eclipse.uml2.uml.Package)
				return new PackageHandler();
			else if (element instanceof Generalization)
				return new GeneralizationHandler();
			else if (element instanceof Constraint)
				return new ConstraintHandler();
			else if (element instanceof org.eclipse.uml2.uml.Class)
				return new ClassHandler();
			else if (element instanceof Association)
				return new AssociationHandler();
			else if (element instanceof Enumeration)
				return new EnumerationHandler();
			else if (element instanceof ProfileApplication)
				return new ProfileApplicationHandler();
			else throw new IllegalArgumentException("The implementation for the ElementHandler (" + element + ") is not supported yet.");
		}
		return null;
	}
}