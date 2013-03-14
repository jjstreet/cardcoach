package com.gdls.cardcoach.timesheet;

public class TimeSheetEntryException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private TimeSheetEntry timeSheetEntry;
	
	public TimeSheetEntryException(TimeSheetEntry entry) {
		timeSheetEntry = entry;
	}
	
	public TimeSheetEntryException(TimeSheetEntry entry, String message) {
		super(message);
		timeSheetEntry = entry;
	}
	
	public TimeSheetEntry getTimeSheetEntry() {
		return timeSheetEntry;
	}

}
