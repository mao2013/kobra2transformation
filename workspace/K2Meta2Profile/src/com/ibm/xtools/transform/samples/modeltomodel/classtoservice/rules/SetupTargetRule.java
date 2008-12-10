/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */


/*
  File: SetupTargetRule.java
 */

package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.rules;

import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLPackage;

import com.ibm.xtools.transform.core.ITransformContext;
import com.ibm.xtools.transform.core.ModelRule;
import com.ibm.xtools.transform.samples.modeltomodel.ModelUtility;
import com.ibm.xtools.transform.samples.modeltomodel.l10n.ResourceManager;
import com.ibm.xtools.uml.transform.core.IsElementKindCondition;

/**
 * A rule to set up the target package in which the model elements produced
 * by the transformation are put. It is expected that the invoker of the
 * transformation would specify a package or a model as the target container
 * in the transformation context. If a model is chosen as the target container,
 * this rule will create a package, named "ClassToServiceOutput" in this model.
 * If no model is chosen or the target container is not specified,the rule 
 * creates the package, "ClassToServiceOutput", in the source model.
 *  
 */
public class SetupTargetRule extends ModelRule {

    public static final String ID = "classtoservice.setuptarget.rule"; //$NON-NLS-1$

    public static final String NAME = ResourceManager.getString("SetupTargetRule.name"); //$NON-NLS-1$

    /**
     * Default constructor. Initializes the id and the name of the rule to
     * their default values and initializes it to accept only
     * a UML2 class as source.
     */
    public SetupTargetRule() {
    	super(ID, NAME);
    	setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE
            .getClass_()));
    }
    
    /**
     * Constructor.
     * 
     * Creates an instance of the rule and initializes it to accept only
     * a UML2 class as source.
     * 
     * @param id
     *            Unique id of the rule
     * @param name
     *            Readable name of the rule
     */
    public SetupTargetRule(String id, String name) {
        super(id, name);
        setAcceptCondition(new IsElementKindCondition(UMLPackage.eINSTANCE
                .getClass_()));
    }

    /**
     * Retrieves the target container from the context. If no target container
     * is specified in the context or the specified, it creates a package,
     * ClassToServiceOutput, in the source model. If a model is chosen as the
     * target container, create a package, ClassToServiceOutput, in the
     * speciied model. The output model elements are placed in the user
     * supplied package or in the ClassToServiceOutput package.
     * 
     * @return null
     * 
     * @see com.ibm.xtools.transform.core.internal.engine.AbstractRule#createTarget(com.ibm.xtools.transform.core.internal.engine.ITransformContext)
     */
    protected Object createTarget(ITransformContext context) {
    	// Check if the invoker has specified a target package or a target
    	// model. If not, get the source model and create a package,
    	// ClassToServiceOutput, in the source model. If invoker selected a
    	// model, create the package, ClassToServiceOutput, in the specified
    	// model
    	Package pkg = null;
    	Model model = null;
        
    	// If the target package has already been set up, don't do anything.
        pkg = (Package) context.getPropertyValue("targetPackage"); //$NON-NLS-1$
        if (pkg != null) {
        	return null;   
        }

        Object tc = context.getTargetContainer();
        if (tc != null) {// No model is selected or target container is not supplied.
    		// Select the model containing the source class as the
    		// container of the output. Alternatively, one can create a
            // new project and a new model in this project. We, however,
            // take the easy (although not the ideal) route.
        	if (tc instanceof Model) {
        		model = (Model) tc;
        	} else if (tc instanceof Package) {
        		pkg = (Package) tc;
        	}
        }
        
        if (pkg == null) {
        	// No package is selected - create a default package
        	String defaultPackageName = "ClassToServiceOutput";  //$NON-NLS-1$
        	
        	if (model == null) {
        		// No model is selected or target container is not supplied.
        		// Select the model containing the source class as the
        		// container of the output
        		model = ((Element) context.getSource()).getModel();
        	}
        	
        	// If the package does not already exist in the model, create it
        	pkg = ModelUtility.getPackageByName(model, defaultPackageName);
        	if (pkg == null) {
        		pkg = model.createNestedPackage(defaultPackageName);
        	}
        }
        
        context.setPropertyValue("targetPackage", pkg); //$NON-NLS-1$

        System.out.println(ID + " is executed"); //$NON-NLS-1$

        return null;
    }

}
