package com.gdls.cardcoach.timesheet;

public class InsertTimeSheetEntryException extends TimeSheetException {

	private static final long serialVersionUID = 1L;
	
	private TimeSheetEntry timeSheetEntry;
	
	public InsertTimeSheetEntryException(TimeSheet timeSheet, TimeSheetEntry entry) {
		this (timeSheet, entry, "Error inserting entry into time sheet.");
	}
	
	public InsertTimeSheetEntryException(TimeSheet timeSheet, TimeSheetEntry entry, String message) {
		super(timeSheet, message);
		this.timeSheetEntry = entry;
	}
	
	
	public TimeSheetEntry getTimeSheetEntry() {
		return timeSheetEntry;
	}
}
