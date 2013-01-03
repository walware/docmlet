/*******************************************************************************
 * Copyright (c) 2009-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
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


/**
 * \begin{name} ... \end{name}
 */
public final class Environment extends ContainerNode {
	
	
	ControlNode fBegin;
	TexAstNode fEnd;
	
	
	Environment() {
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.ENVIRONMENT;
	}
	
	
	public ControlNode getBeginNode() {
		return fBegin;
	}
	
	public TexAstNode getEndNode() {
		return fEnd;
	}
	
	@Override
	public String getText() {
		if (fBegin.hasChildren()) {
			return fBegin.getChild(0).getChild(0).getText();
		}
		return null;
	}
	
	
	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public int getChildCount() {
		return fChildren.length + 2;
	}
	
	@Override
	public TexAstNode getChild(final int index) {
		if (index == 0) {
			return fBegin;
		}
		if (index <= fChildren.length) {
			return fChildren[index-1];
		}
		if (index == fChildren.length+1) {
			return fEnd;
		}
		throw new IndexOutOfBoundsException();
	}
	
	@Override
	public int getChildIndex(final IAstNode element) {
		if (fBegin == element) {
			return 0;
		}
		for (int i = 0; i < fChildren.length; i++) {
			if (fChildren[i] == element) {
				return i+1;
			}
		}
		if (fEnd == element) {
			return fChildren.length+1;
		}
		return -1;
	}
	
	@Override
	public void acceptInChildren(final ICommonAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(fBegin);
		for (final TexAstNode child : fChildren) {
			visitor.visit(child);
		}
		visitor.visit(fEnd);
	}
	
	@Override
	public void acceptInTex(final TexAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	@Override
	public void acceptInTexChildren(final TexAstVisitor visitor) throws InvocationTargetException {
		fBegin.acceptInTex(visitor);
		for (final TexAstNode child : fChildren) {
			child.acceptInTex(visitor);
		}
		fEnd.acceptInTex(visitor);
	}
	
	
	@Override
	void setEndNode(final int stopOffset, final TexAstNode endNode) {
		fEnd = endNode;
		fStopOffset = endNode.fStopOffset;
	}
	
	@Override
	void setMissingEnd() {
		fStatus = ITexAstStatusConstants.STATUS2_ENV_NOT_CLOSED;
		fEnd = new Dummy();
		fEnd.fParent = this;
		fEnd.fStartOffset = fEnd.fStopOffset = fStopOffset = getChild(fChildren.length).fStopOffset;
	}
	
}
