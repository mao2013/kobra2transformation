/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */

package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.singlerule.transforms;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.UMLPackage;

import com.ibm.xtools.transform.core.ITransformationDescriptor;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.ClassRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.SetupTargetRule;
import com.ibm.xtools.uml.transform.core.UMLKindTransform;

/**
 * This transformation accepts a class as the source and creates the following:
 * <ul>
 * <li>An interface containing the public operations of the source class and
 * the accessor methods for the attributes</li>
 * <li>An implementation class that is a copy of the source class plus the
 * accessor methods</li>
 * <li>A factory class containing an operation that creates an instance of the
 * implementation class.
 * 
 * This implementation of the transformation uses a single monolithic rule to
 * carry out the necessary conversions from the source class to the target
 * classes.
 */
public class ClassToServiceSingleRuleTransform
	extends UMLKindTransform {

	public final static String ID = "com.ibm.xtools.transform.samples.modeltomodel.class2service.singlerule"; //$NON-NLS-1$

	/**
	 * Constructor.
	 * 
	 * @param descriptor A transformation descriptor
	 */
	public ClassToServiceSingleRuleTransform(ITransformationDescriptor descriptor) {
		super(descriptor);
		initializeTransform();
	}

	/**
	 * Constructor.
	 * 
	 * @param id An unique transformation id
	 */
	public ClassToServiceSingleRuleTransform(String id) {
		super(id);
		initializeTransform();
	}

	private void initializeTransform() {
		EClass classKind = UMLPackage.eINSTANCE.getClass_();

		addByKind(classKind, new SetupTargetRule());
		addByKind(classKind, new ClassRule());
	}
}