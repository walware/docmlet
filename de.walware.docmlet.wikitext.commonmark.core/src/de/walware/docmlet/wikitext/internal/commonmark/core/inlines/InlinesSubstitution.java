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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;


class InlinesSubstitution {
		
	
	private final Inline first;
	
	private final Inline last;
	
	private final ImList<Inline> substitution;
	
	
	public InlinesSubstitution(final Inline first, final Inline last, final List<? extends Inline> substitution) {
		this.first= checkNotNull(first);
		this.last= checkNotNull(last);
		this.substitution= ImCollections.toList(substitution);
	}
	
	
	public List<Inline> apply(final List<Inline> inlines) {
		final List<Inline> builder= new ArrayList<>();
		
		boolean inReplacementSegment= false;
		for (final Inline inline : inlines) {
			if (inline == this.first) {
				inReplacementSegment= true;
				builder.addAll(this.substitution);
			}
			if (!inReplacementSegment) {
				builder.add(inline);
			}
			if (inReplacementSegment && inline == this.last) {
				inReplacementSegment= false;
			}
		}
		return builder;
	}
	
}
