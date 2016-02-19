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

import de.walware.docmlet.wikitext.commonmark.core.CommonmarkLanguage;


public class AtxHeaderBlockTest {
	
	
	AtxHeaderBlock block = new AtxHeaderBlock();
	
	
	@Test
	public void canStart() {
		assertCannotStart(block, "");
		assertCanStart(block, "#");
		assertCanStart(block, "# ");
		assertCanStart(block, "# #");
		assertCanStart(block, "# Y");
		assertCanStart(block, "# Y #");
		assertCanStart(block, "## Y");
		assertCanStart(block, "### Y");
		assertCanStart(block, "#### Y");
		assertCanStart(block, "##### Y");
		assertCanStart(block, "###### Y");
		assertCannotStart(block, "####### Y");
		assertCanStart(block, "# Y#");
		assertCannotStart(block, "#Y");
		
		// Bug 472386:
		assertCanStart(block, "# #Y");
		assertCanStart(block, "   # Y");
		assertCannotStart(block, "\t# Y");
	}
	
	@Test
	public void basic() {
		assertContent("<h2 id=\"one-two\">One Two</h2>", "## One Two",
				CommonmarkLanguage.MYLYN_COMPAT_MODE );
		assertContent("<h2 id=\"one-two\">One Two</h2>", "## One Two #####   ",
				CommonmarkLanguage.MYLYN_COMPAT_MODE );
		assertContent("<h2 id=\"one-two\">One Two#</h2>", "## One Two#",
				CommonmarkLanguage.MYLYN_COMPAT_MODE );
		assertContent("<h2 id=\"one-two\">#One #Two</h2>", "## #One #Two",
				CommonmarkLanguage.MYLYN_COMPAT_MODE );
		assertContent("<p>One</p><h1 id=\"two\">two</h1><p>Three</p>", "One\n# two\nThree",
				CommonmarkLanguage.MYLYN_COMPAT_MODE );
		assertContent("<h2></h2>", "##");
		assertContent("<h2></h2>", "## ##");
	}
	
	@Test
	public void withNestedInlines() {
		assertContent("<h2 id=\"one-two-three\">One <em>Two</em> \\<strong>three</strong></h2>",
				"## One *Two* \\\\__three__ ##",
				CommonmarkLanguage.MYLYN_COMPAT_MODE );
	}
	
}
