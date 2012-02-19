/*******************************************************************************
 * Copyright (c) 2012 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.internal.core.model;

import org.eclipse.osgi.util.NLS;


public class ProblemMessages extends NLS {
	
	
	public static String Ast_CurlyBracket_NotClosed_message;
	public static String Ast_CurlyBracket_NotOpened_message;
	public static String Ast_SquareBracket_NotClosed_message;
	public static String Ast_SquareBracket_NotOpened_message;
	public static String Ast_OptArgument_NotClosed_Opt_message;
	public static String Ast_ReqArgument_NotClosed_message;
	
	public static String Ast_Env_MissingName_Begin_message;
	public static String Ast_Env_MissingName_End_message;
	public static String Ast_Env_NotClosed_message;
	public static String Ast_Env_NotOpened_message;
	public static String Ast_Math_NotClosed_message;
	public static String Ast_Verbatim_MissingSep_message;
	public static String Ast_Verbatim_NotClosed_message;
	
	public static String Labels_UndefinedRef_message;
	
	
	static {
		NLS.initializeMessages(ProblemMessages.class.getName(), ProblemMessages.class);
	}
	private ProblemMessages() {}
	
}
