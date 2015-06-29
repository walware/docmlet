/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
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
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import de.walware.ecommons.ICommonStatusConstants;
import de.walware.ecommons.IDisposable;
import de.walware.ecommons.preferences.PreferencesUtil;

import de.walware.docmlet.wikitext.core.IWikitextCoreAccess;
import de.walware.docmlet.wikitext.core.WikitextCore;
import de.walware.docmlet.wikitext.core.WikitextCoreAccess;
import de.walware.docmlet.wikitext.internal.core.model.WikitextModelManager;


public class WikitextCorePlugin extends Plugin {
	
	
	/** The shared instance */
	private static WikitextCorePlugin instance;
	
	/**
	 * Returns the shared plug-in instance
	 *
	 * @return the shared instance
	 */
	public static WikitextCorePlugin getInstance() {
		return instance;
	}
	
	public static final void log(final IStatus status) {
		final Plugin plugin= getInstance();
		if (plugin != null) {
			plugin.getLog().log(status);
		}
	}
	
	
	private boolean started;
	
	private List<IDisposable> disposables;
	
	private WikitextModelManager wikitextModelManager;
	
	private volatile WikitextCoreAccess workbenchAccess;
	private volatile WikitextCoreAccess defaultsAccess;
	
	
	public WikitextCorePlugin() {
	}
	
	
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		instance= this;
		
		this.disposables= new ArrayList<>();
		
		this.wikitextModelManager= new WikitextModelManager();
		this.disposables.add(this.wikitextModelManager);
		
		synchronized (this) {
			this.started= true;
		}
	}
	
	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			synchronized (this) {
				this.started= false;
				
				this.wikitextModelManager= null;
			}
			
			for (final IDisposable listener : this.disposables) {
				try {
					listener.dispose();
				}
				catch (final Throwable e) {
					getLog().log(new Status(IStatus.ERROR, WikitextCore.PLUGIN_ID, ICommonStatusConstants.INTERNAL_PLUGGED_IN,
							"Error occured while disposing a module.", e )); 
				}
			}
			this.disposables= null;
		}
		finally {
			instance= null;
			super.stop(context);
		}
	}
	
	
	private void checkStarted() {
		if (!this.started) {
			throw new IllegalStateException("Plug-in is not started.");
		}
	}
	
	public WikitextModelManager getWikidocModelManager() {
		return this.wikitextModelManager;
	}
	
	public IWikitextCoreAccess getWorkbenchAccess() {
		IWikitextCoreAccess access= this.workbenchAccess;
		if (access == null) {
			synchronized (this) {
				access= this.workbenchAccess;
				if (access == null) {
					checkStarted();
					access= this.workbenchAccess= new WikitextCoreAccess(
							PreferencesUtil.getInstancePrefs() );
				}
			}
		}
		return access;
	}
	
	public IWikitextCoreAccess getDefaultsAccess() {
		IWikitextCoreAccess access= this.defaultsAccess;
		if (access == null) {
			synchronized (this) {
				access= this.defaultsAccess;
				if (access == null) {
					checkStarted();
					access= this.defaultsAccess= new WikitextCoreAccess(
							PreferencesUtil.getDefaultPrefs() );
				}
			}
		}
		return access;
	}
	
}
