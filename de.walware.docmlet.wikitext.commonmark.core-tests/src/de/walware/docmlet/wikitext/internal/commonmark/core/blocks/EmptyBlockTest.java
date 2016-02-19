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


public class EmptyBlockTest {
	
	
	private EmptyBlock block = new EmptyBlock();
	
	
	public EmptyBlockTest() {
	}
	
	
	@Test
	public void canStart() {
		assertCannotStart(block, "one");
		assertCanStart(block, "\n");
	}
	
	@Test
	public void process() {
		assertContent("<p>2</p>", "\n2");
	}
	
}
