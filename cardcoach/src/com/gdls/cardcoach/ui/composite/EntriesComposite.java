package com.gdls.cardcoach.ui.composite;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.gdls.cardcoach.timesheet.InsertTimeSheetEntryException;
import com.gdls.cardcoach.timesheet.SplitTimeSheetEntryException;
import com.gdls.cardcoach.timesheet.TimeSheet;
import com.gdls.cardcoach.timesheet.TimeSheetChangeListener;
import com.gdls.cardcoach.timesheet.TimeSheetEntry;
import com.gdls.cardcoach.timesheet.UpdateTimeSheetEntryException;
import com.gdls.cardcoach.ui.ColorManager;
import com.gdls.cardcoach.ui.FontManager;
import com.gdls.cardcoach.ui.MessageFactory;
import com.gdls.cardcoach.ui.dialog.DialogInput;
import com.gdls.cardcoach.ui.dialog.DialogResult;
import com.gdls.cardcoach.ui.dialog.TimeSheetEntryDialog;
import com.gdls.cardcoach.ui.dialog.ViewTimeSheetEntryDialog;
import com.gdls.cardcoach.ui.event.TableEventHandler;
import com.gdls.cardcoach.ui.icons.IconHandler;
import com.gdls.cardcoach.workdirective.Account;
import com.gdls.cardcoach.workdirective.WorkDirective;
import com.gdls.cardcoach.workdirective.WorkDirectiveLibrary;

public class EntriesComposite extends Composite {
	
	private TimeSheet timeSheet;
	private WorkDirectiveLibrary library;
	
	private Color altRowForeground;
	private Font fontBold;
	private Font fontItalic;
	
	private Image suspIcon;
	private Image noteIcon;
	
	private Group entriesGroup;
	private Table entries;
	
	private TableColumn colWorkDirective;
	private TableColumn colAccounts;
	private TableColumn colCrossCharge;
	private TableColumn colTaskCode;
	private TableColumn colStartTime;
	private TableColumn colEndTime;
	private TableColumn colFlags;
	
	private EntrySelectionListener entrySelectionListener;
	private EntryMenuListener entryMenuListener;
	
	private TimeSheetEntryDialog timeSheetEntryDialog;
	
