/*
 *+--------------------------------------------------------------------------------------+
 *| Licensed Materials - Use restricted, please refer to the "Samples Gallery" terms and |
 *| conditions in the IBM International Program License Agreement.						 |	
 *| © Copyright IBM Corporation 2003 - 2006. All Rights Reserved.						 |
 *+--------------------------------------------------------------------------------------+
 */

package com.ibm.xtools.transform.samples.modeltomodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.InterfaceRealization;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;

/**
 * An utility class to search a model for elements with specified condition.
 */
public class ModelUtility {

	/**
	 * Constructor.
	 */
	private ModelUtility() {
		super();
	}

	/**
	 * Searches a given model for package with the specified name.
	 * 
	 * @param model
	 *            Source model
	 * @param name
	 *            Name of the package
	 * @return Returns the package, if present. Otherwise, returns null.
	 */
	public static Package getPackageByName(Model model, String name) {
		return (Package) getElementByKindWithName(model, UMLPackage.eINSTANCE
			.getPackage(), name);
	}

	/**
	 * Searches a given package for an interface with the specified name.
	 * 
	 * @param pkg
	 *            The source package
	 * @param name
	 *            The name of the interface
	 * @return Returns the interface, if present. Otherwise, returns null
	 */
	public static Interface getInterfaceByName(Package pkg, String name) {
		return (Interface) getElementByKindWithName(pkg, UMLPackage.eINSTANCE
			.getInterface(), name);
	}

	/**
	 * Searches a given package for an interface with the specified name.
	 * 
	 * @param pkg
	 *            The source package
	 * @param name
	 *            The name of the interface
	 * @return Returns the interface, if present. Otherwise, returns null
	 */
	public static Class getClassByName(Package pkg, String name) {
		return (Class) getElementByKindWithName(pkg, UMLPackage.eINSTANCE
			.getClass_(), name);
	}

	/**
	 * Checks if a class contains the specified operation
	 * 
	 * @param cls
	 *            The class to be searched for the presence of the specified
	 *            operation.
	 * @param op
	 *            The operation
	 * @return True if the class contains the operation.
	 */
	public static boolean classContainsOperation(Class cls, Operation op) {
		boolean foundOperation = false;
		Iterator iter = cls.getOwnedOperations().iterator();
		while (foundOperation == false && iter.hasNext()) {
			Operation ownedOp = (Operation) iter.next();
			foundOperation = areOperationsEqual(ownedOp, op);
		}
		return foundOperation;
	}

	/**
	 * Checks if a interface contains the specified operation
	 * 
	 * @param cls
	 *            The interface to be searched for the presence of the specified
	 *            operation.
	 * @param op
	 *            The operation
	 * @return True if the interface contains the operation.
	 */
	public static boolean interfaceContainsOperation(Interface intface,
			Operation op) {
		boolean foundOperation = false;
		Iterator iter = intface.getOwnedOperations().iterator();
		while (foundOperation == false && iter.hasNext()) {
			Operation ownedOp = (Operation) iter.next();
			foundOperation = areOperationsEqual(ownedOp, op);
		}
		return foundOperation;
	}

	/**
	 * Checks if two operations are equal.
	 * 
	 * Two operations are defined to be equal if they have the same name and the
	 * same signature.
	 * 
	 * @param op1
	 *            Operation 1
	 * @param op2
	 *            Operation 2
	 * @return True if the operations have the same name and signature.
	 */
	public static boolean areOperationsEqual(Operation op1, Operation op2) {
		return (op1.getName().equals(op2.getName()))
			&& haveSameSignature(op1, op2);
	}

	/**
	 * Checks if two operations have the same signature.
	 * 
	 * Two operations are defined to have the same signature if the parameter
	 * lists of the operations have the same number of parameters, the
	 * parameters appear in the lists in the same order, and each parameter in
	 * one operation has the same signature as the corresponding parameter of
	 * the second operation. Note that the return parameter does not contribute
	 * to the signature of an operation.
	 * 
	 * @param op1
	 *            First operation
	 * @param op2
	 *            Second operation
	 * @return True if the operations have the same signature
	 */
	public static boolean haveSameSignature(Operation op1, Operation op2) {
		boolean isSame = false;

		// To ignore return parameters, create a list of non-return parameters
		// for each operation
		List op1Parameters = getNonReturnParameters(op1);
		List op2Parameters = getNonReturnParameters(op2);

		if (op1Parameters.size() == op2Parameters.size()) {
			Iterator op1paramIter = op1Parameters.iterator();
			Iterator op2paramIter = op2Parameters.iterator();

			isSame = true;

			while (op1paramIter.hasNext() && isSame == true) {
				isSame = haveSameSignature((Parameter) op1paramIter.next(),
					(Parameter) op2paramIter.next());
			}
		}

		return isSame;
	}

	/**
	 * Checks if two parameters have the same signature.
	 * 
	 * Two parameters are defined to have the same signature if they are of the
	 * same type and they have the same direction. The names of the parameters
	 * are ignored because in the context of an operation the names do not
	 * determine the signature of an operation.
	 * 
	 * Also, if the types of the parameters are unspecified (that is if
	 * getType() for each parameter is <code>null</code>, the parameters
	 * are defined to have the same signature only if their directions are the
	 * same.
	 * 
	 * @param p1
	 *            First parameter
	 * @param p2
	 *            Second parameter
	 * @return True if the parameters have the same types and direction
	 */
	public static boolean haveSameSignature(Parameter p1, Parameter p2) {
		boolean isSame = false;
		Type type1 = p1.getType();
		Type type2 = p2.getType();
		
        if (p1.getDirection() == p2.getDirection()) {
            if (type1 == null && type2 == null) {
                isSame = true;
            } else if (type1 != null && type2 != null) {
                isSame = type1.eClass() == type2.eClass();
            }
		}
        return isSame;
	}

