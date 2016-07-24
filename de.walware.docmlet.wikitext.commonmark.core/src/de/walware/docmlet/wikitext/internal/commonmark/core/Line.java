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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Matcher;

import org.eclipse.jface.text.IRegion;


public class Line implements IRegion {
	
	
	private static class LazyLine extends Line {
		
		
		public LazyLine(final int lineNumber, final int offset, final int column,
				final String text, final String lineDelimiter,
				final int indentColumns, final int indentLength) {
			super(lineNumber, offset, column, text, lineDelimiter, indentColumns, indentLength);
		}
		
		
		@Override
		public boolean isLazy() {
			return true;
		}
		
		@Override
		public LazyLine lazy() {
			return this;
		}
		
	}
	
	
	private static final String[] SPACES= new String[] {
			"", " ", "  ", "   ", "    "
	};
	
	
	private final int offset;
	
	private final int column;
	
	private final String text;
	
	private int indentColumns= -1;
	private int indentLength= -1;
	
	private final int lineNumber;
	
	private final String lineDelimiter;
	
	
	public Line(final int lineNumber, final int offset, final int column,
			final String text, final String lineDelimiter) {
		if (lineNumber < 0) {
			throw new IllegalArgumentException("lineNumber= " + lineNumber);
		}
		if (offset < 0) {
			throw new IllegalArgumentException("offset= " + offset);
		}
		if (column < 0) {
			throw new IllegalArgumentException("column= " + column);
		}
		
		this.lineNumber= lineNumber;
		this.offset= offset;
		this.column= column;
		this.text= checkNotNull(text);
		this.lineDelimiter= checkNotNull(lineDelimiter);
		
		computeIndent();
	}
	
	private Line(final int lineNumber, final int offset, final int column,
			final String text, final String lineDelimiter,
			final int indentColumns, final int indentLength) {
		this.lineNumber= lineNumber;
		this.offset= offset;
		this.column= column;
		this.text= text;
		this.lineDelimiter= lineDelimiter;
		
		this.indentColumns= indentColumns;
		this.indentLength= indentLength;
	}
	
	
	public boolean isBlank() {
		return (this.text.length() == this.indentLength);
	}
	
	public String getText() {
		return this.text;
	}
	
	/**
	 * Provides the 0-based offset of the first character of the line.
	 * 
	 * @return the line offset
	 */
	@Override
	public int getOffset() {
		return this.offset;
	}
	
	/**
	 * Returns the length of the line including the line delimiter.
	 * 
	 * @return the length
	 */
	@Override
	public int getLength() {
		return this.text.length() + this.lineDelimiter.length();
	}
	
	/**
	 * Provides the 0-based column of the first character of the line.
	 * 
	 * @return the line column
	 */
	public int getColumn() {
		return this.column;
	}
	
	/**
	 * Returns the 0-based column of the character at the specified offset.
	 * 
	 * @return the column
	 */
	public int getColumn(final int offset) {
		return (offset == this.indentLength) ?
				(this.column + this.indentColumns) : computeColumn(offset);
	}
	
	
	/**
	 * Returns the indent by space and tab chars at the start of this line.
	 * 
	 * @return the indent in columns
	 */
	public int getIndent() {
		return this.indentColumns;
	}
	
	public int getIndentLength() {
		return this.indentLength;
	}
	
	
	public String getTextContent(final boolean trimEnd) {
		return (trimEnd) ?
				this.text.substring(this.indentLength, computeTrimEnd()) :
				this.text.substring(this.indentLength);
	}
	
	public int getTextContentOffset() {
		return this.offset + this.indentLength;
	}
	
	public String getCodeContent() {
		if (this.text.isEmpty()
				|| this.indentLength == 0
				|| this.column % 4 == 0
				|| this.text.charAt(0) != '\t') {
			return this.text;
		}
		return SPACES[this.column % 4] + this.text.substring(1);
	}
	
	
	/**
	 * Provides the 0-based line number.
	 * 
	 * @return the line number
	 */
	public int getLineNumber() {
		return this.lineNumber;
	}
	
	
	/**
	 * Provides a segment of this line, with {@link #getText() text}.
	 * 
	 * @param offset
	 *            the 0-based offset of the {@link #getText() text} of this line
	 * @param length
	 *            the length of the {@link #getText() text} from the given {@code offset}
	 * @return the segment
	 */
	public Line segment(final int offset, final int length) {
		if (offset < 0 || offset > this.text.length()) {
			throw new IllegalArgumentException("offset= " + offset);
		}
		if (length < 0 || length > this.text.length() - offset) {
			throw new IllegalArgumentException("length= " + length);
		}
		final int offsetColumn= getColumn(offset);
		return new Line(this.lineNumber, this.offset + offset, offsetColumn,
				this.text.substring(offset, offset + length),
				(offset + length == this.text.length()) ? this.lineDelimiter : "" );
	}
	
