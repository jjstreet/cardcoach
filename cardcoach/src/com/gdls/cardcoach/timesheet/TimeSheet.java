package com.gdls.cardcoach.timesheet;

import hirondelle.date4j.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import com.gdls.cardcoach.employee.Employee;
import com.gdls.cardcoach.employee.EmployeeInstantiationException;
import com.gdls.cardcoach.util.DateTimeUtil;

public class TimeSheet {

	private Employee employee;
	private DateTime sheetDate;
	private long splitTime;
	private boolean isSplitting;
	private boolean isDirty;
	private ArrayList<TimeSheetEntry> timeSheetEntries;
	
	private Vector<TimeSheetChangeListener> listeners;
	
	TimeSheet() {
		timeSheetEntries = new ArrayList<TimeSheetEntry>();
		listeners = new Vector<TimeSheetChangeListener>();
	}
	
	public TimeSheet(Employee employee, DateTime sheetDate) {
		this.employee = employee;
		this.sheetDate = sheetDate;
		this.splitTime = 0L;
		this.isSplitting = false;
		isDirty = false;
		timeSheetEntries = new ArrayList<TimeSheetEntry>();
		listeners = new Vector<TimeSheetChangeListener>();
	}
	
	public static final TimeSheet newInstance(Employee employee) {
		return new TimeSheet(employee, DateTime.today(TimeZone.getDefault()));
	}
	
	public boolean isDirty() {
		return isDirty;
	}
	
	void setDirtyState(boolean state) {
		isDirty = state;
	}
	
	public Employee getEmployee() {
		try {
			return new Employee(employee.getFirstName(), employee.getLastName(), employee.getBadgeNumber(), employee.getUsername(), employee.getType());
		}
		catch (EmployeeInstantiationException e) {
			// Do nothing
		}
		return null;
	}
	
	public DateTime getSheetDate() {
		return sheetDate;
	}
	
	public String getFormattedSheetDate() {
		return getFormattedSheetDate("YYYY-MM-DD");
	}
	
	public String getFormattedSheetDate(String format) throws IllegalArgumentException {
		if (sheetDate != null) {
			return sheetDate.format(format, Locale.getDefault());
		}
		return "";
	}
	
	public DateTime getStartTime() {
		if (!timeSheetEntries.isEmpty()) {
			return timeSheetEntries.get(0).getStartTime();
		}
		return null;
	}
	
	public String getFormattedStartTime() {
		return getFormattedStartTime("h12:mm:ss a");
	}
	
	public String getFormattedStartTime(String format) throws IllegalArgumentException {
		return getStartTime() != null ? getStartTime().format(format, Locale.getDefault()) : "";
	}
	
	public DateTime getEndTime() {
		if (!timeSheetEntries.isEmpty()) {
			TimeSheetEntry lastEntry = null;
			for (TimeSheetEntry entry : timeSheetEntries) {
				if (entry.isFinished()) {
					lastEntry = entry;
				}
			}
			if (lastEntry != null) {
				return lastEntry.getEndTime();
			}
		}
		return null;
	}
	
	public String getFormattedEndTime() {
		return getFormattedEndTime("h12:mm:ss a");
	}
	
	public String getFormattedEndTime(String format) throws IllegalArgumentException {
		return getEndTime() != null ? getEndTime().format(format, Locale.getDefault()) : "";
	}
	
	public long getGapTimeSeconds() {
		return getGapTimeSeconds(false);
	}
	
