/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.core.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.osgi.util.NLS;

import de.walware.ecommons.ltk.IModelManager;
import de.walware.ecommons.ltk.ISourceUnitManager;
import de.walware.ecommons.ltk.LTK;
import de.walware.ecommons.ltk.core.model.ISourceUnit;

import de.walware.docmlet.tex.core.TexBuildParticipant;
import de.walware.docmlet.tex.core.TexCore;
import de.walware.docmlet.tex.core.model.ITexSourceUnit;
import de.walware.docmlet.tex.core.model.ITexWorkspaceSourceUnit;
import de.walware.docmlet.tex.core.model.LtxSuModelContainer;
import de.walware.docmlet.tex.internal.core.TexCorePlugin;
import de.walware.docmlet.tex.internal.core.model.LtxModelManager;


public class TexProjectBuild extends TexProjectTask
		implements IResourceVisitor, IResourceDeltaVisitor {
	
	
	private final static class VirtualSourceUnit {
		
		private final IFile file;
		
		private final String modelTypeId;
		
		
		public VirtualSourceUnit(final IFile file, final String modelTypeId) {
			this.file= file;
			this.modelTypeId= modelTypeId;
		}
		
		
		public IFile getResource() {
			return this.file;
		}
		
		public String getModelTypeId() {
			return this.modelTypeId;
		}
		
		
		@Override
		public int hashCode() {
			return this.file.hashCode();
		}
		
		@Override
		public String toString() {
			return this.file.toString();
		}
		
	}
	
	
	private final ISourceUnitManager suManager= LTK.getSourceUnitManager();
	
	private final MultiStatus status;
	
	private final List<ITexWorkspaceSourceUnit> updatedLtxUnits;
	private final List<VirtualSourceUnit> removedLtxFiles;
	
	private SubMonitor visitProgress;
	
	
	public TexProjectBuild(final TexProjectBuilder builder) {
		super(builder);
		
		this.status= new MultiStatus(TexCore.PLUGIN_ID, 0,
				NLS.bind("TeX build status for ''{0}''", getTexProject().getProject().getName() ),
				null );
		
		this.updatedLtxUnits= new ArrayList<>();
		this.removedLtxFiles= new ArrayList<>();
	}
	
	private void dispose(final SubMonitor m) {
		m.setWorkRemaining(this.updatedLtxUnits.size());
		for (final ITexSourceUnit unit : this.updatedLtxUnits) {
			unit.disconnect(m.newChild(1));
		}
	}
	
	
	public void build(final int kind,
			final SubMonitor m) throws CoreException {
		try {
			m.beginTask(NLS.bind("Preparing TeX build for ''{0}''", getTexProject().getProject().getName()),
					10 + 20 + 80 + 10 );
			
			final IResourceDelta delta;
			switch (kind) {
			case IncrementalProjectBuilder.AUTO_BUILD:
			case IncrementalProjectBuilder.INCREMENTAL_BUILD:
				delta= getTexProjectBuilder().getDelta(getTexProject().getProject());
				m.worked(10);
				break;
			default:
				delta= null;
			}
			
			if (m.isCanceled()) {
				throw new CoreException(Status.CANCEL_STATUS);
			}
			m.setWorkRemaining(20 + 80 + 10);
			
			this.visitProgress= m.newChild(20);
			if (delta != null) {
				setBuildType(IncrementalProjectBuilder.INCREMENTAL_BUILD);
				delta.accept(this);
			}
			else {
				setBuildType(IncrementalProjectBuilder.FULL_BUILD);
				getTexProject().getProject().accept(this);
			}
			this.visitProgress= null;
			
			if (m.isCanceled()) {
				throw new CoreException(Status.CANCEL_STATUS);
			}
			processLtxFiles(m.newChild(80, SubMonitor.SUPPRESS_NONE));
		}
		finally {
			m.setWorkRemaining(10);
			dispose(m.newChild(10));
			
			if (!this.status.isOK()) {
				TexCorePlugin.log(this.status);
			}
		}
	}
	
	
	@Override
	public boolean visit(final IResourceDelta delta) throws CoreException {
		final IResource resource= delta.getResource();
		if (resource.getType() == IResource.FILE) {
			if (this.visitProgress.isCanceled()) {
				throw new CoreException(Status.CANCEL_STATUS);
			}
			this.visitProgress.setWorkRemaining(100);
			
			try {
				switch (delta.getKind()) {
				case IResourceDelta.ADDED:
				case IResourceDelta.CHANGED:
					visitFileAdded((IFile) resource, delta, this.visitProgress.newChild(1));
					break;
				case IResourceDelta.REMOVED:
					visitFileRemove((IFile) resource, delta, this.visitProgress.newChild(1));
					break;
				default:
					break;
				}
			}
			catch (final Exception e) {
				this.status.add(new Status(IStatus.ERROR, TexCore.PLUGIN_ID, 0,
						"An error occurred when checking file ''{0}''", e ));
			}
		}
		
		return true;
	}
	
	@Override
	public boolean visit(final IResource resource) throws CoreException {
		if (resource.getType() == IResource.FILE) {
			this.visitProgress.setWorkRemaining(100);
			
			visitFileAdded((IFile) resource, null, this.visitProgress.newChild(1));
		}
		return true;
	}
	
	private void visitFileAdded(final IFile file, final IResourceDelta delta,
			final SubMonitor m) throws CoreException {
		final IContentDescription contentDescription= file.getContentDescription();
		if (contentDescription == null) {
			return;
		}
		final IContentType contentType= contentDescription.getContentType();
		if (contentType == null) {
			return;
		}
		if (contentType.isKindOf(TexCore.LTX_CONTENT_TYPE)) {
			final ISourceUnit unit= this.suManager.getSourceUnit(
					LTK.PERSISTENCE_CONTEXT, file, contentType, true, m );
			if (unit instanceof ITexWorkspaceSourceUnit) {
				this.updatedLtxUnits.add((ITexWorkspaceSourceUnit) unit);
			}
			else {
				if (unit != null) {
					unit.disconnect(m);
				}
				clearLtx(file, null);
			}
		}
	}
	
	private void visitFileRemove(final IFile file, final IResourceDelta delta,
			final SubMonitor m) throws CoreException {
		// There is no contentDescription for removed files
//		final IContentDescription contentDescription= file.getContentDescription();
		
//		if (contentType.isKindOf(LTX_CONTENT_TYPE)) {
//			final IModelTypeDescriptor modelType= this.modelRegistry.getModelTypeForContentType(contentType.getId());
//			final VirtualSourceUnit unit= new VirtualSourceUnit(file, (modelType != null) ? modelType.getId() : null);
//			this.removedLtxFiles.add(unit);
//			
//			if ((delta != null && (delta.getFlags() & IResourceDelta.MOVED_TO) != 0)) {
//				final IResource movedTo= file.getWorkspace().getRoot().findMember(delta.getMovedToPath());
//				if (movedTo instanceof IFile) {
//					final TexProject movedToProject= TexProject.getTexProject(movedTo.getProject());
//					if (modelType == null
//							|| movedToProject == null || movedToProject == getTexProject()
//							|| !getTexProjectBuilder().hasBeenBuilt(movedToProject.getProject()) ) {
//						clearLtx((IFile) movedTo, getParticipant(unit.getModelTypeId()));
//					}
//				}
//			}
//		}
	}
	
	private void processLtxFiles(final SubMonitor m) throws CoreException {
		m.beginTask(NLS.bind("Analyzing LaTeX file(s) of ''{0}''", getTexProject().getProject().getName()),
				2 );
		
		final LtxModelManager ltxModelManager= TexCorePlugin.getInstance().getLtxModelManager();
		
		{	final SubMonitor mPart= m.newChild(1);
			int mRemaining= this.removedLtxFiles.size() + this.updatedLtxUnits.size() * 5;
			mPart.setWorkRemaining(mRemaining);
			for (final VirtualSourceUnit unit : this.removedLtxFiles) {
				try {
					final TexBuildParticipant participant= getParticipant(unit.getModelTypeId());
					
					// >> remove from LTX index
					if (participant != null) {
						participant.ltxUnitRemoved(unit.getResource(), mPart.newChild(1));
					}
				}
				catch (final Exception e) {
					this.status.add(new Status(IStatus.ERROR, TexCore.PLUGIN_ID, 0,
							NLS.bind("An error occurred when processing removed file ''{0}''.", unit.getResource()),
							e ));
				}
				if (mPart.isCanceled()) {
					throw new CoreException(Status.CANCEL_STATUS);
				}
				mPart.setWorkRemaining((mRemaining-= 1));
			}
			
			if (!this.updatedLtxUnits.isEmpty()) {
				final LtxBuildReconciler ltxReconciler= new LtxBuildReconciler(ltxModelManager);
				for (final ITexWorkspaceSourceUnit unit : this.updatedLtxUnits) {
					try {
						final TexBuildParticipant participant= getParticipant(unit.getModelTypeId());
						
						clearLtx((IFile) unit.getResource(), participant);
						
						final LtxSuModelContainer<ITexSourceUnit> adapter= (LtxSuModelContainer<ITexSourceUnit>) unit.getAdapter(LtxSuModelContainer.class);
						if (adapter != null) {
							ltxReconciler.reconcile(adapter, IModelManager.MODEL_FILE, mPart.newChild(3));
							
							if (mPart.isCanceled()) {
								throw new CoreException(Status.CANCEL_STATUS);
							}
						}
						
						// >> update LTX index
						if (participant != null && participant.isEnabled()) {
							participant.ltxUnitUpdated(unit, mPart.newChild(2));
						}
					}
					catch (final Exception e) {
						this.status.add(new Status(IStatus.ERROR, TexCore.PLUGIN_ID, 0,
								NLS.bind("An error occurred when processing file ''{0}''.", unit.getResource()),
								e ));
					}
					if (mPart.isCanceled()) {
						throw new CoreException(Status.CANCEL_STATUS);
					}
					mPart.setWorkRemaining((mRemaining-= 5));
				}
			}
		}
		{	final SubMonitor mPart= m.newChild(1);
			final Collection<TexBuildParticipant> participants= getParticipants();
			mPart.setWorkRemaining(participants.size());
			for (final TexBuildParticipant participant : participants) {
				if (participant.isEnabled()) {
					try {
						participant.ltxFinished(mPart.newChild(1));
					}
					catch (final Exception e) {
						this.status.add(new Status(IStatus.ERROR, TexCore.PLUGIN_ID, 0,
								NLS.bind("An error occurred when processing LaTeX file(s) in ''{0}''.", getTexProject().getProject().getName()),
								e ));
					}
				}
			}
		}
	}
	
	private void clearLtx(final IFile file, final TexBuildParticipant partitipant) throws CoreException {
		if (partitipant != null) {
			partitipant.clear(file);
		}
	}
	
}
