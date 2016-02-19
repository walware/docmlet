/*=============================================================================#
 # Copyright (c) 2014-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.ui.sourceediting;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultTextDoubleClickStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;

import de.walware.ecommons.text.core.IPartitionConstraint;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNode;
import de.walware.ecommons.text.core.treepartitioner.TreePartition;

import de.walware.docmlet.wikitext.core.source.IWikitextDocumentConstants;


public class MarkupDoubleClickStrategy extends DefaultTextDoubleClickStrategy {
	
	
	private final String partitioning;
	
	private final IPartitionConstraint partitionConstrait;
	
	
	public MarkupDoubleClickStrategy(final String partitioning) {
		if (partitioning == null) {
			throw new NullPointerException("partitioning"); //$NON-NLS-1$
		}
		this.partitioning= partitioning;
		this.partitionConstrait= IWikitextDocumentConstants.WIKIDOC_DEFAULT_CONTENT_CONSTRAINT;
	}
	
	
	@Override
	protected IRegion findExtendedDoubleClickSelection(final IDocument document, final int offset) {
		try {
			final ITypedRegion partition= TextUtilities.getPartition(document, this.partitioning,
					offset, false );
			if (partition instanceof TreePartition
					&& this.partitionConstrait.matches(partition.getType()) ) {
				final ITreePartitionNode treeNode= ((TreePartition) partition).getTreeNode();
				if (offset == treeNode.getOffset()) {
					return treeNode;
				}
			}
		}
		catch (final BadLocationException e) {
		}
		return null;
	}
	
}
