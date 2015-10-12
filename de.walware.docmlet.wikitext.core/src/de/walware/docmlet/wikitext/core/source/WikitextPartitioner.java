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

package de.walware.docmlet.wikitext.core.source;

import java.util.List;

import de.walware.ecommons.text.core.treepartitioner.TreePartitioner;

import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;


public class WikitextPartitioner extends TreePartitioner
		implements IMarkupLanguagePartitioner {
	
	
	public WikitextPartitioner(final String partitioningId,
			final WikitextPartitionNodeScanner scanner, final List<String> legalContentTypes) {
		super(partitioningId, scanner, legalContentTypes);
	}
	
	
	@Override
	public IMarkupLanguage getMarkupLanguage() {
		final WikitextPartitionNodeScanner scanner= (WikitextPartitionNodeScanner) this.scanner;
		return scanner.getMarkupLanguage();
	}
	
	@Override
	public void setMarkupLanguage(final IMarkupLanguage markupLanguage) {
		final WikitextPartitionNodeScanner scanner= (WikitextPartitionNodeScanner) this.scanner;
		if (!scanner.getMarkupLanguage().equals(markupLanguage)) {
			scanner.setMarkupLanguage(markupLanguage);
			clear();
		}
	}
	
	@Override
	public void reset() {
		super.clear();
	}
	
}
