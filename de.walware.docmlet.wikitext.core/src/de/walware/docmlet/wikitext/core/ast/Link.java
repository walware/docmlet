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

package de.walware.docmlet.wikitext.core.ast;

import java.lang.reflect.InvocationTargetException;

import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.ltk.ast.ICommonAstVisitor;

import de.walware.docmlet.wikitext.core.ast.WikitextAst.NodeType;


public abstract class Link extends ContainerNode {
	
	
	public static final byte COMMON= 0;
	public static final byte LINK_REF_DEFINITION= 1;
	public static final byte LINK_BY_REF= 2;
	
	
	static final class Ref extends Link {
		
		
		private final Label referenceLabel;
		
		
		Ref(final WikitextAstNode parent, final int beginOffset, final int endOffset,
				final byte linkType, final Label referenceLabel) {
			super(parent, beginOffset, endOffset, linkType, null);
			
			if (referenceLabel == null) {
				throw new NullPointerException("referenceLabel");
			}
			referenceLabel.parent= this;
			this.referenceLabel= referenceLabel;
		}
		
		
		@Override
		public boolean hasChildren() {
			return true;
		}
		
		@Override
		public int getChildCount() {
			return this.children.length + 1;
		}
		
		@Override
		public Label getReferenceLabel() {
			return this.referenceLabel;
		}
		
		@Override
		public WikitextAstNode getChild(final int index) {
			if (index == 0) {
				return this.referenceLabel;
			}
			return this.children[index - 1];
		}
		
		@Override
		public int getChildIndex(final IAstNode element) {
			if (this.referenceLabel == element) {
				return 0;
			}
			for (int i= 0; i < this.children.length; i++) {
				if (this.children[i] == element) {
					return i + 1;
				}
			}
			return -1;
		}
		
		@Override
		public void acceptInChildren(final ICommonAstVisitor visitor) throws InvocationTargetException {
			this.referenceLabel.accept(visitor);
			for (final WikitextAstNode child : this.children) {
				child.accept(visitor);
			}
		}
		
		@Override
		public void acceptInWikitextChildren(final WikitextAstVisitor visitor) throws InvocationTargetException {
			this.referenceLabel.acceptInWikitext(visitor);
			for (final WikitextAstNode child : this.children) {
				child.acceptInWikitext(visitor);
			}
		}
		
	}
	
	static final class Common extends Link {
		
		
		Common(final WikitextAstNode parent, final int beginOffset, final int endOffset,
				final byte linkType, final String href) {
			super(parent, beginOffset, endOffset, linkType, href);
		}
		
		
		@Override
		public Label getReferenceLabel() {
			return null;
		}
		
	}
	
	
	private final byte linkType;
	
	private final String uri;
	
	
	private Link(final WikitextAstNode parent, final int beginOffset, final int endOffset,
			final byte linkType, final String uri) {
		super(parent, beginOffset, endOffset);
		
		this.linkType= linkType;
		this.uri= uri;
	}
	
	private Link(final WikitextAstNode parent, final int beginOffset,
			final String uri) {
		super(parent, beginOffset, beginOffset);
		
		this.linkType= 0;
		this.uri= uri;
	}
	
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.LINK;
	}
	
	public byte getLinkType() {
		return this.linkType;
	}
	
	public String getUri() {
		return this.uri;
	}
	
	public abstract Label getReferenceLabel();
	
	
	@Override
	public void acceptInWikitext(final WikitextAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	
}
