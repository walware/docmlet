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

package de.walware.docmlet.wikitext.internal.commonmark.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Strings;


public class LineTest {
	
	@Rule
	public final ExpectedException thrown = ExpectedException.none();
	
	
	public LineTest() {
	}
	
	
	@Test
	public void requiresText() {
		this.thrown.expect(NullPointerException.class);
		new Line(0, 0, 0, null, "\n");
	}
	
	@Test
	public void requiresDelimiter() {
		this.thrown.expect(NullPointerException.class);
		new Line(0, 0, 0, "", null);
	}
	
	@Test
	public void requiresNonNegativeLineOffset() {
		this.thrown.expect(IllegalArgumentException.class);
		new Line(1, -1, 0, "test", "\n");
	}
	
	@Test
	public void requiresNonNegativeLineNumber() {
		this.thrown.expect(IllegalArgumentException.class);
		new Line(-1, 1, 0, "test", "\n");
	}
	
	@Test
	public void isBlank() {
		assertBlank("");
		assertBlank("\t");
		assertBlank("   ");
		assertBlank("      ");
		assertBlank("   \t");
		assertNotBlank("a");
		assertNotBlank(" a");
		assertNotBlank("a ");
	}
	
	@Test
	public void getText() {
		assertEquals("abc", new Line(1, 0, 0, "abc", "\n").getText());
	}
	
	@Test
	public void getLineNumber() {
		assertEquals(0, new Line(0, 1, 0, "abc", "\n").getLineNumber());
		assertEquals(1, new Line(1, 1, 0, "abc", "\n").getLineNumber());
	}
	
	@Test
	public void getOffset() {
		assertEquals(0, new Line(1, 0, 0, "abc", "\n").getOffset());
		assertEquals(1, new Line(1, 1, 0, "abc", "\n").getOffset());
	}
	
	@Test
	public void getColumn() {
		final Line line = new Line(2, 15, 0, "  \t0123456789", "\n");
		assertEquals(0, line.getColumn(0));
		assertEquals(1, line.getColumn(1));
		assertEquals(2, line.getColumn(2));
		assertEquals(4, line.getColumn(3));
		assertEquals(5, line.getColumn(4));
	}
	
	@Test
	public void toStringTest() {
		assertEquals("Line (lineNumber= 1, offset= 15, column= 0)\n\ttext= 1",
				new Line(1, 15, 0, "1", "\n").toString() );
		assertEquals("Line (lineNumber= 2, offset= 0, column= 3)\n\ttext= \\tabc",
				new Line(2, 0, 3, "\tabc", "\n").toString() );
		assertEquals("Line (lineNumber= 0, offset= 0, column= 0)\n\ttext= aaaaaaaaaaaaaaaaaaaa...",
				new Line(0, 0, 0, Strings.repeat("a", 100), "\n").toString() );
	}
	
	@Test
	public void segment() {
		final Line segment = new Line(2, 15, 0, "0123456789", "\n").segment(3, 5);
		assertNotNull(segment);
		assertEquals(2, segment.getLineNumber());
		assertEquals(15 + 3, segment.getOffset());
		assertEquals("34567", segment.getText());
		assertEquals(3, segment.getColumn());
	}
	
	
	@Test
	public void toLocator() {
		final CommonmarkLocator locator= new CommonmarkLocator();
		final Line line = new Line(2, 15, 0, "0123456789", "\n");
		locator.setLine(line);
		assertNotNull(locator);
		assertEquals(3, locator.getLineNumber());
		assertEquals(15, locator.getLineDocumentOffset());
	}
	
	
	private void assertNotBlank(final String string) {
		assertFalse(string, new Line(0, 0, 0, string, "\n").isBlank());
	}
	
	private void assertBlank(final String string) {
		assertTrue(string, new Line(0, 0, 0, string, "\n").isBlank());
	}
	
}
