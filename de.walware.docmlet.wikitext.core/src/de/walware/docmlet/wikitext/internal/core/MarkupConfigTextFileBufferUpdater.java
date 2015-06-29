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

package de.walware.docmlet.wikitext.internal.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.IFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.osgi.util.NLS;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.collections.ImIdentityList;
import de.walware.ecommons.ltk.IModelManager;
import de.walware.ecommons.ltk.ISourceUnitManager;
import de.walware.ecommons.ltk.LTK;
import de.walware.ecommons.ltk.core.model.ISourceUnit;
import de.walware.ecommons.ltk.core.model.IWorkspaceSourceUnit;

import de.walware.docmlet.wikitext.core.WikitextCore;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguageManager1;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguageManager1.IMarkupConfigChangedListener;
import de.walware.docmlet.wikitext.core.model.WikitextModel;
import de.walware.docmlet.wikitext.core.source.IMarkupLanguagePartitioner;
import de.walware.docmlet.wikitext.core.source.MarkupLanguageDocumentSetupParticipant;


public class MarkupConfigTextFileBufferUpdater implements IMarkupConfigChangedListener {
	
	
	private class UpdateRunnable implements Runnable {
		
		
		private final IFile file;
		private final AbstractDocument document;
		private final List<String> partitionings;
		private final Map<String, List<IProject>> languages;
		
		
		public UpdateRunnable(final IFile file, final AbstractDocument document,
				final List<String> partitionings, final Map<String, List<IProject>> languages) {
			this.file= file;
			this.document= document;
			this.partitionings= partitionings;
			this.languages= languages;
		}
		
		
		@Override
		public void run() {
			// runs in UI
			for (final String partitioning : this.partitionings) {
				final IDocumentPartitioner partitioner= this.document.getDocumentPartitioner(partitioning);
				if (partitioner instanceof IMarkupLanguagePartitioner) {
					try {
						final IMarkupLanguagePartitioner markupPartitioner= (IMarkupLanguagePartitioner) partitioner;
						final IMarkupLanguage currentMarkupLanguage= markupPartitioner.getMarkupLanguage();
						if (this.languages != null && !this.languages.containsKey(currentMarkupLanguage.getName())) {
							continue;
						}
						IMarkupLanguage newMarkupLanguage= null;
						if (this.file != null) {
							newMarkupLanguage= MarkupConfigTextFileBufferUpdater.this.markupLanguageManager.getLanguage(this.file,
									currentMarkupLanguage.getName(), true );
						}
						else {
							newMarkupLanguage= MarkupConfigTextFileBufferUpdater.this.markupLanguageManager.getLanguage(
									currentMarkupLanguage.getName() );
						}
						
						if (newMarkupLanguage != null) {
							markupPartitioner.setMarkupLanguage(newMarkupLanguage);
							this.document.setDocumentPartitioner(partitioning, partitioner);
						}
					}
					catch (final Exception e) {
						WikitextCorePlugin.log(new Status(IStatus.ERROR, WikitextCore.PLUGIN_ID,
								NLS.bind("An error occurred when trying to update the markup configuration of the open document (partitioning= {0}).", partitioning),
								e ));
					}
				}
			}
		}
		
	}
	
	
	private final IMarkupLanguageManager1 markupLanguageManager;
	
	private final ITextFileBufferManager textFileBufferManager;
	
	// TODO lookup at runtime
	private final ImIdentityList<String> markupModelTypeIds= ImCollections.newIdentityList(
			WikitextModel.WIKIDOC_TYPE_ID,
			"WikidocRweave" );
	
	private final List<String> checkPartitionings= new ArrayList<>();
	
	
	public MarkupConfigTextFileBufferUpdater(final IMarkupLanguageManager1 markupLanguageManager) {
		this.markupLanguageManager= markupLanguageManager;
		this.textFileBufferManager= FileBuffers.getTextFileBufferManager();
	}
	
	
	@Override
	public void configChanged(final Map<String, List<IProject>> languages,
			final IProgressMonitor monitor) throws CoreException {
		final SubMonitor m= SubMonitor.convert(monitor, 100 + 100);
		
		{	final SubMonitor mBuffers= m.newChild(100, 10 + 80 + 10 + 20);
			IFileBuffer[] fileBuffers;
			fileBuffers= this.textFileBufferManager.getFileBuffers();
			m.worked(10);
			checkFileBuffers(fileBuffers, languages, mBuffers.newChild(80));
			fileBuffers= this.textFileBufferManager.getFileStoreFileBuffers();
			m.worked(10);
			checkFileBuffers(fileBuffers, languages, mBuffers.newChild(20));
		}
		
		{	// Reconcile source units
			final SubMonitor mSus= m.newChild(100);
			final ISourceUnitManager suManager= LTK.getSourceUnitManager();
			final List<ISourceUnit> sus= suManager.getOpenSourceUnits(this.markupModelTypeIds,
					LTK.EDITOR_CONTEXT);
			mSus.setWorkRemaining(sus.size());
			for (final ISourceUnit su : sus) {
				checkEditSourceUnit(su, languages, mSus.newChild(1));
			}
		}
	}
	
