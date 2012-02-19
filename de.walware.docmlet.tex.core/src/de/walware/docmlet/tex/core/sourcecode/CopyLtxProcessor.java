/*******************************************************************************
 * Copyright (c) 2012 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.core.sourcecode;

import de.walware.ecommons.ltk.core.refactoring.CommonCopyProcessor;
import de.walware.ecommons.ltk.core.refactoring.RefactoringAdapter;
import de.walware.ecommons.ltk.core.refactoring.RefactoringDestination;
import de.walware.ecommons.ltk.core.refactoring.RefactoringElementSet;


public class CopyLtxProcessor extends CommonCopyProcessor {
	
	
	public CopyLtxProcessor(final RefactoringElementSet elementsToCopy,
			final RefactoringDestination destination, final RefactoringAdapter adapter) {
		super(elementsToCopy, destination, adapter);
	}
	
	
	@Override
	public String getIdentifier() {
		return TexRefactoring.COPY_LTX_ELEMENTS_PROCESSOR_ID;
	}
	
	@Override
	protected String getRefactoringIdentifier() {
		return TexRefactoring.COPY_LTX_ELEMENTS_REFACTORING_ID;
	}
	
}
