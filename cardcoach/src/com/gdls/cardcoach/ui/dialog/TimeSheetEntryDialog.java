package com.gdls.cardcoach.ui.dialog;

import hirondelle.date4j.DateTime;

import java.util.Locale;

import net.streetj.swt.widgets.ctime.CAnalogTimePicker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.gdls.cardcoach.employee.Employee;
import com.gdls.cardcoach.timesheet.TimeSheetEntry;
import com.gdls.cardcoach.ui.AccountCombo;
import com.gdls.cardcoach.ui.MessageFactory;
import com.gdls.cardcoach.ui.Positioner;
import com.gdls.cardcoach.ui.WidgetBuilder;
import com.gdls.cardcoach.ui.composite.DialogControlsBuilder;
import com.gdls.cardcoach.ui.event.DialogSelectionListener;
import com.gdls.cardcoach.workdirective.WorkDirective;
import com.gdls.cardcoach.workdirective.WorkDirectiveLibrary;

public class TimeSheetEntryDialog extends Dialog {
	
	public static final int MODE_UPDATE = 1<<1;
	public static final int MODE_SPLIT = 1<<2;
	public static final int MODE_INSERT = 1<<3;
	
	private int mode;
	
	private WorkDirectiveLibrary library;
	private Employee employee;
	private TimeSheetEntry entry;
	private DateTime minTime;
	private DateTime maxTime;
	private DateTime sheetDate;
	private Boolean splitBefore;
	
	private final Shell parent;
	private Shell shell;
	
	private Label instructions;
	
	private Composite splitComposite;
	private Label splitLabel1;
	private Button splitBeforeButton;
	private Button splitAfterButton;
	private Label splitLabel2;
	private Label wdNumberLabel;
	private Combo wdNumberCombo;
	private Label accLabel;
	private AccountCombo accCombo;
	private Label ccLabel;
	private Text ccText;
	private Label tsLabel;
	private Text tsText;
	private Label nLabel;
	private Text nText;
	private Label stLabel;
	private CAnalogTimePicker startTimeSelector;
	private Label minTimeLabel;
	private Label etLabel;
	private CAnalogTimePicker endTimeSelector;
	private Label maxTimeLabel;
	private Button suspButton;
	
	private Composite dialogControls;
	
	private WorkDirective selectedWorkDirective;
	
	private DialogResult dialogResult;
	
	public TimeSheetEntryDialog(Shell parent) {
		super(parent);
		this.parent = parent;
	}
	
	private void widgetDisposed(DisposeEvent event) {
	}
	
