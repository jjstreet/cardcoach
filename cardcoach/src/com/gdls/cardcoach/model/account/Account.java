package com.gdls.cardcoach.model.account;

public class Account implements Comparable<Account> {
	
	private volatile int hashCode = 0;

	private String number;
	private String nextWeekNumber;
	
	public Account(String number, String nextWeekNumber) {
		this.number = number;
		this.nextWeekNumber = nextWeekNumber;
	}
	
	public String getNumber() {
		return number;
	}
	
	public String getNextWeekNumber() {
		return nextWeekNumber;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Account)) {
			return false;
		}
		Account a = (Account) obj;
		if (getNumber() == null) {
			if (a.getNumber() != null) {
				return false;
			}
		}
		else {
			if (!getNumber().equals(a.getNumber())) {
				return false;
			}
		}
		if (getNextWeekNumber() == null) {
			if (a.getNextWeekNumber() != null) {
				return false;
			}
		}
		else {
			if (!getNextWeekNumber().equals(a.getNextWeekNumber())) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = 251;
			if (getNumber() != null) {
				hashCode ^= getNumber().hashCode();
			}
			if (getNextWeekNumber() != null) {
				hashCode ^= getNextWeekNumber().hashCode();
			}
		}
		return hashCode;
	}
	
	@Override
	public String toString() {
		return "Account[" + getNumber() + "/" + getNextWeekNumber() + "]";
	}
	
	@Override
	public int compareTo(Account a) {
		int result = getNumber().compareTo(a.getNumber());
		if (result != 0) {
			return result;
		}
		return getNextWeekNumber().compareTo(a.getNextWeekNumber());
	}
}
