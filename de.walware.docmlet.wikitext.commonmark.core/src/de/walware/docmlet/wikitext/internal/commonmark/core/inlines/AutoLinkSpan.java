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

import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.CTRL_OR_SPACE;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.walware.jcommons.collections.ImCollections;

import com.google.common.net.UrlEscapers;

import de.walware.docmlet.wikitext.internal.commonmark.core.Cursor;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;


public class AutoLinkSpan extends SourceSpan {
	
	private static final String SCHEME_REGEX= "\\p{Alpha}[\\p{Alnum}.+-]{1,31}";
	
	private static final String ABSOLUTE_URI_REGEX= SCHEME_REGEX + ":[^" + CTRL_OR_SPACE + "<>]+";
	
	private static final String EMAIL_DOMAIN_PART= "\\p{Alnum}(?:[\\p{Alnum}-]{0,61}\\p{Alnum})?";
	
	private static final String EMAIL_REGEX= "[\\p{Alnum}.!#$%&'*+/=?^_`{|}~-]+@" +
			EMAIL_DOMAIN_PART + "(?:\\." + EMAIL_DOMAIN_PART + ")*";
	
	private static final Pattern PATTERN= Pattern.compile("(<(" + ABSOLUTE_URI_REGEX + "|(" + EMAIL_REGEX + "))>).*",
			Pattern.DOTALL );
	
	
	private Matcher matcher;
	
	
	@Override
	public Inline createInline(final ProcessingContext context, final Cursor cursor) {
		final char c= cursor.getChar();
		if (c == '<') {
			final Matcher matcher= cursor.setup(getMatcher());
			if (matcher.matches()) {
				final String link= matcher.group(2);
				final String href= (matcher.start(3) != -1) ? ("mailto:" + link) : link;
				
				final int cursorLength= matcher.end(1) - matcher.regionStart();
				final int startOffset= cursor.getOffset();
				// no line break: final int endOffset= startOffset + cursorLength;
				
				return new Link(cursor.getLineAtOffset(), startOffset, cursorLength, cursorLength,
						escapeUri(href), null, ImCollections.newList(
								new Characters(cursor.getLineAtOffset(),
										startOffset + 1, cursorLength - 2, cursorLength - 2,
										link )));
			}
		}
		return null;
	}
	
	
	private Matcher getMatcher() {
		if (this.matcher == null) {
			this.matcher= PATTERN.matcher("");
		}
		return this.matcher;
	}
	
	private String escapeUri(final String link) {
		return UrlEscapers.urlFragmentEscaper().escape(link).replace("%23", "#").replace("%25", "%");
	}
	
}
