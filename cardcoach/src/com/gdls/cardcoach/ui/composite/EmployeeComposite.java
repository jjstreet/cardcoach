package com.gdls.cardcoach.ui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.gdls.cardcoach.timesheet.TimeSheet;
import com.gdls.cardcoach.ui.FontManager;
import com.gdls.cardcoach.ui.WidgetBuilder;

public class EmployeeComposite extends Composite {
	
	private Font default16;
	
	private final Text fullName;
	private final Text type;

	EmployeeComposite(Composite parent) {
		super(parent, SWT.NONE);
		
		default16 = FontManager.getInstance().getFont("default.16");
		
		fullName = WidgetBuilder.newText(this, SWT.LEFT | SWT.READ_ONLY);
		fullName.setFont(default16);
		type = WidgetBuilder.newText(this, SWT.RIGHT | SWT.READ_ONLY);
		type.setFont(default16);
		
		FormLayout layout = new FormLayout();
		layout.spacing = 5;
		this.setLayout(layout);
		
		FormData layoutData = new FormData();
		layoutData.left = new FormAttachment(0);
		layoutData.right = new FormAttachment(65);
		layoutData.bottom = new FormAttachment(100);
		fullName.setLayoutData(layoutData);
		
		layoutData = new FormData();
		layoutData.left = new FormAttachment(fullName);
		layoutData.right = new FormAttachment(100);
		layoutData.bottom = new FormAttachment(100);
		type.setLayoutData(layoutData);
		
		addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				default16 = null;
				type.dispose();
				fullName.dispose();
				FontManager.getInstance().disposeInstance("default.16");
			}
		});
	}
	
	public void setTimeSheet(TimeSheet timeSheet) {
		if (timeSheet != null) {
			fullName.setEnabled(true);
			type.setEnabled(true);
			fullName.setText(timeSheet.getEmployee().getFullName());
			type.setText(timeSheet.getEmployee().getType().toString());
		}
		else {
			fullName.setText("");
			type.setText("");
			fullName.setEnabled(false);
			type.setEnabled(false);
		}
	}
}