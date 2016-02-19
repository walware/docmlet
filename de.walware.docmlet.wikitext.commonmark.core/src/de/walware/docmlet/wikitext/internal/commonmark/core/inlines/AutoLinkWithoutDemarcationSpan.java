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

import de.walware.jcommons.collections.ImCollections;

import com.google.common.net.UrlEscapers;

import de.walware.docmlet.wikitext.internal.commonmark.core.Cursor;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;


public class AutoLinkWithoutDemarcationSpan extends SourceSpan {
	
	
	private static final Pattern PATTERN= Pattern.compile(
			"(https?://[\\p{Alnum}%._~!$&?#'()*+,;:@/=-]*[\\p{Alnum}_~!$&?#'(*+@/=-]).*",
			Pattern.DOTALL );
	
	
	private final Matcher matcher= PATTERN.matcher("");
	
	
	@Override
	public Inline createInline(final ProcessingContext context, final Cursor cursor) {
		if (cursor.getChar() == 'h') {
			final Matcher matcher= cursor.setup(this.matcher);
			if (matcher.matches()) {
				final String link= matcher.group(1);
				final String href= link;
				
				final int cursorLength= matcher.end(1) - matcher.regionStart();
				final int startOffset= cursor.getOffset();
				// no line break: final int endOffset= startOffset + cursorLength;
				
				return new Link(cursor.getLineAtOffset(), startOffset, cursorLength, cursorLength,
						escapeUri(link), null, ImCollections.newList(
								new Characters(cursor.getLineAtOffset(),
										startOffset, cursorLength, cursorLength,
										href )));
			}
		}
		return null;
	}
	
	private String escapeUri(final String link) {
		return UrlEscapers.urlFragmentEscaper().escape(link).replace("%23", "#").replace("%25", "%");
	}
	
}
