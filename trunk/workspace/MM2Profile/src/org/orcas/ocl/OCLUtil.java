package org.orcas.ocl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.OCLInput;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.ocl.uml.ExpressionInOCL;
import org.eclipse.ocl.uml.UMLEnvironmentFactory;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.ValueSpecification;

public class OCLUtil {

	private static OCL ocl;
	private static OCLHelper<?, ?, ?, Constraint> helper;

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
	public Constraint convertAssociation2Constraint(
			OCLHelper<Classifier, ?, ?, ?> helper, Property property)
			throws ParserException {

		Constraint c = null;
		Classifier owner = (Classifier) property.getType();
		if (!owner.getName().equals("EnumerationLiteral")) {
			Classifier ownedEnd = (Classifier) property.getOtherEnd().getType();
			StringBuffer associationInOCL = new StringBuffer();
			associationInOCL.append("ownedAttribute->forAll(self.oclIsKindOf(");
			associationInOCL.append(ownedEnd.getName().concat("))"));
			helper.setContext(owner);
			c = (Constraint) helper.createInvariant(associationInOCL.toString());
			c.setName("associationInProfile_" + owner.getName() + "_"
					+ ownedEnd.getName());
		}
		return c;
	}

	public List<Constraint> parseOCL(ResourceSet rset, String oclPath)
			throws ParserException, IOException {

		UMLEnvironmentFactory umlFactory = new UMLEnvironmentFactory(rset);

		ocl = OCL.newInstance(umlFactory);
		ocl.setEvaluationTracingEnabled(true);
		ocl.setParseTracingEnabled(true);
		helper = ocl.createOCLHelper();
		File file = new File(oclPath);
		FileInputStream fis = new FileInputStream(file);

		OCLInput input = new OCLInput(fis);

		List<Constraint> constraints = ocl.parse(input);

		System.out.println("OCLs parsed.");

		return constraints;
	}

	public static OCL getOCL() {
		return ocl;
	}

	public static OCLHelper getOCLHelper() {
		return helper;
	}

}
