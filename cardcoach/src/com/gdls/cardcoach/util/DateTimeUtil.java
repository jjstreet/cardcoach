package com.gdls.cardcoach.util;

import java.math.BigDecimal;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public final class DateTimeUtil {

	public enum Unit {
		TENTH_HOUR
	}
	
	public static final BigDecimal tenthHour = new BigDecimal("0.1");
	
	public static final int ROUND_HALF_UP = BigDecimal.ROUND_HALF_UP;
	public static final int ROUND_HALF_DOWN = BigDecimal.ROUND_HALF_DOWN;
	
	public static final DateTime endOfDay = truncate(DateTime.today(TimeZone.getDefault()).getEndOfDay());
	public static final DateTime startOfDay = truncate(DateTime.today(TimeZone.getDefault()).getStartOfDay());
	
	private DateTimeUtil() {
		
	}
	
	public static final DateTime roundDateTime(DateTime dateTime, Unit unit) {
		DateTime newDateTime = dateTime;
		if (DateTimeUtil.Unit.TENTH_HOUR.equals(unit)) {
			int dateMinutes = newDateTime.getMinute();
			int dateSeconds = newDateTime.getSecond();
			float floatMinutes = dateMinutes + (float) dateSeconds / 60;
			if (floatMinutes > 3f) {
				newDateTime = newDateTime.plus(0, 0, 0, 0, (6 - dateMinutes % 6), 0, DateTime.DayOverflow.LastDay);
				newDateTime = newDateTime.minus(0, 0, 0, 0, 0, dateSeconds, DateTime.DayOverflow.LastDay);
			} else {
				newDateTime = newDateTime.minus(0, 0, 0, 0, dateMinutes % 6, dateSeconds, DateTime.DayOverflow.LastDay);
			}
			return newDateTime;
		}
		return null;
	}
	
	public static final BigDecimal roundTimeInterval(Long interval, Unit unit) {
		return roundTimeInterval(interval, unit, BigDecimal.ROUND_HALF_UP);
	}
	
	public static final BigDecimal roundTimeInterval(Long interval, Unit unit, int roundingMode) {
		if (interval != null) {
			if (Unit.TENTH_HOUR.equals(unit)) {
				BigDecimal hours = new BigDecimal(interval);
				hours.setScale(0);
				return hours.divide(new BigDecimal("360"), roundingMode).movePointLeft(1);
			}
			return null;
		}
		return null;
	}
	
	public static final BigDecimal roundTimeInterval(DateTime startTime, DateTime endTime, Unit unit) {
		return roundTimeInterval(startTime, endTime, unit, BigDecimal.ROUND_HALF_UP);
	}
	
	public static final BigDecimal roundTimeInterval(DateTime startTime, DateTime endTime, Unit unit, int roundingMode) {
		if (startTime != null && endTime != null) {
			Long interval = startTime.numSecondsFrom(endTime);
			return DateTimeUtil.roundTimeInterval(interval, unit, roundingMode);
		}
		return null;
	}
	
	public static final DateTime truncate(DateTime time) {
		if (time != null) {
			return time.truncate(DateTime.Unit.SECOND);
		}
		return null;
	}
}
