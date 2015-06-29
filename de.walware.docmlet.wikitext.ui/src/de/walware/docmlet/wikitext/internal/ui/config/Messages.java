/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.ui.config;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {
	
	
	public static String MarkupConfig_title;
	public static String MarkupConfigs_label;
	
	public static String MarkupConfig_YamlMetadata_Enable_label;
	public static String MarkupConfig_TexMathDollars_Enable_label;
	public static String MarkupConfig_TexMathSBackslash_Enable_label;
	
	public static String MarkupConfig_NeedsBuild_title;
	public static String MarkupConfig_NeedsFullBuild_message;
	public static String MarkupConfig_NeedsProjectBuild_message;
	
	public static String Base_Editors_label;
	public static String Base_Editors_SeeAlso_info;
	
	public static String DocTemplates_title;
	
	public static String EditorOptions_SmartInsert_label;
	public static String EditorOptions_SmartInsert_AsDefault_label;
	public static String EditorOptions_SmartInsert_description;
	public static String EditorOptions_SmartInsert_TabAction_label;
	public static String EditorOptions_SmartInsert_CloseAuto_label;
	public static String EditorOptions_SmartInsert_CloseBrackets_label;
	public static String EditorOptions_SmartInsert_CloseParentheses_label;
	public static String EditorOptions_SmartInsert_CloseMathDollar_label;
	public static String EditorOptions_SmartInsert_HardWrapAuto_label;
	public static String EditorOptions_SmartInsert_HardWrapText_label;
	
	public static String EditorOptions_Folding_Enable_label;
	public static String EditorOptions_Folding_RestoreState_Enable_label;
	public static String EditorOptions_MarkOccurrences_Enable_label;
	public static String EditorOptions_ProblemChecking_Enable_label;
	public static String EditorOptions_AnnotationAppearance_info;
	
	
	static {
		NLS.initializeMessages(Messages.class.getName(), Messages.class);
	}
	private Messages() {}
	
}
