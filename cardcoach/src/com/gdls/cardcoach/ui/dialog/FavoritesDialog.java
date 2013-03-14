package com.gdls.cardcoach.ui.dialog;

import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.gdls.cardcoach.ui.Positioner;
import com.gdls.cardcoach.ui.composite.DialogControlsBuilder;
import com.gdls.cardcoach.ui.event.DialogSelectionListener;
import com.gdls.cardcoach.ui.event.TableEventHandler;
import com.gdls.cardcoach.ui.icons.IconHandler;
import com.gdls.cardcoach.workdirective.WorkDirectiveLibrary;

public class FavoritesDialog extends Dialog {
	
	private Shell parent;
	private Shell shell;
	
	private Composite iComposite;
	private Composite lComposite;
	private Composite dialogControls;
	
	private Label instructions;
	
	private Group wdGroup;
	private Group favGroup;
	
	private Table wdNumbers;
	private Table favNumbers;
	
	private ToolBar arBar;
	private ToolItem add;
	private ToolItem remove;
	private ToolBar udBar;
	private ToolItem up;
	private ToolItem down;
	
	private WorkDirectiveLibrary library;
	
	private TreeSet<String> nonFavorites;
	
	private boolean returnedBoolean;
	
	public FavoritesDialog(Shell parent) {
		super(parent);
		this.parent = parent;
	}
	
	private void widgetDisposed(DisposeEvent event) {
		IconHandler.getInstance().disposeInstance("icon.button.arrowleft");
		IconHandler.getInstance().disposeInstance("icon.button.arrowright");
		IconHandler.getInstance().disposeInstance("icon.button.arrowup");
		IconHandler.getInstance().disposeInstance("icon.button.arrowdown");
	}
	
	public void setLibrary(WorkDirectiveLibrary library) {
		this.library = library;
	}
	
