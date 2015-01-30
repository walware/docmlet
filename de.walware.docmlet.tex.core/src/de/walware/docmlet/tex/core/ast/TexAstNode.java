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


public abstract class TexAstNode implements IAstNode {
	
	
	protected static final TexAstNode[] NO_CHILDREN = new TexAstNode[0];
	
	private static final Object[] NO_ATTACHMENT = new Object[0];
	
	
	int fStatus;
	
	TexAstNode fParent;
	
	int fStartOffset;
	int fStopOffset;
	
	private Object[] fAttachments = NO_ATTACHMENT;
	
	
	TexAstNode() {
	}
	
	TexAstNode(final TexAstNode parent, final int startOffset, final int stopOffset) {
		fParent = parent;
		
		fStartOffset = startOffset;
		fStopOffset = stopOffset;
	}
	
	
	public abstract NodeType getNodeType();
	
	@Override
	public final int getStatusCode() {
		return fStatus;
	}
	
	@Override
	public final TexAstNode getParent() {
		return fParent;
	}
	
	@Override
	public final TexAstNode getRoot() {
		TexAstNode candidate = this;
		TexAstNode p;
		while ((p = candidate.fParent) != null) {
			candidate = p;
		}
		return candidate;
	}
	
	@Override
	public final int getOffset() {
		return fStartOffset;
	}
	
	@Override
	public final int getStopOffset() {
		return fStopOffset;
	}
	
	@Override
	public final int getLength() {
		return fStopOffset - fStartOffset;
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
	
	
	public void addAttachment(final Object data) {
		if (fAttachments == NO_ATTACHMENT) {
			fAttachments = new Object[] { data };
		}
		else {
			final Object[] newArray = new Object[fAttachments.length+1];
			System.arraycopy(fAttachments, 0, newArray, 0, fAttachments.length);
			newArray[fAttachments.length] = data;
			fAttachments = newArray;
		}
	}
	
	public Object[] getAttachments() {
		return fAttachments;
	}
	
}
