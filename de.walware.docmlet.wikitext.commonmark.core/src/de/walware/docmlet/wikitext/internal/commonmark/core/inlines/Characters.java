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

import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Cursor;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;


public class Characters extends InlineWithText {
	
	
	static boolean append(final List<Inline> inlines,
			final int newIndex, final InlineWithText newInline) {
		final Inline previous;
		if (newIndex > 0 && (previous= inlines.get(newIndex - 1)) instanceof Characters) {
			final Characters lastCharacters= (Characters) previous;
			
			final Characters substitution= new Characters(lastCharacters.getLine(),
					lastCharacters.getOffset(),
					newInline.getOffset() + newInline.getLength() - lastCharacters.getOffset(),
					lastCharacters.getCursorLength() + newInline.getCursorLength(),
					lastCharacters.getText() + newInline.getText() );
			inlines.set(newIndex - 1, substitution);
			return true;
		}
		return false;
	}
	
	
	public Characters(final Line line, final int offset, final int length, final int cursorLength,
			final String text) {
		super(line, offset, length, cursorLength, text);
	}
	
	
	@Override
	public void apply(final ProcessingContext context, final List<Inline> inlines,
			final Cursor cursor, final boolean inBlock) {
		if (append(inlines, inlines.size(), this)) {
			cursor.advance(getLength());
			return;
		}
		
		super.apply(context, inlines, cursor, inBlock);
	}
	
	@Override
	public void emit(final ProcessingContext context,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		builder.characters(this.text);
	}
	
}
