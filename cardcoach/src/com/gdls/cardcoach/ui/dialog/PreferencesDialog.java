package com.gdls.cardcoach.ui.dialog;


import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.gdls.cardcoach.CardCoachContext;
import com.gdls.cardcoach.employee.Employee;
import com.gdls.cardcoach.preference.PreferenceManager;
import com.gdls.cardcoach.ui.FontManager;
import com.gdls.cardcoach.ui.MessageFactory;
import com.gdls.cardcoach.ui.Positioner;
import com.gdls.cardcoach.ui.WidgetBuilder;
import com.gdls.cardcoach.ui.composite.DialogControlsBuilder;
import com.gdls.cardcoach.ui.event.DialogSelectionListener;

public class PreferencesDialog {
	
	private CardCoachContext context;
	
	private Display display;
	private Shell parent;
	private Shell shell;
	
	private Tree preferencesTree;
	
	private Composite stackComposite;
	
	private Label stackTitle;
	
	private Composite empComposite;
	private Label empInstructions;
	private Label empFirstNameLabel;
	private Text empFirstName;
	private Label empLastNameLabel;
	private Text empLastName;
	private Label empBadgeNumberLabel;
	private Text empBadgeNumber;
	private Label empUsernameLabel;
	private Text empUsername;
	private Label empTypeLabel;
	private Combo empType;
	
	private Composite wdComposite;
	private Label wdListInstructions;
	private Label wdListLocationLabel;
	private Text wdListLocation;
	private Button wdListButton;
	
	private Composite appComposite;
	private Label updateInstructions;
	private Button updateStartUp;
	
	private StackLayout stackLayout;
	
	private Composite dialogControls;
	
	private static final Pattern usernamePattern = Pattern.compile("^[a-z0-9]{1,8}$");
	private static final Pattern badgeNumberPattern = Pattern.compile("^[0-9]{6}$");
	
	public PreferencesDialog(CardCoachContext context, Shell parent) {
		this.context = context;
		this.parent = parent;
	}
	
	private void widgetDisposed(DisposeEvent event) {
		FontManager.getInstance().disposeInstance("default.18");
	}
	
