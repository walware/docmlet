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

import de.walware.ecommons.text.ITokenScanner;
import de.walware.ecommons.text.PairMatcher;


/**
 * A pair finder class for implementing the pair matching.
 */
public class MarkupBracketPairMatcher extends PairMatcher {
	
	
	public static final char[][] BRACKETS= { {'{', '}'}, {'(', ')'}, {'[', ']'} };
	
	private static final String[] CONTENT_TYPES= new String[] {
		IWikitextDocumentConstants.WIKIDOC_DEFAULT_CONTENT_TYPE,
	};
	
	
	public MarkupBracketPairMatcher(final WikitextHeuristicTokenScanner scanner) {
		this(scanner, scanner.getDocumentPartitioning());
	}
	
	public MarkupBracketPairMatcher(final ITokenScanner scanner,
			final String partitioning) {
		super(BRACKETS, partitioning, CONTENT_TYPES, scanner, '\\');
	}
	
}
