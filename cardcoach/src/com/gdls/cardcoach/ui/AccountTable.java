package com.gdls.cardcoach.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.gdls.cardcoach.ui.event.TableEventHandler;
import com.gdls.cardcoach.workdirective.Account;

public class AccountTable {
	private Table table;
	private TableColumn colNumber;
	private TableColumn colNumberNextWeek;
	
	public AccountTable() {
	}
	
	public void createControl(final Composite parent) {
		table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLinesVisible(false);
		table.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent event) {
				sizeTable();
			}
			
			@Override
			public void controlMoved(ControlEvent event) {
				sizeTable();
			}
		});
		new TableEventHandler(table);
		colNumber = new TableColumn(table, SWT.LEFT);
		colNumber.setText("Number");
		colNumber.setMoveable(false);
		colNumber.setResizable(false);
		colNumberNextWeek = new TableColumn(table, SWT.LEFT);
		colNumberNextWeek.setText("Number Next Week");
		colNumberNextWeek.setMoveable(false);
		colNumberNextWeek.setResizable(false);
	}
	
	private void sizeTable() {
		int width = table.getClientArea().width;
		colNumber.setWidth(width / 2);
		colNumberNextWeek.setWidth(width - colNumber.getWidth());
	}
	
	public int getSelectionCount() {
		return table.getSelectionCount();
	}
	
	public List<Account> getSelectedAccounts() {
		ArrayList<Account> accounts = new ArrayList<Account>();
		for (TableItem item : table.getSelection()) {
			accounts.add((Account) item.getData());
		}
		return accounts;
	}
	
	public void setAccounts(List<Account> accounts) {
		setAccounts(accounts, false);
	}
	
	public void setAccounts(List<Account> accounts, boolean sort) {
		table.setRedraw(false);
		table.clearAll();
		table.removeAll();
		if (sort) {
			Collections.sort(accounts);
		}
		for (Account account : accounts) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setData(account);
			item.setText(new String[] {
				account.getNumber(),
				account.getNumberNextWeek()
			});
		}
		table.setRedraw(true);
	}

	public void deselectAll() {
		table.deselectAll();
	}

	public void setLayoutData(Object layoutData) {
		table.setLayoutData(layoutData);
	}
	
	public void dispose() {
		table.dispose();
	}
	
	public void addSelectionListener(SelectionListener listener) {
		table.addSelectionListener(listener);
	}
	
	public void removeSelectionListener(SelectionListener listener) {
		table.removeSelectionListener(listener);
	}
	
	public void setEnabled(boolean enabled) {
		table.setEnabled(enabled);
	}
	
	public int getItemHeight() {
		return table.getItemHeight();
	}
	
	public int getHeaderHeight() {
		return table.getHeaderHeight();
	}
	
	public void addFocusListener(FocusListener listener) {
		table.addFocusListener(listener);
	}
	
	public void removeFocusListener(FocusListener listener) {
		table.removeFocusListener(listener);
	}
	
}
