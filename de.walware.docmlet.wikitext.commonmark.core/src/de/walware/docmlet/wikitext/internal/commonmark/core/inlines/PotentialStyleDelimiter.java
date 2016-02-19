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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;


class PotentialStyleDelimiter extends InlineWithText {
	
	
	private final PotentialStyleDelimiterInfo info;
	
	private final boolean canOpen;
	
	private final boolean canClose;
	
	
	public PotentialStyleDelimiter(final PotentialStyleDelimiterInfo info,
			final Line line, final int offset, final int length,
			final String text, final boolean canOpen, final boolean canClose) {
		super(line, offset, length, length, text);
		this.info= info;
		this.canOpen= canOpen;
		this.canClose= canClose;
	}
	
	
	@Override
	public void emit(final ProcessingContext context,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		builder.characters(this.text);
	}
	
	@Override
	InlinesSubstitution secondPass(final List<Inline> inlines) {
		if (!this.canClose) {
			return null;
		}
		final int indexOfThis= inlines.indexOf(this);
		final char c= getText().charAt(0);
		final int openingDelimiterIndex= findLastOpeningDelimiter(inlines, indexOfThis, c);
		if (openingDelimiterIndex >= 0) {
			final PotentialStyleDelimiter openingDelimiter= (PotentialStyleDelimiter) inlines.get(openingDelimiterIndex);
			final int delimiterSize= this.info.getSize(openingDelimiter.getLength(), getLength());
			
			if ((this.info.getRequirements(delimiterSize) & PotentialStyleDelimiterInfo.NO_SPACE) != 0) {
				for (int index= openingDelimiterIndex + 1; index < indexOfThis; index++) {
					final Inline inline= inlines.get(index);
					if (inline instanceof Characters) {
						if (((Characters) inline).getText().indexOf(' ') >= 0) {
							return null;
						}
					}
					if (inline instanceof HardLineBreak) {
						if (((HardLineBreak) inline).getType() == ' ') {
							return null;
						}
					}
				}
			}
			
			final List<Inline> contents= InlineParser.secondPass(inlines.subList(openingDelimiterIndex + 1, indexOfThis));
			
			final int startOffset= openingDelimiter.getOffset();
			final int endOffset= getOffset() + getLength();
			
			final Inline styleInline= this.info.createStyleInline(delimiterSize,
					openingDelimiter.getLine(), startOffset,
					endOffset - startOffset, contents );
			
			final List<Inline> substitutionInlines= new ArrayList<>();
			if (delimiterSize < openingDelimiter.getLength()) {
				substitutionInlines.add(createRemainingOpeningDelimiter(openingDelimiter, delimiterSize));
			}
			substitutionInlines.add(styleInline);
			if (delimiterSize < getLength()) {
				substitutionInlines.add(createRemainingClosingDelimiter(delimiterSize));
			}
			
			return new InlinesSubstitution(openingDelimiter, this, substitutionInlines);
		}
		return null;
	}
	
	
	private int findLastOpeningDelimiter(final List<Inline> inlines,
			final int indexOfThis, final char c) {
		for (int index= indexOfThis - 1; index >= 0; --index) {
			final Inline inline= inlines.get(index);
			if (inline instanceof PotentialStyleDelimiter) {
				final PotentialStyleDelimiter previousDelimiter= (PotentialStyleDelimiter) inline;
				if (previousDelimiter.canOpen && previousDelimiter.info == this.info) {
					return index;
				}
			}
		}
		return -1;
	}
	
	private Inline createRemainingOpeningDelimiter(final PotentialStyleDelimiter openingDelimiter,
			final int delimiterSize) {
		final int newLength= openingDelimiter.getLength() - delimiterSize;
		if (this.info.isPotentialSequence(newLength)) {
			return new PotentialStyleDelimiter(this.info,
					openingDelimiter.getLine(),
					openingDelimiter.getOffset(), newLength,
					openingDelimiter.getText().substring(0, newLength),
					openingDelimiter.canOpen, openingDelimiter.canClose );
		}
		else {
			return new Characters(openingDelimiter.getLine(),
					openingDelimiter.getOffset(), newLength, newLength,
					openingDelimiter.getText().substring(0, newLength) );
		}
	}
	
	private Inline createRemainingClosingDelimiter(final int delimiterSize) {
		final int newLength= getLength() - delimiterSize;
		if (this.info.isPotentialSequence(newLength)) {
			return new PotentialStyleDelimiter(this.info,
					getLine(),
					getOffset() + delimiterSize, newLength,
					getText().substring(0, newLength),
					this.canOpen, this.canClose );
		}
		else {
			return new Characters(getLine(),
					getOffset() + delimiterSize, newLength, newLength,
					getText().substring(0, newLength) );
		}
	}
	
}
