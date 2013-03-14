package com.gdls.cardcoach.ui.composite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.gdls.cardcoach.ui.event.DialogSelectionListener;

public class DialogControlsBuilder {
	
	public static final int POSITIVE_BUTTON = 1<<2;
	public static final int NEGATIVE_BUTTON = 1<<3;
	public static final int NEUTRAL_BUTTON = 1<<4;
	
	private int enabledButtons;
	
	private String positiveButtonText;
	private String neutralButtonText;
	private String negativeButtonText;
	
	private Vector<DialogSelectionListener> listeners;
	
	public DialogControlsBuilder() {
		listeners = new Vector<DialogSelectionListener>();
	}
	
	public DialogControlsBuilder setPositiveButtonText(String text) {
		positiveButtonText = text;
		enabledButtons |= POSITIVE_BUTTON;
		return this;
	}
	
	public DialogControlsBuilder setNeutralButtonText(String text) {
		neutralButtonText = text;
		enabledButtons |= NEUTRAL_BUTTON;
		return this;
	}
	
	public DialogControlsBuilder setNegativeButtonText(String text) {
		negativeButtonText = text;
		enabledButtons |= NEGATIVE_BUTTON;
		return this;
	}
	
	
	public Composite createComposite(Composite parent) {
		final ArrayList<Button> buttons = new ArrayList<Button>();
		final Composite composite = new Composite(parent, SWT.NONE);
		for (int id : new int[] {POSITIVE_BUTTON, NEUTRAL_BUTTON, NEGATIVE_BUTTON}) {
			final int buttonId = id;
			if ((enabledButtons & buttonId) != 0) {
				final Button button = new Button(composite, SWT.PUSH);
				if (buttonId == POSITIVE_BUTTON) {
					button.setText(positiveButtonText);
				}
				else if (buttonId == NEUTRAL_BUTTON) {
					button.setText(neutralButtonText);
				}
				else if (buttonId == NEGATIVE_BUTTON) {
					button.setText(negativeButtonText);
				}
				button.addSelectionListener(new SelectionAdapter() {
					
					@Override
					public void widgetSelected(SelectionEvent event) {
						for (DialogSelectionListener listener : listeners) {
							listener.dialogWidgetSelected(buttonId);
						}
					}
				});
				buttons.add(button);
			}
		}
		GridLayout layout = new GridLayout(buttons.size(), false);
		layout.marginHeight = layout.marginWidth = 0;
		composite.setLayout(layout);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, buttons.size(), 1);
		Iterator<Button> buttonIterator = buttons.iterator();
		while (buttonIterator.hasNext()) {
			gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
			gridData.widthHint = 70;
			buttonIterator.next().setLayoutData(gridData);
		}
		return composite;
	}
	
	public DialogControlsBuilder addDialogSelectionListener(DialogSelectionListener listener) {
		listeners.add(listener);
		return this;
	}
	
	public DialogControlsBuilder removeDialogSelectionListener(DialogSelectionListener listener) {
		listeners.remove(listener);
		return this;
	}
}
