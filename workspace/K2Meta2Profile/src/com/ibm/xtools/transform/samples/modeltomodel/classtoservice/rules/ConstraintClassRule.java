package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.OpaqueExpression;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.UMLPackage;

import com.ibm.xtools.transform.core.ITransformContext;
import com.ibm.xtools.transform.core.ModelRule;
import com.ibm.xtools.transform.samples.modeltomodel.IsTopLevelClassCondition;
import com.ibm.xtools.transform.samples.modeltomodel.l10n.ResourceManager;
import com.ibm.xtools.uml.transform.core.IsElementKindCondition;

public class ConstraintClassRule extends ModelRule {
	
	public static final String ID = "meta2profile.constraint.class.rule"; //$NON-NLS-1$
	
	public static final String NAME = 
		ResourceManager.getString("ConstraintClassRule.name"); //$NON-NLS-1$
	
	public ConstraintClassRule() {
    	super(ID, NAME);
    	setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE
			.getConstraint()));
    }
	
	public ConstraintClassRule(String id, String name) {
		super(id, name);
		setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE
			.getConstraint()));
	}
	
	protected Object createTarget(ITransformContext ruleContext) throws Exception {
		final Constraint srcConstraint = (Constraint) ruleContext.getSource();
		
		if (srcConstraint != null){
			OpaqueExpression opaqueExpression = (OpaqueExpression) srcConstraint.getSpecification();
			EList<String> bodies = opaqueExpression.getBodies();
			
			for (String body : bodies) {
				System.out.println(body);
			}
		}
		
		System.out.println(ID + " is executed"); 
		return null;
	}

}
