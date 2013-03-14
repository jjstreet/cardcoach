package com.gdls.cardcoach.ui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.gdls.cardcoach.timecard.TimeCard;
import com.gdls.cardcoach.timesheet.TimeSheet;
import com.gdls.cardcoach.timesheet.TimeSheetChangeListener;

public class TimeCardComposite extends Composite {
	
	private TimeSheet timeSheet;
	
	private EmployeeComposite employeeComposite;
	private TimeDetailsComposite timeDetailsComposite;
	private TimeCardHoursComposite timeCardHoursComposite;
	
	private boolean timeSheetChanged;
	
	public TimeCardComposite(Composite parent) {
		super(parent, SWT.NONE);
		
		addListener(SWT.Show, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				updateView(true);
			}
		});
		
		employeeComposite = new EmployeeComposite(this);
		
		timeDetailsComposite = new TimeDetailsComposite(this);
		
		final Label separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		
		timeCardHoursComposite = new TimeCardHoursComposite(this);
		
		GridLayout layout = new GridLayout();
		setLayout(layout);
		
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		employeeComposite.setLayoutData(layoutData);
		
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		timeDetailsComposite.setLayoutData(layoutData);
		
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		separator.setLayoutData(layoutData);
		
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		timeCardHoursComposite.setLayoutData(layoutData);
	}
	
	public void setTimeSheet(TimeSheet timeSheet) {
		this.timeSheet = timeSheet;
		timeSheetChanged = true;
		employeeComposite.setTimeSheet(timeSheet);
		timeDetailsComposite.setTimeSheet(timeSheet);
		if (this.timeSheet != null) {
			this.timeSheet.addTimeSheetChangeListener(new TimeSheetChangeListener() {
				
				@Override
				public void timeSheetChanged(TimeSheet timeSheet) {
					timeSheetChanged = true;
				}
			});
		}
		updateView();
	}
	
	private void updateView() {
		updateView(false);
	}
	
	private void updateView(boolean force) {
		if (timeSheetChanged && (isVisible() || force)) {
			// Need to update its hours listing.
			if (timeSheet != null) {
				timeCardHoursComposite.setContent(TimeCard.newFromTimeSheet(timeSheet));
			}
			else {
				timeCardHoursComposite.setContent(null);
			}
			timeSheetChanged = false;
			timeCardHoursComposite.layout();
		}
	}
}
