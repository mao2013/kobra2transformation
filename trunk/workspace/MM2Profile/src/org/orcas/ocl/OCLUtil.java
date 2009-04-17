package org.orcas.ocl;

import static java.lang.System.out;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Property;
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
	
	public Constraint convertAssociation2Constraint(
			OCLHelper<Classifier, ?, ?, Constraint> helper2, Property property)
			throws ParserException {

		Constraint c = null;
		Classifier owner = (Classifier) property.getType();
		if (!owner.getName().equals("EnumerationLiteral")) {
			Classifier ownedEnd = (Classifier) property.getOtherEnd().getType();

			// will represent the invariant body
			StringBuffer associationInOCL = new StringBuffer();
			associationInOCL.append("ownedAttribute.association->forAll(self.oclIsKindOf(");
			associationInOCL.append(ownedEnd.getName().concat("))"));
			helper2.setContext(owner);
			c = (Constraint) helper2.createInvariant(associationInOCL.toString());
			c.setName("associationViaOCL_" + owner.getName() + "_" + ownedEnd.getName());
			c.setContext(owner);
			ExpressionInOCL eio = (ExpressionInOCL) c.getSpecification();
			System.out.println("OCL body: " + eio.getBodyExpression());
		}
		return c;
	}
	
	public List<Constraint> evaluateOCL(ResourceSet rset, String oclPath)
			throws ParserException, IOException {
		
		return null;
	}

	public String makeRuleEMFCompliant(String rule) {

		String constraintHeader = null;
		String setBody = null;
		StringBuffer emfCompliantRule = null;

		if (rule.contains("Set{")) {
			rule = "context Element inv elementInv: "
					+ "Set{StructuralElement, "
					+ "StructuralBehavioralElement, " + "ConstraintElement, "
					+ "BehavioralElement}";

			constraintHeader = rule.substring(0, rule.indexOf(":"));
			setBody = rule.substring((rule.indexOf(":") + 1), rule.length())
					.trim();
			StringTokenizer tokenizer = new StringTokenizer(setBody, ",");
			emfCompliantRule = new StringBuffer();

			while (tokenizer.hasMoreTokens()) {
				String currentToken = tokenizer.nextToken();
				String nextToken = tokenizer.nextToken();
				if (nextToken != null)
					emfCompliantRule.append("self.oclIsKindOf(".concat(
							currentToken).concat(") or "));
				else
					emfCompliantRule.append("self.oclIsKindOf(".concat(
							currentToken).concat(")"));
			}

		}
		return constraintHeader.concat(emfCompliantRule.toString());
	}

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
	
	public OCLHelper<Classifier, ?, ?, Constraint> getOCLHelper() {
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