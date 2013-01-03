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
		public TexCommand getCommand() {
			return null;
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
		
		
		TexAstNode[] fArguments = NO_CHILDREN;
		
		TexCommand fCommand;
		
		
		Word(final String word) {
			super(word);
		}
		
		
		@Override
		public TexCommand getCommand() {
			return fCommand;
		}
		
		@Override
		public boolean hasChildren() {
			return (fArguments.length > 0);
		}
		
		@Override
		public int getChildCount() {
			return fArguments.length;
		}
		
		@Override
		public TexAstNode getChild(final int index) {
			return fArguments[index];
		}
		
		@Override
		public int getChildIndex(final IAstNode element) {
			for (int i = 0; i < fArguments.length; i++) {
				if (fArguments[i] == element) {
					return i;
				}
			}
			return -1;
		}
		
		@Override
		public void acceptInChildren(final ICommonAstVisitor visitor) throws InvocationTargetException {
			for (final TexAstNode child : fArguments) {
				visitor.visit(child);
			}
		}
		
		@Override
		public void acceptInTexChildren(final TexAstVisitor visitor) throws InvocationTargetException {
			for (final TexAstNode child : fArguments) {
				child.acceptInTex(visitor);
			}
		}
		
	}
	
	
	private final String fWord;
	
	
	private ControlNode(final String word) {
		fWord = word;
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.CONTROL;
	}
	
	
	@Override
	public final String getText() {
		return fWord;
	}
	
	public abstract TexCommand getCommand();
	
	@Override
	public final void acceptInTex(final TexAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	
}
