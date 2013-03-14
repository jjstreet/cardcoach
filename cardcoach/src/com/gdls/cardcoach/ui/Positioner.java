package com.gdls.cardcoach.ui;

import org.eclipse.swt.widgets.*;

public class Positioner {
	
	private Positioner() {
		
	}

	public static final void centerOnParent(Shell parent, Shell shell) {
		int xLoc = parent.getLocation().x + parent.getSize().x / 2 - shell.getSize().x / 2;
		int yLoc = parent.getLocation().y + parent.getSize().y / 2 - shell.getSize().y / 2;
		shell.setLocation(xLoc, yLoc);
	}
}
