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

package de.walware.docmlet.wikitext.internal.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.osgi.util.NLS;

import de.walware.jcommons.collections.ImCollections;

import de.walware.ecommons.preferences.IPreferenceAccess;
import de.walware.ecommons.preferences.Preference;
import de.walware.ecommons.preferences.PreferencesUtil;

import de.walware.docmlet.wikitext.core.WikitextCore;
import de.walware.docmlet.wikitext.core.markup.IMarkupConfig;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguageManager;


public class MarkupLanguageManager implements IMarkupLanguageManager {
	
	
	private static final String EXTENSION_POINT_ID= "de.walware.docmlet.wikitext.markupLanguages"; //$NON-NLS-1$
	
	private static final String NAME_ATTRIBUTE_NAME= "name"; //$NON-NLS-1$
	private static final String LABEL_ATTRIBUTE_NAME= "label"; //$NON-NLS-1$
	private static final String CLASS_ATTRIBUTE_NAME= "class"; //$NON-NLS-1$
	private static final String CONFIG_CLASS_ATTRIBUTE_NAME= "configClass"; //$NON-NLS-1$
	private static final String CONTENT_TYPE_ID_ATTRIBUTE_NAME= "contentTypeId"; //$NON-NLS-1$
	
	private static final String CONFIG_WORKBENCH_KEY= "MarkupConfig.Workbench.config"; //$NON-NLS-1$
	
	
	protected final class MLEntry implements IMarkupLanguageDescriptor, IPreferenceChangeListener {
		
		private static final byte S_LANGUAGE_FAILED=        0b0_00010000;
		private static final byte S_CONFIG_FAILED=          0b0_00100000;
		
		
		private final String name;
		private final String label;
		
		private final String contentTypdId;
		
		
		private final IConfigurationElement element;
		
		private byte state;
		
		
		private final String prefQualifier;
		
		private volatile IMarkupLanguage mlInstance;
		
		
		public MLEntry(final String name, final IConfigurationElement element) {
			this.name= name.intern();
			
			{	final String value= element.getAttribute(LABEL_ATTRIBUTE_NAME);
				this.label= (value != null && !value.isEmpty()) ? value.intern() : this.name;
			}
			{	final String value= element.getAttribute(CONTENT_TYPE_ID_ATTRIBUTE_NAME);
				this.contentTypdId= (value != null && !value.isEmpty()) ? value.intern() : null;
			}
			
			this.element= element;
			
			final String pluginId= this.element.getContributor().getName();
			this.prefQualifier= pluginId + "/markup/" + name; //$NON-NLS-1$
		}
		
		
		@Override
		public String getName() {
			return this.name;
		}
		
		@Override
		public IContributor getContributor() {
			return this.element.getContributor();
		}
		
		@Override
		public String getPreferenceQualifier() {
			return this.prefQualifier;
		}
		
		@Override
		public String getLabel() {
			return this.label;
		}
		
		public String getContentTypdId() {
			return this.contentTypdId;
		}
		
		public IMarkupLanguage getLanguage() {
			IMarkupLanguage markupLanguage= this.mlInstance;
			if (markupLanguage == null) {
				synchronized (this) {
					markupLanguage= this.mlInstance;
					if (markupLanguage == null && (this.state & S_LANGUAGE_FAILED) == 0) {
						try {
							markupLanguage= (IMarkupLanguage)
									this.element.createExecutableExtension(CLASS_ATTRIBUTE_NAME);
							if (!getName().equals(markupLanguage.getName())) {
								throw new IllegalArgumentException("name"); //$NON-NLS-1$
							}
							final IMarkupConfig config= loadWorkbenchConfig();
							if (config != null) {
								markupLanguage.setMarkupConfig(config);
							}
							this.mlInstance= markupLanguage;
						}
						catch (final CoreException e) {
							this.state|= S_LANGUAGE_FAILED;
							WikitextCorePlugin.log(new Status(IStatus.ERROR, WikitextCore.PLUGIN_ID, 0,
									NLS.bind("An error occurred when loading markup language ''{0}''.", getName()),
									e ));
							return null;
						}
					}
				}
			}
			return markupLanguage;
		}
		