	public long getGapTimeSeconds(boolean upToCurrentTime) {
		long gap = 0L;
		if (!timeSheetEntries.isEmpty()) {
			TimeSheetEntry prevEntry = null;
			TimeSheetEntry entry = null;
			Iterator<TimeSheetEntry> iterEntry = timeSheetEntries.iterator();
			while (iterEntry.hasNext()) {
				entry = iterEntry.next();
				if (prevEntry != null) {
					gap += prevEntry.getEndTime().numSecondsFrom(entry.getStartTime());
				}
				prevEntry = entry;
			}
			if (upToCurrentTime && entry.isFinished() && sheetDate.isSameDayAs(DateTime.today(TimeZone.getDefault()))) {
				if (DateTime.now(TimeZone.getDefault()).lt(DateTimeUtil.truncate(getSheetDate().getEndOfDay()))) {
					gap += entry.getEndTime().numSecondsFrom(DateTimeUtil.truncate(DateTime.now(TimeZone.getDefault())));
				} else {
					gap += entry.getEndTime().numSecondsFrom(DateTimeUtil.truncate(getSheetDate().getEndOfDay()));
				}
			}
		}
		return gap;
	}
	
	public BigDecimal getGapTimeHours() {
		return getGapTimeHours(false);
	}
	
	public BigDecimal getGapTimeHours(boolean upToCurrentTime) {
		return DateTimeUtil.roundTimeInterval(getGapTimeSeconds(upToCurrentTime), DateTimeUtil.Unit.TENTH_HOUR);
	}
	
	public long getChargedTimeSeconds() {
		return getChargedTimeSeconds(false);
	}
	
	public long getChargedTimeSeconds(boolean upToCurrentTime) {
		long total = 0L;
		if (!timeSheetEntries.isEmpty()) {
			for (TimeSheetEntry entry : timeSheetEntries) {
				total += entry.getIntervalSeconds(upToCurrentTime);
			}
		}
		return total;
	}
	
	public BigDecimal getChargedTimeHours() {
		return getChargedTimeHours(false);
	}
	
	public BigDecimal getChargedTimeHours(boolean upToCurrentTime) {
		return DateTimeUtil.roundTimeInterval(getChargedTimeSeconds(upToCurrentTime), DateTimeUtil.Unit.TENTH_HOUR);
	}
	
	public void setSplitTime(long seconds) {
		setSplitTime(seconds, true);
	}
	
	public void setSplitTime(long seconds, boolean notify) throws IllegalArgumentException {
		if (seconds < 0) {
			throw new IllegalArgumentException("seconds can not be negative");
		}
		if (splitTime != seconds) {
			splitTime = seconds;
			if (notify) {
				notifyListeners();
			}
		}
	}
	
	public void setSplitTime(BigDecimal hours) throws IllegalArgumentException {
		setSplitTime(hours, true);
	}
	
	public void setSplitTime(BigDecimal hours, boolean notify) throws IllegalArgumentException {
		if (hours == null) {
			throw new IllegalArgumentException("hours must not be null");
		}
		setSplitTime(hours.multiply(new BigDecimal("3600")).longValue(), notify);
	}
	
	public void setSplitTime(String hours) throws NumberFormatException {
		setSplitTime(hours, true);
	}
	
	public void setSplitTime(String hours, boolean notify) throws NumberFormatException {
		if (hours == null || hours.isEmpty()) {
			throw new IllegalArgumentException("hours must be non-empty string");
		}
		setSplitTime(new BigDecimal(hours), true);
	}
	
	public long getSplitTimeSeconds() {
		return splitTime;
	}
	
	public BigDecimal getSplitTimeHours() {
		return DateTimeUtil.roundTimeInterval(splitTime, DateTimeUtil.Unit.TENTH_HOUR);
	}
	
	public boolean isSplitting() {
		return isSplitting;
	}
	
	public void setIsSplitting(boolean splitting) {
		setIsSplitting(splitting, true);
	}
	
	public void setIsSplitting(boolean splitting, boolean notify) {
		if (isSplitting != splitting) {
			isDirty = true;
			isSplitting = splitting;
			splitTime = 0L;
			if (notify) {
				notifyListeners();
			}
		}
	}
	
