/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */


/*
 * File: CreateFactoryClassRule.java
 */

package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.Usage;

import com.ibm.xtools.transform.core.ITransformContext;
import com.ibm.xtools.transform.core.ModelRule;
import com.ibm.xtools.transform.samples.modeltomodel.IsTopLevelClassCondition;
import com.ibm.xtools.transform.samples.modeltomodel.ModelUtility;
import com.ibm.xtools.transform.samples.modeltomodel.l10n.ResourceManager;
import com.ibm.xtools.uml.transform.core.IsElementKindCondition;

/**
 * A rule that accepts a top level class as the source and creates a factory class and
 * creates the necessary usage relationship.
 * 
 * A top level class is a class that is contained directly by a package or a model.
 *  
 */
public class CreateFactoryClassRule
	extends ModelRule {

	public static final String ID = "classtoservice.create.factory.rule"; //$NON-NLS-1$

	public static final String NAME = ResourceManager
		.getString("CreateFactoryClassRule.name"); //$NON-NLS-1$

	/**
     * Default constructor. 
     * 
     * Initializes the id and the name of the rule to
     * their default values. It also initializes it to accept only
     * a top level UML2 class as source.  A top level class is a class that
     * is contained directly by a package or a model.
     */
    public CreateFactoryClassRule() {
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
	public CreateFactoryClassRule(String id, String name) {
		super(id, name);
		setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE
			.getClass_()).AND(new IsTopLevelClassCondition()));
	}

	/*
	 * Factory Rule: Create the factory method with a return parameter.
	 * 
	 * @see com.ibm.xtools.transform.core.AbstractRule#createTarget(com.ibm.xtools.transform.core.ITransformContext)
	 */
	protected Object createTarget(ITransformContext ruleContext) {
		UMLPackage uml = UMLPackage.eINSTANCE;
		Class cls = (Class) ruleContext.getSource();
		String className = cls.getName();

		Package pkg = (Package) ruleContext.getPropertyValue("targetPackage"); //$NON-NLS-1$
		Interface intface = ModelUtility.getInterfaceByName(pkg,
			"I" + className); //$NON-NLS-1$
		Class impl = ModelUtility.getClassByName(pkg, className + "Impl"); //$NON-NLS-1$
		Class factory = null;

		if (pkg != null && intface != null) {
			String factoryClassName = className + "Factory"; //$NON-NLS-1$
			factory = ModelUtility.getClassByName(pkg, factoryClassName);

			if (factory == null) {
				factory = pkg.createOwnedClass(factoryClassName, false);

				Operation factoryMethod =
                    factory.createOwnedOperation("create", null, null); //$NON-NLS-1$
				factoryMethod.setIsStatic(true);
				factoryMethod.createReturnResult(null, intface);

				// Set up an <<Instantiate>> dependency between the factory and
				// the
				// implementation class
				Usage u = (Usage) pkg.createPackagedElement(null, uml.getUsage());
				u.getClients().add(factory);
				u.getSuppliers().add(impl);
				Stereotype s = u.getApplicableStereotype("Standard::Instantiate"); //$NON-NLS-1$
				u.applyStereotype(s);
			}
		}

		System.out.println(ID + " is executed"); //$NON-NLS-1$

		return factory;
	}

}