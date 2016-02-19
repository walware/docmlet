/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #     Stephan Wahlbrink (WalWare.de) - revised API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.commonmark.core;


public abstract class FilterLineSequence extends LineSequence {
	
	private static final Line STOP= new Line(0, 0, 0, "<STOP>", "\n");
	
	
	private final LineSequence delegate;
	
	private Line filteredLine;
	
	
	public FilterLineSequence(final LineSequence delegate) {
		this.delegate= delegate;
	}
	
	
	protected final LineSequence getDelegate() {
		return this.delegate;
	}
	
	@Override
	public Line getCurrentLine() {
		if (this.filteredLine == null) {
			Line line= this.delegate.getCurrentLine();
			if (line != null) {
				line= filter(line);
			}
			this.filteredLine= (line != null) ? line : STOP;
		}
		return (this.filteredLine != STOP) ? this.filteredLine : null;
	}
	
	@Override
	public Line getNextLine() {
		final Line line= this.delegate.getNextLine();
		if (line != null) {
			return filter(line);
		}
		return null;
	}
	
	@Override
	public void advance() {
		this.delegate.advance();
		this.filteredLine= null;
	}
	
	
	protected abstract Line filter(Line line);
	
}
