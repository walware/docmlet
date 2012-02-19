/*******************************************************************************
 * Copyright (c) 2009-2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.internal.core.model;

import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.IModelElement;
import de.walware.ecommons.ltk.IModelElementDelta;
import de.walware.ecommons.ltk.IModelManager;
import de.walware.ecommons.ltk.ISourceUnit;
import de.walware.ecommons.ltk.ISourceUnitModelInfo;
import de.walware.ecommons.ltk.core.impl.AbstractModelEventJob;
import de.walware.ecommons.ltk.core.impl.AbstractModelManager;
import de.walware.ecommons.ltk.core.impl.SourceUnitModelContainer;

import de.walware.docmlet.tex.core.model.ILtxModelInfo;
import de.walware.docmlet.tex.core.model.ILtxSourceUnit;
import de.walware.docmlet.tex.core.model.LtxSuModelContainer;


public class LtxModelManager extends AbstractModelManager implements IModelManager {
	
	
	private static class ModelDelta implements IModelElementDelta {
		
		private final int fLevel;
		private final IModelElement fElement;
		private final ISourceUnitModelInfo fOldInfo;
		private final AstInfo fOldAst;
		private final ISourceUnitModelInfo fNewInfo;
		private final AstInfo fNewAst;
		
		
		public ModelDelta(final IModelElement element,
				final ISourceUnitModelInfo oldInfo, final ISourceUnitModelInfo newInfo) {
			fLevel = IModelManager.MODEL_FILE;
			fElement = element;
			fOldInfo = oldInfo;
			fOldAst = (oldInfo != null) ? oldInfo.getAst() : null;
			fNewInfo = newInfo;
			fNewAst = (newInfo != null) ? newInfo.getAst() : null;
		}
		
		
		@Override
		public IModelElement getModelElement() {
			return fElement;
		}
		
		@Override
		public AstInfo getOldAst() {
			return fOldAst;
		}
		
		@Override
		public AstInfo getNewAst() {
			return fNewAst;
		}
		
	}
	
	protected static class EventJob extends AbstractModelEventJob<ILtxSourceUnit, ILtxModelInfo> {
		
		public EventJob(final LtxModelManager manager) {
			super(manager);
		}
		
		@Override
		protected IModelElementDelta createDelta(final Task task) {
			return new ModelDelta(task.getElement(), task.getOldInfo(), task.getNewInfo());
		}
		
		@Override
		protected void dispose() {
			super.dispose();
		}
		
	}
	
	
	private final EventJob fEventJob = new EventJob(this);
	
	private final Reconciler fReconciler = new Reconciler(this);
	
	
	public LtxModelManager(final String typeId) {
		super(typeId);
	}
	
	
	public void dispose() {
		fEventJob.dispose();
	}
	
	
	public EventJob getEventJob() {
		return fEventJob;
	}
	
	@Override
	public void reconcile(final SourceUnitModelContainer<?, ?> adapter,
			final int level, final IProgressMonitor monitor) {
		if (adapter instanceof LtxSuModelContainer) {
			fReconciler.reconcile((LtxSuModelContainer) adapter, level, monitor);
		}
	}
	
	@Override
	protected void reconcile(final ISourceUnit su, final int level, final IProgressMonitor monitor) {
		final LtxSuModelContainer adapter = (LtxSuModelContainer) su.getAdapter(LtxSuModelContainer.class);
		if (adapter != null) {
			fReconciler.reconcile(adapter, level, monitor);
		}
	}
	
}
