/*=============================================================================#
 # Copyright (c) 2015-2016 David Green and others.
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

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.HeadingAttributes;

import de.walware.jcommons.collections.ImList;

import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.LineSequence;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlock;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlockItem;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlocks.SourceBlockBuilder;
import de.walware.docmlet.wikitext.internal.commonmark.core.TextSegment;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.InlineParser;


public class SetextHeaderOrParagraphBlock extends ParagraphBlock {
	
	
	private static final Pattern SETEXT_UNDERLINE_PATTERN= Pattern.compile(
			"(=+|-+)[ \t]*",
			Pattern.DOTALL );
	
	
	private static class SetextHeaderOrParagraphBlockItem extends ParagraphSourceBlockItem<SetextHeaderOrParagraphBlock> {
		
		
		private byte headingLevel;
		
		
		public SetextHeaderOrParagraphBlockItem(final SetextHeaderOrParagraphBlock type, final SourceBlockBuilder builder) {
			super(type, builder);
		}
		
		
		@Override
		public boolean isParagraph() {
			return (this.headingLevel == 0);
		}
		
	}
	
	
	private final Matcher setextMatcher= SETEXT_UNDERLINE_PATTERN.matcher("");
	
	
	public SetextHeaderOrParagraphBlock() {
		super();
	}
	
	public SetextHeaderOrParagraphBlock(final Collection<Class<? extends SourceBlock>> interruptExclusions) {
		super(interruptExclusions);
	}
	
	
	@Override
	public void createItem(final SourceBlockBuilder builder, final LineSequence lineSequence) {
		final SetextHeaderOrParagraphBlockItem typedBlockItem= new SetextHeaderOrParagraphBlockItem(this, builder);
		
		lineSequence.advance();
		while (true) {
			final Line line= lineSequence.getCurrentLine();
			if (line != null
					&& !line.isBlank()) {
				Matcher setextMatcher;
				if (!line.isLazy() && line.getIndent() < 4
						&& (setextMatcher= line.setupIndent(this.setextMatcher)).matches() ) {
					typedBlockItem.headingLevel= headingLevel(setextMatcher, line);
					lineSequence.advance();
					break;
				}
				if (!isAnotherBlockStart(lineSequence, builder.getSourceBlocks(), typedBlockItem) ) {
					lineSequence.advance();
					continue;
				}
			}
			break;
		}
	}
	
	private byte headingLevel(final Matcher matcher, final Line line) {
		switch (line.getText().charAt(matcher.start(1))) {
		case '=':
			return 1;
		case '-':
			return 2;
		default:
			throw new IllegalStateException();
		}
	}
	
	@Override
	public void initializeContext(final ProcessingContext context, final SourceBlockItem<?> blockItem) {
		final SetextHeaderOrParagraphBlockItem typedBlockItem= (SetextHeaderOrParagraphBlockItem) blockItem;
		if (typedBlockItem.headingLevel > 0) {
			return;
		}
		
		super.initializeContext(context, typedBlockItem);
	}
	
	@Override
	public void emit(final ProcessingContext context, final SourceBlockItem<?> blockItem,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		final SetextHeaderOrParagraphBlockItem typedBlockItem= (SetextHeaderOrParagraphBlockItem) blockItem;
		if (typedBlockItem.headingLevel > 0) {
			emitHeading(context, typedBlockItem, locator, builder);
		}
		else {
			emit(context, blockItem, true, locator, builder);
		}
	}
	
	private void emitHeading(final ProcessingContext context, final SetextHeaderOrParagraphBlockItem blockItem,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		final ImList<Line> lines= blockItem.getLines();
		
		final TextSegment textSegment= new TextSegment(lines.subList(0, lines.size() - 1));
		
		final HeadingAttributes attributes= new HeadingAttributes();
		
		final InlineParser inlineParser= context.getInlineParser();
		final String headingText= inlineParser.toStringContent(context, textSegment);
		attributes.setId(context.generateHeadingId(blockItem.headingLevel, headingText));
		
		locator.setBlockBegin(blockItem);
		builder.beginHeading(blockItem.headingLevel, attributes);
		
		inlineParser.emit(context, textSegment, locator, builder);
		
		locator.setBlockEnd(blockItem);
		builder.endHeading();
	}
	
}
