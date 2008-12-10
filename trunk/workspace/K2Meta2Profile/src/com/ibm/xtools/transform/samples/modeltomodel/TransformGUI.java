/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003, 2004. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */


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
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.markedup.transforms.MarkedupClassToServiceRootTransform;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.multiplerules.transforms.ClassToServiceMultipleRuleRootTransform;
import com.ibm.xtools.transform.samples.modeltomodel.classtoservice.singlerule.transforms.ClassToServiceSingleRuleRootTransform;
import com.ibm.xtools.transform.ui.AbstractTransformGUI;

/**
 *
 * Override the default GUI to provide filtering for the 'Target' tree
 */
public class TransformGUI extends AbstractTransformGUI {
	/**
	 * A collection to store (and cache) the paths of (or any other information
	 * related to) the models that are opened in the model explorer. 
	 */
	private static final List openedModelInfoCache = new ArrayList();
    
    /**
     * Determines if the specified path is part of the path for any of the
     * models opened in the Model Explorer.
     * 
     * @param path The specified path
     * @return True if the specified path is part os the path of model opened
     */
    private boolean isPartOfOpenedModelPath(String path) {
    	Iterator i = openedModelInfoCache.iterator();
    	boolean isPart = false;
    	while (!isPart && i.hasNext()) {
    		String modelPath = (String) i.next();
            isPart = modelPath.indexOf(path) > -1 ? true : false;
    	}
    	return isPart;
    }
    
    /**
     * Saves information (currently paths) related to models that are opened
     * in the Model Explorer in a collection, openedModelInfo.
     */
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
    	
    	// If transformation id is not the same as that of the transformations
    	// provided by this provider, the target container by default is
    	// invalid.
    	String tid = descriptor.getId();
    	if (!tid.equals(ClassToServiceSingleRuleRootTransform.ID) &&
    			!tid.equals(ClassToServiceMultipleRuleRootTransform.ID) &&
				!tid.equals(MarkedupClassToServiceRootTransform.ID)) {
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
    			if (kind == uml2.getModel() || kind == uml2.getPackage()) {
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
