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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.ListAttributes;

import de.walware.jcommons.collections.ImCollections;

import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.FilterLineSequence;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.LineSequence;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlockItem;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlocks.SourceBlockBuilder;


public class ListBlock extends BlockWithNestedBlocks {
	
	
	public static final Pattern PATTERN= Pattern.compile(
			"([*+-]|([0-9]{1,9})[.)])(?:([ \t]+)(.+)?)?",
			Pattern.DOTALL );
	
	public static int computeItemLineIndent(final Line line, final Matcher matcher) {
		final int markerEndColumn= line.getColumn(matcher.end(1));
		if (matcher.start(3) != -1 && matcher.start(4) != -1) {
			final int contentStartColumn= line.getColumn(matcher.start(4));
			if (contentStartColumn - markerEndColumn <= 4) {
				return contentStartColumn - line.getColumn();
			}
		}
		return markerEndColumn + 1 - line.getColumn();
	}
	
	
	private static enum ListMode {
		TIGHT, LOOSE, TIGHT_WITH_TRAILING_EMPTY_LINE
	}
	
	
	private static final class ListBlockItem extends SourceBlockItem<ListBlock> {
		
		private char bulletType;
		
		private String listStart;
		
		public ListBlockItem(final ListBlock type, final SourceBlockBuilder builder) {
			super(type, builder);
		}
		
	}
	
	private static class ListLines extends FilterLineSequence {
		
		
		private final SourceBlockBuilder builder;
		
		private final ListItemBlock listItemBlock;
		
		
		public ListLines(final LineSequence delegate, final SourceBlockBuilder builder,
				final ListItemBlock blockItem) {
			super(delegate);
			
			this.builder= builder;
			this.listItemBlock= blockItem;
		}
		
		public ListLines(final ListLines from) {
			super(from.getDelegate().lookAhead());
			
			this.builder= from.builder;
			this.listItemBlock= from.listItemBlock;
		}
		
		
		@Override
		public LineSequence lookAhead() {
			return new ListLines(this);
		}
		
		@Override
		protected Line filter(final Line line) {
			if (!line.isBlank()) {
				if (line.getIndent() >= this.listItemBlock.itemIdent
						|| this.listItemBlock.canStart(line) ) {
					return line;
				}
				if (isLazyContinuation(line)) {
					return line.lazy();
				}
			}
			else {
				if (lookAheadSafeLine(getDelegate().lookAhead(line.getLineNumber())) != Integer.MIN_VALUE) {
					return line;
				}
			}
			return null;
		}
		
		private boolean isLazyContinuation(final Line line) {
			final SourceBlockItem<?> currentItem= this.builder.getCurrentItem();
			if (currentItem.getParent() != this.listItemBlock.listBlockItem
					&& currentItem.isParagraph()) {
				if (!(this.listItemBlock.canStart(line)
						|| ((ParagraphBlock) currentItem.getType()).isAnotherBlockStart(
								getDelegate().lookAhead(line.getLineNumber()), this.builder.getSourceBlocks(), currentItem ))) {
					return true;
				}
			}
			return false;
		}
		
		private int lookAheadSafeLine(final LineSequence lineSequence) {
			while (true) {
				final Line line= lineSequence.getCurrentLine();
				if (line != null) {
					if (line.isBlank()) {
						lineSequence.advance();
						continue;
					}
					if (line.getIndent() >= this.listItemBlock.itemIdent
							|| this.listItemBlock.canStart(line) ) {
						return line.getLineNumber();
					}
				}
				return Integer.MIN_VALUE;
			}
		}
		
	}
	
	
	private static class ListItemBlock extends BlockWithNestedBlocks {
		
		
		private final ListBlockItem listBlockItem;
		
		private int itemIdent= 4;
		
		
		public ListItemBlock(final ListBlockItem listBlockItem) {
			this.listBlockItem= listBlockItem;
		}
		
		
		@Override
		public boolean canStart(final LineSequence lineSequence, final SourceBlockItem<?> currentBlockItem) {
			return canStart(lineSequence.getCurrentLine());
		}
		
		public boolean canStart(final Line startLine) {
			if (startLine != null
					&& !startLine.isBlank() && startLine.getIndent() < this.itemIdent) {
				final ListBlock listBlock= this.listBlockItem.getType();
				final Matcher matcher;
				return ((matcher= startLine.setupIndent(listBlock.matcher)).matches()
						&& (listBlock.bulletType(startLine, matcher) == this.listBlockItem.bulletType)
						&& !listBlock.thematicBreakBlock.canStart(startLine) );
			}
			return false;
		}
		
