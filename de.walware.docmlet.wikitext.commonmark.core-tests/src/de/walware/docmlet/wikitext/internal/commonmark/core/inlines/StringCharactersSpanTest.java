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

package de.walware.docmlet.wikitext.internal.commonmark.core.inlines;

import static de.walware.docmlet.wikitext.internal.commonmark.core.inlines.Cursors.createCursor;

import org.junit.Test;


public class StringCharactersSpanTest extends AbstractSourceSpanTest {
	
	
	public StringCharactersSpanTest() {
		super(new StringCharactersSpan());
	}
	
	
	@Test
	public void createInline() {
		assertInline(Characters.class, 0, 1, createCursor("``one"));
		assertInline(Characters.class, 0, 1, createCursor("__two"));
		assertInline(Characters.class, 0, 1, createCursor("***three"));
		assertInline(Characters.class, 0, 3, createCursor("one`"));
		assertInline(Characters.class, 0, 3, createCursor("one\ntwo"));
		assertInline(Characters.class, 1, 3, createCursor(" one *two"));
		assertInline(Characters.class, 1, 7, createCursor(" one two *three"));
		assertInline(Characters.class, 1, 7, createCursor(" one two \\[ab"));
		assertInline(Characters.class, 1, 7, createCursor(" one two !"));
		assertInline(Characters.class, 1, 7, createCursor(" one two <"));
	}
	
}
