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

import static de.walware.docmlet.tex.core.ast.ITexAstStatusConstants.STATUS2_GROUP_NOT_CLOSED;

import java.lang.reflect.InvocationTargetException;

import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.ltk.ast.ICommonAstVisitor;

import de.walware.docmlet.tex.core.ast.TexAst.NodeType;


/**
 * {...}
 */
public class Group extends ContainerNode {
	
	
	static final class Bracket extends Group {
		
		
		Bracket(final TexAstNode group, final int offset, final int stopOffset) {
			super(group, offset, stopOffset);
		}
		
		
		@Override
		public String getText() {
			return "{"; //$NON-NLS-1$
		}
		
	}
	
	static final class Square extends Group {
		
		
		Square(final TexAstNode group, final int offset, final int stopOffset) {
			super(group, offset, stopOffset);
		}
		
		
		@Override
		public String getText() {
			return "["; //$NON-NLS-1$
		}
		
	}
	
	
	private Group(final TexAstNode group, final int offset, final int stopOffset) {
		super(group, offset, stopOffset);
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.GROUP;
	}
	
	
	@Override
	public final boolean hasChildren() {
		return (this.children.length > 0);
	}
	
	@Override
	public final int getChildCount() {
		return this.children.length;
	}
	
	@Override
	public final TexAstNode getChild(final int index) {
		return this.children[index];
	}
	
	@Override
	public final int getChildIndex(final IAstNode element) {
		for (int i= 0; i < this.children.length; i++) {
			if (this.children[i] == element) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public final void acceptInChildren(final ICommonAstVisitor visitor) throws InvocationTargetException {
		for (final TexAstNode child : this.children) {
			visitor.visit(child);
		}
	}
	
	@Override
	public void acceptInTex(final TexAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	@Override
	public final void acceptInTexChildren(final TexAstVisitor visitor) throws InvocationTargetException {
		for (final TexAstNode child : this.children) {
			child.acceptInTex(visitor);
		}
	}
	
	
	@Override
	void setEndNode(final int stopOffset, final TexAstNode endNode) {
		this.stopOffset= stopOffset;
	}
	
	@Override
	void setMissingEnd() {
		this.status= STATUS2_GROUP_NOT_CLOSED;
		if (this.children.length > 0) {
			this.stopOffset= this.children[this.children.length-1].stopOffset;
		}
	}
	
}
