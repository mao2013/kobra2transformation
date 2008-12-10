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

public class ClassToStereotypeTransform extends UMLKindTransform {
	
	public final static String ID = "com.ibm.xtools.transform.samples.modeltomodel.class2stereotype"; //$NON-NLS-1$
	
	public ClassToStereotypeTransform() {
		super();
		initializeTransform();
	}

	/**
	 * Constructor.
	 * 
	 * @param descriptor A transformation descriptor
	 */
	public ClassToStereotypeTransform(ITransformationDescriptor descriptor) {
		super(descriptor);
		initializeTransform();
	}

	/**
	 * Constructor.
	 * 
	 * @param id An unique transformation id
	 */
	public ClassToStereotypeTransform(String id) {
		super(id);
		initializeTransform();
	}
	
	private void initializeTransform() {
		EClass classKind = UMLPackage.eINSTANCE.getClass_();
		//EClass operationKind = UMLPackage.eINSTANCE.getOperation();
		//EClass parameterKind = UMLPackage.eINSTANCE.getParameter();
		//EClass propertyKind = UMLPackage.eINSTANCE.getProperty();

		addByKind(classKind, new CreateInterfaceClassRule(CreateInterfaceClassRule.ID,
			CreateInterfaceClassRule.NAME));

		// Create an operation rule, set it up to accept only public operations
		// and add to transform
		//OperationRule opRule = new OperationRule();
		//opRule.setAcceptCondition(new OperationVisibilityCondition(
		//	VisibilityKind.PUBLIC_LITERAL));
		
		//addByKind(operationKind, opRule);
		//addByKind(parameterKind, new ParameterRule());
		//saddByKind(propertyKind, new CreateAccessorRule());

	}
}
