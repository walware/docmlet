/*=============================================================================#
 # Copyright (c) 2009-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.ui;

import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.ISourceUnitModelInfo;
import de.walware.ecommons.ltk.IWorkspaceSourceUnit;
import de.walware.ecommons.ltk.ui.GenericEditorWorkspaceSourceUnitWorkingCopy;

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.core.model.ILtxSourceUnit;
import de.walware.docmlet.tex.core.model.LtxSuModelContainer;
import de.walware.docmlet.tex.core.model.TexModel;


public final class LtxEditorWorkingCopy extends GenericEditorWorkspaceSourceUnitWorkingCopy
		implements ILtxSourceUnit {
	
	
	private final LtxSuModelContainer<ILtxSourceUnit> fModel = new LtxSuModelContainer<ILtxSourceUnit>(this);
	
	
	public LtxEditorWorkingCopy(final IWorkspaceSourceUnit from) {
		super(from);
	}
	
	
	@Override
	public AstInfo getAstInfo(final String type, final boolean ensureSync, final IProgressMonitor monitor) {
		if (type == null || type == TexModel.LTX_TYPE_ID) {
			return fModel.getAstInfo(ensureSync, monitor);
		}
		return null;
	}
	
	@Override
	public ISourceUnitModelInfo getModelInfo(final String type, final int syncLevel, final IProgressMonitor monitor) {
		if (type == null || type == TexModel.LTX_TYPE_ID) {
			return fModel.getModelInfo(syncLevel, monitor);
		}
		return null;
	}
	
	
	@Override
	public ITexCoreAccess getTexCoreAccess() {
		return ((ILtxSourceUnit) fFrom).getTexCoreAccess();
	}
	
	@Override
	public Object getAdapter(final Class required) {
		if (LtxSuModelContainer.class.equals(required)) {
			return fModel;
		}
		return super.getAdapter(required);
	}
	
}
