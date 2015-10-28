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

package de.walware.docmlet.wikitext.internal.commonmark.core.blocks;

import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkAsserts.assertCanStart;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkAsserts.assertCannotStart;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkAsserts.assertContent;

import org.junit.Test;

import de.walware.jcommons.collections.ImCollections;


public class BlockQuoteBlockTest {
	
	
	private BlockQuoteBlock block= new BlockQuoteBlock();
	
	
	@Test
	public void canStart() {
		assertCanStart(block, ">");
		assertCanStart(block, "> ");
		assertCanStart(block, ">test");
		assertCanStart(block, "> test");
		assertCanStart(block, " > test");
		assertCanStart(block, "  > test");
		assertCanStart(block, "   > test");
		assertCannotStart(block, "    > test");
		assertCannotStart(block, "test");
		assertCannotStart(block, " test");
	}
	
	@Test
	public void blockQuoteSimple() {
		assertContent("<p>test</p><blockquote><p>bq one bq two</p></blockquote><p>three</p>",
				"test\n > bq one\n > bq two\n\nthree");
	}
	
	@Test
	public void blockQuoteSimpleWithLazyContinuation() {
		assertContent("<p>test</p><blockquote><p>bq one bq two</p></blockquote><p>three</p>",
				"test\n > bq one\nbq two\n\nthree");
	}
	
	@Test
	public void blockQuoteContainsBlocks() {
		assertContent("<p>test</p><blockquote><ul><li>one</li></ul></blockquote><ul><li>two</li></ul><p>three</p>",
				"test\n > * one\n* two\n\nthree");
	}
	
	@Test
	public void blockQuoteLazyContinuation() {
		assertContent("<blockquote><ul><li>one two</li></ul></blockquote>", "> * one\ntwo\n");
	}
	
	@Test
	public void blockQuoteLazyContinuationNested() {
		assertContent("<blockquote><ul><li><ul><li>one two</li></ul></li></ul></blockquote>", "> * - one\ntwo\n");
		assertContent("<blockquote><blockquote><p>one two</p></blockquote></blockquote>", "> > one\ntwo\n");
		assertContent("<blockquote><ul><li><h3>one</h3</li></ul></blockquote><p>two</p>", "> * ### one\ntwo\n");
		assertContent("<blockquote><blockquote><pre><code> one\n</code></pre></blockquote></blockquote><p>two</p>", "> >      one\ntwo\n");
	}
	
	@Test
	public void blockQuoteLazyContinuationStopped() {
		assertContent("<blockquote><p>one</p></blockquote><hr/>", "> one\n****");
	}
	
	@Test
	public void blockQuoteParagraphNewlines() {
		for (String newline : ImCollections.newList("\n", "\r", "\r\n")) {
			assertContent(
					"<blockquote><p>p1 first p1 second p1 third</p></blockquote><blockquote><p>p2 first</p></blockquote>",
					"> p1 first" + newline + "> p1 second" + newline + "> p1 third" + newline + newline + "> p2 first");
		}
	}
	
}
