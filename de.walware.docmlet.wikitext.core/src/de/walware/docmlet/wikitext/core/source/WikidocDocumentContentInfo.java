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

package de.walware.docmlet.wikitext.core.source;

import de.walware.jcommons.collections.ImCollections;

import de.walware.ecommons.text.core.sections.AbstractDocContentSections;


public class WikidocDocumentContentInfo extends AbstractDocContentSections {
	
	
	public static final String WIKIDOC=                     IWikitextDocumentConstants.WIKIDOC_PARTITIONING;
	
	
	public static final WikidocDocumentContentInfo INSTANCE= new WikidocDocumentContentInfo();
	
	
	public WikidocDocumentContentInfo() {
		super(IWikitextDocumentConstants.WIKIDOC_PARTITIONING, WIKIDOC,
				ImCollections.newList(WIKIDOC) );
	}
	
	
	@Override
	public String getTypeByPartition(final String contentType) {
		return WIKIDOC;
	}
	
}
