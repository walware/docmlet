/*******************************************************************************
 * Copyright (c) 2012-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.core;

import java.util.Set;

import de.walware.ecommons.preferences.IPreferenceAccess;
import de.walware.ecommons.preferences.PreferencesManageListener;
import de.walware.ecommons.preferences.PreferencesUtil;
import de.walware.ecommons.preferences.SettingsChangeNotifier.ManageListener;

import de.walware.docmlet.tex.core.commands.TexCommandSet;


public class TexCoreAccess implements ITexCoreAccess {
	
	
	private final IPreferenceAccess fPrefs;
	
	private ManageListener fCommonPrefsListener;
	
	private TexCommandSet fCommandSet;
	private final PreferencesManageListener fCodeStyleListener;
	private final TexCodeStyleSettings fCodeStyle;
	
	
	public TexCoreAccess(final IPreferenceAccess prefs) {
		fPrefs = prefs;
		
		fCodeStyle = new TexCodeStyleSettings(1);
		fCodeStyleListener = new PreferencesManageListener(fCodeStyle, fPrefs, TexCodeStyleSettings.ALL_GROUP_IDS);
		fCodeStyle.load(prefs);
		fCodeStyle.resetDirty();
		
		fCommonPrefsListener = new ManageListener() {
			@Override
			public void beforeSettingsChangeNotification(final Set<String> groupIds) {
			}
			@Override
			public void afterSettingsChangeNotification(final Set<String> groupIds) {
				if (groupIds.contains(TexCommandSet.GROUP_ID)) {
					updateCommandSet();
				}
			}
		};
		PreferencesUtil.getSettingsChangeNotifier().addManageListener(fCommonPrefsListener);
		updateCommandSet();
	}
	
	public void dispose() {
		if (fCommonPrefsListener != null) {
			fCodeStyleListener.dispose();
			PreferencesUtil.getSettingsChangeNotifier().removeManageListener(fCommonPrefsListener);
			fCommonPrefsListener = null;
		}
	}
	
	private synchronized void updateCommandSet() {
		fCommandSet = new TexCommandSet(fPrefs);
	}
	
	
	@Override
	public IPreferenceAccess getPrefs() {
		return fPrefs;
	}
	
	@Override
	public TexCommandSet getTexCommandSet() {
		return fCommandSet;
	}
	
	@Override
	public TexCodeStyleSettings getTexCodeStyle() {
		return fCodeStyle;
	}
	
}
