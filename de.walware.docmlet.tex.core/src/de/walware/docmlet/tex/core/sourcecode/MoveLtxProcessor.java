/*=============================================================================#
 # Copyright (c) 2012-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.sourcecode;

import de.walware.ecommons.ltk.core.ElementSet;
import de.walware.ecommons.ltk.core.refactoring.CommonMoveProcessor;
import de.walware.ecommons.ltk.core.refactoring.RefactoringAdapter;
import de.walware.ecommons.ltk.core.refactoring.RefactoringDestination;


public class MoveLtxProcessor extends CommonMoveProcessor {
	
	
	public MoveLtxProcessor(final ElementSet elementsToMove,
			final RefactoringDestination destination, final RefactoringAdapter adapter) {
		super(elementsToMove, destination, adapter);
	}
	
	
	@Override
	public String getIdentifier() {
		return TexRefactoring.MOVE_LTX_ELEMENTS_PROCESSOR_ID;
	}
	
	@Override
	protected String getRefactoringIdentifier() {
		return TexRefactoring.MOVE_LTX_ELEMENTS_REFACTORING_ID;
	}
	
}
