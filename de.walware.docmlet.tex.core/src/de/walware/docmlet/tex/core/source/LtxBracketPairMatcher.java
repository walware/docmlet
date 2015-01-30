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

package de.walware.docmlet.tex.core.source;

import de.walware.ecommons.text.ITokenScanner;
import de.walware.ecommons.text.PairMatcher;


/**
 * A pair finder class for implementing the pair matching.
 */
public class LtxBracketPairMatcher extends PairMatcher {
	
	
	public static final char[][] BRACKETS= { {'{', '}'}, {'(', ')'}, {'[', ']'} };
	
	
	public LtxBracketPairMatcher() {
		this(new LtxHeuristicTokenScanner());
	}
	
	public LtxBracketPairMatcher(final LtxHeuristicTokenScanner scanner) {
		this(scanner, scanner.getPartitioningConfig().getPartitioning());
	}
	
	public LtxBracketPairMatcher(final ITokenScanner scanner,
			final String partitioning) {
		super(BRACKETS, partitioning, new String[] {
				ITexDocumentConstants.LTX_DEFAULT_CONTENT_TYPE,
				ITexDocumentConstants.LTX_DEFAULT_EXPL_CONTENT_TYPE,
				ITexDocumentConstants.LTX_MATH_CONTENT_TYPE,
				ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE,
		}, scanner, '\\');
	}
	
}
