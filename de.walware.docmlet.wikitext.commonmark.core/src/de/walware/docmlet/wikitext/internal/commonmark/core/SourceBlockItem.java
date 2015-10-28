/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.commonmark.core;

import java.util.ArrayList;
import java.util.List;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlocks.SourceBlockBuilder;
import de.walware.docmlet.wikitext.internal.commonmark.core.blocks.BlockWithNestedBlocks;


public class SourceBlockItem<T extends SourceBlock> {
	
	
	private final T type;
	
	private final SourceBlockItem<?> parent;
	private final List<SourceBlockItem<?>> nested;
	
	private ImList<Line> lines;
	
	
	public SourceBlockItem(final T type, final SourceBlockBuilder builder) {
		this.type= type;
		this.parent= builder.getCurrentItem();
		
		this.nested= (type instanceof BlockWithNestedBlocks) ?
				new ArrayList<SourceBlockItem<?>>() :
				ImCollections.<SourceBlockItem<?>>emptyList();
		if (this.parent != null) {
			this.parent.nested.add(this);
		}
		builder.setCurrentItem(this);
	}
	
	
	public T getType() {
		return this.type;
	}
	
	
	public SourceBlockItem<?> getParent() {
		return this.parent;
	}
	
	public List<SourceBlockItem<?>> getNested() {
		return this.nested;
	}
	
	public ImList<Line> getLines() {
		return this.lines;
	}
	
	void setLines(final ImList<Line> lines) {
		this.lines= lines;
	}
	
	
	@Override
	public String toString() {
		final StringBuilder sb= new StringBuilder("SourceBlock");
		sb.append(" type= ").append(this.type);
		sb.append(" (num nested= ").append(this.nested.size()).append(")");
		sb.append("\n").append(this.lines.toString());
		return sb.toString();
	}
	
}
