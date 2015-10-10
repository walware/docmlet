/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #     Stephan Wahlbrink (WalWare.de)
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.commonmark.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

import de.walware.docmlet.wikitext.commonmark.core.ParseHelper;


public class ProcessingHelperTest {
	
	
	public ProcessingHelperTest() {
	}
	
	
	@Test
	public void replaceHtmlEntities() {
		ParseHelper helper = new ParseHelper();
		Escaper escaper = UrlEscapers.urlFormParameterEscaper();
		assertEquals("asf", helper.replaceHtmlEntities("asf", escaper));
		assertEquals("&amp", helper.replaceHtmlEntities("&amp", escaper));
		assertEquals("&amp ;", helper.replaceHtmlEntities("&amp ;", escaper));
		assertEquals("%26", helper.replaceHtmlEntities("&amp;", escaper));
		assertEquals("a%26", helper.replaceHtmlEntities("a&amp;", escaper));
		assertEquals("a%26b", helper.replaceHtmlEntities("a&amp;b", escaper));
		assertEquals("%C3%A4", helper.replaceHtmlEntities("&auml;", escaper));
		assertEquals("&", helper.replaceHtmlEntities("&amp;", null));
		assertEquals("\"", helper.replaceHtmlEntities("&quot;", null));
		assertEquals("\u00e4", helper.replaceHtmlEntities("&auml;", null));
		assertEquals("&xdfsldk;", helper.replaceHtmlEntities("&xdfsldk;", null));
		assertEquals("&0;", helper.replaceHtmlEntities("&0;", null));
	}
	
	@Test
	public void unescape() {
		ParseHelper helper = new ParseHelper();
		assertEquals("(", helper.replaceEscaping("\\("));
		assertEquals("abc(def", helper.replaceEscaping("abc\\(def"));
	}
	
}
