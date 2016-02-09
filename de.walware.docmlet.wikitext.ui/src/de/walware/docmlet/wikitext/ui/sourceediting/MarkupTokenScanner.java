/*******************************************************************************
 * Copyright (c) 2007-2015 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial implementation in Mylyn
 *     Stephan Wahlbrink - API and implementation for DocMLET
 *******************************************************************************/
package de.walware.docmlet.wikitext.ui.sourceediting;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.mylyn.internal.wikitext.core.util.css.CssParser;
import org.eclipse.mylyn.internal.wikitext.core.util.css.CssRule;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.internal.wikitext.ui.editor.preferences.Preferences;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.FontState;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.Locator;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.RGB;

import de.walware.ecommons.ltk.core.SourceContent;
import de.walware.ecommons.preferences.PreferencesUtil;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNode;
import de.walware.ecommons.text.core.treepartitioner.TreePartitionUtil;

import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.markup.MarkupParser2;
import de.walware.docmlet.wikitext.core.source.EmbeddingAttributes;
import de.walware.docmlet.wikitext.core.source.MarkupLanguageDocumentSetupParticipant;
import de.walware.docmlet.wikitext.core.source.extdoc.IExtdocMarkupLanguage;
import de.walware.docmlet.wikitext.internal.ui.sourceediting.EmbeddedHtml;
import de.walware.docmlet.wikitext.internal.ui.sourceediting.MarkupCssStyleManager;


@SuppressWarnings("restriction")
public class MarkupTokenScanner implements ITokenScanner {
	
	
	private static class PositionToken extends Token {
		
		private final int offset;
		
		private final int length;
		
		
		public PositionToken(final TextAttribute attribute, final int offset, final int length) {
			super(attribute);
			this.offset= offset;
			this.length= length;
		}
		
		public int getOffset() {
			return this.offset;
		}
		
		public int getLength() {
			return this.length;
		}
		
		
		@Override
		public String toString() {
			return "Token [offset=" + this.offset + ", length=" + this.length + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
		}
		
	}
	
	private static class BreakException extends RuntimeException {
		
		private static final long serialVersionUID= 1L;
		
		
		public BreakException() {
			super("BreakScan", null, true, false);
		}
		
	}
	
	private class Builder extends DocumentBuilder {
		
		
		private int beginOffset;
		private int endOffset;
		
		private int scanRestartOffset;
		
		private int scanCurrentOffset;
		
		private final ArrayDeque<FontState> scanFontStateStack= new ArrayDeque<>();
		
		private FontState nestedCodeFontState;
		
		
		public Builder() {
		}
		
		
		private void clearBuilder() {
			this.scanFontStateStack.clear();
			this.nestedCodeFontState= null;
		}
		
		public void scan(final IDocument document, final int offset, final int length)
				throws BadLocationException, BreakException {
			this.beginOffset= offset;
			this.endOffset= offset + length;
			
			this.scanRestartOffset= this.beginOffset;
			final ITreePartitionNode rootNode= TreePartitionUtil.getRootNode(document, MarkupTokenScanner.this.partitioning);
			ITreePartitionNode node= TreePartitionUtil.getNode(document, MarkupTokenScanner.this.partitioning, offset, false);
			if (node != rootNode) {
				while (node.getParent() != rootNode) {
					node= node.getParent();
				}
				this.scanRestartOffset= node.getOffset();
			}
			
			final IMarkupLanguage markupLanguage= MarkupLanguageDocumentSetupParticipant.getMarkupLanguage(document, MarkupTokenScanner.this.partitioning);
			
			final MarkupParser2 markupParser= new MarkupParser2(markupLanguage, this);
			markupParser.disable(MarkupParser2.GENERATIVE_CONTENT);
			markupParser.enable(MarkupParser2.SOURCE_STRUCT);
			markupParser.enable(MarkupParser2.INLINE_ALL);
			
			final SourceContent content= new SourceContent(0,
					document.get(this.scanRestartOffset, Math.min(this.endOffset + 100, document.getLength()) - this.scanRestartOffset),
					this.scanRestartOffset );
			markupParser.parse(content, true);
		}
		
		private void updateOffset() {
			updateOffset(getLocator().getDocumentOffset());
		}
		
		private void updateOffset(final int locatorOffset) {
			final int offset= this.scanRestartOffset + locatorOffset;
			if (offset > this.scanCurrentOffset) {
				addToken(this.scanFontStateStack.getLast(),
						this.scanCurrentOffset, Math.min(this.endOffset, offset) );
				this.scanCurrentOffset= offset;
			}
			
			if (offset >= this.endOffset) {
				throw new BreakException();
			}
		}
		
		
		@Override
		public void beginDocument() {
			this.scanCurrentOffset= this.beginOffset;
			this.scanFontStateStack.addLast(MarkupTokenScanner.this.styleManager.createDefaultFontState());
		}
		
		@Override
		public void endDocument() {
			updateOffset();
		}
		
