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

package de.walware.docmlet.wikitext.core.source;

import de.walware.ecommons.text.BasicHeuristicTokenScanner;
import de.walware.ecommons.text.core.sections.IDocContentSections;


public class WikitextHeuristicTokenScanner extends BasicHeuristicTokenScanner {
	
	
	public static WikitextHeuristicTokenScanner create(final IDocContentSections documentContentInfo) {
		return new WikitextHeuristicTokenScanner(documentContentInfo);
	}
	
	
	protected WikitextHeuristicTokenScanner(final IDocContentSections documentContentInfo) {
		super(documentContentInfo, IWikitextDocumentConstants.WIKIDOC_DEFAULT_CONTENT_CONSTRAINT);
	}
	
	
}
