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

import org.junit.Test;

import de.walware.ecommons.collections.ImCollections;


public class ParagraphBlockTest {
	
	
	private final ParagraphBlock block = new ParagraphBlock();
	
	
	public ParagraphBlockTest() {
	}
	
	
	@Test
	public void canStart() {
		assertCannotStart(block, "");
		assertCannotStart(block, "\none");
		assertCanStart(block, "one");
	}
	
	@Test
	public void assertParagraph() {
		assertContent("<p>first para second line</p><p>second para fourth line here</p>",
				"first para\nsecond line\n\nsecond para\nfourth line here\n\n\n");
		assertContent("<p>first para second line</p>", "first para\n    second line");
	}
	
	@Test
	public void paragraphNewlines() {
		for (String newline : ImCollections.newList("\n", "\r", "\r\n")) {
			assertContent("<p>p1 first p1 second p1 third</p><p>p2 first</p>",
					"p1 first" + newline + "p1 second" + newline + "p1 third" + newline + newline + "p2 first");
		}
	}
	
}
