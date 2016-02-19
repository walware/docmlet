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

import de.walware.docmlet.wikitext.internal.commonmark.core.Cursor;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;


public class PotentialBracketSpan extends SourceSpan {
	
	
	private PotentialBracketRegex shared;
	
	
	@Override
	public Inline createInline(final ProcessingContext context, final Cursor cursor) {
		final char c= cursor.getChar();
		if (c == '!' && cursor.hasNext() && cursor.getNext() == '[') {
			return new PotentialBracketOpenDelimiter(cursor.getLineAtOffset(), cursor.getOffset(), 2,
					cursor.getTextAtOffset(2) );
		}
		if (c == '[') {
			return new PotentialBracketOpenDelimiter(cursor.getLineAtOffset(), cursor.getOffset(), 1,
					cursor.getTextAtOffset(1) );
		}
		if (c == ']') {
			return new PotentialBracketCloseDelimiter(cursor.getLineAtOffset(), cursor.getOffset(),
					getShared() );
		}
		return null;
	}
	
	
	private PotentialBracketRegex getShared() {
		if (this.shared == null) {
			this.shared= new PotentialBracketRegex();
		}
		return this.shared;
	}
	
}
