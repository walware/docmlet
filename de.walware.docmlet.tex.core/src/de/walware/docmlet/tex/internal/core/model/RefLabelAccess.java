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

import java.util.ArrayList;
import java.util.List;

import de.walware.ecommons.collections.CollectionUtils;

import de.walware.docmlet.tex.core.ast.TexAstNode;
import de.walware.docmlet.tex.core.model.TexElementName;
import de.walware.docmlet.tex.core.model.TexLabelAccess;


public class RefLabelAccess extends TexLabelAccess {
	
	
	public final static int A_READ =                       0x00000000;
	public final static int A_WRITE =                      0x00000002;
	
	
	static class Shared {
		
		
		private final String fLabel;
		
		private List<TexLabelAccess> fAll;
		
		
		public Shared(final String label) {
			fLabel = label;
			fAll = new ArrayList<>(8);
		}
		
		
		public void finish() {
			fAll = CollectionUtils.asConstList(fAll);
		}
		
		public List<TexLabelAccess> getAll() {
			return fAll;
		}
		
	}
	
	
	private final Shared fShared;
	
	private final TexAstNode fNode;
	private final TexAstNode fNameNode;
	
	int fFlags;
	
	
	protected RefLabelAccess(final Shared shared, final TexAstNode node, final TexAstNode labelNode) {
		fShared = shared;
		shared.fAll.add(this);
		fNode = node;
		fNameNode = labelNode;
	}
	
	
	@Override
	public int getType() {
		return LABEL;
	}
	
	@Override
	public String getSegmentName() {
		return fShared.fLabel;
	}
	
	@Override
	public String getDisplayName() {
		return fShared.fLabel;
	}
	
	@Override
	public TexElementName getNextSegment() {
		return null;
	}
	
	
	@Override
	public TexAstNode getNode() {
		return fNode;
	}
	
	@Override
	public TexAstNode getNameNode() {
		return fNameNode;
	}
	
	@Override
	public List<? extends TexLabelAccess> getAllInUnit() {
		return fShared.fAll;
	}
	
	
	@Override
	public boolean isWriteAccess() {
		return ((fFlags & A_WRITE) != 0);
	}
	
}
