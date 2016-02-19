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

package de.walware.docmlet.wikitext.internal.commonmark.core.inlines;

import org.junit.Test;


public class PotentialEmphasisSpanExtTest extends AbstractSourceSpanTest {
	
	
	public PotentialEmphasisSpanExtTest() {
	}
	
	
	@Test
	public void strict() {
		this.span= new PotentialStyleSpan(false, false, false);
		
		assertParseToHtml("~~text~~", "~~text~~");
		assertParseToHtml("^text^", "^text^");
		assertParseToHtml("~text~", "~text~");
	}
	
	@Test
	public void strikeout_DTilde() {
		this.span= new PotentialStyleSpan(true, false, false);
		
		assertParseToHtml("^text^", "^text^");
		assertParseToHtml("~text~", "~text~");
		
		assertParseToHtml("~~ some text~~", "~~ some text~~");
		assertParseToHtml("~~some text ~~", "~~some text ~~");
		assertParseToHtml("~~some text", "~~some text");
		assertParseToHtml("<del>some text</del>", "~~some text~~");
		assertParseToHtml("I am <del>some text</del> and more", "I am ~~some text~~ and more");
		assertParseToHtml("<del>some\ntext</del>d", "~~some\ntext~~d");
		
		assertParseToHtml("<del>some~ text</del> and more", "~~some~ text~~ and more");
	}
	
	@Test
	public void superscript_SCircumflex() {
		this.span= new PotentialStyleSpan(false, true, false);
		
		assertParseToHtml("~~text~~", "~~text~~");
		assertParseToHtml("~text~", "~text~");
		
		assertParseToHtml("^ some text^", "^ some text^");
		assertParseToHtml("^some text ^", "^some text ^");
		assertParseToHtml("^some text", "^some text");
		assertParseToHtml("^some text^", "^some text^");
		assertParseToHtml("<sup>sometext</sup>", "^sometext^");
		assertParseToHtml("I am <sup>sometext</sup> and more", "I am ^sometext^ and more");
		assertParseToHtml("<sup>some\ntext</sup>d", "^some\ntext^d");
	}
	
	@Test
	public void subscript_STilde() {
		this.span= new PotentialStyleSpan(false, false, true);
		
//		assertParseToHtml("~~text~~", "~~text~~");
		assertParseToHtml("^text^", "^text^");
		
		assertParseToHtml("~ some text~", "~ some text~");
		assertParseToHtml("~some text ~", "~some text ~");
		assertParseToHtml("~some text", "~some text");
		assertParseToHtml("~some text~", "~some text~");
		assertParseToHtml("<sub>sometext</sub>", "~sometext~");
		assertParseToHtml("I am <sub>sometext</sub> and more", "I am ~sometext~ and more");
		assertParseToHtml("<sub>some\ntext</sub>d", "~some\ntext~d");
		
		assertParseToHtml("<sub>some</sub>~ text~ and more", "~some~~ text~ and more");
	}
	
	@Test
	public void together() {
		this.span= new PotentialStyleSpan(true, true, true);
		
		assertParseToHtml("<strong>some <del>deleted</del> x<sub>1</sub><sup>2</sup> text</strong>", "__some ~~deleted~~ x~1~^2^ text__");
	}
	
}
