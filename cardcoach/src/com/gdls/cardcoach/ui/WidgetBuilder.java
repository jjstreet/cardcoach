package com.gdls.cardcoach.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class WidgetBuilder {

	private WidgetBuilder() {
		
	}
	
	public static final Text newText(Composite parent, int style) {
		final Text text = new Text(parent, style);
		text.addListener(SWT.KeyDown, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				if (event.stateMask == SWT.CTRL && event.keyCode == 97) {
					text.selectAll();
					event.doit = false;
				}
			}
		});
		return text;
	}
}
