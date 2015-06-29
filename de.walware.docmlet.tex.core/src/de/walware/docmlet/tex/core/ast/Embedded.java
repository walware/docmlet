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
import de.walware.ecommons.ltk.ast.IEmbeddingAstNode;

import de.walware.docmlet.tex.core.ast.TexAst.NodeType;


public class Embedded extends TexAstNode implements IEmbeddingAstNode {
	
	
	static class Inline extends Embedded {
		
		
		Inline(final TexAstNode parent, final int startOffset, final String type) {
			super(parent, startOffset, startOffset, type);
		}
		
		
		@Override
		public int getEmbedDescr() {
			return EMBED_INLINE;
		}
		
	}
	
	
	private final String foreignType;
	
	private IAstNode foreignNode;
	
	
	Embedded(final TexAstNode parent, final int startOffset, final int stopOffset,
			final String foreignType) {
		super(parent, startOffset, stopOffset);
		this.foreignType= foreignType;
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.EMBEDDED;
	}
	
	@Override
	public String getForeignTypeId() {
		return this.foreignType;
	}
	
	@Override
	public int getEmbedDescr() {
		return EMBED_CHUNK;
	}
	
	@Override
	public String getText() {
		return this.foreignType;
	}
	
	@Override
	public void setForeignNode(final IAstNode node) {
		this.foreignNode= node;
	}
	
	@Override
	public IAstNode getForeignNode() {
		return this.foreignNode;
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}
	
	@Override
	public int getChildCount() {
		return 0;
	}
	
	@Override
	public TexAstNode getChild(final int index) {
		throw new IndexOutOfBoundsException();
	}
	
	@Override
	public int getChildIndex(final IAstNode element) {
		return -1;
	}
	
	
	@Override
	public void acceptInChildren(final ICommonAstVisitor visitor) throws InvocationTargetException {
		if (this.foreignNode != null) {
			this.foreignNode.accept(visitor);
		}
	}
	
	@Override
	public void acceptInTex(final TexAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	@Override
	public void acceptInTexChildren(final TexAstVisitor visitor) throws InvocationTargetException {
	}
	
}
