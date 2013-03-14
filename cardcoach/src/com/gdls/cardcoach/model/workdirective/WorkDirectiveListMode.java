package com.gdls.cardcoach.model.workdirective;

public enum WorkDirectiveListMode {

	FAVORITES("Favorites Only"),
	ALL("All");
	
	private final String name;
	
	private WorkDirectiveListMode(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
