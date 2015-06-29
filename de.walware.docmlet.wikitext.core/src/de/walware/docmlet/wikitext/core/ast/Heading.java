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


public final class Heading extends ContainerNode {
	
	
	private final int level;
	
	private final String label;
	
	
	public Heading(final WikitextAstNode parent, final int offset, final int level,
			final String label) {
		super(parent, offset, offset);
		
		this.level= level;
		this.label= label;
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.HEADING;
	}
	
	public int getLevel() {
		return this.level;
	}
	
	@Override
	public String getLabel() {
		return this.label;
	}
	
	
	@Override
	public void acceptInWikitext(final WikitextAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
}
