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

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.collections.ImList;
import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.ltk.ast.ICommonAstVisitor;

import de.walware.docmlet.tex.core.ast.TexAst.NodeType;


public abstract class TexAstNode implements IAstNode {
	
	
	protected static final TexAstNode[] NO_CHILDREN= new TexAstNode[0];
	
	private static final ImList<Object> NO_ATTACHMENT= ImCollections.emptyList();
	
	
	int status;
	
	TexAstNode texParent;
	
	int startOffset;
	int stopOffset;
	
	private volatile ImList<Object> attachments= NO_ATTACHMENT;
	
	
	TexAstNode() {
	}
	
	TexAstNode(final TexAstNode parent, final int startOffset, final int stopOffset) {
		this.texParent= parent;
		
		this.startOffset= startOffset;
		this.stopOffset= stopOffset;
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
	public final IAstNode getRoot() {
		IAstNode candidate= this;
		IAstNode p;
		while ((p= candidate.getParent()) != null) {
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
	
	public String getText() {
		return null;
	}
	
	
	@Override
	public abstract TexAstNode getChild(final int index);
	
	@Override
	public final void accept(final ICommonAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	public abstract void acceptInTex(TexAstVisitor visitor) throws InvocationTargetException;
	
	public abstract void acceptInTexChildren(TexAstVisitor visitor) throws InvocationTargetException;
	
	
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
