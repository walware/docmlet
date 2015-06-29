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

package de.walware.docmlet.wikitext.internal.core.builder;

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

import de.walware.docmlet.wikitext.core.WikitextBuildParticipant;
import de.walware.docmlet.wikitext.core.WikitextCore;
import de.walware.docmlet.wikitext.core.model.IWikidocWorkspaceSourceUnit;
import de.walware.docmlet.wikitext.core.model.IWikitextSourceUnit;
import de.walware.docmlet.wikitext.core.model.WikidocSuModelContainer;
import de.walware.docmlet.wikitext.internal.core.WikitextCorePlugin;
import de.walware.docmlet.wikitext.internal.core.model.WikitextModelManager;


public class WikitextProjectBuild extends WikitextProjectTask
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
	
	private final List<IWikidocWorkspaceSourceUnit> updatedDocUnits;
	private final List<VirtualSourceUnit> removedDocFiles;
	
	private SubMonitor visitProgress;
	
	
	public WikitextProjectBuild(final WikitextProjectBuilder builder) {
		super(builder);
		
		this.status= new MultiStatus(WikitextCore.PLUGIN_ID, 0,
				NLS.bind("Wikitext build status for ''{0}''", getWikitextProject().getProject().getName() ),
				null );
		
		this.updatedDocUnits= new ArrayList<>();
		this.removedDocFiles= new ArrayList<>();
	}
	
	private void dispose(final SubMonitor m) {
		m.setWorkRemaining(this.updatedDocUnits.size());
		for (final IWikitextSourceUnit unit : this.updatedDocUnits) {
			unit.disconnect(m.newChild(1));
		}
	}
	
	
	public void build(final int kind,
			final SubMonitor m) throws CoreException {
		try {
			m.beginTask(NLS.bind("Preparing Wikitext build for ''{0}''", getWikitextProject().getProject().getName()),
					10 + 20 + 80 + 10 );
			
			final IResourceDelta delta;
			switch (kind) {
			case IncrementalProjectBuilder.AUTO_BUILD:
			case IncrementalProjectBuilder.INCREMENTAL_BUILD:
				delta= getWikitextProjectBuilder().getDelta(getWikitextProject().getProject());
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
				getWikitextProject().getProject().accept(this);
			}
			this.visitProgress= null;
			
			if (m.isCanceled()) {
				throw new CoreException(Status.CANCEL_STATUS);
			}
			processDocFiles(m.newChild(80, SubMonitor.SUPPRESS_NONE));
		}
		finally {
			m.setWorkRemaining(10);
			dispose(m.newChild(10));
			
			if (!this.status.isOK()) {
				WikitextCorePlugin.log(this.status);
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
				this.status.add(new Status(IStatus.ERROR, WikitextCore.PLUGIN_ID, 0,
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
		if (contentType.isKindOf(WikitextCore.WIKIDOC_CONTENT_TYPE)) {
			final ISourceUnit unit= this.suManager.getSourceUnit(
					LTK.PERSISTENCE_CONTEXT, file, contentType, true, m );
			if (unit instanceof IWikidocWorkspaceSourceUnit) {
				this.updatedDocUnits.add((IWikidocWorkspaceSourceUnit) unit);
			}
			else {
				if (unit != null) {
					unit.disconnect(m);
				}
				clearText(file, null);
			}
		}
	}
	
	private void visitFileRemove(final IFile file, final IResourceDelta delta,
			final SubMonitor m) throws CoreException {
		// There is no contentDescription for removed files
//		final IContentDescription contentDescription= file.getContentDescription();
		
//		if (contentType.isKindOf(WIKIDOC_CONTENT_TYPE)) {
//			final IModelTypeDescriptor modelType= this.modelRegistry.getModelTypeForContentType(contentType.getId());
//			final VirtualSourceUnit unit= new VirtualSourceUnit(file, (modelType != null) ? modelType.getId() : null);
//			this.removedDocFiles.add(unit);
//			
//			if ((delta != null && (delta.getFlags() & IResourceDelta.MOVED_TO) != 0)) {
//				final IResource movedTo= file.getWorkspace().getRoot().findMember(delta.getMovedToPath());
//				if (movedTo instanceof IFile) {
//					final WikitextProject movedToProject= WikitextProject.getWikitextProject(movedTo.getProject());
//					if (modelType == null
//							|| movedToProject == null || movedToProject == getWikitextProject()
//							|| !getWikitextProjectBuilder().hasBeenBuilt(movedToProject.getProject()) ) {
//						clearText((IFile) movedTo, getParticipant(unit.getModelTypeId()));
//					}
//				}
//			}
//		}
	}
	
	private void processDocFiles(final SubMonitor m) throws CoreException {
		m.beginTask(NLS.bind("Analyzing Wikitext file(s) of ''{0}''", getWikitextProject().getProject().getName()),
				2 );
		
		final WikitextModelManager wikitextModelManager= WikitextCorePlugin.getInstance().getWikidocModelManager();
		
		{	final SubMonitor sub= m.newChild(1);
			int subRemaining= this.removedDocFiles.size() + this.updatedDocUnits.size() * 5;
			sub.setWorkRemaining(subRemaining);
			for (final VirtualSourceUnit unit : this.removedDocFiles) {
				try {
					final WikitextBuildParticipant participant= getParticipant(unit.getModelTypeId());
					
					// >> remove from doc index
					if (participant != null) {
						participant.docUnitRemoved(unit.getResource(), sub.newChild(1));
					}
				}
				catch (final Exception e) {
					this.status.add(new Status(IStatus.ERROR, WikitextCore.PLUGIN_ID, 0,
							NLS.bind("An error occurred when processing removed file ''{0}''.", unit.getResource()),
							e ));
				}
				if (sub.isCanceled()) {
					throw new CoreException(Status.CANCEL_STATUS);
				}
				sub.setWorkRemaining((subRemaining-= 1));
			}
			
			if (!this.updatedDocUnits.isEmpty()) {
				final WikidocBuildReconciler docReconciler= new WikidocBuildReconciler(wikitextModelManager);
				for (final IWikidocWorkspaceSourceUnit unit : this.updatedDocUnits) {
					try {
						final WikitextBuildParticipant participant= getParticipant(unit.getModelTypeId());
						
						clearText((IFile) unit.getResource(), participant);
						
						final WikidocSuModelContainer<IWikitextSourceUnit> adapter= (WikidocSuModelContainer<IWikitextSourceUnit>) unit.getAdapter(WikidocSuModelContainer.class);
						if (adapter != null) {
							docReconciler.reconcile(adapter, IModelManager.MODEL_FILE, sub.newChild(3));
							
							if (sub.isCanceled()) {
								throw new CoreException(Status.CANCEL_STATUS);
							}
						}
						
						// >> update doc index
						if (participant != null && participant.isEnabled()) {
							participant.docUnitUpdated(unit, sub.newChild(2));
						}
					}
					catch (final Exception e) {
						this.status.add(new Status(IStatus.ERROR, WikitextCore.PLUGIN_ID, 0,
								NLS.bind("An error occurred when processing file ''{0}''.", unit.getResource()),
								e ));
					}
					if (sub.isCanceled()) {
						throw new CoreException(Status.CANCEL_STATUS);
					}
					sub.setWorkRemaining((subRemaining-= 5));
				}
			}
		}
		{	final SubMonitor sub= m.newChild(1);
			final Collection<WikitextBuildParticipant> participants= getParticipants();
			sub.setWorkRemaining(participants.size());
			for (final WikitextBuildParticipant participant : participants) {
				if (participant.isEnabled()) {
					try {
						participant.docFinished(sub.newChild(1));
					}
					catch (final Exception e) {
						this.status.add(new Status(IStatus.ERROR, WikitextCore.PLUGIN_ID, 0,
								NLS.bind("An error occurred when processing Wikitext file(s) in ''{0}''.", getWikitextProject().getProject().getName()),
								e ));
					}
				}
			}
		}
	}
	
	private void clearText(final IFile file, final WikitextBuildParticipant partitipant) throws CoreException {
		if (partitipant != null) {
			partitipant.clear(file);
		}
	}
	
}
