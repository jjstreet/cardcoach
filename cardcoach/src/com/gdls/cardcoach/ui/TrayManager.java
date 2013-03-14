package com.gdls.cardcoach.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import com.gdls.cardcoach.CardCoachContext;
import com.gdls.cardcoach.ui.icons.IconHandler;

public class TrayManager {
	
	private CardCoachContext context;

	private Tray tray;
	private TrayItem trayItem;
	private Menu menu;
	private Shell shell;
	
	private ToolTip toolTip;
	
	public TrayManager(CardCoachContext context) {
		this.context = context;
	}
	
	public void createControl(Shell parent) {
		shell = parent;
		tray = Display.getCurrent().getSystemTray();
		if (tray != null) {
			trayItem = new TrayItem(tray, SWT.NONE);
			trayItem.setImage(IconHandler.getInstance().getImage("icon.window.16"));
			trayItem.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetDefaultSelected(SelectionEvent event) {
					if (shell.getMinimized() == false) {
						shell.setMinimized(true);
						shell.setVisible(false);
					}
					else {
						shell.setMinimized(false);
						shell.setVisible(true);
					}
				}
				
				@Override
				public void widgetSelected(SelectionEvent event) {
					if (!shell.getMinimized()) {
						shell.setVisible(true);
						shell.setFocus();
					}
				}
			});
			trayItem.addMenuDetectListener(new MenuDetectListener() {
				
				@Override
				public void menuDetected(MenuDetectEvent e) {
					menu.setVisible(true);
				}
			});
			tray.addDisposeListener(new DisposeListener() {
				
				@Override
				public void widgetDisposed(DisposeEvent event) {
					IconHandler.getInstance().disposeInstance("icon.window.16");
					if (toolTip != null && !toolTip.isDisposed()) {
						toolTip.dispose();
					}
					trayItem.dispose();
				}
			});
		}
	}
	
	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	
	public boolean isTrayAvailable() {
		return tray != null;
	}
	
	public void setText(String message) {
		trayItem.setToolTipText(message);
	}
	
	public void showMessage(String message) {
		if (toolTip == null) {
			toolTip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION);
		}
		toolTip.setText(context.getApplicationTitle());
		toolTip.setMessage(message);
		toolTip.setVisible(true);
	}
}
