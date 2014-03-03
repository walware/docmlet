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

package de.walware.docmlet.tex.internal.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;

import de.walware.ecommons.ltk.IModelManager;
import de.walware.ecommons.ltk.ISourceUnit;
import de.walware.ecommons.ltk.ui.sourceediting.EcoReconciler2.ISourceUnitStrategy;

import de.walware.docmlet.tex.core.model.ILtxSourceUnit;
import de.walware.docmlet.tex.core.model.LtxSuModelContainer;
import de.walware.docmlet.tex.core.model.TexModel;


public class LtxReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension, ISourceUnitStrategy {
	
	
	private ISourceUnit input;
	private IProgressMonitor monitor;
	
	
	public LtxReconcilingStrategy() {
	}
	
	
	@Override
	public void initialReconcile() {
		reconcile();
	}
	
	@Override
	public void setDocument(final IDocument document) {
	}
	
	@Override
	public void setInput(final ISourceUnit input) {
		this.input= input;
	}
	
	@Override
	public void reconcile(final IRegion partition) {
		reconcile();
	}
	
	@Override
	public void reconcile(final DirtyRegion dirtyRegion, final IRegion subRegion) {
		reconcile();
	}
	
	@Override
	public void setProgressMonitor(final IProgressMonitor monitor) {
		this.monitor= monitor;
	}
	
	
	protected void reconcile() {
		final ISourceUnit su= this.input;
		if (!(su instanceof ILtxSourceUnit) || this.monitor.isCanceled()) {
			return;
		}
		final LtxSuModelContainer<?> adapter= (LtxSuModelContainer<?>) su.getAdapter(LtxSuModelContainer.class);
		if (adapter == null) {
			return;
		}
		final IModelManager modelManager= TexModel.getModelManager();
		if (modelManager != null) {
			modelManager.reconcile(adapter, (IModelManager.MODEL_FILE | IModelManager.RECONCILER),
					this.monitor );
		}
	}
	
}
