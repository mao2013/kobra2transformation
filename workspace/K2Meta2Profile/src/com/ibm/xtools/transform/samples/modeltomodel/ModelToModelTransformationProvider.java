
/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */


package com.ibm.xtools.transform.samples.modeltomodel;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLPackage;

import com.ibm.xtools.modeler.ui.UMLModeler;
import com.ibm.xtools.transform.core.AbstractTransform;
import com.ibm.xtools.transform.core.AbstractTransformationProvider;
import com.ibm.xtools.transform.core.ITransformContext;
import com.ibm.xtools.transform.core.ITransformationDescriptor;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.markedup.transforms.MarkedupClassToServiceRootTransform;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.multiplerules.transforms.ClassToServiceMultipleRuleRootTransform;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.singlerule.transforms.ClassToServiceSingleRuleRootTransform;
import com.ibm.xtools.transform.samples.modeltomodel.l10n.ResourceManager;
import com.ibm.xtools.uml.core.internal.util.ProfileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
/**
 * The provider for the model to model transformation samples.
 * 
 * @see AbstractTransformationProvider
 */
public class ModelToModelTransformationProvider
	extends AbstractTransformationProvider {

	/**
	 * Constructor
	 */
	public ModelToModelTransformationProvider() {
		// TODO: complete constructor body
	}

	/**
	 * @see AbstractTransformationProvider#createTransformation
	 */
	public AbstractTransform createTransformation(
			ITransformationDescriptor descriptor) {
		AbstractTransform transform = null;
		if (ClassToServiceSingleRuleRootTransform.ID.equals(descriptor
			.getId())) {
			transform = new ClassToServiceSingleRuleRootTransform(descriptor);
		} else if (ClassToServiceMultipleRuleRootTransform.ID
			.equals(descriptor.getId())) {
			transform = new ClassToServiceMultipleRuleRootTransform(
				descriptor);
		}else if (MarkedupClassToServiceRootTransform.ID
				.equals(descriptor.getId())) {
			transform = new MarkedupClassToServiceRootTransform(
				descriptor);
		}
		return transform;
	}
	


    /**
     * Validates that the source and the target container specified in the
     * context are valid for this transformation. It also checks that the
     * values of the mutable properties, if changed by the invoker of the
     * transformation, are valid.
     * 
     * @see com.ibm.xtools.transform.core.internal.services.ITransformationProvider#validateContext(com.ibm.xtools.transform.core.internal.services.ITransformationDescriptor,
     *      com.ibm.xtools.transform.core.internal.engine.ITransformContext)
     */
    
    public IStatus validateContext(ITransformationDescriptor descriptor,
			ITransformContext context) {

    	AbstractTransform transform = context.getTransform();
		boolean sourceOk = transform.canAccept(context);
		Object target = context.getTargetContainer();
		
		//System.out.println("target" + target.getClass());
		
		
		boolean targetOk = false;
        if (target instanceof IResource) {
        	
        	IResource targetResource = (IResource) target;
        	
        	if (ProfileUtil.isProfileFile(targetResource.getLocation().toString())){
        		System.out.println("target ok");
        		targetOk = true;
        	}
        	//IFile targetFile = (IFile) target;
        	//System.out.println(targetFile.getFullPath().toString());
        	//System.out.println("target "+ ((EObject) target).eClass());
        	//System.out.println("profile "+ UMLPackage.eINSTANCE.getProfile());
            //targetOk = ((EObject) target).eClass() == UMLPackage.eINSTANCE.getProfile()  ;
        }
		boolean propertiesOk = true;

		final String sourceErrMsg = ResourceManager.getString("ModelToModelTransformationProvider.invalidSourceMsg"); //$NON-NLS-1$
		final String targetErrMsg = ResourceManager.getString("ModelToModelTransformationProvider.invalidTargetMsg"); //$NON-NLS-1$
		final String pluginId = ModelToModelPlugin.getPluginId();
		
		return StatusUtility.createTransformContextValidationStatus(
			pluginId, 
			sourceOk, sourceErrMsg, 
			targetOk, targetErrMsg,
			propertiesOk);
	}
}