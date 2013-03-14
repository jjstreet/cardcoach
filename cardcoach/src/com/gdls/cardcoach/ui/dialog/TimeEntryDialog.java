package com.gdls.cardcoach.ui.dialog;

import hirondelle.date4j.DateTime;

import java.util.Locale;
import java.util.TimeZone;

import net.streetj.swt.widgets.ctime.CAnalogTimePicker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.gdls.cardcoach.ui.MessageFactory;
import com.gdls.cardcoach.ui.Positioner;
import com.gdls.cardcoach.ui.composite.DialogControlsBuilder;
import com.gdls.cardcoach.ui.event.DialogSelectionListener;

public class TimeEntryDialog extends Dialog {

	private Shell parent;
	private Shell shell;
	
	private CAnalogTimePicker timeSelector;
	private Label timeLabel;
	private Label minTimeLabel;
	private Label maxTimeLabel;
	
	private Composite dialogControls;
	
	private DateTime sheetDate;
	private DateTime minTime;
	private DateTime maxTime;
	private DateTime selectedTime;
	private DateTime returnedDateTime;
	
	public TimeEntryDialog(Shell parent) {
		super(parent);
		this.parent = parent;
	}
	
	private void widgetDisposed(DisposeEvent event) {
	}
	
	public void setSheetDate(DateTime sheetDate) {
		this.sheetDate = sheetDate;
	}
	
	public DateTime open(DateTime minTime, DateTime maxTime) {
		return open(null, minTime, maxTime);
	}
	
	public DateTime open(DateTime time, DateTime minTime, DateTime maxTime) {
		selectedTime = time;
		this.minTime = minTime;
		this.maxTime = maxTime;
		createShell();
		createContents();
		shell.pack();
		shell.setSize(shell.computeSize(400, SWT.DEFAULT));
		Positioner.centerOnParent(parent, shell);
		shell.open();
		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) shell.getDisplay().sleep();
		}
		
		return returnedDateTime;
	}
	
	private void createShell() {
		shell = new Shell(parent, SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setText("Start/Stop Time Entry");
		shell.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent event) {
				TimeEntryDialog.this.widgetDisposed(event);
			}
		});
	}
	
	private void createContents() {
		final Composite iComposite = new Composite(shell, SWT.NONE);
		
		final Label instructions = new Label(iComposite, SWT.WRAP);
		instructions.setText("Select a time that is greater than or equal to the minimum time and less than or equal to the maximum time");
		
		final Composite cComposite = new Composite(shell, SWT.NONE);
		
		timeLabel = new Label(cComposite, SWT.CENTER);
		timeLabel.setText("Selected Time:");
		
		timeSelector = new CAnalogTimePicker(cComposite, SWT.BORDER);
		if (selectedTime != null) {
			timeSelector.setTime(selectedTime.getHour(), selectedTime.getMinute(), selectedTime.getSecond());
		}
		else {
			DateTime currentTime = DateTime.now(TimeZone.getDefault());
			currentTime = new DateTime(
					sheetDate.getYear(),
					sheetDate.getMonth(),
					sheetDate.getDay(),
					currentTime.getHour(),
					currentTime.getMinute(),
					currentTime.getSecond(),
					null);
			if (minTime.gt(currentTime)) {
				timeSelector.setTime(minTime.getHour(), minTime.getMinute(), minTime.getSecond());
			}
			else if (maxTime.lt(currentTime)) {
				timeSelector.setTime(maxTime.getHour(), maxTime.getMinute(), maxTime.getSecond());
			}
			else {
				timeSelector.setTime(currentTime.getHour(), currentTime.getMinute(), currentTime.getSecond());
			}
		}
		minTimeLabel = new Label(cComposite, SWT.CENTER);
		minTimeLabel.setText(String.format("Minimum: %s", minTime.format("h12:mm:ss a", Locale.getDefault())));
		
		maxTimeLabel = new Label(cComposite, SWT.CENTER);
		maxTimeLabel.setText(String.format("Maximum: %s", maxTime.format("h12:mm:ss a", Locale.getDefault())));
		
		DialogControlsBuilder builder = new DialogControlsBuilder();
		builder.setPositiveButtonText("Ok");
		builder.setNegativeButtonText("Cancel");
		builder.addDialogSelectionListener(new DialogSelectionListener() {
			
			@Override
			public void dialogWidgetSelected(int which) {
				if (DialogControlsBuilder.POSITIVE_BUTTON == which) {
					if (createDateTime()) {
						shell.dispose();
					}
				}
				else {
					returnedDateTime = null;
					shell.dispose();
				}
			}
		});
		dialogControls = builder.createComposite(shell);
		
		GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		iComposite.setLayoutData(gridData);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		cComposite.setLayoutData(gridData);
		
		gridData = new GridData(SWT.RIGHT, SWT.FILL, true, false);
		dialogControls.setLayoutData(gridData);
		
		
		FormLayout formLayout = new FormLayout();
		formLayout.spacing = 5;
		iComposite.setLayout(formLayout);
		
		FormData formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		instructions.setLayoutData(formData);
		
		gridLayout = new GridLayout();
		gridLayout.marginHeight = gridLayout.marginWidth = 0;
		cComposite.setLayout(gridLayout);
		
		gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		timeLabel.setLayoutData(gridData);
		
		gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		gridData.widthHint = 180;
		gridData.heightHint = 180;
		timeSelector.setLayoutData(gridData);
		
		gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		minTimeLabel.setLayoutData(gridData);
		
		gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		maxTimeLabel.setLayoutData(gridData);
	}
	
	private boolean createDateTime() {
		returnedDateTime = new DateTime(
				sheetDate.getYear(),
				sheetDate.getMonth(),
				sheetDate.getDay(),
				timeSelector.getHours(),
				timeSelector.getMinutes(),
				timeSelector.getSeconds(),
				null);
		if (returnedDateTime.lt(minTime)) {
			MessageFactory.showError(shell, "Time must be greater than or equal to the minimum time.");
			return false;
		}
		if (returnedDateTime.gt(maxTime)) {
			MessageFactory.showError(shell, "Time must be less than or equal to the maximum time.");
			return false;
		}
		return true;
	}
}
