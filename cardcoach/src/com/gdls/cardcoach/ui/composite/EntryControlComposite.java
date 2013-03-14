package com.gdls.cardcoach.ui.composite;

import hirondelle.date4j.DateTime;

import java.util.TimeZone;

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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.gdls.cardcoach.timesheet.TimeSheet;
import com.gdls.cardcoach.timesheet.TimeSheetChangeListener;
import com.gdls.cardcoach.timesheet.TimeSheetEntry;
import com.gdls.cardcoach.ui.AccountCombo;
import com.gdls.cardcoach.ui.LastEntriesTable;
import com.gdls.cardcoach.ui.MessageFactory;
import com.gdls.cardcoach.ui.WidgetBuilder;
import com.gdls.cardcoach.ui.dialog.TimeEntryDialog;
import com.gdls.cardcoach.ui.icons.IconHandler;
import com.gdls.cardcoach.util.DateTimeUtil;
import com.gdls.cardcoach.workdirective.WorkDirective;
import com.gdls.cardcoach.workdirective.WorkDirectiveLibrary;
import com.gdls.cardcoach.workdirective.WorkDirectiveLibraryChangeListener;

public class EntryControlComposite extends Composite {
	
	private TimeSheet timeSheet;
	private WorkDirectiveLibrary library;
	private WorkDirective selectedWorkDirective;
	
	private Label wdLabel;
	private Composite wdComposite;
	private Combo wdNumberCombo;
	private Button wdSearch;
	private Label accLabel;
	private AccountCombo accCombo;
	private Label ccLabel;
	private Text crossCharge;
	private Label tcLabel;
	private Text taskCode;
	private Label noteLabel;
	private Text note;
	
	private Label leLabel;
	private LastEntriesTable lastEntriesTable;
	
	private Composite buttonComposite;
	private Button startNow;
	private Button start;
	private Button stopNow;
	private Button stop;
	
	private final TimeEntryDialog timeEntryDialog;
	
