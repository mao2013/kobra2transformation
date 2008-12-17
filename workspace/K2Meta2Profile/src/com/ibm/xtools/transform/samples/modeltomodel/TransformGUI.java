package com.ibm.xtools.transform.samples.modeltomodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLPackage;

import com.ibm.xtools.modeler.ui.UMLModeler;
import com.ibm.xtools.transform.core.ITransformationDescriptor;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.multiplerules.transforms.ClassToServiceMultipleRuleRootTransform;
import com.ibm.xtools.transform.ui.AbstractTransformGUI;

public class TransformGUI extends AbstractTransformGUI {

	private static final List openedModelInfoCache = new ArrayList();
    
    private boolean isPartOfOpenedModelPath(String path) {
    	Iterator i = openedModelInfoCache.iterator();
    	boolean isPart = false;
    	while (!isPart && i.hasNext()) {
    		String modelPath = (String) i.next();
            isPart = modelPath.indexOf(path) > -1 ? true : false;
    	}
    	return isPart;
    }
    
    private void initializeOpenedModelInfoCache() {
    	// We build the cache fresh each time this method is invoked.
    	openedModelInfoCache.clear();

    	Iterator i = UMLModeler.getOpenedModels().iterator();
    	
    	while (i.hasNext()) {
    		Resource resource = ((Model) i.next()).eResource();
    		
    		// If there is a space in the names of the files containing
    		// models, org.eclipse.emf.common.util.URI class "escapes"
    		// the space in URI segments. Consequently, if one gets the
    		// path for the model files without "unescaping" the segments,
    		// the resulting path will be incorrect. To "unescape", we use
    		// URI.decode(String).
    		
    		openedModelInfoCache.add(URI.decode(resource.getURI().path()));
    	}
    }

	public boolean showInTargetContainerTree(
			ITransformationDescriptor descriptor,
			Object suggestedTargetContainer) {
    	
    	String tid = descriptor.getId();
    	if (!tid.equals(ClassToServiceMultipleRuleRootTransform.ID)) {
    		return super.showInTargetContainerTree(descriptor, suggestedTargetContainer);
    	}
    	
    	boolean isValid = false;
    		
    	// If the suggestedTargetContainer is null, we need to
    	// re-initialize the opened model information.
    	if (suggestedTargetContainer == null) {
    		initializeOpenedModelInfoCache();
    		isValid = false;
    	} else {    					
    		if (suggestedTargetContainer instanceof EObject) {
    			// If the suggestedTargetContainer is a UML model or
        		// a UML package, it is a valid target container
    			EClass kind = ((EObject) suggestedTargetContainer).eClass();
    			UMLPackage uml2 = UMLPackage.eINSTANCE;
    			if (kind == uml2.getProfile()) {
    				isValid = true;
    			}
    		} else if (suggestedTargetContainer instanceof IResource) {
    			// To be a valid target container the path of this resource
    			// should be a part of the path of one of the opened
    			// models.
    			IResource resource = (IResource) suggestedTargetContainer;
    			String resourcePath = resource.getFullPath().toString();
    			isValid = isPartOfOpenedModelPath(resourcePath);
    		}
    	}
    	
    	return isValid;
	}
}