	public Line segmentByIndent(final int indent) {
		final int indentLength= (indent == this.indentColumns) ?
				this.indentLength : computeLength(indent);
		if (indentLength == -1) {
			return segment(this.text.length(), 0);
		}
		if (indent <= this.indentColumns) {
			return new Line(this.lineNumber, this.offset + indentLength, this.column + indent,
					this.text.substring(indentLength), this.lineDelimiter,
					this.indentColumns - indent,
					this.indentLength - indentLength );
		}
		else {
			return new Line(this.lineNumber, this.offset + indentLength, this.column + indent,
					this.text.substring(indentLength), this.lineDelimiter );
		}
	}
	
	
	public boolean isLazy() {
		return false;
	}
	
	public Line lazy() {
		return new LazyLine(this.lineNumber, this.offset, this.column,
				this.text, this.lineDelimiter, this.indentColumns, this.indentLength );
	}
	
	
	public Matcher setup(final Matcher matcher) {
		return matcher.reset(this.text);
	}
	
	public Matcher setupIndent(final Matcher matcher) {
		return setup(matcher, true, false);
	}
	
	public Matcher setup(final Matcher matcher, final boolean trimIndent, final boolean trimEnd) {
		matcher.reset(this.text);
		if (trimIndent || trimEnd) {
			final int start= (trimIndent) ? getIndentLength() : 0;
			final int end= (trimEnd && start < this.text.length()) ? computeTrimEnd() : this.text.length();
			if (start != 0 || end != this.text.length()) {
				matcher.region(start, end);
			}
		}
		return matcher;
	}
	
	
	private void computeIndent() {
		final String text= this.text;
		int column= this.column;
		int idx= 0;
		ITER_CHAR: while (idx < text.length()) {
			final char c= text.charAt(idx);
			switch (c) {
			case ' ':
				column++;
				idx++;
				continue;
			case '\t':
				column+= 4 - (column % 4);
				idx++;
				continue;
			default:
				break ITER_CHAR;
			}
		}
		this.indentColumns= column - this.column;
		this.indentLength= idx;
	}
	
	private int computeLength(final int indent) {
		if (indent == 0) {
			return 0;
		}
		final String text= this.text;
		int column= this.column;
		int idx= 0;
		while (idx < text.length()) {
			final char c= text.charAt(idx);
			switch (c) {
			case '\t':
				column+= 4 - (column % 4);
				if (column - this.column > indent) {
					return idx;
				}
				if (column - this.column >= indent) {
					return idx + 1;
				}
				idx++;
				continue;
			default:
				column++;
				if (column - this.column >= indent) {
					return idx + 1;
				}
				idx++;
				continue;
			}
		}
		return -1;
	}
	
	private int computeColumn(final int offset) {
		assert (offset <= this.text.length());
		
		final String text= this.text;
		int column= this.column;
		int idx= 0;
		while (idx < offset) {
			final char c= text.charAt(idx);
			switch (c) {
			case '\t':
				column+= 4 - (column % 4);
				idx++;
				continue;
			default:
				column++;
				idx++;
				continue;
			}
		}
		return column;
	}
	
	private int computeTrimEnd() {
		final String text= this.text;
		for (int idx= text.length() - 1; idx >= 0; idx--) {
			switch (text.charAt(idx)) {
			case ' ':
			case '\t':
				continue;
			default:
				return idx + 1;
			}
		}
		return 0;
	}
	
	
	@Override
	public String toString() {
		final StringBuilder sb= new StringBuilder("Line");
		sb.append(" (" + "lineNumber= ").append(this.lineNumber);
		sb.append(", " + "offset= ").append(this.offset);
		sb.append(", " + "column= ").append(this.column);
		sb.append(")");
		sb.append("\n\t" + "text= ").append(ToStringHelper.toStringValue(this.text));
		return sb.toString();
	}
	
}
