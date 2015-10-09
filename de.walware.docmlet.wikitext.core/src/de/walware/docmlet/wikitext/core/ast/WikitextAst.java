/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.core.ast;

import de.walware.ecommons.ltk.ast.Ast;


public class WikitextAst extends Ast {
	
	
	/**
	 * Definitions of Wikitext AST node types
	 */
	public enum NodeType {
		
		
		SOURCELINES,
		COMMENT,
		ERROR,
		EMBEDDED,
		
		HEADING,
		BLOCK,
		SPAN,
		VERBATIM,
		
		LABEL,
		TEXT,
		LINK,
		
	}
	
	
	/**
	 * Returns the index in the array for the node at the specified offset
	 * 
	 * @param nodes array with nodes (items can be <code>null</code>)
	 * @param offset the offset of the searched node
	 * @return the index in the array if found, otherwise -1
	 */
	public static int getIndexAt(final WikitextAstNode[] nodes, final int offset) {
		for (int i= 0; i < nodes.length; i++) {
			if (nodes[i] != null) {
				if (offset < nodes[i].getOffset()) {
					return -1;
				}
				if (offset <= nodes[i].getEndOffset()) {
					return i;
				}
			}
		}
		return -1;
	}
	
}
