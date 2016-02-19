/*=============================================================================#
 # Copyright (c) 2012-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.core;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImIdentitySet;

import de.walware.ecommons.preferences.PreferencesManageListener;
import de.walware.ecommons.preferences.core.IPreferenceAccess;
import de.walware.ecommons.preferences.core.IPreferenceSetService;
import de.walware.ecommons.preferences.core.IPreferenceSetService.IChangeEvent;

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.core.TexCodeStyleSettings;
import de.walware.docmlet.tex.core.commands.TexCommandSet;


final class TexCoreAccess implements ITexCoreAccess {
	
	
	private static final ImIdentitySet<String> PREF_QUALIFIERS= ImCollections.newIdentitySet(
			TexCommandSet.QUALIFIER );
	
	
	private boolean isDisposed;
	
	private final IPreferenceAccess prefs;
	
	private IPreferenceSetService.IChangeListener commonPrefsListener;
	
	private TexCommandSet commandSet;
	
	private TexCodeStyleSettings codeStyle;
	private PreferencesManageListener codeStyleListener;
	
	
	public TexCoreAccess(final IPreferenceAccess prefs) {
		this.prefs= prefs;
		
		this.commonPrefsListener= new IPreferenceSetService.IChangeListener() {
			@Override
			public void preferenceChanged(final IChangeEvent event) {
				if (event.contains(TexCommandSet.QUALIFIER)) {
					updateCommandSet();
				}
			}
		};
		this.prefs.addPreferenceSetListener(this.commonPrefsListener, PREF_QUALIFIERS);
		
		updateCommandSet();
	}
	
	
	@Override
	public IPreferenceAccess getPrefs() {
		return this.prefs;
	}
	
	private synchronized void updateCommandSet() {
		this.commandSet= new TexCommandSet(this.prefs);
	}
	
	@Override
	public TexCommandSet getTexCommandSet() {
		return this.commandSet;
	}
	
	@Override
	public TexCodeStyleSettings getTexCodeStyle() {
		TexCodeStyleSettings codeStyle= this.codeStyle;
		if (codeStyle == null) {
			synchronized (this) {
				codeStyle= this.codeStyle;
				if (codeStyle == null) {
					codeStyle= new TexCodeStyleSettings(1);
					if (!this.isDisposed) {
						this.codeStyleListener= new PreferencesManageListener(codeStyle,
								this.prefs, TexCodeStyleSettings.ALL_GROUP_IDS );
					}
					codeStyle.load(this.prefs);
					codeStyle.resetDirty();
					this.codeStyle= codeStyle;
				}
			}
		}
		return codeStyle;
	}
	
	
	public synchronized void dispose() {
		this.isDisposed= true;
		
		if (this.codeStyleListener != null) {
			this.codeStyleListener.dispose();
			this.codeStyleListener= null;
		}
		if (this.commonPrefsListener != null) {
			this.prefs.removePreferenceSetListener(this.commonPrefsListener);
			this.commonPrefsListener= null;
		}
	}
	
}
