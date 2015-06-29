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

import de.walware.docmlet.wikitext.core.ast.WikitextAst.NodeType;


public class Link extends ContainerNode {
	
	
	private final String href;
	
	
	Link(final WikitextAstNode parent, final int startOffset, final int stopOffset,
			final String href) {
		super(parent, startOffset, stopOffset);
		
		this.href= href;
	}
	
	Link(final WikitextAstNode parent, final int startOffset,
			final String href) {
		super(parent, startOffset, startOffset);
		
		this.href= href;
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.LINK;
	}
	
	
	public String getHref() {
		return this.href;
	}
	
	
	@Override
	public void acceptInWikitext(final WikitextAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
}
