package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.UMLPackage;

import com.ibm.xtools.transform.core.ITransformContext;
import com.ibm.xtools.transform.core.ModelRule;
import com.ibm.xtools.transform.samples.modeltomodel.IsTopLevelClassCondition;
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
		final Class srcClass = (Class) ruleContext.getSource();
		final String srcName = srcClass.getName();
		final Profile profile = (Profile) ruleContext.getPropertyValue("targetProfile"); //$NON-NLS-1$
		
		if (profile != null) {
			profile.createOwnedStereotype(srcName);		
		}
		
		EList<Element> ownedElements = srcClass.allOwnedElements();
		
		for (Element element : ownedElements) {
			if (element instanceof Constraint){
				System.out.println("ok constraint hree");
			}
		}
		
		

		System.out.println(ID + " is executed"); 
		return null;
	}

}
