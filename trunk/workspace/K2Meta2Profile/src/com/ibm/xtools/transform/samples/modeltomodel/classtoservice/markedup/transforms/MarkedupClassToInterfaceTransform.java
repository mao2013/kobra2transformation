/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */


/*
 * File: MarkedupClassToInterfaceTransform.java
 */

package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.markedup.transforms;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.VisibilityKind;

import org.eclipse.emf.query.conditions.Condition;
import com.ibm.xtools.transform.core.ITransformationDescriptor;
import com.ibm.xtools.transform.samples.modeltomodel.HasKeywordCondition;
import com.ibm.xtools.transform.samples.modeltomodel.HasStereotypeCondition;
import com.ibm.xtools.transform.samples.modeltomodel.IsTopLevelClassCondition;
import com.ibm.xtools.transform.samples.modeltomodel.OperationVisibilityCondition;
import com.ibm.xtools.transform.samples.modeltomodel.OwnerHasKeywordCondition;
import com.ibm.xtools.transform.samples.modeltomodel.OwnerHasStereotypeCondition;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.CreateAccessorRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.CreateInterfaceClassRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.OperationRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.ParameterRule;
import com.ibm.xtools.uml.transform.core.IsElementKindCondition;
import com.ibm.xtools.uml.transform.core.UMLKindTransform;

/**
 * A sub-transform that accepts a top level class stereotyped as
 * ClassToServiceProfile::service or has an equivalent keyword, "service", as
 * source and creates an interface class containing the public operations of the
 * source class operations and accessor (get and set) methods corresponding to
 * the attributes in the source class.
 * 
 * A top level class is a class that is contained directly in a model or a
 * package.
 */
public class MarkedupClassToInterfaceTransform
	extends UMLKindTransform {

	public final static String ID = "com.ibm.xtools.transform.samples.modeltomodel.markedup.class2interface"; //$NON-NLS-1$

	/**
	 * Constructor.
	 */
	public MarkedupClassToInterfaceTransform() {
		super();
		initializeTransform();
	}

	/**
	 * Constructor.
	 * 
	 * @param descriptor
	 *            A transformation descriptor
	 */
	public MarkedupClassToInterfaceTransform(
			ITransformationDescriptor descriptor) {
		super(descriptor);
		initializeTransform();
	}

	/**
	 * Constructor.
	 * 
	 * @param id An unique transformation id
	 */
	public MarkedupClassToInterfaceTransform(String id) {
		super(id);
		initializeTransform();
	}

	/**
	 * Sets up the transform elements (rules, extractors, and any sub-transforms
	 * used in this transform.
	 */
	private void initializeTransform() {
		EClass classKind = UMLPackage.eINSTANCE.getClass_();
		EClass operationKind = UMLPackage.eINSTANCE.getOperation();
		EClass parameterKind = UMLPackage.eINSTANCE.getParameter();
		EClass propertyKind = UMLPackage.eINSTANCE.getProperty();

		// Create the rules and configure them to accept a class only if
		// it has a ClassToService::service stereotype or a "service" keyword
		Condition acceptCondition = new IsElementKindCondition(classKind).
			AND(new IsTopLevelClassCondition()).
			AND(new HasStereotypeCondition("ClassToService::service").//$NON-NLS-1$
				OR(new HasKeywordCondition("service")));//$NON-NLS-1$


		CreateInterfaceClassRule interfaceRule = new CreateInterfaceClassRule();
		interfaceRule.setAcceptCondition(acceptCondition);
		addByKind(classKind, interfaceRule);
		
		Condition ownerCondition = 
			new OwnerHasStereotypeCondition("ClassToService::service") //$NON-NLS-1$
			.OR(new OwnerHasKeywordCondition("service")); //$NON-NLS-1$

		// Create an operation rule, set it up to accept only public operations
		// and add to transform
		OperationRule opRule = new OperationRule();
		opRule.setAcceptCondition(new OperationVisibilityCondition(
			VisibilityKind.PUBLIC_LITERAL).AND(ownerCondition));
		addByKind(operationKind, opRule);
		
		addByKind(parameterKind, new ParameterRule());
		
		CreateAccessorRule accessorRule = new CreateAccessorRule();
		accessorRule.setAcceptCondition(ownerCondition);
		addByKind(propertyKind, accessorRule);

	}

}