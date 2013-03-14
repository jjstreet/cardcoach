package com.gdls.cardcoach.model.employee;

import com.gdls.cardcoach.model.ModelObject;

public class Employee extends ModelObject {
	
	private volatile int hashCode = 0;

	private String firstName;
	private String lastName;
	private String badgeNumber;
	private String username;
	private EmployeeType employeeType;
	
	public Employee() {
		
	}
	
	public Employee(String firstName, String lastName, String badgeNumber, String username, EmployeeType employeeType) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.badgeNumber = badgeNumber;
		this.username = username;
		this.employeeType = employeeType;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getBadgeNumber() {
		return badgeNumber;
	}
	
	public String getUsername() {
		return username;
	}
	
	public EmployeeType getEmployeeType() {
		return employeeType;
	}
	
	public void setFirstName(String firstName) {
		firePropertyChange("firstName", this.firstName, this.firstName = firstName);
	}
	
	public void setLastName(String lastName) {
		firePropertyChange("lastName", this.lastName, this.lastName = lastName);
	}
	
	public void setBadgeNumber(String badgeNumber) {
		firePropertyChange("badgeNumber", this.badgeNumber, this.badgeNumber = badgeNumber);
	}
	
	public void setUsername(String username) {
		firePropertyChange("username", this.username, this.username = username);
	}
	
	public void setEmployeeType(EmployeeType employeeType) {
		firePropertyChange("employeeType", this.employeeType, this.employeeType = employeeType);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Employee)) {
			return false;
		}
		Employee e = (Employee) obj;
		if (getFirstName() == null) {
			if (e.getFirstName() != null) {
				return false;
			}
		}
		else {
			if (!getFirstName().equals(e.getFirstName())) {
				return false;
			}
		}
		
		if (getLastName() == null) {
			if (e.getLastName() != null) {
				return false;
			}
		}
		else {
			if (!getLastName().equals(e.getLastName())) {
				return false;
			}
		}
		
		if (getBadgeNumber() == null) {
			if (e.getBadgeNumber() != null) {
				return false;
			}
		}
		else {
			if (!getBadgeNumber().equals(e.getBadgeNumber())) {
				return false;
			}
		}
		
		if (getUsername() == null) {
			if (e.getUsername() != null) {
				return false;
			}
		}
		else {
			if (!getUsername().equals(e.getUsername())) {
				return false;
			}
		}
		
		if (getEmployeeType() == null) {
			if (e.getEmployeeType() != null) {
				return false;
			}
		}
		else {
			if (!getEmployeeType().equals(e.getEmployeeType())) {
				return false;
			}
		}
		return true;
	};
	
	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = 233;
			if (getFirstName() != null) {
				hashCode ^= getFirstName().hashCode();
			}
			if (getLastName() != null) {
				hashCode ^= getLastName().hashCode();
			}
			if (getBadgeNumber() != null) {
				hashCode ^= getBadgeNumber().hashCode();
			}
			if (getUsername() != null) {
				hashCode ^= getUsername().hashCode();
			}
			if (getEmployeeType() != null) {
				hashCode ^= getEmployeeType().hashCode();
			}
		}
		return hashCode;
	}
	
	@Override
	public String toString() {
		return getLastName() + ", " + getFirstName();
	}
}
