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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import com.google.common.base.Strings;


public class TextSegmentTest {
	
	@Rule
	public final ExpectedException thrown = ExpectedException.none();
	
	
	public TextSegmentTest() {
	}
	
	
	@Test
	public void requiresLines() {
		thrown.expect(NullPointerException.class);
		new TextSegment((ImList<Line>) null);
	}
	
	@Test
	public void getText() {
		assertEquals("", new TextSegment(ImCollections.<Line>newList()).getText());
		assertEquals("one\ntwo", new TextSegment(createLines("one\r\ntwo")).getText());
		assertEquals("one\ntwo\nthree", new TextSegment(createLines("one\r\ntwo\nthree")).getText());
	}
	
	@Test
	public void offsetOf() {
		TextSegment segment = new TextSegment(createLines("one\r\ntwo\r\nthree four"));
		String text = segment.getText();
		assertEquals(0, segment.offsetOf(text.indexOf("one")));
		assertEquals(5, segment.offsetOf(text.indexOf("two")));
		assertEquals(3, segment.offsetOf(text.indexOf("two") - 1));
		assertEquals(10, segment.offsetOf(text.indexOf("three")));
		assertEquals(8, segment.offsetOf(text.indexOf("three") - 1));
		assertEquals(16, segment.offsetOf(text.indexOf("four")));
	}
	
	@Test
	public void toTextOffset() {
		TextSegment segment = new TextSegment(ImCollections.newList(
				new Line(1, 10, 0, "abc", "\n"),
				new Line(2, 15, 0, "def", "\n") ));
		assertEquals(0, segment.toTextOffset(10));
		assertEquals(2, segment.toTextOffset(12));
		assertEquals(4, segment.toTextOffset(15));
		assertEquals(6, segment.toTextOffset(17));
		try {
			segment.toTextOffset(19);
			fail("expected exception");
		} catch (IllegalArgumentException e) {
			// expected
		}
		try {
			segment.toTextOffset(9);
			fail("expected exception");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}
	
	@Test
	public void getLineAtOffset() {
		ImList<Line> lines= ImCollections.newList(
				new Line(1, 10, 0, "abc", "\n"),
				new Line(2, 15, 0, "def", "\n") );
		TextSegment segment = new TextSegment(lines);
		assertEquals(lines.get(0), segment.getLineAtOffset(0));
		assertEquals(lines.get(0), segment.getLineAtOffset(3));
		assertEquals(lines.get(1), segment.getLineAtOffset(4));
		assertEquals(lines.get(1), segment.getLineAtOffset(6));
	}
	
	@Test
	public void toStringTest() {
		assertEquals("TextSegment{text=one\\ntwo\\nthree four}",
				new TextSegment(createLines("one\ntwo\r\nthree four")).toString());
		assertEquals("TextSegment{text=01234567890123456789...}",
				new TextSegment(createLines(Strings.repeat("0123456789", 10))).toString());
	}
	
	private LineSequence createLines(String content) {
		return LineSequence.create(content);
	}
	
}
