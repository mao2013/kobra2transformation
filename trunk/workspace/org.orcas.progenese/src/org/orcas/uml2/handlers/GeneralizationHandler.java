package org.orcas.uml2.handlers;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Constraint;
import org.orcas.progenese.uml2.UML2Util;

public class GeneralizationHandler implements ElementHandler {

	@Override
	public void handleElement(EObject element, UML2Util uml2Util) {
		Constraint constraint = (Constraint) element;
	}

}
