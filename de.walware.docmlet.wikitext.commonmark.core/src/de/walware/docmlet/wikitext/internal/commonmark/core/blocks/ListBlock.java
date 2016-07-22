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
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlock;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlockItem;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlocks.SourceBlockBuilder;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlocks.SourceBlockParticipate;


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
	
	
	static final class ListBlockItem extends SourceBlockItem<ListBlock> {
		
		private char bulletType;
		
		private String listStart;
		
		public ListBlockItem(final ListBlock type, final SourceBlockBuilder builder) {
			super(type, builder);
		}
		
	}
	
	
	private static class ListItemBlock extends BlockWithNestedBlocks {
		
		
		private final ListBlockItem listBlockItem;
		
		private int itemIdent= 4;
		
		
		public ListItemBlock(final ListBlockItem listBlockItem) {
			this.listBlockItem= listBlockItem;
		}
		
		
		@Override
		public boolean canStart(final LineSequence lineSequence) {
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
			builder.createNestedItems(itemLineSequence, null, itemLineSequence);
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
						&& contentBlockItem.getType() instanceof ParagraphBlock) {
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
	
	private static class ListItemLines extends FilterLineSequence 
			implements SourceBlockParticipate {
		
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
		public boolean approveBlockSelect(final SourceBlock selected, final LineSequence lineSequence) {
			final List<SourceBlockItem<?>> nestedItems= this.blockItem.getNested();
			if (!nestedItems.isEmpty()
					&& selected instanceof EmptyBlock
					&& !isSaveListContinuation(getDelegate().getNextLine()) ) {
				return false;
			}
			return true;
		}
		
		@Override
		public ListItemLines lookAhead() {
			return new ListItemLines(this);
		}
		
		private boolean isSaveListContinuation(final Line line) {
			return (line != null && !line.isBlank()
					&& (line.getIndent() >= this.indent
							|| this.blockItem.getType().canStart(line) ));
		}
		
		
		@Override
		protected Line filter(final Line line) {
			final List<SourceBlockItem<?>> nestedItems= this.blockItem.getNested();
			if (nestedItems.size() == 1
					&& nestedItems.get(0).getType() instanceof EmptyBlock
					&& line.getLineNumber() > this.markerLineNumber + 1) {
				return null;
			}
			if (line.getLineNumber() == this.markerLineNumber
					|| line.isBlank()
					|| line.getIndent() >= this.indent ) {
				return line.segmentByIndent(this.indent);
			}
			if (isLazyContinuation(line)) {
				return line;
			}
			return null;
		}
		
		private boolean isLazyContinuation(final Line line) {
			final SourceBlockItem<?> currentItem= this.builder.getCurrentItem();
			if (currentItem.getType() instanceof ParagraphBlock) {
				if (!(this.blockItem.getType().canStart(line)
						|| ((ParagraphBlock) currentItem.getType()).isAnotherBlockStart(
								createLookAhead(line), this.builder.getSourceBlocks() ))) {
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
	
	
	private final Matcher matcher= PATTERN.matcher("");
	
	private final ThematicBreakBlock thematicBreakBlock= new ThematicBreakBlock();
	
	
	@Override
	public boolean canStart(final LineSequence lineSequence) {
		final Line currentLine= lineSequence.getCurrentLine();
		return (currentLine != null
				&& !currentLine.isBlank() && currentLine.getIndent() < 4
				&& currentLine.setupIndent(this.matcher).matches() );
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
		
		builder.createNestedItems(lineSequence, ImCollections.newList(itemBlock), null);
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
		
		for (final SourceBlockItem<?> itemBlockItem : listBlockItem.getNested()) {
			((ListItemBlock) itemBlockItem.getType()).emit(context, itemBlockItem, listMode,
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
			if (block.getType() instanceof EmptyBlock && block.getLines().size() > 1) {
				return ListMode.LOOSE;
			}
		}
		for (int idx= 1; idx < contentBlockItems.size() - 1; idx++) {
			final SourceBlockItem<?> block= contentBlockItems.get(idx);
			if (block.getType() instanceof EmptyBlock) {
				return ListMode.LOOSE;
			}
		}
		if (contentBlockItems.size() > 1) {
			final SourceBlockItem<?> block= contentBlockItems.get(contentBlockItems.size() - 1);
			if (block.getType() instanceof EmptyBlock) {
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
