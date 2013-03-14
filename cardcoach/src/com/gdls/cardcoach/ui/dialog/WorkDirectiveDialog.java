package com.gdls.cardcoach.ui.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.gdls.cardcoach.employee.Employee;
import com.gdls.cardcoach.ui.MessageFactory;
import com.gdls.cardcoach.ui.Positioner;
import com.gdls.cardcoach.ui.WidgetBuilder;
import com.gdls.cardcoach.ui.composite.AccountComposite;
import com.gdls.cardcoach.ui.composite.DialogControlsBuilder;
import com.gdls.cardcoach.ui.event.DialogSelectionListener;
import com.gdls.cardcoach.workdirective.Account;
import com.gdls.cardcoach.workdirective.WorkDirective;

public class WorkDirectiveDialog extends Dialog {
	
	private static String ALLOWED_WD_CHARS = "^[A-Z0-9@]+$";
	
	public static final int MODE_VIEW = 1<<1;
	public static final int MODE_ADD = 1<<2;
	public static final int MODE_EDIT_SINGLE = 1<<3;
	public static final int MODE_EDIT_MULTIPLE = 1<<4;
	
	private static final Pattern allowedChars = Pattern.compile(ALLOWED_WD_CHARS);
	
	private int mode;
	
	private DialogResult result;
	
	private Shell parent;
	private Shell shell;
	private Label wdLabel;
	private Text wdNumberText;
	private Label descLabel;
	private Text descText;
	private Button descriptionNoChange;
	private Label snLabel;
	private Text snText;
	private Button suspenseNumberNoChange;
	// Account section
	private Label accLabel;
	private TabFolder accountsFolder;
	private Button accountsNoChange;
	private TabItem tabDirect;
	private TabItem tabUnion;
	private TabItem tabContractor;
	private AccountComposite accountsDirect;
	private AccountComposite accountsUnion;
	private AccountComposite accountsContractor;
	private Composite dialogControls;
	
	private WorkDirective wd;
	
	public WorkDirectiveDialog(Shell parent) {
		super(parent);
		this.parent = parent;
	}
	
	private void widgetDisposed(DisposeEvent event) {
	}
	
