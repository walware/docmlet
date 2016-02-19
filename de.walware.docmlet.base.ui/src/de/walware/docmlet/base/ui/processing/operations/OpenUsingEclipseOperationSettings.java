/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.base.ui.processing.operations;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

import de.walware.ecommons.ui.util.LayoutUtil;

import de.walware.docmlet.base.internal.ui.processing.Messages;
import de.walware.docmlet.base.ui.processing.DocProcessingOperationSettings;


public class OpenUsingEclipseOperationSettings extends DocProcessingOperationSettings {
	
	
	public OpenUsingEclipseOperationSettings() {
	}
	
	
	@Override
	public String getId() {
		return OpenUsingEclipseOperation.ID;
	}
	
	@Override
	public String getLabel() {
		return Messages.ProcessingOperation_OpenUsingEclipse_label;
	}
	
	
	@Override
	protected Composite createControl(final Composite parent) {
		final Composite composite= super.createControl(parent);
		
		composite.setLayout(LayoutUtil.createCompositeGrid(1));
		
		LayoutUtil.addSmallFiller(composite, true);
		
		final Link link= new Link(composite, SWT.NONE);
		link.setText("Global preferences: "
				+ "<a href=\"org.eclipse.ui.preferencePages.FileEditors\">Editor File Associations</a>.");
		composite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final PreferenceDialog dialog = org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn(null, e.text, null, null);
				if (dialog != null) {
					dialog.open();
				}
			}
		});
		
		return composite;
	}
	
	
}
