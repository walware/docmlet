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

package de.walware.docmlet.wikitext.core.refactoring;

import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.ITypedRegion;

import de.walware.ecommons.ltk.core.ElementSet;
import de.walware.ecommons.ltk.core.model.IModelElement;
import de.walware.ecommons.ltk.core.model.ISourceElement;
import de.walware.ecommons.ltk.core.model.ISourceUnit;
import de.walware.ecommons.ltk.core.refactoring.RefactoringAdapter;
import de.walware.ecommons.ltk.core.refactoring.RefactoringDestination;
import de.walware.ecommons.ltk.core.refactoring.RefactoringDestination.Position;
import de.walware.ecommons.text.BasicHeuristicTokenScanner;

import de.walware.docmlet.wikitext.core.WikitextCore;
import de.walware.docmlet.wikitext.core.model.WikitextModel;
import de.walware.docmlet.wikitext.core.source.WikitextHeuristicTokenScanner;


public class WikidocRefactoringAdapter extends RefactoringAdapter {
	
	
	public WikidocRefactoringAdapter() {
		super(WikitextModel.WIKIDOC_TYPE_ID);
	}
	
	
	@Override
	public String getPluginIdentifier() {
		return WikitextCore.PLUGIN_ID;
	}
	
	@Override
	public WikitextHeuristicTokenScanner getScanner(final ISourceUnit su) {
		return WikitextHeuristicTokenScanner.create(su.getDocumentContentInfo());
	}
	
	@Override
	public boolean canInsert(final ElementSet elements, final ISourceElement to,
			final RefactoringDestination.Position pos) {
		if (super.canInsert(elements, to, pos)) {
			if ((to.getElementType() & IModelElement.MASK_C1) == IModelElement.C1_EMBEDDED
					&& pos == RefactoringDestination.Position.INTO ) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isCommentContent(final ITypedRegion partition) {
		return false;
	}
	
	@Override
	protected int getInsertionOffset(final AbstractDocument document,
			final ISourceElement element, final Position pos,
			final BasicHeuristicTokenScanner scanner)
			throws BadLocationException, BadPartitioningException {
		return super.getInsertionOffset(document, element, pos, scanner);
	}
	
}