		@Override
		public void beginBlock(final BlockType type, final Attributes attributes) {
			final FontState fontState;
			if (type == BlockType.CODE && attributes instanceof EmbeddingAttributes
					&& ((EmbeddingAttributes) attributes).getForeignType() == IExtdocMarkupLanguage.EMBEDDED_HTML) {
				fontState= createHtmlFontState(
						this.scanFontStateStack.getLast(),
						(EmbeddingAttributes) attributes );
			}
			else {
				fontState= createFontState(
						this.scanFontStateStack.getLast(),
						getPrefCssStyles(type),
						(attributes != null) ? attributes.getCssStyle() : null );
			}
			
			if (type == BlockType.CODE && this.scanFontStateStack.size() > 1) {
				this.nestedCodeFontState= fontState;
				return;
			}
			
			updateOffset();
			this.scanFontStateStack.addLast(fontState);
		}
		
		@Override
		public void endBlock() {
			if (this.nestedCodeFontState != null) {
				this.nestedCodeFontState= null;
				return;
			}
			
			updateOffset();
			this.scanFontStateStack.removeLast();
		}
		
		@Override
		public void beginSpan(final SpanType type, final Attributes attributes) {
			final FontState fontState;
			if (type == SpanType.CODE && attributes instanceof EmbeddingAttributes
					&& ((EmbeddingAttributes) attributes).getForeignType() == IExtdocMarkupLanguage.EMBEDDED_HTML) {
				fontState= createHtmlFontState(
						this.scanFontStateStack.getLast(),
						(EmbeddingAttributes) attributes );
			}
			else {
				fontState= createFontState(
						this.scanFontStateStack.getLast(),
						getPrefCssStyles(type),
						(attributes != null) ? attributes.getCssStyle() : null );
			}
			
			updateOffset();
			this.scanFontStateStack.addLast(fontState);
		}
		
		@Override
		public void endSpan() {
			updateOffset();
			this.scanFontStateStack.removeLast();
		}
		
		@Override
		public void beginHeading(final int level, final Attributes attributes) {
			final FontState fontState= createFontState(
					this.scanFontStateStack.getLast(),
					getPrefCssStyles(level),
					(attributes != null) ? attributes.getCssStyle() : null );
			
			updateOffset();
			this.scanFontStateStack.addLast(fontState);
		}
		
		@Override
		public void endHeading() {
			updateOffset();
			this.scanFontStateStack.removeLast();
		}
		
		@Override
		public void characters(final String text) {
			if (this.nestedCodeFontState != null) {
				updateOffset();
				this.scanFontStateStack.addLast(this.nestedCodeFontState);
				final Locator locator= getLocator();
				updateOffset(locator.getLineDocumentOffset() + locator.getLineLength());
				this.scanFontStateStack.removeLast();
			}
		}
		
		@Override
		public void charactersUnescaped(final String literal) {
			characters(literal);
		}
		
		@Override
		public void entityReference(final String entity) {
			characters(entity);
		}
		
		@Override
		public void image(final Attributes attributes, final String url) {
		}
		
		@Override
		public void link(final Attributes attributes, final String hrefOrHashName, final String text) {
		}
		
		@Override
		public void imageLink(final Attributes linkAttributes, final Attributes imageAttributes, final String href,
				final String imageUrl) {
		}
		
		@Override
		public void acronym(final String text, final String definition) {
		}
		
		@Override
		public void lineBreak() {
		}
		
	}
	
	
	private final String partitioning;
	
	private final List<PositionToken> tokens= new ArrayList<>();
	
	private Iterator<PositionToken> tokenIter= null;
	
	private PositionToken currentToken= null;
	
	
	private MarkupCssStyleManager styleManager;
	
	private Preferences preferences;
	
	private final CssParser cssParser= new CssParser();
	
	private final Builder builder= new Builder();
	
	
	public MarkupTokenScanner(final String partitioning, final StyleConfig config) {
		this.partitioning= partitioning;
		setStyleConfig(config);
		reloadPreferences();
	}
	
	
	/**
	 * Sets the fonts used by this token scanner.
	 */
	public void setStyleConfig(final StyleConfig config) {
		this.styleManager= new MarkupCssStyleManager(config);
	}
	
	public void reloadPreferences() {
		this.preferences= WikiTextUiPlugin.getDefault().getPreferences();
	}
	
	@Override
	public IToken nextToken() {
		if (this.tokenIter != null && this.tokenIter.hasNext()) {
			this.currentToken= this.tokenIter.next();
		}
		else {
			this.currentToken= null;
			this.tokenIter= null;
			return Token.EOF;
		}
		return this.currentToken;
	}
	
	@Override
	public int getTokenOffset() {
		return this.currentToken.getOffset();
	}
	
	@Override
	public int getTokenLength() {
		return this.currentToken.getLength();
	}
	
	@Override
	public void setRange(final IDocument document, final int offset, final int length) {
		this.tokens.clear();
		this.tokenIter= null;
		this.currentToken= null;
		
		try {
			this.builder.scan(document, offset, length);
		}
		catch (final BreakException e) {}
		catch (final BadLocationException e) {
			throw new RuntimeException(e);
		}
		finally {
			this.builder.clearBuilder();
		}
		
		this.tokenIter= this.tokens.iterator();
	}
	
