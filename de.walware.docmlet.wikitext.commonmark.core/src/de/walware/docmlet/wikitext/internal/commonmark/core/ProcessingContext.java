/*=============================================================================#
 # Copyright (c) 2015 David Green and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     David Green - initial API and implementation in Mylyn
 #     Stephan Wahlbrink (WalWare.de) - revised API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.commonmark.core;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.mylyn.wikitext.core.parser.IdGenerator;

import de.walware.docmlet.wikitext.commonmark.core.ParseHelper;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.InlineParser;


public class ProcessingContext {
	
	
	public static final int INITIALIZE_CONTEXT= 1;
	public static final int EMIT_DOCUMENT= 2;
	public static final int PARSE_SOURCE_STRUCT= 3;
	
	
	public static class UriWithTitle {
		
		private final String label;
		
		private final String uri;
		
		private final String title;
		
		public UriWithTitle(final String label, final String uri, final String title) {
			this.label = label;
			this.uri = uri;
			this.title = title;
		}
		
		public String getLabel() {
			return this.label;
		}
		
		public String getUri() {
			return this.uri;
		}
		
		public String getTitle() {
			return this.title;
		}
		
	}
	
	
	private int mode;
	
	private final SourceBlocks sourceBlocks;
	private final InlineParser inlineParser;
	
	private final Map<String, UriWithTitle> links;
	
	private final IdGenerator idGenerator;
	
	private ParseHelper parseHelper;
	
	
	public ProcessingContext(final SourceBlocks sourceBlocks, final InlineParser inlineParser,
			final IdGenerator idGenerator, final int initialMode) {
		this.sourceBlocks= sourceBlocks;
		this.inlineParser= checkNotNull(inlineParser);
		this.links= new HashMap<>();
		this.idGenerator= checkNotNull(idGenerator);
		
		this.mode= initialMode;
	}
	
	
	public int getMode() {
		return this.mode;
	}
	
	public void setMode(final int mode) {
		this.mode= mode;
	}
	
	
	public String normalizeLabel(String label) {
		if (label == null || label.isEmpty()) {
			return null;
		}
		label= getHelper().collapseWhitespace(label);
		if (label.isEmpty()) {
			return null;
		}
		return label.toLowerCase(Locale.ROOT);
	}
	
	public boolean hasNamedUri() {
		return !this.links.isEmpty();
	}
	
	public void addUriDefinition(final String label, final String href, final String title) {
		if (this.mode > INITIALIZE_CONTEXT) {
			throw new IllegalStateException("" + this.mode);
		}
		if (label != null && !label.isEmpty()) {
			if (!this.links.containsKey(label)) {
				this.links.put(label, new UriWithTitle(label, href, title));
			}
		}
	}
	
	public UriWithTitle getNamedUri(final String label) {
		return this.links.get(label.toLowerCase());
	}
	
	public String generateHeadingId(final int headingLevel, final String headingText) {
		if (this.mode <= INITIALIZE_CONTEXT) {
			return "";
		}
		return this.idGenerator.newId("h" + headingLevel, headingText);
	}
	
	public SourceBlocks getSourceBlocks() {
		return this.sourceBlocks;
	}
	
	public InlineParser getInlineParser() {
		return this.inlineParser;
	}
	
	
	public ParseHelper getHelper() {
		if (this.parseHelper == null) {
			this.parseHelper= new ParseHelper();
		}
		return this.parseHelper;
	}
	
}
