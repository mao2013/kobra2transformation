package eu.orcas.kobra2;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlException;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.uml2.x100.uml.Model;
import org.eclipse.uml2.x100.uml.PackageImport;

import eu.orcas.kobra2.util.Parser;

public class ProfileGenerator {

	public static void main(String[] args) throws DocumentException, XmlException {
		
		String metaModelPath = "xmi/Kobra2_Metamodel.xmi";
		
		Parser parser = new Parser(metaModelPath);
		List<Element> profileElements = parser.transformModels();
		
		Document document = DocumentHelper.createDocument();
		document.add(profileElements.get(0));
		
		System.out.println(document.asXML());
		
	}
}
