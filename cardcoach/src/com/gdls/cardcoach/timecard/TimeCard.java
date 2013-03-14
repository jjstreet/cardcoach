package com.gdls.cardcoach.timecard;

import hirondelle.date4j.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import com.gdls.cardcoach.employee.Employee;
import com.gdls.cardcoach.timesheet.TimeSheet;
import com.gdls.cardcoach.timesheet.TimeSheetEntry;
import com.gdls.cardcoach.util.DateTimeUtil;

public class TimeCard {
	
	private DateTime startTime;
	private DateTime endTime;

	private boolean isSplitting;
	private long totalTime;
	private long splitTime;
	private BigDecimal totalTimeHours;
	private BigDecimal splitTimeHours;
	private Employee employee;
	
	private ArrayList<TimeCardEntry> timeCardEntries;
	
	private TimeCard(Employee employee, DateTime startTime, DateTime endTime, long totalTime, boolean isSplitting, long splitTime) {
		this.employee = employee;
		this.startTime = startTime;
		this.endTime = endTime;
		this.totalTime = totalTime;
		this.isSplitting = isSplitting;
		this.splitTime = splitTime;
		totalTimeHours = DateTimeUtil.roundTimeInterval(totalTime, DateTimeUtil.Unit.TENTH_HOUR);
		splitTimeHours = DateTimeUtil.roundTimeInterval(Math.min(splitTime, totalTime), DateTimeUtil.Unit.TENTH_HOUR);
		timeCardEntries = new ArrayList<TimeCardEntry>();
	}
	
	public static final TimeCard newFromTimeSheet(TimeSheet timeSheet) {
//		System.out.println("Attempting to create a time card.");
		if (timeSheet != null) {
			TimeCardEntry cardEntry;
			TimeCardEntry existingEntry;
			final TimeCard timeCard = new TimeCard(
					timeSheet.getEmployee(),
					timeSheet.getStartTime(), timeSheet.getEndTime(),
					timeSheet.getChargedTimeSeconds(),
					timeSheet.isSplitting(), timeSheet.getSplitTimeSeconds());
			// Prepare counters for building time card entries
			long remainingEntryTime = 0L;
			long remainingCurrentWeekTime = timeCard.isSplitting ? Math.min(timeCard.splitTime, timeCard.totalTime) : timeCard.totalTime;
//			System.out.println(remainingCurrentWeekTime);
			long remainingNextWeekTime = timeCard.isSplitting ? timeCard.totalTime - timeCard.splitTime : 0L;
			for (TimeSheetEntry timeSheetEntry : timeSheet.getTimeSheetEntries()) {
				if (timeSheetEntry.isStarted() && timeSheetEntry.isFinished()) {
					remainingEntryTime = 0L;
					if (remainingCurrentWeekTime > 0L) {
						cardEntry = TimeCardEntry.newFromTimeSheetEntry(timeSheetEntry, remainingCurrentWeekTime, false);
						existingEntry = timeCard.getTimeCardEntry(cardEntry);
						if (existingEntry != null) {
							existingEntry.setInterval(existingEntry.getInterval() + cardEntry.getInterval());
							existingEntry.combineNotes(cardEntry.getNotes());
							existingEntry.setHours(DateTimeUtil.roundTimeInterval(existingEntry.getInterval(), DateTimeUtil.Unit.TENTH_HOUR));
						}
						else {
							cardEntry.setHours(DateTimeUtil.roundTimeInterval(cardEntry.getInterval(), DateTimeUtil.Unit.TENTH_HOUR));
							timeCard.timeCardEntries.add(cardEntry);
						}
						remainingCurrentWeekTime -= cardEntry.getInterval();
						remainingEntryTime = timeSheetEntry.getIntervalSeconds() - cardEntry.getInterval();
						if (remainingEntryTime > 0L) {
							// Need to build a cardentry for next week using the same timesheetentry
							cardEntry = TimeCardEntry.newFromTimeSheetEntry(timeSheetEntry, remainingEntryTime, true);
							existingEntry = timeCard.getTimeCardEntry(cardEntry);
							if (existingEntry != null) {
								existingEntry.setInterval(existingEntry.getInterval() + cardEntry.getInterval());
								existingEntry.combineNotes(cardEntry.getNotes());
								existingEntry.setHours(DateTimeUtil.roundTimeInterval(existingEntry.getInterval(), DateTimeUtil.Unit.TENTH_HOUR));
							}
							else {
								cardEntry.setHours(DateTimeUtil.roundTimeInterval(cardEntry.getInterval(), DateTimeUtil.Unit.TENTH_HOUR));
								timeCard.timeCardEntries.add(cardEntry);
							}
							remainingNextWeekTime -= remainingEntryTime;
						}
					}
					else if (remainingNextWeekTime > 0L) {
						cardEntry = TimeCardEntry.newFromTimeSheetEntry(timeSheetEntry, remainingNextWeekTime, true);
						existingEntry = timeCard.getTimeCardEntry(cardEntry);
						if (existingEntry != null) {
							existingEntry.setInterval(existingEntry.getInterval() + cardEntry.getInterval());
							existingEntry.combineNotes(cardEntry.getNotes());
							existingEntry.setHours(DateTimeUtil.roundTimeInterval(existingEntry.getInterval(), DateTimeUtil.Unit.TENTH_HOUR));
						}
						else {
							cardEntry.setHours(DateTimeUtil.roundTimeInterval(cardEntry.getInterval(), DateTimeUtil.Unit.TENTH_HOUR));
							timeCard.timeCardEntries.add(cardEntry);
						}
						remainingNextWeekTime -= cardEntry.getInterval();
					}
				}
			}
			timeCard.handleRounding();
			return timeCard;
		}
		return null;
	}
	
