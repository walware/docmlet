/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
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
import de.walware.docmlet.tex.core.commands.TexCommand;


/**
 * \...
 */
public abstract class ControlNode extends TexAstNode {
	
	
	static final class Char extends ControlNode {
		
		
		Char(final String word) {
			super(word);
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
		public TexAstNode getChild(final int index) {
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
		public void acceptInTexChildren(final TexAstVisitor visitor) throws InvocationTargetException {
		}
		
	}
	
	static final class Word extends ControlNode {
		
		
		TexAstNode[] arguments= NO_CHILDREN;
		
		
		Word(final String word) {
			super(word);
		}
		
		
		@Override
		public boolean hasChildren() {
			return (this.arguments.length > 0);
		}
		
		@Override
		public int getChildCount() {
			return this.arguments.length;
		}
		
		@Override
		public TexAstNode getChild(final int index) {
			return this.arguments[index];
		}
		
		@Override
		public int getChildIndex(final IAstNode element) {
			for (int i= 0; i < this.arguments.length; i++) {
				if (this.arguments[i] == element) {
					return i;
				}
			}
			return -1;
		}
		
		@Override
		public void acceptInChildren(final ICommonAstVisitor visitor) throws InvocationTargetException {
			for (final TexAstNode child : this.arguments) {
				visitor.visit(child);
			}
		}
		
		@Override
		public void acceptInTexChildren(final TexAstVisitor visitor) throws InvocationTargetException {
			for (final TexAstNode child : this.arguments) {
				child.acceptInTex(visitor);
			}
		}
		
	}
	
	
	private final String fWord;
	
	TexCommand fCommand;
	
	
	private ControlNode(final String word) {
		this.fWord= word;
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.CONTROL;
	}
	
	
	@Override
	public final String getText() {
		return this.fWord;
	}
	
	public final TexCommand getCommand() {
		return this.fCommand;
	}
	
	
	@Override
	public final void acceptInTex(final TexAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	
}
