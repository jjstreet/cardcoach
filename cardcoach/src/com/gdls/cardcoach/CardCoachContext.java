package com.gdls.cardcoach;

import com.gdls.cardcoach.employee.Employee;
import com.gdls.cardcoach.timesheet.TimeSheet;
import com.gdls.cardcoach.workdirective.WorkDirectiveLibrary;

public interface CardCoachContext {
	
	public String getApplicationVersion();

	public String getApplicationTitle();
	
	public String getApplicationSubtitle(String subtitle);
	
	public Employee getEmployee();
	
	public void setEmployee(Employee employee);
	
	public TimeSheet getTimeSheet();
	
	public void setTimeSheet(TimeSheet timeSheet);
	
	public WorkDirectiveLibrary getWorkDirectiveLibrary();
	
	public void setWorkDirectiveLibrary(WorkDirectiveLibrary library);
}
