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
import java.util.Objects;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.docmlet.wikitext.internal.commonmark.core.Line;


public abstract class InlineWithNestedContents extends Inline {
	
	
	private final ImList<Inline> contents;
	
	
	public InlineWithNestedContents(final Line line, final int offset,
			final int length, final int cursorLength, final List<? extends Inline> contents) {
		super(line, offset, length, cursorLength);
		
		this.contents= ImCollections.toList(contents);
	}
	
	
	public ImList<Inline> getContents() {
		return this.contents;
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(getOffset(), getLength(), getContents());
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		final InlineWithNestedContents other= (InlineWithNestedContents) obj;
		return getContents().equals(other.getContents());
	}
	
	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(getClass())
				.add("offset", getOffset())
				.add("length", getLength())
				.add("contents", getContents())
				.toString();
	}
	
}
