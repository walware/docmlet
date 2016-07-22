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

import static com.google.common.base.Preconditions.checkState;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.HeadingAttributes;

import de.walware.jcommons.collections.ImCollections;

import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.LineSequence;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlock;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlockItem;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlocks.SourceBlockBuilder;
import de.walware.docmlet.wikitext.internal.commonmark.core.TextSegment;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.InlineParser;


public class AtxHeaderBlock extends SourceBlock {
	
	
	private static final Pattern START_PATTERN= Pattern.compile(
			"#{1,6}(?:\\ .*)?",
			Pattern.DOTALL );
	
	private static final Pattern PATTERN= Pattern.compile(
			"(#{1,6})(?:\\ \\ ??(.+?))??(?:\\ +#+)?\\ *",
			Pattern.DOTALL );
	
	
	private final Matcher startMatcher= START_PATTERN.matcher("");
	
	private Matcher matcher;
	
	
	public AtxHeaderBlock() {
	}
	
	
	@Override
	public boolean canStart(final LineSequence lineSequence) {
		final Line currentLine= lineSequence.getCurrentLine();
		return (currentLine != null
				&& !currentLine.isBlank() && currentLine.getIndent() < 4
				&& currentLine.setupIndent(this.startMatcher).matches() );
	}
	
	@Override
	public void createItem(final SourceBlockBuilder builder, final LineSequence lineSequence) {
		final SourceBlockItem<AtxHeaderBlock> blockItem= new SourceBlockItem<>(this, builder);
		
		lineSequence.advance();
	}
	
	@Override
	public void initializeContext(final ProcessingContext context, final SourceBlockItem<?> blockItem) {
	}
	
	@Override
	public void emit(final ProcessingContext context, final SourceBlockItem<?> blockItem,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		final Line startLine= blockItem.getLines().get(0);
		final Matcher matcher= startLine.setup(getProcessMatcher(), true, true);
		checkState(matcher.matches());
		
		final int contentOffset= matcher.start(2);
		final int contentEnd= matcher.end(2);
		final int headingLevel= headingLevel(startLine, matcher);
		if (contentEnd > contentOffset) {
			final Line headerContent= startLine.segment(contentOffset, contentEnd - contentOffset);
			final TextSegment textSegment= new TextSegment(ImCollections.newList(headerContent));
			
			final HeadingAttributes attributes= new HeadingAttributes();
			
			final InlineParser inlineParser= context.getInlineParser();
			final String headingText= inlineParser.toStringContent(context, textSegment);
			attributes.setId(context.generateHeadingId(headingLevel, headingText));
			
			locator.setBlockBegin(blockItem);
			builder.beginHeading(headingLevel, attributes);
			
			inlineParser.emit(context, textSegment, locator, builder);
			
			builder.endHeading();
		} else {
			locator.setBlockBegin(blockItem);
			builder.beginHeading(headingLevel, new HeadingAttributes());
			builder.endHeading();
		}
	}
	
	
	private Matcher getProcessMatcher() {
		if (this.matcher == null) {
			this.matcher= PATTERN.matcher("");
		}
		return this.matcher;
	}
	
	private int headingLevel(final Line line, final Matcher matcher) {
		return matcher.end(1) - matcher./*start(1)*/regionStart();
	}
	
}
