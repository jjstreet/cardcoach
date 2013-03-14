package com.gdls.cardcoach.ui.composite;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.gdls.cardcoach.preference.PreferenceException;
import com.gdls.cardcoach.preference.PreferenceManager;
import com.gdls.cardcoach.ui.MessageFactory;
import com.gdls.cardcoach.ui.WorkDirectiveTable;
import com.gdls.cardcoach.ui.dialog.DialogInput;
import com.gdls.cardcoach.ui.dialog.DialogResult;
import com.gdls.cardcoach.ui.dialog.FavoritesDialog;
import com.gdls.cardcoach.ui.dialog.WorkDirectiveDialog;
import com.gdls.cardcoach.ui.icons.IconHandler;
import com.gdls.cardcoach.workdirective.WorkDirective;
import com.gdls.cardcoach.workdirective.WorkDirectiveLibrary;
import com.gdls.cardcoach.workdirective.WorkDirectiveLibraryChangeListener;
import com.gdls.cardcoach.workdirective.XMLWorkDirectiveLibraryUtil;
import com.gdls.cardcoach.xml.SaveXMLException;

public class WorkDirectiveLibraryComposite extends Composite {
	
	private WorkDirectiveTable libraryTable;
	
	private final ToolItem viewItem;
	private final ToolItem addItem;
	private final ToolItem updateItem;
	private final ToolItem deleteItem;
	private final ToolItem favoritesItem;
	
	private final WorkDirectiveDialog workDirectiveDialog;
	private final FavoritesDialog favoritesDialog;
	
	private final WorkDirectiveLibrarySaver saver;
	
	private WorkDirectiveLibrary library;
	
