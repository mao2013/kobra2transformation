package com.ibm.xtools.transform.samples.modeltomodel.classtoservice.multiplerules.transforms;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.UMLPackage;

import org.eclipse.emf.query.conditions.Condition;
import com.ibm.xtools.transform.core.ITransformContext;
import com.ibm.xtools.transform.core.ITransformationDescriptor;
import com.ibm.xtools.transform.core.RootTransform;
import com.ibm.xtools.transform.samples.modeltomodel.IsTopLevelClassCondition;

public class ClassToServiceMultipleRuleRootTransform extends RootTransform {

    public final static String ID = "com.ibm.xtools.transform.samples.modeltomodel.class2service.multiplerule.root"; //$NON-NLS-1$
 
    /**
     * Constructor.
     * 
     * @param descriptor A transformation descriptor
     */
    public ClassToServiceMultipleRuleRootTransform(ITransformationDescriptor descriptor) {
        super(descriptor);
		
        ClassToServiceMultpleRuleTransform mainTransform = new ClassToServiceMultpleRuleTransform(
        	ClassToServiceMultpleRuleTransform.ID);
        mainTransform.setName(descriptor.getName());
		
		// Implementation note: do not change the order of initalization
		initialize(mainTransform, false);
		setupInitialize();
		setupFinalize();
    }

	/**
	 * Sets up the initialization phase by adding transformation elements
	 * (rules, transforms, extractors) to the root transform
	 */
	private void setupInitialize() {
		// Auto-generated code - initialization phase
	}

	/**
	 * Sets up the finaalization phase by adding transformation elements (rules,
	 * transforms, extractors) to the root transform
	 */
	private void setupFinalize() {
		// Auto-generated code - finalization phase
	}
	
	/**
	 * Determines if any member in the list of source objects specified in the
	 * context is acceptable. This transformation accepts a model element of
	 * type <code>class</code>.
	 * 
	 * This method iterates over the list supplied as the source and returns
	 * true if any of the members is a UML2 class, a package or a model. If the
	 * member is a UML2 class, it returns true only if the class is top level
	 * class. That is, if the class is directly contained in a package or a
	 * model.
	 * 
	 * @param context
	 *            A context that contains the source and target info.
	 * @return boolean - true if the context is acceptable
	 */
	public boolean canAccept(ITransformContext context) {
		boolean accepted = false;
		UMLPackage uml = UMLPackage.eINSTANCE;
		if (context.getSource() instanceof List) {
			Iterator iter = ((List) context.getSource()).iterator();
			while (!accepted && iter.hasNext()) {
				Object element = iter.next();
				if (element instanceof EObject) {
                    Condition isTopLevelClass = new IsTopLevelClassCondition();
					EClass kind = ((EObject) element).eClass();
					accepted = (kind == uml.getClass_() && isTopLevelClass.isSatisfied(element))
						|| (kind == uml.getPackage())
						|| (kind == uml.getModel());
				}
			}
		}
		return accepted;
	}
}
