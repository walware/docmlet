/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.ui.sourceediting;

import java.beans.Introspector;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import de.walware.ecommons.databinding.jface.DataBindingSupport;
import de.walware.ecommons.ui.dialogs.ExtStatusDialog;
import de.walware.ecommons.ui.util.DialogUtil;
import de.walware.ecommons.ui.util.LayoutUtil;

import de.walware.docmlet.wikitext.core.source.extdoc.AbstractMarkupConfig;
import de.walware.docmlet.wikitext.internal.ui.config.Messages;


public abstract class AbstractMarkupConfigDialog<T extends AbstractMarkupConfig<?>> extends ExtStatusDialog {
	
	
	private final String contextLabel;
	
	private final WritableValue contextValue;
	
	protected final T config;
	
	private Button contextControl;
	
	private Composite extensionsGroup;
	private final Map<String, Control> configControls= new LinkedHashMap<>();
	
	
	public AbstractMarkupConfigDialog(final Shell parent, final String contextLabel,
			final boolean isContextEnabled, final T customConfig) {
		super(parent, WITH_DATABINDING_CONTEXT);
		
		this.contextLabel= contextLabel;
		this.contextValue= new WritableValue((contextLabel == null || isContextEnabled), Boolean.TYPE);
		this.config= customConfig;
	}
	
	
	public boolean isCustomEnabled() {
		return (Boolean) this.contextValue.getValue();
	}
	
	public T getCustomConfig() {
		return this.config;
	}
	
	
	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite area= new Composite(parent, SWT.NONE);
		area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		area.setLayout(LayoutUtil.createDialogGrid(2));
		
		if (this.contextLabel != null) {
			final Button button= new Button(area, SWT.CHECK);
			button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
			button.setText(NLS.bind("Enable {0} specific configuration:", this.contextLabel));
			this.contextControl= button;
		}
		
		this.extensionsGroup= createExtensionGroup(area);
		if (this.extensionsGroup != null) {
			this.extensionsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		}
		
		return area;
	}
	
	protected Composite createExtensionGroup(final Composite parent) {
		final Group composite= new Group(parent, SWT.NONE);
		composite.setLayout(LayoutUtil.createGroupGrid(2));
		composite.setText("Extensions:");
		return composite;
	}
	
	protected Composite getExtensionComposite() {
		return this.extensionsGroup;
	}
	
	
	protected void addProperty(final Composite parent, final String propertyName,
			final String label) {
		{	final Button button= new Button(parent, SWT.CHECK);
			button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
			button.setText(label);
			this.configControls.put(propertyName, button);
		}
	}
	
	protected void addProperty(final Composite parent, final String propertyName) {
		switch (propertyName) {
		case AbstractMarkupConfig.YAML_METADATA_ENABLED_PROP:
			addProperty(parent, propertyName,
					Messages.MarkupConfig_YamlMetadata_Enable_label );
			return;
		case AbstractMarkupConfig.TEX_MATH_DOLLARS_ENABLED_PROP:
			addProperty(parent, propertyName,
					Messages.MarkupConfig_TexMathDollars_Enable_label );
			return;
		case AbstractMarkupConfig.TEX_MATH_SBACKSLASH_ENABLED_PROP:
			addProperty(parent, propertyName,
					Messages.MarkupConfig_TexMathSBackslash_Enable_label );
			return;
		default:
			addProperty(parent, propertyName, "Enable " + propertyName);
			return;
		}
	}
	
	
	@Override
	protected void addBindings(final DataBindingSupport db) {
		final DataBindingContext dbc= db.getContext();
		
		if (this.contextLabel != null) {
			dbc.bindValue(WidgetProperties.selection().observe(this.contextControl),
					this.contextValue );
			
			this.contextValue.addValueChangeListener(new IValueChangeListener() {
				@Override
				public void handleValueChange(final ValueChangeEvent event) {
					updateContextEnabled((Boolean) event.diff.getNewValue());
				}
			});
			updateContextEnabled((Boolean) this.contextValue.getValue());
		}
		
		for (final Entry<String, Control> entry : this.configControls.entrySet()) {
			dbc.bindValue(WidgetProperties.selection().observe(entry.getValue()),
					PojoProperties.value(Introspector.decapitalize(entry.getKey()), Boolean.TYPE)
							.observe(this.config) );
		}
	}
	
	protected void updateContextEnabled(final boolean enabled) {
		if (this.extensionsGroup != null) {
			DialogUtil.setEnabled(this.extensionsGroup, null, enabled);
		}
	}
	
}
