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

package de.walware.docmlet.tex.ui.text;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import de.walware.ecommons.collections.IntArrayMap;
import de.walware.ecommons.text.BufferedDocumentParseInput;
import de.walware.ecommons.text.CharArrayString;
import de.walware.ecommons.text.ui.presentation.ITextPresentationConstants;
import de.walware.ecommons.text.ui.settings.TextStyleManager;
import de.walware.ecommons.ui.ColorManager;
import de.walware.ecommons.ui.ISettingsChangedHandler;

import de.walware.docmlet.tex.core.parser.LtxLexer;


public class LtxDefaultTextStyleScanner extends BufferedDocumentParseInput
		implements ITokenScanner, ISettingsChangedHandler {
	
	
	protected static void putAll(final Map<String, IToken> map, final String[] symbols, final IToken token) {
		for (int i = 0; i < symbols.length; i++) {
			map.put(symbols[i], token);
		}
	}
	
	
	private final LtxLexer fLexer;
	
	private final IToken[] fTokens;
	private final TextStyleManager fTextStyles;
	private final IToken fDefaultToken;
	private IToken fNextToken;
	private final Map<CharArrayString, IToken> fSpecialWords;
	
	private int fCurrentOffset;
	private int fCurrentLength;
	
	
	public LtxDefaultTextStyleScanner(final ColorManager colorManager, final IPreferenceStore preferenceStore) {
		fLexer = createLexer();
		fLexer.setReportAsterisk(false);
		fTextStyles = new TextStyleManager(colorManager, preferenceStore, ITexTextStyles.GROUP_ID);
		
		final IntArrayMap<IToken> tokens = new IntArrayMap<IToken>();
		registerTokens(tokens);
		fDefaultToken = tokens.get(LtxLexer.DEFAULT_TEXT);
		fTokens = tokens.toArray(IToken.class);
		fSpecialWords = new HashMap<CharArrayString, IToken>();
		updateWords(fSpecialWords);
	}
	
	
	protected LtxLexer createLexer() {
		return new LtxLexer(this);
	}
	
	protected IToken getToken(final String key) {
		return fTextStyles.getToken(key);
	}
	
	
	@Override
	public void setRange(final IDocument document, final int offset, final int length) {
		setDocument(document);
		fCurrentOffset = offset;
		fCurrentLength = 0;
		fLexer.setRange(offset, length);
	}
	
	@Override
	public IToken nextToken() {
		fCurrentOffset += fCurrentLength;
		IToken token = fNextToken;
		if (token != null) {
			fNextToken = null;
		}
		else {
			do {
				token = getTokenFromScannerToken(fLexer.next());
			} while (token == fDefaultToken);
		}
		fCurrentLength = fLexer.getOffset()-fCurrentOffset;
		if (fCurrentLength != 0) {
			fNextToken = token;
			return fDefaultToken;
		}
		fCurrentLength = fLexer.getLength();
		return token;
	}
	
	protected IToken getTokenFromScannerToken(final int lexerToken) {
		IToken token;
		switch (lexerToken) {
		case LtxLexer.EOF:
			return Token.EOF;
		case LtxLexer.CONTROL_WORD:
			substring(2, fLexer.getLength()-1, fTmpCharString);
			if (fTmpCharString.length() > 0) {
				token = fSpecialWords.get(fTmpCharString);
				if (token != null) {
					return token;
				}
			}
			return fTokens[LtxLexer.CONTROL_WORD];
		default:
			token = fTokens[lexerToken];
			if (token != null) {
				return token;
			}
			return fDefaultToken;
		}
	}
	
	@Override
	public int getTokenOffset() {
		return fCurrentOffset;
	}
	
	@Override
	public int getTokenLength() {
		return fCurrentLength;
	}
	
	
	@Override
	public void handleSettingsChanged(final Set<String> groupIds, final Map<String, Object> options) {
		fTextStyles.handleSettingsChanged(groupIds, options);
		if (groupIds.contains(ITexTextStyles.GROUP_ID)) {
			options.put(ITextPresentationConstants.SETTINGSCHANGE_AFFECTSPRESENTATION_KEY, Boolean.TRUE);
		}
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
