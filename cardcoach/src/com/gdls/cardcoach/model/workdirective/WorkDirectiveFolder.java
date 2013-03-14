package com.gdls.cardcoach.model.workdirective;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import com.gdls.cardcoach.model.ModelObject;


public class WorkDirectiveFolder extends ModelObject implements Comparable<WorkDirectiveFolder> {

	private volatile int hashCode = 0;
	
	private String fullPath;
	
	private boolean initialized;
	
	private Set<WorkDirective> workDirectives;
	
	public WorkDirectiveFolder(String fullPath) {
		this.fullPath = fullPath;
		workDirectives = new TreeSet<WorkDirective>();
	}
	
	public String getFullPath() {
		return fullPath;
	}
	
	public File getAbsoluteFile() {
		return new File(fullPath).getAbsoluteFile();
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public void setInitialized(boolean initialized) {
		firePropertyChange("initialized", this.initialized, this.initialized = initialized);
	}
	
	public void addWorkDirective(WorkDirective workDirective) {
		workDirective.setWorkDirectiveFolder(this);
		if (workDirectives.add(workDirective)) {
			WorkDirectiveList.getInstance().addWorkDirective(workDirective);
			firePropertyChange("workDirectives", null, null);
		}
	}
	
	void doSearch() {
		
	}

	@Override
	public int compareTo(WorkDirectiveFolder o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
