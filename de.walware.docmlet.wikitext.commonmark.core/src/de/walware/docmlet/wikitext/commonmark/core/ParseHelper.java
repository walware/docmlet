/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.commonmark.core;

import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.HTML_ENTITY_PATTERN;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.HTML_ENTITY_REGEX;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.walware.ecommons.text.core.util.HtmlUtils;

import com.google.common.escape.Escaper;


public class ParseHelper {
	
	
	private static final Pattern ESCAPING_PATTERN= Pattern.compile("\\\\(.)" + "|" + HTML_ENTITY_REGEX);
	
	private static final Pattern WHITESPACE_PATTERN= Pattern.compile("\\s+");
	
	
	private final Matcher escapingMatcher= ESCAPING_PATTERN.matcher("");
	private final Matcher htmlEntityMatcher= HTML_ENTITY_PATTERN.matcher("");
	private final Matcher whitespaceMatcher= WHITESPACE_PATTERN.matcher("");
	
	
	private final StringBuilder tmpBuilder= new StringBuilder(0x40);
	
	
	public ParseHelper() {
	}
	
	
	public Matcher getHtmlEntityMatcher() {
		return this.htmlEntityMatcher;
	}
	
	private Matcher getEscapingMatcher() {
		return this.escapingMatcher;
	}
	
	private Matcher getWhitespaceMatcher() {
		return this.whitespaceMatcher;
	}
	
	
	private StringBuilder getTmpBuilder() {
		this.tmpBuilder.setLength(0);
		return this.tmpBuilder;
	}
	
	
	public String resolveHtmlEntity(final String reference) {
		try {
			final String replacement= HtmlUtils.resolveEntity(reference);
			if (replacement != null && replacement.charAt(0) == 0) {
				throw new IllegalArgumentException();
			}
			return replacement;
		}
		catch (final IllegalArgumentException e) {
			return "\uFFFD"; //$NON-NLS-1$
		}
	}
	
	public String replaceHtmlEntities(final String text, final Escaper escaper) {
		final StringBuilder sb= getTmpBuilder();
		final Matcher matcher= getHtmlEntityMatcher().reset(text);
		int lastEnd= 0;
		while (matcher.find()) {
			try {
				final int start= matcher.start();
				final String reference= matcher.group(1);
				String replacement= resolveHtmlEntity(reference);
				if (replacement != null) {
					if (escaper != null) {
						replacement= escaper.escape(replacement);
					}
					if (lastEnd < start) {
						sb.append(text, lastEnd, start);
					}
					sb.append(replacement);
					lastEnd= matcher.end();
				}
			}
			catch (final IllegalArgumentException e) {
			}
		}
		
		if (lastEnd == 0) {
			return text;
		}
		
		if (lastEnd < text.length()) {
			sb.append(text, lastEnd, text.length());
		}
		return sb.toString();
	}
	
	public String replaceEscaping(final String text) {
		final StringBuilder sb= getTmpBuilder();
		final Matcher matcher= getEscapingMatcher().reset(text);
		int lastEnd= 0;
		while (matcher.find()) {
			final int start= matcher.start();
			{	final int escapedIdx= matcher.start(1);
				if (escapedIdx >= 0) {
					if (isAsciiPunctuation(text.charAt(escapedIdx))) {
						if (lastEnd < start) {
							sb.append(text, lastEnd, start);
						}
						lastEnd= escapedIdx;
					}
					continue;
				}
			}
			try {
				final String reference= matcher.group(2);
				final String replacement= resolveHtmlEntity(reference);
				if (replacement != null) {
					if (lastEnd < start) {
						sb.append(text, lastEnd, start);
					}
					sb.append(replacement);
					lastEnd= matcher.end();
				}
			}
			catch (final IllegalArgumentException e) {
				if (lastEnd < start) {
					sb.append(text, lastEnd, start);
				}
				sb.append('\uFFFD');
				lastEnd= matcher.end();
			}
		}
		
		if (lastEnd == 0) {
			return text;
		}
		
		if (lastEnd < text.length()) {
			sb.append(text, lastEnd, text.length());
		}
		return sb.toString();
	}
	
	public String collapseWhitespace(final String text) {
		final StringBuilder sb= getTmpBuilder();
		final Matcher matcher= getWhitespaceMatcher().reset(text);
		int lastEnd= 0;
		while (matcher.find()) {
			final int start= matcher.start();
			if (start == 0) {
				lastEnd= matcher.end();
				continue;
			}
			sb.append(text, lastEnd, start);
			lastEnd= matcher.end();
			if (lastEnd == text.length()) {
				break;
			}
			sb.append(' ');
		}
		
		if (lastEnd == 0) {
			return text;
		}
		
		if (lastEnd < text.length()) {
			sb.append(text, lastEnd, text.length());
		}
		return sb.toString();
	}
	
	public boolean isUnicodeWhitespace(final char ch) {
		if (ch >= 0 && ch < 0xA0) {
			switch (ch) {
			case '\t':
			case '\n':
			case '\r':
			case '\f':
			case '\u0020':
				return true;
			default:
				return false;
			}
		}
		else {
			return (Character.getType(ch) == Character.SPACE_SEPARATOR);
		}
	}
	
	public boolean isAsciiPunctuation(final char ch) {
		switch (ch) {
			case '!':
			case '"':
			case '#':
			case '$':
			case '%':
			case '&':
			case '\'':
			case '(':
			case ')':
			case '*':
			case '+':
			case ',':
			case '-':
			case '.':
			case '/':
			case ':':
			case ';':
			case '<':
			case '=':
			case '>':
			case '?':
			case '@':
			case '[':
			case '\\':
			case ']':
			case '^':
			case '_':
			case '`':
			case '{':
			case '|':
			case '}':
			case '~':
				return true;
			default:
				return false;
		}
	}
	
	public boolean isPunctuation(final char ch) {
		if (isAsciiPunctuation(ch)) {
			return true;
		}
		final int type= Character.getType(ch);
		switch (type) {
		case Character.DASH_PUNCTUATION:
		case Character.START_PUNCTUATION:
		case Character.END_PUNCTUATION:
		case Character.CONNECTOR_PUNCTUATION:
		case Character.OTHER_PUNCTUATION:
		case Character.INITIAL_QUOTE_PUNCTUATION:
		case Character.FINAL_QUOTE_PUNCTUATION:
			return true;
		default:
			return false;
		}
	}
	
}
