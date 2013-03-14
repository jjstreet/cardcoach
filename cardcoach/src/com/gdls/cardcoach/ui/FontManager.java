package com.gdls.cardcoach.ui;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class FontManager {

	private HashMap<String, Font> fonts;
	private HashMap<String, Integer> fontCounts;
	private Display display;
	
	private static FontManager instance;
	
	private FontManager() {
		display = Display.getCurrent();
		fonts = new HashMap<String, Font>();
		fonts.put("default.default.bold", null);
		fonts.put("default.default.italic", null);
		fonts.put("default.12", null);
		fonts.put("default.14", null);
		fonts.put("default.16", null);
		fonts.put("default.18", null);
		fonts.put("default.20", null);
		fonts.put("default.24", null);
		fonts.put("default.14.bold", null);
		fonts.put("default.16.bold", null);
		fonts.put("default.18.bold", null);
		fonts.put("default.20.bold", null);
		fonts.put("default.24.bold", null);
		fonts.put("default.14.italic", null);
		fonts.put("default.16.italic", null);
		fonts.put("default.18.italic", null);
		fonts.put("default.20.italic", null);
		fonts.put("default.24.italic", null);
		
		fontCounts = new HashMap<String, Integer>();
		fontCounts.put("default.default.bold", 0);
		fontCounts.put("default.default.italic", 0);
		fontCounts.put("default.12", 0);
		fontCounts.put("default.14", 0);
		fontCounts.put("default.16", 0);
		fontCounts.put("default.18", 0);
		fontCounts.put("default.20", 0);
		fontCounts.put("default.24", 0);
		fontCounts.put("default.14.bold", 0);
		fontCounts.put("default.16.bold", 0);
		fontCounts.put("default.18.bold", 0);
		fontCounts.put("default.20.bold", 0);
		fontCounts.put("default.24.bold", 0);
		fontCounts.put("default.14.italic", 0);
		fontCounts.put("default.16.italic", 0);
		fontCounts.put("default.18.italic", 0);
		fontCounts.put("default.20.italic", 0);
		fontCounts.put("default.24.italic", 0);
	}
	
	public static FontManager getInstance() {
		if (instance == null) {
			instance = new FontManager();
		}
		return instance;
	}
	
	public Font getFont(String name) {
		if (fonts.containsKey(name)) {
			if (fonts.get(name) == null) {
				final String parts[] = name.split("\\.");
				String fontName = display.getSystemFont().getFontData()[0].getName();
				if (!parts[0].equals("default")) {
					fontName = parts[0];
				}
				int fontHeight = display.getSystemFont().getFontData()[0].getHeight();
				if (!parts[1].equals("default")) {
					fontHeight = Integer.valueOf(parts[1]).intValue();
				}
				int fontStyle = SWT.NORMAL;
				if (parts.length > 2) {
					if (parts[2].equals("bold")) {
						fontStyle = SWT.BOLD;
					}
					else if (parts[2].equals("italic")) {
						fontStyle = SWT.ITALIC;
					}
				}
				fonts.put(name, loadFont(fontName, fontHeight, fontStyle));
			}
			int count = fontCounts.get(name) + 1;
			fontCounts.put(name, count);
			return fonts.get(name);
		}
		return display.getSystemFont();
	}
	
	private Font loadFont(String name, int height, int style) {
		final Font font = new Font(display, name, height, style);
		return font;
	}
	
	public void disposeInstance(String name) {
		disposeInstance(name, 1);
	}
	
	public void disposeInstance(String name, int number) {
		if (fonts.containsKey(name)) {
			int count = Math.max(fontCounts.get(name) - number, 0);
			fontCounts.put(name, count);
			if (fontCounts.get(name) == 0) {
				fonts.get(name).dispose();
				fonts.put(name, null);
			}
		}
	}
}
