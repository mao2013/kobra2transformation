package org.orcas.kobra2generator.uml2;

import static java.lang.System.out;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ocl.uml.ExpressionInOCL;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Extension;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.OpaqueExpression;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageImport;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.impl.ClassImpl;
import org.eclipse.uml2.uml.resource.UMLResource;

public class UML2Util {

	public UML2Util() {
		
		_debug = true;
		
		_profiles = new HashMap<String, Profile>();
		_stereotypes = new HashMap<String, Stereotype>();

		UMLPackage.eINSTANCE.eClass();

		Registry registry = _createRegistry();
				
		_resourceSet = new ResourceSetImpl();
		_resourceSet.setResourceFactoryRegistry(registry);

		//URI umlResourcePluginURI = _findPluginURI();
		//registerPathmaps(umlResourcePluginURI);
		

		/*Map<String, Object> protMap = UMLResource.Factory.Registry.INSTANCE.getProtocolToFactoryMap();

		try {
			protMap.put("oclenv", Class.forName("org.eclipse.ocl.uml.UMLEnvironmentFactory").newInstance()); 
		} catch (Exception e) {
			e.printStackTrace();
		}*/
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
		
		Generalization generalization = specificClassifier.getGeneralization(generalClassifier);
		
		if ( generalization == null){
			generalization = specificClassifier.createGeneralization(generalClassifier);
			
			_debug("Generalization " + specificClassifier.getQualifiedName() + 
					" ->> " + generalClassifier.getQualifiedName() + " created.");
		}
		
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
			
		Profile profile = null;

		if (package_ != null)
			profile = createOrRetrieveProfile(package_.getName());
		
		Stereotype stereotype = _stereotypes.get(name);
		
		if (stereotype == null){
			stereotype = profile.createOwnedStereotype(name, isAbstract);
			_stereotypes.put(name, stereotype);
				
		//	org.eclipse.uml2.uml.Class metaclass = getMetaclass(name);
			
			//if (metaclass != null){
				
				Class metaclass = referenceMetaclass(profile, name);
				
			//	System.out.println("is meta " +  metaclass.isMetaclass());
				if (metaclass != null){
				createExtension(metaclass, stereotype, false);
				}
			//}
			_debug("Stereotype '" + stereotype.getName() + "' created in " + profile.getName());
		} 
		
		return stereotype;
	}

	public Profile createOrRetrieveProfile(String name) {
		
		Profile profile = _profiles.get(name);
		
		if (profile == null){
	        profile = UMLFactory.eINSTANCE.createProfile();
	        profile.setName(name);
	        _profiles.put(name, profile);
	        _debug("Profile '" + name + "' created.");
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
		Resource resource = _resourceSet.getResource(uri, true);
		//Resource umlMetaResource = 
		//	_resourceSet.getResource(URI.createURI(_umlResourcePluginURI + "metamodels/UML.metamodel.uml"), true) ;
		
		org.eclipse.uml2.uml.Package package_ =
		   	(org.eclipse.uml2.uml.Package) EcoreUtil.getObjectByType(
		   		resource.getContents(), UMLPackage.Literals.PACKAGE);
		
	/*	org.eclipse.uml2.uml.Package umlMetaPackage_ =
		   	(org.eclipse.uml2.uml.Package) EcoreUtil.getObjectByType(
		   		umlMetaResource.getContents(), UMLPackage.Literals.PACKAGE);*/
		
		   
        _resourceSet.getPackageRegistry().put(package_.getQualifiedName(), package_);
      //  _resourceSet.getPackageRegistry().put(UMLResource.UML_METAMODEL_URI, umlMetaPackage_);

        return package_;
	}

	public ResourceSet getResourceSet() {
		return _resourceSet;
	}
	
	public HashMap<String, Stereotype> getStereotypes() {
		return _stereotypes;
	}
	
	public Class referenceMetaclass(Profile profile, String metaclassName) {
		
		Model umlMetamodel = (Model) load(URI.createURI(UMLResource.UML_METAMODEL_URI));

		Class metaclass = null;
		
		if (umlMetamodel.getOwnedType(metaclassName) instanceof  Class){
			
			metaclass = (Class) umlMetamodel.getOwnedType(metaclassName);
			
			if (metaclass != null){
				profile.createMetaclassReference(metaclass);
	
				_debug("Metaclass '" + metaclass.getQualifiedName() + "' referenced.");
			}
		
		}
		
		return metaclass;
	}
	
	public void registerPathmaps(URI umlResourcePluginURI) {
		
		URIConverter.URI_MAP.put(URI.createPlatformPluginURI(UMLResource.LIBRARIES_PATHMAP, true),
			umlResourcePluginURI.appendSegment("libraries").appendSegment(""));
		
		URIConverter.URI_MAP.put(URI.createPlatformPluginURI(UMLResource.METAMODELS_PATHMAP, true),
			umlResourcePluginURI.appendSegment("metamodels").appendSegment(""));
		
		URIConverter.URI_MAP.put(URI.createPlatformPluginURI(UMLResource.PROFILES_PATHMAP, true),
			umlResourcePluginURI.appendSegment("profiles").appendSegment(""));
	}

	/*public void registerPackages(ResourceSet resourceSet) {
        Map packageRegistry = resourceSet.getPackageRegistry();
       
        packageRegistry.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        packageRegistry.put(Ecore2XMLPackage.eNS_URI,Ecore2XMLPackage.eINSTANCE);
        packageRegistry.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
	}*/
	
	public org.eclipse.uml2.uml.Class getMetaclass(String name){
		
		org.eclipse.uml2.uml.Package package_ =
			(Package) _resourceSet.getPackageRegistry().get(UMLResource.UML_METAMODEL_URI);
	
		EList<Element> elementList = package_.getOwnedElements();
		
		for (Element element : elementList) {
			if (element instanceof ClassImpl){
				ClassImpl metaclass = (ClassImpl) element;
				
				if (metaclass.getName().equals(name)){
					return metaclass;
				}
			}
		}
		return null;
	}

	public void save(org.eclipse.uml2.uml.Package package_, URI uri) throws IOException {

        Resource resource = _resourceSet.createResource(uri);
        resource.getContents().add(package_);
        resource.save(URIConverter.INSTANCE.createOutputStream(uri), null);
        
        _debug("Done.");
    }
	
	
	public Registry _createRegistry() {
		
		Registry registry = Resource.Factory.Registry.INSTANCE;
		
		Map extensionFactoryMap = registry.getExtensionToFactoryMap();
		extensionFactoryMap.put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		
		return registry;
	}

	private void _debug(String msg){
		if (_debug)
			out.println(msg);
	}

	private URI _findPluginURI(){
		final String profile = "profiles/Standard.profile.uml";
		URL url = getClass().getClassLoader().getResource(profile);
		String urlString = url.toString();
		
		if (url == null || !url.toString().endsWith(profile)) {
			throw new RuntimeException("Error getting Standard.profile.uml");
		}
		
		urlString = urlString.substring(0, urlString.length() - profile.length());
		return URI.createURI(urlString);
	}	

	private boolean _debug;
	private ResourceSet _resourceSet;
	private HashMap<String, Stereotype> _stereotypes;
	private HashMap<String, Profile> _profiles;
}