	public TimeSheetEntry getTimeSheetEntry(int index) throws IndexOutOfBoundsException {
		TimeSheetEntry entry = timeSheetEntries.get(index);
		return new TimeSheetEntry (
				entry.getWorkDirective(),
				entry.getCrossCharge(),
				entry.getTaskCode(),
				entry.getNote(),
				entry.isSuspended(),
				entry.getStartTime(),
				entry.getEndTime());
	}
	
	public List<TimeSheetEntry> getTimeSheetEntries() {
		ArrayList<TimeSheetEntry> entries = new ArrayList<TimeSheetEntry>();
		for (TimeSheetEntry entry : timeSheetEntries) {
			entries.add(new TimeSheetEntry(
					entry.getWorkDirective(),
					entry.getCrossCharge(),
					entry.getTaskCode(),
					entry.getNote(),
					entry.isSuspended(),
					entry.getStartTime(),
					entry.getEndTime()));
		}
		return entries;
	}
	
	public TimeSheetEntry getLastTimeSheetEntry() {
		if (!timeSheetEntries.isEmpty()) {
			return getTimeSheetEntries().get(timeSheetEntries.size() - 1);
		}
		return null;
	}
	
	public DateTime getLastEntryTimeMade() {
		final TimeSheetEntry entry = getLastTimeSheetEntry();
		if (entry != null) {
			if (entry.isFinished()) {
				return entry.getEndTime();
			}
			return entry.getStartTime();
		}
		return DateTimeUtil.truncate(sheetDate.getStartOfDay());
	}
	
	public DateTime getEntryStartTime(int index) throws IndexOutOfBoundsException {
		if (timeSheetEntries.get(index).isStarted()) {
			return timeSheetEntries.get(index).getStartTime();
		}
		return null;
	}
	
	public DateTime getEntryEndTime(int index) throws IndexOutOfBoundsException {
		if (timeSheetEntries.get(index).isFinished()) {
			return timeSheetEntries.get(index).getEndTime();
		}
		return null;
	}
	
	public DateTime getPreviousEntryEndTime(int index, boolean useDefault) throws IndexOutOfBoundsException {
		ListIterator<TimeSheetEntry> listIterator = timeSheetEntries.listIterator(index);
		if (listIterator.hasPrevious()) {
			TimeSheetEntry previous = listIterator.previous();
			if (previous.isFinished()) {
				return previous.getEndTime();
			}
		}
		if (useDefault) {
			return DateTimeUtil.truncate(sheetDate.getStartOfDay());
		}
		return null;
	}
	
	public DateTime getNextEntryStartTime(int index, boolean useDefault) throws IndexOutOfBoundsException {
		ListIterator<TimeSheetEntry> listIterator = timeSheetEntries.listIterator(index);
		listIterator.next();
		if (listIterator.hasNext()) {
			TimeSheetEntry next = listIterator.next();
			if (next.isStarted()) {
				return next.getStartTime();
			}
		}
		if (useDefault) {
			return DateTimeUtil.truncate(sheetDate.getEndOfDay());
		}
		return null;
	}
	
	public void addTimeSheetEntry(TimeSheetEntry timeSheetEntry) {
		addTimeSheetEntry(timeSheetEntry, true);
	}
	
	public void addTimeSheetEntry(TimeSheetEntry timeSheetEntry, boolean notify) {
		if (!timeSheetEntries.isEmpty()) {
			TimeSheetEntry lastTimeEntry = timeSheetEntries.get(timeSheetEntries.size() - 1);
			if (!lastTimeEntry.isFinished()) {
				lastTimeEntry.setEndTime(timeSheetEntry.getStartTime());
			}
		}
		timeSheetEntries.add(timeSheetEntry);
		isDirty = true;
		if (notify) {
			notifyListeners();
		}
	}
	
	public void insertTimeSheetEntry(int index, TimeSheetEntry timeSheetEntry) throws IndexOutOfBoundsException, InsertTimeSheetEntryException {
		insertTimeSheetEntry(index, timeSheetEntry, true);
	}
	
