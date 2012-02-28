/*******************************************************************************
 * Copyright (c) 2009-2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.core.ast;

import java.lang.reflect.InvocationTargetException;

import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.ltk.ast.ICommonAstVisitor;

import de.walware.docmlet.tex.core.ast.TexAst.NodeType;


public class Embedded extends TexAstNode {
	
	
	static class Inline extends Embedded {
		
		
		Inline(final TexAstNode parent, final int startOffset, final String type) {
			super(parent, startOffset, startOffset, type);
		}
		
		
		@Override
		public boolean isInline() {
			return true;
		}
		
	}
	
	
	final String fType;
	
	protected IAstNode fForeignNode;
	
	
	Embedded(final TexAstNode parent, final int startOffset, final int stopOffset,
			final String type) {
		super(parent, startOffset, stopOffset);
		fType = type;
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.EMBEDDED;
	}
	
	public boolean isInline() {
		return false;
	}
	
	@Override
	public String getText() {
		return fType;
	}
	
	public void setForeignNode(final IAstNode node) {
		fForeignNode = node;
	}
	
	public IAstNode getForeignNode() {
		return fForeignNode;
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
		if (fForeignNode != null) {
			fForeignNode.accept(visitor);
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
