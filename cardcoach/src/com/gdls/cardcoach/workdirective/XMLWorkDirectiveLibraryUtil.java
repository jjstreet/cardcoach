package com.gdls.cardcoach.workdirective;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.gdls.cardcoach.employee.Employee;
import com.gdls.cardcoach.xml.LoadXMLException;
import com.gdls.cardcoach.xml.SaveXMLException;
import com.gdls.cardcoach.xml.schema.XSDResources;

public class XMLWorkDirectiveLibraryUtil {

	private XMLWorkDirectiveLibraryUtil () {
		
	}
	
	public static WorkDirectiveLibrary loadFromXML(String location) throws LoadXMLException {
		File f = new File(location);
		HashMap<String, WorkDirective> workDirectives = new HashMap<String, WorkDirective>();
		try {
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			StreamSource accountXSD = new StreamSource(XSDResources.getSchema("account.xsd"), "account.xsd");
			StreamSource workDirectivesXSD = new StreamSource(XSDResources.getSchema("workdirectives.xsd"), "workdirectives.xsd");
			Schema workDirectiveListSchema = schemaFactory.newSchema(new StreamSource[] {accountXSD, workDirectivesXSD});
			
			Validator validator = workDirectiveListSchema.newValidator();
			validator.validate(new StreamSource(f));
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(f);
			doc.normalizeDocument();
			
			NodeList wdNodeList = doc.getElementsByTagName("wd:workdirective");
			NodeList accNodeList;
			int s;
			int wLen = wdNodeList.getLength();
			int a;
			Employee.Type type;
			for (s = 0; s < wLen; s++) {
				WorkDirective wd = new WorkDirective();
				Element wdElem = (Element) wdNodeList.item(s);
				wd.setNumber(wdElem.getElementsByTagName("wd:number").item(0).getTextContent());
				accNodeList = wdElem.getElementsByTagName("acc:account");
				int aLen = accNodeList.getLength();
				for (a = 0; a < aLen; a++) {
					Element accElem = (Element) accNodeList.item(a);
					type = Employee.Type.valueOf(accElem.getAttribute("employee"));
					wd.addAccount(new Account(
							accElem.getElementsByTagName("acc:number").item(0).getTextContent(),
							accElem.getElementsByTagName("acc:numbernextweek").item(0).getTextContent(),
							type));
				}
				wd.setDescription(wdElem.getElementsByTagName("wd:description").item(0).getTextContent());
				if (wdElem.getElementsByTagName("wd:suspensenumber").getLength() > 0) {
					wd.setSuspenseNumber(wdElem.getElementsByTagName("wd:suspensenumber").item(0).getTextContent());
				}
				workDirectives.put(wd.getNumber(), wd);
			}
			WorkDirectiveLibrary wdl = new WorkDirectiveLibrary();
			wdl.setWorkDirectives(workDirectives);
			wdl.setDirtyState(false);
			wdl.setReadOnlyState(!f.canWrite());
			return wdl;
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new LoadXMLException("Parser not configured: " + e.getMessage());
		}
		catch (SAXParseException e) {
			e.printStackTrace();
			throw new LoadXMLException("Problem parsing XML starting at: " + e.getMessage());
		}
		catch (SAXException e) {
			e.printStackTrace();
			throw new LoadXMLException("General parsing exception: " + e.getMessage());
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new LoadXMLException("Problem reading work directive list file: " + e.getMessage());
		}
		catch (NullPointerException e) {
			throw new LoadXMLException("Work directive list can not be loaded, no file is specified");
		}
	}
	
	public static void saveToXML(WorkDirectiveLibrary workDirectiveLibrary, String location) throws SaveXMLException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			Element wdListElem = doc.createElementNS("workdirectives.xsd", "wd:workdirectives");
			doc.appendChild(wdListElem);
			
			for (WorkDirective wd : workDirectiveLibrary.getWorkDirectives()) {
				Element wdElem = doc.createElement("wd:workdirective");
				wdListElem.appendChild(wdElem);
				Element numberElem = doc.createElement("wd:number");
				numberElem.setTextContent(wd.getNumber());
				wdElem.appendChild(numberElem);
				Element accListElem = doc.createElement("wd:accounts");
				wdElem.appendChild(accListElem);
				for (Account account : wd.getAccounts()) {
					Element accElem = doc.createElementNS("account.xsd", "acc:account");
					accElem.setAttribute("employee", account.getEmployeeType().toString());
					accListElem.appendChild(accElem);
					Element accNumElem = doc.createElement("acc:number");
					accNumElem.setTextContent(account.getNumber());
					accElem.appendChild(accNumElem);
					Element accNumNWElem = doc.createElement("acc:numbernextweek");
					accNumNWElem.setTextContent(account.getNumberNextWeek());
					accElem.appendChild(accNumNWElem);
				}
				Element descElem = doc.createElement("wd:description");
				descElem.setTextContent(wd.getDescription());
				wdElem.appendChild(descElem);
				if (wd.isSuspendable()) {
					Element suspElem = doc.createElement("wd:suspensenumber");
					suspElem.setTextContent(wd.getSuspenseNumber());
					wdElem.appendChild(suspElem);
				}
			}
			// Text transformation and eventual output
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			tf.setAttribute("indent-number", new Integer(4));
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
			
			BufferedWriter out = new BufferedWriter(new FileWriter(location));
			out.write(sw.toString());
			out.close();
			workDirectiveLibrary.setDirtyState(false);
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new SaveXMLException("Could not configure parser: " + e.getMessage());
		}
		catch (TransformerConfigurationException e) {
			e.printStackTrace();
			throw new SaveXMLException("Transformer not configured: " + e.getMessage());
		}
		catch (TransformerException e) {
			e.printStackTrace();
			throw new SaveXMLException("Error transforming to save document: " + e.getMessage());
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new SaveXMLException("Problem saving work directive list file: " + e.getMessage());
		}
	}
}
