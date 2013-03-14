package com.gdls.cardcoach.model.workdirective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.gdls.cardcoach.model.ModelObject;

public class WorkDirectiveList extends ModelObject {
	
	private static WorkDirectiveList instance;
	private Set<WorkDirective> workDirectives;
	
	public static WorkDirectiveList getInstance() {
		if (instance == null) {
			instance = new WorkDirectiveList();
		}
		return instance;
	}
	
	private WorkDirectiveList() {
		workDirectives = new HashSet<WorkDirective>();
	}
	
	public void addWorkDirective(WorkDirective workDirective) {
		if (workDirectives.add(workDirective)) {
			firePropertyChange("workDirectives", null, null);
		}
	}
	
	public void removeWorkDirective(WorkDirective workDirective) {
		if (workDirectives.remove(workDirective)) {
			firePropertyChange("workDirectives", null, null);
		}
	}
	
	public boolean contains(WorkDirective workDirective) {
		return workDirectives.contains(workDirective);
	}
	
	public WorkDirective getWorkDirective(String fullPath) {
		for (WorkDirective workDirective : workDirectives) {
			if (fullPath.equals(workDirective.getFullPath())) {
				return workDirective;
			}
		}
		return null;
	}
	
	public List<WorkDirective> getWorkDirectives() {
		return getWorkDirectives(WorkDirectiveListMode.ALL);
	}
	
	public List<WorkDirective> getWorkDirectives(WorkDirectiveListMode workDirectiveListMode) {
		List<WorkDirective> workDirectiveList = new ArrayList<WorkDirective>(workDirectives);
		if (workDirectiveListMode.equals(WorkDirectiveListMode.FAVORITES)) {
			Iterator<WorkDirective> iter = workDirectiveList.iterator();
			while (iter.hasNext()) {
				if (iter.next().getFavoriteId() == null) {
					iter.remove();
				}
			}
		}
		Collections.sort(workDirectiveList);
		return workDirectiveList;
	}
}
