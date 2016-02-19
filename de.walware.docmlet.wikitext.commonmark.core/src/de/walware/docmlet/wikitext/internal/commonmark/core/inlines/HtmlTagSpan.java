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

import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.CDATA_1_REGEX;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.CLOSE_TAG_1_REGEX;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.COMMENT_1_REGEX;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.DECL_1_REGEX;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.OPEN_TAG_1_REGEX;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.PI_1_REGEX;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.walware.docmlet.wikitext.internal.commonmark.core.Cursor;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;


public class HtmlTagSpan extends SourceSpan {
	
	
	private static final Pattern PATTERN= Pattern.compile("(<(?:" +
				COMMENT_1_REGEX +
				"|" + PI_1_REGEX +
				"|" + DECL_1_REGEX +
				"|" + CDATA_1_REGEX +
				"|" + OPEN_TAG_1_REGEX + "|" + CLOSE_TAG_1_REGEX +
			")).*",
			Pattern.DOTALL );
	
	
	private Matcher matcher;
	
	
	@Override
	public Inline createInline(final ProcessingContext context, final Cursor cursor) {
		final char c= cursor.getChar();
		if (c == '<') {
			final Matcher matcher= cursor.setup(getMatcher());
			if (matcher.matches()) {
				final int startOffset= cursor.getOffset();
				final int endOffset= cursor.getMatcherOffset(matcher.end(1));
				
				return new HtmlTag(cursor.getLineAtOffset(), startOffset, endOffset - startOffset,
						matcher.group(1) );
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
	
}
