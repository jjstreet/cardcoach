package com.gdls.cardcoach.timesheet;

import java.util.Locale;
import java.util.TimeZone;

import com.gdls.cardcoach.util.DateTimeUtil;
import com.gdls.cardcoach.workdirective.WorkDirective;

import hirondelle.date4j.DateTime;

public class TimeSheetEntry {

	private WorkDirective workDirective;
	private String crossCharge;
	private String taskCode;
	private String note;
	private boolean suspended;
	private DateTime startTime;
	private DateTime endTime;
	
	public TimeSheetEntry(WorkDirective workDirective, String crossCharge, String taskCode, String note) {
		this (workDirective, crossCharge, taskCode, note, false);
	}
	
	public TimeSheetEntry(WorkDirective workDirective, String crossCharge, String taskCode, String note, boolean suspended) {
		this (workDirective, crossCharge, taskCode, note, suspended, DateTimeUtil.truncate(DateTime.now(TimeZone.getDefault())), null);
	}
	
	public TimeSheetEntry(WorkDirective workDirective, String crossCharge, String taskCode, String note, boolean suspended, DateTime startTime) {
		this (workDirective, crossCharge, taskCode, note, suspended, startTime, null);
	}
	
	public TimeSheetEntry(WorkDirective workDirective, String crossCharge, String taskCode, String note, boolean suspended, DateTime startTime, DateTime endTime) {
		this.workDirective = workDirective;
		this.crossCharge = "";
		this.taskCode = "";
		this.note = "";
		this.suspended = suspended;
		if (crossCharge != null) {
			this.crossCharge = crossCharge;
		}
		if (taskCode != null) {
			this.taskCode = taskCode;
		}
		if (note != null) {
			this.note = note;
		}
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public WorkDirective getWorkDirective() {
		return workDirective;
	}
	
	void setWorkDirective(WorkDirective wd) {
		workDirective = wd;
	}
	
	void setTaskCode(String taskCode) {
		if (taskCode != null) {
			this.taskCode = taskCode.toUpperCase();
		} else {
			this.taskCode = "";
		}
	}
	public String getTaskCode() {
		return taskCode;
	}
	
	void setCrossCharge(String crossCharge) {
		if (this.crossCharge != null) {
			this.crossCharge = crossCharge.toUpperCase();
		} else {
			this.crossCharge = "";
		}
	}
	
	public String getCrossCharge() {
		return crossCharge;
	}
	
	void setNote(String note) {
		if (this.note != null) {
			this.note = note;
		} else {
			this.note = "";
		}
	}
	
	public String getNote() {
		return note;
	}
	
	void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}
	
	public boolean isSuspended() {
		return this.suspended;
	}
	
	public DateTime getStartTime() {
		return startTime;
	}
	
	public String getFormattedStartTime() {
		return getFormattedStartTime("h12:mm:ss a");
	}
	
	public String getFormattedStartTime(String format) {
		return getStartTime() != null ? getStartTime().format(format, Locale.getDefault()) : "";
	}
	
	public void setStartTime() {
		setStartTime(DateTime.now(TimeZone.getDefault()));
	}
	
	public void setStartTime(DateTime time) {
		startTime = DateTimeUtil.truncate(time);
	}
	
	public DateTime getEndTime() {
		return endTime;
	}
	
	public String getFormattedEndTime() {
		return getFormattedEndTime("h12:mm:ss a");
	}
	
	public String getFormattedEndTime(String format) {
		return getEndTime() != null ? getEndTime().format(format, Locale.getDefault()) : "";
	}
	
	public void setEndTime() {
		setEndTime(DateTime.now(TimeZone.getDefault()));
	}
	
	public void setEndTime(DateTime time) {
		endTime = DateTimeUtil.truncate(time);
	}
	
	public boolean isStarted() {
		return startTime != null;
	}
	
	public boolean isFinished() {
		return endTime != null;
	}
	
	public long getIntervalSeconds() {
		return getIntervalSeconds(false);
	}
	
	public long getIntervalSeconds(boolean upToCurrentTime) {
		if (isFinished()) {
			return startTime.numSecondsFrom(endTime);
		}
		else if (upToCurrentTime) {
			return startTime.numSecondsFrom(DateTime.now(TimeZone.getDefault()));
		}
		return 0L;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!other.getClass().equals(TimeSheetEntry.class)) {
			return false;
		}
		final TimeSheetEntry tse = (TimeSheetEntry) other;
		if (!tse.workDirective.equals(workDirective)) {
			return false;
		}
		if (!tse.crossCharge.equalsIgnoreCase(crossCharge)) {
			return false;
		}
		if (!tse.taskCode.equalsIgnoreCase(crossCharge)) {
			return false;
		}
		if (!tse.note.equalsIgnoreCase(note)) {
			return false;
		}
		if (tse.suspended != suspended) {
			return false;
		}
		if (tse.isStarted() != isStarted()) {
			return false;
		}
		if (!tse.startTime.equals(startTime)) {
			
		}
		if (tse.isFinished() != isFinished()) {
			return false;
		}
		if (!tse.endTime.equals(endTime)) {
			
		}
		return true;
	}
}
