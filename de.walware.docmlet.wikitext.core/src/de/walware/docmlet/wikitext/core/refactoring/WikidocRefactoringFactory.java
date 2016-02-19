/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.core.refactoring;

import org.eclipse.ltk.core.refactoring.participants.CopyProcessor;
import org.eclipse.ltk.core.refactoring.participants.DeleteProcessor;
import org.eclipse.ltk.core.refactoring.participants.MoveProcessor;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;

import de.walware.ecommons.ltk.core.refactoring.CommonRefactoringFactory;
import de.walware.ecommons.ltk.core.refactoring.RefactoringAdapter;
import de.walware.ecommons.ltk.core.refactoring.RefactoringDestination;


public class WikidocRefactoringFactory extends CommonRefactoringFactory {
	
	
	protected WikidocRefactoringFactory() {
	}
	
	
	@Override
	public WikidocRefactoringAdapter createAdapter(final Object elements) {
		return new WikidocRefactoringAdapter();
	}
	
	@Override
	public DeleteProcessor createDeleteProcessor(final Object elementsToDelete, final RefactoringAdapter adapter) {
		return new DeleteWikidocProcessor(createElementSet(elementsToDelete), adapter);
	}
	
	@Override
	public MoveProcessor createMoveProcessor(final Object elementsToMove,
			final RefactoringDestination destination, final RefactoringAdapter adapter) {
		return new MoveWikidocProcessor(createElementSet(elementsToMove), destination, adapter);
	}
	
	@Override
	public CopyProcessor createCopyProcessor(final Object elementsToCopy,
			final RefactoringDestination destination, final RefactoringAdapter adapter) {
		return new CopyWikidocProcessor(createElementSet(elementsToCopy), destination, adapter);
	}
	
	@Override
	public RefactoringProcessor createPasteProcessor(final Object elementsToPaste,
			final RefactoringDestination destination, final RefactoringAdapter adapter) {
		if (elementsToPaste instanceof String) {
			return new PasteWikidocCodeProcessor((String) elementsToPaste, destination, (WikidocRefactoringAdapter) adapter);
		}
		return super.createPasteProcessor(elementsToPaste, destination, adapter);
	}
	
}
