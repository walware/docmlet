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

package de.walware.docmlet.wikitext.internal.commonmark.core.blocks;

import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkAsserts.assertCanStart;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkAsserts.assertCannotStart;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkAsserts.assertContent;
import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.NoOpDocumentBuilder;
import org.junit.Test;

import de.walware.docmlet.wikitext.commonmark.core.CommonmarkLanguage;


public class SetextHeaderBlockTest {
	
	
	private final SetextHeaderBlock block = new SetextHeaderBlock();
	
	
	@Test
	public void canStart() {
		assertCanStart(block, "Heading\n-");
		assertCanStart(block, "Heading\n=");
		assertCanStart(block, "Heading\n  =");
		assertCanStart(block, "Heading\n   =");
		assertCannotStart(block, "Heading\n    =");
		assertCanStart(block, "Heading\n=====");
		assertCanStart(block, "Heading Text\n-----");
		assertCannotStart(block, "Heading\n\n=====");
		assertCanStart(block, "   Heading\n=====");
		assertCannotStart(block, "    Heading\n=====");
		
		// Bug 472404:
		assertCannotStart(block, "\tHeading\n=");
		assertCannotStart(block, "Heading\n\t=");
		assertCannotStart(block, "Heading\n=-");
	}
	
	@Test
	public void process() {
		assertContent("<h2 id=\"heading-text\">Heading Text</h2>", "Heading Text\n-------",
				CommonmarkLanguage.MYLYN_COMPAT_MODE );
		assertContent("<h1 id=\"heading-text\">Heading Text</h1>", "Heading Text\n=",
				CommonmarkLanguage.MYLYN_COMPAT_MODE );
		assertContent("<h1 id=\"heading-text\">Heading Text</h1>", "Heading Text\n====",
				CommonmarkLanguage.MYLYN_COMPAT_MODE );
		assertContent("<h1 id=\"heading-text\">Heading <em>Text</em></h1>", "Heading *Text*\n====",
				CommonmarkLanguage.MYLYN_COMPAT_MODE );
		
		// Bug 472404 (remove of indent from content):
		new MarkupParser(new CommonmarkLanguage(), new NoOpDocumentBuilder() {
			
			@Override
			public void characters(String text) {
				assertEquals("Heading Text", text);
			}
			
		}).parse("   Heading Text\n-------");
	}
	
}
