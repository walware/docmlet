/*******************************************************************************
 * Copyright (c) 2008-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.internal.ui;

import org.eclipse.core.filesystem.IFileStore;

import de.walware.ecommons.ltk.ISourceUnit;
import de.walware.ecommons.ltk.IWorkspaceSourceUnit;
import de.walware.ecommons.ltk.core.impl.AbstractEditorSourceUnitFactory;


/**
 * Source unit factory for LaTeX editor context
 */
public final class LtxEditorWorkingCopyFactory extends AbstractEditorSourceUnitFactory {
	
	
	public LtxEditorWorkingCopyFactory() {
	}
	
	
	@Override
	protected ISourceUnit createSourceUnit(final String id, final IWorkspaceSourceUnit su) {
		return new LtxEditorWorkingCopy(su);
	}
	
	@Override
	protected ISourceUnit createSourceUnit(final String id, final IFileStore file) {
		return new LtxEditorUriSourceUnit(id, file);
	}
	
}
