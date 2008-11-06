package eu.orcas.kobra2.util;

import org.dom4j.Namespace;
import org.dom4j.QName;

public class Constants {
	
	public static final Namespace UML_NAMESPACE =
		new Namespace(StringPool.UML,"http://schema.omg.org/spec/UML/2.1.1");
	public static final Namespace XMI_NAMESPACE =  
		new Namespace(StringPool.XMI, "http://schema.omg.org/spec/XMI/2.1");
	public static final Namespace XSI_NAMESPACE = 
		new Namespace(StringPool.XSI, "http://www.w3.org/2001/XMLSchema-instance");
	public static final Namespace ECORE_NAMESPACE =  
		new Namespace(StringPool.ECORE, "http://www.eclipse.org/emf/2002/Ecore");
	
	public static final QName TYPE_QNAME = new QName(StringPool.TYPE, XMI_NAMESPACE);
}
