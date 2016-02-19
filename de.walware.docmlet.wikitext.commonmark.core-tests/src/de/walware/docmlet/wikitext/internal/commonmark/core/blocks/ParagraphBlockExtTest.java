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

package de.walware.docmlet.wikitext.internal.commonmark.core.blocks;

import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkAsserts.assertContent;

import org.junit.Test;

import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkConfig;


public class ParagraphBlockExtTest {
	
	
	public ParagraphBlockExtTest() {
	}
	
	
	@Test
	public void blank_before_header() {
		String input= "first para\n# second line\n";
		CommonmarkConfig config= new CommonmarkConfig();
		config.setHeaderInterruptParagraphDisabled(true);
		
		assertContent("<p>first para</p><h1>second line</h1>", input);
		assertContent("<p>first para # second line</p>", input, config);
	}
	
	@Test
	public void blank_before_blockquote() {
		String input= "first para\n> second line\n";
		CommonmarkConfig config= new CommonmarkConfig();
		config.setBlockquoteInterruptParagraphDisabled(true);
		assertContent("<p>first para</p><blockquote><p>second line</p></blockquote>", input);
		assertContent("<p>first para &gt; second line</p>", input, config);
	}
	
}
