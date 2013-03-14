package com.gdls.cardcoach.ui.dialog;

public class DialogInputDataException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DialogInputDataException() {
		super("expected data object missing");
	}
	
	public DialogInputDataException(String key) {
		super("expected data object missing for key: " + key);
	}
}
