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

package de.walware.docmlet.wikitext.internal.commonmark.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.base.Strings;


public class ToStringHelperTest {
	
	
	public ToStringHelperTest() {
	}
	
	
	@Test
	public void toStringValue() {
		assertEquals(null, ToStringHelper.toStringValue(null));
		assertEquals("", ToStringHelper.toStringValue(""));
		assertEquals("abc", ToStringHelper.toStringValue("abc"));
		assertEquals("01234567890123456789...",
				ToStringHelper.toStringValue(Strings.repeat("0123456789", 10)));
		assertEquals("a\\r\\n\\tb", ToStringHelper.toStringValue("a\r\n\tb"));
	}
	
}
