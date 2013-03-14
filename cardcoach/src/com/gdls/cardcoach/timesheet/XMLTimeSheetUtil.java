package com.gdls.cardcoach.timesheet;

import hirondelle.date4j.DateTime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

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

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.gdls.cardcoach.employee.Employee;
import com.gdls.cardcoach.employee.EmployeeInstantiationException;
import com.gdls.cardcoach.workdirective.Account;
import com.gdls.cardcoach.workdirective.WorkDirective;
import com.gdls.cardcoach.xml.LoadXMLException;
import com.gdls.cardcoach.xml.SaveXMLException;
import com.gdls.cardcoach.xml.schema.XSDResources;

public class XMLTimeSheetUtil {
	
	private static final String DATE_FORMAT = "YYYY-MM-DD|T|hh:mm:ss";

	private XMLTimeSheetUtil() {
		
	}
	
	public static TimeSheet loadFromXML(String location) throws LoadXMLException {
		File f = new File(location);
		try {
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			StreamSource accountXSD = new StreamSource(XSDResources.getSchema("account.xsd"), "account.xsd");
			StreamSource workDirectivesXSD = new StreamSource(XSDResources.getSchema("workdirectives.xsd"), "workdirectives.xsd");
			StreamSource employeeXSD = new StreamSource(XSDResources.getSchema("employee.xsd"), "employee.xsd");
			StreamSource timeSheetXSD = new StreamSource(XSDResources.getSchema("timesheet.xsd"), "timesheet.xsd");
			Schema timeSheetSchema = schemaFactory.newSchema(new StreamSource[] {employeeXSD, accountXSD, workDirectivesXSD, timeSheetXSD});
			Validator validator = timeSheetSchema.newValidator();
			validator.validate(new StreamSource(f));
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(f);
			doc.normalizeDocument();
			
			final TimeSheet ts = new TimeSheet();
			Element empElem = (Element) doc.getElementsByTagName("emp:employee").item(0);
			String firstName = empElem.getElementsByTagName("emp:firstname").item(0).getTextContent();
			String lastName = empElem.getElementsByTagName("emp:lastname").item(0).getTextContent();
			String badgeNumber = empElem.getElementsByTagName("emp:badgenumber").item(0).getTextContent();
			String username = empElem.getElementsByTagName("emp:username").item(0).getTextContent();
			Employee.Type type = Employee.Type.valueOf(empElem.getElementsByTagName("emp:type").item(0).getTextContent());
			Employee emp = new Employee(firstName, lastName, badgeNumber, username, type);
			ts.setEmployee(emp);
			DateTime sheetDate = new DateTime(doc.getElementsByTagName("ts:date").item(0).getTextContent().replace('T', ' '));
			ts.setSheetDate(sheetDate);
			Element entriesElem = (Element) doc.getElementsByTagName("ts:entries").item(0);
			if (entriesElem.getElementsByTagName("ts:splittime").getLength() > 0) {
				ts.setIsSplitting(true);
				ts.setSplitTime(entriesElem.getElementsByTagName("ts:splittime").item(0).getTextContent());
			}
			NodeList entryNodeList = entriesElem.getElementsByTagName("ts:entry");
			int e;
			int eLen = entryNodeList.getLength();
			final ArrayList<TimeSheetEntry> entries = new ArrayList<TimeSheetEntry>();
			for (e = 0; e < eLen; e++) {
				Element entryElem = (Element) entryNodeList.item(e);
				Element wdElem = (Element) entryElem.getElementsByTagName("wd:workdirective").item(0);
				Element accElem = (Element) wdElem.getElementsByTagName("acc:account").item(0);
				ArrayList<Account> accounts = new ArrayList<Account>();
				accounts.add(new Account(
						accElem.getElementsByTagName("acc:number").item(0).getTextContent(),
						accElem.getElementsByTagName("acc:numbernextweek").item(0).getTextContent(),
						type));
				WorkDirective wd = new WorkDirective(
						wdElem.getElementsByTagName("wd:number").item(0).getTextContent(),
						accounts,
						wdElem.getElementsByTagName("wd:description").item(0).getTextContent(),
						wdElem.getElementsByTagName("wd:suspensenumber").getLength() > 0 ? wdElem.getElementsByTagName("wd:suspensenumber").item(0).getTextContent() : null);
				wd.setSelectedAccount(accounts.get(0), true);
				DateTime startTime = null;
				DateTime endTime = null;
				if (entryElem.getElementsByTagName("ts:starttime").getLength() > 0) {
					startTime = new DateTime(entryElem.getElementsByTagName("ts:starttime").item(0).getTextContent().replace('T', ' '));
				}
				if (entryElem.getElementsByTagName("ts:endtime").getLength() > 0) {
					endTime = new DateTime(entryElem.getElementsByTagName("ts:endtime").item(0).getTextContent().replace('T', ' '));
				}
				entries.add(new TimeSheetEntry(
						wd,
						entryElem.getElementsByTagName("ts:crosscharge").getLength() > 0 ? 
								entryElem.getElementsByTagName("ts:crosscharge").item(0).getTextContent() : null,
						entryElem.getElementsByTagName("ts:taskcode").getLength() > 0 ?
								entryElem.getElementsByTagName("ts:taskcode").item(0).getTextContent() : null,
						entryElem.getElementsByTagName("ts:note").getLength() > 0 ?
								entryElem.getElementsByTagName("ts:note").item(0).getTextContent() : null,
						!entryElem.getAttribute("suspended").isEmpty(),
						startTime,
						endTime));
			}
			ts.setTimeSheetEntries(entries);
			ts.setDirtyState(false);
			return ts;
		} catch (EmployeeInstantiationException e) {
			throw new LoadXMLException("Could not determine employee type in time sheet file.");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new LoadXMLException("Parser not configured: " + e.getMessage());
		} catch (SAXParseException e) {
			e.printStackTrace();
			throw new LoadXMLException("Problem parsing XML starting at: " + e.getMessage());
		} catch (SAXException e) {
			e.printStackTrace();
			throw new LoadXMLException("General parsing exception: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new LoadXMLException("Problem reading timesheet file: " + e.getMessage());
		}
	}
	
	public static void saveToXML(TimeSheet timeSheet, String location) throws SaveXMLException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Comment comment = doc.createComment("For use with Card Coach only. Not valid outside the Card Coach application.");
			doc.appendChild(comment);
			Element tsElem = doc.createElementNS("timesheet.xsd", "ts:timesheet");
			doc.appendChild(tsElem);
			// Employee
			Element empElem = doc.createElementNS("employee.xsd", "emp:employee");
			tsElem.appendChild(empElem);
			Element firstNameElem = doc.createElement("emp:firstname");
			firstNameElem.setTextContent(timeSheet.getEmployee().getFirstName());
			empElem.appendChild(firstNameElem);
			Element lastNameElem = doc.createElement("emp:lastname");
			lastNameElem.setTextContent(timeSheet.getEmployee().getLastName());
			empElem.appendChild(lastNameElem);
			Element bnElem = doc.createElement("emp:badgenumber");
			bnElem.setTextContent(timeSheet.getEmployee().getBadgeNumber());
			empElem.appendChild(bnElem);
			Element usernameElem = doc.createElement("emp:username");
			usernameElem.setTextContent(timeSheet.getEmployee().getUsername());
			empElem.appendChild(usernameElem);
			Element typeElem = doc.createElement("emp:type");
			typeElem.setTextContent(timeSheet.getEmployee().getType().toString());
			empElem.appendChild(typeElem);
			// Date
			Element dateElem = doc.createElement("ts:date");
			dateElem.setTextContent(timeSheet.getFormattedSheetDate());
			tsElem.appendChild(dateElem);
			Element entriesElem = doc.createElement("ts:entries");
			tsElem.appendChild(entriesElem);
			if (timeSheet.isSplitting()) {
				Element splitElement = doc.createElement("ts:splittime");
				splitElement.setTextContent(timeSheet.getSplitTimeHours().toString());
				entriesElem.appendChild(splitElement);
			}
			for (TimeSheetEntry entry : timeSheet.getTimeSheetEntries()) {
				final WorkDirective wd = entry.getWorkDirective();
				Element entryElem = doc.createElement("ts:entry");
				entriesElem.appendChild(entryElem);
				Element wdElem = doc.createElementNS("workdirectives.xsd", "wd:workdirective");
				entryElem.appendChild(wdElem);
				Element numberElem = doc.createElement("wd:number");
				numberElem.setTextContent(wd.getNumber());
				wdElem.appendChild(numberElem);
				Element accListElem = doc.createElement("wd:accounts");
				wdElem.appendChild(accListElem);
				final Account account = wd.getSelectedAccount();
				Element accElem = doc.createElementNS("account.xsd", "acc:account");
				accElem.setAttribute("employee", account.getEmployeeType().toString());
				accElem.setAttribute("selected", "selected");
				accListElem.appendChild(accElem);
				Element accNumElem = doc.createElement("acc:number");
				accNumElem.setTextContent(account.getNumber());
				accElem.appendChild(accNumElem);
				Element accNumNWElem = doc.createElement("acc:numbernextweek");
				accNumNWElem.setTextContent(account.getNumberNextWeek());
				accElem.appendChild(accNumNWElem);
				Element descElem = doc.createElement("wd:description");
				descElem.setTextContent(wd.getDescription());
				wdElem.appendChild(descElem);
				if (wd.isSuspendable()) {
					Element suspElem = doc.createElement("wd:suspensenumber");
					suspElem.setTextContent(wd.getSuspenseNumber());
					wdElem.appendChild(suspElem);
				}
				Element crossChargeElem = doc.createElement("ts:crosscharge");
				crossChargeElem.setTextContent(entry.getCrossCharge());
				entryElem.appendChild(crossChargeElem);
				Element taskCodeElem = doc.createElement("ts:taskcode");
				taskCodeElem.setTextContent(entry.getTaskCode());
				entryElem.appendChild(taskCodeElem);
				Element noteElem = doc.createElement("ts:note");
				noteElem.setTextContent(entry.getNote());
				entryElem.appendChild(noteElem);
				Element startElem = doc.createElement("ts:starttime");
				startElem.setTextContent(entry.getFormattedStartTime(DATE_FORMAT));
				entryElem.appendChild(startElem);
				if (entry.isFinished()) {
					Element endElem = doc.createElement("ts:endtime");
					endElem.setTextContent(entry.getFormattedEndTime(DATE_FORMAT));
					entryElem.appendChild(endElem);
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
			timeSheet.setDirtyState(false);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new SaveXMLException("Could not configure parser: " + e.getMessage());
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			throw new SaveXMLException("Transformer not configured: " + e.getMessage());
		} catch (TransformerException e) {
			e.printStackTrace();
			throw new SaveXMLException("Error transforming to save document: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new SaveXMLException("Problem saving time sheet file: " + e.getMessage());
		}
	}
}
