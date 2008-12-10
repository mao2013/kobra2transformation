/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */

package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.multiplerules.transforms;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.VisibilityKind;

import com.ibm.xtools.transform.core.ITransformationDescriptor;
import com.ibm.xtools.transform.samples.modeltomodel.OperationVisibilityCondition;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.CreateAccessorRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.CreateInterfaceClassRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.OperationRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.ParameterRule;
import com.ibm.xtools.uml.transform.core.UMLKindTransform;

/**
 * A sub-transform that accepts a class as source and creates an interface class
 * containing the public operations of the source class operations and accessor
 * (get and set) methods corresponding to the attributes in the source class.
 *  
 */
public class ClassToInterfaceTransform
	extends UMLKindTransform {

	public final static String ID = "com.ibm.xtools.transform.samples.modeltomodel.class2interface"; //$NON-NLS-1$

	/**
	 *  Constructor.
	 */
	public ClassToInterfaceTransform() {
		super();
		initializeTransform();
	}

	/**
	 * Constructor.
	 * 
	 * @param descriptor A transformation descriptor
	 */
	public ClassToInterfaceTransform(ITransformationDescriptor descriptor) {
		super(descriptor);
		initializeTransform();
	}

	/**
	 * Constructor.
	 * 
	 * @param id An unique transformation id
	 */
	public ClassToInterfaceTransform(String id) {
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

		addByKind(classKind, new CreateInterfaceClassRule(CreateInterfaceClassRule.ID,
			CreateInterfaceClassRule.NAME));

		// Create an operation rule, set it up to accept only public operations
		// and add to transform
		OperationRule opRule = new OperationRule();
		opRule.setAcceptCondition(new OperationVisibilityCondition(
			VisibilityKind.PUBLIC_LITERAL));
		
		addByKind(operationKind, opRule);
		addByKind(parameterKind, new ParameterRule());
		addByKind(propertyKind, new CreateAccessorRule());

	}
}