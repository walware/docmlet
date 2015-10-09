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
import de.walware.ecommons.ltk.core.impl.AbstractAstNode;

import de.walware.docmlet.wikitext.core.ast.WikitextAst.NodeType;


public abstract class WikitextAstNode extends AbstractAstNode
		implements IAstNode {
	
	
	protected static final WikitextAstNode[] NO_CHILDREN= new WikitextAstNode[0];
	
	
	WikitextAstNode parent;
	
	int beginOffset;
	int endOffset;
	
	int status;
	
	
	WikitextAstNode() {
	}
	
	WikitextAstNode(final WikitextAstNode parent, final int beginOffset, final int endOffset) {
		this.parent= parent;
		
		this.beginOffset= beginOffset;
		this.endOffset= endOffset;
	}
	
	
	public abstract NodeType getNodeType();
	
	@Override
	public final int getStatusCode() {
		return this.status;
	}
	
	public final WikitextAstNode getWikitextParent() {
		return this.parent;
	}
	
	@Override
	public final IAstNode getParent() {
		return this.parent;
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
	
	public String getLabel() {
		return null;
	}
	
	
	@Override
	public abstract WikitextAstNode getChild(final int index);
	
	
	public abstract void acceptInWikitext(WikitextAstVisitor visitor) throws InvocationTargetException;
	
	public abstract void acceptInWikitextChildren(WikitextAstVisitor visitor) throws InvocationTargetException;
	
	
}
