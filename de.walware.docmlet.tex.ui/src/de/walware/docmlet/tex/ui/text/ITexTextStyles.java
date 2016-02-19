/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.ui.text;

import de.walware.docmlet.tex.ui.TexUI;


public class ITexTextStyles {
	
	
	public static final String LTX_TEXTSTYLE_CONFIG_QUALIFIER= TexUI.PLUGIN_ID + "/textstyle/Ltx"; //$NON-NLS-1$
	
	
	public static final String PREFIX = "tex_ts_"; //$NON-NLS-1$
	
	public static final String TS_DEFAULT = PREFIX + "Default"; //$NON-NLS-1$
	
	public static final String TS_CONTROL_WORD = PREFIX + "ControlWord"; //$NON-NLS-1$
	public static final String TS_CONTROL_WORD_SUB_SECTIONING = TS_CONTROL_WORD + ".Sectioning"; //$NON-NLS-1$
	public static final String TS_CONTROL_CHAR = PREFIX + "ControlChar"; //$NON-NLS-1$
	public static final String TS_CURLY_BRACKETS = PREFIX + "CurlyBracket"; //$NON-NLS-1$
//	public static final String TS_SQUARED_BRACKETS = PREFIX + "SquaredBracket"; //$NON-NLS-1$
	
	public static final String TS_MATH = PREFIX + "Math"; //$NON-NLS-1$
	public static final String TS_MATH_CONTROL_WORD = PREFIX + "MathControlWord"; //$NON-NLS-1$
	public static final String TS_MATH_CONTROL_CHAR = PREFIX + "MathControlChar"; //$NON-NLS-1$
	public static final String TS_MATH_CURLY_BRACKETS = PREFIX + "MathCurlyBracket"; //$NON-NLS-1$
//	public static final String TS_MATH_SQUARED_BRACKETS = PREFIX + "MathSquaredBracket"; //$NON-NLS-1$
	
	public static final String TS_VERBATIM  = PREFIX + "Verbatim"; //$NON-NLS-1$
	public static final String TS_COMMENT  = PREFIX + "Comment"; //$NON-NLS-1$
	public static final String TS_TASK_TAG  = PREFIX + "TaskTag"; //$NON-NLS-1$
	
	public static final String TS_NUMBER = PREFIX + "Number"; //$NON-NLS-1$
	public static final String TS_STRING = PREFIX + "String"; //$NON-NLS-1$
	
}