	public boolean open() {
		returnedBoolean = false;
		nonFavorites = new TreeSet<String>();
		// Initialize a backing store for favorites for the duration of time the dialog is open
		createShell();
		createContents();
		shell.pack();
		shell.setSize(shell.computeSize(500, SWT.DEFAULT));
		Positioner.centerOnParent(parent, shell);
		shell.open();
		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) shell.getDisplay().sleep();
		}
		
		return returnedBoolean;
	}

	private void createShell() {
		shell = new Shell(parent, SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL);
		shell.setText("Work Directive Favorites");
		shell.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent event) {
				FavoritesDialog.this.widgetDisposed(event);
			}
		});
	}
	
	private void createContents() {
		SelectionAdapter buttonPush = new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				// ADD BUTTON
				if (event.widget.equals(add)) {
					if (wdNumbers.getSelectionCount() != 0) {
						favNumbers.deselectAll();
						for (TableItem item : wdNumbers.getSelection()) {
							new TableItem(favNumbers, SWT.NONE).setText(item.getText());
							nonFavorites.remove(item.getText());
						}
						updateWorkDirectiveTable();
					}
				}
				// REMOVE BUTTON
				else if (event.widget.equals(remove)) {
					if (favNumbers.getSelectionCount() != 0) {
						wdNumbers.deselectAll();
						for (TableItem item : favNumbers.getSelection()) {
							nonFavorites.add(item.getText());
						}
						int[] indices = favNumbers.getSelectionIndices();
						favNumbers.deselectAll();
						favNumbers.remove(indices);
						updateWorkDirectiveTable();
					}
				}
				// UP BUTTON
				else if (event.widget.equals(up)) {
					// Table is multi selectable. If trying to move up more than one item,
					// deselect all but the first selected item first.
					if (favNumbers.getSelectionCount() > 0) {
						int[] indices = favNumbers.getSelectionIndices();
						favNumbers.setRedraw(false);
						for (int i = 1; i < indices.length; i++) {
							favNumbers.deselect(indices[i]);
						}
						favNumbers.setRedraw(true);
						int index = favNumbers.getSelectionIndex();
						if (index > 0) {
							String number = favNumbers.getItem(index).getText();
							favNumbers.remove(index);
							new TableItem(favNumbers, SWT.NONE, index - 1).setText(number);
							favNumbers.setSelection(index - 1);
						}
					}
				}
				// DOWN BUTTON
				else if (event.widget.equals(down)) {
					if (favNumbers.getSelectionCount() > 0) {
						int[] indices = favNumbers.getSelectionIndices();
						favNumbers.setRedraw(false);
						for (int i = 1; i < indices.length; i++) {
							favNumbers.deselect(indices[i]);
						}
						favNumbers.setRedraw(true);
						int index = favNumbers.getSelectionIndex();
						if (index < favNumbers.getItemCount() - 1) {
							String number = favNumbers.getItem(index).getText();
							favNumbers.remove(index);
							new TableItem(favNumbers, SWT.NONE, index + 1).setText(number);
							favNumbers.setSelection(index + 1);
						}
					}
				}
			}
		};
		
		iComposite = new Composite(shell, SWT.NONE);
		
		instructions = new Label(iComposite, SWT.WRAP);
		instructions.setText("Work directives that are \"favorites\" will show up in the work directive " +
				"drop-down at the top of the list. Use the right and left arrows to add and remove work directives from your " +
				"favorites list. Use the up and down arrows to move a selected favorite up and down in the favorites " +
				"list. These favorites will appear in the work directive drop-down in the order you set here.");
		
		lComposite = new Composite(shell, SWT.NONE);
		
		wdGroup = new Group(lComposite, SWT.NONE);
		wdGroup.setText("Work Directives");
		
		wdNumbers = new Table(wdGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		wdNumbers.setHeaderVisible(false);
		wdNumbers.setLinesVisible(false);
		new TableEventHandler(wdNumbers);
		wdNumbers.addControlListener(new TableResizer(wdNumbers));
		TableColumn colNumber = new TableColumn(wdNumbers, SWT.NONE);
		colNumber.setResizable(false);
		colNumber.setMoveable(false);
		for (String number : library.getWorkDirectiveNumbers(WorkDirectiveLibrary.Mode.NO_FAVORITES)) {
			new TableItem(wdNumbers, SWT.NONE).setText(number);
			nonFavorites.add(number);
		}
		
		arBar = new ToolBar(wdGroup, SWT.VERTICAL | SWT.WRAP);
		
		add = new ToolItem(arBar, SWT.PUSH);
		add.setImage(IconHandler.getInstance().getImage("icon.button.arrowright"));
		add.setText("Add");
		add.addSelectionListener(buttonPush);
		
		remove = new ToolItem(arBar, SWT.PUSH);
		remove.setImage(IconHandler.getInstance().getImage("icon.button.arrowleft"));
		remove.setText("Remove");
		remove.addSelectionListener(buttonPush);
		
		favGroup = new Group(lComposite, SWT.NONE);
		favGroup.setText("Favorites");
		
		favNumbers = new Table(favGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		favNumbers.setHeaderVisible(false);
		favNumbers.setLinesVisible(false);
		new TableEventHandler(favNumbers);
		favNumbers.addControlListener(new TableResizer(favNumbers));
		colNumber = new TableColumn(favNumbers, SWT.NONE);
		colNumber.setResizable(false);
		colNumber.setMoveable(false);
		for (String number : library.getWorkDirectiveNumbers(WorkDirectiveLibrary.Mode.FAVORITES_ONLY)) {
			new TableItem(favNumbers, SWT.NONE).setText(number);
		}
		
		udBar = new ToolBar(favGroup, SWT.VERTICAL | SWT.WRAP);
		
		up = new ToolItem(udBar, SWT.PUSH);
		up.setImage(IconHandler.getInstance().getImage("icon.button.arrowup"));
		up.setText("Up");
		up.addSelectionListener(buttonPush);
		
		down = new ToolItem(udBar, SWT.PUSH);
		down.setImage(IconHandler.getInstance().getImage("icon.button.arrowdown"));
		down.setText("Down");
		down.addSelectionListener(buttonPush);
		
		dialogControls = new DialogControlsBuilder()
				.setPositiveButtonText("Ok")
				.setNegativeButtonText("Cancel")
				.addDialogSelectionListener(new DialogSelectionListener() {
					
					@Override
					public void dialogWidgetSelected(int which) {
						if (which == DialogControlsBuilder.POSITIVE_BUTTON) {
							updateLibraryFavorites();
							returnedBoolean = true;
							shell.dispose();
						}
						else {
							shell.dispose();
						}
					}
				})
				.createComposite(shell);
		
		GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		iComposite.setLayoutData(gridData);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		lComposite.setLayoutData(gridData);
		
		gridData = new GridData(SWT.RIGHT, SWT.FILL, true, false);
		dialogControls.setLayoutData(gridData);
		
		FillLayout fillLayout = new FillLayout();
		iComposite.setLayout(fillLayout);
		
		gridLayout = new GridLayout(2, true);
		lComposite.setLayout(gridLayout);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		wdGroup.setLayoutData(gridData);
		
		gridLayout = new GridLayout(2, false);
		wdGroup.setLayout(gridLayout);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		gridData.widthHint = 140;
		gridData.heightHint = wdNumbers.getItemHeight() * 20 + 2 * wdNumbers.getBorderWidth();
		wdNumbers.setLayoutData(gridData);
		
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		arBar.setLayoutData(gridData);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		favGroup.setLayoutData(gridData);
		
		gridLayout = new GridLayout(2, false);
		favGroup.setLayout(gridLayout);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		gridData.widthHint = 140;
		gridData.heightHint = favNumbers.getItemHeight() * 20 + 2 * favNumbers.getBorderWidth();
		favNumbers.setLayoutData(gridData);
		
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		udBar.setLayoutData(gridData);
	}
	
	private void updateLibraryFavorites() {
		String numbers[] = new String[favNumbers.getItemCount()];
		for (int i = 0; i < numbers.length; i++) {
			numbers[i] = favNumbers.getItem(i).getText();
		}
		library.updateFavorites(numbers);
	}
	
	private void updateWorkDirectiveTable() {
		wdNumbers.setRedraw(false);
		wdNumbers.deselectAll();
		wdNumbers.removeAll();
		for (String number : nonFavorites) {
			new TableItem(wdNumbers, SWT.NONE).setText(number);
		}
		wdNumbers.setRedraw(true);
	}
	
	private static class TableResizer implements ControlListener {

		private final Table table;
		
		public TableResizer(Table table) {
			this.table = table;
		}
		
		@Override
		public void controlMoved(ControlEvent event) {
			sizeTable();
		}

		@Override
		public void controlResized(ControlEvent event) {
			sizeTable();
		}
		
		private void sizeTable() {
			table.getColumn(0).setWidth(table.getClientArea().width);
		}
	}
}