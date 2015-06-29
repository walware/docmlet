/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.ui.util;

import org.eclipse.ui.dialogs.SearchPattern;


public class WikitextNameSearchPattern extends SearchPattern {
	
	
	public WikitextNameSearchPattern() {
		super(SearchPattern.RULE_EXACT_MATCH
				| SearchPattern.RULE_PREFIX_MATCH | SearchPattern.RULE_CAMELCASE_MATCH
				| SearchPattern.RULE_PATTERN_MATCH | SearchPattern.RULE_BLANK_MATCH);
	}
	
	
}
