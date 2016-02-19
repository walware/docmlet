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

import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkAsserts.assertCanStart;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkAsserts.assertCannotStart;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkAsserts.assertContent;

import org.junit.Test;


public class FencedCodeBlockTest {
	
	
	private final FencedCodeBlock block = new FencedCodeBlock();
	
	
	public FencedCodeBlockTest() {
	}
	
	
	@Test
	public void canStart() {
		assertCanStart(block, "```");
		assertCanStart(block, " ```");
		assertCanStart(block, "  ```");
		assertCanStart(block, "   ```");
		assertCanStart(block, "````````````````");
		assertCannotStart(block, "    ```");
		assertCanStart(block, "~~~");
		assertCanStart(block, " ~~~");
		assertCanStart(block, "  ~~~");
		assertCanStart(block, "   ~~~");
		assertCannotStart(block, "    ~~~");
		assertCanStart(block, "~~~~~~~~~~~~~~~~~");
	}
	
	@Test
	public void canStartWithInfoText() {
		assertCanStart(block, "```````````````` some info text");
		assertCanStart(block, "~~~~~~~~~ some info text");
		assertCannotStart(block, "``` one ``");
	}
	
	@Test
	public void basic() {
		assertContent(
				"<p>first para</p><pre><code class=\"language-java\">public void foo() {\n\n}\n</code></pre><p>text</p>",
				"first para\n\n```` java and stuff\npublic void foo() {\n\n}\n````\ntext");
	}
	
	@Test
	public void encodedCharacters() {
		assertContent("<pre><code>&lt;\n &gt;\n</code></pre>", "```\n<\n >\n```");
	}
	
	@Test
	public void infoString() {
		assertContent("<pre class=\"language-info\"><code class=\"language-info\">code here\n</code></pre>",
				"``` info\ncode here\n```");
	}
	
	@Test
	public void restString() {
		assertContent("<pre class=\"language-info\"><code class=\"language-info\">code here\n</code></pre>",
				"``` info arg=1\ncode here\n```");
	}
	
}
