package com.gdls.cardcoach.workdirective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

public class WorkDirectiveLibrary {
	
	private Map<String, WorkDirective> workDirectives;
	private List<WorkDirectiveLibraryChangeListener> listeners;
	private boolean isDirty;
	private boolean isReadOnly;
	
	public enum Mode {FAVORITES_ONLY, NO_FAVORITES, ALL};
	
	public WorkDirectiveLibrary() {
		workDirectives = new HashMap<String, WorkDirective>();
		listeners = new Vector<WorkDirectiveLibraryChangeListener>();
		isDirty = false;
	}
	
	public boolean isReadOnly() {
		return isReadOnly;
	}
	
	public boolean isDirty() {
		return isDirty;
	}
	
	void setDirtyState(boolean state) {
		isDirty = state;
	}
	
	void setReadOnlyState(boolean state) {
		isReadOnly = state;
	}
	
	public void add(WorkDirective workDirective) {
		add(workDirective, true);
	}

	public void add(WorkDirective workDirective, boolean notify) {
		if (!workDirectives.containsKey(workDirective.getNumber())) {
			workDirectives.put(workDirective.getNumber(), new WorkDirective(workDirective.getNumber(), workDirective.getAccounts(), workDirective.getDescription(), workDirective.getSuspenseNumber()));
			isDirty = true;
			if (notify) {
				notifyListeners();
			}
		}
	}
	
	public void remove(String number) {
		remove(number, true);
	}
	
	public void remove(String number, boolean notify) {
		if (number != null) {
			if (workDirectives.containsKey(number)) {
				workDirectives.remove(number);
				isDirty = true;
				if (notify) {
					notifyListeners();
				}
			}
		}
	}
	
	public void remove(String[] numbers) {
		remove(numbers, true);
	}
	
	public void remove(String[] numbers, boolean notify) {
		for (String number : numbers) {
			remove(number, false);
		}
		if (notify) {
			notifyListeners();
		}
	}
	
	public void update(String number, WorkDirective workDirective) {
		update(number, workDirective, true);
	}
	
	public void update(String number, WorkDirective workDirective, boolean notify) {
		if (number != null) {
			if (workDirectives.containsKey(number)) {
				WorkDirective wd = workDirectives.get(number);
				if (workDirective.getAccounts() != null) {
					wd.setAccounts(workDirective.getAccounts());
				}
				if (workDirective.getDescription() != null) {
					wd.setDescription(workDirective.getDescription());
				}
				if (workDirective.getSuspenseNumber() != null) {
					wd.setSuspenseNumber(workDirective.getSuspenseNumber());
				}
				isDirty = true;
				if (notify) {
					notifyListeners();
				}
			}
		}
	}
	
	public void update(String[] numbers, WorkDirective workDirective) {
		update(numbers, workDirective, true);
	}
	
	public void update(String[] numbers, WorkDirective workDirective, boolean notify) {
		for (String number : numbers) {
			update(number, workDirective, false);
		}
		if (notify) {
			notifyListeners();
		}
	}
	
	public WorkDirective getWorkDirective(String number) {
		if (number != null) {
			if (workDirectives.containsKey(number)) {
				WorkDirective wd = workDirectives.get(number);
				ArrayList<Account> accounts = new ArrayList<Account>(wd.getAccounts().size());
				for (Account account : wd.getAccounts()) {
					accounts.add(new Account(account.getNumber(), account.getNumberNextWeek(), account.getEmployeeType()));
				}
				return new WorkDirective(wd.getNumber(), accounts, wd.getDescription(), wd.getSuspenseNumber(), wd.getFavoriteId());
			}
		}
		return null;
	}
	
	List<WorkDirective> getWorkDirectives() {
		List<WorkDirective> list = new ArrayList<WorkDirective>();
		for (WorkDirective wd : workDirectives.values()) {
			list.add(wd);
		}
		return Collections.unmodifiableList(list);
	}
	
	private Set<WorkDirective> getSortedWorkDirectives(Mode mode) {
		Set<WorkDirective> newSet = new TreeSet<WorkDirective>();
		for (WorkDirective workDirective : workDirectives.values()) {
			if (Mode.FAVORITES_ONLY.equals(mode)) {
				if (workDirective.getFavoriteId() != null) {
					newSet.add(workDirective);
				}
			} else if (Mode.NO_FAVORITES.equals(mode)) {
				if (workDirective.getFavoriteId() == null) {
					newSet.add(workDirective);
				}
			} else {
				newSet.add(workDirective);
			}
		}
		return newSet;
	}
	
	public String[] getWorkDirectiveNumbers(Mode mode) {
		Set<WorkDirective> directives = getSortedWorkDirectives(mode);
		String[] workDirectiveNumbers = new String[directives.size()];
		WorkDirective[] wdArray = directives.toArray(new WorkDirective[directives.size()]);
		for (int i = 0; i < directives.size(); i++) {
			workDirectiveNumbers[i] = wdArray[i].getNumber();
		}
		return workDirectiveNumbers;
	}
	
	public void updateFavorites(String[] favorites) {
		updateFavorites(favorites, true);
	}
	
	public void updateFavorites(String[] favorites, boolean notify) {
		final int flen = favorites.length;
		int i;
		for (String number : workDirectives.keySet()) {
			workDirectives.get(number).setFavoriteId(null);
			for (i = 0; i<flen; i++) {
				if (favorites[i] != "") {
					if (number.equals(favorites[i])) {
						workDirectives.get(number).setFavoriteId(i);
						break;
					}
				}
			}
		}
		if (notify) {
			notifyListeners();
		}
	}
	
	public boolean containsWorkDirective(String number) {
		return workDirectives.containsKey(number);
	}
	
	void setWorkDirectives(Map<String, WorkDirective> map) {
		workDirectives = map;
	}
	
	private void notifyListeners() {
		for (WorkDirectiveLibraryChangeListener listener : listeners) {
			listener.libraryChanged(this);
		}
	}
	
	public void addWorkDirectiveLibraryChangedListener(WorkDirectiveLibraryChangeListener listener) {
		listeners.add(listener);
	}
	
	public void removeWorkDirectiveLibraryChangedListener(WorkDirectiveLibraryChangeListener listener) {
		listeners.remove(listener);
	}
}
