package com.gdls.cardcoach.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorManager {
	
	public static final Color getAltRowBackground() {
		final Color color = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
		return getDifferentShade(color, -.05f);
	}
	
	public static final Color getAltRowSelected() {
		final Color color = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_SELECTION);
		return getDifferentShade(color, -.2f);
	}
	
	public static final Color getAltInactiveRowSelected() {
		final Color color = Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND);
		return getDifferentShade(color, .2f);
	}
	
	public static final Color getDifferentShade(Color color, float brightDiff) {
		float[] colorHSB = color.getRGB().getHSB();
		return getDifferentShade(color, brightDiff, colorHSB[2]);
	}
	
	public static final Color getDifferentShade(Color color, float brightDiff, float startingBrightness) {
		float[] colorHSB = color.getRGB().getHSB();
		float newBrightness = Math.max(0f, Math.min(1f, startingBrightness + brightDiff));
		return new Color(color.getDevice(), new RGB(colorHSB[0], colorHSB[1], newBrightness));
	}
}