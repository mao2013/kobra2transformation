/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */

package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules;

import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.UMLPackage;

import com.ibm.xtools.transform.core.ITransformContext;
import com.ibm.xtools.transform.core.ModelRule;
import com.ibm.xtools.transform.samples.modeltomodel.l10n.ResourceManager;
import com.ibm.xtools.uml.transform.core.IsElementKindCondition;

/**
 * A rule to copy the parameters of a source operation to a destination operation.
 *
 */
public class ParameterRule extends ModelRule {
    
    public static final String ID = "class2service.parameter.rule"; //$NON-NLS-1$
    public static final String NAME = ResourceManager.getString("ParameterRule.name"); //$NON-NLS-1$

    /**
     * Default constructor. Initializes the id and the name of the rule to
     * their default values.
     */
    public ParameterRule() {
    	super(ID, NAME);
    	setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE.getParameter()));
    }
    
    /**
     * Constructor.
     * 
     * Creates an instance of the rule and initializes it to accept only
     * a UML2 parameter as source.
     * 
	 * @param id
	 *            Unique id of the rule
	 * @param name
	 *            Readable name of the rule
	 */
    public ParameterRule(String id, String name) {
        super(id, name);
        setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE.getParameter()));
    }
    

    /* (non-Javadoc)
     * @see com.ibm.xtools.transform.core.AbstractRule#createTarget(com.ibm.xtools.transform.core.ITransformContext)
     */
    protected Object createTarget(ITransformContext ruleContext) {
    	UMLPackage uml = UMLPackage.eINSTANCE;
        Parameter srcParm = (Parameter) ruleContext.getSource();
        Parameter targetParm = null;
        Element container = (Element) ruleContext.getTargetContainer();

        if (container != null && container.eClass() == uml.getOperation()) {
        	Operation targetOp = (Operation)container;
        	
        	// Treat return parameters differently from in/out parameters
        	if (srcParm.getDirection() == ParameterDirectionKind.RETURN_LITERAL) {
        		targetParm =
                    targetOp.createReturnResult(
                        srcParm.getName(),
                        srcParm.getType());
        	} else {
        		targetParm =
                    targetOp.createOwnedParameter(
                        srcParm.getName(),
                        srcParm.getType());
        	}
        	
            targetParm.setDirection(srcParm.getDirection());
        }
        
        System.out.println(ID + " is executed"); //$NON-NLS-1$
        
        return targetParm;
    }

}
