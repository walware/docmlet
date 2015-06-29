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

package de.walware.docmlet.wikitext.internal.ui.sourceediting;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;

import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNode;
import de.walware.ecommons.text.core.treepartitioner.TreePartition;

public class MarkupDamagerRepairer extends DefaultDamagerRepairer {
	
	
	public MarkupDamagerRepairer(final ITokenScanner scanner) {
		super(scanner);
	}
	
	
	@Override
	public IRegion getDamageRegion(final ITypedRegion partition, final DocumentEvent e,
			final boolean documentPartitioningChanged) {
		if (partition instanceof TreePartition) {
			final ITreePartitionNode treeNode= ((TreePartition) partition).getTreeNode();
			if (treeNode.getParent() != null) {
				return ((TreePartition) partition).getTreeNode();
			}
		}
		return partition;
	}
	
}
