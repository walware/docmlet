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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.HtmlParser;
import org.eclipse.mylyn.wikitext.core.parser.IdGenerator;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentHandler;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.util.XmlStreamWriter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.walware.docmlet.wikitext.commonmark.core.CommonmarkLanguage;
import de.walware.docmlet.wikitext.commonmark.core.ICommonmarkConfig;
import de.walware.docmlet.wikitext.internal.commonmark.core.spec.SimplifiedHtmlDocumentBuilder;


public class CommonmarkAsserts {
	
	
	public static ProcessingContext newContext() {
		return new ProcessingContext(Commonmark.newSourceBlocks(), Commonmark.newInlineParser(),
				createCommonmarkIdGenerator(), ProcessingContext.INITIALIZE_CONTEXT );
	}
	
	private static IdGenerator createCommonmarkIdGenerator() {
		IdGenerator idGenerator= new IdGenerator();
		idGenerator.setGenerationStrategy(new CommonmarkIdGenerationStrategy());
		return idGenerator;
	}
	
	
	public static void assertCanStart(SourceBlock block, String input) {
		LineSequence lineSequence= LineSequence.create(input);
		assertTrue(block.canStart(lineSequence, null));
	}
	
	public static void assertCannotStart(SourceBlock block, String input) {
		LineSequence lineSequence= LineSequence.create(input);
		assertFalse(block.canStart(lineSequence, null));
	}
	
	public static void assertContent(String expectedHtml, String input) {
		assertContent(expectedHtml, input, 0);
	}
	
	public static void assertContent(String expectedHtml, String input, int mode) {
		CommonmarkLanguage language= new CommonmarkLanguage(null, mode, null);
		String html = parseToHtml(language, input);
		assertHtmlEquals(expectedHtml, html);
	}
	
	public static void assertContent(String expectedHtml, String input, ICommonmarkConfig config) {
		CommonmarkLanguage language= new CommonmarkLanguage(null, 0, null);
		language.setMarkupConfig(config);
		String html = parseToHtml(language, input);
		assertHtmlEquals(expectedHtml, html);
	}
	
	public static void assertContent(MarkupLanguage language, String expectedHtml, String input) {
		String html = parseToHtml(language, input);
		assertHtmlEquals(expectedHtml, html);
	}
	
	private static void assertHtmlEquals(String expectedHtml, String html) {
		if (expectedHtml.equals(html)) {
			return;
		}
		expectedHtml= expectedHtml.trim();
		html= html.trim();
		if (expectedHtml.equals(html)) {
			return;
		}
		assertEquals(toComparisonValue(expectedHtml), toComparisonValue(html));
	}
	
	private static String toComparisonValue(String html) {
		if (html == null) {
			return null;
		}
		try {
			StringWriter out = new StringWriter();
			DocumentBuilder builder = createDocumentBuilder(out);
			HtmlParser.instance().parse(new InputSource(new StringReader(html)), builder);
			return out.toString();
		} catch (IOException | SAXException e) {
			throw new RuntimeException(html, e);
		}
	}
	
	private static String parseToHtml(MarkupLanguage markupLanguage, String input) {
		StringWriter out = new StringWriter();
		DocumentBuilder builder = createDocumentBuilder(out);
		MarkupParser parser = new MarkupParser(markupLanguage, builder);
		parser.parse(input);
		return out.toString();
	}
	
	private static DocumentBuilder createDocumentBuilder(StringWriter out) {
		SimplifiedHtmlDocumentBuilder builder = new SimplifiedHtmlDocumentBuilder(out);
		builder.setResolveEntityReferences(true);
		builder.setDocumentHandler(new HtmlDocumentHandler() {
			
			@Override
			public void beginDocument(HtmlDocumentBuilder builder, XmlStreamWriter writer) {
			}
			
			@Override
			public void endDocument(HtmlDocumentBuilder builder, XmlStreamWriter writer) {
			}
			
		});
		return builder;
	}
	
}
