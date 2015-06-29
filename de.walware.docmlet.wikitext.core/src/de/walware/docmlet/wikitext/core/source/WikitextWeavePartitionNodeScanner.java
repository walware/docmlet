/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.core.source;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;

import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNode;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNodeScan;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNodeScan.BreakException;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNodeScanner;
import de.walware.ecommons.text.core.treepartitioner.WrappedPartitionScan;

import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;


public class WikitextWeavePartitionNodeScanner extends WikitextPartitionNodeScanner {
	
	
	private WrappedPartitionScan foreignScan;
	
	private EmbeddingAttributes embeddingAttributes;
	
	protected ITreePartitionNode embeddingNode;
	protected int embeddedContentBeginOffset;
	protected int embeddedContentEndOffset;
	
	
	public WikitextWeavePartitionNodeScanner(final IMarkupLanguage markupLanguage) {
		super(markupLanguage);
	}
	
	public WikitextWeavePartitionNodeScanner(final IMarkupLanguage markupLanguage,
			final boolean templateMode) {
		super(markupLanguage, templateMode);
	}
	
	
	@Override
	public void execute(final ITreePartitionNodeScan scan) throws BreakException {
		this.foreignScan= new WrappedPartitionScan(scan);
		
		super.execute(scan);
		
		this.foreignScan= null;
	}
	
	@Override
	protected void init() {
		this.embeddingNode= null;
		this.embeddingAttributes= null;
		
		super.init();
	}
	
	protected WrappedPartitionScan getForeignScan() {
		return this.foreignScan;
	}
	
	
	protected void setEmbedded(final ITreePartitionNode node,
			final EmbeddingAttributes attributes) {
		this.embeddingNode= node;
		this.embeddedContentBeginOffset= getBeginOffset() + attributes.getContentBeginOffset();
		this.embeddedContentEndOffset= getBeginOffset() + attributes.getContentEndOffset();
	}
	
	protected void executeForeignScanner(final ITreePartitionNodeScanner scanner) {
		final WrappedPartitionScan scan= getForeignScan();
		scan.init(this.embeddedContentBeginOffset, this.embeddedContentEndOffset, this.embeddingNode);
		scanner.execute(scan);
		scan.exit();
		
		this.embeddingNode= null;
	}
	
	
	@Override
	public void beginBlock(final BlockType type, final Attributes attributes) {
		if (attributes instanceof EmbeddingAttributes) {
			this.embeddingAttributes= (EmbeddingAttributes) attributes;
			beginEmbeddingBlock(type, this.embeddingAttributes);
			return;
		}
		super.beginBlock(type, attributes);
	}
	
	protected void beginEmbeddingBlock(final BlockType type, final EmbeddingAttributes attributes) {
		throw new UnsupportedOperationException("blockType= " + type + ", foreignType= " + attributes.getForeignType()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public void endBlock() {
		if (this.embeddingAttributes != null) {
			endEmbeddingBlock(getType(), this.embeddingAttributes);
			this.embeddingAttributes= null;
			return;
		}
		super.endBlock();
	}
	
	protected void endEmbeddingBlock(final WikitextPartitionNodeType type,
			final EmbeddingAttributes attributes) {
		throw new IllegalStateException();
	}
	
	@Override
	public void beginSpan(final SpanType type, final Attributes attributes) {
		if (attributes instanceof EmbeddingAttributes) {
			this.embeddingAttributes= (EmbeddingAttributes) attributes;
			beginEmbeddingSpan(type, this.embeddingAttributes);
			return;
		}
		super.beginSpan(type, attributes);
	}
	
	protected void beginEmbeddingSpan(final SpanType type, final EmbeddingAttributes attributes) {
		throw new UnsupportedOperationException("spanType= " + type + ", foreignType= " + attributes.getForeignType()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public void endSpan() {
		if (this.embeddingAttributes != null) {
			endEmbeddingSpan(getType(), this.embeddingAttributes);
			this.embeddingAttributes= null;
			return;
		}
		super.endSpan();
	}
	
	protected void endEmbeddingSpan(final WikitextPartitionNodeType type,
			final EmbeddingAttributes attributes) {
		throw new IllegalStateException();
	}
	
}
