package org.orcas.uml2.handlers;

import org.eclipse.emf.ecore.EObject;
import org.orcas.progenese.uml2.UML2Util;


public class AssociationHandler implements ElementHandler {

	@Override
	public void handleElement(EObject element, UML2Util uml2Util) {
		/*Association association = (Association) element;
		EList<Property> ownedEnds = association.getMemberEnds();
		Property roleA = null;
		
		if (!ownedEnds.isEmpty()){
		    roleA = ownedEnds.get(0);
		}
		try {
			OCLUtil oclUtil = OCLUtil.newInstance();
			oclUtil.convertAssociation2Constraint(oclUtil.getOCLHelper(), association);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (Property property : ownedEnds) {
			UML2Util.debug("association property: " + property.getName());
		}*/
	}
}