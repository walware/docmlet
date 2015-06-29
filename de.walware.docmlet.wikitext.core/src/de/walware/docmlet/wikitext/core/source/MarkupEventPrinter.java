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

import java.io.IOException;
import java.io.Writer;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;


public class MarkupEventPrinter extends DocumentBuilder {
	
	
	
	private final Writer out;
	
	private int depth;
	
	private MarkupLanguage markupLanguage;
	
	private String text;
	
	
	public MarkupEventPrinter(final Writer out) {
		this.out= out;
	}
	
	public MarkupEventPrinter(final String text, final MarkupLanguage markupLanguage,
			final Writer out) {
		this.out= out;
		reset(text, markupLanguage);
	}
	
	
	public void reset(final String text, final MarkupLanguage markupLanguage) {
		this.text= text;
		this.markupLanguage= markupLanguage;
	}
	
	protected void printIndent(final int depth) throws IOException {
		for (int i= 0; i < depth; i++) {
			this.out.write("    "); //$NON-NLS-1$
		}
	}
	
	
	protected void header() {
		try {
			this.out.append("==== Document Events ("); //$NON-NLS-1$
			if (this.markupLanguage != null) {
				this.out.append("language= "); //$NON-NLS-1$
				this.out.append(this.markupLanguage.getName());
				this.out.append(", "); //$NON-NLS-1$
			}
			this.out.append("textLength= "); //$NON-NLS-1$
			this.out.append(Integer.toString(this.text.length()));
			this.out.append(") ====\n"); //$NON-NLS-1$
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void footer() {
		try {
			this.out.append("====\n"); //$NON-NLS-1$
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void printBegin(final int beginOffset) throws IOException {
		if (beginOffset < 0 || beginOffset > this.text.length()) {
			this.out.append("<out-of-range>"); //$NON-NLS-1$
			return;
		}
		this.out.append(this.text.substring(beginOffset, Math.min(beginOffset + 8, this.text.length())));
		this.out.append(" ..."); //$NON-NLS-1$
	}
	
	protected void printEnd(final int endOffset) throws IOException {
		if (endOffset < 0 || endOffset > this.text.length()) {
			this.out.append("<out-of-range>"); //$NON-NLS-1$
			return;
		}
		this.out.append("... "); //$NON-NLS-1$
		writeEncoded(this.text, Math.max(endOffset - 8, 0), endOffset);
	}
	
	private void writeEncoded(final String s, final int begin, final int end) throws IOException {
		for (int i= begin; i < end; i++) {
			final int c= s.charAt(i);
			if (c < 0x10) {
				this.out.write("<0x0"); //$NON-NLS-1$
				this.out.write(Integer.toHexString(c));
				this.out.write('>');
			}
			else if (c < 0x20) {
				this.out.write("<0x"); //$NON-NLS-1$
				this.out.write(Integer.toHexString(c));
				this.out.write('>');
			}
			else {
				this.out.write(c);
			}
		}
	}
	
	private void begin(final String label) {
		try {
			printIndent(this.depth);
			
			final int beginOffset= this.locator.getDocumentOffset();
			final int endOffset= this.locator.getLineDocumentOffset() + this.locator.getLineSegmentEndOffset();
			this.out.append("["); //$NON-NLS-1$
			this.out.append(Integer.toString(beginOffset));
			this.out.append(", "); //$NON-NLS-1$
			this.out.append(Integer.toString(endOffset));
			this.out.append(") "); //$NON-NLS-1$
			
			this.out.append("begin"); //$NON-NLS-1$
			this.out.append(label);
			this.out.append(": "); //$NON-NLS-1$
			
			printBegin(beginOffset);
			
			this.out.append('\n');
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			this.depth++;
		}
	}
	
	private void end(final String label) {
		try {
			this.depth--;
			
			printIndent(this.depth);
			
			final int beginOffset= this.locator.getDocumentOffset();
			final int endOffset= this.locator.getLineDocumentOffset() + this.locator.getLineSegmentEndOffset();
			this.out.append("["); //$NON-NLS-1$
			this.out.append(Integer.toString(beginOffset));
			this.out.append(", "); //$NON-NLS-1$
			this.out.append(Integer.toString(endOffset));
			this.out.append(") "); //$NON-NLS-1$
			
			this.out.append("end"); //$NON-NLS-1$
			this.out.append(label);
			this.out.append(": "); //$NON-NLS-1$
			
			printEnd(endOffset);
			
			this.out.append('\n');
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void terminal(final String label) {
		try {
			printIndent(this.depth);
			
			final int beginOffset= this.locator.getDocumentOffset();
			final int endOffset= this.locator.getLineDocumentOffset() + this.locator.getLineSegmentEndOffset();
			this.out.append("["); //$NON-NLS-1$
			this.out.append(Integer.toString(beginOffset));
			this.out.append(", "); //$NON-NLS-1$
			this.out.append(Integer.toString(endOffset));
			this.out.append(") "); //$NON-NLS-1$
			
			this.out.append(label);
			this.out.append(": "); //$NON-NLS-1$
			
//			printEnd(endOffset);
			
			this.out.append('\n');
		}
		catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	@Override
	public void beginDocument() {
		header();
		
		begin("Document"); //$NON-NLS-1$
	}
	
	@Override
	public void endDocument() {
		end("Document"); //$NON-NLS-1$
		
		footer();
	}
	
	@Override
	public void beginBlock(final BlockType type, final Attributes attributes) {
		begin("Block(" + type + ")"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public void endBlock() {
		end("Block"); //$NON-NLS-1$
	}
	
	@Override
	public void beginSpan(final SpanType type, final Attributes attributes) {
		begin("Span(" + type + ")"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public void endSpan() {
		end("Span"); //$NON-NLS-1$
	}
	
	@Override
	public void beginHeading(final int level, final Attributes attributes) {
		begin("Heading"); //$NON-NLS-1$
	}
	
	@Override
	public void endHeading() {
		end("Heading"); //$NON-NLS-1$
	}
	
	@Override
	public void characters(final String text) {
		terminal("characters"); //$NON-NLS-1$
	}
	
	@Override
	public void entityReference(final String entity) {
		terminal("entityReference"); //$NON-NLS-1$
	}
	
	@Override
	public void image(final Attributes attributes, final String url) {
		terminal("image"); //$NON-NLS-1$
	}
	
	@Override
	public void link(final Attributes attributes, final String hrefOrHashName, final String text) {
		terminal("link"); //$NON-NLS-1$
	}
	
	@Override
	public void imageLink(final Attributes linkAttributes, final Attributes imageAttributes, final String href,
			final String imageUrl) {
		terminal("imageLink"); //$NON-NLS-1$
	}
	
	@Override
	public void acronym(final String text, final String definition) {
		terminal("acronym"); //$NON-NLS-1$
	}
	
	@Override
	public void lineBreak() {
		terminal("lineBreak"); //$NON-NLS-1$
	}
	
	@Override
	public void charactersUnescaped(final String literal) {
		terminal("charactersUnescaped"); //$NON-NLS-1$
	}
	
}
