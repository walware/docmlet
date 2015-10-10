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


public class IndentedCodeBlockTest {
	
	
	private final IndentedCodeBlock block = new IndentedCodeBlock();
	
	
	public IndentedCodeBlockTest() {
	}
	
	
	@Test
	public void canStart() {
		assertCannotStart(block, "   code");
		assertCannotStart(block, "      ");
		assertCanStart(block, "    code");
		assertCanStart(block, "     code");
		assertCannotStart(block, " code");
		assertCannotStart(block, "  code");
		assertCannotStart(block, "non-blank\n    code");
		assertCanStart(block, "\tcode");
		assertCanStart(block, "\t code");
		assertCanStart(block, " \tcode");
		assertCanStart(block, "  \tcode");
		assertCanStart(block, "   \tcode");
	}
	
	@Test
	public void process() {
		assertContent("<pre><code>code\n</code></pre>", "    code");
		assertContent("<pre><code>code\n</code></pre>", "\tcode");
		assertContent("<pre><code> code\n</code></pre>", "\t code");
		assertContent("<pre><code>code  \n</code></pre>", "\tcode  ");
		assertContent("<pre><code>\tcode\n</code></pre>", "    \tcode");
		assertContent("<pre><code>one\ntwo\n</code></pre><p>three</p>", "    one\n    two\n three");
		assertContent("<pre><code>one\n\nthree\n</code></pre>", "    one\n\n    three");
		assertContent("<pre><code>one\n  \nthree\n</code></pre>", "    one\n      \n    three");
		
		// Bug 472395:
		assertContent("<pre><code>\t\tcode\n</code></pre>", "\t\t\tcode");
	}
	
}
