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

import de.walware.docmlet.wikitext.core.ast.WikitextAst.NodeType;


public final class SourceComponent extends ContainerNode {
	
	
	SourceComponent(final int length) {
		super(null, 0, length);
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.SOURCELINES;
	}
	
	
	@Override
	public void acceptInWikitext(final WikitextAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	
}
