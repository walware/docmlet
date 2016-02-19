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

package de.walware.docmlet.wikitext.internal.commonmark.core.inlines;

import java.util.List;

import de.walware.docmlet.wikitext.internal.commonmark.core.Line;

public abstract class PotentialStyleDelimiterInfo {
	
	public static final byte FLANKING= 1 << 0;
	public static final byte FLANKING_UNDERSCORE= 1 << 1;
	public static final byte NO_SPACE= 1 << 4;
	
	
	static abstract class EmphasisDelimiter extends PotentialStyleDelimiterInfo {
		
		
		@Override
		public boolean isPotentialSequence(final int length) {
			return true;
		}
		
		@Override
		public Inline createStyleInline(final int size, final Line line,
				final int offset, final int length, final List<Inline> contents) {
			switch (size) {
			case 1:
				return new Emphasis(line, offset, length, contents);
			case 2:
				return new Strong(line, offset, length, contents);
			default:
				throw new IllegalStateException();
			}
		}
		
	}
	
	static abstract class ExtDelimiter extends PotentialStyleDelimiterInfo {
		
		private final byte flags;
		
		public ExtDelimiter(final boolean size1, final boolean size2) {
			byte f= 0;
			if (size1) {
				f|= 1;
			}
			if (size2) {
				f|= 2;
			}
			if (f == 0) {
				throw new IllegalArgumentException();
			}
			this.flags= f;
		}
		
		@Override
		public boolean isPotentialSequence(final int length) {
			switch (this.flags & 3) {
			case 2:
				return (length >= 2);
			case 1:
			case 3:
			default:
				return true;
			}
		}
		
		@Override
		public int getSize(final int openingLength, final int closingLength) {
			switch (this.flags & 3) {
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
			default:
				return super.getSize(openingLength, closingLength);
			}
		}
		
		@Override
		public byte getRequirements(final int size) {
			return (size == 1) ? NO_SPACE : FLANKING;
		}
		
	}
	
	
	static final PotentialStyleDelimiterInfo DEFAULT_ASTERISK= new EmphasisDelimiter() {
		
		@Override
		public char getChar() {
			return '*';
		}
		
		@Override
		public byte getRequirements(final int size) {
			return FLANKING;
		}
		
	};
	
	static final PotentialStyleDelimiterInfo DEFAULT_UNDERSCORE= new EmphasisDelimiter() {
		
		@Override
		public char getChar() {
			return '_';
		}
		
		@Override
		public byte getRequirements(final int size) {
			return FLANKING_UNDERSCORE;
		}
		
	};
	
	
	public abstract char getChar();
	
	public abstract boolean isPotentialSequence(int length);
	
	public int getSize(final int openingLength, final int closingLength) {
		if (openingLength < 3 || closingLength < 3) {
			return (openingLength > closingLength) ? closingLength : openingLength;
		}
		return (closingLength % 2 == 0) ? 2 : 1;
	}
	
	public abstract byte getRequirements(int size);
	
	public abstract Inline createStyleInline(int size, Line line, int offset, int length, List<Inline> contents);
	
	
	@Override
	public String toString() {
		return "DelimiterInfo '" + getChar() + "'";
	}
	
}
