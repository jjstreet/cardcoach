package com.gdls.cardcoach.timecard;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gdls.cardcoach.timesheet.TimeSheetEntry;
import com.gdls.cardcoach.util.DateTimeUtil;
import com.gdls.cardcoach.workdirective.Account;
import com.gdls.cardcoach.workdirective.WorkDirective;

public class TimeCardEntry {
	
	private String workDirective;
	private String account;
	private String crossCharge;
	private String taskCode;
	private long interval;
	private BigDecimal hours;
	private ArrayList<String> notes;
	private boolean isNextWeek;

	private TimeCardEntry() {
		notes = new ArrayList<String>();
	}
	
	public static final TimeCardEntry newFromTimeSheetEntry(final TimeSheetEntry timeSheetEntry, long remainingTime, boolean isNextWeek) {
		if (timeSheetEntry.isStarted() && timeSheetEntry.isFinished()) {
			final TimeCardEntry entry = new TimeCardEntry();
			final WorkDirective wd = timeSheetEntry.getWorkDirective();
			final Account a = wd.getSelectedAccount();
			// If entry is in suspense, then use suspense format for time card
			if (!timeSheetEntry.isSuspended()) {
				entry.workDirective = wd.getNumber();
				entry.taskCode = timeSheetEntry.getTaskCode();
			}
			else {
				entry.workDirective = wd.getSuspenseNumber();
				entry.taskCode = wd.getNumber();
			}
			// Account is set according to if this time card entry is for next
			// week or not.
			entry.account = !isNextWeek ? a.getNumber() : a.getNumberNextWeek();
			entry.crossCharge = timeSheetEntry.getCrossCharge();
			if (timeSheetEntry.getNote() != null && !timeSheetEntry.getNote().isEmpty()) {
				entry.notes.add(timeSheetEntry.getNote());
			}
			entry.isNextWeek = isNextWeek;
			entry.interval = Math.min(timeSheetEntry.getIntervalSeconds(), remainingTime);
			return entry;
		}
		return null;
	}
	
	public String getWorkDirective() {
		return workDirective;
	}
	
	public String getAccount() {
		return account;
	}
	
	public String getCrossCharge() {
		return crossCharge;
	}
	
	public String getTaskCode() {
		return taskCode;
	}
	
	public List<String> getNotes() {
		return Collections.unmodifiableList(notes);
	}
	
	public boolean isNextWeek() {
		return isNextWeek;
	}
	
	public long getRoundingRemainder() {
		return interval % 360L;
	}
	
	public Long getInterval() {
		return interval;
	}
	
	public BigDecimal getHours() {
		return hours;
	}
	
	void combineNotes(List<String> notes) {
		for (String note : notes) {
			if (!note.isEmpty()) {
				this.notes.add(note);
			}
		}
	}
	
	void setInterval(long interval) {
		this.interval = interval;
	}
	
	void setHours(BigDecimal hours) {
		this.hours = hours;
	}
	
	void roundDownHours() {
		hours = hours.subtract(DateTimeUtil.tenthHour);
	}
	
	void roundUpHours() {
		hours = hours.add(DateTimeUtil.tenthHour);
	}

}