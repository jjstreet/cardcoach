package com.gdls.cardcoach.ui.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.gdls.cardcoach.ui.AccountTable;
import com.gdls.cardcoach.ui.icons.IconHandler;
import com.gdls.cardcoach.workdirective.Account;

public class AccountComposite extends Composite {
	
	private Button addButton;
	private Button removeButton;
	
	private Label selectedAccountsLabel;
	private AccountTable selectedAccountsTable;
	private Label selectableAccountsLabel;
	private AccountTable selectableAccountsTable;
	
	private ArrayList<Account> selectedAccounts;
	private ArrayList<Account> selectableAccounts;
	
	private boolean editable;

	public AccountComposite(Composite parent) {
		super(parent, SWT.NONE);
		editable = true;
		
		selectedAccounts = new ArrayList<Account>();
		selectableAccounts = new ArrayList<Account>();
		
		selectableAccountsLabel = new Label(this, SWT.LEFT);
		selectableAccountsLabel.setText("Selectable Accounts");
		selectableAccountsTable = new AccountTable();
		selectableAccountsTable.createControl(this);
		selectableAccountsTable.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (editable && selectableAccountsTable.getSelectionCount() > 0) {
					addButton.setEnabled(true);
				}
				else {
					addButton.setEnabled(false);
				}
			}
		});
		selectableAccountsTable.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent event) {
				
			}
			
			@Override
			public void focusGained(FocusEvent event) {
				selectedAccountsTable.deselectAll();
				removeButton.setEnabled(false);
			}
		});
		
		addButton = new Button(this, SWT.PUSH);
		addButton.setEnabled(false);
		addButton.setImage(IconHandler.getInstance().getImage("icon.add"));
		addButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				selectedAccounts.addAll(selectableAccountsTable.getSelectedAccounts());
				selectableAccounts.removeAll(selectableAccountsTable.getSelectedAccounts());
				selectedAccountsTable.setAccounts(selectedAccounts, true);
				selectableAccountsTable.setAccounts(selectableAccounts, true);
				addButton.setEnabled(false);
			}
		});
		removeButton = new Button(this, SWT.PUSH);
		removeButton.setEnabled(false);
		removeButton.setImage(IconHandler.getInstance().getImage("icon.remove"));
		removeButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				selectableAccounts.addAll(selectedAccountsTable.getSelectedAccounts());
				selectedAccounts.removeAll(selectedAccountsTable.getSelectedAccounts());
				selectedAccountsTable.setAccounts(selectedAccounts, true);
				selectableAccountsTable.setAccounts(selectableAccounts, true);
				removeButton.setEnabled(false);
			}
		});
		
		selectedAccountsLabel = new Label(this, SWT.LEFT);
		selectedAccountsLabel.setText("Selected Accounts");
		selectedAccountsTable = new AccountTable();
		selectedAccountsTable.createControl(this);
		selectedAccountsTable.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (editable && selectedAccountsTable.getSelectionCount() > 0) {
					removeButton.setEnabled(true);
				}
				else {
					removeButton.setEnabled(false);
				}
			}
		});
		selectedAccountsTable.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent event) {
				
			}
			
			@Override
			public void focusGained(FocusEvent event) {
				selectableAccountsTable.deselectAll();
				addButton.setEnabled(false);
			}
		});
		
		GridLayout gridLayout = new GridLayout(2, true);
		setLayout(gridLayout);
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		selectableAccountsLabel.setLayoutData(gridData);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		gridData.heightHint = selectableAccountsTable.getItemHeight() * 6 + selectableAccountsTable.getHeaderHeight();
		selectableAccountsTable.setLayoutData(gridData);
		
		gridData = new GridData(SWT.RIGHT, SWT.FILL, true, false);
		addButton.setLayoutData(gridData);
		
		gridData = new GridData(SWT.LEFT, SWT.FILL, true, false);
		removeButton.setLayoutData(gridData);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		selectedAccountsLabel.setLayoutData(gridData);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		gridData.heightHint = selectedAccountsTable.getItemHeight() * 6 + selectedAccountsTable.getHeaderHeight();
		selectedAccountsTable.setLayoutData(gridData);
		
		addListener(SWT.Hide, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				selectableAccountsTable.deselectAll();
				selectedAccountsTable.deselectAll();
				removeButton.setEnabled(false);
				addButton.setEnabled(false);
			}
		});
		
		addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				IconHandler.getInstance().disposeInstance("icon.add");
				IconHandler.getInstance().disposeInstance("icon.remove");
			}
		});
	}
	
	public void setSelectedAccounts(List<Account> accounts) {
		selectedAccounts = new ArrayList<Account>();
		for (Account account : accounts) {
			selectedAccounts.add(account);
		}
		Collections.sort(selectedAccounts);
		selectedAccountsTable.setAccounts(selectedAccounts);
	}
	
	public void setSelectableAcounts(List<Account> accounts) {
		selectableAccounts = new ArrayList<Account>();
		for (Account account : accounts) {
			selectableAccounts.add(account);
		}
		Collections.sort(selectableAccounts);
		selectableAccountsTable.setAccounts(selectableAccounts);
	}
	
	public List<Account> getSelectedAccounts() {
		return selectedAccounts;
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
}
