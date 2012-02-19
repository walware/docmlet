/*******************************************************************************
 * Copyright (c) 2009-2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.internal.ui;

import static de.walware.ecommons.text.ui.presentation.ITextPresentationConstants.TEXTSTYLE_BOLD_SUFFIX;
import static de.walware.ecommons.text.ui.presentation.ITextPresentationConstants.TEXTSTYLE_COLOR_SUFFIX;
import static de.walware.ecommons.text.ui.presentation.ITextPresentationConstants.TEXTSTYLE_ITALIC_SUFFIX;
import static de.walware.ecommons.text.ui.presentation.ITextPresentationConstants.TEXTSTYLE_STRIKETHROUGH_SUFFIX;
import static de.walware.ecommons.text.ui.presentation.ITextPresentationConstants.TEXTSTYLE_UNDERLINE_SUFFIX;
import static de.walware.ecommons.text.ui.presentation.ITextPresentationConstants.TEXTSTYLE_USE_SUFFIX;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.EditorsUI;

import de.walware.ecommons.ltk.ui.sourceediting.ISmartInsertSettings.TabAction;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssistComputerRegistry;
import de.walware.ecommons.preferences.PreferencesUtil;
import de.walware.ecommons.text.ui.settings.AssistPreferences;
import de.walware.ecommons.text.ui.settings.DecorationPreferences;

import de.walware.docmlet.tex.ui.TexUIPreferences;
import de.walware.docmlet.tex.ui.editors.LtxEditorBuild;
import de.walware.docmlet.tex.ui.editors.TexEditorOptions;
import de.walware.docmlet.tex.ui.text.ITexTextStyles;


public class TexUIPreferenceInitializer extends AbstractPreferenceInitializer {
	
	
	public TexUIPreferenceInitializer() {
	}
	
	
	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = TexUIPlugin.getDefault().getPreferenceStore();
		final DefaultScope defaultScope = new DefaultScope();
		
		EditorsUI.useAnnotationsPreferencePage(store);
		EditorsUI.useQuickDiffPreferencePage(store);
		
		store.setDefault(ITexTextStyles.TS_DEFAULT + TEXTSTYLE_COLOR_SUFFIX, "0,0,0"); //$NON-NLS-1$
		store.setDefault(ITexTextStyles.TS_DEFAULT + TEXTSTYLE_BOLD_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_DEFAULT + TEXTSTYLE_ITALIC_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_DEFAULT + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_DEFAULT + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		store.setDefault(ITexTextStyles.TS_CONTROL_WORD + TEXTSTYLE_COLOR_SUFFIX, "127,0,255"); //$NON-NLS-1$
		store.setDefault(ITexTextStyles.TS_CONTROL_WORD + TEXTSTYLE_BOLD_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_CONTROL_WORD + TEXTSTYLE_ITALIC_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_CONTROL_WORD + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_CONTROL_WORD + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		store.setDefault(ITexTextStyles.TS_CONTROL_WORD_SUB_SECTIONING + TEXTSTYLE_COLOR_SUFFIX, "127,0,255"); //$NON-NLS-1$
		store.setDefault(ITexTextStyles.TS_CONTROL_WORD_SUB_SECTIONING + TEXTSTYLE_BOLD_SUFFIX, true);
		store.setDefault(ITexTextStyles.TS_CONTROL_WORD_SUB_SECTIONING + TEXTSTYLE_ITALIC_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_CONTROL_WORD_SUB_SECTIONING + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_CONTROL_WORD_SUB_SECTIONING + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		store.setDefault(ITexTextStyles.TS_CONTROL_CHAR + TEXTSTYLE_COLOR_SUFFIX, "63,0,127"); //$NON-NLS-1$
		store.setDefault(ITexTextStyles.TS_CONTROL_CHAR + TEXTSTYLE_BOLD_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_CONTROL_CHAR + TEXTSTYLE_ITALIC_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_CONTROL_CHAR + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_CONTROL_CHAR + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		store.setDefault(ITexTextStyles.TS_CURLY_BRACKETS + TEXTSTYLE_COLOR_SUFFIX, "0,0,127"); //$NON-NLS-1$
		store.setDefault(ITexTextStyles.TS_CURLY_BRACKETS + TEXTSTYLE_BOLD_SUFFIX, true);
		store.setDefault(ITexTextStyles.TS_CURLY_BRACKETS + TEXTSTYLE_ITALIC_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_CURLY_BRACKETS + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_CURLY_BRACKETS + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
//		store.setDefault(ITexTextStyles.TS_SQUARED_BRACKETS + TEXTSTYLE_COLOR_SUFFIX, "0,0,127"); //$NON-NLS-1$
//		store.setDefault(ITexTextStyles.TS_SQUARED_BRACKETS + TEXTSTYLE_BOLD_SUFFIX, false);
//		store.setDefault(ITexTextStyles.TS_SQUARED_BRACKETS + TEXTSTYLE_ITALIC_SUFFIX, false);
//		store.setDefault(ITexTextStyles.TS_SQUARED_BRACKETS + TEXTSTYLE_UNDERLINE_SUFFIX, false);
//		store.setDefault(ITexTextStyles.TS_SQUARED_BRACKETS + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
//		
		store.setDefault(ITexTextStyles.TS_MATH + TEXTSTYLE_COLOR_SUFFIX, "175,95,95"); //$NON-NLS-1$
		store.setDefault(ITexTextStyles.TS_MATH + TEXTSTYLE_BOLD_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_MATH + TEXTSTYLE_ITALIC_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_MATH + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_MATH + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		store.setDefault(ITexTextStyles.TS_MATH_CONTROL_WORD + TEXTSTYLE_COLOR_SUFFIX, "159,63,127"); //$NON-NLS-1$
		store.setDefault(ITexTextStyles.TS_MATH_CONTROL_WORD + TEXTSTYLE_BOLD_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_MATH_CONTROL_WORD + TEXTSTYLE_ITALIC_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_MATH_CONTROL_WORD + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_MATH_CONTROL_WORD + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		store.setDefault(ITexTextStyles.TS_MATH_CONTROL_CHAR + TEXTSTYLE_USE_SUFFIX, ITexTextStyles.TS_MATH_CONTROL_WORD);
		
		store.setDefault(ITexTextStyles.TS_MATH_CURLY_BRACKETS + TEXTSTYLE_COLOR_SUFFIX, "159,95,127"); //$NON-NLS-1$
		store.setDefault(ITexTextStyles.TS_MATH_CURLY_BRACKETS + TEXTSTYLE_BOLD_SUFFIX, true);
		store.setDefault(ITexTextStyles.TS_MATH_CURLY_BRACKETS + TEXTSTYLE_ITALIC_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_MATH_CURLY_BRACKETS + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_MATH_CURLY_BRACKETS + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		store.setDefault(ITexTextStyles.TS_COMMENT + TEXTSTYLE_COLOR_SUFFIX, "63,127,79"); //$NON-NLS-1$
		store.setDefault(ITexTextStyles.TS_COMMENT + TEXTSTYLE_BOLD_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_COMMENT + TEXTSTYLE_ITALIC_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_COMMENT + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_COMMENT + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		store.setDefault(ITexTextStyles.TS_TASK_TAG + TEXTSTYLE_COLOR_SUFFIX, "63,127,95"); //$NON-NLS-1$
		store.setDefault(ITexTextStyles.TS_TASK_TAG + TEXTSTYLE_BOLD_SUFFIX, true);
		store.setDefault(ITexTextStyles.TS_TASK_TAG + TEXTSTYLE_ITALIC_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_TASK_TAG + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_TASK_TAG + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		store.setDefault(ITexTextStyles.TS_VERBATIM + TEXTSTYLE_COLOR_SUFFIX, "79,79,79"); //$NON-NLS-1$
		store.setDefault(ITexTextStyles.TS_VERBATIM + TEXTSTYLE_BOLD_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_VERBATIM + TEXTSTYLE_ITALIC_SUFFIX, true);
		store.setDefault(ITexTextStyles.TS_VERBATIM + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		store.setDefault(ITexTextStyles.TS_VERBATIM + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		defaultScope.getNode(TexUIPlugin.TEX_EDITOR_QUALIFIER).put(ContentAssistComputerRegistry.CIRCLING_ORDERED, "tex-elements:false,templates:true,paths:true"); //$NON-NLS-1$
		defaultScope.getNode(TexUIPlugin.TEX_EDITOR_QUALIFIER).put(ContentAssistComputerRegistry.DEFAULT_DISABLED, ""); //$NON-NLS-1$
		
		// EditorPreferences
		final DecorationPreferences decoPrefs = TexUIPreferences.EDITING_DECO_PREFERENCES;
		PreferencesUtil.setPrefValue(defaultScope, decoPrefs.getMatchingBracketsEnabled(), Boolean.TRUE);
		PreferencesUtil.setPrefValue(defaultScope, decoPrefs.getMatchingBracketsColor(), new RGB(192, 192, 192));
		
		final AssistPreferences assistPrefs = TexUIPreferences.EDITING_ASSIST_PREFERENCES;
		PreferencesUtil.setPrefValue(defaultScope, assistPrefs.getAutoActivationEnabledPref(), Boolean.TRUE);
		PreferencesUtil.setPrefValue(defaultScope, assistPrefs.getAutoActivationDelayPref(), 200);
		PreferencesUtil.setPrefValue(defaultScope, assistPrefs.getAutoInsertSinglePref(), Boolean.FALSE);
		PreferencesUtil.setPrefValue(defaultScope, assistPrefs.getAutoInsertPrefixPref(), Boolean.FALSE);
		PreferencesUtil.setPrefValue(defaultScope, assistPrefs.getProposalsBackgroundPref(), new RGB(243, 247, 255));
		PreferencesUtil.setPrefValue(defaultScope, assistPrefs.getProposalsForegroundPref(), new RGB(0, 0, 0));
		PreferencesUtil.setPrefValue(defaultScope, assistPrefs.getInformationBackgroundPref(), new RGB(255, 255, 255));
		PreferencesUtil.setPrefValue(defaultScope, assistPrefs.getInformationForegroundPref(), new RGB(0, 0, 0));
		
		PreferencesUtil.setPrefValue(defaultScope, TexEditorOptions.FOLDING_ENABLED_PREF, Boolean.TRUE); 
		PreferencesUtil.setPrefValue(defaultScope, TexEditorOptions.MARKOCCURRENCES_ENABLED_PREF, Boolean.TRUE);
		
		PreferencesUtil.setPrefValue(defaultScope, TexEditorOptions.SMARTINSERT_BYDEFAULT_ENABLED_PREF, Boolean.TRUE);
		PreferencesUtil.setPrefValue(defaultScope, TexEditorOptions.SMARTINSERT_TAB_ACTION_PREF, TabAction.INSERT_INDENT_LEVEL);
		PreferencesUtil.setPrefValue(defaultScope, TexEditorOptions.SMARTINSERT_CLOSEBRACKETS_ENABLED_PREF, Boolean.TRUE);
		PreferencesUtil.setPrefValue(defaultScope, TexEditorOptions.SMARTINSERT_CLOSEPARENTHESIS_ENABLED_PREF, Boolean.TRUE);
		PreferencesUtil.setPrefValue(defaultScope, TexEditorOptions.SMARTINSERT_CLOSEMATHDOLLAR_ENABLED_PREF, Boolean.TRUE);
		PreferencesUtil.setPrefValue(defaultScope, TexEditorOptions.SMARTINSERT_HARDWRAP_TEXT_ENABLED_PREF, Boolean.TRUE);
		
		PreferencesUtil.setPrefValue(defaultScope, LtxEditorBuild.PROBLEMCHECKING_ENABLED_PREF, Boolean.TRUE);
	}
	
}