	public void insertTimeSheetEntry(int index, TimeSheetEntry timeSheetEntry, boolean notify) throws IndexOutOfBoundsException, InsertTimeSheetEntryException {
		// Entry must be both started and finished to be inserted
		if (!timeSheetEntry.isFinished() || !timeSheetEntry.isStarted()) {
			throw new InsertTimeSheetEntryException(this, timeSheetEntry, "Entry must be started and finished.");
		}
		TimeSheetEntry entry = timeSheetEntries.get(index);
		// Entry can not start earlier than the previous or the start of the day
		if (getPreviousEntryEndTime(index, true).gt(timeSheetEntry.getStartTime())) {
			throw new InsertTimeSheetEntryException(this, timeSheetEntry, "Start time can not be less than 12:00:00 AM or the preceding entry's ending time.");
		}
		// New entry can not end after the entry it will follow
		if (timeSheetEntry.getEndTime().gt(entry.getStartTime())) {
			throw new InsertTimeSheetEntryException(this, timeSheetEntry, "Entry must end on or before the selected entry's starting time.");
		}
		timeSheetEntries.add(index, timeSheetEntry);
		isDirty = true;
		if (notify) {
			notifyListeners();
		}
	}
	
	public void updateTimeSheetEntry(int index, TimeSheetEntry timeSheetEntry) throws IndexOutOfBoundsException, UpdateTimeSheetEntryException {
		updateTimeSheetEntry(index, timeSheetEntry, true);
	}
	
	public void updateTimeSheetEntry(int index, TimeSheetEntry timeSheetEntry, boolean notify) throws IndexOutOfBoundsException, UpdateTimeSheetEntryException {
		// Updating can be done on unfinished entries
		// Entry can not start earlier than the previous or the start of the day
		if (getPreviousEntryEndTime(index, true).gt(timeSheetEntry.getStartTime())) {
			throw new UpdateTimeSheetEntryException(this, timeSheetEntry, "Start time can not be less than 12:00:00 AM or the preceding entry's ending time.");
		}
		// Entry can not end later than the next entry's start time, if finished
		if (timeSheetEntry.isFinished() && getNextEntryStartTime(index, true).lt(timeSheetEntry.getEndTime())) {
			throw new UpdateTimeSheetEntryException(this, timeSheetEntry, "End time can not be greater than the following entry's starting time or 11:59:59 PM.");
		}
		final TimeSheetEntry entry = timeSheetEntries.get(index);
		entry.setWorkDirective(timeSheetEntry.getWorkDirective());
		entry.setCrossCharge(timeSheetEntry.getCrossCharge());
		entry.setTaskCode(timeSheetEntry.getTaskCode());
		entry.setNote(timeSheetEntry.getNote());
		entry.setSuspended(timeSheetEntry.isSuspended());
		entry.setStartTime(timeSheetEntry.getStartTime());
		entry.setEndTime(timeSheetEntry.getEndTime());
		isDirty = true;
		if (notify) {
			notifyListeners();
		}
	}
	
	public void splitTimeSheetEntry(int index, TimeSheetEntry timeSheetEntry, boolean beginsBefore) throws IndexOutOfBoundsException, SplitTimeSheetEntryException {
		splitTimeSheetEntry(index, timeSheetEntry, beginsBefore, true);
	}
	
