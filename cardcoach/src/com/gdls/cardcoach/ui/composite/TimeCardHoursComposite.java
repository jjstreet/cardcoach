package com.gdls.cardcoach.ui.composite;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.gdls.cardcoach.timecard.TimeCard;
import com.gdls.cardcoach.timecard.TimeCardEntry;
import com.gdls.cardcoach.ui.WidgetBuilder;

public class TimeCardHoursComposite extends Composite {
	
	private ScrolledComposite scrolled;
	private Composite container;
	
	private ArrayList<EntryComposite> entries;
	
	public TimeCardHoursComposite(Composite parent) {
		super(parent, SWT.BORDER);
		
		entries = new ArrayList<EntryComposite>();
		
		scrolled = new ScrolledComposite(this, SWT.V_SCROLL);
		scrolled.setExpandHorizontal(true);
		scrolled.setExpandVertical(true);
		
		FillLayout fillLayout = new FillLayout();
		setLayout(fillLayout);
		
		container = new Composite(scrolled, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 5;
		container.setLayout(gridLayout);
		
		scrolled.setContent(container);
		scrolled.setMinHeight(container.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		scrolled.getVerticalBar().setIncrement(20);
		scrolled.getVerticalBar().setPageIncrement(200);
		
	}
	
	public void setContent(final TimeCard timeCard) {
		for (EntryComposite entry : entries) {
			entry.dispose();
		}
		entries.clear();
		container.dispose();
		setRedraw(false);
		container = new Composite(scrolled, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 5;
		container.setLayout(gridLayout);
		EntryComposite ec;
		GridData gridData;
		if (timeCard != null) {
			for (TimeCardEntry cardEntry : timeCard.getTimeCardEntries()) {
				ec = new EntryComposite(container);
				ec.setContent(cardEntry);
				entries.add(ec);
				gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
				ec.setLayoutData(gridData);
			}
		}
		scrolled.setContent(container);
		scrolled.setMinHeight(container.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		setRedraw(true);
	}
	
	private static class EntryComposite extends Composite {
		
		private Label wdLabel;
		private Label accLabel;
		private Label ccLabel;
		private Label tcLabel;
		private Label hoursLabel;
		private Label noteLabel;
		
		private Text wdText;
		private Text accText;
		private Text ccText;
		private Text tcText;
		private Text hoursText;
		private Text noteText;
		
		private Button wdCopy;
		private Button accCopy;
		private Button ccCopy;
		private Button tcCopy;
		private Button hoursCopy;
		private Button noteCopy;
		
		public EntryComposite(Composite parent) {
			super(parent, SWT.NONE);
			addDisposeListener(new DisposeListener() {
				
				@Override
				public void widgetDisposed(DisposeEvent event) {
					wdLabel.dispose();
					accLabel.dispose();
					ccLabel.dispose();
					tcLabel.dispose();
					hoursLabel.dispose();
					noteLabel.dispose();
					wdText.dispose();
					accText.dispose();
					ccText.dispose();
					tcText.dispose();
					hoursText.dispose();
					noteText.dispose();
					wdCopy.dispose();
					accCopy.dispose();
					ccCopy.dispose();
					tcCopy.dispose();
					hoursCopy.dispose();
					noteCopy.dispose();
				}
			});
			
			wdLabel = new Label(this, SWT.LEFT);
			wdLabel.setText("Work Directive:");
			
			accLabel = new Label(this, SWT.LEFT);
			accLabel.setText("Account:");
			
			ccLabel = new Label(this, SWT.LEFT);
			ccLabel.setText("Cross Charge Dept:");
			
			tcLabel = new Label(this, SWT.LEFT);
			tcLabel.setText("Task Code:");
			
			hoursLabel = new Label(this, SWT.LEFT);
			hoursLabel.setText("Hours:");
			
			wdText = WidgetBuilder.newText(this, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
			accText = WidgetBuilder.newText(this, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
			ccText = WidgetBuilder.newText(this, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
			tcText = WidgetBuilder.newText(this, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
			hoursText = WidgetBuilder.newText(this, SWT.SINGLE | SWT.RIGHT | SWT.BORDER | SWT.READ_ONLY);
			
			wdCopy = generateCopyButton(wdText);
			accCopy = generateCopyButton(accText);
			ccCopy = generateCopyButton(ccText);
			tcCopy = generateCopyButton(tcText);
			hoursCopy = generateCopyButton(hoursText);
			
			noteLabel = new Label(this, SWT.LEFT);
			noteLabel.setText("Note:");
			
			noteText = WidgetBuilder.newText(this, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
			noteCopy = generateCopyButton(noteText);
			
			GridLayout layout = new GridLayout(5, true);
			this.setLayout(layout);
			
			GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			wdLabel.setLayoutData(layoutData);
			
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			accLabel.setLayoutData(layoutData);
			
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			ccLabel.setLayoutData(layoutData);
			
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			tcLabel.setLayoutData(layoutData);
			
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			hoursLabel.setLayoutData(layoutData);
			
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			wdText.setLayoutData(layoutData);
			
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			accText.setLayoutData(layoutData);
			
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			ccText.setLayoutData(layoutData);
			
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			tcText.setLayoutData(layoutData);
			
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
			hoursText.setLayoutData(layoutData);
			
			layoutData = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
			wdCopy.setLayoutData(layoutData);
			
			layoutData = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
			accCopy.setLayoutData(layoutData);
			
			layoutData = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
			ccCopy.setLayoutData(layoutData);
			
			layoutData = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
			tcCopy.setLayoutData(layoutData);
			
			layoutData = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
			hoursCopy.setLayoutData(layoutData);
			
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1);
			noteLabel.setLayoutData(layoutData);
			
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1);
			layoutData.heightHint = 4 * noteText.getLineHeight();
			noteText.setLayoutData(layoutData);
			
			layoutData = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 5, 1);
			noteCopy.setLayoutData(layoutData);
			
			
			wdCopy.setEnabled(false);
			accCopy.setEnabled(false);
			ccCopy.setEnabled(false);
			tcCopy.setEnabled(false);
			hoursCopy.setEnabled(false);
			noteCopy.setEnabled(false);
		}
		
		public void setContent(TimeCardEntry entry) {
			if (entry.getWorkDirective() != null && !entry.getWorkDirective().isEmpty()) {
				wdText.setText(entry.getWorkDirective());
				wdCopy.setEnabled(true);
			}
			if (entry.getAccount() != null && !entry.getAccount().isEmpty()) {
				accText.setText(entry.getAccount());
				accCopy.setEnabled(true);
			}
			if (entry.getCrossCharge() != null && !entry.getCrossCharge().isEmpty()) {
				ccText.setText(entry.getCrossCharge());
				ccCopy.setEnabled(true);
			}
			if (entry.getTaskCode() != null && !entry.getTaskCode().isEmpty()) {
				tcText.setText(entry.getTaskCode());
				tcCopy.setEnabled(true);
			}
			if (entry.getHours() != null) {
				hoursText.setText(entry.getHours().toString());
				hoursCopy.setEnabled(true);
			}
			if (entry.getNotes() != null && entry.getNotes().size() > 0) {
				StringBuilder builder = new StringBuilder();
				Iterator<String> iterator = entry.getNotes().iterator();
				while (!entry.getNotes().isEmpty() && iterator.hasNext()) {
					builder.append(String.format("- %s", iterator.next()));
					if (iterator.hasNext()) {
						builder.append(String.format("%n"));
					}
				}
				noteText.setText(builder.toString());
				noteCopy.setEnabled(true);
			}
		}
		
		private Button generateCopyButton(final Text text) {
			Button button = new Button(this, SWT.PUSH);
			button.setText("Copy");
			button.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent event) {
					if (text.getText().length() > 0) {
						text.selectAll();
						text.copy();
					}
				}
			});
			return button;
		}
		
		public void setEnabled(boolean enabled) {
			wdText.setEnabled(enabled);
			accText.setEnabled(enabled);
			ccText.setEnabled(enabled);
			tcText.setEnabled(enabled);
			hoursText.setEnabled(enabled);
			noteText.setEnabled(enabled);
			wdCopy.setEnabled(enabled);
			accCopy.setEnabled(enabled);
			ccCopy.setEnabled(enabled);
			tcCopy.setEnabled(enabled);
			hoursCopy.setEnabled(enabled);
			noteCopy.setEnabled(enabled);
		}
	}
}
