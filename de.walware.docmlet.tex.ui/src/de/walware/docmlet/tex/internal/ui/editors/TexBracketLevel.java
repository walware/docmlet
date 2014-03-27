/*=============================================================================#
 # Copyright (c) 2012-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.ui.editors;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.LinkedPosition;

import de.walware.ecommons.text.ui.BracketLevel;

import de.walware.docmlet.tex.core.source.LtxHeuristicTokenScanner;


public class TexBracketLevel extends BracketLevel {
	
	
	public static final class CurlyBracketPosition extends InBracketPosition {
		
		public CurlyBracketPosition(final IDocument document, final int offset, final int length,
				final int sequence) {
			super(document, offset, length, sequence);
		}
		
		@Override
		public char getOpenChar() {
			return '{';
		}
		
		@Override
		public char getCloseChar() {
			return '}';
		}
		
		@Override
		protected boolean isEscaped(final int offset) throws BadLocationException {
			return LtxHeuristicTokenScanner.isEscaped(getDocument(), offset);
		}
		
	}
	
	public static final class SquareBracketPosition extends InBracketPosition {
		
		public SquareBracketPosition(final IDocument document, final int offset, final int length,
				final int sequence) {
			super(document, offset, length, sequence);
		}
		
		@Override
		public char getOpenChar() {
			return '[';
		}
		
		@Override
		public char getCloseChar() {
			return ']';
		}
		
		@Override
		protected boolean isEscaped(final int offset) throws BadLocationException {
			return LtxHeuristicTokenScanner.isEscaped(getDocument(), offset);
		}
		
	}
	
	public static final class ParanthesisBracket extends InBracketPosition {
		
		public ParanthesisBracket(final IDocument document, final int offset, final int length,
				final int sequence) {
			super(document, offset, length, sequence);
		}
		
		@Override
		public char getOpenChar() {
			return '(';
		}
		
		@Override
		public char getCloseChar() {
			return ')';
		}
		
		@Override
		protected boolean isEscaped(final int offset) throws BadLocationException {
			return LtxHeuristicTokenScanner.isEscaped(getDocument(), offset);
		}
		
	}
	
	public static final class MathDollarBracket extends InBracketPosition {
		
		public MathDollarBracket(final IDocument document, final int offset, final int length,
				final int sequence) {
			super(document, offset, length, sequence);
		}
		
		@Override
		public char getOpenChar() {
			return '$';
		}
		
		@Override
		public char getCloseChar() {
			return '$';
		}
		
		@Override
		protected boolean isEscaped(final int offset) throws BadLocationException {
			return LtxHeuristicTokenScanner.isEscaped(getDocument(), offset);
		}
		
		private int countBackward(int offset) throws BadLocationException {
			final IDocument document = getDocument();
			int count = 0;
			while (offset > 0) {
				if (document.getChar(offset--) == '$') {
					count++;
				}
			}
			return count;
		}
		
		@Override
		public boolean matchesClose(final BracketLevel level, final int offset, final char character)
				throws BadLocationException {
			return (super.matchesClose(level, offset, character)
					&& (getLength() > 0 || countBackward(offset-1) == 2) );
		}
		
	}
	
	
	public static InBracketPosition createPosition(final char c, final IDocument document,
			final int offset, final int length, final int sequence) {
		switch(c) {
		case '{':
			return new CurlyBracketPosition(document, offset, length, sequence);
		case '[':
			return new SquareBracketPosition(document, offset, length, sequence);
		case '(':
			return new ParanthesisBracket(document, offset, length, sequence);
		case '$':
			return new MathDollarBracket(document, offset, length, sequence);
		default:
			throw new IllegalArgumentException("Invalid position type: " + c);
		}
	}
	
	
	public TexBracketLevel(final IDocument doc, final String partitioning,
			final List<LinkedPosition> positions, final int mode) {
		super(doc, partitioning, positions, mode);
	}
	
}
