/*=============================================================================#
 # Copyright (c) 2011-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.ui.sourceediting;

import org.eclipse.jface.preference.IPreferenceStore;

import de.walware.ecommons.ltk.ui.sourceediting.ISmartInsertSettings.HardWrapMode;
import de.walware.ecommons.ltk.ui.sourceediting.ISmartInsertSettings.TabAction;
import de.walware.ecommons.preferences.core.Preference;
import de.walware.ecommons.preferences.core.Preference.BooleanPref;
import de.walware.ecommons.preferences.core.Preference.EnumPref;
import de.walware.ecommons.text.ui.settings.AssistPreferences;

import de.walware.docmlet.wikitext.internal.ui.WikitextUIPlugin;
import de.walware.docmlet.wikitext.ui.WikitextUI;


public class WikitextEditingSettings {
	// Default values see WikitextUIPreferenceInitializer
	
	
	public static final String EDITOR_OPTIONS_QUALIFIER= WikitextUI.PLUGIN_ID + "/editor/options"; //$NON-NLS-1$
	
	
	public static final BooleanPref SPELLCHECKING_ENABLED_PREF= new BooleanPref(
			EDITOR_OPTIONS_QUALIFIER, "SpellCheck.enabled"); //$NON-NLS-1$
	
	// not in group
	public static final BooleanPref FOLDING_ENABLED_PREF= new BooleanPref(
			EDITOR_OPTIONS_QUALIFIER, "Folding.enabled"); //$NON-NLS-1$
	
	public static final String FOLDING_SHARED_GROUP_ID= "Wikitext/editor/folding.shared"; //$NON-NLS-1$
	
	public static final BooleanPref FOLDING_RESTORE_STATE_ENABLED_PREF= new BooleanPref(
			EDITOR_OPTIONS_QUALIFIER, "Folding.RestoreState.enabled"); //$NON-NLS-1$
	
	public static final BooleanPref MARKOCCURRENCES_ENABLED_PREF= new BooleanPref(
			EDITOR_OPTIONS_QUALIFIER, "MarkOccurrences.enabled"); //$NON-NLS-1$
	
	
	public static final String SMARTINSERT_GROUP_ID= "Wikitext/editor/smartinsert"; //$NON-NLS-1$
	
	public static final Preference<Boolean> SMARTINSERT_BYDEFAULT_ENABLED_PREF= new BooleanPref(
			EDITOR_OPTIONS_QUALIFIER, "SmartInsert.ByDefault.enabled"); //$NON-NLS-1$
	
	public static final Preference<TabAction> SMARTINSERT_TAB_ACTION_PREF= new EnumPref<>(
			EDITOR_OPTIONS_QUALIFIER, "SmartInsert.Tab.action", TabAction.class); //$NON-NLS-1$
	
	public static final Preference<Boolean> SMARTINSERT_CLOSEBRACKETS_ENABLED_PREF= new BooleanPref(
			EDITOR_OPTIONS_QUALIFIER, "SmartInsert.CloseBrackets.enabled"); //$NON-NLS-1$
	public static final Preference<Boolean> SMARTINSERT_CLOSEPARENTHESIS_ENABLED_PREF= new BooleanPref(
			EDITOR_OPTIONS_QUALIFIER, "SmartInsert.CloseParenthesis.enabled"); //$NON-NLS-1$
	public static final Preference<Boolean> SMARTINSERT_CLOSEMATHDOLLAR_ENABLED_PREF= new BooleanPref(
			EDITOR_OPTIONS_QUALIFIER, "SmartInsert.CloseMathDollar.enabled"); //$NON-NLS-1$
	
	public static final Preference<Boolean> SMARTINSERT_HARDWRAP_TEXT_ENABLED_PREF= new BooleanPref(
			EDITOR_OPTIONS_QUALIFIER, "SmartInsert.HardWrapText.enabled"); //$NON-NLS-1$
	public static final Preference<HardWrapMode> SMARTINSERT_HARDWRAP_MODE_PREF= new EnumPref<>(
			EDITOR_OPTIONS_QUALIFIER, "SmartInsert.HardWrap.mode", HardWrapMode.class); //$NON-NLS-1$
	
	
	public static final String TEXTSTYLE_CONFIG_QUALIFIER= WikitextUI.PLUGIN_ID + "/textstyle/Wikitext"; //$NON-NLS-1$
	
	public static final String ASSIST_PREF_QUALIFIER= WikitextUI.PLUGIN_ID + "/editor/assist"; //$NON-NLS-1$
	public static final String ASSIST_WIKIDOC_PREF_QUALIFIER= ASSIST_PREF_QUALIFIER + "/Wikidoc"; //$NON-NLS-1$
	
	private static final AssistPreferences ASSIST_PREFERENCES= new AssistPreferences(
			ASSIST_PREF_QUALIFIER );
	
	public static AssistPreferences getAssistPrefences() {
		return ASSIST_PREFERENCES;
	}
	
	
	public static IPreferenceStore getPreferenceStore() {
		return WikitextUIPlugin.getInstance().getPreferenceStore();
	}
	
}
