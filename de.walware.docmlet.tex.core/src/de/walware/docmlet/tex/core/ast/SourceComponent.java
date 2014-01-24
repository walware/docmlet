/*=============================================================================#
 # Copyright (c) 2009-2014 Stephan Wahlbrink (WalWare.de) and others.
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
	
	
	public SourceComponent() {
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.SOURCELINES;
	}
	
	@Override
	public final boolean hasChildren() {
		return (fChildren.length == 0);
	}
	
	@Override
	public final int getChildCount() {
		return fChildren.length;
	}
	
	@Override
	public final TexAstNode getChild(final int index) {
		return fChildren[index];
	}
	
	@Override
	public final int getChildIndex(final IAstNode element) {
		for (int i = 0; i < fChildren.length; i++) {
			if (fChildren[i] == element) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public final void acceptInChildren(final ICommonAstVisitor visitor) throws InvocationTargetException {
		for (final TexAstNode child : fChildren) {
			visitor.visit(child);
		}
	}
	
	@Override
	public void acceptInTex(final TexAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	@Override
	public final void acceptInTexChildren(final TexAstVisitor visitor) throws InvocationTargetException {
		for (final TexAstNode child : fChildren) {
			child.acceptInTex(visitor);
		}
	}
	
	
	@Override
	void setEndNode(final int stopOffset, final TexAstNode endNode) {
		fStopOffset = stopOffset;
	}
	
	@Override
	void setMissingEnd() {
		if (fChildren.length > 0) {
			fStopOffset = fChildren[fChildren.length-1].fStopOffset;
		}
	}
	
}
