package com.gdls.cardcoach.timesheet;

public class UpdateTimeSheetEntryException extends TimeSheetException {

	private static final long serialVersionUID = 1L;
	
	private TimeSheetEntry timeSheetEntry;
	
	public UpdateTimeSheetEntryException(TimeSheet timeSheet, TimeSheetEntry entry) {
		this (timeSheet, entry, "Error updating entry in time sheet.");
	}
	
	public UpdateTimeSheetEntryException(TimeSheet timeSheet, TimeSheetEntry entry, String message) {
		super(timeSheet, message);
		this.timeSheetEntry = entry;
	}
	
	
	public TimeSheetEntry getTimeSheetEntry() {
		return timeSheetEntry;
	}
}
