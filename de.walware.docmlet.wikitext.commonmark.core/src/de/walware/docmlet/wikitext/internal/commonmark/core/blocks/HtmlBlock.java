/*=============================================================================#
 # Copyright (c) 2015 David Green and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     David Green - initial API and implementation in Mylyn
 #     Stephan Wahlbrink (WalWare.de) - revised API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.commonmark.core.blocks;

import static com.google.common.base.Preconditions.checkState;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.CDATA_END_REGEX;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.CDATA_START1_REGEX;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.CLOSE_TAG_1_REGEX;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.COMMENT_END_REGEX;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.COMMENT_START1_REGEX;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.DECL_END_REGEX;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.DECL_START1_REGEX;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.OPEN_TAG_1_REGEX;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.PI_END_REGEX;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.PI_START1_REGEX;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

import de.walware.ecommons.collections.ImList;

import de.walware.docmlet.wikitext.core.source.EmbeddingAttributes;
import de.walware.docmlet.wikitext.core.source.extdoc.IExtdocMarkupLanguage;
import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.LineSequence;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlock;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlockItem;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlocks.SourceBlockBuilder;


public class HtmlBlock extends SourceBlock {
	
	
	private static final String HTML_1_TAG_NAMES= "script|pre|style";
	private static final String HTML_BLOCK_TAG_NAMES=
			"address|article|aside|base|basefont|blockquote|body" +
			"|caption|center|col|colgroup" +
			"|dd|details|dialog|dir|div|dl|dt" +
			"|fieldset|figcaption|figure|footer|form|frame|frameset" +
			"|h1|head|header|hr|html|iframe" + 
			"|legend|li|link" +
			"|main|menu|menuitem|meta|nav|noframes" +
			"|ol|optgroup|option|p|param" +
			"|section|source|title|summary" +
			"|table|tbody|td|tfoot|th|thead|tr|track" +
			"|ul";
	
	private static final Pattern START_PATTERN= Pattern.compile("<(?:" +
					"((?:(?i)" + HTML_1_TAG_NAMES + ")(?:[\\s>].*)?)" +
					"|(" + COMMENT_START1_REGEX + ".*)" +
					"|(" + PI_START1_REGEX + ".*)" +
					"|(" + DECL_START1_REGEX + ".*)" +
					"|(" + CDATA_START1_REGEX + ".*)" +
					"|(/?(?:(?i)" + HTML_BLOCK_TAG_NAMES + ")(?:(?:\\s|/?>).*)?)" +
					"|(" + OPEN_TAG_1_REGEX + "\\s*|" + CLOSE_TAG_1_REGEX + "\\s*)" +
			")",
			Pattern.DOTALL );
	
	private static final Pattern END_HTML_1_PATTERN= Pattern.compile(
			"</(?:(?i)" + HTML_1_TAG_NAMES + ")>",
			Pattern.DOTALL );
	private static final Pattern END_COMMENT_PATTERN= Pattern.compile(
			COMMENT_END_REGEX,
			Pattern.DOTALL );
	private static final Pattern END_PI_PATTERN= Pattern.compile(
			PI_END_REGEX,
			Pattern.DOTALL );
	private static final Pattern END_DECL_PATTERN= Pattern.compile(
			DECL_END_REGEX,
			Pattern.DOTALL );
	private static final Pattern END_CDATA_PATTERN= Pattern.compile(
			CDATA_END_REGEX,
			Pattern.DOTALL );
	
	
	static final class HtmlBlockItem extends SourceBlockItem<HtmlBlock> {
		
		private byte htmlType;
		
		private boolean isClosed;
		
		public HtmlBlockItem(final HtmlBlock type, final SourceBlockBuilder builder) {
			super(type, builder);
		}
		
	}
	
	
	private final Matcher startMatcher= START_PATTERN.matcher("");
	
	private Matcher endHtml1Matcher;
	private Matcher endCommentMatcher;
	private Matcher endPIMatcher;
	private Matcher endDeclMatcher;
	private Matcher endCDATAMatcher;
	
	
	public HtmlBlock() {
	}
	
	
	@Override
	public boolean canStart(final LineSequence lineSequence) {
		final Line currentLine= lineSequence.getCurrentLine();
		return (currentLine != null
				&& !currentLine.isBlank() && currentLine.getIndent() < 4
				&& currentLine.setupIndent(this.startMatcher).matches() );
	}
	
	public boolean canInterruptParagraph() {
		final Matcher matcher= this.startMatcher;
		return (matcher.start(7) == -1);
	}
	
	@Override
	public void createItem(final SourceBlockBuilder builder, final LineSequence lineSequence) {
		final HtmlBlockItem htmlBlockItem= new HtmlBlockItem(this, builder);
		
		final Line startLine= lineSequence.getCurrentLine();
		
		final Matcher matcher= startLine.setupIndent(this.startMatcher);
		checkState(matcher.matches());
		
		htmlBlockItem.htmlType= getType(matcher);
		final Matcher endMatcher= getEndMatcher(htmlBlockItem.htmlType);
		
		if (endMatcher != null) {
			while (true) {
				final Line line= lineSequence.getCurrentLine();
				if (line != null) {
					lineSequence.advance();
					
					if (line.setup(endMatcher).find()) {
						htmlBlockItem.isClosed= true;
						break;
					}
					continue;
				}
				break;
			}
		}
		else {
			advanceNonBlankLines(lineSequence);
		}
	}
	
	@Override
	public void initializeContext(final ProcessingContext context, final SourceBlockItem<?> blockItem) {
	}
	
	@Override
	public void emit(final ProcessingContext context, final SourceBlockItem<?> blockItem,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		final HtmlBlockItem htmlBlockItem= (HtmlBlockItem) blockItem;
		final ImList<Line> lines= blockItem.getLines();
		
		if (context.getMode() == ProcessingContext.PARSE_SOURCE_STRUCT) {
			final int descr;
			if (htmlBlockItem.htmlType == 2) {
				descr= IExtdocMarkupLanguage.EMBEDDED_HTML_COMMENT_BLOCK_DESCR;
			}
			else {
				descr= IExtdocMarkupLanguage.EMBEDDED_HTML_OTHER_BLOCK_DESCR
						| ((htmlBlockItem.htmlType << IExtdocMarkupLanguage.EMBEDDED_HTML_DISTINCT_SHIFT) & IExtdocMarkupLanguage.EMBEDDED_HTML_DISTINCT_MASK);
			}
			
			locator.setBlockBegin(blockItem);
			builder.beginBlock(BlockType.CODE, new EmbeddingAttributes(
					IExtdocMarkupLanguage.EMBEDDED_HTML, descr,
					lines.get(0).getOffset(), Integer.MIN_VALUE ));
		}
		
		for (final Line line : lines) {
			locator.setLine(line);
			builder.charactersUnescaped(line.getText());
			builder.charactersUnescaped("\n");
		}
		
		if (context.getMode() == ProcessingContext.PARSE_SOURCE_STRUCT) {
			locator.setBlockEnd(blockItem);
			builder.endBlock();
		}
	}
	
	
	private byte getType(final Matcher matcher) {
		if (matcher.start(1) != -1) {
			return 1;
		}
		if (matcher.start(2) != -1) {
			return 2;
		}
		if (matcher.start(3) != -1) {
			return 3;
		}
		if (matcher.start(4) != -1) {
			return 4;
		}
		if (matcher.start(5) != -1) {
			return 5;
		}
		if (matcher.start(6) != -1) {
			return 6;
		}
		return 7;
	}
	
	private Matcher getEndMatcher(final byte type) {
		switch (type) {
		case 1:
			if (this.endHtml1Matcher == null) {
				this.endHtml1Matcher= END_HTML_1_PATTERN.matcher("");
			}
			return this.endHtml1Matcher;
		case 2:
			if (this.endCommentMatcher == null) {
				this.endCommentMatcher= END_COMMENT_PATTERN.matcher("");
			}
			return this.endCommentMatcher;
		case 3:
			if (this.endPIMatcher == null) {
				this.endPIMatcher= END_PI_PATTERN.matcher("");
			}
			return this.endPIMatcher;
		case 4:
			if (this.endDeclMatcher == null) {
				this.endDeclMatcher= END_DECL_PATTERN.matcher("");
			}
			return this.endDeclMatcher;
		case 5:
			if (this.endCDATAMatcher == null) {
				this.endCDATAMatcher= END_CDATA_PATTERN.matcher("");
			}
			return this.endCDATAMatcher;
		default:
			return null; // blank line
		}
	}
	
}
