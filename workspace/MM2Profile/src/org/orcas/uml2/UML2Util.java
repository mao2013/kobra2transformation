package org.orcas.uml2;

import static java.lang.System.out;

import java.io.IOException;
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
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.OpaqueExpression;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageImport;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.internal.resource.UMLResourceFactoryImpl;
import org.eclipse.uml2.uml.resource.UML22UMLResource;
import org.eclipse.uml2.uml.resource.UMLResource;

public class UML2Util {

	public UML2Util() {
		_resourceSet = new ResourceSetImpl();
		Registry registry = Resource.Factory.Registry.INSTANCE;
		registry.getExtensionToFactoryMap().put("uml", new UMLResourceFactoryImpl());

		// configure the fake "oclenv:" resource factory
		Map<String, Object> protMap = UMLResource.Factory.Registry.INSTANCE.getProtocolToFactoryMap();

		try {
			protMap.put("oclenv", Class.forName("org.eclipse.ocl.uml.UMLEnvironmentFactory").newInstance()); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (Exception e) {
			e.printStackTrace();
		}
		_resourceSet.setResourceFactoryRegistry(registry);
	}

	protected static Property createAttribute(
			org.eclipse.uml2.uml.Class class_, String name, Type type,
			int lowerBound, int upperBound) {

		Property attribute = class_.createOwnedAttribute(name, type,
				lowerBound, upperBound);

		StringBuffer sb = new StringBuffer();
		sb.append("Attribute '");
		sb.append(attribute.getQualifiedName());
		sb.append("' : ");
		sb.append(type.getQualifiedName());
		sb.append(" [");
		sb.append(lowerBound);
		sb.append("..");
		sb.append(LiteralUnlimitedNatural.UNLIMITED == upperBound ? "*"
				: String.valueOf(upperBound));
		sb.append("]");
		sb.append(" created.");
		out.println(sb.toString());
		return attribute;
	}

	public Generalization createGeneralization(Classifier specificClassifier,
			Classifier generalClassifier) {
		Generalization generalization = specificClassifier
				.createGeneralization(generalClassifier);
		out.println("Generalization " + specificClassifier.getQualifiedName()
				+ " ->> " + generalClassifier.getQualifiedName() + " created.");
		return generalization;
	}

	public Profile createProfile(String name) {
		Profile profile = UMLFactory.eINSTANCE.createProfile();
		profile.setName(name);
		out.println("Profile '" + profile.getQualifiedName() + "' created.");
		return profile;
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

	public static void registerPathmaps(URI umlResourcePluginURI) {
		URIConverter.URI_MAP.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP),
				umlResourcePluginURI.appendSegment("libraries").appendSegment(
						""));
		URIConverter.URI_MAP.put(URI.createURI(UMLResource.METAMODELS_PATHMAP),
				umlResourcePluginURI.appendSegment("metamodels").appendSegment(
						""));
		URIConverter.URI_MAP.put(URI.createURI(UMLResource.PROFILES_PATHMAP),
				umlResourcePluginURI.appendSegment("profiles")
						.appendSegment(""));
	}

	public static void registerExtensions() {
		Map extensionFactoryMap = Resource.Factory.Registry.INSTANCE
				.getExtensionToFactoryMap();
		extensionFactoryMap.put(UMLResource.FILE_EXTENSION,
				UMLResource.Factory.INSTANCE);
		extensionFactoryMap.put(Ecore2XMLResource.FILE_EXTENSION,
				Ecore2XMLResource.Factory.INSTANCE);
		extensionFactoryMap.put(UML22UMLResource.FILE_EXTENSION,
				UML22UMLResource.Factory.INSTANCE);

	}
	
	public static void registerPackages(ResourceSet resourceSet) {
        Map packageRegistry = resourceSet.getPackageRegistry();
        packageRegistry.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        packageRegistry.put(Ecore2XMLPackage.eNS_URI,
              Ecore2XMLPackage.eINSTANCE);
        packageRegistry.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
        
        
  }
	

	/*
	 * public Extension createExtension(Stereotype s, boolean isRequired){
	 * Extension extension = s.createExtension(); return extension; }
	 */

	public Stereotype createStereotype(Profile profile, String name,
			boolean isAbstract) {
		Stereotype stereotype = profile.createOwnedStereotype(name, isAbstract);
		out.println("Stereotype '" + stereotype.getQualifiedName()
				+ "' created.");
		return stereotype;
	}

	public void defineProfile(Profile profile) {
		profile.define();
		out.println("Profile '" + profile.getQualifiedName() + "' defined.");
	}

	public PrimitiveType importPrimitiveType(
			org.eclipse.uml2.uml.Package package_, String name) {
		Model umlLibrary = (Model) load(URI
				.createURI(UMLResource.UML_PRIMITIVE_TYPES_LIBRARY_URI));
		PrimitiveType primitiveType = (PrimitiveType) umlLibrary
				.getOwnedType(name);
		package_.createElementImport(primitiveType);
		out.println("Primitive type '" + primitiveType.getQualifiedName()
				+ "' imported.");
		return primitiveType;
	}

	public Package load(URI uri) {
		org.eclipse.uml2.uml.Package package_ = null;
		Resource resource = _resourceSet.getResource(uri, true);
		UMLPackage umlPackage = UMLPackage.eINSTANCE;
		_resourceSet.getPackageRegistry()
				.put(umlPackage.getNsURI(), umlPackage);
		package_ = (org.eclipse.uml2.uml.Package) EcoreUtil.getObjectByType(
				resource.getContents(), UMLPackage.Literals.PACKAGE);
		_resourceSet.getPackageRegistry().put(package_.getQualifiedName(),
				package_);
		_resourceSet.getResources().add(umlPackage.eResource());
		return package_;
	}

	public static ResourceSet getResourceSet() {
		return _resourceSet;
	}

	public org.eclipse.uml2.uml.Class referenceMetaclass(Profile profile,
			String name) {
		Model umlMetamodel = (Model) load(URI
				.createURI(UMLResource.UML_METAMODEL_URI));
		umlMetamodel.setName(UMLFactory.eINSTANCE.getUMLPackage().getName());
		org.eclipse.uml2.uml.Class metaclass = (org.eclipse.uml2.uml.Class) umlMetamodel
				.getOwnedType(name);
		profile.createMetaclassReference(metaclass);
		out.println("Metaclass '" + metaclass.getQualifiedName()
				+ "' referenced.");
		return metaclass;
	}

	public PackageImport createMetamodelReference(Profile profile, String name) {

		// set up a reference to the UML2 metamodel
		Model metamodel = UMLFactory.eINSTANCE.createModel();
		metamodel.setName(name);

		PackageImport metamodelReference = UMLFactory.eINSTANCE
				.createPackageImport();
		metamodelReference.setImportedPackage(metamodel);
		return metamodelReference;
	}

	public void save(org.eclipse.uml2.uml.Package p, URI uri)
			throws IOException {
		Resource resource = _resourceSet.createResource(uri);
		resource.getContents().add(p);
		resource.save(null);
		out.println("Done.");
	}

	private static ResourceSet _resourceSet;
}
