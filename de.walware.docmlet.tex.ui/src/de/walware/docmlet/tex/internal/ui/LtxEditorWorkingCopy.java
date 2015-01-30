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

package de.walware.docmlet.tex.internal.ui;

import de.walware.ecommons.ltk.IWorkspaceSourceUnit;
import de.walware.ecommons.ltk.ui.GenericEditorWorkspaceSourceUnitWorkingCopy2;

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.core.model.ILtxSourceUnit;
import de.walware.docmlet.tex.core.model.ILtxWorkspaceSourceUnit;
import de.walware.docmlet.tex.core.model.LtxSuModelContainer;


public final class LtxEditorWorkingCopy
		extends GenericEditorWorkspaceSourceUnitWorkingCopy2<LtxSuModelContainer<ILtxSourceUnit>>
		implements ILtxWorkspaceSourceUnit {
	
	
	public LtxEditorWorkingCopy(final IWorkspaceSourceUnit from) {
		super(from);
	}
	
	@Override
	protected LtxSuModelContainer<ILtxSourceUnit> createModelContainer() {
		return new LtxSuModelContainer<ILtxSourceUnit>(this);
	}
	
	
	@Override
	public ITexCoreAccess getTexCoreAccess() {
		return ((ILtxSourceUnit) getUnderlyingUnit()).getTexCoreAccess();
	}
	
}
