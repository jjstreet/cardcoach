package com.gdls.cardcoach.ui.event;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.gdls.cardcoach.ui.ColorManager;

public class TableEventHandler {
	
	private Color altRowBackground;
	private Color altRowSelected;
	private Color altInactiveRowSelected;
	
	private Table table;
	
	private Listener measureListener;
	private Listener eraseListener;
	private Listener paintListener;

	public TableEventHandler(Table table) {
		measureListener = null;
		eraseListener = null;
		paintListener = null;
		
		Listener eventListener = new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				if (event.type == SWT.EraseItem) {
					onEraseEvent(event);
				}
				else if (event.type == SWT.MeasureItem) {
					onMeasureEvent(event);
				}
				else if (event.type == SWT.PaintItem) {
					onPaintEvent(event);
				}
			}
		};
		
		this.table = table;
		this.table.addListener(SWT.EraseItem, eventListener);
		this.table.addListener(SWT.MeasureItem, eventListener);
		this.table.addListener(SWT.PaintItem, eventListener);
		altRowBackground = ColorManager.getAltRowBackground();
		altRowSelected = ColorManager.getAltRowSelected();
		altInactiveRowSelected = ColorManager.getAltInactiveRowSelected();
		this.table.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				altRowBackground.dispose();
				altRowSelected.dispose();
				altInactiveRowSelected.dispose();
			}
		});
	}
	
	private void onMeasureEvent(Event event) {
		if (measureListener != null) {
			measureListener.handleEvent(event);
		}
	}
	
	private void onEraseEvent(Event event) {
		if (eraseListener != null) {
			eraseListener.handleEvent(event);
		} else {
			final int width = table.getClientArea().width;
			final GC gc = event.gc;
			final Color bg = gc.getBackground();
			final Color fg = gc.getForeground();
			event.detail &= ~SWT.HOT;
			event.detail &= ~SWT.BACKGROUND;
			final TableItem item = (TableItem) event.item;
			if ((event.detail & SWT.SELECTED) == 0) {
				// Paint unselected background
				if (table.indexOf(item) % 2 == 0) {
					gc.setForeground(altRowBackground);
					gc.setBackground(altRowBackground);
				}
				else {
					gc.setForeground(table.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
					gc.setBackground(table.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
				}
				// Clipping region of table will force paint to only show in the painted item
				// but still paint the full width of the line to get the proper
				// gradient.
				gc.fillGradientRectangle(0, event.y, width, event.height, true);
			}
			else {
				// Paint selection. Paint must also be aware that the table
				// may not have focus.
				if (table.isFocusControl()) {
					// Has focus, paint the selection gradient
					gc.setForeground(altRowSelected);
					gc.setBackground(table.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
				}
				else {
					// No focus, paint a full bar of whatever the background should be as the unfocused
					// selection.
					gc.setForeground(altInactiveRowSelected);
					gc.setBackground(table.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
				}
				gc.fillGradientRectangle(0, event.y, width, event.height, false);
				event.detail &= ~SWT.SELECTED;
			}
			gc.setForeground(fg);
			gc.setBackground(bg);
			if (paintListener != null) {
				event.detail &= ~SWT.FOREGROUND;
			}
		}
	}
	
	private void onPaintEvent(Event event) {
		if (paintListener != null) {
			paintListener.handleEvent(event);
		}
	}
	
	public void setMeasureListener(Listener listener) {
		measureListener = listener;
	}
	
	public void setEraseListener(Listener listener) {
		eraseListener = listener;
	}
	
	public void setPaintListener(Listener listener) {
		paintListener = listener;
	}
}
