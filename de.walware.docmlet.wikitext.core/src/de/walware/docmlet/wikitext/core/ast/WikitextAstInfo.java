/*=============================================================================#
 # Copyright (c) 2014-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.core.ast;

import java.util.Set;

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.ast.IAstNode;

import de.walware.docmlet.wikitext.core.MarkupSourceModelStamp;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;


public class WikitextAstInfo extends AstInfo {
	
	
	private final IMarkupLanguage markupLanguage;
	
	private final Set<String> embeddedTypes;
	
	
	public WikitextAstInfo(final int level, final MarkupSourceModelStamp stamp, final IAstNode root,
			final IMarkupLanguage markupLanguage, final Set<String> embeddedTypes) {
		super(level, stamp, root);
		
		this.markupLanguage= markupLanguage;
		this.embeddedTypes= embeddedTypes;
	}
	
	
	public IMarkupLanguage getMarkupLanguage() {
		return this.markupLanguage;
	}
	
	public Set<String> getEmbeddedTypes() {
		return this.embeddedTypes;
	}
	
	
}
