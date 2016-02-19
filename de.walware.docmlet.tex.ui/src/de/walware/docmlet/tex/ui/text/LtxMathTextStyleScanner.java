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

import java.util.Map;

import org.eclipse.jface.text.rules.IToken;

import de.walware.jcommons.string.CharArrayString;

import de.walware.ecommons.collections.IntArrayMap;
import de.walware.ecommons.text.ui.settings.TextStyleManager;

import de.walware.docmlet.tex.core.parser.LtxLexer;


public class LtxMathTextStyleScanner extends LtxDefaultTextStyleScanner {
	
	
	public LtxMathTextStyleScanner(final TextStyleManager textStyles) {
		super(textStyles);
	}
	
	
	@Override
	protected void registerTokens(final IntArrayMap<IToken> map) {
		map.put(LtxLexer.DEFAULT_TEXT, getToken(ITexTextStyles.TS_MATH));
		map.put(LtxLexer.CONTROL_WORD, getToken(ITexTextStyles.TS_MATH_CONTROL_WORD));
		map.put(LtxLexer.CONTROL_CHAR, getToken(ITexTextStyles.TS_MATH_CONTROL_CHAR));
		map.put(LtxLexer.CONTROL_NONE, getToken(ITexTextStyles.TS_MATH_CONTROL_WORD));
		map.put(LtxLexer.CURLY_BRACKET_OPEN, getToken(ITexTextStyles.TS_MATH_CURLY_BRACKETS));
		map.put(LtxLexer.CURLY_BRACKET_CLOSE, getToken(ITexTextStyles.TS_MATH_CURLY_BRACKETS));
		map.put(LtxLexer.SQUARED_BRACKET_OPEN, getToken(ITexTextStyles.TS_MATH_CURLY_BRACKETS));
		map.put(LtxLexer.SQUARED_BRACKET_CLOSE, getToken(ITexTextStyles.TS_MATH_CURLY_BRACKETS));
		
		map.put(LtxLexer.LINE_COMMENT, getToken(ITexTextStyles.TS_COMMENT));
	}
	
	
	@Override
	protected void updateWords(final Map<CharArrayString, IToken> map) {
	}
	
}
