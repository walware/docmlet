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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Cursor;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;


public abstract class Inline {
	
	
	private final Line line;
	
	private final int offset;
	
	private final int length;
	
	private final int cursorLength;
	
	
	public Inline(final Line line, final int offset, final int length, final int cursorLength) {
		this.line= checkNotNull(line);
		this.offset= offset;
		this.length= length;
		this.cursorLength= cursorLength;
		checkArgument(offset >= 0);
		checkArgument(length > 0);
	}
	
	
	public int getOffset() {
		return this.offset;
	}
	
	public int getLength() {
		return this.length;
	}
	
	public Line getLine() {
		return this.line;
	}
	
	
	public void createContext(final ProcessingContext context) {}
	
	
	protected int getCursorLength() {
		return this.cursorLength;
	}
	
	void apply(final ProcessingContext context, final List<Inline> inlines,
			final Cursor cursor, final boolean inBlock) {
		cursor.advance(getCursorLength());
		inlines.add(this);
	}
	
	InlinesSubstitution secondPass(final List<Inline> inlines) {
		return null;
	}
	
	public abstract void emit(ProcessingContext context,
			CommonmarkLocator locator, DocumentBuilder builder);
	
	
	@Override
	public int hashCode() {
		return Objects.hash(this.offset, this.length);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Inline other= (Inline) obj;
		return (this.offset == other.offset
				&& this.length == other.length);
	}
	
	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(getClass())
				.add("offset", getOffset())
				.add("length", getLength())
				.toString();
	}
	
}
