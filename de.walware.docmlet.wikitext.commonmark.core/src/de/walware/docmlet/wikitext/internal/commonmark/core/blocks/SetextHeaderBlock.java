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


public class SetextHeaderBlock extends SourceBlock {
	
	
	private static final Pattern TEXTLINE_PATTERN= Pattern.compile(
			"(\\S.*)",
			Pattern.DOTALL );
	private static final Pattern UNDERLINE_PATTERN= Pattern.compile(
			"(=+|-+)[ \t]*",
			Pattern.DOTALL );
	
	
	private final Matcher textlineMatcher= TEXTLINE_PATTERN.matcher("");
	private final Matcher underlineMatcher= UNDERLINE_PATTERN.matcher("");
	
	
	@Override
	public boolean canStart(final LineSequence lineSequence) {
		final Line currentLine= lineSequence.getCurrentLine();
		final Line nextLine;
		return (currentLine != null
				&& !currentLine.isBlank() && currentLine.getIndent() < 4 
				&& currentLine.setupIndent(this.textlineMatcher).matches()
				&& (nextLine= lineSequence.getNextLine()) != null
				&& !nextLine.isBlank() && nextLine.getIndent() < 4
				&& nextLine.setupIndent(this.underlineMatcher).matches() );
	}
	
	@Override
	public void createItem(final SourceBlockBuilder builder, final LineSequence lineSequence) {
		final SourceBlockItem<SetextHeaderBlock> blockItem= new SourceBlockItem<>(this, builder);
		
		lineSequence.advance();
		lineSequence.advance();
	}
	
	@Override
	public void initializeContext(final ProcessingContext context, final SourceBlockItem<?> blockItem) {
	}
	
	@Override
	public void emit(final ProcessingContext context, final SourceBlockItem<?> blockItem,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		final ImList<Line> lines= blockItem.getLines();
		
		final Line startLine= lines.get(0);
		final Matcher textlineMatcher= startLine.setup(this.textlineMatcher, true, true);
		checkState(textlineMatcher.matches());
		final Line underlineLine= lines.get(1);
		final Matcher underlineMatcher= underlineLine.setup(this.underlineMatcher, true, false);
		checkState(underlineMatcher.matches());
		
		final int contentOffset= textlineMatcher.start(1);
		final int contentEnd= textlineMatcher.end(1);
		final int headingLevel= headingLevel(underlineMatcher, underlineLine);
		
		final Line headerContent= startLine.segment(contentOffset, contentEnd - contentOffset);
		final TextSegment textSegment= new TextSegment(ImCollections.newList(headerContent));
		
		final HeadingAttributes attributes= new HeadingAttributes();
		
		final InlineParser inlineParser= context.getInlineParser();
		final String headingText= inlineParser.toStringContent(context, textSegment);
		attributes.setId(context.generateHeadingId(headingLevel, headingText));
		
		locator.setLine(startLine);
		builder.beginHeading(headingLevel, attributes);
		
		inlineParser.emit(context, textSegment, locator, builder);
		
		builder.endHeading();
	}
	
	private int headingLevel(final Matcher matcher, final Line line) {
		switch (line.getText().charAt(matcher.start(1))) {
		case '=':
			return 1;
		case '-':
			return 2;
		default:
			throw new IllegalStateException();
		}
	}
	
}
