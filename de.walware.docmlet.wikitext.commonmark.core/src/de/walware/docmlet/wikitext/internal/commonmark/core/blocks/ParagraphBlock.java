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
import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImIdentityList;
import de.walware.jcommons.collections.ImList;

import de.walware.docmlet.wikitext.core.source.TextBlockAttributes;
import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.LineSequence;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlock;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlockItem;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlocks;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlocks.SourceBlockBuilder;
import de.walware.docmlet.wikitext.internal.commonmark.core.TextSegment;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.Inline;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.InlineParser;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.ReferenceDefinition;


public class ParagraphBlock extends SourceBlock {
	
	
	public static final ImIdentityList<Class<? extends SourceBlock>> DEFAULT_INTERRUPT_EXCLUSIONS= ImCollections.<Class<? extends SourceBlock>>newIdentityList(
			IndentedCodeBlock.class,
			SetextHeaderBlock.class );
	
	private final Collection<Class<? extends SourceBlock>> interruptExclusions;
	
	
	public ParagraphBlock() {
		this(DEFAULT_INTERRUPT_EXCLUSIONS);
	}
	
	public ParagraphBlock(final Collection<Class<? extends SourceBlock>> interruptExclusions) {
		this.interruptExclusions= interruptExclusions;
	}
	
	
	@Override
	public boolean canStart(final LineSequence lineSequence) {
		final Line line= lineSequence.getCurrentLine();
		return (line != null && !line.isBlank());
	}
	
	@Override
	public void createItem(final SourceBlockBuilder builder, final LineSequence lineSequence) {
		final SourceBlockItem<ParagraphBlock> blockItem= new SourceBlockItem<>(this, builder);
		
		lineSequence.advance();
		while (true) {
			final Line line= lineSequence.getCurrentLine();
			if (line != null
					&& !line.isBlank()
					&& !isAnotherBlockStart(lineSequence, builder.getSourceBlocks()) ) {
				lineSequence.advance();
				continue;
			}
			break;
		}
	}
	
	@Override
	public void initializeContext(final ProcessingContext context, final SourceBlockItem<?> blockItem) {
		final ImList<Line> lines= blockItem.getLines();
		
		final TextSegment textSegment= new TextSegment(lines);
		
		final List<Inline> inlines= context.getInlineParser().parse(context, textSegment, true);
		for (final Inline inline : inlines) {
			if (inline instanceof ReferenceDefinition) {
				inline.createContext(context);
			}
			else {
				return;
			}
		}
	}
	
	@Override
	public void emit(final ProcessingContext context, final SourceBlockItem<?> blockItem,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		emit(context, blockItem, true, locator, builder);
	}
	
	void emit(final ProcessingContext context, final SourceBlockItem<?> blockItem, final boolean asBlock,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		final ImList<Line> lines= blockItem.getLines();
		
		final TextSegment textSegment= new TextSegment(lines);
		
		final List<Inline> inlines= context.getInlineParser().parse(context, textSegment, true);
		if (!inlines.isEmpty()
				&& (context.getMode() == ProcessingContext.PARSE_SOURCE_STRUCT
						|| !isEmptyParagraph(inlines) )) {
			locator.setBlockBegin(blockItem);
			if (asBlock) {
				builder.beginBlock(BlockType.PARAGRAPH, new TextBlockAttributes(lines));
			}
			InlineParser.emit(context, inlines, locator, builder);
			
			locator.setBlockEnd(blockItem);
			if (asBlock) {
				builder.endBlock();
			}
		}
	}
	
	private boolean isEmptyParagraph(final List<Inline> inlines) {
		for (final Inline inline : inlines) {
			if (inline instanceof ReferenceDefinition) {
				continue;
			}
			else {
				return false;
			}
		}
		return true;
	}
	
	boolean isAnotherBlockStart(final LineSequence lineSequence, final SourceBlocks sourceBlocks) {
		final SourceBlock block= sourceBlocks.selectBlock(lineSequence);
		if (block != null && !(block instanceof ParagraphBlock)) {
			if (block instanceof HtmlBlock) {
				return ((HtmlBlock) block).canInterruptParagraph();
			}
			if (!this.interruptExclusions.contains(block.getClass())) {
				return true;
			}
		}
		return false;
	}
	
}
