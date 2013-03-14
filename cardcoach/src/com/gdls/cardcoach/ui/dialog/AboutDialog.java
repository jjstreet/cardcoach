package com.gdls.cardcoach.ui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.gdls.cardcoach.CardCoachContext;
import com.gdls.cardcoach.ui.FontManager;
import com.gdls.cardcoach.ui.Positioner;
import com.gdls.cardcoach.ui.composite.DialogControlsBuilder;
import com.gdls.cardcoach.ui.event.DialogSelectionListener;

public class AboutDialog extends Dialog {

	private CardCoachContext context;
	
	private Shell parent;
	private Shell shell;
	
	public AboutDialog(CardCoachContext context, Shell parent) {
		super(parent, SWT.None);
		this.parent = parent;
		this.context = context;
	}
	
	public void open() {
		createShell();
		createContents();
		shell.pack();
		shell.setSize(shell.computeSize(400, 200));
		Positioner.centerOnParent(parent, shell);
		shell.open();
		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) shell.getDisplay().sleep();
		}
	}
	
	private void createShell() {
		shell = new Shell(parent, SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setText("About");
		shell.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				FontManager.getInstance().disposeInstance("default.16");
			}
		});
	}
	
	private void createContents() {
		final Composite iComposite = new Composite(shell, SWT.NONE);
		final Label appName = new Label(iComposite, SWT.CENTER);
		appName.setFont(FontManager.getInstance().getFont("default.16"));
		appName.setText(context.getApplicationTitle());
		final Label appVersion = new Label(iComposite, SWT.CENTER);
		appVersion.setText(context.getApplicationVersion());
		final Composite dialogControls = new DialogControlsBuilder()
				.setPositiveButtonText("Ok")
				.addDialogSelectionListener(new DialogSelectionListener() {
					
					@Override
					public void dialogWidgetSelected(int which) {
						shell.dispose();
					}
				})
				.createComposite(shell);
		GridLayout layout = new GridLayout();
		shell.setLayout(layout);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		iComposite.setLayoutData(layoutData);
		layoutData = new GridData(SWT.RIGHT, SWT.FILL, true, false);
		dialogControls.setLayoutData(layoutData);
		layout = new GridLayout();
		iComposite.setLayout(layout);
		layoutData = new GridData(SWT.CENTER, SWT.TOP, true, false);
		appName.setLayoutData(layoutData);
		layoutData = new GridData(SWT.CENTER, SWT.TOP, true, false);
		appVersion.setLayoutData(layoutData);
	}
}
