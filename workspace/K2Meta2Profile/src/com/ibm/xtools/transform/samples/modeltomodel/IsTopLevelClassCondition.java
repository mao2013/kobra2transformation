/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */





/*
 * File: IsTopLevelClassCondition.java
 */
package com.ibm.xtools.transform.samples.modeltomodel;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.UMLPackage;

import org.eclipse.emf.query.conditions.Condition;


/**
 * A condition that is satisfied if a specified object is an UML class is
 * contained directly by a package or a model. In other words, the condition
 * is true if the specified object is a top-level class.
 */
public class IsTopLevelClassCondition extends Condition {

	/**
	 * Default constructor - creates an instance of the condition.
	 */
	public IsTopLevelClassCondition() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.query.conditions.Condition#isSatisfied(java.lang.Object)
	 */
	public boolean isSatisfied(Object object) {
		boolean result = false;
        UMLPackage uml2 = UMLPackage.eINSTANCE;
        
        if (object instanceof EObject) {
            EObject element = (EObject) object;
            EClass elementKind = element.eClass();
            EClass containerKind = element.eContainer().eClass();
            
            // Condition is satisfied if the input object is a UML class and
            // it is contained directly by a package or a model.
        	result = (elementKind == uml2.getClass_()) &&
                     (containerKind == uml2.getPackage() ||
                      containerKind == uml2.getModel());
        }
        
        return result;
	}
}
