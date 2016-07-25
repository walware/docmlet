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

import de.walware.ecommons.text.LineInformationCreator;


public abstract class LineSequence {
	
	
	public static LineSequence create(final String text) {
		return new ContentLineSequence(text, new LineInformationCreator().create(text));
	}
	
	
	public abstract LineSequence lookAhead();
	
	public LineSequence lookAhead(final int lineNumber) {
		final LineSequence lookAhead= lookAhead();
		Line line= lookAhead.getCurrentLine();
		if (line != null) {
			if (line.getLineNumber() > lineNumber) {
				throw new IllegalArgumentException("lineNumber= " + lineNumber);
			}
			do {
				if (line.getLineNumber() >= lineNumber) {
					break;
				}
				else {
					lookAhead.advance();
					line= lookAhead.getCurrentLine();
				}
			}
			while (line != null);
		}
		return lookAhead;
	}
	
	
	public abstract Line getCurrentLine();
	
	public abstract Line getNextLine();
	
	public abstract void advance();
	
	
	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(LineSequence.class)
				.add("currentLine", getCurrentLine())
				.add("nextLine", getNextLine())
				.toString();
	}
	
}
