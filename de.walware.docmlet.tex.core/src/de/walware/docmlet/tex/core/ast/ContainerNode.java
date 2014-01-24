/*=============================================================================#
 # Copyright (c) 2009-2014 Stephan Wahlbrink (WalWare.de) and others.
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
	
	
	TexAstNode[] fChildren = NO_CHILDREN;
	
	
	ContainerNode() {
	}
	
	ContainerNode(final TexAstNode parent, final int startOffset, final int stopOffset) {
		super(parent, startOffset, stopOffset);
	}
	
	
	abstract void setEndNode(int stopOffset, final TexAstNode endNode);
	
	abstract void setMissingEnd();
	
}