	public void open() {
		display = Display.getCurrent();
		shell = new Shell(parent, SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setText(context.getApplicationSubtitle("Preferences"));
		shell.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent event) {
				PreferencesDialog.this.widgetDisposed(event);
			}
		});
		buildSkeleton();
		buildApplicationComposite();
		buildEmployeeComposite();
		buildWorkDirectiveComposite();
		shell.pack();
		shell.setSize(shell.computeSize(500, 300));
		Positioner.centerOnParent(parent, shell);
		stackLayout.topControl = appComposite;
		stackComposite.layout();
		shell.layout();
		loadPreferences();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
	}
	
	private void buildSkeleton() {
		preferencesTree = new Tree(shell, SWT.BORDER);
		stackTitle = new Label(shell, SWT.WRAP);
		stackTitle.setFont(FontManager.getInstance().getFont("default.18"));
//		stackTitle.setFont(new Font(Display.getCurrent(), "Tahoma", 18, SWT.NORMAL));
		stackTitle.setText("Application");
		stackComposite = new Composite(shell, SWT.NONE);
		stackLayout = new StackLayout();
		stackComposite.setLayout(stackLayout);
		final TreeItem applicationItem = new TreeItem(preferencesTree, SWT.NONE);
		applicationItem.setText("Application");
		final TreeItem employeeTreeItem = new TreeItem(preferencesTree, SWT.NONE);
		employeeTreeItem.setText("Employee");
		final TreeItem wdTreeItem = new TreeItem(preferencesTree, SWT.NONE);
		wdTreeItem.setText("Work Directives");
		preferencesTree.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (applicationItem.equals((TreeItem) event.item) &&
						!stackLayout.topControl.equals(appComposite)) {
					stackLayout.topControl = appComposite;
					stackTitle.setText("Application");
					stackComposite.layout();
				}
				else if (employeeTreeItem.equals((TreeItem) event.item) &&
						!stackLayout.topControl.equals(empComposite)) {
					stackLayout.topControl = empComposite;
					stackTitle.setText("Employee");
					stackComposite.layout();
				}
				else if (wdTreeItem.equals((TreeItem) event.item) &&
						!stackLayout.topControl.equals(wdComposite)) {
					stackLayout.topControl = wdComposite;
					stackTitle.setText("Work Directives");
					stackComposite.layout();
				}
			}
		});
		preferencesTree.setSelection(applicationItem);
		dialogControls = new DialogControlsBuilder()
			.setPositiveButtonText("Apply")
			.setNegativeButtonText("Cancel")
			.addDialogSelectionListener(new DialogSelectionListener() {
				
				@Override
				public void dialogWidgetSelected(int which) {
					switch (which) {
					case DialogControlsBuilder.POSITIVE_BUTTON:
						if (wdListLocation.getText().isEmpty()) {
							MessageFactory.showError(shell, "You must specify a work directive list.");
							return;
						}
						if (empFirstName.getText().isEmpty()) {
							MessageFactory.showError(shell, "You must specify a first name.");
							return;
						}
						if (empLastName.getText().isEmpty()) {
							MessageFactory.showError(shell, "You must specify a last name.");
							return;
						}
						if (!badgeNumberPattern.matcher(empBadgeNumber.getText()).matches()) {
							MessageFactory.showError(shell, "You must specify a badge number that is exactly 6 numbers.");
							return;
						}
						if (!usernamePattern.matcher(empUsername.getText()).matches()) {
							MessageFactory.showError(shell, "You must specify a username consisting of 1 to 8 lowercase letters and numbers.");
							return;
						}
						if (empType.getSelectionIndex() == -1) {
							MessageFactory.showError(shell, "You must select an employee type");
							return;
						}
						savePreferences();
					case DialogControlsBuilder.NEGATIVE_BUTTON:
						shell.dispose();
					}
				}
			}).createComposite(shell);
		// Apply layout
		GridLayout layout = new GridLayout(2, false);
		shell.setLayout(layout);
		
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 2);
		layoutData.verticalSpan = 2;
		layoutData.widthHint = 150;
		preferencesTree.setLayoutData(layoutData);
		
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		stackTitle.setLayoutData(layoutData);
		
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		stackComposite.setLayoutData(layoutData);
		
		layoutData = new GridData(SWT.RIGHT, SWT.FILL, true, false, 2, 1);
		layoutData.horizontalSpan = 2;
		dialogControls.setLayoutData(layoutData);

	}
	
	private void buildWorkDirectiveComposite() {
		wdComposite = new Composite(stackComposite, SWT.NONE);
		wdListInstructions = new Label(wdComposite, SWT.WRAP);
		wdListInstructions.setText("Choose file that contains the list of work directives to use with Card Coach.");
		wdListLocationLabel = new Label(wdComposite, SWT.NONE);
		wdListLocationLabel.setText("List Location:");
		wdListLocation = WidgetBuilder.newText(wdComposite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		wdListButton = new Button(wdComposite, SWT.PUSH);
		wdListButton.setText("Browse");
		wdListButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Open Work Directive List");
				if (wdListLocation.getText().isEmpty()) {
					fd.setFilterPath(System.getProperty("user.home"));
				} else {
					fd.setFilterPath(wdListLocation.getText());
				}
				String[] filterExtNames = {"Work Directive Lists (*.xml)"};
				String[] filterExts = {"*.xml"};
				fd.setFilterExtensions(filterExts);
				fd.setFilterNames(filterExtNames);
				String selected = fd.open();
				if (selected != null) {
					wdListLocation.setText(selected);
				}
			}
		});
		
		FormLayout formLayout = new FormLayout();
		formLayout.marginHeight = 5;
		formLayout.marginWidth = 5;
		formLayout.spacing = 5;
		wdComposite.setLayout(formLayout);
		
		FormData formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		wdListInstructions.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(30);
		formData.top = new FormAttachment(wdListInstructions);
		formData.right = new FormAttachment(100);
		wdListLocation.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(wdListLocation, 0, SWT.CENTER);
		formData.right = new FormAttachment(wdListLocation);
		wdListLocationLabel.setLayoutData(formData);
		
		formData = new FormData();
		formData.top = new FormAttachment(wdListLocation);
		formData.right = new FormAttachment(100);
		formData.width = 100;
		wdListButton.setLayoutData(formData);
	}
	
	private void buildApplicationComposite() {
		appComposite = new Composite(stackComposite, SWT.NONE);
		updateInstructions = new Label(appComposite, SWT.WRAP);
		updateInstructions.setText("Select whether or not you would like Card Coach to check for updates when it starts up.");
		updateStartUp = new Button(appComposite, SWT.CHECK);
		updateStartUp.setText("Check for updates on start-up.");
		
		FormLayout formLayout = new FormLayout();
		formLayout.marginHeight = 5;
		formLayout.marginWidth = 5;
		formLayout.spacing = 5;
		appComposite.setLayout(formLayout);
		
		FormData formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		updateInstructions.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(updateInstructions);
		formData.right = new FormAttachment(100);
		updateStartUp.setLayoutData(formData);
	}
	
	private void buildEmployeeComposite() {
		empComposite = new Composite(stackComposite, SWT.NONE);
		empInstructions = new Label(empComposite, SWT.WRAP);
		empInstructions.setText("Enter your information. This information is used to identify you as the owner of created time sheets and cards.");
		empFirstNameLabel = new Label(empComposite, SWT.NONE);
		empFirstNameLabel.setText("First Name:");
		empFirstName = WidgetBuilder.newText(empComposite, SWT.SINGLE | SWT.BORDER);
		empLastNameLabel = new Label(empComposite, SWT.NONE);
		empLastNameLabel.setText("Last Name:");
		empLastName = WidgetBuilder.newText(empComposite, SWT.SINGLE | SWT.BORDER);
		empUsernameLabel = new Label(empComposite, SWT.NONE);
		empUsernameLabel.setText("Username:");
		empUsername = WidgetBuilder.newText(empComposite, SWT.SINGLE | SWT.BORDER);
		empBadgeNumberLabel = new Label(empComposite, SWT.NONE);
		empBadgeNumberLabel.setText("Badge Number:");
		empBadgeNumber = WidgetBuilder.newText(empComposite, SWT.SINGLE | SWT.BORDER);
		empTypeLabel = new Label(empComposite, SWT.NONE);
		empTypeLabel.setText("Employee Type:");
		empType = new Combo(empComposite, SWT.SINGLE | SWT.READ_ONLY);
		empType.setItems(getEmployeeTypes());
		
		FormLayout formLayout = new FormLayout();
		formLayout.marginHeight = 5;
		formLayout.marginWidth = 5;
		formLayout.spacing = 5;
		empComposite.setLayout(formLayout);
		
		FormData formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		empInstructions.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(40);
		formData.top = new FormAttachment(empInstructions);
		formData.right = new FormAttachment(100);
		empFirstName.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(empFirstName, 0, SWT.CENTER);
		formData.right = new FormAttachment(empFirstName);
		empFirstNameLabel.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(40);
		formData.top = new FormAttachment(empFirstName);
		formData.right = new FormAttachment(100);
		empLastName.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(empLastName, 0, SWT.CENTER);
		formData.right = new FormAttachment(empLastName);
		empLastNameLabel.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(40);
		formData.top = new FormAttachment(empLastName);
		formData.right = new FormAttachment(100);
		empUsername.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(empUsername, 0, SWT.CENTER);
		formData.right = new FormAttachment(empUsername);
		empUsernameLabel.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(40);
		formData.top = new FormAttachment(empUsername);
		formData.right = new FormAttachment(100);
		empBadgeNumber.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(empBadgeNumber, 0, SWT.CENTER);
		formData.right = new FormAttachment(empBadgeNumber);
		empBadgeNumberLabel.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(40);
		formData.top = new FormAttachment(empBadgeNumber);
		formData.right = new FormAttachment(100);
		empType.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(empType, 0, SWT.CENTER);
		formData.right = new FormAttachment(empType);
		empTypeLabel.setLayoutData(formData);
	}
	
	private void savePreferences() {
		PreferenceManager.getInstance().putBoolean(PreferenceManager.UPDATE_CHECK_ON_STARTUP, updateStartUp.getSelection());
		PreferenceManager.getInstance().putString(PreferenceManager.FIRST_NAME, empFirstName.getText());
		PreferenceManager.getInstance().putString(PreferenceManager.LAST_NAME, empLastName.getText());
		PreferenceManager.getInstance().putString(PreferenceManager.USERNAME, empUsername.getText());
		PreferenceManager.getInstance().putString(PreferenceManager.BADGE_NUMBER, empBadgeNumber.getText());
		PreferenceManager.getInstance().putString(PreferenceManager.EMPLOYEE_TYPE, empType.getText());
		PreferenceManager.getInstance().putString(PreferenceManager.WORKDIRECTIVES_LIST_LOCATION, wdListLocation.getText());
	}
	
	private void loadPreferences() {
		updateStartUp.setSelection(PreferenceManager.getInstance().getBoolean(PreferenceManager.UPDATE_CHECK_ON_STARTUP, false));
		empFirstName.setText(PreferenceManager.getInstance().getString(PreferenceManager.FIRST_NAME, ""));
		empLastName.setText(PreferenceManager.getInstance().getString(PreferenceManager.LAST_NAME, ""));
		empUsername.setText(PreferenceManager.getInstance().getString(PreferenceManager.USERNAME, ""));
		empBadgeNumber.setText(PreferenceManager.getInstance().getString(PreferenceManager.BADGE_NUMBER, ""));
		String empTypeStr = PreferenceManager.getInstance().getString(PreferenceManager.EMPLOYEE_TYPE, "");
		if (empTypeStr != null && !empTypeStr.isEmpty()) {
			empType.setText(empTypeStr);
		}
		wdListLocation.setText(PreferenceManager.getInstance().getString(PreferenceManager.WORKDIRECTIVES_LIST_LOCATION, ""));
	}
	
	private String[] getEmployeeTypes() {
		String[] empTypes = new String[Employee.Type.values().length];
		for (int i = 0; i < empTypes.length; i++) {
			empTypes[i] = Employee.Type.values()[i].toString();
		}
		return empTypes;
	}
}