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

import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkAsserts.assertContent;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import de.walware.jcommons.collections.ImCollections;

import com.google.common.base.Joiner;

import de.walware.docmlet.wikitext.internal.commonmark.core.LineSequence;


public class ListBlockTest {
	
	
	public ListBlockTest() {
	}
	
	
	@Test
	public void canStartBulleted() {
		assertCanStart(true, "-");
		assertCanStart(true, "- ");
		assertCanStart(true, "- test");
		assertCanStart(true, " - test");
		assertCanStart(true, "  - test");
		assertCanStart(true, "   - test");
		assertCanStart(false, "    - test");
		assertCanStart(false, "\t- test");
		assertCanStart(true, "* test");
		assertCanStart(true, "+ test");
		assertCanStart(false, "x test");
	}
	
	@Test
	public void canStartOrdered() {
		assertCanStart(true, "1.");
		assertCanStart(true, "1. ");
		assertCanStart(true, "1. test");
		assertCanStart(true, " 1. test");
		assertCanStart(true, "  1. test");
		assertCanStart(true, "   1. test");
		assertCanStart(false, "    1. test");
		assertCanStart(false, "\t1. test");
		assertCanStart(true, "2. test");
		assertCanStart(true, "3. test");
		assertCanStart(true, "1) test");
		assertCanStart(true, "2) test");
		assertCanStart(true, "23) test");
		assertCanStart(true, "23. test");
		assertCanStart(true, "0. test");
		assertCanStart(true, "001. test");
		assertCanStart(true, "123456789. test");
		assertCanStart(false, "1234567890. test");
		assertCanStart(false, "a. test");
	}
	
	@Test
	public void simpleList() {
		assertSimpleBulletedList("*");
		assertSimpleBulletedList("-");
		assertSimpleBulletedList("+");
	}
	
	@Test
	public void listWithNestedBlocks() {
		assertBulletedListWithNestedBlocks("*");
		assertBulletedListWithNestedBlocks("-");
		assertBulletedListWithNestedBlocks("+");
	}
	
	@Test
	public void tightListWithSublist() {
		List<String> lines = ImCollections.newList("* one", "* two", "    * three", "* four");
		assertContent("<ul><li>one</li><li>two<ul><li>three</li></ul></li><li>four</li></ul>",
				Joiner.on("\n").join(lines));
	}
	
	@Test
	public void simpleOrderedList() {
		assertSimpleOrderedList(".");
		assertSimpleOrderedList(")");
	}
	
	@Test
	public void simpleOrderedListWithNestedBlocks() {
		assertOrderedListWithNestedBlocks(".");
		assertOrderedListWithNestedBlocks(")");
	}
	
	@Test
	public void listWithFencedCodeBlock() {
		assertContent("<ul><li><pre><code>a\n\n\nb\n</code></pre></li></ul>", "* ```\n  a\n\n\n  b\n  ```");
	}
	
	@Test
	public void terminatesWithDoubleBlankLine() {
		List<String> lines = ImCollections.newList("* one", "* two", "", "", "* three");
		assertContent("<ul><li>one</li><li>two</li></ul><ul><li>three</li></ul>", Joiner.on("\n").join(lines));
	}
	
	@Test
	public void orderedListWithStart() {
		List<String> lines = ImCollections.newList("3. one", "4. two");
		assertContent("<ol start=\"3\"><li>one</li><li>two</li></ol>", Joiner.on("\n").join(lines));
	}
	
	@Test
	public void doubleBlankLineStartingOnListItem() {
		List<String> lines = ImCollections.newList("* one", "*", "", "* three");
		assertContent("<ul><li><p>one</p></li><li></li><li><p>three</p></li></ul>", Joiner.on("\n").join(lines));
	}
	
	@Test
	public void startingWithBlank() {
		List<String> lines = ImCollections.newList("*      ", "  one", "     two");
		assertContent("<ul><li>one two</li></ul>", Joiner.on("\n").join(lines));
	}
	
	
	private void assertSimpleOrderedList(String delimiter) {
		List<String> lines = ImCollections.newList("1" + delimiter + " one", "2" + delimiter + " two",
				"3" + delimiter + " three four");
		assertContent("<ol><li>one</li><li>two</li><li>three four</li></ol>", Joiner.on("\n").join(lines));
	}
	
	private void assertBulletedListWithNestedBlocks(String delimiter) {
		List<String> lines = ImCollections.newList(delimiter + " one", delimiter + " two\n  two.2\n\n  two.3",
				delimiter + " three four");
		assertContent("<ul><li><p>one</p></li><li><p>two two.2</p><p>two.3</p></li><li><p>three four</p></li></ul>",
				Joiner.on("\n").join(lines));
	}
	
	private void assertOrderedListWithNestedBlocks(String delimiter) {
		List<String> lines = ImCollections.newList("1" + delimiter + " one", "2" + delimiter + " two\n   two.2\n\n   two.3",
				"3" + delimiter + " three four");
		assertContent("<ol><li><p>one</p></li><li><p>two two.2</p><p>two.3</p></li><li><p>three four</p></li></ol>",
				Joiner.on("\n").join(lines));
	}
	
	private void assertSimpleBulletedList(String delimiter) {
		List<String> lines = ImCollections.newList(delimiter + " one", delimiter + " two", delimiter + " three four");
		assertContent("<ul><li>one</li><li>two</li><li>three four</li></ul>", Joiner.on("\n").join(lines));
	}
	
	private void assertCanStart(boolean expected, String string) {
		assertEquals(expected, new ListBlock().canStart(LineSequence.create(string)));
	}
	
}
