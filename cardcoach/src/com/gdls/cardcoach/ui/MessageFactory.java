package com.gdls.cardcoach.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public final class MessageFactory {

		/**
		 * Show a informational message box with text.
		 * @param parent the parent shell of the message box.
		 * @param message the message to be displayed.
		 * @return integer corresponding to an SWT constant.
		 */
		public static int showMessage(Shell parent, String message) {
			MessageBox dialog = new MessageBox(parent, SWT.ICON_INFORMATION | SWT.OK);
			dialog.setText(parent.getText());
			dialog.setMessage(message);
			return dialog.open();
		}
		
		/**
		 * Show a informational message box with text.
		 * @param parent the parent shell of the message box.
		 * @param style the style the message box will take on.
		 * @param message the message to be displayed.
		 * @return integer corresponding to an SWT constant.
		 */
		public static int showMessage(Shell parent, int style, String message) {
			MessageBox dialog = new MessageBox(parent, style);
			dialog.setText(parent.getText());
			dialog.setMessage(message);
			return dialog.open();
		}
		
		/**
		 * Show a error message box with text.
		 * @param parent the parent shell of the message box.
		 * @param message the message to be displayed.
		 * @return integer corresponding to an SWT constant.
		 */
		public static int showError(Shell parent, String message) {
			MessageBox dialog = new MessageBox(parent, SWT.ICON_ERROR | SWT.OK);
			dialog.setText(parent.getText());
			dialog.setMessage(message);
			return dialog.open();
		}
		
		/**
		 * Show a error message box with text.
		 * @param parent the parent shell of the message box.
		 * @param style the style the message box will take on.
		 * @param message the message to be displayed.
		 * @return integer corresponding to an SWT constant.
		 */
		public static int showError(Shell parent, int style, String message) {
			MessageBox dialog = new MessageBox(parent, style);
			dialog.setText(parent.getText());
			dialog.setMessage(message);
			return dialog.open();
		}
		
		/**
		 * Show a confirmation message with yes and no buttons.
		 * @param parent the parent shell of the message box.
		 * @param message the message to be displayed.
		 * @return integer corresponding to an SWT constant.
		 */
		public static int showConfirmation(Shell parent, String message) {
			MessageBox dialog = new MessageBox(parent, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			dialog.setText(parent.getText());
			dialog.setMessage(message);
			return dialog.open();
		}
		
		/**
		 * Show a confirmation message with yes and no buttons.
		 * @param parent the parent shell of the message box.
		 * @param style the style the message box will take on.
		 * @param message the message to be displayed.
		 * @return integer corresponding to an SWT constant.
		 */
		public static int showConfirmation(Shell parent, int style, String message) {
			MessageBox dialog = new MessageBox(parent, style);
			dialog.setText(parent.getText());
			dialog.setMessage(message);
			return dialog.open();
		}
}
