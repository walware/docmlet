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

import de.walware.ecommons.ltk.core.refactoring.CommonRefactoringFactory;


public class WikitextRefactoring {
	
	
	private static final CommonRefactoringFactory WIKIDOC_FACTORY= new WikidocRefactoringFactory();
	
	public static CommonRefactoringFactory getWikidocFactory() {
		return WIKIDOC_FACTORY;
	}
	
	
	public static final String DELETE_WIKIDOC_ELEMENTS_REFACTORING_ID= "de.walware.docmlet.wikitext.refactoring.DeleteWikidocElementsOperation"; //$NON-NLS-1$
	
	public static final String MOVE_WIKIDOC_ELEMENTS_REFACTORING_ID= "de.walware.docmlet.wikitext.refactoring.MoveWikidocElementsOperation"; //$NON-NLS-1$
	
	public static final String COPY_WIKIDOC_ELEMENTS_REFACTORING_ID= "de.walware.docmlet.wikitext.refactoring.CopyWikidocElementsOperation"; //$NON-NLS-1$
	
	public static final String PASTE_WIKIDOC_CODE_REFACTORING_ID= "de.walware.docmlet.wikitext.refactoring.PasteWikidocCodeOperation"; //$NON-NLS-1$
	
	
	public static final String DELETE_WIKIDOC_ELEMENTS_PROCESSOR_ID= "de.walware.docmlet.wikitext.refactoring.DeleteWikidocElementsProcessor"; //$NON-NLS-1$
	
	public static final String MOVE_WIKIDOC_ELEMENTS_PROCESSOR_ID= "de.walware.docmlet.wikitext.refactoring.MoveWikidocElementsProcessor"; //$NON-NLS-1$
	
	public static final String COPY_WIKIDOC_ELEMENTS_PROCESSOR_ID= "de.walware.docmlet.wikitext.refactoring.CopyWikidocElementsProcessor"; //$NON-NLS-1$
	
	public static final String PASTE_WIKIDOC_CODE_PROCESSOR_ID= "de.walware.docmlet.wikitext.refactoring.PasteWikidocCodeProcessor"; //$NON-NLS-1$
	
}
