package com.gdls.cardcoach.ui.dialog;

import java.util.HashMap;

public class DialogInput {
	
	public static final int MODE_UNDEFINED = -1;

	private int mode;
	private HashMap<String, Object> data;
	
	public DialogInput(int mode) {
		this.mode = mode;
	}
	
	public DialogInput() {
		this.mode = MODE_UNDEFINED;
	}
	
	public int getMode() {
		return mode;
	}
	
	public void setMode(int mode) {
		this.mode = mode;
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
