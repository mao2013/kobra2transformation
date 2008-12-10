/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003 - 2006. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */


/*
 * File: CreateRealizationRule.java
 */

package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.InterfaceRealization;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLPackage;

import com.ibm.xtools.transform.core.ITransformContext;
import com.ibm.xtools.transform.core.ModelRule;
import com.ibm.xtools.transform.samples.modeltomodel.IsTopLevelClassCondition;
import com.ibm.xtools.transform.samples.modeltomodel.ModelUtility;
import com.ibm.xtools.transform.samples.modeltomodel.l10n.ResourceManager;
import com.ibm.xtools.uml.transform.core.IsElementKindCondition;

/**
 * A rule to create a realization relationship between an interface and a class
 * implementing the interface.
 *  
 */
public class CreateRealizationRule
	extends ModelRule {

	public static final String ID = "classtoservice.realization.rule"; //$NON-NLS-1$

	public static final String NAME = ResourceManager
		.getString("CreateRealizationRule.name"); //$NON-NLS-1$

	/**
     * Default constructor. 
     * 
     * Initializes the id and the name of the rule to
     * their default values. It also initializes it to accept only
     * a top level UML2 class as source.  A top level class is a class that
     * is contained directly by a package or a model.
     */
    public CreateRealizationRule() {
    	super(ID, NAME);
    	setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE
			.getClass_()).AND(new IsTopLevelClassCondition()));
    }
	
    /**
     * Constructor.
     * 
     * Creates an instance of the rule and initializes it to accept only
     * a top level UML2 class as source.  A top level class is a class that
     * is contained directly by a package or a model.
     * 
	 * @param id
	 *            Unique id of the rule
	 * @param name
	 *            Readable name of the rule
	 */
	public CreateRealizationRule(String id, String name) {
		super(id, name);
		setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE
			.getClass_()).AND(new IsTopLevelClassCondition()));
	}

	/* (non-Javadoc)
	 * @see com.ibm.xtools.transform.core.AbstractRule#createTarget(com.ibm.xtools.transform.core.ITransformContext)
	 */
	protected Object createTarget(ITransformContext ruleContext) {

		Class cls = (Class) ruleContext.getSource();
		String className = cls.getName();

		Package pkg = (Package) ruleContext.getPropertyValue("targetPackage"); //$NON-NLS-1$
		Interface intface = ModelUtility.getInterfaceByName(pkg,
			"I" + className); //$NON-NLS-1$
		Class impl = ModelUtility.getClassByName(pkg, className + "Impl"); //$NON-NLS-1$
		InterfaceRealization realization = null;

		// Set Realization relationship between interface and impl
		if (pkg != null && intface != null && impl != null) {
			realization = ModelUtility.getRealization(impl, intface);
			if (realization == null) {
				realization =
                    (InterfaceRealization) pkg.createPackagedElement(
                        null, UMLPackage.eINSTANCE.getInterfaceRealization());
				realization.setImplementingClassifier(impl);
				realization.setContract(intface);
			}
		}

		System.out.println(ID + " is executed"); //$NON-NLS-1$

		return realization;
	}

}