		@Override
		public void createItem(final SourceBlockBuilder builder, final LineSequence lineSequence) {
			final SourceBlockItem<ListItemBlock> blockItem= new SourceBlockItem<>(this, builder);
			
			final Line startLine= lineSequence.getCurrentLine();
			final ListBlock listBlock= this.listBlockItem.getType();
			
			this.itemIdent= computeItemLineIndent(startLine, listBlock.matcher);
			final ListItemLines itemLineSequence= new ListItemLines(lineSequence, builder, blockItem,
					startLine.getLineNumber(), this.itemIdent );
			builder.createNestedItems(itemLineSequence, null);
		}
		
		@Override
		public void emit(final ProcessingContext context, final SourceBlockItem<?> blockItem,
				final CommonmarkLocator locator, final DocumentBuilder builder) {
			throw new UnsupportedOperationException();
		}
		
		public void emit(final ProcessingContext context, final SourceBlockItem<?> blockItem,
				final ListBlock.ListMode listMode,
				final CommonmarkLocator locator, final DocumentBuilder builder) {
			locator.setBlockBegin(blockItem);
			builder.beginBlock(BlockType.LIST_ITEM, new Attributes());
			
			for (final SourceBlockItem<?> contentBlockItem : blockItem.getNested()) {
				if (listMode == ListBlock.ListMode.TIGHT
						&& contentBlockItem.isParagraph()) {
					((ParagraphBlock) contentBlockItem.getType())
							.emit(context, contentBlockItem, false, locator, builder);
				}
				else {
					contentBlockItem.getType()
							.emit(context, contentBlockItem, locator, builder);
				}
			}
			
			locator.setBlockEnd(blockItem);
			builder.endBlock();
		}
		
	}
	
	private static class ListItemLines extends FilterLineSequence {
		
		private final SourceBlockBuilder builder;
		private final SourceBlockItem<ListItemBlock> blockItem;
		
		private final int markerLineNumber;
		private final int indent;
		
		
		public ListItemLines(final LineSequence delegate, final SourceBlockBuilder builder,
				final SourceBlockItem<ListItemBlock> blockItem,
				final int markerLineNumber, final int indent) {
			super(delegate);
			
			this.builder= builder;
			this.blockItem= blockItem;
			this.markerLineNumber= markerLineNumber;
			this.indent= indent;
		}
		
		protected ListItemLines(final ListItemLines from) {
			super(from.getDelegate().lookAhead());
			
			this.builder= from.builder;
			this.blockItem= from.blockItem;
			this.markerLineNumber= from.markerLineNumber;
			this.indent= from.indent;
		}
		
		
		@Override
		public ListItemLines lookAhead() {
			return new ListItemLines(this);
		}
		
		
		@Override
		protected Line filter(final Line line) {
			final List<SourceBlockItem<?>> nestedItems= this.blockItem.getNested();
			if (nestedItems.size() == 1
					&& nestedItems.get(0).isEmpty()
					&& line.getLineNumber() > this.markerLineNumber + 1) {
				return null;
			}
			// validity already checked in ListLines
			if (line.isLazy()) {
				return line;
			}
			if (line.getLineNumber() == this.markerLineNumber
					|| line.isBlank()
					|| line.getIndent() >= this.indent ) {
				return line.segmentByIndent(this.indent);
			}
			return null;
		}
		
	}
	
	
	private final Matcher matcher= PATTERN.matcher("");
	
	private final ThematicBreakBlock thematicBreakBlock= new ThematicBreakBlock();
	
	
	@Override
	public boolean canStart(final LineSequence lineSequence,
			final SourceBlockItem<?> currentBlockItem) {
		final Line currentLine= lineSequence.getCurrentLine();
		final Matcher matcher;
		return (currentLine != null
				&& !currentLine.isBlank() && currentLine.getIndent() < 4
				&& (matcher= currentLine.setupIndent(this.matcher)).matches()
				&& (currentBlockItem == null || canInterrupt(currentLine, matcher)) );
	}
	
