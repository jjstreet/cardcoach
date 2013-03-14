package com.gdls.cardcoach.ui;

import hirondelle.date4j.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import com.gdls.cardcoach.CardCoachContext;
import com.gdls.cardcoach.employee.Employee;
import com.gdls.cardcoach.employee.EmployeeInstantiationException;
import com.gdls.cardcoach.export.TextExporter;
import com.gdls.cardcoach.preference.PreferenceException;
import com.gdls.cardcoach.preference.PreferenceManager;
import com.gdls.cardcoach.timesheet.TimeSheet;
import com.gdls.cardcoach.timesheet.XMLTimeSheetUtil;
import com.gdls.cardcoach.ui.composite.TimeCardComposite;
import com.gdls.cardcoach.ui.composite.TimeSheetComposite;
import com.gdls.cardcoach.ui.composite.WorkDirectiveLibraryComposite;
import com.gdls.cardcoach.ui.dialog.AboutDialog;
import com.gdls.cardcoach.ui.dialog.PreferencesDialog;
import com.gdls.cardcoach.ui.icons.IconHandler;
import com.gdls.cardcoach.update.UpdateSearch;
import com.gdls.cardcoach.workdirective.WorkDirectiveLibrary;
import com.gdls.cardcoach.workdirective.XMLWorkDirectiveLibraryUtil;
import com.gdls.cardcoach.xml.LoadXMLException;
import com.gdls.cardcoach.xml.SaveXMLException;

public class CardCoachGUI implements PreferenceManager.PreferenceChangeListener {
	
	private static final int SHELL_WIDTH = 750;
	
	private CardCoachContext context;
	
	private Color tabLightColor;
	private Color tabDarkColor;
	
	private Display display;
	private Shell shell;
	
	private TrayManager trayManager;
	
	private CTabItem timeSheetItem;
	private CTabItem timeCardItem;
	private CTabItem workDirectiveItem;
	
	private CTabFolder folder;
	
	private TimeSheetComposite timeSheetComposite;
	private WorkDirectiveLibraryComposite workDirectiveLibraryComposite;
	private TimeCardComposite timeCardComposite;
	
	private final NewTimeSheetSelection newTimeSheetSelection;
	private final OpenTimeSheetSelection openTimeSheetSelection;
	private final SaveTimeSheetSelection saveTimeSheetSelection;
	private final SaveTimeSheetSelection saveAsTimeSheetSelection;
	private final PreferencesSelection preferencesSelection;
	private final AuditSelection auditSelection;
	private final ExitSelection exitSelection;
	private final UpdateSelection updateSelection;
	
	public CardCoachGUI(CardCoachContext context) {
		this.context = context;
		
		Display.setAppName(context.getApplicationTitle());
		display = new Display();
		
		PreferenceManager.getInstance().registerPreferenceChangeListener(this);
		newTimeSheetSelection = new NewTimeSheetSelection();
		openTimeSheetSelection = new OpenTimeSheetSelection();
		saveTimeSheetSelection = new SaveTimeSheetSelection(false);
		saveAsTimeSheetSelection = new SaveTimeSheetSelection(true);
		preferencesSelection = new PreferencesSelection();
		auditSelection = new AuditSelection();
		exitSelection = new ExitSelection();
		updateSelection = new UpdateSelection();
	}
	
	public void runEventLoop() {
		shell = open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		if (display.isDisposed()) {
			System.exit(0);
		}
		display.dispose();
	}
	
