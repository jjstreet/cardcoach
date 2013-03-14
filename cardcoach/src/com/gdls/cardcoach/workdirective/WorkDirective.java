package com.gdls.cardcoach.workdirective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gdls.cardcoach.employee.Employee.Type;

public class WorkDirective implements Comparable<WorkDirective> {

	private String number;
	private List<Account> accounts;
	private String description;
	private String suspenseNumber;
	private Integer favoriteId;
	
	WorkDirective() {
		this (null, new ArrayList<Account>(), null, null);
	}
	
	public WorkDirective(String number, Account account, String description, String suspenseNumber) {
		this.number = number;
		this.accounts = new ArrayList<Account>();
		account.setSelected(true);
		this.accounts.add(account);
		this.description = description;
		this.suspenseNumber = suspenseNumber;
	}
	
	public WorkDirective(String number, List<Account> accounts, String description, String suspenseNumber) {
		this (number, accounts, description, suspenseNumber, null);
	}
	
	public WorkDirective(String number, List<Account> accounts, String description, String suspenseNumber, Integer favoriteId) {
		this.number = number;
		this.accounts = accounts;
		this.description = description;
		this.suspenseNumber = suspenseNumber;
		this.favoriteId = favoriteId;
	}
	
	public String getNumber() {
		return number;
	}
	
	void setNumber(String number) {
		this.number = number;
	}

	public Account getSelectedAccount() {
		if (accounts != null) {
			for (Account account : accounts) {
				if (account.isSelected()) {
					return account;
				}
			}
		}
		return null;
	}

	public List<Account> getAccounts() {
		if (accounts != null) {
			return Collections.unmodifiableList(accounts);
		}
		return null;
	}

	public List<Account> getAccounts(Type type) {
		if (accounts != null) {
			ArrayList<Account> accs = new ArrayList<Account>();
			for (Account account : accounts) {
				if (account.getEmployeeType().equals(type)) {
					accs.add(account);
				}
			}
			return Collections.unmodifiableList(accs);
		}
		return null;
	}

	void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	void addAccount(Account account) {
		if (!accounts.contains(account)) {
			accounts.add(account);
		}
	}

	void removeAccount(Account account) {
		if (accounts.contains(account)) {
			accounts.remove(account);
		}
	}
	
	void replaceAccount(Account account, Account newAccount) {
		if (accounts.contains(account)) {
			accounts.remove(account);
			accounts.add(newAccount);
		}
	}
	
	public void setSelectedAccount(Account account, boolean selected) {
		for (Account acc : accounts) {
			if (acc.equals(account)) {
				acc.setSelected(selected);
			}
			else {
				acc.setSelected(false);
			}
		}
	}
	
	public String getDescription() {
		return description;
	}

	void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isSuspendable() {
		return suspenseNumber != null && !suspenseNumber.isEmpty();
	}
	
	public String getSuspenseNumber() {
		return suspenseNumber;
	}

	void setSuspenseNumber(String suspenseNumber) {
		this.suspenseNumber = suspenseNumber;
	}
	
	public Integer getFavoriteId() {
		return favoriteId;
	}

	public void setFavoriteId(Integer id) {
		this.favoriteId = id;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!other.getClass().equals(WorkDirective.class)) {
			return false;
		}
		WorkDirective wd = (WorkDirective) other;
		if (!number.equals(wd.number)) {
			return false;
		}
		if (wd.accounts != null && accounts == null || wd.accounts == null && accounts != null) {
			return false;
		}
		if (wd.accounts != null && accounts != null) {
			if (wd.accounts.size() != accounts.size()) {
				return false;
			}
			for (Account account : wd.accounts) {
				if (!accounts.contains(account)) {
					return false;
				}
			}
		}
		if (isSuspendable() != wd.isSuspendable()) {
			return false;
		}
		if (!suspenseNumber.equalsIgnoreCase(wd.suspenseNumber)) {
			return false;
		}
		return true;
	}
	
	@Override
	public int compareTo(WorkDirective otherWorkDirective) {
		if (favoriteId == null) {
			if (otherWorkDirective.favoriteId == null) {
				if (this.getNumber().compareToIgnoreCase(otherWorkDirective.getNumber()) > 0) {
					return 1;
				} else if (this.getNumber().compareToIgnoreCase(otherWorkDirective.getNumber()) < 0) {
					return -1;
				} else {
					return 0;
				}
			} else {
				return 1;
			}
		} else if (otherWorkDirective.favoriteId == null) {
			return -1;
		} else {
			if (this.favoriteId.compareTo(otherWorkDirective.favoriteId) > 0) {
				return 1;
			} else if (this.favoriteId.compareTo(otherWorkDirective.favoriteId) < 0) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}
