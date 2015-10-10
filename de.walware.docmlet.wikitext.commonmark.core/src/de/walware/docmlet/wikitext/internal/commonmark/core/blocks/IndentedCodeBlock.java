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

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

import de.walware.ecommons.collections.ImList;

import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.LineSequence;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlock;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlockItem;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlocks.SourceBlockBuilder;


public class IndentedCodeBlock extends SourceBlock {
	
	
	@Override
	public boolean canStart(final LineSequence lineSequence) {
		final Line line= lineSequence.getCurrentLine();
		return (line != null
				&& !line.isBlank() && line.getIndent() >= 4 );
	}
	
	@Override
	public void createItem(final SourceBlockBuilder builder, final LineSequence lineSequence) {
		final SourceBlockItem<IndentedCodeBlock> blockItem= new SourceBlockItem<>(this, builder);
		
		lineSequence.advance();
		while (true) {
			final Line line= lineSequence.getCurrentLine();
			if (line != null) {
				if (line.isBlank()) {
					final int end= lookAheadValidIndent(lineSequence.lookAhead());
					if (end != Integer.MIN_VALUE) {
						advanceLinesUpto(lineSequence, end);
						continue;
					}
				}
				else {
					if (line.getIndent() >= 4) {
						lineSequence.advance();
						continue;
					}
				}
			}
			break;
		}
	}
	
	private int lookAheadValidIndent(final LineSequence lineSequence) {
		while (true) {
			final Line line= lineSequence.getCurrentLine();
			if (line != null) {
				if (line.isBlank()) {
					lineSequence.advance();
					continue;
				}
				if (line.getIndent() >= 4) {
					return line.getLineNumber();
				}
			}
			return Integer.MIN_VALUE;
		}
	}
	
	@Override
	public void initializeContext(final ProcessingContext context, final SourceBlockItem<?> blockItem) {
	}
	
	@Override
	public void emit(final ProcessingContext context, final SourceBlockItem<?> blockItem,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		final ImList<Line> lines= blockItem.getLines();
		
		locator.setBlockBegin(blockItem);
		builder.beginBlock(BlockType.CODE, new Attributes());
		
		for (final Line line : lines) {
			if (line.getIndent() >= 4) {
				final Line codeSegment= line.segmentByIndent(4);
				locator.setLine(codeSegment);
				builder.characters(codeSegment.getText());
				builder.characters("\n");
			}
			else {
				builder.characters("\n");
			}
		}
		
		locator.setBlockEnd(blockItem);
		builder.endBlock();
	}
	
}
