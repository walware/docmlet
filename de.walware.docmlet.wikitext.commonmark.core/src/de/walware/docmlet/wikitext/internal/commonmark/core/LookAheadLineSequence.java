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

package de.walware.docmlet.wikitext.internal.commonmark.core;

import static com.google.common.base.Preconditions.checkState;


class LookAheadLineSequence extends LineSequence {
	
	
	private final ContentLineSequence lineSequence;
	
	private Line currentLine;
	
	private final Line referenceLine;
	
	private int index;
	
	
	public LookAheadLineSequence(final ContentLineSequence lineSequence) {
		this.lineSequence = lineSequence;
		this.currentLine = lineSequence.getCurrentLine();
		this.referenceLine = this.currentLine;
		this.index = -1;
	}
	
	public LookAheadLineSequence(final LookAheadLineSequence lookAheadLineSequence) {
		this.lineSequence = lookAheadLineSequence.lineSequence;
		this.currentLine = lookAheadLineSequence.currentLine;
		this.referenceLine = lookAheadLineSequence.referenceLine;
		this.index = lookAheadLineSequence.index;
	}
	
	
	@Override
	public LineSequence lookAhead() {
		return new LookAheadLineSequence(this);
	}
	
	
	@Override
	public Line getCurrentLine() {
		return this.currentLine;
	}
	
	@Override
	public Line getNextLine() {
		checkConcurrentModification();
		return this.lineSequence.getNextLine(this.index + 1);
	}
	
	@Override
	public void advance() {
		checkConcurrentModification();
		this.currentLine = getNextLine();
		++this.index;
	}
	
	private void checkConcurrentModification() {
		checkState(this.referenceLine == this.lineSequence.getCurrentLine());
	}
	
}
