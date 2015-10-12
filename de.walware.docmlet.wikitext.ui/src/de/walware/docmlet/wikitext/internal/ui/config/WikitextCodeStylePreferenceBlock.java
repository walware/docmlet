/*=============================================================================#
 # Copyright (c) 2007-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.ui.config;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import de.walware.ecommons.IStatusChangeListener;
import de.walware.ecommons.databinding.jface.DataBindingSupport;
import de.walware.ecommons.preferences.Preference;
import de.walware.ecommons.preferences.ui.ManagedConfigurationBlock;
import de.walware.ecommons.text.ui.settings.IndentSettingsUI;
import de.walware.ecommons.ui.CombineStatusChangeListener;
import de.walware.ecommons.ui.util.LayoutUtil;

import de.walware.docmlet.wikitext.core.WikitextCodeStyleSettings;


/**
 * A PreferenceBlock for WikitextCodeStyleSettings (code formatting preferences).
 */
public class WikitextCodeStylePreferenceBlock extends ManagedConfigurationBlock {
	// in future supporting multiple profiles?
	// -> we bind to bean not to preferences
	
	
	private WikitextCodeStyleSettings model;
	
	private IndentSettingsUI stdIndentSettings;
	
	private final CombineStatusChangeListener statusListener;
	
	
	public WikitextCodeStylePreferenceBlock(final IProject project, final IStatusChangeListener statusListener) {
		super(project);
		this.statusListener = new CombineStatusChangeListener(statusListener);
		setStatusListener(this.statusListener);
	}
	
	
	@Override
	protected void createBlockArea(final Composite pageComposite) {
		final Map<Preference<?>, String> prefs = new HashMap<>();
		
		prefs.put(WikitextCodeStyleSettings.TAB_SIZE_PREF, WikitextCodeStyleSettings.INDENT_GROUP_ID);
		prefs.put(WikitextCodeStyleSettings.INDENT_DEFAULT_TYPE_PREF, WikitextCodeStyleSettings.INDENT_GROUP_ID);
		prefs.put(WikitextCodeStyleSettings.INDENT_SPACES_COUNT_PREF, WikitextCodeStyleSettings.INDENT_GROUP_ID);
		prefs.put(WikitextCodeStyleSettings.REPLACE_CONVERSATIVE_PREF, WikitextCodeStyleSettings.INDENT_GROUP_ID);
		prefs.put(WikitextCodeStyleSettings.REPLACE_TABS_WITH_SPACES_PREF, WikitextCodeStyleSettings.INDENT_GROUP_ID);
		
		setupPreferenceManager(prefs);
		
		this.model = new WikitextCodeStyleSettings(0);
		this.stdIndentSettings = new IndentSettingsUI();
		
		final Composite mainComposite = new Composite(pageComposite, SWT.NONE);
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mainComposite.setLayout((LayoutUtil.createCompositeGrid(2)));
		
		final TabFolder folder = new TabFolder(mainComposite, SWT.NONE);
		folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		{	final TabItem item = new TabItem(folder, SWT.NONE);
			item.setText(this.stdIndentSettings.getGroupLabel());
			item.setControl(createIndentControls(folder));
		}
		{	final TabItem item = new TabItem(folder, SWT.NONE);
			item.setText("&Line Wrapping");
			item.setControl(createLineControls(folder));
		}
		
		initBindings();
		updateControls();
	}
	
	private Control createIndentControls(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(LayoutUtil.createTabGrid(2));
		
		this.stdIndentSettings.createControls(composite);
		this.stdIndentSettings.getTabSizeControl().setEditable(false);
		LayoutUtil.addSmallFiller(composite, false);
		
		final Composite depthComposite = new Composite(composite, SWT.NONE);
		depthComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		depthComposite.setLayout(LayoutUtil.createCompositeGrid(4));
		
		LayoutUtil.addSmallFiller(composite, false);
		return composite;
	}
	
	private Control createLineControls(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(LayoutUtil.createTabGrid(2));
		
		this.stdIndentSettings.addLineWidth(composite);
		
		return composite;
	}
	
	@Override
	protected void addBindings(final DataBindingSupport db) {
		this.stdIndentSettings.addBindings(db, this.model);
	}
	
	@Override
	protected void updateControls() {
		this.model.load(this);
		this.model.resetDirty();
		getDataBinding().getContext().updateTargets();  // required for invalid target values
	}
	
	@Override
	protected void updatePreferences() {
		if (this.model.isDirty()) {
			this.model.resetDirty();
			setPrefValues(this.model.toPreferencesMap());
		}
	}
	
}
