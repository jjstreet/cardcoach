package com.gdls.cardcoach.model.workdirective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gdls.cardcoach.model.ModelObject;

public class WorkDirectiveFolders extends ModelObject {

	private static WorkDirectiveFolders instance;
	private List<WorkDirectiveFolder> workDirectiveFolders;
	private List<String> folderPaths;
	
	public static WorkDirectiveFolders getInstance() {
		if (instance == null) {
			instance = new WorkDirectiveFolders();
		}
		return instance;
	}
	
	private WorkDirectiveFolders() {
		workDirectiveFolders = new ArrayList<WorkDirectiveFolder>();
		folderPaths = new ArrayList<String>();
	}
	
	public List<String> getFolderPaths() {
		return Collections.unmodifiableList(folderPaths);
	}
	
	public void addWorkDirectiveFolder(WorkDirectiveFolder folder) {
		if (!folderPaths.contains(folder.getFullPath())) {
			workDirectiveFolders.add(folder);
			if (!folder.isInitialized()) {
				folder.doSearch();
				folder.setInitialized(true);
			}
			firePropertyChange("workDirectiveFolders", null, null);
		}
	}
}
