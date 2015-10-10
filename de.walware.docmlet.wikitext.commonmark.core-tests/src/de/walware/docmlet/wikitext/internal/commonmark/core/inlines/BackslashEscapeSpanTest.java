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

package de.walware.docmlet.wikitext.internal.commonmark.core.inlines;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.walware.docmlet.wikitext.internal.commonmark.core.Cursor;


public class BackslashEscapeSpanTest extends AbstractSourceSpanTest {
	
	
	public BackslashEscapeSpanTest() {
		super(new BackslashEscapeSpan());
	}
	
	
	@Test
	public void backslashEscapedChar() {
		assertNoInline(Cursors.createCursor("\\"));
		assertNoInline(Cursors.createCursor("\\a"));
		assertEscapedCharacter('\\', 0, 2, Cursors.createCursor("\\\\"));
		assertEscapedCharacter('*', 0, 2, Cursors.createCursor("\\*"));
		assertEscapedCharacter('_', 0, 2, Cursors.createCursor("\\_*"));
	}
	
	@Test
	public void backslashEscapedLinebreak() {
		assertHardLineBreak('_', 0, 2, Cursors.createCursor("\\\nabc"));
		assertHardLineBreak('_', 0, 2, Cursors.createCursor("\\\rabc"));
		assertHardLineBreak('_', 0, 3, Cursors.createCursor("\\\r\nabc"));
	}
	
	private void assertEscapedCharacter(char ch, int offset, int length, Cursor cursor) {
		EscapedCharacter escapedCharacter = assertInline(EscapedCharacter.class, offset, length, cursor);
		assertEquals(ch, escapedCharacter.getCharacter());
	}
	
	private void assertHardLineBreak(char ch, int offset, int length, Cursor cursor) {
		HardLineBreak escapedCharacter = assertInline(HardLineBreak.class, offset, length, cursor);
	}
	
}
