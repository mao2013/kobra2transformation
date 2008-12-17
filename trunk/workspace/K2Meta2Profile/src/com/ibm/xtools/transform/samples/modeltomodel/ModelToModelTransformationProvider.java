
package com.ibm.xtools.transform.samples.modeltomodel;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;

import com.ibm.xtools.transform.core.AbstractTransform;
import com.ibm.xtools.transform.core.AbstractTransformationProvider;
import com.ibm.xtools.transform.core.ITransformContext;
import com.ibm.xtools.transform.core.ITransformationDescriptor;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.multiplerules.transforms.ClassToServiceMultipleRuleRootTransform;
import com.ibm.xtools.transform.samples.modeltomodel.l10n.ResourceManager;
import com.ibm.xtools.uml.core.internal.util.ProfileUtil;


public class ModelToModelTransformationProvider
	extends AbstractTransformationProvider {

	public ModelToModelTransformationProvider() {
		// TODO: complete constructor body
	}

	public AbstractTransform createTransformation(
			ITransformationDescriptor descriptor) {
		AbstractTransform transform = null;

		transform = new ClassToServiceMultipleRuleRootTransform(descriptor);
		
		return transform;
	}
	
    
    public IStatus validateContext(ITransformationDescriptor descriptor,
			ITransformContext context) {

    	AbstractTransform transform = context.getTransform();
		boolean sourceOk = transform.canAccept(context);
		Object target = context.getTargetContainer();
		
		
		boolean targetOk = false;
        if (target instanceof IResource) {
        	
        	IResource targetResource = (IResource) target;
        	
        	if (ProfileUtil.isProfileFile(targetResource.getLocation().toString())){
        		System.out.println("target ok");
        		targetOk = true;
        	}
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