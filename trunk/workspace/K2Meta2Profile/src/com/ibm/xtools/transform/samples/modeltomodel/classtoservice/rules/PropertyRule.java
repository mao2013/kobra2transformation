/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */

package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLPackage;

import com.ibm.xtools.transform.core.ITransformContext;
import com.ibm.xtools.transform.core.ModelRule;
import com.ibm.xtools.transform.samples.modeltomodel.ModelUtility;
import com.ibm.xtools.transform.samples.modeltomodel.l10n.ResourceManager;
import com.ibm.xtools.uml.transform.core.IsElementKindCondition;
/**
 * A rule to copy the properties of source class to a destination class.
 *  
 */
public class PropertyRule extends ModelRule {

    public static final String ID = "classtoservice.property.rule"; //$NON-NLS-1$

    public static final String NAME = ResourceManager.getString("PropertyRule.name"); //$NON-NLS-1$

    /**
     * Default constructor. Initializes the id and the name of the rule to
     * their default values.
     */
    public PropertyRule() {
    	super(ID, NAME);
    	setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE.getProperty()));
    }
    
    /**
     * Constructor.
     * 
     * Creates an instance of the rule and initializes it to accept only
     * a UML2 property as source.
     * 
	 * @param id
	 *            Unique id of the rule
	 * @param name
	 *            Readable name of the rule
	 */
    public PropertyRule(String id, String name) {
        super(id, name);
        setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE.getProperty()));
    }
    

    /* (non-Javadoc)
     * @see com.ibm.xtools.transform.core.AbstractRule#createTarget(com.ibm.xtools.transform.core.ITransformContext)
     */
    protected Object createTarget(ITransformContext ruleContext) {
    	UMLPackage uml = UMLPackage.eINSTANCE;
        Property srcProp = (Property) ruleContext.getSource();
        Property targetProp = null;
        Element targetContainer = (Element) ruleContext.getTargetContainer();
        
        if (targetContainer != null
                && targetContainer.eClass() == uml.getClass_()) {
        	targetProp = ModelUtility.getPropertyByName((Class)targetContainer, srcProp.getName()); 
            if (targetProp == null) {
            	targetProp =
                    ((Class) targetContainer)
                        .createOwnedAttribute(
                            srcProp.getName(),
                            srcProp.getType());

            	targetProp.setVisibility(srcProp.getVisibility());
            }
        }

        System.out.println(ID + " is executed"); //$NON-NLS-1$

        return targetProp;
    }

}
