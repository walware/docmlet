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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext.UriWithTitle;


public class ProcessingContextTest {
	
	@Rule
	public final ExpectedException thrown = ExpectedException.none();
	
	
	public ProcessingContextTest() {
	}
	
	
	@Test
	public void empty() {
		ProcessingContext context = CommonmarkAsserts.newContext();
		assertNotNull(context);
		assertFalse(context.hasNamedUri());
	}
	
	@Test
	public void referenceDefinition() {
		ProcessingContext context = CommonmarkAsserts.newContext();
		context.addUriDefinition(context.normalizeLabel("onE"), "/uri", "a title");
		assertNotNull(context);
		assertTrue(context.hasNamedUri());
		assertNotNull(context.getNamedUri("one"));
		assertNotNull(context.getNamedUri("One"));
		UriWithTitle link = context.getNamedUri("ONE");
//		assertEquals("onE", link.getName());
		assertEquals("/uri", link.getUri());
		assertEquals("a title", link.getTitle());
		assertNull(context.getNamedUri("Unknown"));
	}
	
	public void referenceDefinitionEmptyName() {
		ProcessingContext context = CommonmarkAsserts.newContext();
		context.addUriDefinition("", "one", "two");
		assertFalse(context.hasNamedUri());
	}
	
	@Test
	public void referenceDefinitionDuplicate() {
		ProcessingContext context = CommonmarkAsserts.newContext();
		context.addUriDefinition("a", "/uri", "a title");
		context.addUriDefinition("a", "/uri2", "a title2");
		UriWithTitle uriWithTitle = context.getNamedUri("a");
		assertEquals("/uri", uriWithTitle.getUri());
	}
	
	@Test
	public void generateHeadingId() {
		ProcessingContext processingContext = CommonmarkAsserts.newContext();
		processingContext.setMode(ProcessingContext.EMIT_DOCUMENT);
		assertEquals("a", processingContext.generateHeadingId(1, "a"));
		assertEquals("a2", processingContext.generateHeadingId(1, "a"));
		assertEquals("a3", processingContext.generateHeadingId(2, "a"));
		assertEquals("h1-3", processingContext.generateHeadingId(1, null));
		assertEquals("h1-4", processingContext.generateHeadingId(1, ""));
	}
	
}
