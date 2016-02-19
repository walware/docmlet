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

package de.walware.docmlet.wikitext.commonmark.core;

import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkAsserts.assertContent;
import static java.text.MessageFormat.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.EventDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.BeginBlockEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.BeginDocumentEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.CharactersEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.DocumentBuilderEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.EndBlockEvent;
import org.eclipse.mylyn.wikitext.core.parser.builder.event.EndDocumentEvent;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Joiner;


public class CommonmarkLanguageTest {
	
	@Rule
	public final ExpectedException thrown = ExpectedException.none();
	
	
	private final CommonmarkLanguage language = new CommonmarkLanguage();
	
	
	public CommonmarkLanguageTest() {
	}
	
	
	@Test
	public void name() {
		assertEquals("CommonMark\u2002[StatET]", language.getName());
	}
	
	@Test
	public void processEmpty() {
		assertEvents("", new BeginDocumentEvent(), new EndDocumentEvent());
	}
	
	@Test
	public void processBlankLines() {
		assertEvents("\n\n\n\n\n", new BeginDocumentEvent(), new EndDocumentEvent());
	}
	
	@Test
	public void processDocumentFalse() {
		assertEvents("", false);
	}
	
	@Test
	public void processSimple() {
		assertEvents("first line\nsecond line\n\nnext para", new BeginDocumentEvent(),
				new BeginBlockEvent(BlockType.PARAGRAPH, new Attributes()), new CharactersEvent("first line"),
				new CharactersEvent("\n"), new CharactersEvent("second line"), new EndBlockEvent(),
				new BeginBlockEvent(BlockType.PARAGRAPH, new Attributes()), new CharactersEvent("next para"),
				new EndBlockEvent(), new EndDocumentEvent());
	}
	
//	@Test
//	public void isDiscoverable() {
//		MarkupLanguage markupLanguage = OsgiServiceLocator.getApplicableInstance().getMarkupLanguage("CommonMark");
//		assertNotNull(markupLanguage);
//		assertEquals(CommonmarkLanguage.class, markupLanguage.getClass());
//	}
	
	@Test
	public void modeStrictlyConforming() {
		final int mode= 0;
		CommonmarkLanguage language= this.language;
		assertEquals(mode, language.getMode());
		assertEquals(mode, language.clone().getMode());
		assertNull(language.getIdGenerationStrategy());
		assertContent(language, "<p>one (http://example.com/#hey) two</p>", "one (http://example.com/#hey) two");
		assertContent(language, "<h1>A Heading</h1>", "# A Heading");
	}
	
	@Test
	public void modeCompat() {
		final int mode= CommonmarkLanguage.MYLYN_COMPAT_MODE | CommonmarkLanguage.MARKDOWN_COMPAT_MODE;
		CommonmarkLanguage language= this.language.clone(null, mode);
		assertEquals(mode, language.getMode());
		assertEquals(mode, language.clone().getMode());
		assertNotNull(language.getIdGenerationStrategy());
		assertContent(language, "<p>one (<a href=\"http://example.com/#hey\">http://example.com/#hey</a>) two</p>",
				"one (http://example.com/#hey) two");
		assertContent(language, "<h1 id=\"a-heading\">A Heading</h1>", "# A Heading");
	}
	
	@Test
	public void cloneTest() {
		CommonmarkLanguage language = new CommonmarkLanguage();
		assertNotNull(language.clone());
		assertEquals(language.getName(), language.clone().getName());
		assertEquals(language.getMode(), language.clone().getMode());
	}
	
	@Test
	public void linksWithHash() {
		assertContent("<p><a href=\"#FooBar\">text</a></p>", "[text](#FooBar)");
		assertContent("<p><a href=\"A#FooBar\">text</a></p>", "[text](A#FooBar)");
		assertContent("<p><a href=\"http://example.com/page.html#someId\">text</a></p>",
				"[text](http://example.com/page.html#someId)");
	}
	
	private void assertEvents(String content, DocumentBuilderEvent... events) {
		assertEvents(content, true, events);
	}
	
	private void assertEvents(String content, boolean asDocument, DocumentBuilderEvent... events) {
		MarkupParser parser = new MarkupParser(language);
		EventDocumentBuilder builder = new EventDocumentBuilder();
		parser.setBuilder(builder);
		parser.parse(content, asDocument);
		List<DocumentBuilderEvent> expectedEvents = Arrays.asList(events);
		List<DocumentBuilderEvent> actualEvents = builder.getDocumentBuilderEvents().getEvents();
		assertEquals(format("Expected {0} but got {1}", toMessage(expectedEvents), toMessage(actualEvents)),
				expectedEvents, actualEvents);
	}
	
	private String toMessage(List<DocumentBuilderEvent> expectedEvents) {
		return Joiner.on(",\n").join(expectedEvents);
	}
	
}
