package com.gdls.cardcoach.ui.dialog;

import java.util.HashMap;

public class DialogResult {
	
	public static final int RETURN_UNDEFINED = -1;
	public static final int RETURN_OK = 0;
	public static final int RETURN_CANCEL = 1;
	
	private int returnCode;
	private HashMap<String, Object> data;
	
	public DialogResult(int returnCode) {
		this.returnCode = returnCode;
	}
	
	public DialogResult() {
		returnCode = RETURN_UNDEFINED;
	}
	
	public int getReturnCode() {
		return returnCode;
	}
	
	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}
	
	public Object getData() {
		if (data != null) {
			return data.get(null);
		}
		return null;
	}
	
	public Object getData(String key) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException("key can not be null");
		}
		if (data != null) {
			return data.get(key);
		}
		return null;
	}
	
	public void setData(Object data) {
		if (this.data == null) {
			this.data = new HashMap<String, Object>();
		}
		this.data.put(null, data);
	}
	
	public void setData(String key, Object data) throws IllegalArgumentException {
		if (key == null) {
			throw new IllegalArgumentException("key can not be null");
		}
		if (this.data == null) {
			this.data = new HashMap<String, Object>();
		}
		if (data == null) {
			if (this.data.containsKey(key)) {
				this.data.remove(key);
			}
		}
		else {
			this.data.put(key, data);
		}
	}
}