	protected TextAttribute createTextAttribute(final StyleRange styleRange) {
		int fontStyle= styleRange.fontStyle;
		if (styleRange.strikeout) {
			fontStyle |= TextAttribute.STRIKETHROUGH;
		}
		if (styleRange.underline) {
			fontStyle |= TextAttribute.UNDERLINE;
		}
		return new TextAttribute(styleRange.foreground, styleRange.background, fontStyle, styleRange.font);
	}
	
	protected final String getPrefCssStyles(final SpanType spanType) {
		final String key;
		switch (spanType) {
		case BOLD:
			key= Preferences.PHRASE_BOLD;
			break;
		case CITATION:
			key= Preferences.PHRASE_CITATION;
			break;
		case CODE:
			key= Preferences.PHRASE_CODE;
			break;
		case DELETED:
			key= Preferences.PHRASE_DELETED_TEXT;
			break;
		case EMPHASIS:
			key= Preferences.PHRASE_EMPHASIS;
			break;
		case INSERTED:
			key= Preferences.PHRASE_INSERTED_TEXT;
			break;
		case ITALIC:
			key= Preferences.PHRASE_ITALIC;
			break;
		case MONOSPACE:
			key= Preferences.PHRASE_MONOSPACE;
			break;
		case QUOTE:
			key= Preferences.PHRASE_QUOTE;
			break;
		case SPAN:
			key= Preferences.PHRASE_SPAN;
			break;
		case STRONG:
			key= Preferences.PHRASE_STRONG;
			break;
		case SUBSCRIPT:
			key= Preferences.PHRASE_SUBSCRIPT;
			break;
		case SUPERSCRIPT:
			key= Preferences.PHRASE_SUPERSCRIPT;
			break;
		case UNDERLINED:
			key= Preferences.PHRASE_UNDERLINED;
			break;
		default:
			key= null;
			break;
		}
		return this.preferences.getCssByPhraseModifierType().get(key);
	}
	
	protected final String getPrefCssStyles(final BlockType blockType) {
		final String key;
		switch (blockType) {
		case CODE:
			key= Preferences.BLOCK_BC;
			break;
		case QUOTE:
			key= Preferences.BLOCK_QUOTE;
			break;
		case PREFORMATTED:
			key= Preferences.BLOCK_PRE;
			break;
		case DEFINITION_TERM:
			key= Preferences.BLOCK_DT;
			break;
		default:
			key= null;
			break;
		}
		return this.preferences.getCssByBlockModifierType().get(key);
	}
	
	protected final String getPrefCssStyles(final int headingLevel) {
		final String key= Preferences.HEADING_PREFERENCES[headingLevel];
		return this.preferences.getCssByBlockModifierType().get(key);
	}
	
	private FontState createFontState(final FontState parentState,
			final String prefCssStyles, final String explCssStyle) {
		if (prefCssStyles == null && explCssStyle == null) {
			return parentState;
		}
		
		final FontState fontState= new FontState(parentState);
		if (prefCssStyles != null) {
			processCssStyles(fontState, parentState, prefCssStyles);
		}
		if (explCssStyle != null) {
			processCssStyles(fontState, parentState, explCssStyle);
		}
		return fontState;
	}
	
	
	private final RGB htmlCommentColor= PreferencesUtil.getInstancePrefs().getPreferenceValue(EmbeddedHtml.HTML_COMMENT_COLOR_PREF);
	private final RGB htmlBackgroundColor= PreferencesUtil.getInstancePrefs().getPreferenceValue(EmbeddedHtml.HTML_BACKGROUND_COLOR_PREF);
	
	private FontState createHtmlFontState(final FontState parentState,
			final EmbeddingAttributes attributes) {
		final FontState fontState= new FontState(parentState);
		
		if ((attributes.getEmbedDescr() & IExtdocMarkupLanguage.EMBEDDED_HTML_COMMENT_FLAG) != 0) {
			if (this.htmlCommentColor != null) {
				fontState.setForeground(this.htmlCommentColor);
			}
		}
		else {
			if (this.htmlBackgroundColor != null) {
				fontState.setBackground(this.htmlBackgroundColor);
			}
		}
		
		return fontState;
	}
	
	private void processCssStyles(final FontState fontState, final FontState parentState,
			final String cssStyles) {
		final Iterator<CssRule> ruleIterator= this.cssParser.createRuleIterator(cssStyles);
		while (ruleIterator.hasNext()) {
			this.styleManager.processCssStyles(fontState, parentState, ruleIterator.next());
		}
	}
	
	private void addToken(final FontState fontState, final int beginOffset, final int endOffset) {
		final StyleRange styleRange= this.styleManager.createStyleRange(fontState, beginOffset, endOffset - beginOffset);
		final TextAttribute textAttribute= createTextAttribute(styleRange);
		this.tokens.add(new PositionToken(textAttribute, beginOffset, endOffset - beginOffset));
	}
	
}
