/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */

package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.UMLPackage;

import com.ibm.xtools.transform.core.ITransformContext;
import com.ibm.xtools.transform.core.ModelRule;
import com.ibm.xtools.transform.samples.modeltomodel.ModelUtility;
import com.ibm.xtools.transform.samples.modeltomodel.l10n.ResourceManager;
import com.ibm.xtools.uml.transform.core.IsElementKindCondition;

/**
 * A rule to copy an operation in a source class to a desination class. This
 * rule only copies the operation name and the visibility of the operation and
 * does not copy the parameters,
 *  
 */
public class OperationRule
	extends ModelRule {

	public static final String ID = "classtoservice.operation.rule"; //$NON-NLS-1$

	public static final String NAME = ResourceManager
		.getString("OperationRule.name"); //$NON-NLS-1$

	/**
     * Default constructor. Initializes the id and the name of the rule to
     * their default values.
     */
    public OperationRule() {
    	super(ID, NAME);
    	setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE
            .getOperation()));
    }
	
    /**
     * Constructor.
     * 
     * Creates an instance of the rule and initializes it to accept only
     * a UML2 operation as source.
     * 
	 * @param id
	 *            Unique id of the rule
	 * @param name
	 *            Readable name of the rule
	 */
	public OperationRule(String id, String name) {
		super(id, name);
		setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE
			.getOperation()));
	}

	/* (non-Javadoc)
	 * @see com.ibm.xtools.transform.core.AbstractRule#createTarget(com.ibm.xtools.transform.core.ITransformContext)
	 */
	protected Object createTarget(ITransformContext ruleContext) {
		UMLPackage uml = UMLPackage.eINSTANCE;
		Operation srcOp = (Operation) ruleContext.getSource();
		Operation targetOp = null;

		EObject container = (EObject) ruleContext.getTargetContainer();
		if (container.eClass() == uml.getClass_()) {
			if (ModelUtility.classContainsOperation((Class) container, srcOp) == false) {
				targetOp = ((Class) container).createOwnedOperation(srcOp.getName(), null, null);
			}
		} else if (container.eClass() == uml.getInterface()) {
			if (ModelUtility.interfaceContainsOperation((Interface) container, srcOp) == false) {
				targetOp = ((Interface) container).createOwnedOperation(srcOp.getName(), null, null);
			}
		}

		if (targetOp != null) {
			targetOp.setVisibility(srcOp.getVisibility());
		}

		System.out.println(ID + " is executed"); //$NON-NLS-1$

		return targetOp;
	}
}