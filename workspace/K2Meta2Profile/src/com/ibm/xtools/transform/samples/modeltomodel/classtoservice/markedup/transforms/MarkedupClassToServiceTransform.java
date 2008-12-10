/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */

package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.markedup.transforms;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.UMLPackage;

import org.eclipse.emf.query.conditions.Condition;
import com.ibm.xtools.transform.core.ITransformationDescriptor;
import com.ibm.xtools.transform.samples.modeltomodel.HasKeywordCondition;
import com.ibm.xtools.transform.samples.modeltomodel.HasStereotypeCondition;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.CreateRealizationRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.SetupTargetRule;
import com.ibm.xtools.uml.transform.core.IsElementKindCondition;
import com.ibm.xtools.uml.transform.core.UMLKindTransform;

/**
 * This transformation accepts a class as the source if it is stereotyped as
 * ClassToServiceProfile::service or has an equivalent keyword, "service", and creates the following:
 * <ul>
 * <li>An interface containing the public operations of the source class and
 * the accessor methods for the attributes</li>
 * <li>An implementation class that is a copy of the source class plus the
 * accessor methods</li>
 * <li>A factory class containing an operation that creates an instance of the
 * implementation class
 * <li>If there is an operation stereotyped as ClassToServiceProfile::create,
 * the operation is copied in the factory class.
 * </ul>
 */
public class MarkedupClassToServiceTransform
	extends UMLKindTransform {

	public final static String ID = "com.ibm.xtools.transform.samples.modeltomodel.markedup.class2service"; //$NON-NLS-1$

	/**
	 *  Default constructor.
	 */
	public MarkedupClassToServiceTransform() {
		super();
		initializeTransform();
	}

	/**
	 * Constructor.
	 * 
	 * @param descriptor A transformation descriptor
	 */
	public MarkedupClassToServiceTransform(ITransformationDescriptor descriptor) {
		super(descriptor);
		initializeTransform();
	}

	/**
	 * @param id
	 */
	public MarkedupClassToServiceTransform(String id) {
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

		// Create the rules and configure them to accept a class only if
		// it has a ClassToService::service stereotype or a "service" keyword
		Condition acceptCondition = new IsElementKindCondition(classKind)
			.AND(new HasStereotypeCondition("ClassToService::service").//$NON-NLS-1$
				OR(new HasKeywordCondition("service")));//$NON-NLS-1$

		SetupTargetRule setupTargetRule = new SetupTargetRule();
		setupTargetRule.setAcceptCondition(acceptCondition);
		addByKind(classKind, setupTargetRule);

		addByKind(classKind, new MarkedupClassToInterfaceTransform(MarkedupClassToInterfaceTransform.ID));
		addByKind(classKind, new MarkedupClassToImplementationTransform(MarkedupClassToImplementationTransform.ID));

		CreateRealizationRule realizationRule = new CreateRealizationRule();
		realizationRule.setAcceptCondition(acceptCondition);
		addByKind(classKind, realizationRule);

		addByKind(classKind, new MarkedupClassToFactoryTransform(MarkedupClassToFactoryTransform.ID));
	}

}