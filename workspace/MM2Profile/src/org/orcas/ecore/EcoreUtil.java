package org.orcas.ecore;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;


public class EcoreUtil {
	
	public EcoreUtil(){
		
		_resourceSet = new ResourceSetImpl();
		Registry registry = Resource.Factory.Registry.INSTANCE;
		registry.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		
		_resourceSet.setResourceFactoryRegistry(registry);
	}
	
	public Resource loadResource(URI resourceURI){
				
		
		Resource resource = _resourceSet.getResource(resourceURI, true); 
		
		// Add resource to resourceSet
		// rset.getResources().add(resource);
				
		return resource;
	}
		
	private ResourceSet _resourceSet;
}
