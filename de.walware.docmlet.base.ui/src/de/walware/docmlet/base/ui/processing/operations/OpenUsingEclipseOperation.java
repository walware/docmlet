/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.base.ui.processing.operations;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;

import de.walware.ecommons.ui.util.UIAccess;

import de.walware.docmlet.base.internal.ui.processing.Messages;
import de.walware.docmlet.base.ui.DocBaseUI;
import de.walware.docmlet.base.ui.processing.DocProcessingOperation;
import de.walware.docmlet.base.ui.processing.DocProcessingToolConfig.StepConfig;
import de.walware.docmlet.base.ui.processing.DocProcessingToolProcess;


public class OpenUsingEclipseOperation extends DocProcessingOperation {
	
	
	public static final String ID= "de.walware.docmlet.base.docProcessing.OpenUsingEclipseOperation"; //$NON-NLS-1$
	
	
	private IFile file;
	
	private int failSeverity= IStatus.ERROR;
	
	
	public OpenUsingEclipseOperation() {
	}
	
	public OpenUsingEclipseOperation(final IFile file) {
		this.file= file;
	}
	
	
	@Override
	public String getId() {
		return ID;
	}
	
	@Override
	public String getLabel() {
		return Messages.ProcessingOperation_OpenUsingEclipse_label;
	}
	
	
	public void setFile(final IFile file) {
		this.file= file;
	}
	
	public IFile getFile() {
		return this.file;
	}
	
	public void setFailSeverity(final int severity) {
		this.failSeverity= severity;
	}
	
	@Override
	public void init(final StepConfig stepConfig, final Map<String, String> settings,
			final SubMonitor m) throws CoreException {
		super.init(stepConfig, settings, m);
		
		if (this.file == null) {
			this.file= stepConfig.getInputFile();
		}
	}
	
	
	@Override
	public int getTicks() {
		return 15;
	}
	
	@Override
	public IStatus run(final DocProcessingToolProcess toolProcess,
			final SubMonitor m) throws CoreException {
		final IFile file= getFile();
		if (file == null) {
			throw new NullPointerException("file"); //$NON-NLS-1$
		}
		
		m.beginTask(NLS.bind(Messages.ProcessingOperation_OpenUsingEclipse_task, file.getName()),
				10 );
		
		class UIRunnable implements Runnable {
			
			private Exception error;
			
			@Override
			public void run() {
				try {
					IWorkbenchPage page= getStepConfig().getToolConfig().getWorkbenchPage();
					if (page == null || UIAccess.isOkToUse(page.getWorkbenchWindow().getShell())) {
						page= UIAccess.getActiveWorkbenchPage(true);
					}
					IDE.openEditor(page, file);
				}
				catch (final Exception e) {
					this.error= e;
				}
			}
		}
		final UIRunnable runnable= new UIRunnable();
		UIAccess.getDisplay().syncExec(runnable);
		
		if (runnable.error != null) {
			return new Status(this.failSeverity, DocBaseUI.PLUGIN_ID, 0,
					NLS.bind(Messages.ProcessingOperation_OpenUsingEclipse_error_message,
							file.getName(), getStepConfig().getLabel() ),
					runnable.error );
		}
		return Status.OK_STATUS;
	}
	
}
