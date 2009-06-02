package org.orcas.uml2.handlers;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.util.UMLUtil;

public class PackageHandler implements ElementHandler {

	@Override
	public void handleElement(EObject element, org.orcas.progenese.uml2.UML2Util uml2Util) {
		Package tmp = (Package) element;
		Package nestingPackage = tmp.getNestingPackage();
		Profile profileTmp = uml2Util.createOrRetrieveProfile(tmp.getName());
		profileTmp.define();
		Profile nestingProfile = uml2Util.createOrRetrieveProfile(nestingPackage.getName());
		nestingProfile.define();
		nestingProfile.getNestedPackages().add(profileTmp);
		
		// Merge dependencies
		UMLUtil.merge(tmp, null);
		
		if (!tmp.getNestedPackages().isEmpty()) {
			//uml2Util.processResource(tmp.getOwnedElements());
		} else {
			//uml2Util.processPackage(tmp);
		}
	}
}