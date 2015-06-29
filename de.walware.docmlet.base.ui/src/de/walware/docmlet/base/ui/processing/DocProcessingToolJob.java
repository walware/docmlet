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

package de.walware.docmlet.base.ui.processing;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.progress.IProgressConstants;


public class DocProcessingToolJob extends Job {
	
	
	private final DocProcessingToolProcess toolProcess;
	
	
	public DocProcessingToolJob(final DocProcessingToolProcess toolProcess) {
		super(toolProcess.getLabel());
		
		setUser(false);
		setPriority(Job.BUILD);
		setRule(new DocumentRule(toolProcess.getConfig().getSourceFile()));
		
		this.toolProcess= toolProcess;
		
		{	final Image image= toolProcess.getImage();
			if (image != null) {
				setProperty(IProgressConstants.ICON_PROPERTY, ImageDescriptor.createFromImage(image));
			}
		}
	}
	
	
	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		final Thread thread= new Thread("DocProcessingWorker-" + this.toolProcess.getLabel()) { //$NON-NLS-1$
			@Override
			public void run() {
				DocProcessingToolJob.this.toolProcess.run(monitor);
			}
		};
		thread.setDaemon(true);
		thread.start();
		while (true) {
			try {
				thread.join();
				break;
			}
			catch (final InterruptedException e) {
			}
		}
		
		return this.toolProcess.getStatus();
	}
	
	@Override
	protected void canceling() {
		try {
			this.toolProcess.terminate();
		}
		catch (final DebugException e) {
		}
	}
	
}
