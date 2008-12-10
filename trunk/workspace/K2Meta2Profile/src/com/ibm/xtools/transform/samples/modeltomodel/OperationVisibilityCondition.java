/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */

package com.ibm.xtools.transform.samples.modeltomodel;

import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.VisibilityKind;

import org.eclipse.emf.query.conditions.Condition;

/**
 * A class that represents a condition that is satisfied for an object if the
 * object is an operation and the visibility of the operation is the same as
 * visibility kind specified during construction.
 *  
 */
public class OperationVisibilityCondition extends Condition {

	private VisibilityKind visibility = null;

	/**
	 * Constructs a condition to assert that a specified object is an operation
	 * and the visibility of the operation is same as visibilityKind.
	 * 
	 * @param visibilityKind
	 *            Kind of visibility. Should one of
	 *            VisibilityKind.PUBLIC_LITERAL, VisibilityKind.PRIVATE_LITERAL,
	 *            VisibilityKind.PROTECTED_LITERAL.
	 */
	public OperationVisibilityCondition(VisibilityKind visibilityKind) {
		super();
		visibility = visibilityKind;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.query.internal.conditions.Condition#isSatisfied(java.lang.Object)
	 */
	public boolean isSatisfied(Object object) {
		return (object instanceof Operation && ((Operation) object)
			.getVisibility() == visibility);
	}
}