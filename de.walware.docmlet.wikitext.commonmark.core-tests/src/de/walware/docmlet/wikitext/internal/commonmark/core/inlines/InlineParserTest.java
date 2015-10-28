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

import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.walware.jcommons.collections.ImCollections;

import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkAsserts;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.LineSequence;
import de.walware.docmlet.wikitext.internal.commonmark.core.TextSegment;


public class InlineParserTest {
	
	@Rule
	public final ExpectedException thrown = ExpectedException.none();
	
	
	private final Line line = new Line(0, 1, 0, "test", "\n");
	
	
	public InlineParserTest() {
	}
	
	
	@Test
	public void requiresSpans() {
		thrown.expect(NullPointerException.class);
		new InlineParser(null);
	}
	
	@Test
	public void parse() {
		assertParse("");
		assertParse("one\ntwo",
				new Characters(line, 0, 3, 3, "one"),
				new SoftLineBreak(line, 3, 1, 1),
				new Characters(line, 4, 3, 3, "two") );
		assertParse("one\ntwo three",
				new Characters(line, 0, 3, 3, "one"),
				new SoftLineBreak(line, 3, 1, 1),
				new Characters(line, 4, 9, 9, "two three") );
	}
	
	@Test
	public void toStringContent() {
		InlineParser parser = new InlineParser(ImCollections.newList(
				new CodeSpan(), new AllCharactersSpan() ));
		String stringContent = parser.toStringContent(CommonmarkAsserts.newContext(),
				new TextSegment(ImCollections.newList(new Line(1, 0, 0, "one `two` three", "\n"))));
		assertEquals("one two three", stringContent);
	}
	
	private void assertParse(String content, Inline... inlines) {
		List<Inline> expected = Arrays.asList(inlines);
		List<Inline> actual = createInlines().parse(CommonmarkAsserts.newContext(),
				new TextSegment(LineSequence.create(content)), true );
		for (int x = 0; x < expected.size() && x < actual.size(); ++x) {
			assertEquivalent(x, expected.get(x), actual.get(x));
		}
		assertEquals(expected, actual);
	}
	
	private void assertEquivalent(int index, Inline expected, Inline actual) {
		String message = "inline at " + index;
		assertEquals(message + " type", expected.getClass(), actual.getClass());
		assertEquals(message + " offset", expected.getOffset(), actual.getOffset());
		assertEquals(message + " length", expected.getLength(), actual.getLength());
		assertEquals(message, expected, actual);
	}
	
	private InlineParser createInlines() {
		return new InlineParser(ImCollections.newList(
				new LineBreakSpan(), new StringCharactersSpan(), new AllCharactersSpan() ));
	}
	
}
