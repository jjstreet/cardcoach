package com.gdls.cardcoach.ui.composite;

import java.math.BigDecimal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import com.gdls.cardcoach.timesheet.TimeSheet;
import com.gdls.cardcoach.timesheet.TimeSheetChangeListener;

public class SplitTimeComposite extends Composite {
	
	private Group splitGroup;
	private Composite splitComposite;
	private Label splitLabel;
	private Composite radioComposite;
	private Button yesButton;
	private Button noButton;
	private Composite splitHoursComposite;
	private Label splitAfterLabel;
	private Spinner hours;
	private Label hoursLabel;
	
	private TimeSheet timeSheet;
	
	public SplitTimeComposite(Composite parent) {
		super(parent, SWT.NONE);
		
		splitGroup = new Group(this, SWT.NONE);
		splitGroup.setText("Split Time");
		
		splitComposite = new Composite(splitGroup, SWT.NONE);
		
		splitLabel = new Label(splitComposite, SWT.NONE);
		splitLabel.setText("Split for Next Week:");
		
		radioComposite = new Composite(splitComposite, SWT.NONE);
		
		yesButton = new Button(radioComposite, SWT.RADIO);
		yesButton.setText("Yes");
		yesButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				timeSheet.setIsSplitting(true);
			}
		});
		
		noButton = new Button(radioComposite, SWT.RADIO);
		noButton.setText("No");
		noButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				timeSheet.setIsSplitting(false);
			}
		});
		
		splitHoursComposite= new Composite(splitGroup, SWT.NONE);
		
		splitAfterLabel = new Label(splitHoursComposite, SWT.NONE);
		splitAfterLabel.setText("Split After:");
		
		hours = new Spinner(splitHoursComposite, SWT.BORDER);
		hours.setValues(0, 0, 90, 1, 1, 10);
		hours.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				timeSheet.setSplitTime(hours.getSelection() * 360);
			}
		});
		
		hoursLabel = new Label(splitHoursComposite, SWT.NONE);
		hoursLabel.setText("Hours");
		
		FillLayout fillLayout = new FillLayout();
		fillLayout.marginWidth = 0;
		setLayout(fillLayout);
		
		GridLayout gridLayout = new GridLayout(2, true);
		splitGroup.setLayout(gridLayout);
		
		GridData gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		splitComposite.setLayoutData(gridData);
		
		gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		splitHoursComposite.setLayoutData(gridData);
		
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.center = true;
		rowLayout.marginBottom = 0;
		rowLayout.marginTop = 0;
		rowLayout.marginLeft = 0;
		rowLayout.marginRight = 0;
		rowLayout.spacing = 5;
		splitComposite.setLayout(rowLayout);
		
		rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.spacing = 5;
		radioComposite.setLayout(rowLayout);
		
		rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.center = true;
		rowLayout.marginBottom = 0;
		rowLayout.marginTop = 0;
		rowLayout.marginLeft = 0;
		rowLayout.marginRight = 0;
		rowLayout.spacing = 5;
		splitHoursComposite.setLayout(rowLayout);
		
		RowData rowData = new RowData();
		rowData.width = 45;
		hours.setLayoutData(rowData);
	}
	
	public void setTimeSheet(TimeSheet timeSheet) {
		this.timeSheet = timeSheet;
		if (this.timeSheet != null) {
			yesButton.setEnabled(true);
			noButton.setEnabled(true);
			hours.setEnabled(true);
			if (timeSheet.isSplitting()) {
				yesButton.setSelection(true);
				noButton.setSelection(false);
				hours.setEnabled(true);
			}
			else {
				yesButton.setSelection(false);
				noButton.setSelection(true);
				hours.setEnabled(false);
			}
			hours.setSelection(this.timeSheet.getSplitTimeHours().multiply(BigDecimal.TEN).intValue());
			this.timeSheet.addTimeSheetChangeListener(new TimeSheetChangeListener() {
				
				@Override
				public void timeSheetChanged(TimeSheet timeSheet) {
					if (timeSheet.isSplitting()) {
						yesButton.setSelection(true);
						noButton.setSelection(false);
						hours.setEnabled(true);
					}
					else {
						yesButton.setSelection(false);
						noButton.setSelection(true);
						hours.setEnabled(false);
					}
					hours.setSelection(timeSheet.getSplitTimeHours().multiply(BigDecimal.TEN).intValue());
				}
			});
		}
		else {
			yesButton.setSelection(false);
			noButton.setSelection(false);
			hours.setSelection(0);
			yesButton.setEnabled(false);
			noButton.setEnabled(false);
			hours.setEnabled(false);
		}
	}
}
