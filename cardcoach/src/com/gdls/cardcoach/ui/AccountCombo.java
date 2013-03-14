package com.gdls.cardcoach.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.gdls.cardcoach.workdirective.Account;

public class AccountCombo {

	private ArrayList<Account> accounts;
	
	private Combo combo;
	
	public AccountCombo() {
		accounts = new ArrayList<Account>();
	}
	
	public Combo createControl(Composite parent) {
		combo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		return combo;
	}
	
	public void setAccounts(List<Account> accounts) {
		combo.removeAll();
		Account selectedAccount = null;
		this.accounts.clear();
		for (Account account : accounts) {
			this.accounts.add(account);
			combo.add(getAccountAsItem(account));
			if (account.isSelected()) {
				selectedAccount = account;
			}
		}
		if (combo.getItemCount() == 1) {
			combo.setText(getAccountAsItem(0));
		}
		else if (selectedAccount != null) {
			combo.setText(getAccountAsItem(selectedAccount));
		}
	}
	
	public void addSelectionListener(SelectionListener listener) {
		combo.addSelectionListener(listener);
	}
	
	public void removeSelectionListener(SelectionListener listener) {
		combo.removeSelectionListener(listener);
	}
	
	public Account getSelectedAccount() {
		int idx = combo.getSelectionIndex();
		if (idx > -1) {
			return accounts.get(idx);
		}
		return null;
	}
	
	public void clearAccounts() {
		accounts = new ArrayList<Account>();
		combo.removeAll();
	}
	
	private String getAccountAsItem(int idx) {
		return accounts.get(idx).getNumber() + "/" + accounts.get(idx).getNumberNextWeek();
	}
	
	private String getAccountAsItem(Account account) {
		return account.getNumber() + "/" + account.getNumberNextWeek();
	}
	
	public int getAccountCount() {
		return accounts.size();
	}
	
	public Control getControl() {
		return combo;
	}
	
	public void setAccount(Account account) {
		int idx = -1;
		for (Account acc : accounts) {
			if (acc.equals(account)) {
				idx = accounts.indexOf(account);
			}
		}
		if (idx != -1) {
			combo.setText(getAccountAsItem(idx));
		}
	}
	
//	public void setAccount(Account account, boolean force) {
//		if (force) {
//			accounts = new ArrayList<Account>();
//			accounts.add(account);
//		}
//		setAccount(account);
//	}
}
