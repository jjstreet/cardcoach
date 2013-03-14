package com.gdls.cardcoach.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.gdls.cardcoach.ui.event.TableEventHandler;
import com.gdls.cardcoach.workdirective.WorkDirective;

public class WorkDirectiveTable {
	
	private Table table;
	private TableColumn colWorkDirective;
	private TableColumn colDescription;
	private TableColumn colSuspenseNumber;
	
	public WorkDirectiveTable() {
		
	}
	
	public void createControl(final Composite parent) {
		final Font medFont = FontManager.getInstance().getFont("default.12");
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
		table.setFont(medFont);
		new TableEventHandler(table);
		colWorkDirective = new TableColumn(table, SWT.LEFT);
		colWorkDirective.setText("Work Directive");
		colWorkDirective.setMoveable(false);
		colWorkDirective.setResizable(false);
		
		colDescription = new TableColumn(table, SWT.LEFT);
		colDescription.setText("Description");
		colDescription.setMoveable(false);
		colDescription.setResizable(false);
		
		colSuspenseNumber = new TableColumn(table, SWT.LEFT);
		colSuspenseNumber.setText("Suspense Number");
		colSuspenseNumber.setMoveable(false);
		colSuspenseNumber.setResizable(false);
		
		table.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent event) {
				FontManager.getInstance().disposeInstance("default.12");
			}
		});
	}
	
	private void sizeTable() {
		int width = table.getClientArea().width;
		colWorkDirective.setWidth(width * 25 / 100);
		colDescription.setWidth(width * 50 / 100);
		colSuspenseNumber.setWidth(width - colWorkDirective.getWidth() - colDescription.getWidth());
	}
	
	public void addSelectionListener(SelectionListener listener) {
		table.addSelectionListener(listener);
	}
	
	public void removeSelectionListener(SelectionListener listener) {
		table.removeSelectionListener(listener);
	}
	
	public void setWorkDirectives(List<WorkDirective> workDirectives) {
		table.setRedraw(false);
		table.clearAll();
		table.removeAll();
		for (WorkDirective wd : workDirectives) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(new String[] {
					wd.getNumber(),
					wd.getDescription(),
					wd.getSuspenseNumber()
			});
		}
		table.setRedraw(true);
	}
	
	public List<String> getSelectedWorkDirectives() {
		ArrayList<String> numbers = new ArrayList<String>();
		for (TableItem item : table.getSelection()) {
			numbers.add(item.getText(0));
		}
		return numbers;
	}
	
	public int getSelectionCount() {
		return table.getSelectionCount();
	}
	
	public void deselectAll() {
		table.deselectAll();
	}
	
	public Control getControl() {
		return table;
	}
}
