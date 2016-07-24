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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.LineSequence;
import de.walware.docmlet.wikitext.internal.commonmark.core.TextSegment;


public class PotentialBracketCloseDelimiterTest {
	
	private final Line line = new Line(0, 1, 0, "test", "\n");
	
	private final PotentialBracketRegex shared= new PotentialBracketRegex();
	
	
	public PotentialBracketCloseDelimiterTest() {
	}
	
	
	@Test
	public void bracketUriPart() {
		Pattern pattern = Pattern.compile(PotentialBracketRegex.BRACKETS_URI_REGEX);
		assertTrue(pattern.matcher("<one+two>").matches());
		assertTrue(pattern.matcher("<>").matches());
		assertFalse(pattern.matcher("<one two").matches());
		assertFalse(pattern.matcher("<one+two").matches());
		assertFalse(pattern.matcher("<one+two>>").matches());
		Matcher matcher = pattern.matcher("<one+two\\>>");
		assertTrue(matcher.matches());
		assertEquals("one+two\\>", matcher.group(1));
	}
	
	@Test
	public void nobracketUriPart() {
		Pattern pattern = Pattern.compile(PotentialBracketRegex.NOBRACKET_URI_REGEX);
		assertTrue(pattern.matcher("onetwo").matches());
		assertFalse(pattern.matcher("one two").matches());
		assertFalse(pattern.matcher("one(two(three))").matches());
		assertTrue(pattern.matcher("/one/two(threefour\\))").matches());
		
		assertMatch(1, "/one/two(three\\))", pattern, "/one/two(three\\))");
		assertMatch(1, "foo(and\\(bar\\))", pattern, "foo(and\\(bar\\))");
		assertMatch(1, "foo\\)\\:", pattern, "foo\\)\\:");
	}
	
	@Test
	public void uriPart() {
		Pattern pattern = Pattern.compile(PotentialBracketRegex.URI_M2_REGEX);
		assertMatch(2, "/one/two", pattern, "/one/two");
		assertMatch(2, "/one/(two)", pattern, "/one/(two)");
		assertMatch(1, "/one/two", pattern, "</one/two>");
		assertMatch(2, "foo(and\\(bar\\))", pattern, "foo(and\\(bar\\))");
		assertMatch(2, "foo\\)\\:", pattern, "foo\\)\\:");
	}
	
	@Test
	public void titlePart() {
		Pattern pattern = Pattern.compile("(" + PotentialBracketRegex.TITLE_REGEX + ")", Pattern.DOTALL);
		assertMatch(pattern, "\"one two ('\\\" three\"");
		assertMatch(pattern, "'one two \\\"\\' three'");
		assertMatch(pattern, "(one two \"\\'\\) three)");
		assertMatch(pattern, "(one two (three (four\\)\\))");
		assertFalse(pattern.matcher("\"one").matches());
		assertFalse(pattern.matcher("one\"").matches());
		assertFalse(pattern.matcher("one'").matches());
		assertFalse(pattern.matcher("'one").matches());
		assertFalse(pattern.matcher("(one").matches());
		assertFalse(pattern.matcher("one)").matches());
	}
	
	@Test
	public void linkEndPattern() {
		Matcher matcher = shared.getEndMatcher();
		matcher.reset("(/uri \"a title\")");
		assertTrue(matcher.matches());
		assertEquals("/uri", matcher.group(2));
		assertEquals("\"a title\"", matcher.group(3));
		
		matcher.reset("(</uri+to-here> (one two (three (four\\)\\)))");
		assertTrue(matcher.matches());
		assertEquals("/uri+to-here", matcher.group(1));
		assertEquals("(one two (three (four\\)\\))", matcher.group(3));
		
		assertMatch(2, "one(two\\(three\\))", matcher, "(one(two\\(three\\)))");
		assertMatch(2, "foo\\)\\:", matcher, "(foo\\)\\:)");
	}
	
	@Test
	public void referenceDefinitionEndPattern() {
		Matcher matcher = shared.getReferenceDefinitionEndMatcher();
		matcher.reset(":\n      /url\n           'the title'");
		assertTrue(matcher.matches());
		assertEquals("/url", matcher.group(2));
		assertEquals("'the title'", matcher.group(3));
	}
	
	@Test
	public void eligibleForReferenceDefinitionImage() {
		List<Inline> inlines= new ArrayList<>();
		PotentialBracketOpenDelimiter openingDelimiter= new PotentialBracketOpenDelimiter(line, 0, 2, "![");
		inlines.add(openingDelimiter);
		PotentialBracketCloseDelimiter delimiter= new PotentialBracketCloseDelimiter(line, 4, shared);
		inlines.add(delimiter);
		assertFalse(delimiter.isEligibleForReferenceDefinition(inlines, openingDelimiter, 0));
	}
	
	@Test
	public void eligibleForReferenceDefinitionLinkStartOfLine() {
		assertTrue(checkIsEligibleForReferenceDefinition("[", 0));
		assertTrue(checkIsEligibleForReferenceDefinition("a\n[", 2));
	}
	
	@Test
	public void eligibleForReferenceDefinitionLinkIndented() {
		assertTrue(checkIsEligibleForReferenceDefinition("a\n [", 3));
		assertTrue(checkIsEligibleForReferenceDefinition("a\n   [", 5));
		assertFalse(checkIsEligibleForReferenceDefinition("a\n    [", 6));
		assertFalse(checkIsEligibleForReferenceDefinition("    [", 4));
		assertTrue(checkIsEligibleForReferenceDefinition("   [", 3));
		assertFalse(checkIsEligibleForReferenceDefinition("a  [", 3));
	}
	
	private boolean checkIsEligibleForReferenceDefinition(String content, int offset) {
		TextSegment textSegment= new TextSegment(LineSequence.create(content));
		Line line= textSegment.getLineAtOffset(offset);
		List<Inline> inlines= new ArrayList<>();
		PotentialBracketOpenDelimiter openingDelimiter= new PotentialBracketOpenDelimiter(line, offset, 1, "[");
		inlines.add(openingDelimiter);
		PotentialBracketCloseDelimiter delimiter= new PotentialBracketCloseDelimiter(line, 10, shared);
		inlines.add(delimiter);
		return delimiter.isEligibleForReferenceDefinition(inlines, openingDelimiter, 0);
	}
	
	private void assertMatch(Pattern pattern, String input) {
		Matcher matcher = pattern.matcher(input);
		assertTrue(matcher.matches());
		assertEquals(input, matcher.group(1));
	}
	
	private void assertMatch(int group, String expected, Pattern pattern, String input) {
		Matcher matcher = pattern.matcher(input);
		assertTrue(matcher.matches());
		assertEquals(expected, matcher.group(group));
	}
	
	private void assertMatch(int group, String expected, Matcher matcher, String input) {
		matcher.reset(input);
		assertTrue(matcher.matches());
		assertEquals(expected, matcher.group(group));
	}
	
}
