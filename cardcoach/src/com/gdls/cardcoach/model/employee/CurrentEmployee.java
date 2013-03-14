package com.gdls.cardcoach.model.employee;

public enum CurrentEmployee {
	
	INSTANCE;
	
	private Employee employee;
	
	private CurrentEmployee() {
		this.employee = new Employee();
	}
	
	public Employee getEmployee() {
		return employee;
	}

}
