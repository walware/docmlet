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

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;

import de.walware.docmlet.wikitext.core.source.EmbeddingAttributes;
import de.walware.docmlet.wikitext.core.source.extdoc.IExtdocMarkupLanguage;
import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;


public class HtmlTag extends InlineWithText {
	
	
	public HtmlTag(final Line line, final int offset, final int length, final String content) {
		super(line, offset, length, content.length(), content);
	}
	
	
	@Override
	public void emit(final ProcessingContext context,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		if (context.getMode() == ProcessingContext.PARSE_SOURCE_STRUCT) {
			final int descr= (getText().startsWith("<!--")) ?
					IExtdocMarkupLanguage.EMBEDDED_HTML_COMMENT_INLINE_DESCR :
					IExtdocMarkupLanguage.EMBEDDED_HTML_OTHER_INLINE_DESCR;
			
			builder.beginSpan(SpanType.CODE, new EmbeddingAttributes(
					IExtdocMarkupLanguage.EMBEDDED_HTML, descr,
					getOffset(), getOffset() + getLength() ));
		}
		
		builder.charactersUnescaped(getText());
		
		if (context.getMode() == ProcessingContext.PARSE_SOURCE_STRUCT) {
			builder.endSpan();
		}
	}
	
}
