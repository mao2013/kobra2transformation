/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */

package com.ibm.xtools.transform.samples.modeltomodel;

import org.eclipse.uml2.uml.Element;

import org.eclipse.emf.query.conditions.Condition;

/**
 * This condition class is satisfied if the owner of the UML2 element it is
 * given has the specified stereotype. 
 * 
 * The stereotype is specified by supplying the constructor the qualified name
 * of the stereotype. Qualified names are of the form
 * profileName::stereotypeName.
 */
public class OwnerHasStereotypeCondition extends Condition {

	/**
	 * Qualified name of the stereotype. Qualified names are of the form
	 * profileName::stereotypeName.
	 */
	private String qualifiedStereotypeName = null;

	/**
	 * Constructor.
	 * 
	 * @param qualifiedStereotypeName
	 *            Qualified name of the stereotype. Qualified names are of the
	 *            form profileName::stereotypeName. Should not be
	 *            <code>null</code>.
	 */
	public OwnerHasStereotypeCondition(String qualifiedStereotypeName) {
		super();
		this.qualifiedStereotypeName = qualifiedStereotypeName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.query.conditions.Condition#isSatisfied(java.lang.Object)
	 */
	public boolean isSatisfied(Object object) {
		boolean result = false;
		if (object instanceof Element) {
			Element srcElement = (Element) object;
			if (srcElement.getOwner().getAppliedStereotype(qualifiedStereotypeName) != null)
				result = true;
		}
		return result;
	}

}
