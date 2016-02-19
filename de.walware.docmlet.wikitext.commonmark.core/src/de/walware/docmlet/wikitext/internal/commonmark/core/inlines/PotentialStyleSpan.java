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

import static de.walware.docmlet.wikitext.internal.commonmark.core.inlines.PotentialStyleDelimiterInfo.FLANKING;
import static de.walware.docmlet.wikitext.internal.commonmark.core.inlines.PotentialStyleDelimiterInfo.FLANKING_UNDERSCORE;

import java.util.List;

import de.walware.docmlet.wikitext.commonmark.core.ParseHelper;
import de.walware.docmlet.wikitext.internal.commonmark.core.Cursor;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.PotentialStyleDelimiterInfo.ExtDelimiter;


public class PotentialStyleSpan extends SourceSpan {
	
	private static char charAfter(final Cursor cursor, final int length) {
		return cursor.hasNext(length) ? cursor.getNext(length) : '\n';
	}
	
	private static char charBefore(final Cursor cursor) {
		return cursor.hasPrevious() ? cursor.getPrevious() : '\n';
	}
	
	
	private final PotentialStyleDelimiterInfo tilde;
	private final PotentialStyleDelimiterInfo circumflex;
	
	
	public PotentialStyleSpan() {
		this.tilde= null;
		this.circumflex= null;
	}
	
	public PotentialStyleSpan(final boolean strikeoutEnabled,
			final boolean superscriptEnabled, final boolean subscriptEnabled) {
		this.tilde= (strikeoutEnabled || subscriptEnabled) ?
				new ExtDelimiter(subscriptEnabled, strikeoutEnabled) {
			@Override
			public char getChar() {
				return '~';
			}
			@Override
			public Inline createStyleInline(final int size, final Line line,
					final int offset, final int length, final List<Inline> contents) {
				switch (size) {
				case 1:
					return new Subscript(line, offset, length, contents);
				case 2:
					return new Strikeout(line, offset, length, contents);
				default:
					throw new IllegalStateException();
				}
			}
		} : null;
		this.circumflex= (superscriptEnabled) ? 
				new ExtDelimiter(superscriptEnabled, false) {
			@Override
			public char getChar() {
				return '^';
			}
			@Override
			public Inline createStyleInline(final int size, final Line line,
					final int offset, final int length, final List<Inline> contents) {
				switch (size) {
				case 1:
					return new Superscript(line, offset, length, contents);
				default:
					throw new IllegalStateException();
				}
			}
		} : null;
	}
	
	
	public String getControlChars() {
		if (this.tilde != null) {
			if (this.circumflex != null) {
				return "*^_~";
			}
			else {
				return "*_~";
			}
		}
		else {
			if (this.circumflex != null) {
				return "*^_";
			}
			else {
				return "*_";
			}
		}
	}
	
	@Override
	public Inline createInline(final ProcessingContext context, final Cursor cursor) {
		final PotentialStyleDelimiterInfo info= getInfo(cursor.getChar());
		if (info != null && !currentPositionIsEscaped(cursor)) {
			final int length= lengthMatching(cursor, info.getChar());
			if (info.isPotentialSequence(length)) {
				boolean canOpen= true;
				boolean canClose= true;
				
				if ((info.getRequirements(0) & (FLANKING | FLANKING_UNDERSCORE)) != 0) {
					final boolean leftFlanking= isLeftFlanking(cursor, length, context);
					final boolean rightFlanking= isRightFlanking(cursor, length, context);
					
					if ((info.getRequirements(0) & FLANKING_UNDERSCORE) != 0) {
						final ParseHelper helper= context.getHelper();
						canOpen= leftFlanking && (!rightFlanking || helper.isPunctuation(charBefore(cursor)));
						canClose= rightFlanking && (!leftFlanking || helper.isPunctuation(charAfter(cursor, length)));
					}
					else {
						canOpen= leftFlanking;
						canClose= rightFlanking;
					}
				}
				
				return new PotentialStyleDelimiter(info,
						cursor.getLineAtOffset(),
						cursor.getOffset(), length,
						cursor.getTextAtOffset(length),
						canOpen, canClose );
			}
		}
		return null;
	}
	
	private PotentialStyleDelimiterInfo getInfo(final char c) {
		switch (c) {
		case '*':
			return PotentialStyleDelimiterInfo.DEFAULT_ASTERISK;
		case '_':
			return PotentialStyleDelimiterInfo.DEFAULT_UNDERSCORE;
		case '~':
			return this.tilde;
		case '^':
			return this.circumflex;
		default:
			return null;
		}
	}
	
	boolean isLeftFlanking(final Cursor cursor, final int length, final ProcessingContext context) {
		final char charBefore= charBefore(cursor);
		final char charAfter= charAfter(cursor, length);
		final ParseHelper helper= context.getHelper();
		return !helper.isUnicodeWhitespace(charAfter) && !(helper.isPunctuation(charAfter)
				&& !helper.isUnicodeWhitespace(charBefore) && !helper.isPunctuation(charBefore));
	}
	
	boolean isRightFlanking(final Cursor cursor, final int length, final ProcessingContext context) {
		final char charBefore= charBefore(cursor);
		final char charAfter= charAfter(cursor, length);
		final ParseHelper helper= context.getHelper();
		return !helper.isUnicodeWhitespace(charBefore) && !(helper.isPunctuation(charBefore)
				&& !helper.isUnicodeWhitespace(charAfter) && !helper.isPunctuation(charAfter));
	}
	
	private boolean currentPositionIsEscaped(final Cursor cursor) {
		int backslashCount= 0;
		for (int x= 1; cursor.hasPrevious(x) && cursor.getPrevious(x) == '\\'; ++x) {
			++backslashCount;
		}
		return backslashCount % 2 == 1;
	}
	
	private int lengthMatching(final Cursor cursor, final char c) {
		int x= 1;
		while (cursor.hasNext(x) && cursor.getNext(x) == c) {
			++x;
		}
		return x;
	}
	
}
