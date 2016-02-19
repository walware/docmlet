/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.core.source;

import de.walware.ecommons.text.IndentUtil;

import de.walware.docmlet.wikitext.core.ast.WikitextAstNode;


/**
 * Adapter for source (AST) related tasks for a markup language
 */
public interface IMarkupSourceFormatAdapter {
	
	
	String getPrefixCont(WikitextAstNode node,
			IndentUtil indentUtil) throws Exception;
	
}
