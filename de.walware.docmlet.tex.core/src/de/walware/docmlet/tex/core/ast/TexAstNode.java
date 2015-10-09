/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.ast;

import java.lang.reflect.InvocationTargetException;

import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.ltk.core.impl.AbstractAstNode;

import de.walware.docmlet.tex.core.ast.TexAst.NodeType;


public abstract class TexAstNode extends AbstractAstNode
		implements IAstNode {
	
	
	protected static final TexAstNode[] NO_CHILDREN= new TexAstNode[0];
	
	
	int status;
	
	TexAstNode texParent;
	
	int beginOffset;
	int endOffset;
	
	
	TexAstNode() {
	}
	
	TexAstNode(final TexAstNode parent, final int beginOffset, final int endOffset) {
		this.texParent= parent;
		
		this.beginOffset= beginOffset;
		this.endOffset= endOffset;
	}
	
	
	public abstract NodeType getNodeType();
	
	@Override
	public final int getStatusCode() {
		return this.status;
	}
	
	public final TexAstNode getTexParent() {
		return this.texParent;
	}
	
	@Override
	public IAstNode getParent() {
		return this.texParent;
	}
	
	
	@Override
	public final int getOffset() {
		return this.beginOffset;
	}
	
	@Override
	public final int getEndOffset() {
		return this.endOffset;
	}
	
	@Override
	public final int getLength() {
		return this.endOffset - this.beginOffset;
	}
	
	
	@Override
	public abstract TexAstNode getChild(final int index);
	
	
	public abstract void acceptInTex(TexAstVisitor visitor) throws InvocationTargetException;
	
	public abstract void acceptInTexChildren(TexAstVisitor visitor) throws InvocationTargetException;
	
	
}
