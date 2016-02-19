/*=============================================================================#
 # Copyright (c) 2015-2016 David Green and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     David Green - initial API and implementation in Mylyn
 #     Stephan Wahlbrink (WalWare.de) - revised API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.commonmark.core.inlines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex;
import de.walware.docmlet.wikitext.internal.commonmark.core.Cursor;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;


public class HtmlEntitySpan extends SourceSpan {
	
	
	private static final Pattern PATTERN= Pattern.compile(
			CommonRegex.HTML_ENTITY_REGEX + ".*",
			Pattern.DOTALL );
	
	
	private Matcher matcher;
	
	
	@Override
	public Inline createInline(final ProcessingContext context, final Cursor cursor) {
		final Matcher matcher;
		final char c= cursor.getChar();
		if (c == '&'
				&& (matcher= cursor.setup(getMatcher())).matches() ) {
			final String entity= matcher.group(1);
			return new HtmlEntity(
					cursor.getLineAtOffset(), cursor.getOffset(), entity.length() + 2,
					entity );
		}
		return null;
	}
	
	
	private Matcher getMatcher() {
		if (this.matcher == null) {
			this.matcher= PATTERN.matcher("");
		}
		return this.matcher;
	}
	
}
