package org.orcas.uml2;


import static java.lang.System.out;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.resource.UML22UMLResourceFactoryImpl;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.util.UMLUtil;

public class UML2Util {
	
	public UML2Util(){
		_resourceSet = new ResourceSetImpl();

		
		Registry registry = Resource.Factory.Registry.INSTANCE;
		registry.getExtensionToFactoryMap().put("uml", new UMLResourceFactoryImpl());
		
		
		_resourceSet.setResourceFactoryRegistry(registry);
	}
	
	protected static Property createAttribute(
		org.eclipse.uml2.uml.Class class_,String name, Type type, int lowerBound, int upperBound) {

		Property attribute = class_.createOwnedAttribute(name, type,lowerBound, upperBound);
		
		StringBuffer sb = new StringBuffer();
		sb.append("Attribute '");
		sb.append(attribute.getQualifiedName());
		sb.append("' : ");
		sb.append(type.getQualifiedName());
		sb.append(" [");
		sb.append(lowerBound);
		sb.append("..");
		sb.append(LiteralUnlimitedNatural.UNLIMITED == upperBound ? "*": String.valueOf(upperBound));
		sb.append("]");
		sb.append(" created.");
		
		out.println(sb.toString());
		
		return attribute;
	}
	
	public Generalization createGeneralization(
		Classifier specificClassifier, Classifier generalClassifier) {

		Generalization generalization = specificClassifier.createGeneralization(generalClassifier);
		
		out.println(
			"Generalization " + specificClassifier.getQualifiedName() + 
			" ->> " + generalClassifier.getQualifiedName() + " created.");

		return generalization;
	}
	
	public Profile createProfile(String name) {

        Profile profile = UMLFactory.eINSTANCE.createProfile();

        profile.setName(name);

        out.println("Profile '" + profile.getQualifiedName() + "' created.");

        return profile;
    }
	
	public Stereotype createStereotype(Profile profile, String name, boolean isAbstract) {
	
		Stereotype stereotype = profile.createOwnedStereotype(name, isAbstract);
	
	    out.println("Stereotype '" + stereotype.getQualifiedName() + "' created.");
	
	    return stereotype;
	}
	
	public void defineProfile(Profile profile) {

        profile.define();

        out.println("Profile '" + profile.getQualifiedName() + "' defined.");
    }
	
	public PrimitiveType importPrimitiveType(
			org.eclipse.uml2.uml.Package package_, String name) {

		Model umlLibrary = (Model) load(URI.createURI(UMLResource.UML_PRIMITIVE_TYPES_LIBRARY_URI));
		
		PrimitiveType primitiveType = (PrimitiveType) umlLibrary.getOwnedType(name);
		package_.createElementImport(primitiveType);
		
		out.println("Primitive type '" + primitiveType.getQualifiedName() + "' imported.");

		return primitiveType;

	}
	
	public org.eclipse.uml2.uml.Package load(URI uri) {

        org.eclipse.uml2.uml.Package package_ = null;

        Resource resource = _resourceSet.getResource(uri, true);
        
        UMLPackage umlPackage = UMLPackage.eINSTANCE;
        
        _resourceSet.getPackageRegistry().put(umlPackage.getNsURI(), umlPackage);     

        package_ = (org.eclipse.uml2.uml.Package) EcoreUtil.getObjectByType(
        		 resource.getContents(), UMLPackage.Literals.PACKAGE);
        
        _resourceSet.getPackageRegistry().put(package_.getQualifiedName(), package_);
        
        _resourceSet.getResources().add(umlPackage.eResource());
        
        return package_;
	}
	
	public static ResourceSet getResourceSet() {
		return _resourceSet;
	}
	
	public org.eclipse.uml2.uml.Class referenceMetaclass(Profile profile,String name) {

		Model umlMetamodel = (Model) load(URI.createURI(UMLResource.UML_METAMODEL_URI));
		
		org.eclipse.uml2.uml.Class metaclass = (org.eclipse.uml2.uml.Class) umlMetamodel.getOwnedType(name);

		profile.createMetaclassReference(metaclass);

		out.println("Metaclass '" + metaclass.getQualifiedName() + "' referenced.");

		return metaclass;
	}
	
	public void save(org.eclipse.uml2.uml.Package package_, URI uri) throws IOException {

        Resource resource = _resourceSet.createResource(uri);

        resource.getContents().add(package_);
        resource.save(null);
        
        out.println("Done.");
    }
	
	private static ResourceSet _resourceSet;
}
