/*=============================================================================#
 # Copyright (c) 2012-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.core.model;

import java.util.List;

import de.walware.docmlet.tex.core.ast.TexAstNode;
import de.walware.docmlet.tex.core.model.TexElementName;
import de.walware.docmlet.tex.core.model.TexLabelAccess;


public class EnvLabelAccess extends TexLabelAccess {
	
	
	private final TexAstNode fNode;
	private final TexAstNode fNameNode;
	
	List<TexLabelAccess> fAll;
	
	
	protected EnvLabelAccess(final TexAstNode node, final TexAstNode labelNode) {
		fNode = node;
		fNameNode = labelNode;
	}
	
	
	@Override
	public int getType() {
		return ENV;
	}
	
	@Override
	public String getSegmentName() {
		return fNode.getText();
	}
	
	@Override
	public String getDisplayName() {
		return fNode.getText();
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
		return fAll;
	}
	
	
	@Override
	public boolean isWriteAccess() {
		return false;
	}
	
	
}
