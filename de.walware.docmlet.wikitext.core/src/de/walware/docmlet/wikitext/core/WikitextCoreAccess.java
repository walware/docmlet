/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.core;

import java.util.Set;

import de.walware.ecommons.preferences.IPreferenceAccess;
import de.walware.ecommons.preferences.PreferencesManageListener;
import de.walware.ecommons.preferences.PreferencesUtil;
import de.walware.ecommons.preferences.SettingsChangeNotifier.ManageListener;


public class WikitextCoreAccess implements IWikitextCoreAccess {
	
	
	private final IPreferenceAccess fPrefs;
	
	private ManageListener fCommonPrefsListener;
	
	private final PreferencesManageListener fCodeStyleListener;
	private final WikitextCodeStyleSettings fCodeStyle;
	
	
	public WikitextCoreAccess(final IPreferenceAccess prefs) {
		this.fPrefs= prefs;
		
		this.fCodeStyle= new WikitextCodeStyleSettings(1);
		this.fCodeStyleListener= new PreferencesManageListener(this.fCodeStyle, this.fPrefs, WikitextCodeStyleSettings.ALL_GROUP_IDS);
		this.fCodeStyle.load(prefs);
		this.fCodeStyle.resetDirty();
		
		this.fCommonPrefsListener= new ManageListener() {
			@Override
			public void beforeSettingsChangeNotification(final Set<String> groupIds) {
			}
			@Override
			public void afterSettingsChangeNotification(final Set<String> groupIds) {
				// update...()
			}
		};
		PreferencesUtil.getSettingsChangeNotifier().addManageListener(this.fCommonPrefsListener);
	}
	
	public void dispose() {
		if (this.fCommonPrefsListener != null) {
			this.fCodeStyleListener.dispose();
			PreferencesUtil.getSettingsChangeNotifier().removeManageListener(this.fCommonPrefsListener);
			this.fCommonPrefsListener= null;
		}
	}
	
	
	@Override
	public IPreferenceAccess getPrefs() {
		return this.fPrefs;
	}
	
	@Override
	public WikitextCodeStyleSettings getWikitextCodeStyle() {
		return this.fCodeStyle;
	}
	
}
