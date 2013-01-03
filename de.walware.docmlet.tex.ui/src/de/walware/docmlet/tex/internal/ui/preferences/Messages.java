/*******************************************************************************
 * Copyright (c) 2009-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.internal.ui.preferences;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {
	
	
	public static String TexTextStyles_DefaultCodeCategory_label;
	public static String TexTextStyles_DefaultCodeCategory_short;
	public static String TexTextStyles_Default_label;
	public static String TexTextStyles_Default_description;
	public static String TexTextStyles_ControlWord_label;
	public static String TexTextStyles_ControlWord_description;
	public static String TexTextStyles_ControlWord_Sectioning_label;
	public static String TexTextStyles_ControlWord_Sectioning_description;
	public static String TexTextStyles_ControlChar_label;
	public static String TexTextStyles_ControlChar_description;
	public static String TexTextStyles_CurlyBracket_label;
	public static String TexTextStyles_CurlyBracket_description;
	public static String TexTextStyles_MathCodeCategory_label;
	public static String TexTextStyles_MathCodeCategory_short;
	public static String TexTextStyles_Equation_label;
	public static String TexTextStyles_Equation_description;
	public static String TexTextStyles_Verbatim_label;
	public static String TexTextStyles_Verbatim_description;
	
	public static String TexTextStyles_CommentCategory_label;
	public static String TexTextStyles_Comment_label;
	public static String TexTextStyles_Comment_description;
	public static String TexTextStyles_TaskTag_label;
	public static String TexTextStyles_TaskTag_description;
	
	public static String TexEditorTemplates_title;
	
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
