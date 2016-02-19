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


public class AllCharactersSpan extends SourceSpan {
	
	
	@Override
	public Inline createInline(final ProcessingContext context, final Cursor cursor) {
		final int startOffset= cursor.getOffset();
		final int endOffset= cursor.getOffset(1);
		
		return new Characters(cursor.getLineAtOffset(), startOffset, endOffset - startOffset, 1,
				Character.toString(cursor.getChar()) );
	}
	
}