	public void splitTimeSheetEntry(int index, TimeSheetEntry timeSheetEntry, boolean beginsBefore, boolean notify) throws IndexOutOfBoundsException, SplitTimeSheetEntryException {
		TimeSheetEntry entry = timeSheetEntries.get(index);
		// Entry must be both started and finished to be used in a split
		if (!timeSheetEntry.isFinished() || !timeSheetEntry.isStarted()) {
			throw new SplitTimeSheetEntryException(this, timeSheetEntry, "Entry must be started and finished.");
		}
		if (beginsBefore) {
			// Adjusting the entry's start time and inserting a new one before it
			// Entry must still have time
			if (timeSheetEntry.getEndTime().compareTo(entry.getEndTime()) >= 0) {
				throw new SplitTimeSheetEntryException(this, timeSheetEntry, "Split entry must still have time in it.");
			}
			// New entry must start on or after previous entry end time
			if (getPreviousEntryEndTime(index, true).compareTo(timeSheetEntry.getStartTime()) > 0) {
				throw new SplitTimeSheetEntryException(this, timeSheetEntry, "New entry must start on or after the preceding entry's end time, or 12:00:00 AM");
			}
			// Adjust start time of original and then insert
			entry.setStartTime(timeSheetEntry.getEndTime());
				timeSheetEntries.add(index, timeSheetEntry);
		}
		else {
			// Adjusting the entry's end time and inserting after it
			// Entry must still have time
			if (timeSheetEntry.getStartTime().compareTo(entry.getStartTime()) <= 0) {
				throw new SplitTimeSheetEntryException(this, timeSheetEntry, "Split entry must still have time in it.");
			}
			// New entry must end on or before previous entry start time
			if (getNextEntryStartTime(index, true).compareTo(timeSheetEntry.getEndTime()) < 0) {
				throw new SplitTimeSheetEntryException(this, timeSheetEntry, "New entry must end on or before the next entry's start time, or 11:59:59 AM");
			}
			entry.setEndTime(timeSheetEntry.getStartTime());
			timeSheetEntries.add(Math.min(index + 1, timeSheetEntries.size()), timeSheetEntry);
		}
		isDirty = true;
		if (notify) {
			notifyListeners();
		}
	}
	
	public void deleteTimeSheetEntry(int index) throws IndexOutOfBoundsException {
		deleteTimeSheetEntry(index, true);
	}
	
	public void deleteTimeSheetEntry(int index, boolean notify) throws IndexOutOfBoundsException {
		timeSheetEntries.remove(index);
		isDirty = true;
		if (notify) {
			notifyListeners();
		}
	}
	
	public void stopTimeSheetEntry() {
		stopTimeSheetEntry(true);
	}
	
	public void stopTimeSheetEntry(boolean notify) {
		final TimeSheetEntry entry = timeSheetEntries.get(timeSheetEntries.size() - 1);
		if (!entry.isFinished()) {
			entry.setEndTime();
			isDirty = true;
			if (notify) {
				notifyListeners();
			}
		}
	}
	
	public void stopTimeSheetEntry(DateTime endTime) {
		stopTimeSheetEntry(endTime, true);
	}
	
	public void stopTimeSheetEntry(DateTime endTime, boolean notify) {
		final TimeSheetEntry entry = timeSheetEntries.get(timeSheetEntries.size() - 1);
		if (!entry.isFinished()) {
			entry.setEndTime(endTime);
			isDirty = true;
			if (notify) {
				notifyListeners();
			}
		}
	}
	
	public void setEntrySuspended(int index, boolean suspended) throws IndexOutOfBoundsException {
		setEntrySuspended(index, suspended, true);
	}
	
	public void setEntrySuspended(int index, boolean suspended, boolean notify) throws IndexOutOfBoundsException {
		final TimeSheetEntry entry = timeSheetEntries.get(index);
		if (entry.isSuspended() != suspended) {
			entry.setSuspended(suspended);
			isDirty = true;
			if (notify) {
				notifyListeners();
			}
		}
	}
	
	private void notifyListeners() {
		for (TimeSheetChangeListener listener : listeners) {
			listener.timeSheetChanged(this);
		}
	}
	
	public void addTimeSheetChangeListener(TimeSheetChangeListener listener) {
		listeners.add(listener);
	}
	
	public void removeTimeSheetChangeListener(TimeSheetChangeListener listener) {
		listeners.remove(listener);
	}
	
	void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	void setSheetDate(DateTime date) {
		sheetDate = date;
	}
	
	void setTimeSheetEntries(List<TimeSheetEntry> entries) {
		timeSheetEntries = (ArrayList<TimeSheetEntry>) entries;
	}
}
