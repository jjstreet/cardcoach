package com.gdls.cardcoach.timesheet;

public class AddTimeSheetEntryException extends TimeSheetException {

	private static final long serialVersionUID = 1L;
	
	private TimeSheetEntry timeSheetEntry;
	
	public AddTimeSheetEntryException(TimeSheet timeSheet, TimeSheetEntry entry) {
		this (timeSheet, entry, "Error adding entry to time sheet.");
	}
	
	public AddTimeSheetEntryException(TimeSheet timeSheet, TimeSheetEntry entry, String message) {
		super(timeSheet, message);
		this.timeSheetEntry = entry;
	}
	
	
	public TimeSheetEntry getTimeSheetEntry() {
		return timeSheetEntry;
	}
}
