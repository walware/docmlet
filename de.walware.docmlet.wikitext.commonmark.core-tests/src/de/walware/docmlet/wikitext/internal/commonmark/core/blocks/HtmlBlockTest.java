/*=============================================================================#
 # Copyright (c) 2015-2016 David Green and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     David Green - initial API and implementation in Mylyn
 #     Stephan Wahlbrink (WalWare.de)
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.commonmark.core.blocks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.LineSequence;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlockItem;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlocks;


public class HtmlBlockTest {
	
	
	private final HtmlBlock block = new HtmlBlock();
	
	
	public HtmlBlockTest() {
	}
	
	
	@Test
	public void canStart_Type1() {
		for (String tagName : new String[] { "script", "pre", "style" }) {
			assertCanStart(true, 1, "<" + tagName);
			assertCanStart(true, 1, " <" + tagName);
			assertCanStart(true, 1, "   <" + tagName);
			assertCanStart(false, 1, "    <" + tagName);
			assertCanStart(true, 1, "<" + tagName + ">");
			assertCanStart(true, 1, "<" + tagName + "> ");
			assertCanStart(true, 7, "<" + tagName + "/>");
			assertCanStart(true, 1, "<" + tagName + ">with some text");
			assertCanStart(true, 1, "<" + tagName + "></" + tagName + ">");
			assertCanStart(true, 1, "<" + tagName + "></" + tagName + " >");
			assertCanStart(true, 1, "<" + tagName + ">  sdf</" + tagName + " >");
		}
	}
	
	@Test
	public void canStart_Comment() {
		assertCanStart(true, 2, "<!-- a comment -->");
		assertCanStart(true, 2, "<!-- <");
		assertCanStart(true, 2, "<!--");
		assertCanStart(true, 2, "<!-- <-");
		assertCanStart(true, 2, "<!--<-");
		// CommonMark spec != HTML spec
//		assertCanStart(false, "<!-->");
//		assertCanStart(false, "<!--->");
		assertCanStart(true, 2, "<!-- ->");
//		assertCanStart(false, "<!-- -- -->");
	}
	
	@Test
	public void canStart() {
		assertFalse(block.canStart(LineSequence.create(""), null));
		assertTrue(block.canStart(LineSequence.create("<div>"), null));
		assertTrue(block.canStart(LineSequence.create("<table>"), null));
		assertTrue(block.canStart(LineSequence.create("<p>"), null));
		assertTrue(block.canStart(LineSequence.create("<one>"), null));
		assertFalse(block.canStart(LineSequence.create("<one invalid=>"), null));
		assertFalse(block.canStart(LineSequence.create("<one> with text"), null));
		assertTrue(block.canStart(LineSequence.create("   <p>"), null));
		assertFalse(block.canStart(LineSequence.create("    <p>"), null));
		assertFalse(block.canStart(LineSequence.create("\t<p>"), null));
		assertTrue(block.canStart(LineSequence.create("<p"), null));
		assertTrue(block.canStart(LineSequence.create("<p >"), null));
		assertTrue(block.canStart(LineSequence.create("<p />"), null));
		assertTrue(block.canStart(LineSequence.create("<p/>"), null));
		assertTrue(block.canStart(LineSequence.create("<p\n  a=\"b\"\n>"), null));
	}
	
	@Test
	public void canStartDoesNotAdvanceLineSequencePosition() {
		LineSequence lineSequence = LineSequence.create("<p\n  a=\"b\"\n>");
		Line firstLine = lineSequence.getCurrentLine();
		assertTrue(block.canStart(lineSequence, null));
		assertSame(firstLine, lineSequence.getCurrentLine());
	}
	
	private void assertCanStart(boolean expected, int type, String string) {
		assertEquals(expected, block.canStart(LineSequence.create(string), null));
		if (expected && type > 0) {
			final SourceBlockItem<?> paragraph= new SourceBlocks(new ParagraphBlock()).createItems(LineSequence.create("abc")).get(0);
			assertEquals(type != 7, block.canStart(LineSequence.create(string), paragraph));
		}
	}
	
}
