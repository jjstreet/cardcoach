package com.gdls.cardcoach.export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.gdls.cardcoach.timecard.TimeCard;
import com.gdls.cardcoach.timecard.TimeCardEntry;
import com.gdls.cardcoach.timesheet.TimeSheet;
import com.gdls.cardcoach.timesheet.TimeSheetEntry;

public class TextExporter {

	private static final String HEADER =
			"********************************************************************************%n" +
			"**                       Card Coach - Daily Charge Log                        **%n" +
			"**                         For auditing purposes only.                        **%n" +
			"********************************************************************************%n";
	private static final String SECTION_HEADER = 
			"    %s:%n";
	private static final String EMP_FULL_NAME =
			"                            Name:  %s%n";
	private static final String EMP_BADGE_NUMBER =
			"                    Badge Number:  %s%n";
	private static final String EMP_TYPE =
			"                            Type:  %s%n";
	private static final String DATE =
			"                            Date:  %s%n";
	private static final String START_TIME =
			"                      Start Time:  %s%n";
	private static final String END_TIME =
			"                        End Time:  %s%n";
	private static final String SPLIT_NEXT_WEEK =
			"             Split for Next Week:  %s%n";
	private static final String SPLIT_HOURS =
			"                     Split After:  %s Hours%n";
	private static final String TIME_ENTRIES_TABLE_HEADER =
			"Work Directive  Cross Charge Dept  Task Code    Starting Time   Ending Time%n";
	private static final String TIME_ENTRIES_TABLE_ENTRY =
			"%1$-15s %2$-18s %3$-12s %4$-15s %5$-15s%n";
	private static final String HOURS_TABLE_HEADER =
			"Work Directive  Account                  Cross Charge Dept  Task Code    Hours%n";
	private static final String HOURS_TABLE_ENTRY =
			"%1$-15s %2$-24s %3$-18s %4$-12s %5$-6s%n";
	private static final String TOTAL_HOURS =
			"                     Total Hours:  %s%n";
	
	public static void exportTo(String location, TimeSheet timeSheet) {
		BufferedWriter out;
		StringBuilder sb = new StringBuilder();
		sb.append(String.format(HEADER));
		sb.append(String.format("%n"));
		sb.append(String.format(SECTION_HEADER, "Details"));
		sb.append(String.format(EMP_FULL_NAME, timeSheet.getEmployee().getFullName()));
		sb.append(String.format(EMP_BADGE_NUMBER, timeSheet.getEmployee().getBadgeNumber()));
		sb.append(String.format(EMP_TYPE, timeSheet.getEmployee().getType().toString()));
		sb.append(String.format(DATE, timeSheet.getFormattedSheetDate()));
		sb.append(String.format(START_TIME, timeSheet.getFormattedStartTime()));
		sb.append(String.format(END_TIME, timeSheet.getFormattedEndTime()));
		sb.append(String.format("%n"));
		sb.append(String.format(SPLIT_NEXT_WEEK, timeSheet.isSplitting() ? "YES" : "NO"));
		if (timeSheet.isSplitting()) {
			sb.append(String.format(SPLIT_HOURS, timeSheet.getSplitTimeHours().toString()));
		}
		sb.append(String.format("%n"));
		sb.append(String.format(SECTION_HEADER, "Time Entries"));
		sb.append(String.format(TIME_ENTRIES_TABLE_HEADER));
		for (TimeSheetEntry entry : timeSheet.getTimeSheetEntries()) {
			sb.append(String.format(TIME_ENTRIES_TABLE_ENTRY,
					entry.getWorkDirective().getNumber(),
					entry.getCrossCharge(),
					entry.getTaskCode(),
					entry.getFormattedStartTime(),
					entry.getFormattedEndTime()));
		}
		sb.append(String.format("%n"));
		sb.append(String.format(SECTION_HEADER, "Hours Breakdown"));
		sb.append(String.format(HOURS_TABLE_HEADER));
		TimeCard timeCard = TimeCard.newFromTimeSheet(timeSheet);
		for (TimeCardEntry entry : timeCard.getTimeCardEntries()) {
			sb.append(String.format(HOURS_TABLE_ENTRY,
					entry.getWorkDirective(),
					entry.getAccount(),
					entry.getCrossCharge(),
					entry.getTaskCode(),
					entry.getHours().toString()));
		}
		sb.append(String.format("%n"));
		sb.append(String.format(TOTAL_HOURS, timeSheet.getChargedTimeHours().toString()));
		try {
			out = new BufferedWriter(new FileWriter(location));
			out.write(sb.toString());
			out.close();
		} catch (IOException e) {
			// Nothing
		}
	}
}
