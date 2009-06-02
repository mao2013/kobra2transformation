package org.orcas.uml2.handlers;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.ProfileApplication;
import org.orcas.progenese.uml2.UML2Util;

public class ProfileApplicationHandler implements ElementHandler {

	@Override
	public void handleElement(EObject element, UML2Util uml2Util) {
		Package pkg = (Package) element;
		EList<PackageableElement> elements = pkg.getPackagedElements();
		ProfileApplication pa = null;
		Profile profile = null;
		for (PackageableElement packageableElement : elements) {
             if (packageableElement instanceof ProfileApplication){
            	 System.out.println("ProfileApplication: " + packageableElement);
            	 pa = (ProfileApplication) packageableElement;
            	 profile = pa.getAppliedProfile();
            	 System.out.println("Profile: " + profile);
             }
		}
	}
}
