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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class LineSequenceTest {
	
	@Rule
	public final ExpectedException thrown = ExpectedException.none();
	
	
	public LineSequenceTest() {
	}
	
	
	@Test
	public void createRequiresContent() {
		thrown.expect(NullPointerException.class);
		LineSequence.create((String) null);
	}
	
	@Test
	public void empty() {
		assertOneLine("", LineSequence.create(""));
	}
	
	@Test
	public void oneLine() {
		assertOneLine("a", LineSequence.create("a"));
	}
	
	@Test
	public void twoLines() {
		assertTwoLines(LineSequence.create("abc\r\ndefg"));
	}
	
	
	@Test
	public void advance() {
		assertAdvance(LineSequence.create("one"));
	}
	
	@Test
	public void lookAhead() {
		assertLookAhead(LineSequence.create("a\nb\nc"));
	}
	
	@Test
	public void lookAheadFailsFast() {
		assertLookAheadFailsFast(LineSequence.create("a\nb\nc"));
	}
	
	private void assertLookAheadFailsFast(LineSequence lineSequence) {
		LineSequence lookAhead = lineSequence.lookAhead();
		lineSequence.advance();
		thrown.expect(IllegalStateException.class);
		lookAhead.advance();
	}
	
	private void assertAdvance(LineSequence lineSequence) {
		lineSequence.advance();
		assertNoLinesRemain(lineSequence);
		lineSequence.advance();
		assertNoLinesRemain(lineSequence);
		lineSequence.advance();
		assertNoLinesRemain(lineSequence);
	}
	
	private void assertNoLinesRemain(LineSequence lineSequence) {
		assertNull(lineSequence.getCurrentLine());
		assertNull(lineSequence.getNextLine());
	}
	
	private void assertLookAhead(LineSequence lineSequence) {
		lineSequence.advance();
		assertEquals("b", lineSequence.getCurrentLine().getText());
		LineSequence lookAhead = lineSequence.lookAhead();
		assertEquals(lineSequence.getCurrentLine(), lookAhead.getCurrentLine());
		lookAhead.advance();
		assertEquals("b", lineSequence.getCurrentLine().getText());
		assertEquals("c", lookAhead.getCurrentLine().getText());
		LineSequence lookAhead2 = lookAhead.lookAhead();
		assertNotNull(lookAhead2);
		assertNotSame(lookAhead, lookAhead2);
		assertNotSame(lookAhead2, lookAhead.lookAhead());
		lookAhead.advance();
		assertEquals("c", lookAhead2.getCurrentLine().getText());
		assertNoLinesRemain(lookAhead);
		assertNoLinesRemain(lookAhead.lookAhead());
		assertEquals("b", lineSequence.getCurrentLine().getText());
		lineSequence.advance();
		assertEquals("c", lineSequence.getCurrentLine().getText());
	}
	
	private void assertTwoLines(LineSequence lineSequence) {
		Line currentLine = lineSequence.getCurrentLine();
		assertNotNull(currentLine);
		assertEquals("abc", currentLine.getText());
		assertEquals(0, currentLine.getOffset());
		assertEquals(0, currentLine.getLineNumber());
		assertSame(currentLine, lineSequence.getCurrentLine());
		Line nextLine = lineSequence.getNextLine();
		assertNotNull(nextLine);
		assertEquals("defg", nextLine.getText());
		assertEquals(5, nextLine.getOffset());
		assertEquals(1, nextLine.getLineNumber());
		assertSame(nextLine, lineSequence.getNextLine());
		
		lineSequence.advance();
		
		assertNotSame(currentLine, lineSequence.getCurrentLine());
		assertNotNull(lineSequence.getCurrentLine());
		assertEquals("defg", lineSequence.getCurrentLine().getText());
		assertNull(lineSequence.getNextLine());
		
		lineSequence.advance();
		
		assertNoLinesRemain(lineSequence);
	}
	
	private void assertOneLine(String line1, LineSequence lineSequence) {
		Line currentLine = lineSequence.getCurrentLine();
		assertNotNull(currentLine);
		assertEquals(line1, currentLine.getText());
		assertSame(currentLine, lineSequence.getCurrentLine());
		assertNull(lineSequence.getNextLine());
		lineSequence.advance();
		assertNoLinesRemain(lineSequence);
	}
	
}
