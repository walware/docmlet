/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.refactoring;

import de.walware.ecommons.ltk.core.refactoring.CommonPasteCodeProcessor;
import de.walware.ecommons.ltk.core.refactoring.RefactoringAdapter;
import de.walware.ecommons.ltk.core.refactoring.RefactoringDestination;


public class PasteLtxCodeProcessor extends CommonPasteCodeProcessor {
	
	
	public PasteLtxCodeProcessor(final String code,
			final RefactoringDestination destination, final RefactoringAdapter adapter) {
		super(code, destination, adapter);
	}
	
	
	@Override
	public String getIdentifier() {
		return TexRefactoring.PASTE_LTX_CODE_PROCESSOR_ID;
	}
	
	@Override
	protected String getRefactoringIdentifier() {
		return TexRefactoring.PASTE_LTX_CODE_REFACTORING_ID;
	}
	
}
