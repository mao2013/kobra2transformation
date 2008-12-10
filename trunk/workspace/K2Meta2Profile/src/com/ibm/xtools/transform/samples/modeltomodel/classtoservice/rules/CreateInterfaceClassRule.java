
/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */

/*
 * File: CreateInterfaceClassRule.java
 */

package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLPackage;

import com.ibm.xtools.transform.core.ITransformContext;
import com.ibm.xtools.transform.core.ModelRule;
import com.ibm.xtools.transform.samples.modeltomodel.IsTopLevelClassCondition;
import com.ibm.xtools.transform.samples.modeltomodel.ModelUtility;
import com.ibm.xtools.transform.samples.modeltomodel.l10n.ResourceManager;
import com.ibm.xtools.uml.transform.core.IsElementKindCondition;

/**
 * A rule to create an interface in a package. The target package is assumed to
 * be configured as a transformation property.
 *  
 */
public class CreateInterfaceClassRule
	extends ModelRule {

	public static final String ID = "class2service.create.interface.class.rule"; //$NON-NLS-1$

	public static final String NAME = ResourceManager
		.getString("CreateInterfaceClassRule.name"); //$NON-NLS-1$

	/**
     * Default constructor. 
     * 
     * Initializes the id and the name of the rule to
     * their default values. It also initializes it to accept only
     * a top level UML2 class as source.  A top level class is a class that
     * is contained directly by a package or a model.
     */
    public CreateInterfaceClassRule() {
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
	public CreateInterfaceClassRule(String id, String name) {
		super(id, name);
		setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE
			.getClass_()).AND(new IsTopLevelClassCondition()));
	}

	/* (non-Javadoc)
	 * @see com.ibm.xtools.transform.core.AbstractRule#createTarget(com.ibm.xtools.transform.core.ITransformContext)
	 */
	protected Object createTarget(ITransformContext ruleContext) {

		final String srcName = ((Class) ruleContext.getSource()).getName();
		final Package pkg = (Package) ruleContext.getPropertyValue("targetPackage"); //$NON-NLS-1$
		Interface targetInterface = null;

		if (pkg != null) {
			String targetInterfaceName = "I" + srcName; //$NON-NLS-1$
			targetInterface = ModelUtility.getInterfaceByName(pkg,
				targetInterfaceName);
			if (targetInterface == null) {
				targetInterface = pkg.createOwnedInterface(targetInterfaceName);
			}
		}

		System.out.println(ID + " is executed"); //$NON-NLS-1$

		return targetInterface;
	}

}