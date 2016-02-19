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


abstract class ContainerNode extends TexAstNode {
	
	
	TexAstNode[] children= NO_CHILDREN;
	
	
	ContainerNode() {
	}
	
	ContainerNode(final TexAstNode parent, final int beginOffset, final int endOffset) {
		super(parent, beginOffset, endOffset);
	}
	
	
	abstract void setEndNode(int endOffset, final TexAstNode endNode);
	
	abstract void setMissingEnd();
	
}
