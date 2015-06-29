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

package de.walware.docmlet.tex.internal.ui.config;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {
	
	
	public static String Base_Editors_label;
	public static String Base_Editors_SeeAlso_info;
	public static String Base_Tools_label;
	public static String Base_Tools_SeeAlso_info;
	
	public static String DocTemplates_title;
	
	public static String TextStyles_DefaultCodeCategory_label;
	public static String TextStyles_DefaultCodeCategory_short;
	public static String TextStyles_Default_label;
	public static String TextStyles_Default_description;
	public static String TextStyles_ControlWord_label;
	public static String TextStyles_ControlWord_description;
	public static String TextStyles_ControlWord_Sectioning_label;
	public static String TextStyles_ControlWord_Sectioning_description;
	public static String TextStyles_ControlChar_label;
	public static String TextStyles_ControlChar_description;
	public static String TextStyles_CurlyBracket_label;
	public static String TextStyles_CurlyBracket_description;
	public static String TextStyles_MathCodeCategory_label;
	public static String TextStyles_MathCodeCategory_short;
	public static String TextStyles_Equation_label;
	public static String TextStyles_Equation_description;
	public static String TextStyles_Verbatim_label;
	public static String TextStyles_Verbatim_description;
	
	public static String TextStyles_CommentCategory_label;
	public static String TextStyles_Comment_label;
	public static String TextStyles_Comment_description;
	public static String TextStyles_TaskTag_label;
	public static String TextStyles_TaskTag_description;
	
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
	
	public static String CodeStyle_Indent_IndentInBlocks_label;
	public static String CodeStyle_Indent_IndentInBlocks_error_message;
	public static String CodeStyle_Indent_IndentInEnvs_label;
	public static String CodeStyle_Indent_IndentInEnvs_error_message;
	
	
	static {
		NLS.initializeMessages(Messages.class.getName(), Messages.class);
	}
	private Messages() {}
	
}