		protected IMarkupConfig loadWorkbenchConfig() {
			if (this.prefQualifier == null) {
				return null;
			}
			
			final IMarkupConfig config= newConfig();
			if (config == null) {
				return null;
			}
			
			final IPreferenceAccess prefs= PreferencesUtil.getInstancePrefs();
			prefs.addPreferenceNodeListener(this.prefQualifier, this);
			final Preference<String> pref= new Preference.StringPref2(this.prefQualifier,
					"MarkupConfig.Workbench.config" ); //$NON-NLS-1$
			final String configString= prefs.getPreferenceValue(pref);
			if (configString != null) {
				config.load(configString);
			}
			return config;
		}
		
		@Override
		public boolean isConfigSupported() {
			return (this.element.getAttribute(CONFIG_CLASS_ATTRIBUTE_NAME) != null
					&& (this.state & S_CONFIG_FAILED) == 0);
		}
		
		@Override
		public IMarkupConfig newConfig() {
			if (isConfigSupported()) {
				try {
					return (IMarkupConfig) this.element.createExecutableExtension(CONFIG_CLASS_ATTRIBUTE_NAME);
				}
				catch (final CoreException e) {
					this.state|= S_CONFIG_FAILED;
					WikitextCorePlugin.log(new Status(IStatus.ERROR, WikitextCore.PLUGIN_ID, 0,
							NLS.bind("An error occurred when loading markup language ''{0}''.", getName()),
							e ));
				}
			}
			return null;
		}
		
		@Override
		public void preferenceChange(final PreferenceChangeEvent event) {
			if (event.getKey().equals(CONFIG_WORKBENCH_KEY)) {
				synchronized (this) {
					this.mlInstance.setMarkupConfig(loadWorkbenchConfig());
				}
				configChanged(this.name);
			}
		}
		
	}
	
	
	private List<String> names;
	private final Map<String, MLEntry> nameMap= new HashMap<>();
	private final Map<String, MLEntry> contentTypeMap= new HashMap<>();
	
	
	public MarkupLanguageManager() {
		readRegistry();
	}
	
	
	private void readRegistry() {
		final IConfigurationElement[] elements= Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID);
		for (final IConfigurationElement element : elements) {
			if (element.getName().equals("markupLanguage")) { //$NON-NLS-1$
				final String name= element.getAttribute(NAME_ATTRIBUTE_NAME);
				if (name == null || name.isEmpty()) {
					continue;
				}
				final MLEntry mlEntry= new MLEntry(name, element);
				this.nameMap.put(mlEntry.getName(), mlEntry);
				if (mlEntry.getContentTypdId() != null) {
					this.contentTypeMap.put(mlEntry.getContentTypdId(), mlEntry);
				}
			}
		}
		{	final Set<String> nameSet= this.nameMap.keySet();
			final String[] array= nameSet.toArray(new String[nameSet.size()]);
			Arrays.sort(array);
			this.names= ImCollections.newList(array);
		}
	}
	
	
	@Override
	public List<String> getLanguageNames() {
		return this.names;
	}
	
	@Override
	public MLEntry getLanguageDescriptor(final String name) {
		return this.nameMap.get(name);
	}
	
	@Override
	public IMarkupLanguage getLanguage(final String name) {
		final MLEntry mlEntry= this.nameMap.get(name);
		return (mlEntry != null) ? mlEntry.getLanguage() : null;
	}
	
	@Override
	public IMarkupLanguage getLanguage(final IContentType contentType) {
		final MLEntry mlEntry= this.contentTypeMap.get(contentType.getId());
		return (mlEntry != null) ? mlEntry.getLanguage() : null;
	}
	
	public String getLanguageName(final IContentType contentType) {
		final MLEntry mlEntry= this.contentTypeMap.get(contentType.getId());
		return (mlEntry != null) ? mlEntry.getName() : null;
	}
	
	protected IMarkupConfig createNewConfig(final String languageName) {
		final MLEntry mlEntry= this.nameMap.get(languageName);
		return (mlEntry != null) ? mlEntry.newConfig() : null;
	}
	
	protected void configChanged(final String languageName) {
	}
	
}
