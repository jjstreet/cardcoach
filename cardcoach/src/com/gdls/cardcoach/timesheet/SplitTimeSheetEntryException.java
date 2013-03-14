package com.gdls.cardcoach.timesheet;

public class SplitTimeSheetEntryException extends TimeSheetException {

	private static final long serialVersionUID = 1L;
	
	private TimeSheetEntry timeSheetEntry;
	
	public SplitTimeSheetEntryException(TimeSheet timeSheet, TimeSheetEntry entry) {
		this (timeSheet, entry, "Error splitting entry in time sheet.");
	}
	
	public SplitTimeSheetEntryException(TimeSheet timeSheet, TimeSheetEntry entry, String message) {
		super(timeSheet, message);
		this.timeSheetEntry = entry;
	}
	
	
	public TimeSheetEntry getTimeSheetEntry() {
		return timeSheetEntry;
	}
}
