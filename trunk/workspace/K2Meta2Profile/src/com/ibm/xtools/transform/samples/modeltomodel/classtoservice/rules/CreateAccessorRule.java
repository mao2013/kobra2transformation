/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */

package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLPackage;

import com.ibm.xtools.transform.core.ITransformContext;
import com.ibm.xtools.transform.core.ModelRule;
import com.ibm.xtools.transform.samples.modeltomodel.ModelUtility;
import com.ibm.xtools.transform.samples.modeltomodel.l10n.ResourceManager;
import com.ibm.xtools.uml.transform.core.IsElementKindCondition;

/**
 * A rule that accepts a property as the source and creates accessor (get ans
 * set) methods in the target class or interface supplied in the transformation
 * context.
 *  
 */
public class CreateAccessorRule
	extends ModelRule {

	public static final String ID = "classtoservice.accessor.rule"; //$NON-NLS-1$

	public static final String NAME = ResourceManager
		.getString("CreateAccessorRule.name"); //$NON-NLS-1$

	/**
     * Default constructor. Initializes the id and the name of the rule to
     * their default values.
     */
    public CreateAccessorRule() {
    	super(ID, NAME);
    	setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE
            .getProperty()));
    }
	
    /**
     * Constructor.
     * 
     * Creates an instance of the rule and initializes it to accept only
     * a UML2 property as source.
     * 
	 * @param id
	 *            Unique id of the rule
	 * @param name
	 *            Readable name of the rule
	 */
	public CreateAccessorRule(String id, String name) {
		super(id, name);
		setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE
			.getProperty()));
	}

	/* (non-Javadoc)
	 * @see com.ibm.xtools.transform.core.AbstractRule#createTarget(com.ibm.xtools.transform.core.ITransformContext)
	 */
	protected Object createTarget(ITransformContext ruleContext) {
		UMLPackage uml2 = UMLPackage.eINSTANCE;
		Property srcProperty = (Property) ruleContext.getSource();
		EObject container = (EObject) ruleContext.getTargetContainer();

		if (container != null) {
			EClass containerKind = container.eClass();

			Operation getter = ModelUtility.createGetter(srcProperty);
			Operation setter = ModelUtility.createSetter(srcProperty);

			if (containerKind == uml2.getClass_()) {
				if (ModelUtility.classContainsOperation((Class) container,
					getter) == false) {
					((Class) container).getOwnedOperations().add(getter);
				}
				if (ModelUtility.classContainsOperation((Class) container,
					setter) == false) {
					((Class) container).getOwnedOperations().add(setter);
				}
			} else if (containerKind == uml2.getInterface()) {
				if (ModelUtility.interfaceContainsOperation(
					(Interface) container, getter) == false) {
					((Interface) container).getOwnedOperations().add(getter);
				}
				if (ModelUtility.interfaceContainsOperation(
					(Interface) container, setter) == false) {
					((Interface) container).getOwnedOperations().add(setter);
				}
			}
		}

		System.out.println(ID + " is executed"); //$NON-NLS-1$

		return null;
	}

}