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

import de.walware.docmlet.wikitext.internal.commonmark.core.Cursor;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;


public class CodeSpan extends SourceSpan {
	
	
	private static final Pattern OPEN_PATTERN= Pattern.compile("(`+).*",
			Pattern.DOTALL );
	
	private static final Pattern CLOSE_PATTERN= Pattern.compile("`+",
			Pattern.DOTALL );
	
	
	private Matcher openMatcher;
	private Matcher closeMatcher;
	
	
	@Override
	public Inline createInline(final ProcessingContext context, final Cursor cursor) {
		final char c= cursor.getChar();
		if (c == '`') {
			final Matcher openMatcher= cursor.setup(getOpenMatcher());
			if (openMatcher.matches()) {
				final int startOffset= cursor.getOffset();
				final int backtickCount= openMatcher.end(1) - openMatcher.start(1);
				
				final Matcher closeMatcher= cursor.setup(getCloseMatcher(), backtickCount);
				
				while (closeMatcher.find()) {
					if (closeMatcher.end() - closeMatcher.start() == backtickCount) {
						final String codeText= cursor.getText(openMatcher.end(1), closeMatcher.start());
						final int cursorLength= closeMatcher.end() - openMatcher.regionStart();
						final int endOffset= cursor.getMatcherOffset(closeMatcher.end());
						
						return new Code(cursor.getLineAtOffset(),
								startOffset, endOffset - startOffset, cursorLength,
								codeText );
					}
				}
				
				if (backtickCount > 1) {
						return new Characters(cursor.getLineAtOffset(),
								startOffset, backtickCount, backtickCount,
								openMatcher.group(1) );
				}
			}
		}
		return null;
	}
	
	
	private Matcher getOpenMatcher() {
		if (this.openMatcher == null) {
			this.openMatcher= OPEN_PATTERN.matcher("");
		}
		return this.openMatcher;
	}
	
	private Matcher getCloseMatcher() {
		if (this.closeMatcher == null) {
			this.closeMatcher= CLOSE_PATTERN.matcher("");
		}
		return this.closeMatcher;
	}
	
}
