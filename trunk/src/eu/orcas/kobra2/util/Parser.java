package eu.orcas.kobra2.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.omg.CORBA._PolicyStub;

public class Parser {
	
	public Parser(String filePath) throws DocumentException{
		SAXReader reader = new SAXReader();

		_filePath = filePath;
		_modelDocument = reader.read(new File(_filePath));
//		_profileDocument = DocumentHelper.createDocument();
		
		
	}
	
	public Document getDocument() throws DocumentException{
		return _modelDocument;
	}
	
	public List<Element> transformModels(){
		List<Element> modelElements = _modelDocument.selectNodes("//Model");
		List<Element> profileElements = new ArrayList<Element>();
		
		for (Iterator<Element> modelIterator =
			modelElements.iterator(); modelIterator.hasNext();){
			
			Element model = modelIterator.next();
			Element profile = _createProfileElement(model);
			
			_transformPackagedElements(model, profile);
			
			
			profileElements.add(profile);
			
		}
		
		
		return profileElements;
	}
	
	
	private void _transformPackagedElements(
		Element model, Element profile) {
		
		List<Element> packagedElements = model.selectNodes("//packagedElement");
		
		for (Iterator<Element> packagedElementsIterator =
			packagedElements.iterator(); packagedElementsIterator.hasNext();){
			
			Element packgedElement = packagedElementsIterator.next();
			
			QName typeQName = new QName(StringPool.TYPE, Constants.XMI_NAMESPACE);
			String typeValue = packgedElement.attributeValue(typeQName);
			
			if (typeValue.equals(StringPool.UML_CLASS)){
				profile.add(_createStereotypeElement(packgedElement));
			}
			
		}
		
	}

	private Element _createProfileElement(Element model){
		QName profileQName = new QName(StringPool.PROFILE, Constants.UML_NAMESPACE);
		
		Element profile = DocumentHelper.createElement(profileQName);
		
		// version
		profile.addAttribute(
			new QName(StringPool.VERSION, Constants.XMI_NAMESPACE), "2.1"); 
		// schemaLocation
		profile.addAttribute(
			new QName(StringPool.SCHEMA_LOCATION, Constants.XSI_NAMESPACE),
			"http://schema.omg.org/spec/UML/2.1.1 http://www.eclipse.org/uml2/2.1.0/UML");
		// name
		profile.addAttribute(StringPool.NAME, StringPool.PROFILE_NAME);
		
		
		return profile;
	}
	
	private Element _createStereotypeElement(Element packgedElement){
		Element packagedElement =  DocumentHelper.createElement(StringPool.PACKAGED_ELEMENT);
		packagedElement.addAttribute(Constants.TYPE_QNAME, StringPool.UML_STEREOTYPE);
		packagedElement.addAttribute(StringPool.NAME, packgedElement.attributeValue(StringPool.NAME));
		
		return packagedElement;
	}
	
	
	
	
	private String _filePath;
	private Document _modelDocument;
	
//	private Document _profileDocument;
}
