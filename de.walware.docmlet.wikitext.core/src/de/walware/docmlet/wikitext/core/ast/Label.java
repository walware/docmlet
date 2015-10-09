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

package de.walware.docmlet.wikitext.core.ast;

import java.lang.reflect.InvocationTargetException;

import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.ltk.ast.ICommonAstVisitor;

import de.walware.docmlet.wikitext.core.ast.WikitextAst.NodeType;


public final class Label extends WikitextAstNode {
	
	
	private final String label;
	
	
	Label(final WikitextAstNode parent, final int beginOffset, final int endOffset,
			final String label) {
		super(parent, beginOffset, endOffset);
		
		this.label= label;
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.LABEL;
	}
	
	@Override
	public String getText() {
		return this.label;
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
	public WikitextAstNode getChild(final int index) {
		throw new IndexOutOfBoundsException();
	}
	
	@Override
	public int getChildIndex(final IAstNode element) {
		return -1;
	}
	
	@Override
	public void acceptInChildren(final ICommonAstVisitor visitor) throws InvocationTargetException {
	}
	
	@Override
	public void acceptInWikitext(final WikitextAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	@Override
	public void acceptInWikitextChildren(final WikitextAstVisitor visitor) throws InvocationTargetException {
	}
	
}
