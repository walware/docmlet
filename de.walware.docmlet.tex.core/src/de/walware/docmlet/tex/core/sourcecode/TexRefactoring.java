/*******************************************************************************
 * Copyright (c) 2009-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.core.sourcecode;

import de.walware.ecommons.ltk.core.refactoring.CommonRefactoringFactory;


public class TexRefactoring {
	
	
	private static final CommonRefactoringFactory LTX_FACTORY = new LtxRefactoringFactory();
	
	public static CommonRefactoringFactory getLtxFactory() {
		return LTX_FACTORY;
	}
	
	
	public static final String DELETE_LTX_ELEMENTS_REFACTORING_ID = "de.walware.docmlet.tex.refactoring.DeleteLtxElementsOperation"; //$NON-NLS-1$
	
	public static final String MOVE_LTX_ELEMENTS_REFACTORING_ID = "de.walware.docmlet.tex.refactoring.MoveLtxElementsOperation"; //$NON-NLS-1$
	
	public static final String COPY_LTX_ELEMENTS_REFACTORING_ID = "de.walware.docmlet.tex.refactoring.CopyLtxElementsOperation"; //$NON-NLS-1$
	
	public static final String PASTE_LTX_CODE_REFACTORING_ID = "de.walware.docmlet.tex.refactoring.PasteLtxCodeOperation"; //$NON-NLS-1$
	
	
	public static final String DELETE_LTX_ELEMENTS_PROCESSOR_ID = "de.walware.docmlet.tex.refactoring.DeleteLtxElementsProcessor"; //$NON-NLS-1$
	
	public static final String MOVE_LTX_ELEMENTS_PROCESSOR_ID = "de.walware.docmlet.tex.refactoring.MoveLtxElementsProcessor"; //$NON-NLS-1$
	
	public static final String COPY_LTX_ELEMENTS_PROCESSOR_ID = "de.walware.docmlet.tex.refactoring.CopyLtxElementsProcessor"; //$NON-NLS-1$
	
	public static final String PASTE_LTX_CODE_PROCESSOR_ID = "de.walware.docmlet.tex.refactoring.PasteLtxCodeProcessor"; //$NON-NLS-1$
	
}
