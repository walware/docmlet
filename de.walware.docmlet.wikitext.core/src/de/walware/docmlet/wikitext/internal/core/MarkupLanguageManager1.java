/*=============================================================================#
 # Copyright (c) 2014-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.osgi.util.NLS;

import de.walware.jcommons.collections.CopyOnWriteIdentityListSet;
import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.docmlet.wikitext.core.WikitextCore;
import de.walware.docmlet.wikitext.core.markup.IMarkupConfig;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguageManager1;


public class MarkupLanguageManager1 extends MarkupLanguageManager 
		implements IMarkupLanguageManager1, IResourceChangeListener {
	
	
	public static final IMarkupLanguageManager1 INSTANCE;
	static {
		final MarkupLanguageManager1 instance= new MarkupLanguageManager1();
		instance.addConfigChangedListener(new MarkupConfigTextFileBufferUpdater(instance));
		INSTANCE= instance;
	}
	
	
	private static final String NODE_QUALIFIER= WikitextCore.PLUGIN_ID + "/markup/Wikitext"; //$NON-NLS-1$
	
	private static final String KEY= "MarkupConfig"; //$NON-NLS-1$
	
	private static final QualifiedName PROPERTY_NAME= new QualifiedName(WikitextCore.PLUGIN_ID, "Wikitext." + KEY);
	
	private static final String PREFIX= KEY + '!';
	
	
	
	private static class Property {
		
		
		final String languageName;
		
		final IMarkupLanguage language;
		
		
		public Property(final String languageName, final IMarkupLanguage language) {
			this.languageName= languageName;
			this.language= language;
		}
		
	}
	
	
	private static String getPrefKey(final IFile file) {
		return PREFIX + file.getProjectRelativePath().toPortableString(); 
	}
	
	
	private class ProjectEntry implements IPreferenceChangeListener {
		
		private final IProject project;
		
		private final IEclipsePreferences prefNode;
		
		private IFile tmpFile;
		private Property tmpFileProperty;
		
		
		public ProjectEntry(final IProject project) {
			this.project= project;
			
			final ProjectScope projectScope= new ProjectScope(project);
			this.prefNode= projectScope.getNode(NODE_QUALIFIER);
			
			this.prefNode.addPreferenceChangeListener(this);
		}
		
		
		public IProject getProject() {
			return this.project;
		}
		
		public IEclipsePreferences getPrefNode() {
			return this.prefNode;
		}
		
		public void dispose() {
			this.prefNode.removePreferenceChangeListener(this);
		}
		
		public void setTmpFileProperty(final IFile file, final Property property) {
			this.tmpFile= file;
			this.tmpFileProperty= property;
		}
		
		@Override
		public void preferenceChange(final PreferenceChangeEvent event) {
			if (event.getKey().startsWith(PREFIX) && this.project.isOpen()) {
				final IPath path= Path.fromPortableString(event.getKey().substring(PREFIX.length()));
				IFile file= null;
				synchronized (this) {
					Property property= null;
					if (this.tmpFile != null && this.tmpFile.getFullPath().equals(path)) {
						file= this.tmpFile;
						property= this.tmpFileProperty;
						this.tmpFile= null;
						this.tmpFileProperty= null;
					}
					else {
						file= this.project.getFile(path);
					}
					try {
						file.setSessionProperty(PROPERTY_NAME, property);
					}
					catch (final CoreException e) {}
				}
				synchronized (MarkupLanguageManager1.this.backgroundJob) {
					MarkupLanguageManager1.this.backgroundJob.addChangedConfig(file);
					MarkupLanguageManager1.this.backgroundJob.schedule();
				}
			}
		}
		
	}
	
	private class BackgroundJob extends Job {
		
		
		private List<IProject> projectsToDispose;
		
		private List<IFile> changedFiles;
		
		private Map<String, List<IProject>> changedLanguages;
		
		
		public BackgroundJob() {
			super("Markup Config Worker");
			setUser(false);
			setSystem(true);
			setPriority(SHORT);
		}
		
		
		public void addProjectToDispose(final IProject project) {
			if (this.projectsToDispose == null) {
				this.projectsToDispose= new ArrayList<>();
			}
			this.projectsToDispose.add(project);
		}
		
		public void addChangedConfig(final String languageName) {
			if (this.changedLanguages == null) {
				this.changedLanguages= new IdentityHashMap<>();
			}
			this.changedLanguages.put(languageName, ImCollections.<IProject>newList());
		}
		
		public void addChangedConfig(final IProject project, final String languageName) {
			if (this.changedLanguages == null) {
				this.changedLanguages= new IdentityHashMap<>();
			}
			List<IProject> projects= this.changedLanguages.get(languageName);
			if (projects == null) {
				projects= new ArrayList<>();
				this.changedLanguages.put(languageName, projects);
			}
			else if (projects.isEmpty()) {
				return;
			}
			if (!projects.contains(project)) {
				projects.add(project);
			}
		}
		
		public void addChangedConfig(final IFile file) {
			if (this.changedFiles == null) {
				this.changedFiles= new ArrayList<>();
			}
			this.changedFiles.add(file);
		}
		
		
		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			final SubMonitor m= SubMonitor.convert(monitor);
			execDispose();
			execNotify(m);
			return Status.OK_STATUS;
		}
		
		private void execDispose() {
			while (true) {
				final List<IProject> projects;
				synchronized (this) {
					projects= this.projectsToDispose;
					this.projectsToDispose= null;
				}
				
				if (projects != null) {
					synchronized (projects) {
						for (final IProject project : projects) {
							final ProjectEntry entry= MarkupLanguageManager1.this.projectEntries.remove(project);
							if (entry != null) {
								entry.dispose();
							}
						}
					}
				}
			}
		}
		
		private void execNotify(final SubMonitor m) {
			final List<IFile> files;
			final Map<String, List<IProject>> changedLanguages;
			synchronized (this) {
				files= this.changedFiles;
				this.changedFiles= null;
				
				changedLanguages= this.changedLanguages;
				this.changedLanguages= null;
			}
			
			m.setWorkRemaining(((changedLanguages != null) ? changedLanguages.size() * 10 : 0) +
					((files != null) ? files.size() : 0) );
			
			if (changedLanguages != null) {
				final SubMonitor mLanguages= m.newChild(changedLanguages.size() * 10);
				final ImList<IMarkupConfigChangedListener> listeners= MarkupLanguageManager1.this.configChangedListeners.toList();
				mLanguages.setWorkRemaining(listeners.size());
				for (final IMarkupConfigChangedListener listener : listeners) {
					try {
						listener.configChanged(changedLanguages, mLanguages.newChild(1));
					}
					catch (final CoreException e) {
						
					}
				}
			}
			
			if (files != null) {
				for (final IFile file : files) {
					final SubMonitor mFile= m.newChild(1);
					if (file.exists()) {
						final ImList<IMarkupConfigChangedListener> listeners= MarkupLanguageManager1.this.configChangedListeners.toList();
						mFile.setWorkRemaining(listeners.size());
						for (final IMarkupConfigChangedListener listener : listeners) {
							try {
								listener.configChanged(file, mFile.newChild(1));
							}
							catch (final CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		
	}
	
	
	private final CopyOnWriteIdentityListSet<IMarkupConfigChangedListener> configChangedListeners= new CopyOnWriteIdentityListSet<>();
	
	private final Map<IProject, ProjectEntry> projectEntries= new HashMap<>();
	
	private final BackgroundJob backgroundJob= new BackgroundJob();
	
	
	public MarkupLanguageManager1() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
				IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE );
	}
	
	
	private String getLanguageName(final IFile file, final boolean required) throws CoreException {
		Exception cause= null;
		try {
			final IContentDescription contentDescription= file.getContentDescription();
			if (contentDescription != null) {
				return getLanguageName(contentDescription.getContentType());
			}
		}
		catch (final CoreException e) {
			cause= e;
		}
		if (required) {
			throw new CoreException(new Status(IStatus.ERROR, WikitextCore.PLUGIN_ID,
					NLS.bind("Failed to detect markup language for file ''{0}''.", file.getFullPath()),
					cause ));
		}
		else {
			return null;
		}
	}
	
	private ProjectEntry getProjectEntry(final IProject project) {
		synchronized (this.projectEntries) {
			ProjectEntry entry= this.projectEntries.get(project);
			if (entry == null) {
				entry= new ProjectEntry(project);
				this.projectEntries.put(project, entry);
			}
			return entry;
		}
	}
	
	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		final IResource resource= event.getResource();
		if (resource instanceof IResource) {
			synchronized (this.backgroundJob) {
				this.backgroundJob.addProjectToDispose((IProject) resource);
				this.backgroundJob.schedule(100);
			}
		}
	}
	
	
	@Override
	public void addConfigChangedListener(final IMarkupConfigChangedListener listener) {
		this.configChangedListeners.add(listener);
	}
	
	@Override
	public void removeConfigChangedListern(final IMarkupConfigChangedListener listener) {
		this.configChangedListeners.remove(listener);
	}
	
	
	@Override
	public IMarkupLanguage getLanguage(final IFile file, String languageName,
			final boolean inherit) {
		if (file == null) {
			throw new NullPointerException("file"); //$NON-NLS-1$
		}
		try {
			final boolean exists= file.exists();
			Property property= null;
			String configString= null;
			if (exists) {
				property= (Property) file.getSessionProperty(PROPERTY_NAME);
			}
			
			if (property == null || (languageName != null && property.languageName != languageName)) {
				final ProjectEntry projectEntry= getProjectEntry(file.getProject());
				final IEclipsePreferences prefNode= projectEntry.getPrefNode();
				
				configString= prefNode.get(getPrefKey(file), null);
				
				if (property == null) {
					final String fileLanguageName= getLanguageName(file, (languageName == null));
					
					if (exists && fileLanguageName != null) {
						property= new Property(fileLanguageName, (configString != null) ?
								getLanguage(fileLanguageName, configString) : null );
						synchronized (projectEntry) {
							if (prefNode.get(getPrefKey(file), null) == configString) {
								file.setSessionProperty(PROPERTY_NAME, property);
							}
						}
					}
					if (languageName == null) {
						languageName= fileLanguageName;
					}
				}
				
				if (property == null || property.languageName != languageName) {
					return (configString != null || inherit) ?
							getLanguage(languageName, configString) :
							null;
				}
			}
			
			return (property.language != null || !inherit) ?
					property.language :
					getLanguage(file.getProject(), property.languageName, true);
		}
		catch (final Exception e) {
			WikitextCorePlugin.log(new Status(IStatus.ERROR, WikitextCore.PLUGIN_ID,
					NLS.bind("An error occurred when occurred when reading markup configuration for ''{0}''.",
							file.getFullPath() ),
					e ));
			return null;
		}
	}
	
	private IMarkupLanguage getLanguage(final String name, final String configString) {
		if (configString != null) {
			final IMarkupConfig config= createNewConfig(name);
			if (config != null && config.load(configString)) {
				return createLanguage(name, config);
			}
		}
		return getLanguage(name);
	}
	
	private IMarkupLanguage createLanguage(final String name, final IMarkupConfig config) {
		final IMarkupLanguage language= getLanguage(name).clone();
		language.setMarkupConfig(config);
		return language;
	}
	
	
	@Override
	public IMarkupConfig getConfig(final String languageName) {
		final IMarkupLanguage language= getLanguage(languageName);
		if (language == null) {
			throw new IllegalStateException("Language is missing: " + languageName);
		}
		return language.getMarkupConfig();
	}
	
	@Override
	protected void configChanged(final String languageName) {
		super.configChanged(languageName);
		
		synchronized (this.backgroundJob) {
			this.backgroundJob.addChangedConfig(languageName);
			this.backgroundJob.schedule(100);
		}
	}
	
	
	public IMarkupLanguage getLanguage(final IProject project, final String languageName,
			final boolean inherit) {
		return (inherit) ?
				getLanguage(languageName) :
				null;
	}
	
	@Override
	public void setConfig(final IFile file, final IMarkupConfig config) {
		if (file == null) {
			throw new NullPointerException("file"); //$NON-NLS-1$
		}
		try {
			final ProjectEntry projectEntry= getProjectEntry(file.getProject());
			final IEclipsePreferences prefNode= projectEntry.getPrefNode();
			
			if (config == null) {
				synchronized (projectEntry) {
					projectEntry.setTmpFileProperty(file, null);
					prefNode.remove(getPrefKey(file));
				}
			}
			else {
				final String languageName= getLanguageName(file, true);
				final Property property= new Property(languageName,
						createLanguage(languageName, config) );
				final String value= config.getString();
				synchronized (projectEntry) {
					projectEntry.setTmpFileProperty(file, property);
					prefNode.put(getPrefKey(file), value);
				}
			}
			
			prefNode.flush();
		}
		catch (final Exception e) {
			WikitextCorePlugin.log(new Status(IStatus.ERROR, WikitextCore.PLUGIN_ID,
					NLS.bind("An error occurred when occurred when saving markup configuration for ''{0}''.",
							file.getFullPath().toFile() ),
					e ));
		}
	}
	
	@Override
	public IMarkupConfig getConfig(final IFile file, final String languageName) {
		final IMarkupLanguage language= getLanguage(file, languageName, false);
		return (language != null) ? language.getMarkupConfig() : null;
	}
	
}