	public void open(DialogInput input, DialogResult result) throws DialogInputDataException {
		wd = null;
		mode = input.getMode();
		this.result = result;
		switch (mode) {
		case MODE_VIEW:
			if (input.getData("workDirective") == null) {
				throw new DialogInputDataException("workDirective");
			}
			wd = (WorkDirective) input.getData("workDirective");
			break;
		case MODE_ADD:
			wd = (WorkDirective) input.getData("workDirective");
			break;
		case MODE_EDIT_SINGLE:
			if (input.getData("workDirective") == null) {
				throw new DialogInputDataException("workDirective");
			}
			wd = (WorkDirective) input.getData("workDirective");
			break;
		case MODE_EDIT_MULTIPLE:
			break;
		}
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
		case MODE_VIEW:
			shell.setText("View Work Directive");
			break;
		case MODE_ADD:
			shell.setText("Add New Work Directive");
			break;
		case MODE_EDIT_SINGLE:
			shell.setText("Edit Work Directive");
			break;
		case MODE_EDIT_MULTIPLE:
			shell.setText("Edit Work Directives");
			break;
		}
		shell.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent event) {
				WorkDirectiveDialog.this.widgetDisposed(event);
			}
		});
	}
	
	private void createContents() {
		NoChangeSelection noChangeSelection;
		final Composite composite = new Composite(shell, SWT.NONE);
		
		wdLabel = new Label(composite, SWT.RIGHT);
		wdLabel.setText("Work Directive:");
		
		wdNumberText = WidgetBuilder.newText(composite, SWT.SINGLE | SWT.BORDER);
		if (mode == MODE_VIEW) {
			wdNumberText.setEditable(false);
			wdNumberText.setText(wd.getNumber());
		}
		else if (mode == MODE_ADD) {
			wdNumberText.addVerifyListener(new WorkDirectiveVerifier(wdNumberText));
			if (wd != null) {
				wdNumberText.setText(wd.getNumber());
			}
		}
		else if (mode == MODE_EDIT_SINGLE) {
			wdNumberText.setEditable(false);
			wdNumberText.setText(wd.getNumber());
		}
		else if (mode == MODE_EDIT_MULTIPLE) {
			wdNumberText.setEditable(false);
			wdNumberText.setText("Multiple");
		}
		
		descLabel = new Label(composite, SWT.RIGHT);
		descLabel.setText("Description:");
		
		descText = WidgetBuilder.newText(composite, SWT.MULTI | SWT.BORDER);
		if (mode == MODE_VIEW) {
			descText.setEditable(false);
			descText.setText(wd.getDescription());
		}
		else if (mode == MODE_ADD) {
			if (wd != null) {
				descText.setText(wd.getDescription());
			}
		}
		else if (mode == MODE_EDIT_SINGLE) {
			descText.setText(wd.getDescription());
		}
		else if (mode == MODE_EDIT_MULTIPLE) {
			descriptionNoChange = new Button(composite, SWT.CHECK);
			descriptionNoChange.setText("No Change");
			noChangeSelection = new NoChangeSelection(descText);
			descriptionNoChange.addSelectionListener(noChangeSelection);
			descriptionNoChange.setSelection(true);
			noChangeSelection.doNoChange(descText, true);
		}
		
		snLabel = new Label(composite, SWT.RIGHT);
		snLabel.setText("Suspense Number:");
		
		snText = WidgetBuilder.newText(composite, SWT.SINGLE | SWT.BORDER);
		if (mode == MODE_VIEW) {
			snText.setEditable(false);
			if (wd.getSuspenseNumber() != null) {
				snText.setText(wd.getSuspenseNumber());
			}
		}
		else if (mode == MODE_ADD) {
			snText.addVerifyListener(new WorkDirectiveVerifier(snText));
			if (wd != null && wd.getSuspenseNumber() != null) {
				snText.setText(wd.getSuspenseNumber());
			}
		}
		else if (mode == MODE_EDIT_SINGLE) {
			if (wd.getSuspenseNumber() != null) {
				snText.setText(wd.getSuspenseNumber());
			}
			snText.addVerifyListener(new WorkDirectiveVerifier(snText));
		}
		else if (mode == MODE_EDIT_MULTIPLE) {
			snText.addVerifyListener(new WorkDirectiveVerifier(snText));
			suspenseNumberNoChange = new Button(composite, SWT.CHECK);
			suspenseNumberNoChange.setText("No Change");
			noChangeSelection = new NoChangeSelection(snText);
			suspenseNumberNoChange.addSelectionListener(noChangeSelection);
			suspenseNumberNoChange.setSelection(true);
			noChangeSelection.doNoChange(snText, true);
		}
		
		accLabel = new Label(shell, SWT.LEFT);
		accLabel.setText("Accounts:");
		
		if (mode == MODE_EDIT_MULTIPLE) {
			accountsNoChange = new Button(shell, SWT.CHECK);
			accountsNoChange.setText("No Change");
		}
		
		accountsFolder = new TabFolder(shell, SWT.NONE);
		accountsDirect = new AccountComposite(accountsFolder);
		accountsUnion = new AccountComposite(accountsFolder);
		accountsContractor = new AccountComposite(accountsFolder);
		
		if (mode == MODE_VIEW) {
			accountsDirect.setEditable(false);
			accountsUnion.setEditable(false);
			accountsContractor.setEditable(false);
		}
		else if (mode == MODE_EDIT_MULTIPLE) {
			Control[] controls = new Control[] {accountsDirect, accountsUnion, accountsContractor};
			noChangeSelection = new NoChangeSelection(controls);
			accountsNoChange.addSelectionListener(noChangeSelection);
			accountsNoChange.setSelection(true);
			noChangeSelection.doNoChange(controls, true);
		}
		List<Account> accounts;
		if (mode == MODE_VIEW || mode == MODE_EDIT_SINGLE || (mode == MODE_ADD && wd != null)) {
			accountsDirect.setSelectedAccounts(wd.getAccounts(Employee.Type.DIRECT));
			accounts = Account.allAccounts(Employee.Type.DIRECT);
			accounts.removeAll(accountsDirect.getSelectedAccounts());
			accountsDirect.setSelectableAcounts(accounts);
			accountsUnion.setSelectedAccounts(wd.getAccounts(Employee.Type.UNION));
			accounts = Account.allAccounts(Employee.Type.UNION);
			accounts.removeAll(accountsUnion.getSelectedAccounts());
			accountsUnion.setSelectableAcounts(accounts);
			accountsContractor.setSelectedAccounts(wd.getAccounts(Employee.Type.CONTRACTOR));
			accounts = Account.allAccounts(Employee.Type.CONTRACTOR);
			accounts.removeAll(accountsContractor.getSelectedAccounts());
			accountsContractor.setSelectableAcounts(accounts);
		}
		else {
			accounts = Account.allAccounts(Employee.Type.DIRECT);
			accountsDirect.setSelectableAcounts(accounts);
			accounts = Account.allAccounts(Employee.Type.UNION);
			accountsUnion.setSelectableAcounts(accounts);
			accounts = Account.allAccounts(Employee.Type.CONTRACTOR);
			accountsContractor.setSelectableAcounts(accounts);
		}
		
		tabDirect = new TabItem(accountsFolder, SWT.NONE);
		tabDirect.setText("Direct");
		tabDirect.setControl(accountsDirect);
		tabUnion = new TabItem(accountsFolder, SWT.NONE);
		tabUnion.setText("Union");
		tabUnion.setControl(accountsUnion);
		tabContractor = new TabItem(accountsFolder, SWT.NONE);
		tabContractor.setText("Contractor");
		tabContractor.setControl(accountsContractor);
		
		createDialogControls();
		
		GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		composite.setLayoutData(gridData);
		
		FormLayout formLayout = new FormLayout();
		formLayout.spacing = 5;
		composite.setLayout(formLayout);
		
		if (mode != MODE_EDIT_MULTIPLE) {
			FormData formData = new FormData();
			formData.left = new FormAttachment(30);
			formData.top = new FormAttachment(0);
			formData.right = new FormAttachment(80);
			wdNumberText.setLayoutData(formData);
			
			formData = new FormData();
			formData.left = new FormAttachment(30);
			formData.top = new FormAttachment(wdNumberText);
			formData.right = new FormAttachment(80);
			formData.height = descText.getLineHeight() * 3;
			descText.setLayoutData(formData);
			
			formData = new FormData();
			formData.left = new FormAttachment(30);
			formData.top = new FormAttachment(descText);
			formData.right = new FormAttachment(80);
			formData.bottom = new FormAttachment(100);
			snText.setLayoutData(formData);
			
			formData = new FormData();
			formData.left = new FormAttachment(0);
			formData.top = new FormAttachment(wdNumberText, 0, SWT.CENTER);
			formData.right = new FormAttachment(wdNumberText);
			wdLabel.setLayoutData(formData);
			
			formData = new FormData();
			formData.left = new FormAttachment(0);
			formData.top = new FormAttachment(descText, 3, SWT.TOP);
			formData.right = new FormAttachment(descText);
			descLabel.setLayoutData(formData);
			
			formData = new FormData();
			formData.left = new FormAttachment(0);
			formData.top = new FormAttachment(snText, 0, SWT.CENTER);
			formData.right = new FormAttachment(snText);
			snLabel.setLayoutData(formData);
		}
		else {
			FormData formData = new FormData();
			formData.left = new FormAttachment(30);
			formData.top = new FormAttachment(0);
			formData.right = new FormAttachment(80);
			wdNumberText.setLayoutData(formData);
			
			formData = new FormData();
			formData.left = new FormAttachment(30);
			formData.top = new FormAttachment(wdNumberText);
			formData.right = new FormAttachment(80);
			formData.height = descText.getLineHeight() * 3;
			descText.setLayoutData(formData);
			
			formData = new FormData();
			formData.left = new FormAttachment(30);
			formData.top = new FormAttachment(descText);
			formData.right = new FormAttachment(80);
			descriptionNoChange.setLayoutData(formData);
			
			formData = new FormData();
			formData.left = new FormAttachment(30);
			formData.top = new FormAttachment(descriptionNoChange);
			formData.right = new FormAttachment(80);
			snText.setLayoutData(formData);
			
			formData = new FormData();
			formData.left = new FormAttachment(30);
			formData.top = new FormAttachment(snText);
			formData.right = new FormAttachment(80);
			formData.bottom = new FormAttachment(100);
			suspenseNumberNoChange.setLayoutData(formData);
			
			formData = new FormData();
			formData.left = new FormAttachment(0);
			formData.top = new FormAttachment(wdNumberText, 0, SWT.CENTER);
			formData.right = new FormAttachment(wdNumberText);
			wdLabel.setLayoutData(formData);
			
			formData = new FormData();
			formData.left = new FormAttachment(0);
			formData.top = new FormAttachment(descText, 3, SWT.TOP);
			formData.right = new FormAttachment(descText);
			descLabel.setLayoutData(formData);
			
			formData = new FormData();
			formData.left = new FormAttachment(0);
			formData.top = new FormAttachment(snText, 0, SWT.CENTER);
			formData.right = new FormAttachment(snText);
			snLabel.setLayoutData(formData);
		}
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		accLabel.setLayoutData(gridData);
		
		if (mode == MODE_EDIT_MULTIPLE) {
			gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
			accountsNoChange.setLayoutData(gridData);
		}
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		accountsFolder.setLayoutData(gridData);
		
		gridData = new GridData(SWT.RIGHT, SWT.FILL, true, false);
		dialogControls.setLayoutData(gridData);
	}
	
	private void createDialogControls() {
		DialogControlsBuilder builder = new DialogControlsBuilder();
		builder.setPositiveButtonText("Ok");
		if (mode != MODE_VIEW) {
			builder.setNegativeButtonText("Cancel");
			builder.addDialogSelectionListener(new DialogSelectionListener() {
				
				@Override
				public void dialogWidgetSelected(int which) {
					if (DialogControlsBuilder.POSITIVE_BUTTON == which) {
						if (createWorkDirective()) {
							shell.dispose();
						}
						else {
							MessageFactory.showError(shell, "You must have a number, description, and at least one account to add or edit a work directive.");
						}
					}
					else {
						result.setReturnCode(DialogResult.RETURN_CANCEL);
						result.setData("workDirective", null);
						shell.dispose();
					}
				}
			});
		}
		else {
			builder.addDialogSelectionListener(new DialogSelectionListener() {
				
				@Override
				public void dialogWidgetSelected(int which) {
					result.setReturnCode(DialogResult.RETURN_OK);
					result.setData("workDirective", null);
					shell.dispose();
				}
			});
		}
		dialogControls = builder.createComposite(shell);
	}
	
	public boolean createWorkDirective() {
		if (mode == MODE_EDIT_SINGLE || mode == MODE_ADD) {
			if (wdNumberText.getText().isEmpty() || wdNumberText.getText().toCharArray().length != 10) {
				return false;
			}
			if (descText.getText().isEmpty()) {
				return false;
			}
			if (!snText.getText().isEmpty() && snText.getText().toCharArray().length != 10) {
				return false;
			}
			List<Account> accounts = new ArrayList<Account>();
			accounts.addAll(accountsDirect.getSelectedAccounts());
			accounts.addAll(accountsUnion.getSelectedAccounts());
			accounts.addAll(accountsContractor.getSelectedAccounts());
			if (accounts.size() == 0) {
				return false;
			}
			result.setData("workDirective", new WorkDirective(
					wdNumberText.getText(),
					accounts,
					descText.getText(),
					snText.getText()));
			result.setReturnCode(DialogResult.RETURN_OK);
			return true;
		}
		else if (mode == MODE_EDIT_MULTIPLE) {
			String description = null;
			String suspenseNumber = null;
			ArrayList<Account> accounts = null;
			if (!descriptionNoChange.getSelection()) {
				if (descText.getText() == "") {
					return false;
				}
				description = descText.getText();
			}
			if (!suspenseNumberNoChange.getSelection()) {
				if (!snText.getText().isEmpty() && snText.getText().toCharArray().length != 10) {
					return false;
				}
				suspenseNumber = snText.getText();
			}
			if (!accountsNoChange.getSelection()) {
				accounts = new ArrayList<Account>();
				accounts.addAll(accountsDirect.getSelectedAccounts());
				accounts.addAll(accountsUnion.getSelectedAccounts());
				accounts.addAll(accountsContractor.getSelectedAccounts());
				if (accounts.size() == 0) {
					return false;
				}
			}
			result.setData("workDirective", new WorkDirective(
					null,
					accounts,
					description,
					suspenseNumber));
			result.setReturnCode(DialogResult.RETURN_OK);
		}
		else {
			result.setData("workDirective", null);
			result.setReturnCode(DialogResult.RETURN_OK);
		}
		return true;
	}
	
	private static class WorkDirectiveVerifier implements VerifyListener {
		
		private final Text text;
		
		public WorkDirectiveVerifier(Text text) {
			this.text = text;
		}

		@Override
		public void verifyText(VerifyEvent event) {
			event.text = event.text.toUpperCase();
			if (event.text != "") {
				if (event.text.toCharArray().length > 10) {
					MessageFactory.showError(text.getShell(), "Work directives must only contain 10 characters.");
					event.doit = false;
					return;
				}
				if (!allowedChars.matcher(event.text).matches() || text.getText().concat(event.text).toCharArray().length - text.getSelectionCount() > 10) {
					MessageFactory.showError(text.getShell(), "Work directives must only contain 10 characters.");
					event.doit = false;
					return;
				}
			}
		}
	}
	
	private static class NoChangeSelection extends SelectionAdapter {
		
		private final Control[] controls;
		private Listener[] verifyListeners;
		
		public NoChangeSelection(Control... controls) {
			this.controls = controls;
		}
		
		@Override
		public void widgetSelected(SelectionEvent event) {
			final Button button = (Button) event.widget;
			doNoChange(controls, button.getSelection());
		}
		
		public final void doNoChange(Control control, boolean state) {
			if (state) {
				if (control instanceof Text) {
					final Text text = (Text) control;
					verifyListeners = text.getListeners(SWT.Verify);
					for (Listener listener : verifyListeners) {
						text.removeListener(SWT.Verify, listener);
					}
					text.setText("No Change");
					text.setForeground(text.getDisplay().getSystemColor(SWT.COLOR_GRAY));
					text.setEditable(false);
				}
				else {
					control.setEnabled(false);
				}
			}
			else {
				if (control instanceof Text) {
					final Text text = (Text) control;
					text.setText("");
					text.setForeground(text.getDisplay().getSystemColor(SWT.COLOR_WIDGET_FOREGROUND));
					text.setEditable(true);
					if (verifyListeners != null) {
						for (Listener listener : verifyListeners) {
							text.addListener(SWT.Verify, listener);
						}
					}
				}
				else {
					control.setEnabled(true);
				}
			}
		}
		
		
		public final void doNoChange(Control[] controls, boolean state) {
			for (Control control : controls) {
				doNoChange(control, state);
			}
		}
	}
}
