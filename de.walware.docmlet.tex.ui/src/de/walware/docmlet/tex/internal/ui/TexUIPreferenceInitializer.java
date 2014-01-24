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

package de.walware.docmlet.tex.internal.ui;

import static de.walware.ecommons.text.ui.presentation.ITextPresentationConstants.TEXTSTYLE_BOLD_SUFFIX;
import static de.walware.ecommons.text.ui.presentation.ITextPresentationConstants.TEXTSTYLE_COLOR_SUFFIX;
import static de.walware.ecommons.text.ui.presentation.ITextPresentationConstants.TEXTSTYLE_ITALIC_SUFFIX;
import static de.walware.ecommons.text.ui.presentation.ITextPresentationConstants.TEXTSTYLE_STRIKETHROUGH_SUFFIX;
import static de.walware.ecommons.text.ui.presentation.ITextPresentationConstants.TEXTSTYLE_UNDERLINE_SUFFIX;
import static de.walware.ecommons.text.ui.presentation.ITextPresentationConstants.TEXTSTYLE_USE_SUFFIX;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.editors.text.EditorsUI;

import de.walware.ecommons.ltk.ui.sourceediting.ISmartInsertSettings.TabAction;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssistComputerRegistry;
import de.walware.ecommons.preferences.PreferencesUtil;
import de.walware.ecommons.text.ui.settings.AssistPreferences;
import de.walware.ecommons.text.ui.settings.DecorationPreferences;

