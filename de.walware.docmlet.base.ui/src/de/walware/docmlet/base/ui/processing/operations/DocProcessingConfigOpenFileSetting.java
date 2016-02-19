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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.docmlet.base.internal.ui.processing.Messages;


public class DocProcessingConfigOpenFileSetting {
	
	
	private static final String OPEN_DISABLED= ""; //$NON-NLS-1$
	private static final String OPEN_SINGLE_STEP= "step_only"; //$NON-NLS-1$
	private static final String OPEN_ALWAYS= "always"; //$NON-NLS-1$
	
	private static final ImList<String> OPEN_OPTIONS= ImCollections.newList(
			OPEN_DISABLED, OPEN_SINGLE_STEP, OPEN_ALWAYS );
	
	
	private final String enabledAttrName;
	
	private final WritableValue enabledValue;
	
	private ComboViewer enabledViewer;
	
	
	public DocProcessingConfigOpenFileSetting(final String attrName, final Realm realm) {
		this.enabledAttrName= attrName;
		
		this.enabledValue= new WritableValue(realm, "", String.class); //$NON-NLS-1$
	}
	
	
	public ComboViewer createControls(final Composite parent, final String labelText) {
		final Label label= new Label(parent, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		
		final ComboViewer viewer= new ComboViewer(parent);
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				switch ((String) element) {
				case OPEN_SINGLE_STEP:
					return Messages.StepTab_OpenFile_SingleStep_label;
				case OPEN_ALWAYS:
					return Messages.StepTab_OpenFile_Always_label;
				default:
					return Messages.StepTab_OpenFile_Disabled_label;
				}
			}
		});
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(OPEN_OPTIONS);
		viewer.setSelection(new StructuredSelection(OPEN_DISABLED));
		
		this.enabledViewer= viewer;
		
		return viewer;
	}
	
	public void addBindings(final DataBindingContext dbc) {
		dbc.bindValue(ViewerProperties.singleSelection().observe(this.enabledViewer),
				this.enabledValue );
	}
	
	public void load(final ILaunchConfiguration configuration) throws CoreException {
		final String type= OPEN_DISABLED;
		try {
			configuration.getAttribute(this.enabledAttrName, type);
		}
		finally {
			this.enabledValue.setValue(type);
		}
	}
	
	public void save(final ILaunchConfigurationWorkingCopy configuration) {
		final String type= (String) this.enabledValue.getValue();
		if (type != null && !type.isEmpty()) {
			configuration.setAttribute(this.enabledAttrName, type);
		}
		else {
			configuration.removeAttribute(this.enabledAttrName);
		}
	}
	
}
