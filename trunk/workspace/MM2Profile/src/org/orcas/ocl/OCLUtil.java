package org.orcas.ocl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
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

public class OCLUtil {

	private static OCL<?, ?, ?, ?, ?, ?, ?, ?, ?, Constraint, ?, ?> ocl;
	private static OCLHelper<Classifier, ?, ?, Constraint> helper;

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
		EcoreEnvironment ecoreEnv = (EcoreEnvironment) EcoreEnvironmentFactory.INSTANCE
				.createEnvironment();
		reg.registerEnvironment(ecoreEnv);
		UMLEnvironment umlEnv = new UMLEnvironmentFactory(rset)
				.createEnvironment();
		reg.registerEnvironment(umlEnv);

		// register their standard library packages
		ecoreEnv.getOCLStandardLibrary();
		umlEnv.getOCLStandardLibrary();
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
			c.setName("associationInProfile_" + owner.getName() + "_"+ ownedEnd.getName());
			c.setContext(owner);
			ExpressionInOCL eio = (ExpressionInOCL) c.getSpecification();
			System.out.println("OCL body: " + eio.getBodyExpression());
		}
		return c;
	}

	public List<Constraint> parseOCL(ResourceSet rset, String oclPath)
			throws ParserException, IOException {
		UMLEnvironmentFactory umlFactory = new UMLEnvironmentFactory(rset);
		ocl = OCL.newInstance(umlFactory);
		ocl.setEvaluationTracingEnabled(true);
		ocl.setParseTracingEnabled(true);
		helper = (OCLHelper<Classifier, ?, ?, Constraint>) ocl
				.createOCLHelper();
		File file = new File(oclPath);
		FileInputStream fis = new FileInputStream(file);
		OCLInput input = new OCLInput(fis);
		List<Constraint> constraints = ocl.parse(input);
		System.out.println("OCLs parsed.");
		return constraints;
	}

	public List<Constraint> evaluateOCL(ResourceSet rset, String oclPath)
			throws ParserException, IOException {
		return null;
	}

	public static OCLHelper<Classifier, ?, ?, Constraint> getOCLHelper() {
		return helper;
	}
}