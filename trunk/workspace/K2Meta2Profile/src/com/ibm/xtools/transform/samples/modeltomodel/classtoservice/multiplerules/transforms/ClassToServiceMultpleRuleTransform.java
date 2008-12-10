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
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.CreateFactoryClassRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.CreateRealizationRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.SetupTargetRule;
import com.ibm.xtools.uml.transform.core.UMLKindTransform;


public class ClassToServiceMultpleRuleTransform
	extends UMLKindTransform {

	public final static String ID = "com.ibm.xtools.transform.samples.modeltomodel.class2service"; //$NON-NLS-1$

	/**
	 * Constructor.
	 * 
	 * @param descriptor A transformation descriptor
	 */
	public ClassToServiceMultpleRuleTransform(ITransformationDescriptor descriptor) {
		super(descriptor);
		initializeTransform();
	}

	/**
	 * Construtor.
	 * 
	 * @param id An unique transformation id.
	 */
	public ClassToServiceMultpleRuleTransform(String id) {
		super(id);
		initializeTransform();
	}

	/**
	 * Sets up the contained transform elements (rules, extractors, and
	 * sub-transforms for this transform.
	 *  
	 */
	private void initializeTransform() {

		EClass classKind = UMLPackage.eINSTANCE.getClass_();

		addByKind(classKind, new SetupTargetRule());
		addByKind(classKind, new ClassToStereotypeTransform(
			ClassToStereotypeTransform.ID));
		//addByKind(classKind, new ClassToImplementationTransform(
		//	ClassToImplementationTransform.ID));

		//addByKind(classKind, new CreateRealizationRule());
		//addByKind(classKind, new CreateFactoryClassRule());

	}

}