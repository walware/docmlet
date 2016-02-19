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

package de.walware.docmlet.wikitext.internal.core;

import de.walware.ecommons.preferences.PreferencesManageListener;
import de.walware.ecommons.preferences.core.IPreferenceAccess;

import de.walware.docmlet.wikitext.core.IWikitextCoreAccess;
import de.walware.docmlet.wikitext.core.WikitextCodeStyleSettings;


final class WikitextCoreAccess implements IWikitextCoreAccess {
	
	
	private boolean isDisposed;
	
	private final IPreferenceAccess prefs;
	
	private PreferencesManageListener codeStyleListener;
	private WikitextCodeStyleSettings codeStyle;
	
	
	public WikitextCoreAccess(final IPreferenceAccess prefs) {
		this.prefs= prefs;
		
		this.codeStyle= new WikitextCodeStyleSettings(1);
		this.codeStyleListener= new PreferencesManageListener(this.codeStyle, this.prefs, WikitextCodeStyleSettings.ALL_GROUP_IDS);
		this.codeStyle.load(prefs);
		this.codeStyle.resetDirty();
	}
	
	
	@Override
	public IPreferenceAccess getPrefs() {
		return this.prefs;
	}
	
	@Override
	public WikitextCodeStyleSettings getWikitextCodeStyle() {
		WikitextCodeStyleSettings codeStyle= this.codeStyle;
		if (codeStyle == null) {
			synchronized (this) {
				codeStyle= this.codeStyle;
				if (codeStyle == null) {
					codeStyle= new WikitextCodeStyleSettings(1);
					if (!this.isDisposed) {
						this.codeStyleListener= new PreferencesManageListener(codeStyle,
								this.prefs, WikitextCodeStyleSettings.ALL_GROUP_IDS );
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
	}
	
}