	@Override
	public void configChanged(final IFile file,
			final IProgressMonitor monitor) throws CoreException {
		final SubMonitor m= SubMonitor.convert(monitor, 100 + 100);
		
		{	// Update document
			final SubMonitor mFileBuffers= m.newChild(100);
			final ITextFileBuffer fileBuffer= this.textFileBufferManager.getTextFileBuffer(
					file.getFullPath(), LocationKind.IFILE );
			if (fileBuffer != null) {
				checkFileBuffer(fileBuffer, file, null);
			}
		}
		
		{	// Reconcile source units
			final SubMonitor mSus= m.newChild(100);
			final ISourceUnitManager suManager= LTK.getSourceUnitManager();
			final List<ISourceUnit> sus= suManager.getOpenSourceUnits(this.markupModelTypeIds,
					LTK.EDITOR_CONTEXT, file );
			mSus.setWorkRemaining(sus.size());
			for (final ISourceUnit su : sus) {
				checkEditSourceUnit(su, null, mSus.newChild(1));
			}
		}
	}
	
	
	private AbstractDocument getDocument(final ITextFileBuffer fileBuffer) {
		final IDocument document= fileBuffer.getDocument();
		if (document instanceof AbstractDocument) {
			return (AbstractDocument) document;
		}
		else {
			return null;
		}
	}
	
	private void checkFileBuffers(final IFileBuffer[] fileBuffers,
			final Map<String, List<IProject>> languages, final SubMonitor m) {
		// Update documents
		final SubMonitor mFileBuffers= m.newChild(100);
		for (int i= 0; i < fileBuffers.length; i++) {
			mFileBuffers.setWorkRemaining(fileBuffers.length - i);
			if (fileBuffers[i] instanceof ITextFileBuffer) {
				checkFileBuffer((ITextFileBuffer) fileBuffers[i], null, languages);
			}
		}
	}
	
	private void checkFileBuffer(final ITextFileBuffer fileBuffer, IFile file,
			final Map<String, List<IProject>> languages) {
		final AbstractDocument document= getDocument(fileBuffer);
		if (document == null) {
			return;
		}
		
		this.checkPartitionings.clear();
		final String[] partitionings= document.getPartitionings();
		for (int i= 0; i < partitionings.length; i++) {
			if (document.getDocumentPartitioner(partitionings[i]) instanceof IMarkupLanguagePartitioner) {
				this.checkPartitionings.add(partitionings[i]);
			}
		}
		if (this.checkPartitionings.isEmpty()) {
			return;
		}
		
		if (file == null && fileBuffer.getLocation() != null) {
			file= FileBuffers.getWorkspaceFileAtLocation(fileBuffer.getLocation(), true);
		}
		
		final UpdateRunnable runnable= new UpdateRunnable(file, document,
				ImCollections.toList(this.checkPartitionings), languages );
		
		this.textFileBufferManager.execute(runnable); // async
	}
	
	private void checkEditSourceUnit(final ISourceUnit su,
			final Map<String, List<IProject>> languages,
			final SubMonitor m) {
		if (su.isConnected()) {
			su.connect(m);
			try {
				if (su.getModelInfo(null, 0, m) == null) {
					return;
				}
				final AbstractDocument document= su.getDocument(m);
				final IMarkupLanguage currentMarkupLanguage= MarkupLanguageDocumentSetupParticipant
						.getMarkupLanguage(document, su.getDocumentContentInfo().getPartitioning());
				if (currentMarkupLanguage != null && languages != null) {
					final List<IProject> projects= languages.get(currentMarkupLanguage.getName());
					if (projects == null) {
						return;
					}
					if (!projects.isEmpty() && su instanceof IWorkspaceSourceUnit
							&& !projects.contains(((IWorkspaceSourceUnit) su).getResource().getProject())) {
						return;
					}
				}
				su.getModelInfo(null, IModelManager.REFRESH | IModelManager.RECONCILE, m);
			}
			finally {
				su.disconnect(m);
			}
		}
	}
	
}
