package com.gdls.cardcoach.ui.icons;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class IconHandler {
	
	private HashMap<String, Image> images;
	private HashMap<String, String> imageFiles;
	private HashMap<String, Integer> imageCounts;

	private static IconHandler instance;
	
	private IconHandler() {
		images = new HashMap<String, Image>();
		images.put("icon.window.16", null);
		images.put("icon.window.32", null);
		images.put("icon.window.48", null);
		images.put("icon.window.64", null);
		images.put("icon.command.timesheet.new", null);
		images.put("icon.command.timesheet.open", null);
		images.put("icon.command.timesheet.save", null);
		images.put("icon.command.timesheet.saveas", null);
		images.put("icon.command.exit", null);
		images.put("icon.command.preferences", null);
		images.put("icon.command.help", null);
		images.put("icon.command.about", null);
		images.put("icon.command.timesheetentry.insert", null);
		images.put("icon.command.timesheetentry.split", null);
		images.put("icon.command.timesheetentry.delete", null);
		images.put("icon.command.timesheetentry.update", null);
		images.put("icon.command.timesheetentry.suspend", null);
		images.put("icon.tab.timesheet", null);
		images.put("icon.tab.timecard", null);
		images.put("icon.tab.workdirectives", null);
		images.put("icon.add", null);
		images.put("icon.remove", null);
		images.put("icon.delete", null);
		images.put("icon.search", null);
		images.put("icon.delete", null);
		images.put("icon.update", null);
		images.put("icon.favorite", null);
		images.put("icon.timeentry.note", null);
		images.put("icon.timeentry.suspense", null);
		images.put("icon.button.startentrynow", null);
		images.put("icon.button.startentry", null);
		images.put("icon.button.stopentrynow", null);
		images.put("icon.button.stopentry", null);
		images.put("icon.button.arrowup", null);
		images.put("icon.button.arrowdown", null);
		images.put("icon.button.arrowleft", null);
		images.put("icon.button.arrowright", null);
		
		
		imageFiles = new HashMap<String, String>();
		imageFiles.put("icon.window.16", "app-icon-16.png");
		imageFiles.put("icon.window.32", "app-icon-32.png");
		imageFiles.put("icon.window.48", "app-icon-48.png");
		imageFiles.put("icon.window.64", "app-icon-64.png");
		imageFiles.put("icon.command.timesheet.new", "sheet-new.gif");
		imageFiles.put("icon.command.timesheet.open", "sheet-open.png");
		imageFiles.put("icon.command.timesheet.save", "sheet-save.gif");
		imageFiles.put("icon.command.timesheet.saveas", "sheet-save-as.gif");
		imageFiles.put("icon.command.exit", "app-exit.png");
		imageFiles.put("icon.command.preferences", "preferences.gif");
		imageFiles.put("icon.command.help", "help.gif");
		imageFiles.put("icon.command.about", "information.gif");
		imageFiles.put("icon.command.timesheetentry.insert", "insert.gif");
		imageFiles.put("icon.command.timesheetentry.split", "split.gif");
		imageFiles.put("icon.command.timesheetentry.delete", "delete.gif");
		imageFiles.put("icon.command.timesheetentry.update", "update.gif");
		imageFiles.put("icon.command.timesheetentry.suspend", "suspense.gif");
		imageFiles.put("icon.tab.timesheet", "tab-sheet.gif");
		imageFiles.put("icon.tab.timecard", "tab-card.gif");
		imageFiles.put("icon.tab.workdirectives", "tab-workdirective.gif");
		imageFiles.put("icon.add", "add.gif");
		imageFiles.put("icon.remove", "minus.gif");
		imageFiles.put("icon.delete", "delete.gif");
		imageFiles.put("icon.search", "search.gif");
		imageFiles.put("icon.delete", "delete.gif");
		imageFiles.put("icon.update", "update.gif");
		imageFiles.put("icon.favorite", "bookmark.gif");
		imageFiles.put("icon.timeentry.note", "note.gif");
		imageFiles.put("icon.timeentry.suspense", "suspense.gif");
		imageFiles.put("icon.button.startentrynow", "entry-start-now.png");
		imageFiles.put("icon.button.startentry", "entry-start-clock.png");
		imageFiles.put("icon.button.stopentrynow", "entry-stop-now.png");
		imageFiles.put("icon.button.stopentry", "entry-stop-clock.png");
		imageFiles.put("icon.button.arrowup", "arrow_up_32.png");
		imageFiles.put("icon.button.arrowdown", "arrow_down_32.png");
		imageFiles.put("icon.button.arrowleft", "arrow_left_32.png");
		imageFiles.put("icon.button.arrowright", "arrow_right_32.png");
//		imageFiles.put("icon.button.arrowup", "arrow-up-16.gif");
//		imageFiles.put("icon.button.arrowdown", "arrow-down-16.gif");
//		imageFiles.put("icon.button.arrowleft", "arrow-left-16.gif");
//		imageFiles.put("icon.button.arrowright", "arrow-right-16.gif");
		
		imageCounts = new HashMap<String, Integer>();
		imageCounts.put("icon.window.16", 0);
		imageCounts.put("icon.window.32", 0);
		imageCounts.put("icon.window.48", 0);
		imageCounts.put("icon.window.64", 0);
		imageCounts.put("icon.command.timesheet.new", 0);
		imageCounts.put("icon.command.timesheet.open", 0);
		imageCounts.put("icon.command.timesheet.save", 0);
		imageCounts.put("icon.command.timesheet.saveas", 0);
		imageCounts.put("icon.command.exit", 0);
		imageCounts.put("icon.command.preferences", 0);
		imageCounts.put("icon.command.help", 0);
		imageCounts.put("icon.command.about", 0);
		imageCounts.put("icon.command.timesheetentry.insert", 0);
		imageCounts.put("icon.command.timesheetentry.split", 0);
		imageCounts.put("icon.command.timesheetentry.delete", 0);
		imageCounts.put("icon.command.timesheetentry.update", 0);
		imageCounts.put("icon.command.timesheetentry.suspend", 0);
		imageCounts.put("icon.tab.timesheet", 0);
		imageCounts.put("icon.tab.timecard", 0);
		imageCounts.put("icon.tab.workdirectives", 0);
		imageCounts.put("icon.add", 0);
		imageCounts.put("icon.remove", 0);
		imageCounts.put("icon.delete", 0);
		imageCounts.put("icon.search", 0);
		imageCounts.put("icon.delete", 0);
		imageCounts.put("icon.update", 0);
		imageCounts.put("icon.favorite", 0);
		imageCounts.put("icon.timeentry.note", 0);
		imageCounts.put("icon.timeentry.suspense", 0);
		imageCounts.put("icon.button.startentrynow", 0);
		imageCounts.put("icon.button.startentry", 0);
		imageCounts.put("icon.button.stopentrynow", 0);
		imageCounts.put("icon.button.stopentry", 0);
		imageCounts.put("icon.button.arrowup", 0);
		imageCounts.put("icon.button.arrowdown", 0);
		imageCounts.put("icon.button.arrowleft", 0);
		imageCounts.put("icon.button.arrowright", 0);
	}
	
	public static IconHandler getInstance() {
		if (instance == null) {
			instance = new IconHandler();
		}
		return instance;
	}
	
	public Image getImage(String name) {
//		System.out.println("Attempting to retrieve image: " + name);
		if (images.containsKey(name)) {
			if (images.get(name) == null) {
//				System.out.println("Image " + name + " not loaded...loading...");
				images.put(name, loadImage(imageFiles.get(name)));
			}
			int count = imageCounts.get(name) + 1;
			imageCounts.put(name, count);
			return images.get(name);
		}
		return null;
	}
	
	private Image loadImage(String name) {
		InputStream stream = IconHandler.class.getResourceAsStream(name);
		if (stream == null) {
			return null;
		}
		Image image = null;
		try {
			image = new Image(Display.getCurrent(), stream);
		} catch (SWTException e) {
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return image;
	}
	
	public void disposeInstance(String name) {
		disposeInstance(name, 1);
	}
	
	public void disposeInstance(String name, int number) {
		if (images.containsKey(name)) {
			int count = Math.max(imageCounts.get(name) - number, 0);
			imageCounts.put(name, count);
			if (imageCounts.get(name) == 0 && images.get(name) != null) {
				images.get(name).dispose();
				images.put(name, null);
			}
		}
	}
}
