package org.orcas.uml2;

import static java.lang.System.out;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.mapping.ecore2xml.Ecore2XMLPackage;
import org.eclipse.emf.mapping.ecore2xml.util.Ecore2XMLResource;
import org.eclipse.ocl.uml.ExpressionInOCL;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Extension;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.OpaqueExpression;
import org.eclipse.uml2.uml.PackageImport;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;
import org.eclipse.uml2.uml.resource.UML22UMLResource;
import org.eclipse.uml2.uml.resource.UMLResource;

public class UML2Util {

	public UML2Util() {
		
		_debug = true;
		
		_profiles = new HashMap<String, Profile>();
		_stereotypes = new HashMap<String, Stereotype>();

		Registry registry = Resource.Factory.Registry.INSTANCE;
		registry.getExtensionToFactoryMap().put("uml", new UMLResourceFactoryImpl());
		
		_resourceSet = new ResourceSetImpl();
		_resourceSet.setResourceFactoryRegistry(registry);

		// configure the fake "oclenv:" resource factory
		Map<String, Object> protMap = UMLResource.Factory.Registry.INSTANCE.getProtocolToFactoryMap();

		try {
			protMap.put("oclenv", Class.forName("org.eclipse.ocl.uml.UMLEnvironmentFactory").newInstance()); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a Constraint to a Stereotype and then add the former to the
	 * ownedRules property of the Stereotype
	 * 
	 * @param c
	 *            A Constraint object that will be added to s
	 * @param s
	 *            The owner of the Constraint
	 */
	public void addConstraint2Stereotype(Constraint c, Stereotype s) {
		ExpressionInOCL eio = (ExpressionInOCL) c.getSpecification();
		String bodyExpression = eio.getBodyExpression().toString();
		Constraint constraint = s.createOwnedRule(c.getName());
		OpaqueExpression opaqueExpression = (OpaqueExpression) constraint.createSpecification(bodyExpression, null, UMLPackage.Literals.OPAQUE_EXPRESSION);
		opaqueExpression.getLanguages().add("OCL");
		opaqueExpression.getBodies().add(bodyExpression);
		constraint.setSpecification(opaqueExpression);
		c.createOwnedComment().setBody("Association representation in a profile definition goes via OCL constraints");
	}
	
	public Extension createExtension(
		org.eclipse.uml2.uml.Class metaclass, Stereotype stereotype, boolean required) {

		Extension extension = stereotype.createExtension(metaclass, required);
		
		_debug((required ? "Required extension '": "Extension '") + extension.getQualifiedName() + "' created.");
		
		return extension;
	}
	
	public Generalization createGeneralization(
		Classifier specificClassifier, Classifier generalClassifier) {

		Generalization generalization = specificClassifier.createGeneralization(generalClassifier);
		
		_debug("Generalization " + specificClassifier.getQualifiedName() + 
				" ->> " + generalClassifier.getQualifiedName() + " created.");
		
		return generalization;
	}
	
	public PackageImport createMetamodelReference(Profile profile, String name) {

		// set up a reference to the UML2 metamodel
		Model metamodel = UMLFactory.eINSTANCE.createModel();
		metamodel.setName(name);

		PackageImport metamodelReference = UMLFactory.eINSTANCE.createPackageImport();
		metamodelReference.setImportedPackage(metamodel);
		
		return metamodelReference;
	}
	
	public Stereotype createOrRetrieveStereotype(org.eclipse.uml2.uml.Package package_, String name, boolean isAbstract) {
	
		Stereotype stereotype = null;
		
		Profile profile = createOrRetrieveProfile(package_.getName());
		
		if (!_stereotypes.containsKey(name)){
			stereotype = profile.createOwnedStereotype(name, isAbstract);
			_stereotypes.put(name, stereotype);
			_debug("Stereotype '" + stereotype.getName() + "' created in " + profile.getName());
		} else {
			stereotype = _stereotypes.get(name);
			_debug("Stereotype '" + name + "' retrieved.");
		}
		
		return stereotype;
	}

	public Profile createOrRetrieveProfile(String name) {
		
		Profile profile = null;
		
		if (!_profiles.containsKey(name)){
	        profile = UMLFactory.eINSTANCE.createProfile();
	        profile.setName(name);
	        _profiles.put(name, profile);
	        _debug("Profile '" + name + "' created.");
		}
		else {
			profile = _profiles.get(name);
	        _debug("Profile '" + profile.getName() + "' retrieved.");
		}
        
        
        return profile;
    }

	public void defineProfile(Profile profile) {

        profile.define();

        _debug("Profile '" + profile.getQualifiedName() + "' defined.");
    }

	public PrimitiveType importPrimitiveType(
			org.eclipse.uml2.uml.Package package_, String name) {

		Model umlLibrary = (Model) load(URI.createURI(UMLResource.UML_PRIMITIVE_TYPES_LIBRARY_URI));
		
		PrimitiveType primitiveType = (PrimitiveType) umlLibrary.getOwnedType(name);
		package_.createElementImport(primitiveType);
		
		_debug("Primitive type '" + primitiveType.getQualifiedName() + "' imported.");
		
		return primitiveType;
	}

	public org.eclipse.uml2.uml.Package load(URI uri) {
		
		UMLPackage umlPackage = UMLPackage.eINSTANCE;

		Resource resource = _resourceSet.getResource(uri, true);

		org.eclipse.uml2.uml.Package package_ =
		   	(org.eclipse.uml2.uml.Package) EcoreUtil.getObjectByType(
		   		resource.getContents(), UMLPackage.Literals.PACKAGE);
        
        _resourceSet.getPackageRegistry().put(umlPackage.getNsURI(), umlPackage);     
        _resourceSet.getPackageRegistry().put(package_.getQualifiedName(), package_);
        _resourceSet.getResources().add(umlPackage.eResource());

        return package_;
	}

	public ResourceSet getResourceSet() {
		return _resourceSet;
	}
	
	public HashMap<String, Stereotype> getStereotypes() {
		return _stereotypes;
	}
	
	public void referenceMetaclass(Profile profile, Classifier metaclass) {

		profile.createMetaclassReference(metaclass);
		
		_debug("Metaclass '" + metaclass.getQualifiedName() + "' referenced.");
	}
	
	public void registerPathmaps(URI umlResourcePluginURI) {
		URIConverter.URI_MAP.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP),
			umlResourcePluginURI.appendSegment("libraries").appendSegment(""));
		
		URIConverter.URI_MAP.put(URI.createURI(UMLResource.METAMODELS_PATHMAP),
			umlResourcePluginURI.appendSegment("metamodels").appendSegment(""));
		
		URIConverter.URI_MAP.put(URI.createURI(UMLResource.PROFILES_PATHMAP),
			umlResourcePluginURI.appendSegment("profiles").appendSegment(""));
	}

	public void registerExtensions() {
		Map extensionFactoryMap = 
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap();

		extensionFactoryMap.put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		extensionFactoryMap.put(Ecore2XMLResource.FILE_EXTENSION, Ecore2XMLResource.Factory.INSTANCE);
		extensionFactoryMap.put(UML22UMLResource.FILE_EXTENSION, UML22UMLResource.Factory.INSTANCE);
	}
	
	public void registerPackages(ResourceSet resourceSet) {
        Map packageRegistry = resourceSet.getPackageRegistry();
       
        packageRegistry.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        packageRegistry.put(Ecore2XMLPackage.eNS_URI,Ecore2XMLPackage.eINSTANCE);
        packageRegistry.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
	}

	public void save(org.eclipse.uml2.uml.Package package_, URI uri) throws IOException {

        Resource resource = _resourceSet.createResource(uri);

        resource.getContents().add(package_);
        resource.save(null);
        
        _debug("Done.");
    }
	
	private void _debug(String msg){
		if (_debug)
			out.println(msg);
	}

	private boolean _debug;
	private ResourceSet _resourceSet;
	private HashMap<String, Stereotype> _stereotypes;
	private HashMap<String, Profile> _profiles;
}
