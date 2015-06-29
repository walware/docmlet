/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.core.source;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.Locator;


public class CorrectOffsetDocumentBuilder extends DocumentBuilder {
	
	
	private static final byte S_endBlock= 1;
	private static final byte S_endSpan= 2;
	private static final byte S_endHeading= 3;
	
	
	private final DocumentBuilder builder;
	
	private byte state;
	
	
	public CorrectOffsetDocumentBuilder(final DocumentBuilder builder) {
		if (builder == null) {
			throw new NullPointerException("builder"); //$NON-NLS-1$
		}
		this.builder= builder;
	}
	
	
	@Override
	public void setLocator(final Locator locator) {
		super.setLocator(locator);
		this.builder.setLocator(locator);
	}
	
	private void check() {
		switch (this.state) {
		case S_endBlock:
			this.builder.endBlock();
			break;
		case S_endSpan:
			this.builder.endSpan();
			break;
		case S_endHeading:
			this.builder.endHeading();
			break;
		default:
			return;
		}
		this.state= 0;
	}
	
	
	public void reset() {
		this.state= 0;
	}
	
	public void close() {
		check();
		reset();
	}
	
	
	@Override
	public void beginDocument() {
		reset();
		this.builder.beginDocument();
	}
	
	@Override
	public void endDocument() {
		check();
		this.builder.endDocument();
	}
	
	@Override
	public void beginBlock(final BlockType type, final Attributes attributes) {
		check();
		this.builder.beginBlock(type, attributes);
	}
	
	@Override
	public void endBlock() {
		check();
		this.state= S_endBlock;
	}
	
	@Override
	public void beginSpan(final SpanType type, final Attributes attributes) {
		check();
		this.builder.beginSpan(type, attributes);
	}
	
	@Override
	public void endSpan() {
		check();
		this.state= S_endSpan;
	}
	
	@Override
	public void beginHeading(final int level, final Attributes attributes) {
		check();
		this.builder.beginHeading(level, attributes);
	}
	
	@Override
	public void endHeading() {
		check();
		this.state= S_endHeading;
	}
	
	@Override
	public void characters(final String text) {
		check();
		this.builder.characters(text);
	}
	
	@Override
	public void entityReference(final String entity) {
		check();
		this.builder.entityReference(entity);
	}
	
	@Override
	public void image(final Attributes attributes, final String url) {
		check();
		this.builder.image(attributes, url);
	}
	
	@Override
	public void link(final Attributes attributes, final String hrefOrHashName, final String text) {
		check();
		this.builder.link(attributes, hrefOrHashName, text);
	}
	
	@Override
	public void imageLink(final Attributes linkAttributes, final Attributes imageAttributes,
			final String href, final String imageUrl) {
		check();
		this.builder.imageLink(linkAttributes, imageAttributes, href, imageUrl);
	}
	
	@Override
	public void acronym(final String text, final String definition) {
		check();
		this.builder.acronym(text, definition);
	}
	
	@Override
	public void lineBreak() {
		check();
		this.builder.lineBreak();
	}
	
	@Override
	public void charactersUnescaped(final String literal) {
		check();
		this.builder.charactersUnescaped(literal);
	}
	
}
