/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */


/*
 * File: MarkedupClassToImplementationTransform.java
 */

package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.markedup.transforms;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.UMLPackage;

import org.eclipse.emf.query.conditions.Condition;
import com.ibm.xtools.transform.core.ITransformationDescriptor;
import com.ibm.xtools.transform.samples.modeltomodel.HasKeywordCondition;
import com.ibm.xtools.transform.samples.modeltomodel.HasStereotypeCondition;
import com.ibm.xtools.transform.samples.modeltomodel.IsTopLevelClassCondition;
import com.ibm.xtools.transform.samples.modeltomodel.OwnerHasKeywordCondition;
import com.ibm.xtools.transform.samples.modeltomodel.OwnerHasStereotypeCondition;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.CreateAccessorRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.CreateImplementationClassRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.OperationRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.ParameterRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.PropertyRule;
import com.ibm.xtools.uml.transform.core.IsElementKindCondition;
import com.ibm.xtools.uml.transform.core.UMLKindTransform;

/**
 * A sub-transform that accepts a top level class stereotyped as
 * ClassToServiceProfile::service or has an equivalent keyword, "service", as
 * source and creates an implementation class with the same attributes,
 * operations and additional accessor (get and set) methods.
 * 
 * A top level class is a class that is contained directly in a model or a
 * package.
 */
public class MarkedupClassToImplementationTransform
	extends UMLKindTransform {

	public final static String ID = "com.ibm.xtools.transform.samples.modeltomodel.markedup.class2implementation"; //$NON-NLS-1$

	/**
	 * Default constructor.
	 */
	public MarkedupClassToImplementationTransform() {
		super();
		initializeTransform();
	}

	/**
	 * Constructor.
	 * 
	 * @param descriptor
	 *            A transformation descriptor
	 */
	public MarkedupClassToImplementationTransform(
			ITransformationDescriptor descriptor) {
		super(descriptor);
		initializeTransform();
	}

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            An unique transformation id
	 */
	public MarkedupClassToImplementationTransform(String id) {
		super(id);
		initializeTransform();
	}

	/**
	 * Sets up the transform elements (rules, extractors, and any sub-transforms
	 * used in this transform.
	 *  
	 */
	private void initializeTransform() {
		EClass classKind = UMLPackage.eINSTANCE.getClass_();
		EClass operationKind = UMLPackage.eINSTANCE.getOperation();
		EClass parameterKind = UMLPackage.eINSTANCE.getParameter();
		EClass propertyKind = UMLPackage.eINSTANCE.getProperty();

		// Create the rules and configure them to accept a top level class only if
		// it has a ClassToService::service stereotype or a "service" keyword
		Condition acceptCondition = new IsElementKindCondition(classKind).
			AND(new IsTopLevelClassCondition()).
			AND(new HasStereotypeCondition("ClassToService::service").//$NON-NLS-1$
				OR(new HasKeywordCondition("service")));//$NON-NLS-1$

		CreateImplementationClassRule implClassRule = new CreateImplementationClassRule();
		implClassRule.setAcceptCondition(acceptCondition);
		addByKind(classKind, implClassRule);
		
		Condition ownerCondition = 
			new OwnerHasStereotypeCondition("ClassToService::service") //$NON-NLS-1$
			.OR(new OwnerHasKeywordCondition("service")); //$NON-NLS-1$
									
		OperationRule opRule = new OperationRule();
		opRule.setAcceptCondition(ownerCondition);
		addByKind(operationKind, opRule);
		
		addByKind(parameterKind, new ParameterRule());
		
		PropertyRule propertyRule = new PropertyRule();
		propertyRule.setAcceptCondition(ownerCondition);
		addByKind(propertyKind, propertyRule);
		
		CreateAccessorRule accessorRule = new CreateAccessorRule();
		accessorRule.setAcceptCondition(ownerCondition);
		addByKind(propertyKind, accessorRule);
	}

}