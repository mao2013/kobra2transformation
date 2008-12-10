/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003 - 2006. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */

/*
 * File: ClassRule.java
 */

package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules;

import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.InterfaceRealization;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.Usage;
import org.eclipse.uml2.uml.VisibilityKind;

import com.ibm.xtools.transform.core.ITransformContext;
import com.ibm.xtools.transform.core.ModelRule;
import com.ibm.xtools.transform.samples.modeltomodel.IsTopLevelClassCondition;
import com.ibm.xtools.transform.samples.modeltomodel.ModelUtility;
import com.ibm.xtools.transform.samples.modeltomodel.l10n.ResourceManager;
import com.ibm.xtools.uml.transform.core.IsElementKindCondition;

/**
 * A rule class that accepts a top level class as source and creates an
 * interface, an  implementation class and a factory class. It also
 * establishes the realization relationship between the generated interface
 * and the implementation class.
 * 
 * A top level class is a class that is contained directly by a package or a
 * model. An example of a class that is not a top level class is an inner
 * class. This rule ignores such classes.
 * 
 * This is an examp[le of a monolithic rule that does everything. This is
 * adequate for a rule that will not be reused. Ideally, one would break this
 * monolothic rule into a set of smaller rules each handling a particular type
 * of UML model element.
 *  
 */

public class ClassRule
	extends ModelRule {

	public static final String ID = "classtoservice.class.rule"; //$NON-NLS-1$

	public static final String NAME = ResourceManager
		.getString("ClassRule.name"); //$NON-NLS-1$

	/**
	 * Default constructor. Initializes the id and the name of the rule to
	 * their default values. It also initializes it to accept only a top level
	 * UML2 class as source. A top level class is a class that is contained
	 * directly by a package or a model.
	 */
	public ClassRule() {
		super(ID, NAME);
		setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE
			.getClass_()).AND(new IsTopLevelClassCondition()));
	}

	/**
	 * Constructor.
     * 
     * Creates an instance of the rule and initializes it to accept only
     * a top level UML2 class as source. A top level class is a class that is contained
	 * directly by a package or a model.
	 * 
	 * @param id
	 *            Unique id of the rule
	 * @param name
	 *            Readable name of the rule
	 */
	public ClassRule(String id, String name) {
		super(id, name);
		setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE
			.getClass_()).AND(new IsTopLevelClassCondition()));
	}

	/* (non-Javadoc)
	 * @see com.ibm.xtools.transform.core.AbstractRule#createTarget(com.ibm.xtools.transform.core.ITransformContext)
	 */
	protected Object createTarget(ITransformContext ruleContext) {
		UMLPackage uml2 = UMLPackage.eINSTANCE;
		Class cls = (Class) ruleContext.getSource();
		String className = cls.getName();
		final Package pkg = (Package) ruleContext
			.getPropertyValue("targetPackage"); //$NON-NLS-1$

		// Create the interface, implementation and factory classes
		// and put them in the package
		Interface intface = null;
		Class impl = null;

		if (pkg != null) {
			String intfaceName = "I" + className; //$NON-NLS-1$
			intface = ModelUtility.getInterfaceByName(pkg, intfaceName);
			if (intface == null) {
				intface = pkg.createOwnedInterface(intfaceName);
			}
			String implClassName = className + "Impl"; //$NON-NLS-1$
			impl = ModelUtility.getClassByName(pkg, implClassName);
			if (impl == null) {
				impl = pkg.createOwnedClass(implClassName, false);
			}

			if (intface != null && impl != null) {
				// Set Realization relationship between interface and impl
				InterfaceRealization realization = null;
				realization = ModelUtility.getRealization(impl, intface);
				if (realization == null) {
					realization = (InterfaceRealization) pkg.createPackagedElement(null, uml2
						.getInterfaceRealization());
					realization.setImplementingClassifier(impl);
					realization.setContract(intface);
				}

				// Set up the factory
				Class factory = null;
				String factoryClassName = className + "Factory"; //$NON-NLS-1$
				factory = ModelUtility.getClassByName(pkg, factoryClassName);

				if (factory == null) {
					factory = (Class)
                        pkg.createPackagedElement(
                            factoryClassName, uml2.getClass_());

					Operation factoryMethod =
                        factory.createOwnedOperation("create", null, null); //$NON-NLS-1$
					factoryMethod.setIsStatic(true);
                    factoryMethod.createReturnResult(null, intface);

					// Set up an <<Instantiate>> dependency between the factory
					// and the
					// implementation class
					Usage u = (Usage) pkg.createPackagedElement(null, uml2.getUsage());
					u.getClients().add(factory);
					u.getSuppliers().add(impl);
					Stereotype s = u
						.getApplicableStereotype("Standard::Instantiate"); //$NON-NLS-1$
					u.applyStereotype(s);
				}

				// Attribute rule: copy attributes of the source class to the
				// implementation class and generate getter and setter for each
				// attribute in the interface and the implementation class
				EList attributes = cls.getOwnedAttributes();
				for (Iterator i = attributes.iterator(); i.hasNext();) {
					Property srcProperty = (Property) i.next();
					Property targetProperty = null;
					targetProperty = ModelUtility.getPropertyByName(impl,
						srcProperty.getName());
					if (targetProperty == null) {
						targetProperty =
                            impl.createOwnedAttribute(
                                srcProperty.getName(),
                                srcProperty.getType());
						targetProperty.setVisibility(srcProperty
							.getVisibility());
					}

					Operation ifGetter = ModelUtility.createGetter(srcProperty);
					Operation ifSetter = ModelUtility.createSetter(srcProperty);
					if (ModelUtility.interfaceContainsOperation(intface,
						ifGetter) == false) {
						intface.getOwnedOperations().add(ifGetter);
					}
					if (ModelUtility.interfaceContainsOperation(intface,
						ifSetter) == false) {
						intface.getOwnedOperations().add(ifSetter);
					}

					Operation implGetter = ModelUtility
						.createGetter(srcProperty);
					Operation implSetter = ModelUtility
						.createSetter(srcProperty);
					if (ModelUtility.classContainsOperation(impl, implGetter) == false) {
						impl.getOwnedOperations().add(implGetter);
					}
					if (ModelUtility.classContainsOperation(impl, implSetter) == false) {
						impl.getOwnedOperations().add(implSetter);
					}
				}

				// Operation rule: copy all operations to the implementation
				// class and
				// only the public ones to the interface
				EList srcOperations = cls.getOwnedOperations();
				for (Iterator i = srcOperations.iterator(); i.hasNext();) {
					Operation srcOp = (Operation) i.next();

					if (srcOp.getVisibility() == VisibilityKind.PUBLIC_LITERAL
						&& ModelUtility.interfaceContainsOperation(intface,
							srcOp) == false) {
						intface.getOwnedOperations().add(
							ModelUtility.createCopyOfOperation(srcOp));
					}

					if (ModelUtility.classContainsOperation(impl, srcOp) == false) {
						impl.getOwnedOperations().add(
							ModelUtility.createCopyOfOperation(srcOp));
					}
				}
			}
		}

		System.out.println(ID + " is executed"); //$NON-NLS-1$

		return pkg;
	}
}