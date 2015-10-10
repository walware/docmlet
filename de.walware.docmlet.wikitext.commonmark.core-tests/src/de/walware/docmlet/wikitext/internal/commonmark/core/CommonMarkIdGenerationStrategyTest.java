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

package de.walware.docmlet.wikitext.internal.commonmark.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CommonMarkIdGenerationStrategyTest {
	
	
	private final CommonmarkIdGenerationStrategy strategy = new CommonmarkIdGenerationStrategy();
	
	
	public CommonMarkIdGenerationStrategyTest() {
	}
	
	
	@Test
	public void simple() {
		assertId("abc", "abc");
		assertId("abc123", "abc123");
		assertId("a_bc", "a_bc");
	}
	
	@Test
	public void mixedCase() {
		assertId("abc", "AbC");
	}
	
	@Test
	public void whitespace() {
		assertId("a-bc", "a bc");
		assertId("a-bc", "a  \tbc");
		assertId("abc", " abc");
		assertId("abc", "abc ");
	}
	
	@Test
	public void allWhitespace() {
		assertId("", "   \t");
	}
	
	@Test
	public void hyphenated() {
		assertId("a-b", "a-b");
		assertId("ab", "-ab");
		assertId("ab", "ab-");
	}
	
	@Test
	public void punctuationAndSpecialCharacters() {
		assertId("a-b", "a.b");
		assertId("a-b", "a....b");
		assertId("a-b", "a,b");
		assertId("a-b", "a;b");
		assertId("a-b", "a*b");
		assertId("a-b", "a&b");
		assertId("ab", ".ab");
	}
	
	private void assertId(String expected, String headingText) {
		assertEquals(expected, strategy.generateId(headingText));
	}
	
}
