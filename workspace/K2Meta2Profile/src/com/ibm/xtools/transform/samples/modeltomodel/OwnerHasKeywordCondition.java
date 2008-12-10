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
 * given has the specified keyword.
 */
public class OwnerHasKeywordCondition extends Condition {

	/**
	 * The keyword.
	 */
	private String keyword = null;

	/**
	 * Constructor.
	 * 
	 * @param keyword
	 *            The keyword. It shouldn't be <code>null</code>.
	 */
	public OwnerHasKeywordCondition(String keyword) {
		super();
		this.keyword = keyword;
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
			result = srcElement.getOwner().hasKeyword(keyword);
		}
		return result;
	}

}
