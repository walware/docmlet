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


/**
 * \begin{name} ... \end{name}
 */
public abstract class Environment extends ContainerNode {
	
	
	static final class MathLatexShorthand extends Environment{
		
		
		MathLatexShorthand(final TexAstNode parent, final ControlNode beginNode) {
			super(parent, beginNode);
		}
		
		
		@Override
		public String getText() {
			return this.beginNode.getText();
		}
		
		@Override
		void setMissingEnd() {
			this.status= ITexAstStatusConstants.STATUS2_MATH_NOT_CLOSED;
			this.endNode= new Dummy();
			this.endNode.texParent= this;
			this.endNode.startOffset= this.endNode.stopOffset= this.stopOffset= getChild(this.children.length).stopOffset;
		}
		
	}
	
	static final class Word extends Environment {
		
		Word(final TexAstNode parent, final ControlNode beginNode) {
			super(parent, beginNode);
		}
		
		
		@Override
		public String getText() {
			if (this.beginNode.hasChildren()) {
				return this.beginNode.getChild(0).getChild(0).getText();
			}
			return null;
		}
		
		@Override
		void setMissingEnd() {
			this.status= ITexAstStatusConstants.STATUS2_ENV_NOT_CLOSED;
			this.endNode= new Dummy();
			this.endNode.texParent= this;
			this.endNode.startOffset= this.endNode.stopOffset= this.stopOffset= getChild(this.children.length).stopOffset;
		}
		
	}
	
	
	final ControlNode beginNode;
	TexAstNode endNode;
	
	
	Environment(final TexAstNode parent, final ControlNode beginNode) {
		this.texParent= parent;
		this.beginNode= beginNode;
		this.startOffset= beginNode.startOffset;
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.ENVIRONMENT;
	}
	
	
	public ControlNode getBeginNode() {
		return this.beginNode;
	}
	
	public TexAstNode getEndNode() {
		return this.endNode;
	}
	
	
	
	@Override
	public boolean hasChildren() {
		return true;
	}
	
	@Override
	public int getChildCount() {
		return this.children.length + 2;
	}
	
	@Override
	public TexAstNode getChild(final int index) {
		if (index == 0) {
			return this.beginNode;
		}
		if (index <= this.children.length) {
			return this.children[index-1];
		}
		if (index == this.children.length+1) {
			return this.endNode;
		}
		throw new IndexOutOfBoundsException();
	}
	
	@Override
	public int getChildIndex(final IAstNode element) {
		if (this.beginNode == element) {
			return 0;
		}
		for (int i= 0; i < this.children.length; i++) {
			if (this.children[i] == element) {
				return i+1;
			}
		}
		if (this.endNode == element) {
			return this.children.length+1;
		}
		return -1;
	}
	
	@Override
	public void acceptInChildren(final ICommonAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this.beginNode);
		for (final TexAstNode child : this.children) {
			visitor.visit(child);
		}
		visitor.visit(this.endNode);
	}
	
	@Override
	public void acceptInTex(final TexAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	@Override
	public void acceptInTexChildren(final TexAstVisitor visitor) throws InvocationTargetException {
		this.beginNode.acceptInTex(visitor);
		for (final TexAstNode child : this.children) {
			child.acceptInTex(visitor);
		}
		this.endNode.acceptInTex(visitor);
	}
	
	
	@Override
	void setEndNode(final int stopOffset, final TexAstNode endNode) {
		this.endNode= endNode;
		this.stopOffset= endNode.stopOffset;
	}
	
}
