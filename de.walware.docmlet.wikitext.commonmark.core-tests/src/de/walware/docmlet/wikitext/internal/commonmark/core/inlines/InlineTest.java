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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.junit.Test;

import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkAsserts;
import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;


public class InlineTest {
	
	
	public InlineTest() {
	}
	
	
	@Test
	public void create() {
		Line line = new Line(3, 5, 0, "text", "\n");
		Inline inline = new Inline(line, 8, 3, 3) {
			
			@Override
			public void emit(ProcessingContext context, CommonmarkLocator locator, DocumentBuilder builder) {
			}
			
		};
		assertSame(line, inline.getLine());
		assertEquals(8, inline.getOffset());
		assertEquals(3, inline.getLength());
		
		CommonmarkLocator locator= new CommonmarkLocator();
		locator.setInline(inline);
		assertEquals(3, locator.getLineCharacterOffset());
		assertEquals(6, locator.getLineSegmentEndOffset());
		assertEquals(8, locator.getDocumentOffset());
		assertEquals(line.getOffset(), locator.getLineDocumentOffset());
		assertEquals(line.getText().length(), locator.getLineLength());
		assertEquals(line.getLineNumber() + 1, locator.getLineNumber());
	}
	
	@Test
	public void createContext() {
		ProcessingContext context= CommonmarkAsserts.newContext();
		new Inline(new Line(1, 2, 0, "text", "\n"), 0, 1, 1) {
			
			@Override
			public void emit(ProcessingContext context, CommonmarkLocator locator, DocumentBuilder builder) {
			}
			
		}.createContext(context);
		assertTrue(!context.hasNamedUri());
	}
	
}
