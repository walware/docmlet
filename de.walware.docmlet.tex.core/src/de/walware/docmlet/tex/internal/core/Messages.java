/*=============================================================================#
 # Copyright (c) 2012-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.core;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {
	
	
	public static String CommandCategory_Math_prefix;
	
	public static String CommandCategory_Sectioning_label;
	public static String CommandCategory_Label_label;
	public static String CommandCategory_TextStyling_label;
	public static String CommandCategory_MathStyling_label;
	public static String CommandCategory_CommonSymbols_label;
	public static String CommandCategory_TextSymbols_label;
	public static String CommandCategory_MathSymbols_GreekUpper_label;
	public static String CommandCategory_MathSymbols_GreekLower_label;
	public static String CommandCategory_MathSymbols_BinOp_label;
	public static String CommandCategory_MathSymbols_RootFracOp_label;
	public static String CommandCategory_MathSymbols_RelStd_label;
	public static String CommandCategory_MathSymbols_RelArrow_label;
	public static String CommandCategory_MathSymbols_RelMisc_label;
	public static String CommandCategory_MathSymbols_LargeOp_label;
	public static String CommandCategory_MathSymbols_NamedOp_label;
	public static String CommandCategory_MathSymbols_MiscAlpha_label;
	public static String CommandCategory_MathSymbols_MiscOrd_label;
	public static String CommandCategory_MathSymbols_Dots_label;
	public static String CommandCategory_MathSymbols_Accents_label;
	public static String CommandCategory_MathSymbols_Brackets_label;
	
	
	static {
		NLS.initializeMessages(Messages.class.getName(), Messages.class);
	}
	private Messages() {}
	
}