	public WorkDirectiveLibraryComposite(Composite parent) {
		super(parent, SWT.NONE);
		
		saver = new WorkDirectiveLibrarySaver(getShell());
		
		final ToolBar bar = new ToolBar(this, SWT.HORIZONTAL | SWT.WRAP);
		
		viewItem = new ToolItem(bar, SWT.PUSH);
		viewItem.setImage(IconHandler.getInstance().getImage("icon.search"));
		viewItem.setText("View");
		viewItem.setEnabled(false);
		viewItem.setToolTipText("View a work directive");
		viewItem.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				DialogInput input = new DialogInput(WorkDirectiveDialog.MODE_VIEW);
				WorkDirective wd = library.getWorkDirective(libraryTable.getSelectedWorkDirectives().get(0));
				input.setData("workDirective", wd);
				DialogResult result = new DialogResult();
				workDirectiveDialog.open(input, result);
			}
		});
		
		addItem = new ToolItem(bar, SWT.PUSH);
		addItem.setImage(IconHandler.getInstance().getImage("icon.add"));
		addItem.setText("Add");
		addItem.setToolTipText("Add a work directive");
		addItem.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				DialogInput input = new DialogInput(WorkDirectiveDialog.MODE_ADD);
				DialogResult result = new DialogResult();
				WorkDirective wd;
				while (result.getReturnCode() == DialogResult.RETURN_UNDEFINED) {
					workDirectiveDialog.open(input, result);
					if (result.getReturnCode() == DialogResult.RETURN_OK) {
						wd = (WorkDirective) result.getData("workDirective");
						if (!library.containsWorkDirective(wd.getNumber())) {
							library.add(wd);
							reset();
						}
						else {
							input.setData("workDirective", wd);
							MessageFactory.showError(getShell(), "The library already contains a work directive with this number.");
							result.setReturnCode(DialogResult.RETURN_UNDEFINED);
						}
					}
				}
				input = null;
				result = null;
			}
		});
		
		updateItem = new ToolItem(bar, SWT.PUSH);
		updateItem.setImage(IconHandler.getInstance().getImage("icon.update"));
		updateItem.setText("Update");
		updateItem.setToolTipText("Update selected work directives");
		updateItem.setEnabled(false);
		updateItem.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				WorkDirective wd;
				DialogInput input;
				DialogResult result = new DialogResult();
				if (libraryTable.getSelectionCount() == 1) {
					wd = library.getWorkDirective(libraryTable.getSelectedWorkDirectives().get(0));
					input = new DialogInput(WorkDirectiveDialog.MODE_EDIT_SINGLE);
					input.setData("workDirective", wd);
					workDirectiveDialog.open(input, result);
					if (result.getReturnCode() == DialogResult.RETURN_OK) {
						wd = (WorkDirective) result.getData("workDirective");
						library.update(libraryTable.getSelectedWorkDirectives().get(0), wd);
						reset();
					}
				}
				else {
					input = new DialogInput(WorkDirectiveDialog.MODE_EDIT_MULTIPLE);
					workDirectiveDialog.open(input, result);
					if (result.getReturnCode() == DialogResult.RETURN_OK) {
						wd = (WorkDirective) result.getData("workDirective");
						String[] numbers = new String[libraryTable.getSelectedWorkDirectives().size()];
						numbers = libraryTable.getSelectedWorkDirectives().toArray(numbers);
						library.update(numbers, wd);
						reset();
					}
				}
				input = null;
				result = null;
			}
		});
		
		deleteItem = new ToolItem(bar, SWT.PUSH);
		deleteItem.setImage(IconHandler.getInstance().getImage("icon.delete"));
		deleteItem.setText("Delete");
		deleteItem.setToolTipText("Delete selected work directives");
		deleteItem.setEnabled(false);
		deleteItem.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				int count = libraryTable.getSelectionCount();
				String message = String.format("Are you sure you wish to delete %s work %s?", count, count > 1 ? "directives" : "directive");
				if (MessageFactory.showConfirmation(getShell(), message) == SWT.YES) {
					String[] numbers = new String[libraryTable.getSelectedWorkDirectives().size()];
					numbers = libraryTable.getSelectedWorkDirectives().toArray(numbers);
					library.remove(numbers);
					reset();
				}
			}
		});
		
		favoritesItem = new ToolItem(bar, SWT.PUSH);
		favoritesItem.setImage(IconHandler.getInstance().getImage("icon.favorite"));
		favoritesItem.setText("Favorites");
		favoritesItem.setToolTipText("Select favorite work directives");
		favoritesItem.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (favoritesDialog.open()) {
					PreferenceManager.getInstance().putStringArray(PreferenceManager.WORKDIRECTIVES_FAVORITES, library.getWorkDirectiveNumbers(WorkDirectiveLibrary.Mode.FAVORITES_ONLY));
					try {
						PreferenceManager.getInstance().savePreferences();
					}
					catch (PreferenceException e) {
						MessageFactory.showError(getShell(), e.getMessage());
					}
				}
			}
		});
		
		libraryTable = new WorkDirectiveTable();
		libraryTable.createControl(this);
		libraryTable.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (libraryTable.getSelectionCount() != 0) {
					viewItem.setEnabled(true);
					if (!library.isReadOnly()) {
						updateItem.setEnabled(true);
						deleteItem.setEnabled(true);
					}
					else {
						updateItem.setEnabled(false);
						deleteItem.setEnabled(false);
					}
					if (libraryTable.getSelectionCount() > 1) {
						viewItem.setEnabled(false);
					}
				}
				else {
					updateItem.setEnabled(false);
					deleteItem.setEnabled(false);
					viewItem.setEnabled(false);
				}
			}
		});
		
		GridLayout layout = new GridLayout();
		setLayout(layout);
		
		GridData layoutData = new GridData(SWT.CENTER, SWT.FILL, false, false);
		bar.setLayoutData(layoutData);
		
		layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		libraryTable.getControl().setLayoutData(layoutData);
		
		addListener(SWT.Hide, new Listener() {
			@Override
			public void handleEvent(Event event) {
				viewItem.setEnabled(false);
				updateItem.setEnabled(false);
				deleteItem.setEnabled(false);
				libraryTable.deselectAll();
			}
		});
		
		workDirectiveDialog = new WorkDirectiveDialog(getShell());
		favoritesDialog = new FavoritesDialog(getShell());
		
		addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent event) {
				IconHandler.getInstance().getImage("icon.favorite");
				IconHandler.getInstance().getImage("icon.delete");
				IconHandler.getInstance().getImage("icon.update");
				IconHandler.getInstance().getImage("icon.add");
				IconHandler.getInstance().getImage("icon.search");
			}
		});
	}
	
	public void setLibrary(WorkDirectiveLibrary library) {
		this.library = library;
		favoritesDialog.setLibrary(library);
		saver.setLibrary(library);
		ArrayList<WorkDirective> workDirectives = new ArrayList<WorkDirective>();
		if (this.library != null) {
			for (String number : library.getWorkDirectiveNumbers(WorkDirectiveLibrary.Mode.ALL)) {
				workDirectives.add(library.getWorkDirective(number));
			}
			libraryTable.setWorkDirectives(workDirectives);
			library.addWorkDirectiveLibraryChangedListener(new WorkDirectiveLibraryChangeListener() {
				
				@Override
				public void libraryChanged(WorkDirectiveLibrary library) {
					ArrayList<WorkDirective> workDirectives = new ArrayList<WorkDirective>();
					for (String number : library.getWorkDirectiveNumbers(WorkDirectiveLibrary.Mode.ALL)) {
						workDirectives.add(library.getWorkDirective(number));
					}
					libraryTable.setWorkDirectives(workDirectives);
					getDisplay().asyncExec(saver);
				}
			});
			if (!this.library.isReadOnly()) {
				addItem.setEnabled(true);
			}
			else {
				addItem.setEnabled(false);
			}
			favoritesItem.setEnabled(true);
		}
		else {
			libraryTable.setWorkDirectives(workDirectives);
			addItem.setEnabled(false);
			favoritesItem.setEnabled(false);
		}
	}
	
	private void reset() {
		libraryTable.deselectAll();
		viewItem.setEnabled(false);
		updateItem.setEnabled(false);
		deleteItem.setEnabled(false);
	}
	
	private static class WorkDirectiveLibrarySaver implements Runnable {

		private final Shell shell;
		private WorkDirectiveLibrary library;
		
		public WorkDirectiveLibrarySaver(Shell shell) {
			this.shell = shell;
		}
		
		public void setLibrary(WorkDirectiveLibrary library) {
			this.library = library;
		}
		
		@Override
		public void run() {
			if (library.isDirty()) {
				try {
					XMLWorkDirectiveLibraryUtil.saveToXML(library, PreferenceManager.getInstance().getString(PreferenceManager.WORKDIRECTIVES_LIST_LOCATION, null));
				}
				catch (SaveXMLException e) {
					MessageFactory.showError(shell, e.getMessage());
				}
			}
		}
	}
}
