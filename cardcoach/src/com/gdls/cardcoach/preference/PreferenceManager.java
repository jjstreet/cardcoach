package com.gdls.cardcoach.preference;

import java.util.HashMap;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class PreferenceManager {

	public interface PreferenceChangeListener {
		void preferenceChanged(String key);
	}
	
	private static PreferenceManager instance;
	
	private static final Preferences ROOT_NODE = Preferences.userRoot().node("cardcoach");
	private static final Preferences PREFERENCES_NODE = ROOT_NODE.node("preferences");
	private static final Preferences APPLICATION_NODE = PREFERENCES_NODE.node("application");
	private static final Preferences TIMESHEET_NODE = PREFERENCES_NODE .node("timesheet");
	private static final Preferences WORKDIRECTIVES_NODE = PREFERENCES_NODE.node("workdirectives");
	private static final Preferences EMPLOYEE_NODE = PREFERENCES_NODE.node("employee");
	
	public static final String TIMESHEET_LAST_FILE_DIR = "lastfiledir";
	public static final String TIMESHEET_LAST_FILE_NAME = "lastfilename";
	public static final String WORKDIRECTIVES_LIST_LOCATION = "listlocation";
	public static final String WORKDIRECTIVES_FAVORITES = "favorites";
	public static final String DAY_MINDER_ENABLED = "dayminderenabled";
	public static final String UPDATES_LOCATION = "updateslocation";
	public static final String UPDATE_CHECK_ON_STARTUP = "updatecheckonstartup";
	public static final String FIRST_NAME = "firstname";
	public static final String LAST_NAME = "lastname";
	public static final String BADGE_NUMBER = "badgenumber";
	public static final String USERNAME = "username";
	public static final String EMPLOYEE_TYPE = "employeetype";
	
	private HashMap<String, String> instancePrefs;
	
	private Vector<PreferenceChangeListener> preferenceChangeListeners;
	
	private PreferenceManager() {
		instancePrefs = new HashMap<String, String>();
		preferenceChangeListeners = new Vector<PreferenceChangeListener>();
	}
	
	public static PreferenceManager getInstance() {
		if (instance == null) {
			instance = new PreferenceManager();
			instance.loadPreferences();
		}
		return instance;
	}
	
	public void loadPreferences() {
		instancePrefs.put(WORKDIRECTIVES_LIST_LOCATION, WORKDIRECTIVES_NODE.get(WORKDIRECTIVES_LIST_LOCATION, ""));
		instancePrefs.put(WORKDIRECTIVES_FAVORITES, WORKDIRECTIVES_NODE.get(WORKDIRECTIVES_FAVORITES, ""));
		instancePrefs.put(TIMESHEET_LAST_FILE_DIR, TIMESHEET_NODE.get(TIMESHEET_LAST_FILE_DIR, System.getProperty("user.home", "C:/")));
		instancePrefs.put(TIMESHEET_LAST_FILE_NAME, "");
		instancePrefs.put(DAY_MINDER_ENABLED, APPLICATION_NODE.get(DAY_MINDER_ENABLED, "false"));
		instancePrefs.put(UPDATES_LOCATION, APPLICATION_NODE.get(UPDATES_LOCATION, ""));
		instancePrefs.put(UPDATE_CHECK_ON_STARTUP, APPLICATION_NODE.get(UPDATE_CHECK_ON_STARTUP, "true"));
		instancePrefs.put(FIRST_NAME, EMPLOYEE_NODE.get(FIRST_NAME, ""));
		instancePrefs.put(LAST_NAME, EMPLOYEE_NODE.get(LAST_NAME, ""));
		instancePrefs.put(BADGE_NUMBER, EMPLOYEE_NODE.get(BADGE_NUMBER, ""));
		instancePrefs.put(USERNAME, EMPLOYEE_NODE.get(USERNAME, ""));
		instancePrefs.put(EMPLOYEE_TYPE, EMPLOYEE_NODE.get(EMPLOYEE_TYPE, null));
	}
	
	public void savePreferences() throws PreferenceException {
		try {
			TIMESHEET_NODE.put(TIMESHEET_LAST_FILE_DIR, instancePrefs.get(TIMESHEET_LAST_FILE_DIR));
			WORKDIRECTIVES_NODE.put(WORKDIRECTIVES_FAVORITES, instancePrefs.get(WORKDIRECTIVES_FAVORITES));
			WORKDIRECTIVES_NODE.put(WORKDIRECTIVES_LIST_LOCATION, instancePrefs.get(WORKDIRECTIVES_LIST_LOCATION));
			APPLICATION_NODE.put(DAY_MINDER_ENABLED, instancePrefs.get(DAY_MINDER_ENABLED));
			APPLICATION_NODE.put(UPDATE_CHECK_ON_STARTUP, instancePrefs.get(UPDATE_CHECK_ON_STARTUP));
			EMPLOYEE_NODE.put(FIRST_NAME, instancePrefs.get(FIRST_NAME));
			EMPLOYEE_NODE.put(LAST_NAME, instancePrefs.get(LAST_NAME));
			EMPLOYEE_NODE.put(BADGE_NUMBER, instancePrefs.get(BADGE_NUMBER));
			EMPLOYEE_NODE.put(USERNAME, instancePrefs.get(USERNAME));
			EMPLOYEE_NODE.put(EMPLOYEE_TYPE, instancePrefs.get(EMPLOYEE_TYPE));
			ROOT_NODE.flush();
		} catch (BackingStoreException e) {
			throw new PreferenceException("Unable to store preferences: " + e.getMessage());
		}
	}
	
	public boolean getBoolean(String key, boolean def) {
		if (instancePrefs.containsKey(key)) {
			return Boolean.parseBoolean(instancePrefs.get(key));
		}
		return def;
	}
	
	public void putBoolean(String key, boolean value) {
		if (instancePrefs.containsKey(key)) {
			instancePrefs.put(key, Boolean.toString(value));
			callPreferenceChanged(key);
		}
	}
	
	public String getString(String key, String def) {
		if (instancePrefs.containsKey(key)) {
			return instancePrefs.get(key);
		}
		return def;
	}
	
	public void putString(String key, String value) {
		if (instancePrefs.containsKey(key)) {
			instancePrefs.put(key, value);
			callPreferenceChanged(key);
		}
	}
	
	public String[] getStringArray(String key) {
		if (instancePrefs.containsKey(key)) {
			return instancePrefs.get(key).split(",");
		}
		return null;
	}
	
	public void putStringArray(String key, String[] values) {
		if (instancePrefs.containsKey(key)) {
			StringBuilder builder = new StringBuilder();
			for (String value : values) {
				builder.append(value);
				builder.append(",");
			}
			instancePrefs.put(key, builder.toString());
			callPreferenceChanged(key);
		}
	}
	
	private void callPreferenceChanged(String key) {
		for (PreferenceChangeListener listener : preferenceChangeListeners) {
			listener.preferenceChanged(key);
		}
	}
	
	public void registerPreferenceChangeListener(PreferenceChangeListener listener) {
		preferenceChangeListeners.add(listener);
	}
	
	public void unregisterPreferenceChangeListener(PreferenceChangeListener listener) {
		preferenceChangeListeners.remove(listener);
	}
}
