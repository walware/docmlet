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

import de.walware.ecommons.ltk.core.ElementSet;
import de.walware.ecommons.ltk.core.refactoring.CommonDeleteProcessor;
import de.walware.ecommons.ltk.core.refactoring.RefactoringAdapter;


public class DeleteWikidocProcessor extends CommonDeleteProcessor {
	
	
	public DeleteWikidocProcessor(final ElementSet elements, final RefactoringAdapter adapter) {
		super(elements, adapter);
	}
	
	
	@Override
	public String getIdentifier() {
		return WikitextRefactoring.DELETE_WIKIDOC_ELEMENTS_PROCESSOR_ID;
	}
	
	@Override
	protected String getRefactoringIdentifier() {
		return WikitextRefactoring.DELETE_WIKIDOC_ELEMENTS_REFACTORING_ID;
	}
	
}
