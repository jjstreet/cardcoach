package com.gdls.cardcoach.workdirective;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.gdls.cardcoach.employee.Employee;
import com.gdls.cardcoach.employee.Employee.Type;

public class Account implements Comparable<Account> {

	private String number;
	private String numberNextWeek;
	private Employee.Type employeeType;
	private boolean selected;
	
	public Account(String number, String numberNextWeek, Employee.Type type) throws IllegalArgumentException {
		this (number, numberNextWeek, type, false);
	}
	
	public Account(String number, String numberNextWeek, Employee.Type type, boolean selected) throws IllegalArgumentException {
		if (number == null || number.isEmpty()) {
			throw new IllegalArgumentException("number must be non-empty string"); //$NON-NLS-1$
		}
		if (numberNextWeek == null || numberNextWeek.isEmpty()) {
			throw new IllegalArgumentException("number must be non-empty string"); //$NON-NLS-1$
		}
		if (type == null) {
			throw new IllegalArgumentException("type must not be null"); //$NON-NLS-1$
		}
		this.number = number;
		this.numberNextWeek = numberNextWeek;
		this.employeeType = type;
		this.selected = selected;
	}
	
	void setNumber(String number) {
		this.number = number;
	}
	
	void setNumberNextWeek(String numberNextWeek) {
		this.numberNextWeek = numberNextWeek;
	}
	
	void setEmployeeType(Type type) {
		this.employeeType = type;
	}
	
	void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public String getNumber() {
		return number;
	}
	
	public String getNumberNextWeek() {
		return numberNextWeek;
	}
	
	public Type getEmployeeType() {
		return employeeType;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !obj.getClass().equals(Account.class)) {
			return false;
		}
		Account acc = (Account) obj;
		if (!acc.number.equals(number)) {
			return false;
		}
		if (!acc.numberNextWeek.equalsIgnoreCase(numberNextWeek)) {
			return false;
		}
		if (!acc.employeeType.equals(employeeType)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Account o) {
		return number.compareToIgnoreCase(o.number);
	}
	
