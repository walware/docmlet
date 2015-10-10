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

package de.walware.docmlet.wikitext.internal.commonmark.core.inlines;

import de.walware.docmlet.wikitext.internal.commonmark.core.Cursor;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;


public class BackslashEscapeSpan extends SourceSpan {
	
	
	public BackslashEscapeSpan() {
	}
	
	
	@Override
	public Inline createInline(final ProcessingContext context, final Cursor cursor) {
		final char c= cursor.getChar();
		if (c == '\\' && cursor.hasNext()) {
			if (cursor.getNext() == '\n') {
				final int startOffset= cursor.getOffset();
				final int endOffset= cursor.getOffset(2);
				
				return new HardLineBreak(cursor.getLineAtOffset(),
						startOffset, endOffset - startOffset, 2 );
			}
			else if (context.getHelper().isAsciiPunctuation(cursor.getNext())) {
				return new EscapedCharacter(cursor.getLineAtOffset(), cursor.getOffset(), cursor.getNext());
			}
		}
		return null;
	}
	
}
