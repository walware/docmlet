/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.core.model;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.collections.ImList;
import de.walware.ecommons.ltk.core.impl.NameAccessAccumulator;

import de.walware.docmlet.tex.core.ast.TexAstNode;
import de.walware.docmlet.tex.core.model.TexElementName;
import de.walware.docmlet.tex.core.model.TexNameAccess;


public class RefLabelAccess extends TexNameAccess {
	
	
	public final static int A_READ =                       0x00000000;
	public final static int A_WRITE =                      0x00000002;
	
	
	private final NameAccessAccumulator<TexNameAccess> shared;
	
	private final TexAstNode node;
	private final TexAstNode nameNode;
	
	int flags;
	
	
	protected RefLabelAccess(final NameAccessAccumulator<TexNameAccess> shared, final TexAstNode node, final TexAstNode labelNode) {
		this.shared = shared;
		shared.getList().add(this);
		this.node = node;
		this.nameNode = labelNode;
	}
	
	
	@Override
	public int getType() {
		return LABEL;
	}
	
	@Override
	public String getSegmentName() {
		return this.shared.getLabel();
	}
	
	@Override
	public String getDisplayName() {
		return this.shared.getLabel();
	}
	
	@Override
	public TexElementName getNextSegment() {
		return null;
	}
	
	
	@Override
	public TexAstNode getNode() {
		return this.node;
	}
	
	@Override
	public TexAstNode getNameNode() {
		return this.nameNode;
	}
	
	@Override
	public ImList<? extends TexNameAccess> getAllInUnit() {
		return ImCollections.toList(this.shared.getList());
	}
	
	
	@Override
	public boolean isWriteAccess() {
		return ((this.flags & A_WRITE) != 0);
	}
	
}
