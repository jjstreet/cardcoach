package com.gdls.cardcoach.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.gdls.cardcoach.ui.event.TableEventHandler;
import com.gdls.cardcoach.workdirective.Account;

public class LastEntriesTable {

	private Table table;
	private TableColumn colWorkDirective;
	private TableColumn colAccount;
	private TableColumn colCrossCharge;
	private TableColumn colTaskCode;
	
	public LastEntriesTable() {
		
	}
	
	public void createControl(final Composite parent) {
		table = new Table(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL);
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
		table.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				table.deselectAll();
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// Do nothing
			}
		});
		
		colWorkDirective = new TableColumn(table, SWT.LEFT);
		colWorkDirective.setText("Work Directive");
		colWorkDirective.setMoveable(false);
		colWorkDirective.setResizable(false);
		
		colAccount = new TableColumn(table, SWT.LEFT);
		colAccount.setText("Accounts");
		colAccount.setMoveable(false);
		colAccount.setResizable(false);
		
		colCrossCharge = new TableColumn(table, SWT.LEFT);
		colCrossCharge.setText("Cross Charge Dept.");
		colCrossCharge.setMoveable(false);
		colCrossCharge.setResizable(false);
		
		colTaskCode = new TableColumn(table, SWT.LEFT);
		colTaskCode.setText("Task Code");
		colTaskCode.setMoveable(false);
		colTaskCode.setResizable(false);
	}
	
	public Control getControl() {
		return table;
	}
	
	private void sizeTable() {
		int width = table.getClientArea().width;
		colWorkDirective.setWidth(width * 30 / 100);
		colAccount.setWidth(width * 30 / 100);
		colCrossCharge.setWidth(width * 20 / 100);
		colTaskCode.setWidth(width - colWorkDirective.getWidth() - colAccount.getWidth() - colCrossCharge.getWidth());
	}
	
	public void setLayoutData(Object layoutData) {
		table.setLayoutData(layoutData);
	}
	
	public void deselectAll() {
		table.deselectAll();
	}
	
	public void dispose() {
		table.dispose();
	}
	
	public void clearEntries() {
		table.deselectAll();
		table.removeAll();
	}
	
	public int getItemHeight() {
		return table.getItemHeight();
	}
	
	public int getHeaderHeight() {
		return table.getHeaderHeight();
	}
	
	public void addLastEntry(String workDirective, Account account, String crossCharge, String taskCode) {
		table.setRedraw(false);
		int idx = 0;
		TableItem tableItem = new TableItem(table, SWT.NONE, idx);
		tableItem.setText(new String[] {
				workDirective,
				getAccountAsItem(account),
				crossCharge,
				taskCode});
		tableItem.setData(account);
		TableItem item;
		for (int i = 1; i < table.getItemCount(); i++) {
			item = table.getItem(i);
			if (item.getText(0) == workDirective &&
					item.getText(1).equalsIgnoreCase(getAccountAsItem(account)) &&
					item.getText(2).equalsIgnoreCase(crossCharge) &&
					item.getText(3).equalsIgnoreCase(taskCode)) {
				idx = i;
				break;
			}
		}
		if (idx != 0) {
			table.remove(idx);
		}
		if (table.getItemCount() > 5) {
			table.remove(4);
		}
		table.setRedraw(true);
	}
	
	private String getAccountAsItem(Account account) {
		return account.getNumber() + "/" + account.getNumberNextWeek();
	}
	
	public String getSelectedWorkDirective() {
		final int idx = table.getSelectionIndex();
		if (idx != -1) {
			return table.getItem(idx).getText(0);
		}
		return "";
	}
	
	public Account getSelectedAccount() {
		final int idx = table.getSelectionIndex();
		if (idx != -1) {
			return (Account) table.getItem(idx).getData();
		}
		return null;
	}
	
	public String getSelectedCrossCharge() {
		final int idx = table.getSelectionIndex();
		if (idx != -1) {
			return table.getItem(idx).getText(2);
		}
		return "";	
	}
	
	public String getSelectedTaskCode() {
		final int idx = table.getSelectionIndex();
		if (idx != -1) {
			return table.getItem(idx).getText(3);
		}
		return "";
	}
	
	public void addSelectionListener(SelectionListener listener) {
		table.addSelectionListener(listener);
	}
	
	public void removeSelectionListener(SelectionListener listener) {
		table.removeSelectionListener(listener);
	}
}
