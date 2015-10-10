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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.walware.docmlet.wikitext.internal.commonmark.core.Cursor;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;


public class StringCharactersSpan extends SourceSpan {
	
	
	private static final Pattern PATTERN= Pattern.compile("(.(?: *[^\n `\\[\\]\\\\!<&*_]+)*).*",
			Pattern.DOTALL );
	
	private static final Map<String, Pattern> EXT_PATTERN= new HashMap<>();
	private static final char[] EXT_BUFFER= new char[256];
	
	private static final boolean isDefaultControlChar(final char c) {
		switch (c) {
		case '\n':
		case '`':
		case '[':
		case ']':
		case '\\':
		case '!':
		case '<':
		case '&':
		case '*':
		case '_':
			return true;
		default:
			return false;
		}
	}
	
	private static synchronized final Pattern getPattern(final String controlChars) {
		final char[] chars= EXT_BUFFER;
		int n= 0;
		if (controlChars != null) {
			for (int j= 0; j < controlChars.length(); j++) {
				final char c= controlChars.charAt(j);
				if (isDefaultControlChar(c)) {
					continue;
				}
				int i= Arrays.binarySearch(chars, 0, n, c);
				if (i >= 0) {
					continue;
				}
				i= -(i + 1);
				if (i < n) {
					System.arraycopy(chars, i, chars, i + 1, n - i);
				}
				chars[i]= c;
				n++;
			}
		}
		if (n == 0) {
			return PATTERN;
		}
		
		final String key= new String(chars, 0, n);
		Pattern pattern= EXT_PATTERN.get(key);
		if (pattern == null) {
			pattern= Pattern.compile("(.(?: *[^\n `\\[\\]\\\\!<&*_" + key + "]+)*).*",
				Pattern.DOTALL );
			EXT_PATTERN.put(key, pattern);
		}
		return pattern;
	}
	
	
	private final Matcher matcher;
	
	
	public StringCharactersSpan() {
		this(null);
	}
	
	public StringCharactersSpan(final String additional) {
		this.matcher= getPattern(additional).matcher("");
	}
	
	
	@Override
	public Inline createInline(final ProcessingContext context, final Cursor cursor) {
		final Matcher matcher= cursor.setup(this.matcher);
		if (matcher.matches()) {
			final String group= matcher.group(1);
			
			final int cursorLength= matcher.end(1) - matcher.regionStart();
			final int startOffset= cursor.getOffset();
			
			return new Characters(cursor.getLineAtOffset(),
					startOffset, cursorLength, cursorLength,
					group );
		}
		return null;
	}
	
}
