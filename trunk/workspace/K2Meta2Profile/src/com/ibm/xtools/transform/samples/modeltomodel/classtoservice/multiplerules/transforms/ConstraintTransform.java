package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.multiplerules.transforms;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.VisibilityKind;

import com.ibm.xtools.transform.core.ITransformationDescriptor;
import com.ibm.xtools.transform.samples.modeltomodel.OperationVisibilityCondition;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.ConstraintClassRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.CreateAccessorRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.CreateInterfaceClassRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.CreateStereotypeClassRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.OperationRule;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules.ParameterRule;
import com.ibm.xtools.uml.transform.core.UMLKindTransform;

public class ConstraintTransform extends UMLKindTransform {
	
	public final static String ID = "com.ibm.xtools.transform.samples.modeltomodel.contrainttransfomr"; //$NON-NLS-1$
	
	public ConstraintTransform() {
		super();
		initializeTransform();
	}

	/**
	 * Constructor.
	 * 
	 * @param descriptor A transformation descriptor
	 */
	public ConstraintTransform(ITransformationDescriptor descriptor) {
		super(descriptor);
		initializeTransform();
	}

	/**
	 * Constructor.
	 * 
	 * @param id An unique transformation id
	 */
	public ConstraintTransform(String id) {
		super(id);
		initializeTransform();
	}
	
	private void initializeTransform() {
		EClass consraintKind = UMLPackage.eINSTANCE.getConstraint();
		
		
		//EClass operationKind = UMLPackage.eINSTANCE.getOperation();
		//EClass parameterKind = UMLPackage.eINSTANCE.getParameter();
		//EClass propertyKind = UMLPackage.eINSTANCE.getProperty();

		addByKind(consraintKind, new ConstraintClassRule(ConstraintClassRule.ID,
				ConstraintClassRule.NAME));

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
