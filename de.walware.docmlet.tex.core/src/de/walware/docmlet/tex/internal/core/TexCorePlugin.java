/*=============================================================================#
 # Copyright (c) 2009-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import de.walware.ecommons.preferences.PreferencesUtil;

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.core.TexCoreAccess;
import de.walware.docmlet.tex.internal.core.model.LtxModelManager;


public class TexCorePlugin extends Plugin {
	
	
	/** The shared instance */
	private static TexCorePlugin gPlugin;
	
	/**
	 * Returns the shared plug-in instance
	 *
	 * @return the shared instance
	 */
	public static TexCorePlugin getDefault() {
		return gPlugin;
	}
	
	public static final void log(final IStatus status) {
		final Plugin plugin = getDefault();
		if (plugin != null) {
			plugin.getLog().log(status);
		}
	}
	
	
	private boolean started;
	
	private LtxModelManager ltxModelManager;
	
	private TexCoreAccess workbenchAccess;
	private TexCoreAccess defaultsAccess;
	
	
	public TexCorePlugin() {
	}
	
	
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		gPlugin = this;
		
		this.ltxModelManager = new LtxModelManager();
		
		this.started = true;
	}
	
	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			synchronized (this) {
				this.started = false;
			}
			if (this.ltxModelManager != null) {
				this.ltxModelManager.dispose();
				this.ltxModelManager = null;
			}
		}
		finally {
			gPlugin = null;
			super.stop(context);
		}
	}
	
	
	public LtxModelManager getLtxModelManager() {
		return this.ltxModelManager;
	}
	
	public synchronized ITexCoreAccess getWorkbenchAccess() {
		if (this.workbenchAccess == null) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.workbenchAccess = new TexCoreAccess(PreferencesUtil.getInstancePrefs());
		}
		return this.workbenchAccess;
	}
	
	public synchronized ITexCoreAccess getDefaultsAccess() {
		if (this.defaultsAccess == null) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.defaultsAccess = new TexCoreAccess(PreferencesUtil.getDefaultPrefs());
		}
		return this.defaultsAccess;
	}
	
}
