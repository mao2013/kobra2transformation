package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;

import com.ibm.xtools.transform.core.ITransformContext;
import com.ibm.xtools.transform.core.ModelRule;
import com.ibm.xtools.transform.samples.modeltomodel.IsTopLevelClassCondition;
import com.ibm.xtools.transform.samples.modeltomodel.ModelUtility;
import com.ibm.xtools.transform.samples.modeltomodel.l10n.ResourceManager;
import com.ibm.xtools.uml.transform.core.IsElementKindCondition;

public class CreateStereotypeClassRule extends ModelRule {
	
	public static final String ID = "meta2profile.create.stereotype.class.rule"; //$NON-NLS-1$
	
	public static final String NAME = 
		ResourceManager.getString("CreateStereotypeClassRule.name"); //$NON-NLS-1$
	
	public CreateStereotypeClassRule() {
    	super(ID, NAME);
    	setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE
			.getClass_()).AND(new IsTopLevelClassCondition()));
    }
	
	public CreateStereotypeClassRule(String id, String name) {
		super(id, name);
		setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE
			.getClass_()).AND(new IsTopLevelClassCondition()));
	}
	
	protected Object createTarget(ITransformContext ruleContext) throws Exception {
		final String srcName = ((Class) ruleContext.getSource()).getName();
		final Package pkg = (Package) ruleContext.getPropertyValue("targetPackage"); //$NON-NLS-1$
		Class targetStereotype = null;
		
		if (pkg != null) {
			String targetStereotypeName = srcName; //$NON-NLS-1$
		
			targetStereotype = (Stereotype) ModelUtility.getClassByName(pkg, targetStereotypeName);
			
			if (targetStereotype == null) {
				targetStereotype = pkg.createOwnedClass(targetStereotypeName, false);
				//targetStereotype.iss
			}
		}

		System.out.println(ID + " is executed"); //$NON-NLS-1$

		return targetStereotype;
	}

}
