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

import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;


/**
 * The document setup participant for Wikitext documents.
 */
public class WikidocDocumentSetupParticipant extends MarkupLanguageDocumentSetupParticipant1 {
	
	
	private static final String[] CONTENT_TYPES= IWikitextDocumentConstants.WIKIDOC_CONTENT_TYPES.toArray(
			new String[IWikitextDocumentConstants.WIKIDOC_CONTENT_TYPES.size()] );
	
	
	public WikidocDocumentSetupParticipant(final IMarkupLanguage markupLanguage) {
		this(markupLanguage, false);
	}
	
	public WikidocDocumentSetupParticipant(final IMarkupLanguage markupLanguage,
			final boolean templateMode) {
		super(markupLanguage, (templateMode) ? IMarkupLanguage.TEMPLATE_MODE : 0);
	}
	
	
	@Override
	public String getPartitioningId() {
		return IWikitextDocumentConstants.WIKIDOC_PARTITIONING;
	}
	
	@Override
	protected IMarkupLanguagePartitioner createDocumentPartitioner(final IMarkupLanguage markupLanguage) {
		return new WikitextPartitioner(
				new WikitextPartitionNodeScanner(markupLanguage, getMarkupLanguageMode()),
				CONTENT_TYPES );
	}
	
}
