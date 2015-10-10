/*=============================================================================#
 # Copyright (c) 2015 David Green and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     David Green - initial API and implementation in Mylyn
 #     Stephan Wahlbrink (WalWare.de) - revised API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.commonmark.core.inlines;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.builder.NoOpDocumentBuilder;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.collections.ImList;

import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Cursor;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;
import de.walware.docmlet.wikitext.internal.commonmark.core.TextSegment;


public class InlineParser {
	
	
	private static final CommonmarkLocator NO_OP_LOCATOR= new CommonmarkLocator();
	
	
	public static void emit(final ProcessingContext context, final List<Inline> inlines,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		for (final Inline inline : inlines) {
			locator.setInline(inline);
			inline.emit(context, locator, builder);
		}
	}
	
	public static String toStringContent(final ProcessingContext context, final List<Inline> contents) {
		final StringBuilder stringBuilder= new StringBuilder();
		final DocumentBuilder altDocumentBuilder= new NoOpDocumentBuilder() {
			
			@Override
			public void characters(final String text) {
				stringBuilder.append(text);
			}
			
			@Override
			public void entityReference(final String entity) {
				final String replacement= context.getHelper().resolveHtmlEntity(entity);
				if (replacement != null) {
					stringBuilder.append(replacement);
				}
			}
			
		};
		for (final Inline inline : contents) {
			inline.emit(context, NO_OP_LOCATOR, altDocumentBuilder);
		}
		return stringBuilder.toString();
	}
	
	
	private final ImList<SourceSpan> spans;
	
	
	public InlineParser(final ImList<SourceSpan> spans) {
		this.spans= ImCollections.toList(spans);
	}
	
	
	public void emit(final ProcessingContext context, final TextSegment textSegment,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		final List<Inline> inlines= parse(context, textSegment, false);
		emit(context, inlines, locator, builder);
	}
	
	public String toStringContent(final ProcessingContext context, final TextSegment textSegment) {
		final List<Inline> inlines= parse(context, textSegment, false);
		return toStringContent(context, inlines);
	}
	
	public List<Inline> parse(final ProcessingContext context, final TextSegment segment,
			final boolean inBlock) {
		final Cursor cursor= new Cursor(segment);
		
		final List<Inline> inlines= new ArrayList<>();
		ITER_CHARS: while (cursor.hasChar()) {
			for (final SourceSpan span : this.spans) {
				final Inline inline= span.createInline(context, cursor);
				if (inline != null) {
					inline.apply(context, inlines, cursor, inBlock);
					continue ITER_CHARS;
				}
			}
			throw new IllegalStateException();
		}
		
		return secondPass(inlines);
	}
	
	static List<Inline> secondPass(final List<Inline> inlines) {
		List<Inline> processedInlines= new ArrayList<>(inlines);
		InlinesSubstitution substitution= null;
		do {
			for (final Inline inline : processedInlines) {
				substitution= inline.secondPass(processedInlines);
				if (substitution != null) {
					processedInlines= substitution.apply(processedInlines);
					break;
				}
			}
		} while (substitution != null);
		return processedInlines;
	}
	
}
