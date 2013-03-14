package com.gdls.cardcoach.ui.dialog;

import hirondelle.date4j.DateTime;
import net.streetj.swt.widgets.ctime.CAnalogTimePicker;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.gdls.cardcoach.timesheet.TimeSheetEntry;
import com.gdls.cardcoach.ui.Positioner;
import com.gdls.cardcoach.ui.WidgetBuilder;
import com.gdls.cardcoach.ui.composite.DialogControlsBuilder;
import com.gdls.cardcoach.ui.event.DialogSelectionListener;

public class ViewTimeSheetEntryDialog extends Dialog {
	
	private Shell parent;
	private Shell shell;
	
	private Label wdNumberLabel;
	private Text wdNumberText;
	private Label accLabel;
	private Text accText;
	private Label ccLabel;
	private Text ccText;
	private Label tcLabel;
	private Text tcText;
	private Label nLabel;
	private Text nText;
	private Label stLabel;
	private CAnalogTimePicker startTimeSelector;
	private Label etLabel;
	private CAnalogTimePicker endTimeSelector;
	private Label suspLabel;
	private Button suspButton;
	private Image suspImage;
	
	private Composite dialogControls;
	
	private TimeSheetEntry entry;

	public ViewTimeSheetEntryDialog(Shell parent) {
		super(parent);
		this.parent = parent;
	}
	
	private void widgetDisposed(DisposeEvent event) {
	}
	
	public void open(TimeSheetEntry entry) {
		this.entry = entry;
		createShell();
		createContents();
		shell.pack();
		shell.setSize(shell.computeSize(500, SWT.DEFAULT));
		shell.layout();
		Positioner.centerOnParent(parent, shell);
		replaceControls();
		shell.open();
		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) shell.getDisplay().sleep();
		}
	}
	
	private void createShell() {
		shell = new Shell(parent, SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setText("View Time Sheet Entry");
		shell.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent event) {
				ViewTimeSheetEntryDialog.this.widgetDisposed(event);
			}
		});
	}
	
	private void createContents() {
		final Composite composite = new Composite(shell, SWT.NONE);
		
		wdNumberLabel = new Label(composite, SWT.RIGHT);
		wdNumberLabel.setText("Work Directive:");
		
		wdNumberText = WidgetBuilder.newText(composite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		wdNumberText.setText(entry.getWorkDirective().getNumber());
		
		accLabel = new Label(composite, SWT.RIGHT);
		accLabel.setText("Account:");
		
		accText = WidgetBuilder.newText(composite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		accText.setText(
				entry.getWorkDirective().getSelectedAccount().getNumber() + "/" +
				entry.getWorkDirective().getSelectedAccount().getNumberNextWeek());
		
		ccLabel = new Label(composite, SWT.RIGHT);
		ccLabel.setText("Cross Charge Dept:");
		
		ccText = WidgetBuilder.newText(composite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		ccText.setText(entry.getCrossCharge());
		
		tcLabel = new Label(composite, SWT.RIGHT);
		tcLabel.setText("Task Code:");
		
		tcText = WidgetBuilder.newText(composite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		tcText.setText(entry.getTaskCode());
		
		nLabel = new Label(composite, SWT.RIGHT);
		nLabel.setText("Note:");
		
		nText = WidgetBuilder.newText(composite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		nText.setText(entry.getNote());
		
		suspButton = new Button(composite, SWT.CHECK);
		suspButton.setText("Suspended");
		suspButton.setSelection(entry.isSuspended());
		suspButton.setVisible(false);

		suspLabel = new Label(composite, SWT.NONE);
		
		final Composite tComposite = new Composite(shell, SWT.NONE);
		
		stLabel = new Label(tComposite, SWT.CENTER);
		stLabel.setText("Start Time:");
		
		etLabel = new Label(tComposite, SWT.CENTER);
		etLabel.setText("End Time:");
		
		startTimeSelector = new CAnalogTimePicker(tComposite, SWT.BORDER | SWT.READ_ONLY);
		DateTime startTime = entry.getStartTime();
		if (startTime != null) {
			startTimeSelector.setTime(
					startTime.getHour(),
					startTime.getMinute(),
					startTime.getSecond());
		}
		else {
			startTimeSelector.setVisible(false);
		}
		
		endTimeSelector = new CAnalogTimePicker(tComposite, SWT.BORDER | SWT.READ_ONLY);
		DateTime endTime = entry.getEndTime();
		if (endTime != null) {
			endTimeSelector.setTime(
					endTime.getHour(),
					endTime.getMinute(),
					endTime.getSecond());
		}
		else {
			endTimeSelector.setVisible(false);
		}
		
		createDialogControls();
		
		GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		composite.setLayoutData(gridData);
		
		gridData = new GridData(SWT.RIGHT, SWT.FILL, true, false);
		dialogControls.setLayoutData(gridData);
		
		FormLayout formLayout = new FormLayout();
		formLayout.spacing = 5;
		composite.setLayout(formLayout);
		
		FormData formData = new FormData();
		formData.left = new FormAttachment(30);
		formData.top = new FormAttachment(0);
		formData.right = new FormAttachment(80);
		wdNumberText.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(30);
		formData.top = new FormAttachment(wdNumberText);
		formData.right = new FormAttachment(80);
		accText.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(30);
		formData.top = new FormAttachment(accText);
		formData.right = new FormAttachment(80);
		ccText.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(30);
		formData.top = new FormAttachment(ccText);
		formData.right = new FormAttachment(80);
		tcText.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(30);
		formData.top = new FormAttachment(tcText);
		formData.right = new FormAttachment(80);
		formData.height = nText.getLineHeight() * 3;
		nText.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(30);
		formData.top = new FormAttachment(nText);
		formData.right = new FormAttachment(60);
		formData.bottom = new FormAttachment(100);
		suspButton.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(suspButton, 0, SWT.LEFT);
		formData.top = new FormAttachment(suspButton, 0, SWT.TOP);
		formData.right = new FormAttachment(suspButton, 0, SWT.RIGHT);
		formData.bottom = new FormAttachment(suspButton, 0, SWT.BOTTOM);
		suspLabel.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(wdNumberText, 0, SWT.CENTER);
		formData.right = new FormAttachment(wdNumberText);
		wdNumberLabel.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(accText, 0, SWT.CENTER);
		formData.right = new FormAttachment(accText);
		accLabel.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(ccText, 0, SWT.CENTER);
		formData.right = new FormAttachment(ccText);
		ccLabel.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(tcText, 0, SWT.CENTER);
		formData.right = new FormAttachment(tcText);
		tcLabel.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.top = new FormAttachment(nText, 3, SWT.TOP);
		formData.right = new FormAttachment(nText);
		nLabel.setLayoutData(formData);
		
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
	}
	
	private void createDialogControls() {
		DialogControlsBuilder builder = new DialogControlsBuilder();
		builder.setPositiveButtonText("Ok");
		builder.addDialogSelectionListener(new DialogSelectionListener() {
			
			@Override
			public void dialogWidgetSelected(int which) {
				shell.dispose();
			}
		});
		dialogControls = builder.createComposite(shell);
	}
	
	private void replaceControls() {
		suspImage = new Image(parent.getDisplay(), suspButton.getBounds());
		GC gc = new GC(suspImage);
		suspButton.print(gc);
		gc.dispose();
		suspLabel.setImage(suspImage);
	}
}