import de.walware.workbench.ui.IWaThemeConstants;
import de.walware.workbench.ui.util.ThemeUtil;

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
		final IScopeContext scope = DefaultScope.INSTANCE;
		final IEclipsePreferences pref = scope.getNode(TexUIPlugin.PLUGIN_ID);
		final ThemeUtil theme = new ThemeUtil();
		
		EditorsUI.useAnnotationsPreferencePage(store);
		EditorsUI.useQuickDiffPreferencePage(store);
		
		String color;
		pref.put(ITexTextStyles.TS_DEFAULT + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue(IWaThemeConstants.CODE_DEFAULT_COLOR));
		pref.putBoolean(ITexTextStyles.TS_DEFAULT + TEXTSTYLE_BOLD_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_DEFAULT + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_DEFAULT + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_DEFAULT + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		pref.put(ITexTextStyles.TS_CONTROL_WORD + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue(IWaThemeConstants.CODE_DOC_COMMAND_COLOR));
		pref.putBoolean(ITexTextStyles.TS_CONTROL_WORD + TEXTSTYLE_BOLD_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_CONTROL_WORD + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_CONTROL_WORD + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_CONTROL_WORD + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		pref.put(ITexTextStyles.TS_CONTROL_WORD_SUB_SECTIONING + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue("de.walware.docmlet.themes.SectionColor")); //$NON-NLS-1$
		pref.putBoolean(ITexTextStyles.TS_CONTROL_WORD_SUB_SECTIONING + TEXTSTYLE_BOLD_SUFFIX, true);
		pref.putBoolean(ITexTextStyles.TS_CONTROL_WORD_SUB_SECTIONING + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_CONTROL_WORD_SUB_SECTIONING + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_CONTROL_WORD_SUB_SECTIONING + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		pref.put(ITexTextStyles.TS_CONTROL_CHAR + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue(IWaThemeConstants.CODE_DOC_COMMAND_SPECIAL_COLOR));
		pref.putBoolean(ITexTextStyles.TS_CONTROL_CHAR + TEXTSTYLE_BOLD_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_CONTROL_CHAR + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_CONTROL_CHAR + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_CONTROL_CHAR + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		color = theme.getColorPrefValue(IWaThemeConstants.CODE_SUB_COLOR);
		pref.put(ITexTextStyles.TS_CURLY_BRACKETS + TEXTSTYLE_COLOR_SUFFIX, color);
		pref.putBoolean(ITexTextStyles.TS_CURLY_BRACKETS + TEXTSTYLE_BOLD_SUFFIX, true);
		pref.putBoolean(ITexTextStyles.TS_CURLY_BRACKETS + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_CURLY_BRACKETS + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_CURLY_BRACKETS + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
//		pref.put(ITexTextStyles.TS_SQUARED_BRACKETS + TEXTSTYLE_COLOR_SUFFIX, color);
//		pref.putBoolean(ITexTextStyles.TS_SQUARED_BRACKETS + TEXTSTYLE_BOLD_SUFFIX, false);
//		pref.putBoolean(ITexTextStyles.TS_SQUARED_BRACKETS + TEXTSTYLE_ITALIC_SUFFIX, false);
//		pref.putBoolean(ITexTextStyles.TS_SQUARED_BRACKETS + TEXTSTYLE_UNDERLINE_SUFFIX, false);
//		pref.putBoolean(ITexTextStyles.TS_SQUARED_BRACKETS + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
//		
		pref.put(ITexTextStyles.TS_MATH + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue(IWaThemeConstants.CODE_DOC_2ND_COLOR));
		pref.putBoolean(ITexTextStyles.TS_MATH + TEXTSTYLE_BOLD_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_MATH + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_MATH + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_MATH + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		pref.put(ITexTextStyles.TS_MATH_CONTROL_WORD + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue(IWaThemeConstants.CODE_DOC_2ND_COMMAND_COLOR));
		pref.putBoolean(ITexTextStyles.TS_MATH_CONTROL_WORD + TEXTSTYLE_BOLD_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_MATH_CONTROL_WORD + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_MATH_CONTROL_WORD + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_MATH_CONTROL_WORD + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		pref.put(ITexTextStyles.TS_MATH_CONTROL_CHAR + TEXTSTYLE_USE_SUFFIX, ITexTextStyles.TS_MATH_CONTROL_WORD);
		
		pref.put(ITexTextStyles.TS_MATH_CURLY_BRACKETS + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue(IWaThemeConstants.CODE_DOC_2ND_SUB_COLOR));
		pref.putBoolean(ITexTextStyles.TS_MATH_CURLY_BRACKETS + TEXTSTYLE_BOLD_SUFFIX, true);
		pref.putBoolean(ITexTextStyles.TS_MATH_CURLY_BRACKETS + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_MATH_CURLY_BRACKETS + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_MATH_CURLY_BRACKETS + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		pref.put(ITexTextStyles.TS_VERBATIM + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue(IWaThemeConstants.CODE_VERBATIM_COLOR));
		pref.putBoolean(ITexTextStyles.TS_VERBATIM + TEXTSTYLE_BOLD_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_VERBATIM + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_VERBATIM + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_VERBATIM + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		pref.put(ITexTextStyles.TS_COMMENT + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue(IWaThemeConstants.CODE_COMMENT_COLOR));
		pref.putBoolean(ITexTextStyles.TS_COMMENT + TEXTSTYLE_BOLD_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_COMMENT + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_COMMENT + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_COMMENT + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		pref.put(ITexTextStyles.TS_TASK_TAG + TEXTSTYLE_COLOR_SUFFIX, theme.getColorPrefValue(IWaThemeConstants.CODE_COMMENT_TASKTAG_COLOR));
		pref.putBoolean(ITexTextStyles.TS_TASK_TAG + TEXTSTYLE_BOLD_SUFFIX, true);
		pref.putBoolean(ITexTextStyles.TS_TASK_TAG + TEXTSTYLE_ITALIC_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_TASK_TAG + TEXTSTYLE_UNDERLINE_SUFFIX, false);
		pref.putBoolean(ITexTextStyles.TS_TASK_TAG + TEXTSTYLE_STRIKETHROUGH_SUFFIX, false);
		
		final IEclipsePreferences editorNode = scope.getNode(TexUIPlugin.TEX_EDITOR_QUALIFIER);
		editorNode.put(ContentAssistComputerRegistry.CIRCLING_ORDERED, "tex-elements:false,templates:true,paths:true"); //$NON-NLS-1$
		editorNode.put(ContentAssistComputerRegistry.DEFAULT_DISABLED, ""); //$NON-NLS-1$
		
		// EditorPreferences
		pref.putBoolean(DecorationPreferences.MATCHING_BRACKET_ENABLED_KEY, true);
		pref.put(DecorationPreferences.MATCHING_BRACKET_COLOR_KEY, theme.getColorPrefValue(IWaThemeConstants.MATCHING_BRACKET_COLOR));
		
		{	final AssistPreferences assistPrefs = TexUIPreferences.EDITING_ASSIST_PREFERENCES;
			PreferencesUtil.setPrefValue(scope, assistPrefs.getAutoActivationEnabledPref(), Boolean.TRUE);
			PreferencesUtil.setPrefValue(scope, assistPrefs.getAutoActivationDelayPref(), 200);
			PreferencesUtil.setPrefValue(scope, assistPrefs.getAutoInsertSinglePref(), Boolean.FALSE);
			PreferencesUtil.setPrefValue(scope, assistPrefs.getAutoInsertPrefixPref(), Boolean.FALSE);
		}
		PreferencesUtil.setPrefValue(scope, TexEditorOptions.FOLDING_ENABLED_PREF, Boolean.TRUE);
		PreferencesUtil.setPrefValue(scope, TexEditorOptions.FOLDING_RESTORE_STATE_ENABLED_PREF, Boolean.TRUE);
		PreferencesUtil.setPrefValue(scope, TexEditorOptions.MARKOCCURRENCES_ENABLED_PREF, Boolean.TRUE);
		
		PreferencesUtil.setPrefValue(scope, TexEditorOptions.SMARTINSERT_BYDEFAULT_ENABLED_PREF, Boolean.TRUE);
		PreferencesUtil.setPrefValue(scope, TexEditorOptions.SMARTINSERT_TAB_ACTION_PREF, TabAction.INSERT_INDENT_LEVEL);
		PreferencesUtil.setPrefValue(scope, TexEditorOptions.SMARTINSERT_CLOSEBRACKETS_ENABLED_PREF, Boolean.TRUE);
		PreferencesUtil.setPrefValue(scope, TexEditorOptions.SMARTINSERT_CLOSEPARENTHESIS_ENABLED_PREF, Boolean.TRUE);
		PreferencesUtil.setPrefValue(scope, TexEditorOptions.SMARTINSERT_CLOSEMATHDOLLAR_ENABLED_PREF, Boolean.TRUE);
		PreferencesUtil.setPrefValue(scope, TexEditorOptions.SMARTINSERT_HARDWRAP_TEXT_ENABLED_PREF, Boolean.TRUE);
		
		PreferencesUtil.setPrefValue(scope, LtxEditorBuild.PROBLEMCHECKING_ENABLED_PREF, Boolean.TRUE);
		
	}
	
}
