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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.FilterLineSequence;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.LineSequence;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlockItem;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlocks.SourceBlockBuilder;


public class BlockQuoteBlock extends BlockWithNestedBlocks {
	
	
	private static final Pattern START_PATTERN= Pattern.compile(
			">.*",
			Pattern.DOTALL );
	private static final Pattern PROCESS_PATTERN= Pattern.compile(
			">([ \t])?.*",
			Pattern.DOTALL );
	
	public static final Pattern PATTERN= PROCESS_PATTERN;
	
	public static final int computeContentLineIndent(final Line line, final Matcher matcher) {
		if (matcher.start(1) != -1) {
			return line.getColumn(matcher.regionStart() + 2) - line.getColumn();
		}
		else {
			return line.getColumn(matcher.regionStart() + 1) - line.getColumn();
		}
	}
	
	
	private static class QuotedBlockLines extends FilterLineSequence {
		
		
		private final SourceBlockBuilder builder;
		private final SourceBlockItem<BlockQuoteBlock> blockItem;
		
		private final Matcher matcher;
		
		
		public QuotedBlockLines(final LineSequence delegate, final SourceBlockBuilder builder,
				final SourceBlockItem<BlockQuoteBlock> blockItem,
				final Matcher matcher) {
			super(delegate);
			
			this.builder= builder;
			this.blockItem= blockItem;
			this.matcher= matcher;
		}
		
		protected QuotedBlockLines(final QuotedBlockLines from) {
			super(from.getDelegate().lookAhead());
			
			this.builder= from.builder;
			this.blockItem= from.blockItem;
			this.matcher= from.matcher;
		}
		
		
		@Override
		public LineSequence lookAhead() {
			return new QuotedBlockLines(this);
		}
		
		@Override
		protected Line filter(final Line line) {
			if (!line.isBlank()) {
				final Matcher matcher;
				if (line.getIndent() < 4
						&& (matcher= line.setupIndent(this.matcher)).matches() ) {
					return line.segmentByIndent(computeContentLineIndent(line, matcher));
				}
				if (isLazyContinuation(line)) {
					return line;
				}
			}
			return null;
		}
		
		private boolean isLazyContinuation(final Line line) {
			final SourceBlockItem<?> currentItem= this.builder.getCurrentItem();
			if (currentItem.getType() instanceof ParagraphBlock) {
				final LineSequence lookAhead= createLookAhead(line);
				if (!((ParagraphBlock) currentItem.getType()).isAnotherBlockStart(
						lookAhead, this.builder.getSourceBlocks())) {
					return true;
				}
			}
			return false;
		}
		
		private LineSequence createLookAhead(final Line line) {
			final LineSequence lookAhead= getDelegate().lookAhead();
			while (lookAhead.getCurrentLine() != null
					&& lookAhead.getCurrentLine().getLineNumber() < line.getLineNumber()) {
				lookAhead.advance();
			}
			return lookAhead;
		}
		
	}
	
	
	private final Matcher startMatcher= START_PATTERN.matcher("");
	
	private Matcher processMatcher;
	
	
	public BlockQuoteBlock() {
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
		final SourceBlockItem<BlockQuoteBlock> blockItem= new SourceBlockItem<>(this, builder);
		
		final QuotedBlockLines quotedBlock= new QuotedBlockLines(lineSequence, builder, blockItem,
				getProcessMatcher() );
		builder.createNestedItems(quotedBlock, null, null);
	}
	
	@Override
	public void emit(final ProcessingContext context, final SourceBlockItem<?> blockItem,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		locator.setBlockBegin(blockItem);
		builder.beginBlock(BlockType.QUOTE, new Attributes());
		
		super.emit(context, blockItem, locator, builder);
		
		locator.setBlockEnd(blockItem);
		builder.endBlock();
	}
	
	
	private Matcher getProcessMatcher() {
		if (this.processMatcher == null) {
			this.processMatcher= PROCESS_PATTERN.matcher("");
		}
		return this.processMatcher;
	}
	
}
