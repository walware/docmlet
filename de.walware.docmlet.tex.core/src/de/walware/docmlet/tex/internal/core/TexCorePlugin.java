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
import de.walware.docmlet.tex.core.model.TexModel;
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
	
	
	private boolean fStarted;
	
	private LtxModelManager fTexModelManager;
	
	private TexCoreAccess fWorkbenchAccess;
	private TexCoreAccess fDefaultsAccess;
	
	
	public TexCorePlugin() {
	}
	
	
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		gPlugin = this;
		
		fTexModelManager = new LtxModelManager(TexModel.LTX_TYPE_ID);
		
		fStarted = true;
	}
	
	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			synchronized (this) {
				fStarted = false;
			}
			if (fTexModelManager != null) {
				fTexModelManager.dispose();
				fTexModelManager = null;
			}
		}
		finally {
			gPlugin = null;
			super.stop(context);
		}
	}
	
	
	public LtxModelManager getLtxModelManager() {
		return fTexModelManager;
	}
	
	public synchronized ITexCoreAccess getWorkbenchAccess() {
		if (fWorkbenchAccess == null) {
			if (!fStarted) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			fWorkbenchAccess = new TexCoreAccess(PreferencesUtil.getInstancePrefs());
		}
		return fWorkbenchAccess;
	}
	
	public synchronized ITexCoreAccess getDefaultsAccess() {
		if (fDefaultsAccess == null) {
			if (!fStarted) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			fDefaultsAccess = new TexCoreAccess(PreferencesUtil.getDefaultPrefs());
		}
		return fDefaultsAccess;
	}
	
}
