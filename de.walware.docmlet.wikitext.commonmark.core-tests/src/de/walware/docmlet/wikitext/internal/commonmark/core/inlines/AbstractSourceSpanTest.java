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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.StringWriter;
import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;

import de.walware.jcommons.collections.ImCollections;

import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkAsserts;
import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Cursor;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;
import de.walware.docmlet.wikitext.internal.commonmark.core.TextSegment;
import de.walware.docmlet.wikitext.internal.commonmark.core.spec.SimplifiedHtmlDocumentBuilder;


abstract class AbstractSourceSpanTest {
	
	
	protected SourceSpan span;
	
	
	public AbstractSourceSpanTest(SourceSpan span) {
		this.span = checkNotNull(span);
	}
	
	public AbstractSourceSpanTest() {
	}
	
	
	public void assertNoInline(Cursor cursor) {
		assertNoInline(cursor, 0);
	}
	
	public void assertNoInline(Cursor cursor, int offset) {
		ProcessingContext context= CommonmarkAsserts.newContext();
		Inline inline = span.createInline(context, cursor);
		assertNull(inline);
		assertEquals(offset, cursor.getOffset());
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Inline> T assertInline(Class<T> type, int offset, int length, Cursor cursor) {
		ProcessingContext context= CommonmarkAsserts.newContext();
		Inline inline = span.createInline(context, cursor);
		assertNotNull(inline);
		assertEquals(type, inline.getClass());
		assertEquals(offset, inline.getOffset());
		assertEquals(length, inline.getLength());
		
		return (T) inline;
	}
	
	public <T extends Inline> void assertInline(Class<T> type, int offset, int length, String expectedHtml, Cursor cursor) {
		ProcessingContext context= CommonmarkAsserts.newContext();
		Inline inline = span.createInline(context, cursor);
		assertNotNull(inline);
		assertEquals(type, inline.getClass());
		assertEquals(offset, inline.getOffset());
		assertEquals(length, inline.getLength());
		
		String html= emitToHtml(context, ImCollections.newList(inline));
		assertEquals(expectedHtml, html);
	}
	
	public void assertParseToHtml(String expected, String markup) {
		InlineParser parser = new InlineParser(ImCollections.newList(
				span, new AllCharactersSpan() ));
		ProcessingContext context= CommonmarkAsserts.newContext();
		List<Inline> inlines = parser.parse(context,
				new TextSegment(ImCollections.newList(new Line(1, 0, 0, markup, ""))), true);
		
		String html= emitToHtml(context, inlines);
		
		assertEquals(expected, html);
	}
	
	public String emitToHtml(ProcessingContext context, List<Inline> inlines) {
		context.setMode(ProcessingContext.EMIT_DOCUMENT);
		
		StringWriter writer = new StringWriter();
		
		HtmlDocumentBuilder builder = new SimplifiedHtmlDocumentBuilder(writer);
		builder.setEmitAsDocument(false);
		
		final CommonmarkLocator locator= new CommonmarkLocator();
		builder.setLocator(locator);
		
		builder.beginDocument();
		
		for (Inline inline : inlines) {
			inline.emit(context, locator, builder);
		}
		
		builder.endDocument();
		
		return writer.toString();
	}
	
}
