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

import com.google.common.base.Strings;


public class ThematicBreakBlockTest {
	
	
	private final ThematicBreakBlock block = new ThematicBreakBlock();
	
	
	public ThematicBreakBlockTest() {
	}
	
	
	@Test
	public void canStart() {
		assertCannotStart(block, "");
		assertCannotStart(block, "a");
		assertCannotStart(block, "    ***");
		for (char c : "*_-".toCharArray()) {
			String hrIndicator = Strings.repeat("" + c, 3);
			assertCanStart(block, "   " + hrIndicator);
			assertCanStart(block, "  " + hrIndicator);
			assertCanStart(block, " " + hrIndicator);
			assertCannotStart(block, "    " + hrIndicator);
			assertCanStart(block, hrIndicator);
			assertCanStart(block, Strings.repeat("" + c, 4));
			assertCanStart(block, Strings.repeat("" + c, 14));
		}
		
		// Bug 472390:
		assertCannotStart(block, "\t***");
	}
	
	@Test
	public void process() {
		assertContent("<p>one</p><hr/>", "one\n\n------\n");
		assertContent("<p>one</p><hr/>", "one\n\n---\n");
		assertContent("<p>one</p><hr/>", "one\n\n-  - -\n");
		assertContent("<p>one</p><hr/>", "one\n\n   ** *****\n");
	}
	
}
