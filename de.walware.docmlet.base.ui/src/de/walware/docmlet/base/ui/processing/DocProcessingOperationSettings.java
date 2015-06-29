/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.base.ui.processing;

import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.walware.ecommons.databinding.DataBindingSubContext;


public abstract class DocProcessingOperationSettings {
	
	
	private DocProcessingConfigStepTab tab;
	
	private boolean isSelected;
	
	private DataBindingSubContext bindings;
	
	
	public DocProcessingOperationSettings() {
	}
	
	
	public abstract String getId();
	
	public abstract String getLabel();
	
	public String getInfo() {
		return getLabel();
	}
	
	protected String limitInfo(String info) {
		final int idx= info.indexOf('\n');
		if (idx >= 0) {
			info= info.substring(0, idx);
		}
		return (info.length() < 40) ? info : (info.substring(0, 35) + "\u2026"); //$NON-NLS-1$
	}
	
	
	protected void init(final DocProcessingConfigStepTab tab) {
		this.tab= tab;
	}
	
	protected void dispose() {
	}
	
	
	public DocProcessingConfigStepTab getTab() {
		return this.tab;
	}
	
	protected Realm getRealm() {
		return this.tab.getRealm();
	}
	
	protected ILaunchConfigurationDialog getLaunchConfigurationDialog() {
		return this.tab.getLaunchConfigurationDialog();
	}
	
	
	Composite createDetailControl(final Composite parent) {
		final Composite composite= createControl(parent);
		
		if (composite != null) {
			initBindings();
		}
		
		return composite;
	}
	
	void initBindings() {
		final DataBindingContext dbc= this.tab.getDataBindingContext();
		this.bindings= new DataBindingSubContext(dbc);
		this.bindings.run(new Runnable() {
			@Override
			public void run() {
				addBindings(dbc);
			}
		});
	}
	
	protected Composite createControl(final Composite parent) {
		final Composite composite= new Composite(parent, SWT.NONE);
		return composite;
	}
	
	protected void addBindings(final DataBindingContext dbc) {
	}
	
	protected void setSelected(final boolean selected) {
		if (selected == this.isSelected) {
			return;
		}
		
		this.isSelected= selected;
		
		if (this.bindings != null) {
			this.bindings.setEnabled(selected);
		}
	}
	
	public boolean isSelected() {
		return this.isSelected;
	}
	
	
	protected void load(final Map<String, String> config) {
	}
	
	protected void save(final Map<String, String> config) {
	}
	
}