	public EntryControlComposite(Composite parent) {
		super(parent, SWT.NONE);
		
		timeEntryDialog = new TimeEntryDialog(getShell());
		
		final SelectionAdapter startEntryAdapter = new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				WorkDirective wd = new WorkDirective(
						selectedWorkDirective.getNumber(),
						selectedWorkDirective.getSelectedAccount(),
						selectedWorkDirective.getDescription(),
						selectedWorkDirective.getSuspenseNumber());
				if (event.widget.equals(startNow)) {
					lastEntriesTable.addLastEntry(
							selectedWorkDirective.getNumber(),
							selectedWorkDirective.getSelectedAccount(),
							crossCharge.getText(),
							taskCode.getText());
					timeSheet.addTimeSheetEntry(new TimeSheetEntry(
							wd,
							crossCharge.getText(),
							taskCode.getText(),
							note.getText()));

				}
				else {
					DateTime minTime = timeSheet.getLastEntryTimeMade();
					DateTime maxTime = DateTimeUtil.truncate(timeSheet.getSheetDate().getEndOfDay());
					DateTime time = timeEntryDialog.open(minTime, maxTime);
					if (time != null) {
						lastEntriesTable.addLastEntry(
								selectedWorkDirective.getNumber(),
								selectedWorkDirective.getSelectedAccount(),
								crossCharge.getText(),
								taskCode.getText());
						timeSheet.addTimeSheetEntry(new TimeSheetEntry(
								wd,
								crossCharge.getText(),
								taskCode.getText(),
								note.getText(),
								false,
								time));
					}
				}

			}
		};
		
		final SelectionAdapter stopEntryAdapter = new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (event.widget.equals(stopNow)) {
					timeSheet.stopTimeSheetEntry();
				}
				else {
					DateTime minTime = timeSheet.getLastEntryTimeMade();
					DateTime maxTime = DateTimeUtil.truncate(timeSheet.getSheetDate().getEndOfDay());
					DateTime time = timeEntryDialog.open(minTime, maxTime);
					if (time != null) {
						timeSheet.stopTimeSheetEntry(time);
					}
				}
			}
		};
		
		Group group = new Group(this, SWT.NONE);
		group.setText("Add Time Entries");
		
		wdLabel = new Label(group, SWT.RIGHT);
		wdLabel.setText("Work Directive:");
		
		wdComposite = new Composite(group, SWT.NONE);
		
		wdNumberCombo  = new Combo(wdComposite, SWT.BORDER | SWT.READ_ONLY);
		wdNumberCombo.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				selectedWorkDirective = null;
				selectedWorkDirective = library.getWorkDirective(wdNumberCombo.getText());
				accCombo.setAccounts(selectedWorkDirective.getAccounts(timeSheet.getEmployee().getType()));
				if (accCombo.getAccountCount() < 1) {
					MessageFactory.showMessage(getShell(), "Work directive '" + selectedWorkDirective.getNumber() + "' does not have any chargeable accounts for your employee type.");
				}
				if (accCombo.getSelectedAccount() != null) {
					selectedWorkDirective.setSelectedAccount(accCombo.getSelectedAccount(), true);
					setStartControlsEnabled(true);
				}
				else {
					setStartControlsEnabled(false);
				}
			}
		});
		
		wdSearch = new Button(wdComposite, SWT.PUSH);
		wdSearch.setImage(IconHandler.getInstance().getImage("icon.search"));
		wdSearch.setEnabled(false);
		
		accLabel = new Label(group, SWT.RIGHT);
		accLabel.setText("Accounts:");
		
		accCombo = new AccountCombo();
		accCombo.createControl(group);
		accCombo.addSelectionListener(new SelectionAdapter() {
		
			@Override
			public void widgetSelected(SelectionEvent event) {
				selectedWorkDirective.setSelectedAccount(accCombo.getSelectedAccount(), true);
				setStartControlsEnabled(true);
			}
		});
		
		ccLabel = new Label(group, SWT.RIGHT);
		ccLabel.setText("Cross Charge Dept:");
		
		crossCharge = WidgetBuilder.newText(group, SWT.SINGLE | SWT.BORDER);
		crossCharge.addVerifyListener(new VerifyListener() {
			
			@Override
			public void verifyText(VerifyEvent event) {
				event.text = event.text.toUpperCase();
			}
		});
		
		tcLabel = new Label(group, SWT.RIGHT);
		tcLabel.setText("Task Code:");
		
		taskCode = WidgetBuilder.newText(group, SWT.SINGLE | SWT.BORDER);
		taskCode.addVerifyListener(new VerifyListener() {
			
			@Override
			public void verifyText(VerifyEvent event) {
				event.text = event.text.toUpperCase();
			}
		});
		
		noteLabel = new Label(group, SWT.RIGHT);
		noteLabel.setText("Note:");
		
		note = WidgetBuilder.newText(group, SWT.BORDER | SWT.MULTI);
		
		leLabel = new Label(group, SWT.LEFT);
		leLabel.setText("Last 5 Entries:");
		
		lastEntriesTable = new LastEntriesTable();
		lastEntriesTable.createControl(group);
		lastEntriesTable.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				selectedWorkDirective = library.getWorkDirective(lastEntriesTable.getSelectedWorkDirective());
				wdNumberCombo.setText(selectedWorkDirective.getNumber());
				accCombo.setAccounts(selectedWorkDirective.getAccounts(timeSheet.getEmployee().getType()));
				if (accCombo.getAccountCount() < 1) {
					MessageFactory.showMessage(getShell(), "Work directive '" + selectedWorkDirective.getNumber() + "' does not have any chargeable accounts for your employee type.");
				}
				accCombo.setAccount(lastEntriesTable.getSelectedAccount());
				if (accCombo.getSelectedAccount() != null) {
					selectedWorkDirective.setSelectedAccount(accCombo.getSelectedAccount(), true);
					setStartControlsEnabled(true);
				}
				else {
					setStartControlsEnabled(false);
				}
				crossCharge.setText(lastEntriesTable.getSelectedCrossCharge());
				taskCode.setText(lastEntriesTable.getSelectedTaskCode());
			}
		});
		
		buttonComposite = new Composite(group, SWT.NONE);
		
		startNow = new Button(buttonComposite, SWT.PUSH);
		startNow.setText("Start Entry Now");
		startNow.setImage(IconHandler.getInstance().getImage("icon.button.startentrynow"));
		startNow.setEnabled(false);
		startNow.addSelectionListener(startEntryAdapter);
		
		start = new Button(buttonComposite, SWT.PUSH);
		start.setText("Start Entry");
		start.setImage(IconHandler.getInstance().getImage("icon.button.startentry"));
		start.setEnabled(false);
		start.addSelectionListener(startEntryAdapter);
		
		stopNow = new Button(buttonComposite, SWT.PUSH);
		stopNow.setText("Stop Entry Now");
		stopNow.setImage(IconHandler.getInstance().getImage("icon.button.stopentrynow"));
		stopNow.setEnabled(false);
		stopNow.addSelectionListener(stopEntryAdapter);
		
		stop = new Button(buttonComposite, SWT.PUSH);
		stop.setText("Stop Entry");
		stop.setImage(IconHandler.getInstance().getImage("icon.button.stopentry"));
		stop.setEnabled(false);
		stop.addSelectionListener(stopEntryAdapter);
		
		FillLayout fillLayout = new FillLayout();
		setLayout(fillLayout);
		
		FormLayout layout = new FormLayout();
		layout.spacing = 5;
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		group.setLayout(layout);
		
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = gridLayout.marginWidth = 0;
		wdComposite.setLayout(gridLayout);
		
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		wdNumberCombo.setLayoutData(gridData);
		
		gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		wdSearch.setLayoutData(gridData);
		
		FormData layoutData = new FormData();
		layoutData.left = new FormAttachment(20);
		layoutData.top = new FormAttachment(0);
		layoutData.right = new FormAttachment(50);
		wdComposite.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.left = new FormAttachment(0);
		layoutData.top = new FormAttachment(wdComposite, 0, SWT.CENTER);
		layoutData.right = new FormAttachment(wdComposite);
		wdLabel.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.left = new FormAttachment(wdComposite, 0, SWT.LEFT);
		layoutData.top = new FormAttachment(wdComposite);
		layoutData.right = new FormAttachment(wdComposite, 0, SWT.RIGHT);
		accCombo.getControl().setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.left = new FormAttachment(0);
		layoutData.top = new FormAttachment(accCombo.getControl(), 0, SWT.CENTER);
		layoutData.right = new FormAttachment(accCombo.getControl());
		accLabel.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.left = new FormAttachment(accCombo.getControl(), 0, SWT.LEFT);
		layoutData.top = new FormAttachment(accCombo.getControl());
		layoutData.right = new FormAttachment(accCombo.getControl(), 0, SWT.RIGHT);
		crossCharge.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.left = new FormAttachment(0);
		layoutData.top = new FormAttachment(crossCharge, 0, SWT.CENTER);
		layoutData.right = new FormAttachment(crossCharge);
		ccLabel.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.left = new FormAttachment(crossCharge, 0, SWT.LEFT);
		layoutData.top = new FormAttachment(crossCharge);
		layoutData.right = new FormAttachment(crossCharge, 0, SWT.RIGHT);
		taskCode.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.left = new FormAttachment(0);
		layoutData.top = new FormAttachment(taskCode, 0, SWT.CENTER);
		layoutData.right = new FormAttachment(taskCode);
		tcLabel.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.left = new FormAttachment(wdComposite);
		layoutData.top = new FormAttachment(wdComposite, 0, SWT.CENTER);
		layoutData.right = new FormAttachment(57);
		noteLabel.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.left = new FormAttachment(noteLabel);
		layoutData.top = new FormAttachment(0);
		layoutData.right = new FormAttachment(100);
		layoutData.bottom = new FormAttachment(taskCode, 0, SWT.BOTTOM);
		note.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.left = new FormAttachment(0);
		layoutData.top = new FormAttachment(note);
		layoutData.right = new FormAttachment(100);
		leLabel.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.left = new FormAttachment(0);
		layoutData.top = new FormAttachment(leLabel);
		layoutData.right = new FormAttachment(100);
		layoutData.height = 5 * lastEntriesTable.getItemHeight();
		lastEntriesTable.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.left = new FormAttachment(0);
		layoutData.top = new FormAttachment(lastEntriesTable.getControl());
		layoutData.right = new FormAttachment(100);
		layoutData.bottom = new FormAttachment(100);
		buttonComposite.setLayoutData(layoutData);
		
		gridLayout = new GridLayout(4, true);
		gridLayout.marginHeight = gridLayout.marginWidth = 0;
		buttonComposite.setLayout(gridLayout);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		startNow.setLayoutData(gridData);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		start.setLayoutData(gridData);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		stopNow.setLayoutData(gridData);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		stop.setLayoutData(gridData);
		
		addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				IconHandler.getInstance().disposeInstance("icon.button.startentry");
				IconHandler.getInstance().disposeInstance("icon.button.startentrynow");
				IconHandler.getInstance().disposeInstance("icon.button.stopentry");
				IconHandler.getInstance().disposeInstance("icon.button.stopentrynow");
			}
		});
		
	}
	
	public void setLibrary(WorkDirectiveLibrary library) {
		clearSelection();
		this.library = library;
		if (this.library != null) {
			wdNumberCombo.setItems(library.getWorkDirectiveNumbers(WorkDirectiveLibrary.Mode.ALL));
			library.addWorkDirectiveLibraryChangedListener(new WorkDirectiveLibraryChangeListener() {
				
				@Override
				public void libraryChanged(WorkDirectiveLibrary library) {
					clearSelection();
					wdNumberCombo.setItems(library.getWorkDirectiveNumbers(WorkDirectiveLibrary.Mode.ALL));
				}
			});
		}
		else {
			wdNumberCombo.setEnabled(false);
		}
	}
	
	public void setTimeSheet(TimeSheet timeSheet) {
		this.timeSheet = timeSheet;
		if (this.timeSheet != null) {
			wdNumberCombo.setEnabled(true);
			accCombo.getControl().setEnabled(true);
			crossCharge.setEnabled(true);
			taskCode.setEnabled(true);
			note.setEnabled(true);
			lastEntriesTable.clearEntries();
			timeEntryDialog.setSheetDate(timeSheet.getSheetDate());
			clearSelection();
			if (!this.timeSheet.getTimeSheetEntries().isEmpty() && !this.timeSheet.getLastTimeSheetEntry().isFinished()) {
				stop.setEnabled(true);
				stopNow.setEnabled(true);
			}
			else {
				stop.setEnabled(false);
				stopNow.setEnabled(false);
			}
			this.timeSheet.addTimeSheetChangeListener(new TimeSheetChangeListener() {
				
				@Override
				public void timeSheetChanged(TimeSheet timeSheet) {
					clearSelection();
					if (!timeSheet.getTimeSheetEntries().isEmpty() && !timeSheet.getLastTimeSheetEntry().isFinished()) {
						if (timeSheet.getLastEntryTimeMade().lteq(DateTime.now(TimeZone.getDefault()))) {
							stopNow.setEnabled(true);
						} else {
							stopNow.setEnabled(false);
						}
						stop.setEnabled(true);
						
					}
					else {
						stop.setEnabled(false);
						stopNow.setEnabled(false);
					}
				}
			});
		}
		else {
			lastEntriesTable.clearEntries();
			timeEntryDialog.setSheetDate(null);
			clearSelection();
			wdNumberCombo.setEnabled(false);
			accCombo.getControl().setEnabled(false);
			crossCharge.setEnabled(false);
			taskCode.setEnabled(false);
			note.setEnabled(false);
			crossCharge.setText("");
			taskCode.setText("");
			note.setText("");
		}
	}
	
	private void clearSelection() {
		wdNumberCombo.deselectAll();
		wdNumberCombo.clearSelection();
		accCombo.clearAccounts();
		crossCharge.setText("");
		taskCode.setText("");
		note.setText("");
		selectedWorkDirective = null;
		setStartControlsEnabled(false);
	}
	
	private void setStartControlsEnabled(boolean enabled) {
		if (enabled) {
			if (timeSheet.getSheetDate().equals(DateTime.today(TimeZone.getDefault())) &&
					timeSheet.getLastEntryTimeMade().lt(DateTimeUtil.truncate(DateTime.now(TimeZone.getDefault())))) {
				startNow.setEnabled(enabled);
			}
			start.setEnabled(enabled);
		}
		else {
			startNow.setEnabled(enabled);
			start.setEnabled(enabled);
		}
	}
}
