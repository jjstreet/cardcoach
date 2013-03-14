package com.gdls.cardcoach.ui.composite;

import java.math.BigDecimal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.gdls.cardcoach.timesheet.TimeSheet;
import com.gdls.cardcoach.timesheet.TimeSheetChangeListener;
import com.gdls.cardcoach.ui.FontManager;
import com.gdls.cardcoach.ui.WidgetBuilder;

public class TimeDetailsComposite extends Composite {
	
	private Group dateGroup;
	private Label dateLabel;
	private Text sheetDate;
	private Label startLabel;
	private Text startTime;
	private Label endLabel;
	private Text endTime;
	private Group hoursGroup;
	
	private Label chargedLabel;
	private Text chargedHours;
	private Label projectedChargedLabel;
	private Text projectedChargedHours;
	private Label notChargedLabel;
	private Text notChargedHours;
	
	private HoursTimer hoursTimer;
	
	private Font default16;

	public TimeDetailsComposite(Composite parent) {
		super(parent, SWT.NONE);
		default16 = FontManager.getInstance().getFont("default.16");
		dateGroup = new Group(this, SWT.NONE);
		dateGroup.setText("Date/Times");
		dateLabel = new Label(dateGroup, SWT.RIGHT);
		dateLabel.setText("Sheet/Card Date:");
		sheetDate = WidgetBuilder.newText(dateGroup, SWT.READ_ONLY);
		startLabel = new Label(dateGroup, SWT.RIGHT);
		startLabel.setText("Start Time:");
		startTime = WidgetBuilder.newText(dateGroup, SWT.READ_ONLY);
		endLabel = new Label(dateGroup, SWT.RIGHT);
		endLabel.setText("End Time:");
		endTime = WidgetBuilder.newText(dateGroup, SWT.READ_ONLY);
		hoursGroup = new Group(this, SWT.NONE);
		hoursGroup.setText("Hours");
		
		chargedLabel = new Label(hoursGroup, SWT.CENTER);
		chargedLabel.setText("Charged:");
		projectedChargedLabel = new Label(hoursGroup, SWT.CENTER);
		projectedChargedLabel.setText("Projected:");
		notChargedLabel = new Label(hoursGroup, SWT.CENTER);
		notChargedLabel.setText("Not Charged:");
		chargedHours = WidgetBuilder.newText(hoursGroup, SWT.READ_ONLY | SWT.CENTER);
		chargedHours.setFont(default16);
		projectedChargedHours = WidgetBuilder.newText(hoursGroup, SWT.READ_ONLY | SWT.CENTER);
		projectedChargedHours.setFont(default16);
		notChargedHours = WidgetBuilder.newText(hoursGroup, SWT.READ_ONLY | SWT.CENTER);
		notChargedHours.setFont(default16);
		
		GridLayout gridLayout = new GridLayout(2, true);
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		dateGroup.setLayoutData(gridData);
		
		FormLayout formLayout = new FormLayout();
		formLayout.marginHeight = 5;
		formLayout.marginWidth = 5;
		formLayout.spacing = 5;
		dateGroup.setLayout(formLayout);
		
		FormData formData = new FormData();
		formData.top = new FormAttachment(0);
		formData.left = new FormAttachment(35);
		formData.right = new FormAttachment(100);
		sheetDate.setLayoutData(formData);
		
		formData = new FormData();
		formData.top = new FormAttachment(sheetDate, 0, SWT.CENTER);
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(sheetDate);
		dateLabel.setLayoutData(formData);
		
		formData = new FormData();
		formData.top = new FormAttachment(sheetDate);
		formData.left = new FormAttachment(35);
		formData.right = new FormAttachment(100);
		startTime.setLayoutData(formData);
		
		formData = new FormData();
		formData.top = new FormAttachment(startTime, 0, SWT.CENTER);
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(startTime);
		startLabel.setLayoutData(formData);
		
		formData = new FormData();
		formData.top = new FormAttachment(startTime);
		formData.left = new FormAttachment(35);
		formData.right = new FormAttachment(100);
		endTime.setLayoutData(formData);
		
		formData = new FormData();
		formData.top = new FormAttachment(endTime, 0, SWT.CENTER);
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(endTime);
		endLabel.setLayoutData(formData);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		hoursGroup.setLayoutData(gridData);
		
		gridLayout = new GridLayout(3, true);
		hoursGroup.setLayout(gridLayout);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		chargedLabel.setLayoutData(gridData);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		projectedChargedLabel.setLayoutData(gridData);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		notChargedLabel.setLayoutData(gridData);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		chargedHours.setLayoutData(gridData);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		projectedChargedHours.setLayoutData(gridData);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		notChargedHours.setLayoutData(gridData);
		
		addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent event) {
				default16 = null;
				FontManager.getInstance().disposeInstance("default.16");
			}
		});
	}
	
	public void setChargedHours(String hours) {
		Point selected = chargedHours.getSelection();
		chargedHours.setText(hours);
		chargedHours.setSelection(selected);
	}
	
	public void setProjectedChargedHours(String hours) {
		Point selected = projectedChargedHours.getSelection();
		projectedChargedHours.setText(hours);
		projectedChargedHours.setSelection(selected);
	}
	
	public void setNotChargedHours(String hours) {
		Point selected = notChargedHours.getSelection();
		notChargedHours.setText(hours);
		notChargedHours.setSelection(selected);
	}
	
	public void setTimeSheet(TimeSheet timeSheet) {
		if (timeSheet != null) {
			sheetDate.setText(timeSheet.getFormattedSheetDate());
			startTime.setText(timeSheet.getFormattedStartTime());
			endTime.setText(timeSheet.getFormattedEndTime());
			setChargedHours(timeSheet.getChargedTimeHours().toString());
			if (hoursTimer != null) {
				getDisplay().timerExec(-1, hoursTimer);
			}
			if (timeSheet.getChargedTimeHours(true).compareTo(BigDecimal.ZERO) < 0) {
				setProjectedChargedHours("N/A");
			}
			else {
				setProjectedChargedHours(timeSheet.getChargedTimeHours(true).toString());
			}
			setNotChargedHours(timeSheet.getGapTimeHours().toString());
			hoursTimer = new HoursTimer(this, timeSheet);
			getDisplay().timerExec(0, hoursTimer);
			timeSheet.addTimeSheetChangeListener(new TimeSheetChangeListener() {
				
				@Override
				public void timeSheetChanged(TimeSheet timeSheet) {
					sheetDate.setText(timeSheet.getFormattedSheetDate());
					startTime.setText(timeSheet.getFormattedStartTime());
					endTime.setText(timeSheet.getFormattedEndTime());
					setChargedHours(timeSheet.getChargedTimeHours().toString());
					if (timeSheet.getChargedTimeHours(true).compareTo(BigDecimal.ZERO) < 0) {
						setProjectedChargedHours("N/A");
					}
					else {
						setProjectedChargedHours(timeSheet.getChargedTimeHours(true).toString());
					}
					setNotChargedHours(timeSheet.getGapTimeHours().toString());
					hoursTimer.setTimeSheet(timeSheet);
				}
			});
		}
		else {
			sheetDate.setText("");
			sheetDate.setText("");
			sheetDate.setText("");
			setChargedHours("");
			setProjectedChargedHours("");
			setNotChargedHours("");
			if (hoursTimer != null) {
				getDisplay().timerExec(-1, hoursTimer);
			}
		}
	}
	
	public void setLocked(boolean locked) {
		sheetDate.setEnabled(locked);
		startTime.setEnabled(locked);
		endTime.setEnabled(locked);
		chargedHours.setEnabled(locked);
		projectedChargedHours.setEnabled(locked);
		notChargedHours.setEnabled(locked);
	}
	
	private static class HoursTimer implements Runnable {
		
		private final TimeDetailsComposite composite;
		private TimeSheet timeSheet;
		
		public HoursTimer(TimeDetailsComposite composite, TimeSheet timeSheet) {
			this.composite = composite;
			this.timeSheet = timeSheet;
		}
		
		@Override
		public void run() {
			if (!composite.isDisposed()) {
				composite.setChargedHours(timeSheet.getChargedTimeHours().toString());
				if (timeSheet.getChargedTimeHours(true).compareTo(BigDecimal.ZERO) < 0) {
					composite.setProjectedChargedHours("N/A");
				}
				else {
					composite.setProjectedChargedHours(timeSheet.getChargedTimeHours(true).toString());
				}
				composite.setNotChargedHours(timeSheet.getGapTimeHours(true).toString());
				composite.getDisplay().timerExec(10000, this);
			}
		}
		
		public void setTimeSheet(TimeSheet timeSheet) {
			this.timeSheet = timeSheet;
		}
	}
}