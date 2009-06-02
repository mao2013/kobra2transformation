
package org.orcas.uml2.handlers;

import org.eclipse.emf.ecore.EObject;
import org.orcas.progenese.uml2.UML2Util;

public interface ElementHandler {
	
	public void handleElement(EObject element, UML2Util uml2Util);
}
