package com.gdls.cardcoach.timesheet;

public class TimeSheetException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private TimeSheet timeSheet;
	
	public TimeSheetException(TimeSheet timeSheet) {
		this (timeSheet, "Error performing action with time sheet.");
	}
	
	public TimeSheetException(TimeSheet timeSheet, String message) {
		super(message);
		this.timeSheet = timeSheet;
	}
	
	public TimeSheet getTimeSheet() {
		return timeSheet;
	}
}
