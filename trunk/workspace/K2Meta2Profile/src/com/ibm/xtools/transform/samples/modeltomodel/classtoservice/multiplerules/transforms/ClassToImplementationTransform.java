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

import com.ibm.xtools.transform.core.ITransformationDescriptor;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.CreateAccessorRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.CreateImplementationClassRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.OperationRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.ParameterRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.PropertyRule;
import com.ibm.xtools.uml.transform.core.UMLKindTransform;

/**
 * A sub-transform that accepts a class as source and creates an implementation
 * class with the same attributes, operations and additional accessor (get and
 * set) methods.
 *  
 */
public class ClassToImplementationTransform
	extends UMLKindTransform {

	public final static String ID = "com.ibm.xtools.transform.samples.modeltomodel.class2implementation"; //$NON-NLS-1$

	/**
	 *  Constructor.
	 */
	public ClassToImplementationTransform() {
		super();
		initializeTransform();
	}

	/**
	 * Constructor.
	 * 
	 * @param descriptor A transformation descriptor
	 */
	public ClassToImplementationTransform(ITransformationDescriptor descriptor) {
		super(descriptor);
		initializeTransform();
	}

	/**
	 * Constructor.
	 * 
	 * @param id An unique transformation id
	 */
	public ClassToImplementationTransform(String id) {
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

		addByKind(classKind, new CreateImplementationClassRule());
		addByKind(operationKind, new OperationRule());
		addByKind(parameterKind, new ParameterRule());
		addByKind(propertyKind, new PropertyRule());
		addByKind(propertyKind, new CreateAccessorRule());

	}

}