	private void widgetDisposed(DisposeEvent event) {
		tabLightColor.dispose();
		tabDarkColor.dispose();
		IconHandler.getInstance().disposeInstance("icon.window.64");
		IconHandler.getInstance().disposeInstance("icon.window.48");
		IconHandler.getInstance().disposeInstance("icon.window.32");
		IconHandler.getInstance().disposeInstance("icon.window.16");
		IconHandler.getInstance().disposeInstance("icon.command.timesheet.new");
		IconHandler.getInstance().disposeInstance("icon.command.timesheet.open");
		IconHandler.getInstance().disposeInstance("icon.command.timesheet.save");
		IconHandler.getInstance().disposeInstance("icon.command.timesheet.saveas");
		IconHandler.getInstance().disposeInstance("icon.command.preferences");
		IconHandler.getInstance().disposeInstance("icon.command.exit");
		IconHandler.getInstance().disposeInstance("icon.tab.timesheet");
		IconHandler.getInstance().disposeInstance("icon.tab.timecard");
		IconHandler.getInstance().disposeInstance("icon.tab.workdirectives");
	}
	
	private Shell open() {
		tabLightColor = display.getSystemColor(SWT.COLOR_LIST_SELECTION);
		tabDarkColor = ColorManager.getAltRowSelected();
		createShell();
		createTrayManager();
		createMenus();
		createContents();
		shell.pack();
		shell.setSize(shell.computeSize(SHELL_WIDTH, SWT.DEFAULT));
		shell.open();
		shell.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent event) {
				CardCoachGUI.this.widgetDisposed(event);
			}
		});
		postOpen();
		return shell;
	}
	
	private final void postOpen() {
		// Update application first if user wants it
		if (PreferenceManager.getInstance().getBoolean(PreferenceManager.UPDATE_CHECK_ON_STARTUP, false)) {
			updateSelection.invoke(true);
		}
		try {
			// Get employee information first
			newEmployee();
			newWorkDirectiveLibrary(XMLWorkDirectiveLibraryUtil.loadFromXML(PreferenceManager.getInstance().getString(PreferenceManager.WORKDIRECTIVES_LIST_LOCATION, null)));
			newTimeSheet();
		}
		catch (LoadXMLException e) {
			MessageFactory.showError(shell, e.getMessage());
		}
	}
	
	private void createShell() {
		shell = new Shell(display, SWT.TITLE | SWT.CLOSE | SWT.MIN);
		shell.setText(context.getApplicationTitle());
		final Image[] icons = new Image[] {
				IconHandler.getInstance().getImage("icon.window.64"),
				IconHandler.getInstance().getImage("icon.window.48"),
				IconHandler.getInstance().getImage("icon.window.32"),
				IconHandler.getInstance().getImage("icon.window.16")
		};
		shell.setImages(icons);
		shell.addListener(SWT.Close, new ShellClosureHandler());
		shell.setLayout(new FillLayout());
	}
	
	private boolean closeApp() {
		return closeApp(false);
	}
	
	private boolean closeApp(boolean force) {
		if (!force) {
			if (context.getTimeSheet() != null && context.getTimeSheet().isDirty()) {
				if (MessageFactory.showConfirmation(shell, "There are unsaved changed. Exit without saving?") == SWT.NO) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	private void createTrayManager() {
		trayManager = new TrayManager(context);
		final Menu trayMenu = new Menu(shell, SWT.POP_UP);
		final MenuItem restoreItem = new MenuItem(trayMenu, SWT.PUSH);
		restoreItem.setText("Restore");
		restoreItem.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				shell.setMinimized(false);
				shell.setVisible(true);
			}
		});
		new MenuItem(trayMenu, SWT.SEPARATOR);
		final MenuItem exitItem = new MenuItem(trayMenu, SWT.PUSH);
		exitItem.setText("Exit");
		exitItem.setImage(IconHandler.getInstance().getImage("icon.command.exit"));
		exitItem.addSelectionListener(exitSelection);
		trayManager.setMenu(trayMenu);
		trayManager.createControl(shell);
	}
	
	private void createMenus() {
		final Menu menuBar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menuBar);
		final MenuItem fileItem = new MenuItem(menuBar, SWT.CASCADE);
		fileItem.setText("&File");
		createFileMenu(fileItem);
		final MenuItem auditItem = new MenuItem(menuBar, SWT.CASCADE);
		auditItem.setText("&Auditing");
		createAuditMenu(auditItem);
		final MenuItem helpItem = new MenuItem(menuBar, SWT.CASCADE);
		helpItem.setText("&Help");
		createHelpMenu(helpItem);
	}
	
	private void createFileMenu(MenuItem parent) {
		final Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		parent.setMenu(fileMenu);
		final MenuItem newItem = new MenuItem(fileMenu, SWT.PUSH);
		newItem.setText("&New Timesheet\tCtrl+N");
		newItem.setAccelerator(SWT.CTRL + 'N');
		newItem.setImage(IconHandler.getInstance().getImage("icon.command.timesheet.new"));
		newItem.addSelectionListener(newTimeSheetSelection);
		final MenuItem openItem = new MenuItem(fileMenu, SWT.PUSH);
		openItem.setText("&Open Timesheet...\tCtrl+O");
		openItem.setAccelerator(SWT.CTRL + 'O');
		openItem.setImage(IconHandler.getInstance().getImage("icon.command.timesheet.open"));
		openItem.addSelectionListener(openTimeSheetSelection);
		final MenuItem saveItem = new MenuItem(fileMenu, SWT.PUSH);
		saveItem.setText("&Save Timesheet\tCtrl+S");
		saveItem.setAccelerator(SWT.CTRL + 'S');
		saveItem.setImage(IconHandler.getInstance().getImage("icon.command.timesheet.save"));
		saveItem.setEnabled(false);
		saveItem.addSelectionListener(saveTimeSheetSelection);
		final MenuItem saveAsItem = new MenuItem(fileMenu, SWT.PUSH);
		saveAsItem.setText("&Save Timesheet As...\tCtrl+Shift+S");
		saveAsItem.setAccelerator(SWT.CTRL + SWT.SHIFT + 'S');
		saveAsItem.setImage(IconHandler.getInstance().getImage("icon.command.timesheet.saveas"));
		saveAsItem.setEnabled(false);
		saveAsItem.addSelectionListener(saveAsTimeSheetSelection);
		new MenuItem(fileMenu, SWT.SEPARATOR);
		final MenuItem preferencesItem = new MenuItem(fileMenu, SWT.PUSH);
		preferencesItem.setText("&Preferences...");
		preferencesItem.setImage(IconHandler.getInstance().getImage("icon.command.preferences"));
		preferencesItem.addSelectionListener(preferencesSelection);
		new MenuItem(fileMenu, SWT.SEPARATOR);
		final MenuItem exitItem = new MenuItem(fileMenu, SWT.PUSH);
		exitItem.setText("E&xit");
		exitItem.setImage(IconHandler.getInstance().getImage("icon.command.exit"));
		exitItem.addSelectionListener(exitSelection);
		fileMenu.addMenuListener(new MenuAdapter() {
			
			@Override
			public void menuShown(MenuEvent event) {
				saveItem.setEnabled(false);
				saveAsItem.setEnabled(false);
				newItem.setEnabled(false);
				if (context.getTimeSheet() != null && context.getTimeSheet().isDirty()) {
					saveItem.setEnabled(true);
				}
				if (context.getTimeSheet() != null) {
					saveAsItem.setEnabled(true);
				}
				newItem.setEnabled(context.getEmployee() != null);
			}
		});
	}
	
	private void createAuditMenu(MenuItem parent) {
		final Menu auditMenu = new Menu(shell, SWT.DROP_DOWN);
		parent.setMenu(auditMenu);
		final MenuItem saveLogItem = new MenuItem(auditMenu, SWT.PUSH);
		saveLogItem.setText("Save Audit Log");
		saveLogItem.addSelectionListener(auditSelection);
		auditMenu.addMenuListener(new MenuAdapter() {
		
			@Override
			public void menuShown(MenuEvent event) {
				saveLogItem.setEnabled(context.getTimeSheet() != null);
			}
		});
	}
	
	private void createHelpMenu(MenuItem parent) {
		final Menu helpMenu = new Menu(shell, SWT.DROP_DOWN);
		parent.setMenu(helpMenu);
		final MenuItem checkUpdateItem = new MenuItem(helpMenu, SWT.PUSH);
		checkUpdateItem.setText("Check for Updates");
		checkUpdateItem.addSelectionListener(updateSelection);
		final MenuItem aboutItem = new MenuItem(helpMenu, SWT.PUSH);
		aboutItem.setText("About");
		aboutItem.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				new AboutDialog(context, shell).open();
			}
		});
	}
	
	private void createContents() {
		folder = new CTabFolder(shell, SWT.NONE);
		folder.setSimple(false);
		folder.setTabHeight(24);
		folder.setMinimumCharacters(100);
		folder.setSelectionBackground(new Color[] {
				tabDarkColor,
				tabLightColor},
				new int[] {50}, true);
		folder.setSelectionForeground(display.getSystemColor(SWT.COLOR_WHITE));
		
		timeSheetComposite = new TimeSheetComposite(folder);
		workDirectiveLibraryComposite = new WorkDirectiveLibraryComposite(folder);
		timeCardComposite = new TimeCardComposite(folder);
		
		timeSheetItem = new CTabItem(folder, SWT.NONE);
		timeSheetItem.setText("My Time Sheet");
		timeSheetItem.setImage(IconHandler.getInstance().getImage("icon.tab.timesheet"));
		timeSheetItem.setControl(timeSheetComposite);
		
		timeCardItem = new CTabItem(folder, SWT.NONE);
		timeCardItem.setText("My Time Card");
		timeCardItem.setImage(IconHandler.getInstance().getImage("icon.tab.timecard"));
		timeCardItem.setControl(timeCardComposite);
		
		workDirectiveItem = new CTabItem(folder, SWT.NONE);
		workDirectiveItem.setText("My Work Directives");
		workDirectiveItem.setImage(IconHandler.getInstance().getImage("icon.tab.workdirectives"));
		workDirectiveItem.setControl(workDirectiveLibraryComposite);
		
	}
	
	public void newEmployee() {
		try {
			String type = PreferenceManager.getInstance().getString(PreferenceManager.EMPLOYEE_TYPE, null);
			context.setEmployee(new Employee(
					PreferenceManager.getInstance().getString(PreferenceManager.FIRST_NAME, null),
					PreferenceManager.getInstance().getString(PreferenceManager.LAST_NAME, null),
					PreferenceManager.getInstance().getString(PreferenceManager.BADGE_NUMBER, null),
					PreferenceManager.getInstance().getString(PreferenceManager.USERNAME, null),
					type != null && !type.isEmpty() ? Employee.Type.valueOf(type) : null
					));
		}
		catch (EmployeeInstantiationException e) {
			MessageFactory.showError(shell, "There was a problem retrieving your employee information. Please update your employee information in the preferences.");
			context.setEmployee(null);
		}
	}
	
	
	private class ShellClosureHandler implements Listener {
		@Override
		public void handleEvent(Event event) {
			if (shell.isVisible() && trayManager.isTrayAvailable()) {
				shell.setMinimized(true);
				shell.setVisible(false);
				event.doit = false;
				return;
			}
			if (!closeApp()) {
				event.doit = false;
			}
		}
	}
	
	private class NewTimeSheetSelection extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent event) {
			if (context.getTimeSheet() == null || !context.getTimeSheet().isDirty() || MessageFactory.showConfirmation(shell, "Current time sheet has been modified. Lose Changes?") == SWT.YES) {
				newTimeSheet();
				PreferenceManager.getInstance().putString(PreferenceManager.TIMESHEET_LAST_FILE_NAME, "");
			}
		}
	}
	
	private class OpenTimeSheetSelection extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent event) {
			if (context.getTimeSheet() == null || !context.getTimeSheet().isDirty() || MessageFactory.showConfirmation(shell, "Current time sheet has been modified. Lose changes and open a different time sheet?") == SWT.YES) {
				String lastPath = PreferenceManager.getInstance().getString(PreferenceManager.TIMESHEET_LAST_FILE_DIR, System.getProperty("user.home", "C:\\"));
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Open Timesheet");
				fd.setFilterPath(lastPath);
				String[] filterExtNames = {"Time Sheets (*.xml)"};
				String[] filterExts = {"*.xml"};
				fd.setFilterExtensions(filterExts);
				fd.setFilterNames(filterExtNames);
				String location = fd.open();
				if (location != null) {
					try {
						TimeSheet newTimeSheet = XMLTimeSheetUtil.loadFromXML(location);
						newTimeSheet(newTimeSheet);
						PreferenceManager.getInstance().putString(PreferenceManager.TIMESHEET_LAST_FILE_DIR, new File(location).getParent());
						PreferenceManager.getInstance().putString(PreferenceManager.TIMESHEET_LAST_FILE_NAME, new File(location).getName());
					}
					catch (LoadXMLException e) {
						MessageFactory.showError(shell, e.getMessage());
					}
				}
			}
		}
	}
	
	private class SaveTimeSheetSelection extends SelectionAdapter {
		
		private final boolean isSaveAs;
		
		public SaveTimeSheetSelection(boolean isSaveAs) {
			this.isSaveAs = isSaveAs;
		}
		
		@Override
		public void widgetSelected(SelectionEvent event) {
			invoke();
		}
		
		public void invoke() {
			if (isSaveAs || PreferenceManager.getInstance().getString(PreferenceManager.TIMESHEET_LAST_FILE_NAME, "").isEmpty()) {
				final String lastPath = PreferenceManager.getInstance().getString(PreferenceManager.TIMESHEET_LAST_FILE_DIR, null);
				final FileDialog fd = new FileDialog(shell, SWT.SAVE);
				fd.setText("Save Timesheet");
				fd.setFilterPath(lastPath);
				String[] filterExtNames = {"Time Sheets (*.xml)"};
				String[] filterExts = {"*.xml"};
				fd.setFilterExtensions(filterExts);
				fd.setFilterNames(filterExtNames);
				fd.setFileName(PreferenceManager.getInstance().getString(PreferenceManager.TIMESHEET_LAST_FILE_NAME, null));
				fd.setOverwrite(true);
				String location = fd.open();
				while (location != null) {
					try {
						XMLTimeSheetUtil.saveToXML(context.getTimeSheet(), location);
						File savedFile = new File(location);
						PreferenceManager.getInstance().putString(PreferenceManager.TIMESHEET_LAST_FILE_DIR, savedFile.getParent());
						PreferenceManager.getInstance().putString(PreferenceManager.TIMESHEET_LAST_FILE_NAME, savedFile.getName());
						break;
					} catch (SaveXMLException e) {
						MessageFactory.showError(shell, e.getMessage());
					}
					location = fd.open();
				}
			}
			else {
				try {
					String location = PreferenceManager.getInstance().getString(PreferenceManager.TIMESHEET_LAST_FILE_DIR, "") + File.separator +
					PreferenceManager.getInstance().getString(PreferenceManager.TIMESHEET_LAST_FILE_NAME, "");
					XMLTimeSheetUtil.saveToXML(context.getTimeSheet(), location);
				}
				catch (SaveXMLException e) {
					MessageFactory.showError(shell, e.getMessage());
				}
			}
		}
	}
	
	private class PreferencesSelection extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent event) {
			new PreferencesDialog(context, shell).open();
			try {
				PreferenceManager.getInstance().savePreferences();
			}
			catch (PreferenceException e) {
				MessageFactory.showError(shell, e.getMessage());
			}
			newEmployee();
		}
	}
	
	private class ExitSelection extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent event) {
			if (closeApp()) {
				shell.dispose();
			}
		}
	}
	
	private class AuditSelection extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent event) {
			FileDialog fd = new FileDialog(shell, SWT.SAVE);
			fd.setText("Save Audit Log");
			String[] filterExtNames = {"Text Files (*.txt)"};
			String[] filterExts = {"*.txt"};
			fd.setFilterExtensions(filterExts);
			fd.setFilterNames(filterExtNames);
			fd.setFilterPath(System.getProperty("user.home"));
			fd.setFileName(DateTime.now(TimeZone.getDefault()).format("YYYY-MM-DD h12-mm-ss a", Locale.getDefault()));
			fd.setOverwrite(true);
			String location = fd.open();
			while (location != null) {
				TextExporter.exportTo(location, context.getTimeSheet());
				MessageFactory.showMessage(shell, "Audit log saved successfully");
				return;
			}
		}
	}
	
	private class UpdateSelection extends SelectionAdapter {
		
		public UpdateSelection() {
			
		}
		
		@Override
		public void widgetSelected(SelectionEvent event) {
			invoke(false);
		}
		
		public void invoke(boolean silent) {
			String latestVersion = UpdateSearch.getLatestVersion(PreferenceManager.getInstance().getString(PreferenceManager.UPDATES_LOCATION, null), context.getApplicationVersion());
			if (!context.getApplicationVersion().equals(latestVersion)) {
				if (MessageFactory.showConfirmation(shell, "Version " + latestVersion + " is available. Would you like to update? " +
						"You will be asked to save any unsaved changes.") == SWT.YES) {
					if (context.getTimeSheet() != null && context.getTimeSheet().isDirty()) {
						int res = MessageFactory.showConfirmation(shell, SWT.YES | SWT.NO | SWT.CANCEL, "Save changes before updating?");
						switch (res) {
						case SWT.CANCEL:
							return;
						case SWT.YES:
							saveTimeSheetSelection.invoke();
						}
					}
					try {
						shell.dispose();
						Runtime.getRuntime().exec("\"" + PreferenceManager.getInstance().getString(PreferenceManager.UPDATES_LOCATION, "") + "\\cardcoach setup-" + latestVersion + ".exe\" /S /INSTALLTYPE=update /RUNAFTER=yes");
					}
					catch (IOException e) {
						MessageFactory.showError(shell, "Could not start installer!");
					}
				}
			} else {
				if (!silent) {
					MessageFactory.showMessage(shell, "There are no updates.");
				}
			}
		}
	}
	
	private void newTimeSheet() {
		if (context.getEmployee() != null) {
			newTimeSheet(TimeSheet.newInstance(context.getEmployee()));
		}
		else {
			newTimeSheet(null);
		}
	}
	
	private void newTimeSheet(final TimeSheet timeSheet) {
		// Set this shell's time sheet
		// Set immediate composites' time sheets that need it
		context.setTimeSheet(timeSheet);
		timeSheetComposite.setTimeSheet(context.getTimeSheet());
		timeCardComposite.setTimeSheet(context.getTimeSheet());
	}
	
	private void newWorkDirectiveLibrary(WorkDirectiveLibrary library) {
		context.setWorkDirectiveLibrary(library);
		if (context.getWorkDirectiveLibrary() != null) {
			context.getWorkDirectiveLibrary().updateFavorites(PreferenceManager.getInstance().getStringArray(PreferenceManager.WORKDIRECTIVES_FAVORITES));
		}
		timeSheetComposite.setLibrary(context.getWorkDirectiveLibrary());
		workDirectiveLibraryComposite.setLibrary(context.getWorkDirectiveLibrary());
	}

	@Override
	public void preferenceChanged(String key) {
		if (PreferenceManager.WORKDIRECTIVES_LIST_LOCATION.equalsIgnoreCase(key)) {
			try {
				newWorkDirectiveLibrary(XMLWorkDirectiveLibraryUtil.loadFromXML(PreferenceManager.getInstance().getString(key, "")));
			}
			catch (LoadXMLException e) {
				MessageFactory.showError(shell, e.getMessage());
				newWorkDirectiveLibrary(null);
			}
		}
		if (PreferenceManager.WORKDIRECTIVES_FAVORITES.equalsIgnoreCase(key)) {
			context.getWorkDirectiveLibrary().updateFavorites(PreferenceManager.getInstance().getStringArray(key));
		}
	}
}
