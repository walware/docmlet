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

import static de.walware.docmlet.wikitext.internal.commonmark.core.inlines.Cursors.createCursor;

import org.junit.Test;


public class HtmlEntitySpanTest extends AbstractSourceSpanTest {
	
	
	public HtmlEntitySpanTest() {
		super(new HtmlEntitySpan());
	}
	
	
	@Test
	public void createInline() {
		assertNoInline(createCursor("one"));
		assertNoInline(createCursor("&copy"));
		assertEntity(6, "&copy;", "&copy; ayyy");
		assertEntity(5, "&xa0;", "&xa0; ayyy;");
		assertEntity(6, "&#160;", "&#160;");
		assertEntity(6, "&nbsp;", "&nbsp;");
		assertEntity(6, "&nbsp;", "&nbsp; ab\ncd");
		assertEntity(5, "&#x9;", "&#x9;");
		assertEntity(5, "&#X9;", "&#X9;");
		assertEntity(7, "&#x912;", "&#x912;");
		assertEntity(4, "\uFFFD", "&#0;");
		assertEntity(5, "\uFFFD", "&#00;");
		assertEntity(8, "&#65536;", "&#65536;");
		assertEntity(5, "\uFFFD", "&#x0;");
		assertEntity(9, "&#xfffff;", "&#xfffff;");
	}

	private void assertEntity(int length, String entity, String content) {
		assertInline(HtmlEntity.class, 0, length, entity, createCursor(content));
	}
	
}
