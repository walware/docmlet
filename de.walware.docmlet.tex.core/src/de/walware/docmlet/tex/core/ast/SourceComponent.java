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
import de.walware.ecommons.ltk.ast.ICommonAstVisitor;

import de.walware.docmlet.tex.core.ast.TexAst.NodeType;


public final class SourceComponent extends ContainerNode {
	
	
	private IAstNode parent;
	
	
	public SourceComponent() {
	}
	
	public SourceComponent(final IAstNode parent, final int beginOffset, final int endOffset) {
		super(null, beginOffset, endOffset);
		this.parent= parent;
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.SOURCELINES;
	}
	
	
	@Override
	public IAstNode getParent() {
		return this.parent;
	}
	
	@Override
	public final boolean hasChildren() {
		return (this.children.length > 0);
	}
	
	@Override
	public final int getChildCount() {
		return this.children.length;
	}
	
	@Override
	public final TexAstNode getChild(final int index) {
		return this.children[index];
	}
	
	@Override
	public final int getChildIndex(final IAstNode element) {
		for (int i= 0; i < this.children.length; i++) {
			if (this.children[i] == element) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public final void acceptInChildren(final ICommonAstVisitor visitor) throws InvocationTargetException {
		for (final TexAstNode child : this.children) {
			visitor.visit(child);
		}
	}
	
	@Override
	public void acceptInTex(final TexAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	@Override
	public final void acceptInTexChildren(final TexAstVisitor visitor) throws InvocationTargetException {
		for (final TexAstNode child : this.children) {
			child.acceptInTex(visitor);
		}
	}
	
	
	@Override
	void setEndNode(final int endOffset, final TexAstNode endNode) {
		this.endOffset= endOffset;
	}
	
	@Override
	void setMissingEnd() {
		if (this.children.length > 0) {
			this.endOffset= this.children[this.children.length-1].endOffset;
		}
	}
	
}
