package com.gdls.cardcoach;

import hirondelle.date4j.DateTime;

import com.gdls.cardcoach.employee.Employee;
import com.gdls.cardcoach.timecard.TimeCard;
import com.gdls.cardcoach.timecard.TimeCardEntry;
import com.gdls.cardcoach.timesheet.TimeSheet;
import com.gdls.cardcoach.timesheet.TimeSheetEntry;
import com.gdls.cardcoach.workdirective.WorkDirective;
import com.gdls.cardcoach.workdirective.WorkDirectiveLibrary;
import com.gdls.cardcoach.workdirective.XMLWorkDirectiveLibraryUtil;

public class CardCoachTest {
	
	public CardCoachTest() {
		try {
			Employee josh = new Employee(
					"Joshua",
					"Street",
					"308493",
					"streetj",
					Employee.Type.CONTRACTOR);
		TimeSheet timeSheet = TimeSheet.newInstance(josh);
		
		WorkDirectiveLibrary wdl = XMLWorkDirectiveLibraryUtil.loadFromXML("lib/workdirectives.xml");
		WorkDirective m1014a1l99 = wdl.getWorkDirective("M1014A1L99");
		m1014a1l99.setSelectedAccount(m1014a1l99.getAccounts(josh.getType()).get(0), true);
		WorkDirective m1014a2l99 = wdl.getWorkDirective("M1014A2L99");
		m1014a2l99.setSelectedAccount(m1014a2l99.getAccounts(josh.getType()).get(0), true);
		WorkDirective b8ssconfig = wdl.getWorkDirective("B8SSCONFIG");
		b8ssconfig.setSelectedAccount(b8ssconfig.getAccounts(josh.getType()).get(0), true);
		
		timeSheet.addTimeSheetEntry(new TimeSheetEntry(b8ssconfig, null, "Z721AA", null, false, new DateTime("2010-12-14 08:30:00")));
		timeSheet.addTimeSheetEntry(new TimeSheetEntry(m1014a1l99, null, "Z721AA", null, false, new DateTime("2010-12-14 09:45:00")));
		timeSheet.addTimeSheetEntry(new TimeSheetEntry(m1014a2l99, null, "Z721AA", null, false, new DateTime("2010-12-14 16:45:00")));
		timeSheet.addTimeSheetEntry(new TimeSheetEntry(b8ssconfig, null, "Z721AA", null, false, new DateTime("2010-12-14 17:30:00"), new DateTime("2010-12-14 18:15:00")));
		timeSheet.getTimeSheetEntries().get(3).setEndTime(new DateTime("2010-12-14 19:15:00")); 
		
		for (TimeSheetEntry entry : timeSheet.getTimeSheetEntries()) {
			System.out.println(String.format("Start: %s, End: %s", entry.getFormattedStartTime(), entry.getFormattedEndTime()));
		}
		
		TimeCard timeCard = TimeCard.newFromTimeSheet(timeSheet);
		System.out.println(String.format("Employee: %s, (%s)", josh.getFullName(), josh.getBadgeNumber()));
		System.out.println("");
		System.out.println(String.format("Start Time: %s", timeCard.getFormattedStartTime()));
		System.out.println(String.format("End Time: %s", timeCard.getFormattedEndTime()));
		System.out.println("");
		System.out.println(String.format("Total Hours: %s", timeCard.getTotalHours().toString()));
		System.out.println("");
		System.out.println(String.format("Time Card Hours:"));
		for (TimeCardEntry entry : timeCard.getTimeCardEntries()) {
			System.out.println(String.format("%s %s %s %s %s (%s)", entry.getWorkDirective(), entry.getAccount(), entry.getCrossCharge(), entry.getTaskCode(), entry.getHours(), entry.getInterval()));
		}
		} catch (Exception e) {
			
		}
	}
	
	public static void main(String[] args) {
		new CardCoachTest();
	}
}


