/*=============================================================================#
 # Copyright (c) 2012-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.core.model;

import de.walware.jcommons.collections.ImList;

import de.walware.docmlet.tex.core.ast.TexAstNode;
import de.walware.docmlet.tex.core.model.TexElementName;
import de.walware.docmlet.tex.core.model.TexNameAccess;


public class EnvLabelAccess extends TexNameAccess {
	
	
	private final TexAstNode node;
	private final TexAstNode nameNode;
	
	ImList<EnvLabelAccess> all;
	
	
	protected EnvLabelAccess(final TexAstNode node, final TexAstNode labelNode) {
		this.node= node;
		this.nameNode= labelNode;
	}
	
	
	@Override
	public int getType() {
		return ENV;
	}
	
	@Override
	public String getSegmentName() {
		return this.node.getText();
	}
	
	@Override
	public String getDisplayName() {
		return this.node.getText();
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
		return this.all;
	}
	
	
	@Override
	public boolean isWriteAccess() {
		return false;
	}
	
}
