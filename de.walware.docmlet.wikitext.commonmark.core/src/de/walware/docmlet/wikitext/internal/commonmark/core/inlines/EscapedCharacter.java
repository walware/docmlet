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

import java.util.Objects;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;


public class EscapedCharacter extends Inline {
	
	
	private final char character;
	
	
	public EscapedCharacter(final Line line, final int offset, final char c) {
		super(line, offset, 2, 2);
		this.character= c;
	}
	
	
	public char getCharacter() {
		return this.character;
	}
	
	@Override
	public void emit(final ProcessingContext context,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		builder.characters(Character.toString(this.character));
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(getOffset(), getLength(), this.character);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		final EscapedCharacter other= (EscapedCharacter) obj;
		return (this.character == other.character);
	}
	
}