	public EntriesComposite(Composite parent) {
		super(parent, SWT.NONE);
		
		fontItalic = FontManager.getInstance().getFont("default.default.italic");
		fontBold = FontManager.getInstance().getFont("default.default.bold");
		
		suspIcon = IconHandler.getInstance().getImage("icon.timeentry.suspense");
		noteIcon = IconHandler.getInstance().getImage("icon.timeentry.note");
		
		entriesGroup = new Group(this, SWT.NONE);
		entriesGroup.setText("Current Time Entries");
		
		entrySelectionListener = new EntrySelectionListener();
		entryMenuListener = new EntryMenuListener();
		entries = new Table(entriesGroup, SWT.FULL_SELECTION | SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		entries.setHeaderVisible(true);
		entries.setLinesVisible(false);
		entries.addSelectionListener(entrySelectionListener);
		entries.addMenuDetectListener(entryMenuListener);
		entries.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				sizeTable();
			}
			
			@Override
			public void controlMoved(ControlEvent e) {
				sizeTable();
			}
		});
		TableEventHandler teh = new TableEventHandler(entries);
		teh.setMeasureListener(new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				final GC gc = event.gc;
				final Font font = gc.getFont();
				final TableItem item = (TableItem) event.item;
				final TimeSheetEntry entry = (TimeSheetEntry) item.getData();
				Point size = new Point(0,0);
				switch (event.index) {
				case 0:
					Point boldSize;
					Point italicSize;
					boldSize = event.gc.textExtent(entry.getWorkDirective().getNumber());
					gc.setFont(fontItalic);
					italicSize = event.gc.textExtent(entry.getWorkDirective().getDescription());
					size.x = Math.max(boldSize.x, italicSize.x);
					size.y = boldSize.y + italicSize.y;
					gc.setFont(font);
					break;
				default:
					size.x = gc.textExtent(item.getText(event.index)).x;
				}
				event.width = size.x + 2 * 3;
				event.height = Math.max(event.height, size.y + 3);
			}
		});
		teh.setPaintListener(new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				final GC gc = event.gc;
				final Color fg = gc.getForeground();
				final TableItem item = (TableItem) event.item;
				if (entries.isFocusControl() && (event.detail & SWT.SELECTED) != 0) {
					altRowForeground = ColorManager.getDifferentShade(fg, -.3f);
				}
				else {
					altRowForeground = ColorManager.getDifferentShade(fg, .3f);
				}
				final TimeSheetEntry entry = (TimeSheetEntry) item.getData();
				final WorkDirective wd = entry.getWorkDirective();
				final Account acc = wd.getSelectedAccount();
				Point point;
				switch (event.index) {
				case 0:
					StringBuffer sb = new StringBuffer();
					gc.setFont(fontBold);
					point = gc.textExtent(wd.getNumber());
					gc.drawText(wd.getNumber(), event.x + 3, event.y, true);
					gc.setFont(fontItalic);
					gc.setForeground(altRowForeground);
					for (char tChar : wd.getDescription().toCharArray()) {
						if (gc.textExtent(sb.toString().concat(String.valueOf(tChar)).concat("...")).x < event.width) {
							sb.append(tChar);
						}
						else {
							sb.append("...");
						}
					}
					gc.drawText(sb.toString(), event.x + 3, event.y + point.y, true);
					gc.setForeground(fg);
					break;
				case 1:
					point = gc.textExtent(acc.getNumber());
					gc.drawText(acc.getNumber(), event.x + 3, event.y, true);
					gc.drawText(acc.getNumberNextWeek(), event.x + 3, event.y + point.y, true);
					break;
				case 6:
					int x = event.x + 3;
					if (!entry.getNote().isEmpty()) {
						gc.drawImage(noteIcon, x, event.y);
						x += noteIcon.getBounds().width + 3;
					}
					if (entry.isSuspended()) {
						gc.drawImage(suspIcon, x, event.y);
					}
					break;
				default:
					gc.drawText(item.getText(event.index), event.x + 3, event.y, true);
				}
				altRowForeground.dispose();
			}
		});
		
		colWorkDirective = new TableColumn(entries, SWT.LEFT);
		colWorkDirective.setText("Work Directive");
		colWorkDirective.setMoveable(false);
		colWorkDirective.setResizable(false);
		
		colAccounts = new TableColumn(entries, SWT.LEFT);
		colAccounts.setText("Accounts");
		colAccounts.setMoveable(false);
		colAccounts.setResizable(false);
		
		colCrossCharge = new TableColumn(entries, SWT.LEFT);
		colCrossCharge.setText("Cross Charge Dept.");
		colCrossCharge.setMoveable(false);
		colCrossCharge.setResizable(false);
		
		colTaskCode = new TableColumn(entries, SWT.LEFT);
		colTaskCode.setText("Task Code");
		colTaskCode.setMoveable(false);
		colTaskCode.setResizable(false);
		
		colStartTime = new TableColumn(entries, SWT.LEFT);
		colStartTime.setText("Start Time");
		colStartTime.setMoveable(false);
		colStartTime.setResizable(false);
		
		colEndTime = new TableColumn(entries, SWT.LEFT);
		colEndTime.setText("End Time");
		colEndTime.setMoveable(false);
		colEndTime.setResizable(false);
		
		colFlags = new TableColumn(entries, SWT.LEFT);
		colFlags.setText("");
		colFlags.setMoveable(false);
		colFlags.setResizable(false);
		
		FillLayout fillLayout = new FillLayout();
		setLayout(fillLayout);
		
		GridLayout gridLayout = new GridLayout();
		entriesGroup.setLayout(gridLayout);
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.heightHint = entries.getItemHeight() * 11 + 3 + entries.getHeaderHeight();
		entries.setLayoutData(gridData);
		
		timeSheetEntryDialog = new TimeSheetEntryDialog(getShell());
	}
	
	private void sizeTable() {
		int width = entries.getClientArea().width;
		colWorkDirective.setWidth(width * 18 / 100);
		colAccounts.setWidth(width * 17 / 100);
		colCrossCharge.setWidth(width * 16 / 100);
		colTaskCode.setWidth(width * 14 / 100);
		colStartTime.setWidth(width * 15 / 100);
		colEndTime.setWidth(width * 15 / 100);
		colFlags.setWidth(width - colWorkDirective.getWidth()
				- colAccounts.getWidth()
				- colCrossCharge.getWidth()
				- colTaskCode.getWidth()
				- colStartTime.getWidth()
				- colEndTime.getWidth());
	}
	
	private void update(List<TimeSheetEntry> timeEntries) {
		WorkDirective wd;
		TimeSheetEntry timeSheetEntry;
		entries.setRedraw(false);
		entries.clearAll();
		entries.removeAll();
		Iterator<TimeSheetEntry> entry = timeEntries.iterator();
		while (entry.hasNext()) {
			timeSheetEntry = entry.next();
			wd = timeSheetEntry.getWorkDirective();
			TableItem item = new TableItem(entries, SWT.NONE);
			item.setText(new String[] {
				String.format("%s%n%s", wd.getNumber(), wd.getDescription()),
				String.format("%s%n%s", wd.getSelectedAccount().getNumber(), wd.getSelectedAccount().getNumberNextWeek()),
				timeSheetEntry.getCrossCharge(),
				timeSheetEntry.getTaskCode(),
				timeSheetEntry.getFormattedStartTime(),
				timeSheetEntry.getFormattedEndTime(),
				"",
				""
			});
			item.setData(timeSheetEntry);
		}
		if (timeEntries.size() > 0) {
			entries.showItem(entries.getItem(entries.getItemCount() - 1));
		}
		entries.setRedraw(true);
	}
	
	public void setTimeSheet(TimeSheet timeSheet) {
		this.timeSheet = timeSheet;
		if (this.timeSheet != null) {
			entries.setEnabled(true);
			update(this.timeSheet.getTimeSheetEntries());
			this.timeSheet.addTimeSheetChangeListener(new TimeSheetChangeListener() {
				
				@Override
				public void timeSheetChanged(TimeSheet timeSheet) {
					update(timeSheet.getTimeSheetEntries());
				}
			});
		}
		else {
			this.timeSheet = null;
			entries.setRedraw(false);
			entries.deselectAll();
			entries.removeAll();
			entries.setRedraw(true);
			entries.setEnabled(false);
		}
	}
	
	public void setLibrary(WorkDirectiveLibrary library) {
		this.library = library;
	}
	
	private class EntrySelectionListener extends SelectionAdapter {
		int selectedIndex;
		
		public EntrySelectionListener() {
			selectedIndex = -1;
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent event) {
			selectedIndex = entries.getSelectionIndex();
			if (timeSheet != null && selectedIndex != -1) {
				new ViewTimeSheetEntryDialog(getShell()).open(timeSheet.getTimeSheetEntry(selectedIndex));
			}
		}
	}
	
	private class EntryMenuListener implements MenuDetectListener {
		
		private Menu popupMenu;
		private int selectedIndex;
		
		public EntryMenuListener() {
			
		}
		
		@Override
		public void menuDetected(MenuDetectEvent event) {
			selectedIndex = entries.getSelectionIndex();
			if (selectedIndex != -1) {
				TableItem item = entries.getItem(entries.getSelectionIndex());
				Point clickPoint = new Point(event.x, event.y);
				Rectangle selectionRect = item.getBounds(0)
						.union(item.getBounds(1))
						.union(item.getBounds(2))
						.union(item.getBounds(3))
						.union(item.getBounds(4))
						.union(item.getBounds(5))
						.union(item.getBounds(6))
						.union(item.getBounds(7));
				Point translatedOrigin = entries.toDisplay(selectionRect.x, selectionRect.y);
				selectionRect.x = translatedOrigin.x;
				selectionRect.y = translatedOrigin.y;
				if (selectionRect.contains(clickPoint)) {
					popMenu(clickPoint);
				}
			}
		}
		
		public void popMenu(Point location) {
			if (timeSheet != null) {
				if (popupMenu == null || popupMenu.isDisposed()) {
					createMenu();
				}
				popupMenu.setLocation(location);
				popupMenu.setVisible(true);
			}
		}
		
		public void createMenu() {
			popupMenu = new Menu(getShell(), SWT.POP_UP);
			final MenuItem viewEntry = new MenuItem(popupMenu, SWT.PUSH);
			viewEntry.setText("View details...");
			viewEntry.setImage(IconHandler.getInstance().getImage("icon.search"));
			viewEntry.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent event) {
					new ViewTimeSheetEntryDialog(getShell()).open(timeSheet.getTimeSheetEntry(selectedIndex));
				}
			});
			new MenuItem(popupMenu, SWT.SEPARATOR);
			final MenuItem updateEntry = new MenuItem(popupMenu, SWT.PUSH);
			updateEntry.setText("Update...");
			updateEntry.setImage(IconHandler.getInstance().getImage("icon.command.timesheetentry.update"));
			updateEntry.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent event) {
					// Min time is previous entry's end time or beginning of day
					// Max time is next entry's start time or end of day
					DialogInput input = new DialogInput(TimeSheetEntryDialog.MODE_UPDATE);
					input.setData("employee", timeSheet.getEmployee());
					input.setData("library", library);
					input.setData("sheetDate", timeSheet.getSheetDate());
					input.setData("minTime", timeSheet.getPreviousEntryEndTime(selectedIndex, true));
					input.setData("maxTime", timeSheet.getNextEntryStartTime(selectedIndex, true));
					input.setData("entry", timeSheet.getTimeSheetEntry(selectedIndex));
					DialogResult result = new DialogResult();
					while (result.getReturnCode() == DialogResult.RETURN_UNDEFINED) {
						timeSheetEntryDialog.open(input, result);
						if (result.getReturnCode() == DialogResult.RETURN_OK) {
							try {
								TimeSheetEntry entry = (TimeSheetEntry) result.getData("entry");
								input.setData("entry", entry);
								timeSheet.updateTimeSheetEntry(selectedIndex, entry);
							}
							catch (UpdateTimeSheetEntryException e) {
								MessageFactory.showError(getShell(), e.getMessage());
								result.setReturnCode(DialogResult.RETURN_UNDEFINED);
							}
						}
					}
					input = null;
					result = null;
				}
			});
			final MenuItem suspendEntry = new MenuItem(popupMenu, SWT.CHECK);
			suspendEntry.setText("Suspend");
			suspendEntry.setImage(IconHandler.getInstance().getImage("icon.command.timesheetentry.suspend"));
			suspendEntry.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent event) {
					timeSheet.setEntrySuspended(selectedIndex, suspendEntry.getSelection());
				}
			});
			new MenuItem(popupMenu, SWT.SEPARATOR);
			final MenuItem insertEntry = new MenuItem(popupMenu, SWT.PUSH);
			insertEntry.setText("Insert new...");
			insertEntry.setImage(IconHandler.getInstance().getImage("icon.command.timesheetentry.insert"));
			insertEntry.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent event) {
					// Min time is previous entry's end time or beginning of day
					// Max time is entry's start time at the selected index
					DialogInput input = new DialogInput(TimeSheetEntryDialog.MODE_INSERT);
					input.setData("employee", timeSheet.getEmployee());
					input.setData("library", library);
					input.setData("sheetDate", timeSheet.getSheetDate());
					input.setData("minTime", timeSheet.getPreviousEntryEndTime(selectedIndex, true));
					input.setData("maxTime", timeSheet.getTimeSheetEntry(selectedIndex).getStartTime());
					DialogResult result = new DialogResult();
					while (result.getReturnCode() == DialogResult.RETURN_UNDEFINED) {
						timeSheetEntryDialog.open(input, result);
						if (result.getReturnCode() == DialogResult.RETURN_OK) {
							try {
								TimeSheetEntry entry = (TimeSheetEntry) result.getData("entry");
								input.setData("entry", entry);
								timeSheet.insertTimeSheetEntry(selectedIndex, entry);
							}
							catch (InsertTimeSheetEntryException e) {
								MessageFactory.showError(getShell(), e.getMessage());
								result.setReturnCode(DialogResult.RETURN_UNDEFINED);
							}
						}
					}
					input = null;
					result = null;
				}
			});
			final MenuItem splitEntry = new MenuItem(popupMenu, SWT.PUSH);
			splitEntry.setText("Split...");
			splitEntry.setImage(IconHandler.getInstance().getImage("icon.command.timesheetentry.split"));
			splitEntry.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent event) {
					// Min time is the selected entry's start time
					// Min time is the selected entry's end time
					DialogInput input = new DialogInput(TimeSheetEntryDialog.MODE_SPLIT);
					input.setData("employee", timeSheet.getEmployee());
					input.setData("library", library);
					input.setData("sheetDate", timeSheet.getSheetDate());
					input.setData("minTime", timeSheet.getTimeSheetEntry(selectedIndex).getStartTime());
					input.setData("maxTime", timeSheet.getTimeSheetEntry(selectedIndex).getEndTime());
					DialogResult result = new DialogResult();
					while (result.getReturnCode() == DialogResult.RETURN_UNDEFINED) {
						timeSheetEntryDialog.open(input, result);
						if (result.getReturnCode() == DialogResult.RETURN_OK) {
							try {
								TimeSheetEntry entry = (TimeSheetEntry) result.getData("entry");
								input.setData("entry", entry);
								input.setData("splitBefore", ((Boolean) result.getData("splitBefore")).booleanValue());
								timeSheet.splitTimeSheetEntry(selectedIndex, entry, ((Boolean) result.getData("splitBefore")).booleanValue());
							}
							catch (SplitTimeSheetEntryException e) {
								MessageFactory.showError(getShell(), e.getMessage());
								result.setReturnCode(DialogResult.RETURN_UNDEFINED);
							}
						}
					}
					input = null;
					result = null;
				}
			});
			new MenuItem(popupMenu, SWT.SEPARATOR);
			final MenuItem deleteEntry = new MenuItem(popupMenu, SWT.PUSH);
			deleteEntry.setText("Delete");
			deleteEntry.setImage(IconHandler.getInstance().getImage("icon.command.timesheetentry.delete"));
			deleteEntry.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent event) {
					if (MessageFactory.showConfirmation(getShell(), "Are you sure you wish to delete this entry?") == SWT.YES) {
						timeSheet.deleteTimeSheetEntry(selectedIndex);
					}
				}
			});
			addDisposeListener(new DisposeListener() {
				
				@Override
				public void widgetDisposed(DisposeEvent e) {
					IconHandler.getInstance().disposeInstance("icon.search");
					IconHandler.getInstance().disposeInstance("icon.command.timesheetentry.update");
					IconHandler.getInstance().disposeInstance("icon.command.timesheetentry.suspend");
					IconHandler.getInstance().disposeInstance("icon.command.timesheetentry.insert");
					IconHandler.getInstance().disposeInstance("icon.command.timesheetentry.split");
					IconHandler.getInstance().disposeInstance("icon.command.timesheetentry.delete");
					popupMenu.dispose();
				}
			});
			popupMenu.addListener(SWT.Show, new Listener() {
				
				@Override
				public void handleEvent(Event event) {
					if (library != null && timeSheet.getPreviousEntryEndTime(selectedIndex, true).numSecondsFrom(timeSheet.getEntryStartTime(selectedIndex)) > 1) {
						insertEntry.setEnabled(true);
					}
					else {
						insertEntry.setEnabled(false);
					}
					if (library != null && timeSheet.getTimeSheetEntry(selectedIndex).isFinished()) {
						splitEntry.setEnabled(true);
					}
					else {
						splitEntry.setEnabled(false);
					}
					suspendEntry.setSelection(timeSheet.getTimeSheetEntry(selectedIndex).isSuspended());
					if (timeSheet.getTimeSheetEntry(selectedIndex).getWorkDirective().isSuspendable()) {
						suspendEntry.setEnabled(true);
					}
					else {
						suspendEntry.setEnabled(false);
					}
					if (library != null) {
						updateEntry.setEnabled(true);
					}
					else {
						updateEntry.setEnabled(false);
					}
				}
			});
		}
	}
}
