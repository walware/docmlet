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

package de.walware.docmlet.wikitext.internal.commonmark.core;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Matcher;


public class Cursor {
	
	
	private final TextSegment segment;
	
	private final String text;
	
	private int textOffset;
	
	
	public Cursor(final TextSegment segment) {
		this.segment = checkNotNull(segment);
		this.text = segment.getText();
		this.textOffset = 0;
	}
	
	/**
	 * Provides the offset of the current cursor position relative to the document.
	 * 
	 * @return the current cursor position offset
	 */
	public int getOffset() {
		return this.segment.offsetOf(this.textOffset);
	}
	
	/**
	 * Provides the offset of the given cursor position relative to the document.
	 * 
	 * @param offset
	 *            the position relative to the cursor
	 * @return the current cursor position offset
	 */
	public int getOffset(final int offset) {
		return this.segment.offsetOf(this.textOffset + offset);
	}
	
	public int toCursorOffset(final int documentOffset) {
		return this.segment.toTextOffset(documentOffset);
	}
	
	public char getChar() {
		return this.text.charAt(this.textOffset);
	}
	
	/**
	 * Provides the character at the cursor's 0-based offset, where the given offset is not affected by the position of
	 * the cursor.
	 * 
	 * @param offset
	 *            the absolute offset of the character relative to this cursor
	 * @return the character
	 */
	public char getChar(final int offset) {
		return this.text.charAt(offset);
	}
	
	public boolean hasChar() {
		return (this.textOffset < this.text.length());
	}
	
	/**
	 * Provides the string at the cursor's 0-based offset, where the given offset is not affected by the position of the
	 * cursor.
	 * 
	 * @param offset
	 *            the absolute offset of the character relative to this cursor
	 * @param endIndex
	 *            the end index of the string to provide, exclusive
	 * @return the string
	 */
	public String getText(final int offset, final int endIndex) {
		return this.text.substring(offset, endIndex);
	}
	
	public char getPrevious() {
		return getPrevious(1);
	}
	
	public boolean hasPrevious() {
		return hasPrevious(1);
	}
	
	public boolean hasPrevious(final int offset) {
		return this.textOffset - offset >= 0;
	}
	
	public char getPrevious(final int offset) {
		final int charOffset = this.textOffset - offset;
		checkArgument(charOffset >= 0);
		return this.text.charAt(charOffset);
	}
	
	public char getNext() {
		return getNext(1);
	}
	
	public char getNext(final int offset) {
		checkArgument(offset >= 0);
		return this.text.charAt(this.textOffset + offset);
	}
	
	public String getTextAtOffset() {
		return this.text.substring(this.textOffset, this.text.length());
	}
	
	public String getTextAtOffset(final int length) {
		checkArgument(length > 0);
		return this.text.substring(this.textOffset, this.textOffset + length);
	}
	
	public boolean hasNext() {
		return hasNext(1);
	}
	
	public boolean hasNext(final int offset) {
		checkArgument(offset > 0);
		return (this.textOffset + offset < this.text.length());
	}
	
	
	public void advance() {
		if (this.textOffset < this.text.length()) {
			++this.textOffset;
		}
	}
	
	public void advance(final int count) {
		checkArgument(count >= 0);
		for (int x = 0; x < count; ++x) {
			advance();
		}
	}
	
	public void rewind(final int count) {
		checkArgument(count >= 0);
		for (int x = 0; x < count; ++x) {
			rewind();
		}
	}
	
	public void rewind() {
		if (this.textOffset > 0) {
			--this.textOffset;
		}
	}
	
	public Line getLineAtOffset() {
		return this.segment.getLineAtOffset(this.textOffset);
	}
	
	
	public Matcher setup(final Matcher matcher) {
		matcher.reset(this.text);
		matcher.region(this.textOffset, this.text.length());
		return matcher;
	}
	
	public Matcher setup(final Matcher matcher, final int offset) {
		matcher.reset(this.text);
		matcher.region(this.textOffset + offset, this.text.length());
		return matcher;
	}
	
	public int getMatcherOffset(final int matchOffset) {
		return this.segment.offsetOf(matchOffset);
	}
	
}
