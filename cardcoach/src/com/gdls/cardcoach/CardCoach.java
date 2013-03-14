package com.gdls.cardcoach;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Properties;

import com.gdls.cardcoach.employee.Employee;
import com.gdls.cardcoach.timesheet.TimeSheet;
import com.gdls.cardcoach.ui.CardCoachGUI;
import com.gdls.cardcoach.workdirective.WorkDirectiveLibrary;

public class CardCoach implements CardCoachContext {
	
	private static final String APPLICATION_TITLE = "Card Coach";
	
	private String version;
	
	private TimeSheet timeSheet;
	
	private WorkDirectiveLibrary workDirectiveLibrary;
	
	private Employee employee;
	
	
	private CardCoach() {
		try {
			Properties props = new Properties();
			props.load(CardCoach.class.getResourceAsStream("CardCoach.properties"));
			version = props.getProperty("version");
		}
		catch (Exception e) { /* Should never fail */ }
	}
	
	@Override
	public String getApplicationVersion() {
		return version;
	}
	
	@Override
	public String getApplicationTitle() {
		return APPLICATION_TITLE;
	}
	
	@Override
	public String getApplicationSubtitle(String subtitle) {
		return APPLICATION_TITLE + " " + subtitle;
	}
	
	@Override
	public Employee getEmployee() {
		return employee;
	}
	
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	@Override
	public TimeSheet getTimeSheet() {
		return timeSheet;
	}
	
	@Override
	public void setTimeSheet(TimeSheet timeSheet) {
		this.timeSheet = timeSheet;
	}
	
	@Override
	public WorkDirectiveLibrary getWorkDirectiveLibrary() {
		return workDirectiveLibrary;
	}
	
	public void setWorkDirectiveLibrary(WorkDirectiveLibrary library) {
		this.workDirectiveLibrary = library;
	}
	
	private final void run() {
		new CardCoachGUI(this).runEventLoop();
	}

	public static void main(String[] args) {
		if (args.length > 0 && args[0].equals("nodebug")) {
			CardCoach cardCoach = new CardCoach();
			cardCoach.run();
		} else {
			try {
				CardCoach cardCoach = new CardCoach();
				cardCoach.run();
			}
			catch (Exception e) {
				PrintStream ps;
				try {
					ps = new PrintStream(new File("cardcoach.log"));
					e.printStackTrace(ps);
					ps.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
