package com.gdls.cardcoach.model.workdirective;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gdls.cardcoach.model.ModelObject;
import com.gdls.cardcoach.model.account.Account;
import com.gdls.cardcoach.model.employee.EmployeeType;
import com.gdls.cardcoach.model.taskcode.Taskcode;

public class WorkDirective extends ModelObject implements Comparable<WorkDirective> {
	
	private WorkDirectiveFolder workDirectiveFolder;
	private String fullPath;
	
	private Integer favoriteId;
	
	private boolean editable;
	
	private String number;
	private String suspenseNumber;
	private String description;
	private boolean active;
	private String location;
	
	private Map<EmployeeType, Set<Account>> accounts;
	
	private Set<Taskcode> taskcodes;
	private boolean limitedToTaskcodeList;
	
	public WorkDirective() {
		accounts = new HashMap<EmployeeType, Set<Account>>();
		taskcodes = new HashSet<Taskcode>();
		active = true;
		editable = true;
	}
	
	public String getNumber() {
		return number;
	}
	
	public String getSuspenseNumber() {
		return suspenseNumber;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public String getLocation() {
		return location;
	}
	
	public List<Account> getAccounts(EmployeeType employeeType) {
		if (accounts.get(employeeType) != null) {
			List<Account> accountList = new ArrayList<Account>();
			Collections.sort(accountList);
			return accountList;
		}
		return Collections.emptyList();
	}
	
	public List<Taskcode> getTaskCodes() {
		List<Taskcode> taskcodeList = new ArrayList<Taskcode>(taskcodes);
		Collections.sort(taskcodeList);
		return taskcodeList;
	}
	
	public boolean isLimitedToTaskcodeList() {
		return limitedToTaskcodeList;
	}
	
	public WorkDirectiveFolder getWorkDirectiveFolder() {
		return workDirectiveFolder;
	}
	
	public String getFullPath() {
		return fullPath;
	}
	
	public Integer getFavoriteId() {
		return favoriteId;
	}
	
	public boolean isEditable() {
		return editable;
	}
	
	public void setNumber(String number) {
		firePropertyChange("number", this.number, this.number = number);
	}
	
	public void setSuspenseNumber(String suspenseNumber) {
		firePropertyChange("suspenseNumber", this.suspenseNumber, this.suspenseNumber = suspenseNumber);
	}
	
	public void setDescription(String description) {
		firePropertyChange("description", this.description, this.description = description);
	}
	
	public void setActive(boolean active) {
		firePropertyChange("active", this.active, this.active = active);
	}
	
	public void setLocation(String location) {
		firePropertyChange("favoriteId", this.location, this.location = location);
	}
	
	public void setAccounts(EmployeeType employeeType, Collection<Account> accounts) {
		this.accounts.put(employeeType, new HashSet<Account>(accounts));
		firePropertyChange("accounts", null, null);
	}
	
	public void addAccount(EmployeeType employeeType, Account account) { 
		Set<Account> accountSet = accounts.get(employeeType);
		if (accountSet == null) {
			accountSet = new HashSet<Account>();
			accounts.put(employeeType, accountSet);
		}
		if (accountSet.add(account)) {
			firePropertyChange("accounts", null, null);
		}
	}
	
	public void removeAccount(EmployeeType employeeType, Account account) {
		Set<Account> accountSet = accounts.get(employeeType);
		if (accountSet == null) {
			return;
		}
		if (accountSet.remove(account)) {
			if (accountSet.isEmpty()) {
				accounts.remove(employeeType);
			}
			firePropertyChange("accounts", null, null);
		}
	}
	
	public void setTaskcodes(Collection<Taskcode> taskcodes) {
		this.taskcodes = new HashSet<Taskcode>(taskcodes);
		firePropertyChange("taskcodes", null, null);
	}
	
	public void addTaskcode(Taskcode taskcode) {
		if (taskcodes.add(taskcode)) {
			firePropertyChange("taskcodes", null, null);
		}
	}
	
	public void removeTaskcode(Taskcode taskcode) {
		if (taskcodes.remove(taskcode)) {
			firePropertyChange("taskcodes", null, null);
		}
	}
	
	public void setLimitedToTaskcodeList(boolean limitedToTaskcodeList) {
		this.limitedToTaskcodeList = limitedToTaskcodeList;
	}
	
	public void setWorkDirectiveFolder(WorkDirectiveFolder workDirectiveFolder) {
		firePropertyChange("workDirectiveFolder", this.workDirectiveFolder, this.workDirectiveFolder = workDirectiveFolder);
	}
	
	public void setFullPath(String fullPath) {
		firePropertyChange("fullPath", this.fullPath, this.fullPath = fullPath);
	}
	
	public void setFavoriteId(Integer favoriteId) {
		firePropertyChange("favoriteId", this.favoriteId, this.favoriteId = favoriteId);
	}
	
	public void setFavoriteId(int favoriteId) {
		setFavoriteId(Integer.valueOf(favoriteId));
	}
	
	public void setEditable(boolean editable) {
		firePropertyChange("favoriteId", this.favoriteId, this.favoriteId = favoriteId);
	}

	@Override
	public int compareTo(WorkDirective wd) {
		List<String> folderOrder = WorkDirectiveFolders.getInstance().getFolderPaths();
		int numberComparison;
		if (getFavoriteId() == null && wd.getFavoriteId() == null) {
			numberComparison = getNumber().compareTo(wd.getNumber());
			if (numberComparison == 0) {
				if (folderOrder.indexOf(getWorkDirectiveFolder().getFullPath()) >
					folderOrder.indexOf(wd.getWorkDirectiveFolder().getFullPath())) {
					return 1;
				}
				else {
					return -1;
				}
			}
			else {
				return numberComparison / Math.abs(numberComparison);
			}
		}
		else if (getFavoriteId() == null && wd.getFavoriteId() != null) {
			return -1;
		}
		else if (getFavoriteId() != null && wd.getFavoriteId() == null) {
			return 1;
		}
		else {
			numberComparison = getFavoriteId().compareTo(wd.getFavoriteId());
			return numberComparison / Math.abs(numberComparison);
		}
	}

	
}