	private boolean canInterrupt(final Line startLine, final Matcher matcher) {
		return (listStart(startLine, matcher) == null
				&& matcher.start(4) != -1 );
	}
	
	@Override
	public void createItem(final SourceBlockBuilder builder, final LineSequence lineSequence) {
		final ListBlockItem listBlockItem= new ListBlockItem(this, builder);
		
		final Line startLine= lineSequence.getCurrentLine();
		final Matcher matcher= startLine.setupIndent(this.matcher);
		checkState(matcher.matches());
		listBlockItem.bulletType= bulletType(startLine, matcher);
		listBlockItem.listStart= listStart(startLine, matcher);
		
		final ListItemBlock itemBlock= new ListItemBlock(listBlockItem);
		
		builder.createNestedItems(new ListLines(lineSequence, builder, itemBlock),
				ImCollections.newList(itemBlock) );
	}
	
	@Override
	public void emit(final ProcessingContext context, final SourceBlockItem<?> blockItem,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		final ListBlockItem listBlockItem= (ListBlockItem) blockItem;
		
		final ListAttributes listAttributes= new ListAttributes();
		listAttributes.setStart(listBlockItem.listStart);
		
		final ListMode listMode= (context.getMode() == ProcessingContext.PARSE_SOURCE_STRUCT) ?
				ListMode.LOOSE :
				calculateListMode(listBlockItem);
		
		locator.setBlockBegin(blockItem);
		builder.beginBlock(toBlockType(listBlockItem.bulletType), listAttributes);
		
		for (final SourceBlockItem<?> nestedBlockItem : listBlockItem.getNested()) {
			((ListItemBlock) nestedBlockItem.getType()).emit(context, nestedBlockItem, listMode,
					locator, builder );
		}
		
		locator.setBlockEnd(blockItem);
		builder.endBlock();
	}
	
	
	private ListMode calculateListMode(final ListBlockItem listBlockItem) {
		ListMode listMode= ListMode.TIGHT;
		for (final SourceBlockItem<?> itemBlockItem : listBlockItem.getNested()) {
			switch (listMode) {
			case LOOSE:
			case TIGHT_WITH_TRAILING_EMPTY_LINE:
				return ListMode.LOOSE;
			case TIGHT:
				listMode= getListItemListMode(itemBlockItem);
				continue;
			}
		}
		return (listMode == ListMode.TIGHT_WITH_TRAILING_EMPTY_LINE) ? ListMode.TIGHT : listMode;
	}
	
	private ListMode getListItemListMode(final SourceBlockItem<?> itemBlockItem) {
		final List<SourceBlockItem<?>> contentBlockItems= itemBlockItem.getNested();
		if (contentBlockItems.isEmpty()) {
			return ListMode.TIGHT;
		}
		{	final SourceBlockItem<?> block= contentBlockItems.get(0);
			if (block.isEmpty() && block.getLines().size() > 1) {
				return ListMode.LOOSE;
			}
		}
		for (int idx= 1; idx < contentBlockItems.size() - 1; idx++) {
			final SourceBlockItem<?> block= contentBlockItems.get(idx);
			if (block.isEmpty()) {
				return ListMode.LOOSE;
			}
		}
		if (contentBlockItems.size() > 1) {
			final SourceBlockItem<?> block= contentBlockItems.get(contentBlockItems.size() - 1);
			if (block.isEmpty()) {
				return ListMode.TIGHT_WITH_TRAILING_EMPTY_LINE;
			}
		}
		return ListMode.TIGHT;
	}
	
	
	private char bulletType(final Line line, final Matcher matcher) {
		return line.getText().charAt(matcher.end(1) - 1);
	}
	
	private String listStart(final Line line, final Matcher matcher) {
		String number= matcher.group(2);
		if (number != null) {
			int startIdx= 0;
			while (startIdx < number.length() - 1) {
				if (number.charAt(startIdx) == '0') {
					startIdx++;
					continue;
				}
				else {
					break;
				}
			}
			if (startIdx > 0) {
				number= number.substring(startIdx);
			}
			if (number.equals("1")) {
				return null;
			}
			return number;
		}
		return null;
	}
	
	private BlockType toBlockType(final char bulletType) {
		switch (bulletType) {
		case '*':
		case '+':
		case '-':
			return BlockType.BULLETED_LIST;
		default:
			return BlockType.NUMERIC_LIST;
		}
	}
	
}
