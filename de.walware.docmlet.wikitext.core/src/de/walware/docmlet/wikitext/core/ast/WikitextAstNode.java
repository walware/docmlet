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

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.collections.ImList;
import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.ltk.ast.ICommonAstVisitor;

import de.walware.docmlet.wikitext.core.ast.WikitextAst.NodeType;


public abstract class WikitextAstNode implements IAstNode {
	
	
	protected static final WikitextAstNode[] NO_CHILDREN= new WikitextAstNode[0];
	
	private static final ImList<Object> NO_ATTACHMENT= ImCollections.emptyList();
	
	
	WikitextAstNode parent;
	
	int startOffset;
	int stopOffset;
	
	int status;
	
	private volatile ImList<Object> attachments= NO_ATTACHMENT;
	
	
	WikitextAstNode() {
	}
	
	WikitextAstNode(final WikitextAstNode parent, final int startOffset, final int stopOffset) {
		this.parent= parent;
		
		this.startOffset= startOffset;
		this.stopOffset= stopOffset;
	}
	
	
	public abstract NodeType getNodeType();
	
	@Override
	public final int getStatusCode() {
		return this.status;
	}
	
	@Override
	public final IAstNode getParent() {
		return this.parent;
	}
	
	@Override
	public final WikitextAstNode getRoot() {
		WikitextAstNode candidate= this;
		WikitextAstNode p;
		while ((p= candidate.parent) != null) {
			candidate= p;
		}
		return candidate;
	}
	
	@Override
	public final int getOffset() {
		return this.startOffset;
	}
	
	@Override
	public final int getStopOffset() {
		return this.stopOffset;
	}
	
	@Override
	public final int getLength() {
		return this.stopOffset - this.startOffset;
	}
	
	public String getLabel() {
		return null;
	}
	
	public String getText() {
		return null;
	}
	
	
	@Override
	public abstract WikitextAstNode getChild(final int index);
	
	@Override
	public final void accept(final ICommonAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	public abstract void acceptInWikitext(WikitextAstVisitor visitor) throws InvocationTargetException;
	
	public abstract void acceptInWikitextChildren(WikitextAstVisitor visitor) throws InvocationTargetException;
	
	
	@Override
	public synchronized void addAttachment(final Object data) {
		this.attachments= ImCollections.addElement(this.attachments, data);
	}
	
	@Override
	public synchronized void removeAttachment(final Object data) {
		this.attachments= ImCollections.removeElement(this.attachments, data);
	}
	
	@Override
	public ImList<Object> getAttachments() {
		return this.attachments;
	}
	
}
