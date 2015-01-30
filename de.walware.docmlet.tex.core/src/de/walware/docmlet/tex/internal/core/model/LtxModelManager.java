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
import de.walware.docmlet.tex.core.model.TexModel;


public class LtxModelManager extends AbstractModelManager implements IModelManager {
	
	
	private static class ModelDelta implements IModelElementDelta {
		
		private final int level;
		private final IModelElement element;
		private final ISourceUnitModelInfo oldInfo;
		private final AstInfo oldAst;
		private final ISourceUnitModelInfo newInfo;
		private final AstInfo newAst;
		
		
		public ModelDelta(final IModelElement element,
				final ISourceUnitModelInfo oldInfo, final ISourceUnitModelInfo newInfo) {
			this.level = IModelManager.MODEL_FILE;
			this.element = element;
			this.oldInfo = oldInfo;
			this.oldAst = (oldInfo != null) ? oldInfo.getAst() : null;
			this.newInfo = newInfo;
			this.newAst = (newInfo != null) ? newInfo.getAst() : null;
		}
		
		
		@Override
		public IModelElement getModelElement() {
			return this.element;
		}
		
		@Override
		public AstInfo getOldAst() {
			return this.oldAst;
		}
		
		@Override
		public AstInfo getNewAst() {
			return this.newAst;
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
	
	
	private final EventJob eventJob = new EventJob(this);
	
	private final LtxReconciler reconciler = new LtxReconciler(this);
	
	
	public LtxModelManager() {
		super(TexModel.LTX_TYPE_ID);
	}
	
	
	public void dispose() {
		this.eventJob.dispose();
	}
	
	
	public EventJob getEventJob() {
		return this.eventJob;
	}
	
	@Override
	public void reconcile(final SourceUnitModelContainer<?, ?> adapter,
			final int level, final IProgressMonitor monitor) {
		if (adapter instanceof LtxSuModelContainer) {
			this.reconciler.reconcile((LtxSuModelContainer<?>) adapter, level, monitor);
		}
	}
	
	@Override
	protected void reconcile(final ISourceUnit su, final int level, final IProgressMonitor monitor) {
		final LtxSuModelContainer<?> adapter = (LtxSuModelContainer<?>) su.getAdapter(LtxSuModelContainer.class);
		if (adapter != null) {
			this.reconciler.reconcile(adapter, level, monitor);
		}
	}
	
}