	public static final List<Account> allAccounts() {
		ArrayList<Account> accounts = new ArrayList<Account>();
		accounts.add(new Account("BEREAVEMENT 91161", "BEREAVEMENT NEXT WEEK 91161", Employee.Type.DIRECT));
		accounts.add(new Account("COMM ACTIVITY 90140", "COMM ACTIVITY NEXT WEEK 90140", Employee.Type.DIRECT));
		accounts.add(new Account("DCI 90120", "DCI NEXT WEEK 90120", Employee.Type.DIRECT));
		accounts.add(new Account("DCI SHIFT 2 90120", "DCI SHIFT 2 NEXT WEEK 90120", Employee.Type.DIRECT));
		accounts.add(new Account("DIR 72100", "DIR NEXT WEEK 72100", Employee.Type.DIRECT));
		accounts.add(new Account("DIR SHIFT 2 72100", "DIR SHIFT 2 NEXT WEEK 72100", Employee.Type.DIRECT));
		accounts.add(new Account("DIR SHIFT 3 72100", "DIR SHIFT 3 NEXT WEEK 72100", Employee.Type.DIRECT));
		accounts.add(new Account("EXPN UNALLOW 9013X", "EXPN UNALLOW NEXT WEEK 9013X", Employee.Type.DIRECT));
		accounts.add(new Account("EXPNWO 90130", "EXPNWO NEXT WEEK 90130", Employee.Type.DIRECT));
		accounts.add(new Account("EXPNWO SHIFT 2 90130", "EXPNWO SHIFT 2 NEXT WEEK 90130", Employee.Type.DIRECT));
		accounts.add(new Account("HOLIDAY 91120", "HOLIDAY NEXT WEEK 91120", Employee.Type.DIRECT));
		accounts.add(new Account("INDIRECT SHIFT 2 90100", "INDIRECT SHIFT 2 NEXT WEEK 90100", Employee.Type.DIRECT));
		accounts.add(new Account("INDIRECT SHIFT 3 90100", "INDIRECT SHIFT 3 NEXT WEEK 90100", Employee.Type.DIRECT));
		accounts.add(new Account("JURY DUTY 91160", "JURY DUTY NEXT WEEK 91160", Employee.Type.DIRECT));
		accounts.add(new Account("MEDICAL 91180", "MEDICAL NEXT WEEK 91180", Employee.Type.DIRECT));
		accounts.add(new Account("MILITARY PAY 91162", "MILITARY PAY NEXT WEEK 91162", Employee.Type.DIRECT));
		accounts.add(new Account("OTHER LOST TIME 91170", "OTHER LOST TIME NEXT WEEK 91170", Employee.Type.DIRECT));
		accounts.add(new Account("PERSONAL PD 91110", "PERSONAL PD NEXT WEEK 91110", Employee.Type.DIRECT));
		accounts.add(new Account("SICK 91100", "SICK NEXT WEEK 91100", Employee.Type.DIRECT));
		accounts.add(new Account("TRAINING 90110", "TRAINING NEXT WEEK 90110", Employee.Type.DIRECT));
		accounts.add(new Account("TRAINING SHIFT 2 90110", "TRAINING SHIFT 2 NEXT WEEK 90110", Employee.Type.DIRECT));
		accounts.add(new Account("UNALLOW LABOR 9010X", "UNALLOW LABOR NEXT WEEK 9010X", Employee.Type.DIRECT));
		accounts.add(new Account("UNPD DISCIPLINE SUSP 92175", "UNPD DISCIPLINE SUSP NEXT WEEK 92175", Employee.Type.DIRECT));
		accounts.add(new Account("UNPD LOA 92105", "UNPD LOA NEXT WEEK 92105", Employee.Type.DIRECT));
		accounts.add(new Account("UNPD NEW HIRE TERM SEP 92100", "UNPD NEW HIRE TERM SEP NEXT WEEK 92100", Employee.Type.DIRECT));
		accounts.add(new Account("UNPD OTHER LOST TIME 92170", "UNPD OTHER LOST TIME NEXT WEEK 92170", Employee.Type.DIRECT));
		accounts.add(new Account("UNPD PERSONAL 92110", "UNPD PERSONAL NEXT WEEK 92110", Employee.Type.DIRECT));
		accounts.add(new Account("UNPD TARDINESS 92140", "UNPD TARDINESS NEXT WEEK 92140", Employee.Type.DIRECT));
		accounts.add(new Account("VACATION 91130", "VACATION NEXT WEEK 91130", Employee.Type.DIRECT));
		
		accounts.add(new Account("BU SCHEDULED SHORT WORK WEEK 91195", "BU SCHEDULED SHORT WORK WEEK 91195", Employee.Type.UNION));
		accounts.add(new Account("BU UNSCHEDULED SHORT WORK WEEK 91191", "BU UNSCHEDULED SHORT WORK WEEK 91191", Employee.Type.UNION));
		accounts.add(new Account("BEREAVEMENT 91161", "BEREAVE NEXT WEEK 91161", Employee.Type.UNION));
		accounts.add(new Account("COMM ACTIVITY 90140", "COMM ACT NEXT WEEK 90140", Employee.Type.UNION));
		accounts.add(new Account("DCI 90120", "DCI NEXT WEEK 90120", Employee.Type.UNION));
		accounts.add(new Account("DCI FLEX TIME 90120", "DCI FLEX TIME NEXT WEEK 90120", Employee.Type.UNION));
		accounts.add(new Account("DCI OVT PAID 90120", "DCI OVT PAID 90120", Employee.Type.UNION));
		accounts.add(new Account("DCI SHIFT 2 90120", "DCI SHIFT 2 NEXT WEEK 90120", Employee.Type.UNION));
		accounts.add(new Account("DCI SHIFT 2 OVT PAID 90120", "DCI SHIFT 2 OVT PAID 90120", Employee.Type.UNION));
		accounts.add(new Account("DCI SHIFT 2 FLEX TIME 90120", "DCI SHIFT 2 FLEX TIME NEXT WEEK 90120", Employee.Type.UNION));
		accounts.add(new Account("DIR OVT PAID 72100", "DIR OVT PAID 72100", Employee.Type.UNION));
		accounts.add(new Account("DIR SHIFT 2 72100", "DIR SHIFT 2 NEXT WEEK 72100", Employee.Type.UNION));
		accounts.add(new Account("DIR SHIFT 2 OVT PAID 72100", "DIR SHIFT 2 OVT PAID 72100", Employee.Type.UNION));
		accounts.add(new Account("DIR SHIFT 3 72100", "DIR SHIFT 3 NEXT WEEK 72100", Employee.Type.UNION));
		accounts.add(new Account("DIR SHIFT 2 FLEX TIME 72100", "DIR SHIFT 2 FLEX TIME NEXT WEEK 72100", Employee.Type.UNION));
		accounts.add(new Account("DIR 72100", "DIR NEXT WEEK 72100", Employee.Type.UNION));
		accounts.add(new Account("DIR FLEX TIME 72100", "DIR FLEX TIME NEXT WEEK 72100", Employee.Type.UNION));
		accounts.add(new Account("EXPN SHIFT 2 OVT PAID 90130", "EXPN SHIFT 2 OVT PAID 90130", Employee.Type.UNION));
		accounts.add(new Account("EXPN UNALLOW 9013X", "EXPN UNALLOW NEXT WEEK 9013X", Employee.Type.UNION));
		accounts.add(new Account("EXPN UNALLOW FLEX TIME 9013X", "EXPN UNALLOW FLEX TIME NEXT WEEK 9013X", Employee.Type.UNION));
		accounts.add(new Account("EXPN UNALLOW SHIFT 2 9013X", "EXPN UNALLOW SHIFT 2 NEXT WEEK 9013X", Employee.Type.UNION));
		accounts.add(new Account("EXPNWO 90130", "EXPNWO NEXT WEEK 90130", Employee.Type.UNION));
		accounts.add(new Account("EXPNWO FLEX TIME 90130", "EXPNWO FLEX TIME NEXT WEEK 90130", Employee.Type.UNION));
		accounts.add(new Account("EXPNWO SHIFT 2 90130", "EXPNWO SHIFT 2 NEXT WEEK 90130", Employee.Type.UNION));
		accounts.add(new Account("EXPNWO SHIFT 2 FLEX TIME 60130", "EXPNWO SHIFT 2 FLEX TIME NEXT WEEK 60130", Employee.Type.UNION));
		accounts.add(new Account("HOLIDAY 91120", "HOLIDAY NEXT WEEK 91120", Employee.Type.UNION));
		accounts.add(new Account("INDIRECT SHIFT 2 90100", "INDIRECT SHIFT 2 NEXT WEEK 90100", Employee.Type.UNION));
		accounts.add(new Account("INDIRECT SHIFT 2 FLEX TIME 90100", "INDIRECT SHIFT 2 FLEX TIME NEXT WEEK 90100", Employee.Type.UNION));
		// MISSING UNION INDIRECT SHIFT 3
		accounts.add(new Account("ILLNESS FAMILY 91155", "ILLNESS FAMILY NEXT WEEK 91155", Employee.Type.UNION));
		accounts.add(new Account("ILLNESS INJURY 91150", "ILLNESS INJURY NEXT WEEK 91150", Employee.Type.UNION));
		accounts.add(new Account("INDIRECT 90100", "INDIRECT NEXT WEEK 90100", Employee.Type.UNION));
		accounts.add(new Account("INDIRECT FLEX TIME 90100", "INDIRECT FLEX TIME NEXT WEEK 90100", Employee.Type.UNION));
		accounts.add(new Account("INDIRECT OVT PAID 90100", "INDIRECT OVT PAID NEXT WEEK 90100", Employee.Type.UNION));
		accounts.add(new Account("JURY DUTY 91160", "JURY DUTY NEXT WEEK 91160", Employee.Type.UNION));
		accounts.add(new Account("MEDICAL 91180", "MEDICAL NEXT WEEK 91180", Employee.Type.UNION));
		accounts.add(new Account("MILITARY PAY 91162", "MILITARY PAY NEXT WEEK 91162", Employee.Type.UNION));
		accounts.add(new Account("OTHER LOST TIME 91170", "OTHER LOST TIME NEXT WEEK 91170", Employee.Type.UNION));
		accounts.add(new Account("PERSONAL PAID 91110", "PERSONAL PD NEXT WEEK 91110", Employee.Type.UNION));
		accounts.add(new Account("SICK 91100", "SICK NEXT WEEK 91100", Employee.Type.UNION));
		accounts.add(new Account("TARDINESS 91140", "TARDINESS NEXT WEEK 91140", Employee.Type.UNION));
		accounts.add(new Account("TRAINING 90110", "TRAINING NEXT WEEK 90110", Employee.Type.UNION));
		accounts.add(new Account("TRAINING SHIFT 2 90110", "TRAINING SHIFT 2 NEXT WEEK 90110", Employee.Type.UNION));
		accounts.add(new Account("UNPD OTHER LOST TIME 92170", "UNPD OTHER LOST TIME NEXT WEEK 92170", Employee.Type.UNION));
		accounts.add(new Account("UNPD SALARY UNION BUSINESS 92160", "UNPD SAL UNION BUS NEXT WEEK 92160", Employee.Type.UNION));
		accounts.add(new Account("UNALLOW LABOR 9010X", "UNALLOW LAB NEXT WEEK 9010X", Employee.Type.UNION));
		accounts.add(new Account("UNION BUSINESS 90150", "UNION BUS NEXT WEEK 90150", Employee.Type.UNION));
		accounts.add(new Account("UNION BUSINESS FLEX TIME 90150", "UNION BUSINESS NEXT WEEK FLEX TIME 90150", Employee.Type.UNION));
		accounts.add(new Account("UNPD DISCIPLINE SUSP 92175", "UNPD DISCIPLINE SUSP NEXT WEEK 92175", Employee.Type.UNION));
		accounts.add(new Account("UNPD ILLNESS FAMILY 92155", "UNPD ILLNESS FAMILY 92155 NEXT WEEK", Employee.Type.UNION));
		accounts.add(new Account("UNPD ILLNESS INJURY 92150", "UNPD ILLNESS INJURY 92150 NEXT WEEK", Employee.Type.UNION));
		accounts.add(new Account("UNPD LOA 92105", "UNPD LOA NEXT WEEK 92105", Employee.Type.UNION));
		accounts.add(new Account("UNPD NEW HIRE TERM SEP 92100", "UNPD NEW HIRE TERM SEP 92100", Employee.Type.UNION));
		accounts.add(new Account("UNPD PERSONAL 92110", "UNPD PERSONAL 92110", Employee.Type.UNION));
		accounts.add(new Account("UNPD TARDINESS 92140", "UNPD TARDINESS 92140", Employee.Type.UNION));
		accounts.add(new Account("VACATION 91130", "VACATION NEXT WEEK 91130", Employee.Type.UNION));
		
		accounts.add(new Account("COMM ACTIVITY 90140", "COMM ACTIVITY NEXT WEEK 90140", Employee.Type.CONTRACTOR));
		accounts.add(new Account("CONTRACT LABOR PAID LOST TIME", "CONTRACT LABOR PAID LOST TIME NEXT WEEK 91175", Employee.Type.CONTRACTOR));
		accounts.add(new Account("DCI 90120", "DCI NEXT WEEK 90120", Employee.Type.CONTRACTOR));
		accounts.add(new Account("DIR SHIFT 2 72500", "DIR SHFT 2 NEXT WEEK 72500", Employee.Type.CONTRACTOR));
		accounts.add(new Account("DIR SHIFT 3 72500", "DIR SHIFT 3 NEXT WEEK 72500", Employee.Type.CONTRACTOR));
		accounts.add(new Account("DIR 72500", "DIR NEXT WEEK 72500", Employee.Type.CONTRACTOR));
		accounts.add(new Account("EXPN CONTL 90474", "EXPN CONTL NEXT WEEK 90474", Employee.Type.CONTRACTOR));
		accounts.add(new Account("EXPN CONTL UNALLOW 9047X", "EXPN CONTL UNALLOW NEXT WEEK 9047X", Employee.Type.CONTRACTOR));
		accounts.add(new Account("INDIRECT SHIFT 2 90100", "INDIRECT SHIFT 2 NEXT WEEK 90100", Employee.Type.CONTRACTOR));
		accounts.add(new Account("INDIRECT SHIFT 3 90100", "INDIRECT SHIFT 3 NEXT WEEK 90100", Employee.Type.CONTRACTOR));
		accounts.add(new Account("INDIRECT 90100", "INDIRECT NEXT WEEK 90100", Employee.Type.CONTRACTOR));
		accounts.add(new Account("TRAINING 90110", "TRAINING NEXT WEEK 90110", Employee.Type.CONTRACTOR));
		accounts.add(new Account("UNALLOW LABOR 9010X", "UNALLOW LABOR NEXT WEEK 9010X", Employee.Type.CONTRACTOR));
		accounts.add(new Account("UNPD OTHER LOST TIME 92170", "UNPD OTHER LOST TIME NEXT WEEK 92170", Employee.Type.CONTRACTOR));
		
		return accounts;
	}
	
	public static final List<Account> allAccounts(Employee.Type type) {
		ArrayList<Account> accounts = (ArrayList<Account>) allAccounts();
		Iterator<Account> iterator = accounts.iterator();
		while (iterator.hasNext()) {
			if (!iterator.next().employeeType.equals(type)) {
				iterator.remove();
			}
		}
		return accounts;
	}
}
