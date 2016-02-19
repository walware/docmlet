/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

import de.walware.ecommons.ICommonStatusConstants;
import de.walware.ecommons.IDisposable;
import de.walware.ecommons.preferences.core.util.PreferenceUtils;

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.core.TexCore;
import de.walware.docmlet.tex.internal.core.model.LtxModelManager;


public class TexCorePlugin extends Plugin {
	
	
	/** The shared instance */
	private static TexCorePlugin instance;
	
	/**
	 * Returns the shared plug-in instance
	 *
	 * @return the shared instance
	 */
	public static TexCorePlugin getInstance() {
		return instance;
	}
	
	public static final void log(final IStatus status) {
		final Plugin plugin = getInstance();
		if (plugin != null) {
			plugin.getLog().log(status);
		}
	}
	
	
	private boolean started;
	
	private List<IDisposable> disposables;
	
	private TexCoreAccess workbenchCoreAccess;
	private TexCoreAccess defaultsCoreAccess;
	
	private LtxModelManager ltxModelManager;
	
	
	public TexCorePlugin() {
	}
	
	
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		instance = this;
		
		this.disposables= new ArrayList<>();
		
		this.workbenchCoreAccess= new TexCoreAccess(
				PreferenceUtils.getInstancePrefs() );
		
		this.ltxModelManager= new LtxModelManager();
		this.disposables.add(ltxModelManager);
		
		synchronized (this) {
			this.started= true;
		}
	}
	
	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			synchronized (this) {
				this.started= false;
				
				this.ltxModelManager= null;
			}
			
			if (this.workbenchCoreAccess != null) {
				this.workbenchCoreAccess.dispose();
				this.workbenchCoreAccess= null;
			}
			if (this.defaultsCoreAccess != null) {
				this.defaultsCoreAccess.dispose();
				this.defaultsCoreAccess= null;
			}
			
			for (final IDisposable listener : this.disposables) {
				try {
					listener.dispose();
				}
				catch (final Throwable e) {
					getLog().log(new Status(IStatus.ERROR, TexCore.PLUGIN_ID, ICommonStatusConstants.INTERNAL_PLUGGED_IN,
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
	
	public LtxModelManager getLtxModelManager() {
		return this.ltxModelManager;
	}
	
	public ITexCoreAccess getWorkbenchAccess() {
		return this.workbenchCoreAccess;
	}
	
	public synchronized ITexCoreAccess getDefaultsAccess() {
		if (this.defaultsCoreAccess == null) {
			checkStarted();
			this.defaultsCoreAccess= new TexCoreAccess(
					PreferenceUtils.getDefaultPrefs() );
		}
		return this.defaultsCoreAccess;
	}
	
}
