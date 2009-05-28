package org.orcas.kobra2generator.ocl;

import static java.lang.System.out;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ocl.Environment;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.OCLInput;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.EcoreEnvironment;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.ocl.uml.ExpressionInOCL;
import org.eclipse.ocl.uml.UMLEnvironment;
import org.eclipse.ocl.uml.UMLEnvironmentFactory;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.Type;

public class OCLUtil {
	
	public OCLUtil(){
		_debug = true;
	}

	/**
	 * Converts an Association to a Constraint in order to allow associations in
	 * a profile definition
	 * 
	 * @param helper
	 * @param relationship
	 * @return a <code>Constraint</code> representation of an
	 *         <code>Association</code>
	 * @throws ParserException
	 */
	public static void configureOCL(ResourceSet rset) {
		Environment.Registry reg = Environment.Registry.INSTANCE;

		// register prototype environments
		EcoreEnvironment ecoreEnv = (EcoreEnvironment) EcoreEnvironmentFactory.INSTANCE.createEnvironment();
		reg.registerEnvironment(ecoreEnv);
		UMLEnvironment umlEnv = new UMLEnvironmentFactory(rset).createEnvironment();
		reg.registerEnvironment(umlEnv);

		// register their standard library packages
		ecoreEnv.getOCLStandardLibrary();
		umlEnv.getOCLStandardLibrary();
	}
	
	public Constraint convertAssociation2Constraint(Association association) throws ParserException {
		Constraint c = null;
		EList<Type> endTypes = association.getEndTypes();
		Classifier c1 = (Classifier) endTypes.get(0);
		Classifier c2 = (Classifier) endTypes.get(1);

		// will represent the invariant body
		StringBuffer associationInOCL = new StringBuffer();
		associationInOCL.append("self.endType->forAll( t | t.oclIsKindOf(");
		associationInOCL.append(c1.getName().concat(")) and "));
		associationInOCL.append("t.oclAsType(" + c1.getName() + ").getAppliedStereotypes()->select(a | a.name='"
				+ c2.getName() + "')->notEmpty())");
		getOCLHelper().setContext(c1);
		c = (Constraint) getOCLHelper().createInvariant(associationInOCL.toString());
		c.setName("associationViaOCL_" + c1.getName() + "_" + c2.getName());
		ExpressionInOCL eio = (ExpressionInOCL) c.getSpecification();
		System.out.println("OCL body: " + eio.getBodyExpression());
		return c;
	}
	
	public List<Constraint> evaluateOCL(ResourceSet rset, String oclPath)
			throws ParserException, IOException {
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Constraint> parseInvariantOCL(
			ResourceSet rset, Classifier context, 
			String constraintName, String constraintBody)  throws IOException{
		
		UMLEnvironmentFactory umlFactory = new UMLEnvironmentFactory(rset);
		OCL ocl = OCL.newInstance(umlFactory);
		_helper = (OCLHelper<Classifier, ?, ?, Constraint>) ocl.createOCLHelper();
		StringBuilder constraint = new StringBuilder();
		constraint.append("context ");
		constraint.append(context.getQualifiedName());
		constraint.append(" inv ");	
		constraint.append(constraintName);
		constraint.append(": ");	
		constraint.append(constraintBody);
		
		OCLInput input = new OCLInput(constraint.toString());
		_debug("Parsing " + constraint.toString());
		
		List<Constraint> constraints = new ArrayList<Constraint>();
		
		try{
			constraints = ocl.parse(input);
			_debug("Parsed.");
		} catch (Exception e) {
			_debug("Cant parse.");
		}

		return constraints;
	}
	
	public static OCLHelper<Classifier, ?, ?, Constraint> getOCLHelper() {
		return _helper;
	}
	
	public void transformOCL(
		Type type,List<Constraint> inputConstraints, Map<String, Stereotype> stereotypes){
		
		for (Constraint constraint : inputConstraints) {
			
		}
	}

	private void _debug(String msg){
		if (_debug)
			out.println(msg);
	}
	
	private boolean _debug;
	private static OCL<?, ?, ?, ?, ?, ?, ?, ?, ?, Constraint, ?, ?> _ocl;
	private static OCLHelper<Classifier, ?, ?, Constraint> _helper;
}