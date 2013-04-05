/*******************************************************************************
 * Copyright (c) 2011-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.ui.editors;

import de.walware.ecommons.ltk.ui.sourceediting.ISmartInsertSettings.TabAction;
import de.walware.ecommons.preferences.Preference;
import de.walware.ecommons.preferences.Preference.BooleanPref;
import de.walware.ecommons.preferences.Preference.EnumPref;

import de.walware.docmlet.tex.internal.ui.TexUIPlugin;


public class TexEditorOptions {
	// Default values see RUIPreferenceInitializer
	
	public static final String GROUP_ID = "tex/tex.editor/options"; //$NON-NLS-1$
	
	
	public static final String TEXEDITOR_NODE = TexUIPlugin.PLUGIN_ID + "/editor.tex/options"; //$NON-NLS-1$
	
	
	public static final BooleanPref SPELLCHECKING_ENABLED_PREF = new BooleanPref(
			TEXEDITOR_NODE, "SpellCheck.enabled"); //$NON-NLS-1$
	
	// not in group
	public static final BooleanPref FOLDING_ENABLED_PREF = new BooleanPref(
			TEXEDITOR_NODE, "Folding.enabled"); //$NON-NLS-1$
	
	public static final String FOLDING_SHARED_GROUP_ID = "tex/tex.editor/folding.shared"; //$NON-NLS-1$
	
	public static final BooleanPref FOLDING_RESTORE_STATE_ENABLED_PREF = new BooleanPref(
			TEXEDITOR_NODE, "Folding.RestoreState.enabled"); //$NON-NLS-1$
	
	public static final BooleanPref MARKOCCURRENCES_ENABLED_PREF = new BooleanPref(
			TEXEDITOR_NODE, "MarkOccurrences.enabled"); //$NON-NLS-1$
	
	
	public static final String SMARTINSERT_GROUP_ID = "tex/tex.editor/smartinsert"; //$NON-NLS-1$
	
	public static final Preference<Boolean> SMARTINSERT_BYDEFAULT_ENABLED_PREF = new BooleanPref(
			TEXEDITOR_NODE, "SmartInsert.ByDefault.enabled"); //$NON-NLS-1$
	
	public static final Preference<TabAction> SMARTINSERT_TAB_ACTION_PREF = new EnumPref<TabAction>(
			TEXEDITOR_NODE, "SmartInsert.Tab.action", TabAction.class); //$NON-NLS-1$
	
	public static final Preference<Boolean> SMARTINSERT_CLOSEBRACKETS_ENABLED_PREF = new BooleanPref(
			TEXEDITOR_NODE, "SmartInsert.CloseBrackets.enabled"); //$NON-NLS-1$
	public static final Preference<Boolean> SMARTINSERT_CLOSEPARENTHESIS_ENABLED_PREF = new BooleanPref(
			TEXEDITOR_NODE, "SmartInsert.CloseParenthesis.enabled"); //$NON-NLS-1$
	public static final Preference<Boolean> SMARTINSERT_CLOSEMATHDOLLAR_ENABLED_PREF = new BooleanPref(
			TEXEDITOR_NODE, "SmartInsert.CloseMathDollar.enabled"); //$NON-NLS-1$
	
	public static final Preference<Boolean> SMARTINSERT_HARDWRAP_TEXT_ENABLED_PREF = new BooleanPref(
			TEXEDITOR_NODE, "SmartInsert.HardWrap.enabled"); //$NON-NLS-1$
	
}
