package com.gdls.cardcoach.ui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.gdls.cardcoach.timesheet.TimeSheet;
import com.gdls.cardcoach.workdirective.WorkDirectiveLibrary;

public class TimeSheetComposite extends Composite {
	
	private EmployeeComposite employeeComposite;
	private TimeDetailsComposite timeDetailsComposite;
	private SplitTimeComposite splitTimeComposite;
	private EntryControlComposite entryControlComposite;
	private EntriesComposite entriesComposite;
	
	
	public TimeSheetComposite(Composite parent) {
		super(parent, SWT.NONE);
		employeeComposite = new EmployeeComposite(this);
		timeDetailsComposite = new TimeDetailsComposite(this);
		
		final Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		
		splitTimeComposite = new SplitTimeComposite(this);
		
		entryControlComposite = new EntryControlComposite(this);
		
		entriesComposite = new EntriesComposite(this);
		
		GridLayout layout = new GridLayout();
		setLayout(layout);
		
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		employeeComposite.setLayoutData(layoutData);
		
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		timeDetailsComposite.setLayoutData(layoutData);
		
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		separator.setLayoutData(layoutData);
		
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		splitTimeComposite.setLayoutData(layoutData);
		
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		entryControlComposite.setLayoutData(layoutData);
		
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		entriesComposite.setLayoutData(layoutData);
	}
	
	public void setTimeSheet(TimeSheet timeSheet) {
		employeeComposite.setTimeSheet(timeSheet);
		timeDetailsComposite.setTimeSheet(timeSheet);
		splitTimeComposite.setTimeSheet(timeSheet);
		entriesComposite.setTimeSheet(timeSheet);
		entryControlComposite.setTimeSheet(timeSheet);
	}
	
	public void setLibrary(WorkDirectiveLibrary library) {
		entryControlComposite.setLibrary(library);
		entriesComposite.setLibrary(library);
	}
}
