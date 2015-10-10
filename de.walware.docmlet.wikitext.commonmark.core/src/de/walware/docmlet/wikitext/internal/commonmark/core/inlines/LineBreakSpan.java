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

package de.walware.docmlet.wikitext.internal.commonmark.core.inlines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.walware.docmlet.wikitext.internal.commonmark.core.Cursor;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;


public class LineBreakSpan extends SourceSpan {
	
	
	private final static Pattern PATTERN= Pattern.compile("( *(\\\\)?\n).*",
			Pattern.DOTALL );
	
	
	private final Matcher matcher= PATTERN.matcher("");
	
	
	@Override
	public Inline createInline(final ProcessingContext context, final Cursor cursor) {
		final char c= cursor.getChar();
		if (c == '\n' || c == ' ') {
			final Matcher matcher= cursor.setup(this.matcher);
			if (matcher.matches()) {
				final int cursorLength= matcher.end(1) - matcher.regionStart();
				final int startOffset= cursor.getOffset();
				final int endOffset= cursor.getMatcherOffset(matcher.end(1));
				
				if (cursorLength > 2 || matcher.start(2) != -1) {
					return new HardLineBreak(cursor.getLineAtOffset(),
							startOffset, endOffset - startOffset, cursorLength );
				}
				return new SoftLineBreak(cursor.getLineAtOffset(),
						startOffset, endOffset - startOffset, cursorLength );
			}
		}
		return null;
	}
	
}
