package org.orcas.ocl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.OCLInput;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.uml.UMLEnvironmentFactory;
import org.eclipse.uml2.uml.Constraint;

public class OCLUtil {
	
	public List<Constraint> parseOCL(
		ResourceSet rset , String oclPath)  throws ParserException, IOException{
		
		UMLEnvironmentFactory umlFactory = new UMLEnvironmentFactory(rset);
		
		OCL ocl = OCL.newInstance(umlFactory);
						
		File file = new File(oclPath);
		FileInputStream fis = new FileInputStream(file);
		
		OCLInput input = new OCLInput(fis);
		
		List<Constraint> constraints = ocl.parse(input);
		
		System.out.println("OCLs parsed.");
		
		return constraints;
	}
	
}
