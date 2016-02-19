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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import de.walware.jcommons.string.CharArrayString;

import de.walware.ecommons.collections.IntArrayMap;
import de.walware.ecommons.text.core.input.DocumentParserInput;
import de.walware.ecommons.text.ui.settings.TextStyleManager;

import de.walware.docmlet.tex.core.parser.LtxLexer;


public class LtxDefaultTextStyleScanner extends DocumentParserInput implements ITokenScanner {
	
	
	protected static void putAll(final Map<String, IToken> map, final String[] symbols, final IToken token) {
		for (int i = 0; i < symbols.length; i++) {
			map.put(symbols[i], token);
		}
	}
	
	
	private final LtxLexer lexer;
	
	private final IToken[] tokens;
	private final TextStyleManager textStyles;
	private final IToken defaultToken;
	private IToken nextToken;
	private final Map<CharArrayString, IToken> specialWords;
	
	private int currentOffset;
	private int currentLength;
	
	
	public LtxDefaultTextStyleScanner(final TextStyleManager textStyles) {
		this.lexer= createLexer();
		this.lexer.setReportAsterisk(false);
		this.textStyles= textStyles;
		
		final IntArrayMap<IToken> tokens= new IntArrayMap<>();
		registerTokens(tokens);
		this.defaultToken= tokens.get(LtxLexer.DEFAULT_TEXT);
		this.tokens= tokens.toArray(IToken.class);
		this.specialWords= new HashMap<>();
		updateWords(this.specialWords);
	}
	
	
	protected LtxLexer createLexer() {
		return new LtxLexer();
	}
	
	protected IToken getToken(final String key) {
		return this.textStyles.getToken(key);
	}
	
	
	@Override
	public void setRange(final IDocument document, final int offset, final int length) {
		reset(document);
		init(offset, offset + length);
		this.lexer.reset(this);
		
		this.currentOffset= offset;
		this.currentLength= 0;
	}
	
	@Override
	public IToken nextToken() {
		this.currentOffset += this.currentLength;
		IToken token = this.nextToken;
		if (token != null) {
			this.nextToken = null;
		}
		else {
			do {
				token = getTokenFromScannerToken(this.lexer.next());
			} while (token == this.defaultToken);
		}
		this.currentLength = this.lexer.getOffset() - this.currentOffset;
		if (this.currentLength != 0) {
			this.nextToken = token;
			return this.defaultToken;
		}
		this.currentLength = this.lexer.getLength();
		return token;
	}
	
	protected IToken getTokenFromScannerToken(final int lexerToken) {
		IToken token;
		switch (lexerToken) {
		case LtxLexer.EOF:
			return Token.EOF;
		case LtxLexer.CONTROL_WORD:
			final CharArrayString label= getTmpString(1, this.lexer.getLength() - 1);
			if (label.length() > 0) {
				token = this.specialWords.get(label);
				if (token != null) {
					return token;
				}
			}
			return this.tokens[LtxLexer.CONTROL_WORD];
		default:
			token = this.tokens[lexerToken];
			if (token != null) {
				return token;
			}
			return this.defaultToken;
		}
	}
	
	@Override
	public int getTokenOffset() {
		return this.currentOffset;
	}
	
	@Override
	public int getTokenLength() {
		return this.currentLength;
	}
	
	
	protected void registerTokens(final IntArrayMap<IToken> map) {
		map.put(LtxLexer.DEFAULT_TEXT, getToken(ITexTextStyles.TS_DEFAULT));
		map.put(LtxLexer.CONTROL_WORD, getToken(ITexTextStyles.TS_CONTROL_WORD));
		map.put(LtxLexer.CONTROL_CHAR, getToken(ITexTextStyles.TS_CONTROL_CHAR));
		map.put(LtxLexer.CONTROL_NONE, getToken(ITexTextStyles.TS_CONTROL_WORD));
		map.put(LtxLexer.CURLY_BRACKET_OPEN, getToken(ITexTextStyles.TS_CURLY_BRACKETS));
		map.put(LtxLexer.CURLY_BRACKET_CLOSE, getToken(ITexTextStyles.TS_CURLY_BRACKETS));
		map.put(LtxLexer.SQUARED_BRACKET_OPEN, getToken(ITexTextStyles.TS_CURLY_BRACKETS));
		map.put(LtxLexer.SQUARED_BRACKET_CLOSE, getToken(ITexTextStyles.TS_CURLY_BRACKETS));
		
		map.put(LtxLexer.LINE_COMMENT, getToken(ITexTextStyles.TS_COMMENT));
		map.put(LtxLexer.VERBATIM_TEXT, getToken(ITexTextStyles.TS_VERBATIM));
	}
	
	
	private static final CharArrayString[] SECTION_WORDS = new CharArrayString[] {
		new CharArrayString("part"), //$NON-NLS-1$
		new CharArrayString("chapter"), //$NON-NLS-1$
		new CharArrayString("section"), //$NON-NLS-1$
		new CharArrayString("subsection"), //$NON-NLS-1$
		new CharArrayString("subsubsection"), //$NON-NLS-1$
	};
	
	protected void updateWords(final Map<CharArrayString, IToken> map) {
		final IToken sectioningToken = getToken(ITexTextStyles.TS_CONTROL_WORD_SUB_SECTIONING);
		for (int i = 0; i < SECTION_WORDS.length; i++) {
			map.put(SECTION_WORDS[i], sectioningToken);
		}
	}
	
}
