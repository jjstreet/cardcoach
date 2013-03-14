package com.gdls.cardcoach.employee;

/**
 * The employee class is responsible for handling information about the employee
 * and for storing that information so that it can be used to select accounts
 * for work directives.
 */
public class Employee {
	
	public enum Type {DIRECT, UNION, CONTRACTOR};
	
	private String firstName;
	private String lastName;
	private String badgeNumber;
	private String username;
	private Type type;
	
	public Employee(String firstName, String lastName, String badgeNumber, String username, Type type) throws EmployeeInstantiationException {
		if (firstName == null || firstName.isEmpty()) {
//			throw new IllegalArgumentException("firstName must be non-empty string"); //$NON-NLS-1$
			throw new EmployeeInstantiationException();
		}
		if (lastName == null || lastName.isEmpty()) {
//			throw new IllegalArgumentException("lastName must be non-empty string"); //$NON-NLS-1$
			throw new EmployeeInstantiationException();
		}
		if (badgeNumber == null || badgeNumber.isEmpty()) {
//			throw new IllegalArgumentException("firstName must be non-empty string"); //$NON-NLS-1$
			throw new EmployeeInstantiationException();
		}
		if (firstName == null || firstName.isEmpty()) {
//			throw new IllegalArgumentException("firstName must be non-empty string"); //$NON-NLS-1$
			throw new EmployeeInstantiationException();
		}
		if (username == null || username.isEmpty()) {
//			throw new IllegalArgumentException("username must be non-empty string"); //$NON-NLS-1$
			throw new EmployeeInstantiationException();
		}
		if (type == null) {
//			throw new IllegalArgumentException("type must not be null"); //$NON-NLS-1$
			throw new EmployeeInstantiationException();
		}
		this.firstName = firstName;
		this.lastName = lastName;
		this.badgeNumber = badgeNumber;
		this.username = username;
		this.type = type;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	/**
	 * Gets this employee's full name
	 */
	public String getFullName() {
		return firstName + " " + lastName;
	}
	
	/**
	 * Gets this employee's badge number
	 */
	public String getBadgeNumber() {
		return badgeNumber;
	}
	
	/**
	 * Gets this employee's badge number
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Gets this employee's badge number
	 */
	public Type getType() {
		return type;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!other.getClass().equals(Employee.class)) {
			return false;
		}
		Employee emp = (Employee) other;
		if (!firstName.equalsIgnoreCase(emp.firstName)) {
			return false;
		}
		if (!lastName.equalsIgnoreCase(emp.lastName)) {
			return false;
		}
		if (!badgeNumber.equalsIgnoreCase(emp.badgeNumber)) {
			return false;
		}
		if (!username.equalsIgnoreCase(emp.username)) {
			return false;
		}
		if (!type.equals(emp.type)) {
			return false;
		}
		return true;
	}

}
