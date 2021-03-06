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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import de.walware.docmlet.wikitext.internal.commonmark.core.Line;


abstract class InlineWithText extends Inline {
	
	
	protected final String text;
	
	
	public InlineWithText(final Line line, final int offset, final int length, final int cursorLength,
			final String text) {
		super(line, offset, length, cursorLength);
		this.text= checkNotNull(text);
	}
	
	
	public String getText() {
		return this.text;
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(getOffset(), getLength(), this.text);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		final InlineWithText other= (InlineWithText) obj;
		return this.text.equals(other.text);
	}
	
	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(getClass())
				.add("offset", getOffset())
				.add("length", getLength())
				.add("text", getText())
				.toString();
	}
	
}
