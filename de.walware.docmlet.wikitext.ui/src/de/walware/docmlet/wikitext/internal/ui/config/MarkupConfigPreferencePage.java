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

package de.walware.docmlet.wikitext.internal.ui.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.walware.ecommons.IStatusChangeListener;
import de.walware.ecommons.databinding.jface.DataBindingSupport;
import de.walware.ecommons.preferences.Preference;
import de.walware.ecommons.preferences.ui.ConfigurationBlock;
import de.walware.ecommons.preferences.ui.ConfigurationBlockPreferencePage;
import de.walware.ecommons.preferences.ui.ManagedConfigurationBlock;
import de.walware.ecommons.ui.components.ButtonGroup;
import de.walware.ecommons.ui.components.ButtonGroup.SelectionHandler;
import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.ui.util.ViewerUtil;

import de.walware.docmlet.wikitext.core.WikitextCore;
import de.walware.docmlet.wikitext.core.markup.IMarkupConfig;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguageManager.IMarkupLanguageDescriptor;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguageManager1;
import de.walware.docmlet.wikitext.ui.sourceediting.IMarkupConfigUIAdapter;


public class MarkupConfigPreferencePage extends ConfigurationBlockPreferencePage {
	
	
	public MarkupConfigPreferencePage() {
	}
	
	
	@Override
	protected ConfigurationBlock createConfigurationBlock() throws CoreException {
		return new MarkupConfigurationBlock(null, createStatusChangedListener());
	}
	
}


class MarkupConfigurationBlock extends ManagedConfigurationBlock {
	
	
	private class MarkupEntry {
		
		
		private final IMarkupLanguageDescriptor descriptor;
		
		private final Preference<String> pref;
		
		
		public MarkupEntry(final IMarkupLanguageDescriptor descriptor) {
			this.descriptor= descriptor;
			this.pref= new Preference.StringPref2(descriptor.getPreferenceQualifier(),
					"MarkupConfig.Workbench.config" ); //$NON-NLS-1$
		}
		
		
		public IMarkupLanguageDescriptor getDescriptor() {
			return this.descriptor;
		}
		
		public String getLabel() {
			return this.descriptor.getLabel();
		}
		
		public Preference<String> getPref() {
			return this.pref;
		}
		
	}
	
	
	private IMarkupLanguageManager1 markupLanguageManager;
	
	private List<MarkupEntry> markupEntries;
	
	private TableViewer markupEntriesControl;
	private ButtonGroup<MarkupEntry> markupEntriesButtons;
	
	
	public MarkupConfigurationBlock(final IProject project, final IStatusChangeListener statusListener) {
		super(project, statusListener);
	}
	
	
	@Override
	protected void createBlockArea(final Composite pageComposite) {
		this.markupLanguageManager= WikitextCore.getMarkupLanguageManager();
		
		final Map<Preference<?>, String> prefs= new HashMap<>();
		
		final List<String> languageNames= this.markupLanguageManager.getLanguageNames();
		this.markupEntries= new ArrayList<>(languageNames.size());
		for (final String languageName : languageNames) {
			final IMarkupLanguageDescriptor languageDescriptor= this.markupLanguageManager
					.getLanguageDescriptor(languageName);
			if (languageDescriptor.isConfigSupported()
					&& languageDescriptor.getPreferenceQualifier() != null) {
				final MarkupEntry entry= new MarkupEntry(languageDescriptor);
				this.markupEntries.add(entry);
				prefs.put(entry.getPref(), null);
			}
		}
		
		setupPreferenceManager(prefs);
		
		final Composite mainComposite= new Composite(pageComposite, SWT.NONE);
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mainComposite.setLayout(LayoutUtil.createCompositeGrid(2));
		
		{	final Label label= new Label(mainComposite, SWT.LEFT);
			label.setText(Messages.MarkupConfigs_label);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		}
		{	final TableViewer viewer= new TableViewer(mainComposite);
			viewer.setContentProvider(new ArrayContentProvider());
			viewer.setLabelProvider(new LabelProvider() {
				@Override
				public String getText(final Object element) {
					if (element instanceof MarkupEntry) {
						return ((MarkupEntry) element).getLabel();
					}
					return super.getText(element);
				}
			});
			viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			this.markupEntriesControl= viewer;
		}
		{	final ButtonGroup<MarkupEntry> buttons= new ButtonGroup<>(mainComposite);
			buttons.addEditButton(new SelectionHandler() {
				@Override
				public boolean run(final IStructuredSelection selection) {
					final Object element= getElement(selection);
					if (element instanceof MarkupEntry) {
						edit((MarkupEntry) element);
						return true;
					}
					return false;
				}
			});
			buttons.connectTo(this.markupEntriesControl, null);
			buttons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, true));
			this.markupEntriesButtons= buttons;
		}
		initBindings();
		updateControls();
	}
	
	
	@Override
	protected void addBindings(final DataBindingSupport db) {
		this.markupEntriesControl.setInput(this.markupEntries);
	}
	
	@Override
	protected void updateControls() {
		super.updateControls();
		
		ViewerUtil.scheduleStandardSelection(this.markupEntriesControl);
	}
	
	private void edit(final MarkupEntry entry) {
		final IMarkupConfig config= entry.getDescriptor().newConfig();
		if (config != null) {
			{	final String configString= getPreferenceValue(entry.getPref());
				if (configString != null) {
					config.load(configString);
				}
			}
			final IMarkupConfigUIAdapter ui= (IMarkupConfigUIAdapter) Platform.getAdapterManager()
					.loadAdapter(config, IMarkupConfigUIAdapter.class.getName());
			if (ui != null) {
				if (ui.edit(null, null, config, getShell())) {
					final String configString= config.getString();
					setPrefValue(entry.getPref(), configString);
				}
				return;
			}
		}
		MessageDialog.openInformation(getShell(), Messages.MarkupConfig_title,
				NLS.bind("Sorry, the configuration of {0} is not supported.",
						entry.getDescriptor().getName() ));
		this.markupEntriesControl.remove(entry);
	}
	
	@Override
	protected String[] getFullBuildDialogStrings(final boolean workspaceSettings) {
		final String title = Messages.MarkupConfig_NeedsBuild_title;
		String message;
		if (workspaceSettings) {
			message = Messages.MarkupConfig_NeedsFullBuild_message; 
		} else {
			message = Messages.MarkupConfig_NeedsProjectBuild_message; 
		}	
		return new String[] { title, message };
	}	
	
}
