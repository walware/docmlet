/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.base.ui.processing.actions;

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.services.IServiceScopes;

import de.walware.ecommons.ltk.ui.sourceediting.SourceEditor1;

import de.walware.docmlet.base.ui.processing.DocProcessingManager;
import de.walware.docmlet.base.ui.processing.DocProcessingUI;


public class RunDocProcessingOnSaveExtension {
	
	
	private final SourceEditor1 editor;
	
	private boolean isRunEnabled;
	
	
	public RunDocProcessingOnSaveExtension(final SourceEditor1 editor) {
		this.editor= editor;
	}
	
	
	public boolean isAutoRunEnabled() {
		return this.isRunEnabled;
	}
	
	public void setAutoRunEnabled(final boolean enabled) {
		this.isRunEnabled= enabled;
		
		final ICommandService commandService= (ICommandService) this.editor.getServiceLocator()
				.getService(ICommandService.class);
		final Map<String, IWorkbenchWindow> filter= Collections.singletonMap(IServiceScopes.WINDOW_SCOPE, this.editor.getSite()
				.getPage().getWorkbenchWindow() );
		commandService.refreshElements(DocProcessingUI.TOGGLE_RUN_ON_SAVE_COMMAND_ID, filter);
	}
	
	public void onEditorSaved() {
		if (isAutoRunEnabled()) {
			runDocProcessing();
		}
	}
	
	private void runDocProcessing() {
		final IFile file= ResourceUtil.getFile(this.editor.getEditorInput());
		final DocProcessingManager manager= DocProcessingUI.getDocProcessingManager(
				this.editor.getContentType(), true);
		if (file == null || manager == null) {
			return;
		}
		final ILaunchConfiguration config= manager.getActiveConfig();
		if (config != null) {
			manager.launch(config, file, DocProcessingUI.CommonFlags.PROCESS_AND_PREVIEW);
		}
	}
	
}
