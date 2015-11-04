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
import de.walware.ecommons.ltk.ast.IEmbeddingAstNode;

import de.walware.docmlet.wikitext.core.ast.WikitextAst.NodeType;


public class Embedded extends ContainerNode implements IEmbeddingAstNode {
	
	
	private final String foreignType;
	private final int embedDescr;
	
	private IAstNode foreignNode;
	
	
	Embedded(final WikitextAstNode parent, final int beginOffset, final int endOffset,
			final String foreignType, final int embedDescr) {
		super(parent, beginOffset, endOffset);
		this.foreignType= foreignType;
		this.embedDescr= embedDescr;
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
		return this.embedDescr;
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
	public void acceptInChildren(final ICommonAstVisitor visitor) throws InvocationTargetException {
		// ambiguous! (at moment HTML => no foreignNode)
		if (this.foreignNode != null) {
			this.foreignNode.accept(visitor);
		}
		else {
			super.acceptInChildren(visitor);
		}
	}
	
	@Override
	public void acceptInWikitext(final WikitextAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
}
