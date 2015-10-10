/*=============================================================================#
 # Copyright (c) 2015 David Green and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     David Green - initial API and implementation in Mylyn
 #     Stephan Wahlbrink (WalWare.de)
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.commonmark.core;

import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.builder.EventDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.BeginBlockEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.CharactersEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.DocumentBuilderEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.EndBlockEvent;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.collections.ImList;

import com.google.common.base.Joiner;

import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlocks.SourceBlockBuilder;
import de.walware.docmlet.wikitext.internal.commonmark.core.blocks.EmptyBlock;


public class SourceBlocksTest {
	
	@Rule
	public final ExpectedException thrown = ExpectedException.none();
	
	
	private final SourceBlock block1 = mockBlock(BlockType.QUOTE, "b1");
	
	private final SourceBlock block2 = mockBlock(BlockType.PARAGRAPH, "b2");
	
	private final SourceBlocks sourceBlocks = new SourceBlocks(block1, block2, new EmptyBlock());
	
	
	public SourceBlocksTest() {
	}
	
	
	@Test
	public void requiresBlocks() {
		thrown.expect(NullPointerException.class);
		new SourceBlocks((SourceBlock[]) null);
	}
	
	@Test
	public void requiresBlocksCollection() {
		thrown.expect(NullPointerException.class);
		new SourceBlocks((List<SourceBlock>) null);
	}
	
	@Test
	public void process() {
		EventDocumentBuilder builder = new EventDocumentBuilder();
		ProcessingContext context= CommonmarkAsserts.newContext();
		List<SourceBlockItem<?>> items= sourceBlocks.createItems(
				LineSequence.create("\nb2\nmore\n\nb1 and\n\n\nb2") );
		sourceBlocks.emit(context, items, builder);
		ImList<DocumentBuilderEvent> expectedEvents= ImCollections.newList(
				new BeginBlockEvent(BlockType.PARAGRAPH, new Attributes()),
				new CharactersEvent("b2"),
				new CharactersEvent("more"),
				new EndBlockEvent(),
				new BeginBlockEvent(BlockType.QUOTE, new Attributes()),
				new CharactersEvent("b1 and"),
				new EndBlockEvent(),
				new BeginBlockEvent(BlockType.PARAGRAPH, new Attributes()),
				new CharactersEvent("b2"),
				new EndBlockEvent() );
		Assert.assertArrayEquals(Joiner.on("\n").join(builder.getDocumentBuilderEvents().getEvents()),
				expectedEvents.toArray(), builder.getDocumentBuilderEvents().getEvents().toArray());
	}
	
	private SourceBlock mockBlock(final BlockType blockType, final String startString) {
		return new SourceBlock() {
			
			@Override
			public boolean canStart(LineSequence lineSequence) {
				return lineSequence.getCurrentLine() != null
						&& lineSequence.getCurrentLine().getText().startsWith(startString);
			}
			
			@Override
			public void createItem(final SourceBlockBuilder builder, final LineSequence lineSequence) {
				final SourceBlockItem<?> blockItem= new SourceBlockItem<>(this, builder);
				
				advanceNonBlankLines(lineSequence);
			}
			
			@Override
			public void initializeContext(final ProcessingContext context, final SourceBlockItem<?> blockItem) {
			}
			
			@Override
			public void emit(final ProcessingContext context, final SourceBlockItem<?> blockItem,
					final CommonmarkLocator locator, final DocumentBuilder builder) {
				final List<Line> lines= blockItem.getLines();
				
				builder.beginBlock(blockType, new Attributes());
				
				for (final Line line : lines) {
					builder.characters(line.getText());
				}
				
				builder.endBlock();
			}
			
		};
	}
	
}