	private TimeCardEntry getTimeCardEntry(TimeCardEntry timeCardEntry) {
		for (TimeCardEntry entry : timeCardEntries) {
			if (entry.getWorkDirective().equalsIgnoreCase(timeCardEntry.getWorkDirective()) &&
					entry.getAccount().equalsIgnoreCase(timeCardEntry.getAccount()) &&
					entry.getCrossCharge().equalsIgnoreCase(timeCardEntry.getCrossCharge()) &&
					entry.getTaskCode().equalsIgnoreCase(timeCardEntry.getTaskCode())) {
				return entry;
			}
			
		}
		return null;
	}
	
	private void handleRounding() {
//		System.out.println("Handling rounding");
		TreeMap<Long, TimeCardEntry> sortedCurrentWeekIndices = new TreeMap<Long, TimeCardEntry>();
		TreeMap<Long, TimeCardEntry> sortedNextWeekIndices = new TreeMap<Long, TimeCardEntry>();
		BigDecimal calculatedSplitHours = BigDecimal.ZERO;
		BigDecimal calculatedTotalHours = BigDecimal.ZERO;
//		System.out.println("...building rounding indexes");
		for (TimeCardEntry entry : timeCardEntries) {
			if (isSplitting) {
				if (!entry.isNextWeek()) {
					sortedCurrentWeekIndices.put(entry.getRoundingRemainder(), entry);
					calculatedSplitHours = calculatedSplitHours.add(
							DateTimeUtil.roundTimeInterval(entry.getInterval(), DateTimeUtil.Unit.TENTH_HOUR));
				}
				else {
					sortedNextWeekIndices.put(entry.getRoundingRemainder(), entry);
				}
			}
			else {
				sortedCurrentWeekIndices.put(entry.getRoundingRemainder(), entry);
			}
			calculatedTotalHours = calculatedTotalHours.add(
					DateTimeUtil.roundTimeInterval(entry.getInterval(), DateTimeUtil.Unit.TENTH_HOUR));
		}
		TimeCardEntry entry;
//		System.out.println("Number of current week entries: " + sortedCurrentWeekIndices.size());
//		System.out.println("Number of next week entries: " + sortedNextWeekIndices.size());
//		System.out.println("Rounding split time from " + calculatedSplitHours.toString() + " to " + splitTimeHours.toString());
//		System.out.println("Rounding total time from " + calculatedTotalHours.toString() + " to " + totalTimeHours.toString());
		if (isSplitting) {
			while (calculatedSplitHours.compareTo(splitTimeHours) > 0) {
				entry = sortedCurrentWeekIndices.firstEntry().getValue();
				entry.roundDownHours();
				calculatedSplitHours = calculatedSplitHours.subtract(DateTimeUtil.tenthHour);
				calculatedTotalHours = calculatedTotalHours.subtract(DateTimeUtil.tenthHour);
				sortedCurrentWeekIndices.remove(sortedCurrentWeekIndices.firstKey());
			}
			while (calculatedSplitHours.compareTo(splitTimeHours) < 0) {
				entry = sortedCurrentWeekIndices.lastEntry().getValue();
				entry.roundUpHours();
				calculatedSplitHours = calculatedSplitHours.add(DateTimeUtil.tenthHour);
				calculatedTotalHours = calculatedTotalHours.add(DateTimeUtil.tenthHour);
				sortedCurrentWeekIndices.remove(sortedCurrentWeekIndices.lastKey());
			}
			while (calculatedTotalHours.compareTo(totalTimeHours) > 0) {
				entry = sortedNextWeekIndices.firstEntry().getValue();
				entry.roundDownHours();
				calculatedTotalHours = calculatedTotalHours.subtract(DateTimeUtil.tenthHour);
				sortedNextWeekIndices.remove(sortedNextWeekIndices.firstKey());
			}
			while (calculatedTotalHours.compareTo(totalTimeHours) < 0) {
				entry = sortedNextWeekIndices.lastEntry().getValue();
				entry.roundUpHours();
				calculatedTotalHours = calculatedTotalHours.add(DateTimeUtil.tenthHour);
				sortedNextWeekIndices.remove(sortedNextWeekIndices.lastKey());
			}
		}
		else {
			while (calculatedTotalHours.compareTo(totalTimeHours) > 0) {
				entry = sortedCurrentWeekIndices.firstEntry().getValue();
				System.out.println("Rounding down: " + entry.getWorkDirective() + " - " + entry.getAccount() + " - " + entry.getHours());
				entry.roundDownHours();
				calculatedTotalHours = calculatedTotalHours.subtract(DateTimeUtil.tenthHour);
				sortedCurrentWeekIndices.remove(sortedCurrentWeekIndices.firstKey());
			}
			while (calculatedTotalHours.compareTo(totalTimeHours) < 0) {
				entry = sortedCurrentWeekIndices.lastEntry().getValue();
				entry.roundUpHours();
				calculatedTotalHours = calculatedTotalHours.add(DateTimeUtil.tenthHour);
				sortedCurrentWeekIndices.remove(sortedCurrentWeekIndices.lastKey());
			}
		}
	}
	
	public Employee getEmployee() {
		return employee;
	}
	
	public List<TimeCardEntry> getTimeCardEntries() {
		return Collections.unmodifiableList(timeCardEntries);
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
	
	public DateTime getEndTime() {
		return endTime;
	}
	
	public String getFormattedEndTime() {
		return getFormattedEndTime("h12:mm:ss a");
	}
	
	public String getFormattedEndTime(String format) {
		return getEndTime() != null ? getEndTime().format(format, Locale.getDefault()) : "";
	}
	
	public BigDecimal getTotalHours() {
		return totalTimeHours;
	}
	
	public BigDecimal getSplitHours() {
		return splitTimeHours;
	}
}