	/**
	 * Creates a list of non-return (in, out, and in-out) parameters of an
	 * operation.
	 * 
	 * @param op
	 *            The specified operation
	 * @return List of non-return parameters.
	 */
	private static List getNonReturnParameters(Operation op) {
		ArrayList plist = new ArrayList();
		Iterator iter = op.getOwnedParameters().iterator();
		while (iter.hasNext()) {
			Parameter p = (Parameter) iter.next();
			if (p.getDirection() != ParameterDirectionKind.RETURN_LITERAL) {
				plist.add(p);
			}
		}
		return plist;
	}

	/**
	 * Searches a given class for a property with the specified name
	 * 
	 * @param cls
	 *            The source class
	 * @param name
	 *            The name of the property
	 * @return Returns the property, if present. Otherwise, returns null.
	 */
	public static Property getPropertyByName(Class cls, String name) {
		return (Property) getElementByKindWithName(cls, UMLPackage.eINSTANCE
			.getProperty(), name);
	}

	/**
	 * Gets a realization from a class.
	 * 
	 * Given a realization client and a realizing classifier, this method
	 * retrieves a realization from the client. If the realization is not
	 * present in the client, it returns a null.
	 * 
	 * @param realizationClient
	 *            Realization client
	 * @param realizingClassifier
	 *            Realizing classifier - in this case an interface
	 * @return A realization if present. Otherwise, returns a null.
	 */

	public static InterfaceRealization getRealization(
			Class realizationClient, Interface realizingClassifier) {

        return realizationClient.getInterfaceRealization(null, realizingClassifier);
	}

	/**
	 * Searches a source model element for an element of a specific kind with a
	 * a specified name.
	 * 
	 * @param src
	 *            Source model element that is searched
	 * @param elementKind
	 *            Kind of element that being searched for
	 * @param name
	 *            Name of the element being searched for
	 * @return Returns the element, if present. Otherwise, returns null
	 */
	private static NamedElement getElementByKindWithName(NamedElement src,
			EClass elementKind, String name) {

		NamedElement retElement = null;
		if (src != null && name != null) {
			EList elements = src.eContents();
			for (Iterator i = elements.iterator(); i.hasNext();) {
				Object obj = i.next();
				if (obj instanceof NamedElement) {
					NamedElement element = (NamedElement) obj;
					if (elementKind == element.eClass()
						&& name.equals(element.getName())) {
						retElement = element;
						break;
					}
				}
			}
		}
		return retElement;
	}

	/**
	 * Gets the name of the given model element.
	 * 
	 * It assumes that the given element is an instance of NamedElement or one
	 * of its sub-classes. If the element is not an instance of NamedElement, an
	 * empty string is returned.
	 * 
	 * @param element
	 *            The model element. Should not be <code>null</code>.
	 * @return Name of the element if it is an instance of NamedElement.
	 *         Otherwise, an empty string is returned.
	 */
	public static String getElementName(Object element) {
		String name = new String(""); //$NON-NLS-1$
		if (element != null && element instanceof NamedElement) {
			name = ((NamedElement) element).getName();
		}
		return name;
	}
	
    
    /**
     * Creates a "getter" UML operation for a UML property.
     * 
     * @param srcProperty The UML property
     * @return The "getter" operation
     */
    public static Operation createGetter(Property srcProperty) {
    	Operation getter = UMLFactory.eINSTANCE.createOperation();
        getter.setName("get_" + srcProperty.getName()); //$NON-NLS-1$

        getter.createReturnResult(
            srcProperty.getName(),
            srcProperty.getType());

    	return getter;
    }
    
    /**
     * Creates a "setter" UML operation for a UML property.
     * 
     * @param srcProperty The UML property
     * @return The "setter" operation
     */
    public static Operation createSetter(Property srcProperty) {
    	Operation setter = UMLFactory.eINSTANCE.createOperation();
        setter.setName("set_" + srcProperty.getName()); //$NON-NLS-1$
        
        Parameter setParm =
            setter.createOwnedParameter(
                srcProperty.getName(),
                srcProperty.getType());
        setParm.setDirection(ParameterDirectionKind.IN_LITERAL);
        
        return setter;
    }
    
    /**
     * Creates new UML operation which is a shallow copy of the specified
     * source operation. It copies the name and the visibility of the
     * source operation as well as the parameters. For the parameters,
     * it copies the name, type and direction of the parameters of the
     * source operation.
     * 
     * @param srcOp Source operation
     * @return the copy of the operation
     */
    public static Operation createCopyOfOperation(Operation srcOp) {
    	Operation targetOp = UMLFactory.eINSTANCE.createOperation();
    	targetOp.setName(srcOp.getName());
        targetOp.setVisibility(srcOp.getVisibility());
        
        // EList srcParameters = srcOp.getOwnedParameters();
        EList srcParameters = srcOp.getOwnedMembers();
        for (Iterator piter = srcParameters.iterator(); piter.hasNext();) {
        	Object element = piter.next();
        	if (element instanceof Parameter) {
        		Parameter srcParm = (Parameter) element;
        		Parameter targetParm =
                    targetOp.createOwnedParameter(
                        srcParm.getName(),
                        srcParm.getType());
        		targetParm.setDirection(srcParm.getDirection());
        	}
        }
        
        return targetOp;
    }

}