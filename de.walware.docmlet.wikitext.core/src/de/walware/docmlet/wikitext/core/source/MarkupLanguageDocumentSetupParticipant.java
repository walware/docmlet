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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;

import de.walware.ecommons.text.PartitionerDocumentSetupParticipant;

import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;


public abstract class MarkupLanguageDocumentSetupParticipant extends PartitionerDocumentSetupParticipant {
	
	
	public static IMarkupLanguage getMarkupLanguage(final IDocument document, final String partitioning) {
		final IMarkupLanguagePartitioner partitioner= (IMarkupLanguagePartitioner)
				((IDocumentExtension3) document).getDocumentPartitioner(partitioning);
		return partitioner.getMarkupLanguage();
	}
	
	
	private final IMarkupLanguage markupLanguage;
	
	
	public MarkupLanguageDocumentSetupParticipant(final IMarkupLanguage markupLanguage) {
		if (markupLanguage == null) {
			throw new NullPointerException("markupLanguage"); //$NON-NLS-1$
		}
		this.markupLanguage= markupLanguage;
	}
	
	
	public IMarkupLanguage getMarkupLanguage() {
		return this.markupLanguage;
	}
	
	
	@Override
	protected void doSetup(final IDocument document) {
		doSetup(document, getMarkupLanguage());
	}
	
	protected void doSetup(final IDocument document, final IMarkupLanguage markupLanguage) {
		final IDocumentExtension3 extension3= (IDocumentExtension3) document;
		IMarkupLanguagePartitioner partitioner= (IMarkupLanguagePartitioner) extension3.getDocumentPartitioner(getPartitioningId());
		if (partitioner == null) {
			// Setup the document scanner
			partitioner= createDocumentPartitioner(markupLanguage);
			partitioner.connect(document, true);
			extension3.setDocumentPartitioner(getPartitioningId(), partitioner);
		}
		else {
			partitioner.setMarkupLanguage(markupLanguage);
			extension3.setDocumentPartitioner(getPartitioningId(), partitioner);
		}
	}
	
	
	@Override
	protected IDocumentPartitioner createDocumentPartitioner() {
		throw new UnsupportedOperationException();
	}
	
	protected abstract IMarkupLanguagePartitioner createDocumentPartitioner(IMarkupLanguage markupLanguage);
	
}
