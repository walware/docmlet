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

package de.walware.docmlet.wikitext.internal.commonmark.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;


public class SourceBlocks {
	
	
	public static interface SourceBlockParticipate { 
		
		
		boolean approveBlockSelect(SourceBlock candidate, LineSequence lineSequence);
		
	}
	
	private static final SourceBlockParticipate TRUE_PARTICIPATE= new SourceBlockParticipate() {
		
		@Override
		public boolean approveBlockSelect(final SourceBlock candidate, final LineSequence lineSequence) {
			return true;
		}
		
	};
	
	private static interface ItemRunnable {
		
		void run(SourceBlockItem<?> blockItem);
		
	}
	
	public class SourceBlockBuilder {
		
		
		private SourceBlockItem<?> currentItem;
		
		
		private SourceBlockBuilder() {
		}
		
		
		void setCurrentItem(final SourceBlockItem<?> blockItem) {
			this.currentItem= blockItem;
		}
		
		public SourceBlockItem<?> getCurrentItem() {
			return this.currentItem;
		}
		
		public SourceBlocks getSourceBlocks() {
			return SourceBlocks.this;
		}
		
		public void createNestedItems(final LineSequence lineSequence,
				final ImList<? extends SourceBlock> supportedBlocks, final SourceBlockParticipate participate) {
			processItems(lineSequence, this,
					(supportedBlocks != null) ? supportedBlocks : getSourceBlocks().supportedBlocks, 
					new ItemRunnable() {
						@Override
						public void run(final SourceBlockItem<?> blockItem) {
						}
					},
					(participate != null) ? participate : TRUE_PARTICIPATE );
		}
		
	}
	
	private static class CollectLineSequence extends LineSequence {
		
		
		private final LineSequence delegate;
		
		private Line currentLine;
		
		private final List<Line> lines= new ArrayList<>();
		
		
		public CollectLineSequence(final LineSequence delegate) {
			this.delegate= delegate;
		}
		
		
		@Override
		public LineSequence lookAhead() {
			return this.delegate.lookAhead();
		}
		
		@Override
		public Line getCurrentLine() {
			return this.currentLine= this.delegate.getCurrentLine();
		}
		
		@Override
		public Line getNextLine() {
			return this.delegate.getNextLine();
		}
		
		@Override
		public void advance() {
			Line line= this.currentLine;
			if (line == null) {
				line= this.delegate.getCurrentLine();
			}
			else {
				this.currentLine= null;
			}
			
			this.delegate.advance();
			
			this.lines.add(line);
		}
		
		
		public ImList<Line> getLines() {
			return ImCollections.toList(this.lines);
		}
		
		public void initLines() {
			this.lines.clear();
		}
		
	}
	
	
	private final ImList<SourceBlock> supportedBlocks;
	
	
	public SourceBlocks(final SourceBlock... blocks) {
		this(ImCollections.newList(blocks));
	}
	
	public SourceBlocks(final List<SourceBlock> supportedBlocks) {
		this.supportedBlocks= ImCollections.toList(supportedBlocks);
	}
	
	
	public List<SourceBlockItem<?>> createItems(final LineSequence lineSequence) {
		final List<SourceBlockItem<?>> items= new ArrayList<>();
		
		final SourceBlockBuilder sourceBlockBuilder= new SourceBlockBuilder();
		
		processItems(lineSequence, sourceBlockBuilder, this.supportedBlocks,
				new ItemRunnable() {
					@Override
					public void run(final SourceBlockItem<?> blockItem) {
						items.add(blockItem);
					}
				}, TRUE_PARTICIPATE );
		
		return items;
	}
	
	public void parseSourceStruct(final ProcessingContext context, final LineSequence lineSequence,
			final DocumentBuilder builder) {
		context.setMode(ProcessingContext.PARSE_SOURCE_STRUCT);
		
		final SourceBlockBuilder sourceBlockBuilder= new SourceBlockBuilder();
		
		final CommonmarkLocator locator= new CommonmarkLocator();
		builder.setLocator(locator);
		
		processItems(lineSequence, sourceBlockBuilder, this.supportedBlocks,
				new ItemRunnable() {
					@Override
					public void run(final SourceBlockItem<?> blockItem) {
						blockItem.getType().emit(context, blockItem, locator, builder);
					}
				}, TRUE_PARTICIPATE );
	}
	
	private void processItems(final LineSequence lineSequence, final SourceBlockBuilder builder,
			final ImList<? extends SourceBlock> supportedBlocks,
			final ItemRunnable runnable, final SourceBlockParticipate participate) {
		final CollectLineSequence collectLineSequence= new CollectLineSequence(lineSequence);
		
		final SourceBlockItem<?> parentItem= builder.getCurrentItem();
		while (collectLineSequence.getCurrentLine() != null) {
			final SourceBlock block= selectBlock(collectLineSequence, supportedBlocks);
			if (block != null
					&& participate.approveBlockSelect(block, collectLineSequence)) {
				collectLineSequence.initLines();
				block.createItem(builder, collectLineSequence);
				final SourceBlockItem<?> blockItem= builder.getCurrentItem();
				blockItem.setLines(collectLineSequence.getLines());
				builder.setCurrentItem(parentItem);
				
				runnable.run(blockItem);
			}
			else {
				return;
			}
		}
	}
	
	public void initializeContext(final ProcessingContext context, final List<SourceBlockItem<?>> items) {
		context.setMode(ProcessingContext.INITIALIZE_CONTEXT);
		for (final SourceBlockItem<?> item : items) {
			item.getType().initializeContext(context, item);
		}
	}
	
	public void emit(final ProcessingContext context, final List<SourceBlockItem<?>> items,
			final DocumentBuilder builder) {
		context.setMode(ProcessingContext.EMIT_DOCUMENT);
		
		final CommonmarkLocator locator= new CommonmarkLocator();
		builder.setLocator(locator);
		
		for (final SourceBlockItem<?> item : items) {
			item.getType().emit(context, item, locator, builder);
		}
	}
	
	
	public SourceBlock selectBlock(final LineSequence lineSequence) {
		return selectBlock(lineSequence, this.supportedBlocks);
	}
	
	public SourceBlock selectBlock(final LineSequence lineSequence,
			final ImList<? extends SourceBlock> supportedBlocks) {
		for (final SourceBlock candidate : supportedBlocks) {
			if (candidate.canStart(lineSequence)) {
				return candidate;
			}
		}
		return null;
	}
	
}