	public void open(DialogInput input, DialogResult result) throws DialogInputDataException {
		if (input.getData("minTime") == null) {
			throw new DialogInputDataException("minTime");
		}
		if (input.getData("maxTime") == null) {
			throw new DialogInputDataException("maxTime");
		}
		if (input.getData("library") == null) {
			throw new DialogInputDataException("library");
		}
		if (input.getData("sheetDate") == null) {
			throw new DialogInputDataException("sheetDate");
		}
		if (input.getData("employee") == null) {
			throw new DialogInputDataException("employee");
		}
		this.mode = input.getMode();
		this.entry = (TimeSheetEntry) input.getData("entry");
		this.minTime = (DateTime) input.getData("minTime");
		this.maxTime = (DateTime) input.getData("maxTime");
		this.employee = (Employee) input.getData("employee");
		this.library = (WorkDirectiveLibrary) input.getData("library");
		this.sheetDate = (DateTime) input.getData("sheetDate");
		this.splitBefore = (Boolean) input.getData("splitBefore");
		dialogResult = result;
		createShell();
		createContents();
		shell.pack();
		shell.setSize(shell.computeSize(500, SWT.DEFAULT));
		Positioner.centerOnParent(parent, shell);
		shell.open();
		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) shell.getDisplay().sleep();
		}
	}
	
	private void createShell() {
		shell = new Shell(parent, SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL);
		switch (mode) {
		case MODE_UPDATE:
			shell.setText("Update Time Sheet Entry");
			break;
		case MODE_INSERT:
			shell.setText("Insert Time Sheet Entry");
			break;
		case MODE_SPLIT:
			shell.setText("Split Time Sheet Entry");
			break;
		}
		shell.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent event) {
				TimeSheetEntryDialog.this.widgetDisposed(event);
			}
		});
	}
	
	private void createContents() {
		final Composite iComposite = new Composite(shell, SWT.NONE);
		instructions = new Label(iComposite, SWT.WRAP);
		switch (mode) {
		case MODE_UPDATE:
			instructions.setText("Use the controls below to edit the time sheet entry's work directive, task code, " +
					"start time, or end time. Keep in mind that the start time and end time must be greater than or equal to the " +
					"specified minimum time and less than or equal to the specified maximum time, respectively.");
			break;
		case MODE_INSERT:
			instructions.setText("Set a work directive, task code, start time, and end time for the below work " +
					"time sheet entry and it will be inserted above the selected time sheet entry. Keep in mind " +
					"that the start time and end time must be greater than or equal to the " +
					"specified minimum time and less than or equal to the specified maximum time, respectively.");
			break;
		case MODE_SPLIT:
			instructions.setText("Use the controls below to use a portion of the time in the selected time sheet entry for a new one. " +
					"You may choose whether this new entry will be inserted before or after the one selected. Keep in mind " +
					"that the start time and end time must be greater than or equal to the " +
					"specified minimum time and less than or equal to the specified maximum time, respectively.");
			break;
		}
		
		final Composite cComposite = new Composite(shell, SWT.NONE);
		
		if (mode == MODE_SPLIT) {
			splitLabel1 = new Label(cComposite, SWT.RIGHT);
			splitLabel1.setText("Insert New Entry:");
			splitComposite = new Composite(cComposite, SWT.NONE);
			splitBeforeButton = new Button(splitComposite, SWT.RADIO);
			splitBeforeButton.setText("Before");
			splitBeforeButton.setSelection(splitBefore != null ? splitBefore.booleanValue() : true);
			splitAfterButton = new Button(splitComposite, SWT.RADIO);
			splitAfterButton.setText("After");
			splitAfterButton.setSelection(false);
			splitLabel2 = new Label(cComposite, SWT.LEFT);
			splitLabel2.setText("selected entry.");
		}
		
		wdNumberLabel = new Label(cComposite, SWT.RIGHT);
		wdNumberLabel.setText("Work Directive:");
		
		wdNumberCombo = new Combo(cComposite, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		wdNumberCombo.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (library.containsWorkDirective(wdNumberCombo.getText())) {
					selectedWorkDirective = library.getWorkDirective(wdNumberCombo.getText());
					accCombo.setAccounts(selectedWorkDirective.getAccounts(employee.getType()));
					// Check if this entry was suspended. It can not be for directives that don't support it.
					if (!selectedWorkDirective.isSuspendable() && suspButton.getSelection()) {
						MessageFactory.showMessage(shell, "You have selected a work directive that can not be suspended. This entry will no longer be suspended.");
						suspButton.setSelection(false);
					}
					if (!selectedWorkDirective.isSuspendable()) {
						suspButton.setSelection(false);
						suspButton.setEnabled(false);
					}
					else {
						suspButton.setEnabled(true);
					}
				}
				else {
					selectedWorkDirective = entry.getWorkDirective();
					accCombo.setAccounts(selectedWorkDirective.getAccounts(employee.getType()));
				}
				if (accCombo.getAccountCount() < 1) {
					MessageFactory.showMessage(shell, "Work directive '" + selectedWorkDirective.getNumber() + "' does not have any chargeable accounts for your employee type.");
				}
				if (accCombo.getSelectedAccount() != null) {
					selectedWorkDirective.setSelectedAccount(accCombo.getSelectedAccount(), true);
				}
			}
		});

		accLabel = new Label(cComposite, SWT.RIGHT);
		accLabel.setText("Account:");
		
		accCombo = new AccountCombo();
		accCombo.createControl(cComposite);
		accCombo.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				selectedWorkDirective.setSelectedAccount(accCombo.getSelectedAccount(), true);
			}
		});
		
		ccLabel = new Label(cComposite, SWT.RIGHT);
		ccLabel.setText("Cross Charge Dept:");
		
		ccText = WidgetBuilder.newText(cComposite, SWT.SINGLE | SWT.BORDER);
		ccText.addVerifyListener(new VerifyListener() {
			
			@Override
			public void verifyText(VerifyEvent event) {
				event.text = event.text.toUpperCase();
			}
		});
		
		tsLabel = new Label(cComposite, SWT.RIGHT);
		tsLabel.setText("Task Code:");
		
		tsText = WidgetBuilder.newText(cComposite, SWT.SINGLE | SWT.BORDER);
		tsText.addVerifyListener(new VerifyListener() {
			
			@Override
			public void verifyText(VerifyEvent event) {
				event.text = event.text.toUpperCase();
			}
		});
		
		nLabel = new Label(cComposite, SWT.RIGHT);
		nLabel.setText("Note:");
		
		nText = WidgetBuilder.newText(cComposite, SWT.MULTI | SWT.BORDER);
		
		suspButton = new Button(cComposite, SWT.CHECK);
		suspButton.setText("Suspended");
		
		final Composite tComposite = new Composite(shell, SWT.NONE);
		
		stLabel = new Label(tComposite, SWT.CENTER);
		stLabel.setText("Start Time:");
		
		etLabel = new Label(tComposite, SWT.CENTER);
		etLabel.setText("End Time:");
		
		startTimeSelector = new CAnalogTimePicker(tComposite, SWT.BORDER);
		
		endTimeSelector = new CAnalogTimePicker(tComposite, SWT.BORDER);
		
		minTimeLabel = new Label(tComposite, SWT.CENTER);
		minTimeLabel.setText("(Minimum: " + minTime.format("h12:mm:ss a", Locale.getDefault()) + ")");
		
		maxTimeLabel = new Label(tComposite, SWT.CENTER);
		maxTimeLabel.setText("(Maximum: " + maxTime.format("h12:mm:ss a", Locale.getDefault()) + ")");
		
		setupControls();
		
		createDialogControls();
		
		GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		iComposite.setLayoutData(gridData);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		cComposite.setLayoutData(gridData);
		
		gridData = new GridData(SWT.RIGHT, SWT.FILL, true, false);
		dialogControls.setLayoutData(gridData);
		
		iComposite.setLayout(new FillLayout());
		
		FormLayout formLayout = new FormLayout();
		formLayout.spacing = 5;
		cComposite.setLayout(formLayout);
		
		FormData formData;
		if (mode == MODE_SPLIT) {
			formData = new FormData();
			formData.left = new FormAttachment(30);
			formData.top = new FormAttachment(0);
//			formData.right = new FormAttachment(75);
			splitComposite.setLayoutData(formData);
			
			RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
			splitComposite.setLayout(rowLayout);
			
			formData = new FormData();
			formData.left = new FormAttachment(30);
			formData.top = new FormAttachment(splitComposite);
			formData.right = new FormAttachment(80);
			wdNumberCombo.setLayoutData(formData);
		} else {
			formData = new FormData();
			formData.left = new FormAttachment(30);
			formData.top = new FormAttachment(0);
			formData.right = new FormAttachment(80);
			wdNumberCombo.setLayoutData(formData);
		}
	
		formData = new FormData();
		formData.left = new FormAttachment(30);
		formData.top = new FormAttachment(wdNumberCombo);
		formData.right = new FormAttachment(80);
		accCombo.getControl().setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(30);
		formData.top = new FormAttachment(accCombo.getControl());
		formData.right = new FormAttachment(80);
		ccText.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(30);
		formData.top = new FormAttachment(ccText);
		formData.right = new FormAttachment(80);
		tsText.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(30);
		formData.top = new FormAttachment(tsText);
		formData.right = new FormAttachment(80);
		formData.height = nText.getLineHeight() * 3;
		nText.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(30);
		formData.top = new FormAttachment(nText);
		formData.right = new FormAttachment(80);
		formData.bottom = new FormAttachment(100);
		suspButton.setLayoutData(formData);
		
		gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		tComposite.setLayoutData(gridData);
		
		gridLayout = new GridLayout(2, true);
		tComposite.setLayout(gridLayout);
		
		gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		stLabel.setLayoutData(gridData);
		
		gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		etLabel.setLayoutData(gridData);
		
		gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		gridData.widthHint = 180;
		gridData.heightHint = 180;
		startTimeSelector.setLayoutData(gridData);
		
		gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		gridData.widthHint = 180;
		gridData.heightHint = 180;
		endTimeSelector.setLayoutData(gridData);
		
		gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		minTimeLabel.setLayoutData(gridData);
		
		gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		maxTimeLabel.setLayoutData(gridData);
		

		
		if (mode == MODE_SPLIT) {
			formData = new FormData();
			formData.left = new FormAttachment(0);
			formData.top = new FormAttachment(splitComposite, 0, SWT.CENTER);
			formData.right = new FormAttachment(splitComposite);
			splitLabel1.setLayoutData(formData);
			
			formData = new FormData();
			formData.left = new FormAttachment(splitComposite);
			formData.top = new FormAttachment(splitComposite, 0, SWT.CENTER);
			formData.right = new FormAttachment(100);
			splitLabel2.setLayoutData(formData);
		}
		
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(wdNumberCombo, 0, SWT.CENTER);
		formData.right = new FormAttachment(wdNumberCombo);
		wdNumberLabel.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(accCombo.getControl(), 0, SWT.CENTER);
		formData.right = new FormAttachment(accCombo.getControl());
		accLabel.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(ccText, 0, SWT.CENTER);
		formData.right = new FormAttachment(ccText);
		ccLabel.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(tsText, 0, SWT.CENTER);
		formData.right = new FormAttachment(tsText);
		tsLabel.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(nText, 3, SWT.TOP);
		formData.right = new FormAttachment(nText);
		nLabel.setLayoutData(formData);
	}
	
	private void setupControls() {
		wdNumberCombo.setItems(library.getWorkDirectiveNumbers(WorkDirectiveLibrary.Mode.ALL));
		DateTime startTime = minTime;
		DateTime endTime = maxTime;
		if (entry != null) {
			if (library.containsWorkDirective(entry.getWorkDirective().getNumber())) {
				selectedWorkDirective = library.getWorkDirective(entry.getWorkDirective().getNumber());
				selectedWorkDirective.setSelectedAccount(entry.getWorkDirective().getSelectedAccount(), true);
			}
			else {
				// This time sheet entry is using a work directive thats not in the library.
				selectedWorkDirective = entry.getWorkDirective();
				wdNumberCombo.add(selectedWorkDirective.getNumber(), 0);
			}
			wdNumberCombo.setText(selectedWorkDirective.getNumber());
			accCombo.setAccounts(selectedWorkDirective.getAccounts(employee.getType()));
			if (accCombo.getAccountCount() < 1) {
				MessageFactory.showMessage(shell, "Work directive '" + selectedWorkDirective.getNumber() + "' does not have any chargeable accounts for your employee type.");
			}
			if (accCombo.getSelectedAccount() != null) {
				selectedWorkDirective.setSelectedAccount(accCombo.getSelectedAccount(), true);
			}
			ccText.setText(entry.getCrossCharge());
			tsText.setText(entry.getTaskCode());
			nText.setText(entry.getNote());
			// Adjust start and end times if necessary
			if (entry.isStarted()) {
				startTime = entry.getStartTime();
			}
			if (entry.isFinished()) {
				endTime = entry.getEndTime();
			}
			suspButton.setSelection(entry.isSuspended());
		}
		startTimeSelector.setTime(
				startTime.getHour(),
				startTime.getMinute(),
				startTime.getSecond());
		endTimeSelector.setTime(
				endTime.getHour(),
				endTime.getMinute(),
				endTime.getSecond());
		if (mode == MODE_UPDATE && entry != null && !entry.isFinished()) {
			// Hide the end time selector when updating an entry that is not finished
			etLabel.setVisible(false);
			endTimeSelector.setVisible(false);
			maxTimeLabel.setVisible(false);
		}
	}
	
	private void createDialogControls() {
		DialogControlsBuilder builder = new DialogControlsBuilder();
		builder.setPositiveButtonText("Ok");
		builder.setNegativeButtonText("Cancel");
		builder.addDialogSelectionListener(new DialogSelectionListener() {
			
			@Override
			public void dialogWidgetSelected(int which) {
				if (DialogControlsBuilder.POSITIVE_BUTTON == which) {
					if (createTimeSheetEntry()) {
						shell.dispose();
					}
				}
				else {
					dialogResult.setReturnCode(DialogResult.RETURN_CANCEL);
					dialogResult.setData("entry", null);
					shell.dispose();
				}
			}
		});
		dialogControls = builder.createComposite(shell);
	}

	private boolean createTimeSheetEntry() {
		
		DateTime startTime = null;
		DateTime endTime = null;
		if (wdNumberCombo.getText().isEmpty()) {
			MessageFactory.showError(shell, "You must select a work directive.");
			return false;
		}
		if (accCombo.getSelectedAccount() == null) {
			MessageFactory.showError(shell, "You must select an account.");
			return false;
		}
		startTime = new DateTime(
				sheetDate.getYear(),
				sheetDate.getMonth(),
				sheetDate.getDay(),
				startTimeSelector.getHours(),
				startTimeSelector.getMinutes(),
				startTimeSelector.getSeconds(),
				null);
		endTime = new DateTime(
				sheetDate.getYear(),
				sheetDate.getMonth(),
				sheetDate.getDay(),
				endTimeSelector.getHours(),
				endTimeSelector.getMinutes(),
				endTimeSelector.getSeconds(),
				null);
		if (startTime.compareTo(minTime) < 0) {
			MessageFactory.showError(shell, "Start time must be on or after the minimum time.");
			return false;
		}
		if (endTimeSelector.isVisible()) {
			// Checking against max time and start time
			if (endTime.compareTo(maxTime) > 0) {
				MessageFactory.showError(shell, "End time must be on or before the maximum time.");
				return false;
			}
			if (endTime.compareTo(startTime) <= 0) {
				MessageFactory.showError(shell, "There must be at least one second of time between the start and end times.");
				return false;
			}
		}
		else {
			endTime = null;
		}
		TimeSheetEntry entry = new TimeSheetEntry(
				new WorkDirective(
						selectedWorkDirective.getNumber(),
						selectedWorkDirective.getSelectedAccount(),
						selectedWorkDirective.getDescription(),
						selectedWorkDirective.getSuspenseNumber()),
				ccText.getText(),
				tsText.getText(),
				nText.getText(),
				suspButton.getSelection(),
				startTime,
				endTime);
		if (mode == MODE_SPLIT) {
			dialogResult.setData("splitBefore", new Boolean(splitBeforeButton.getSelection()));
		}
		dialogResult.setReturnCode(DialogResult.RETURN_OK);
		dialogResult.setData("entry", entry);
		return true;
	}
		
}
