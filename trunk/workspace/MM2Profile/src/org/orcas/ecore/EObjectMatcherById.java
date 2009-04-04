package org.orcas.ecore;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.common.util.UML2Util.EObjectMatcher;

public class EObjectMatcherById implements EObjectMatcher {

	public EObjectMatcherById(String id){
		_id = id;
	}
	
	public boolean matches(EObject eObject){
		return false;		
	}
	
	private String _id;
}
