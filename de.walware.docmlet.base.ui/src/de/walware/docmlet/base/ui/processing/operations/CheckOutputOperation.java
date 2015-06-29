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

package de.walware.docmlet.base.ui.processing.operations;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.osgi.util.NLS;

import de.walware.docmlet.base.internal.ui.processing.Messages;
import de.walware.docmlet.base.ui.DocBaseUI;
import de.walware.docmlet.base.ui.processing.DocProcessingOperation;
import de.walware.docmlet.base.ui.processing.DocProcessingToolConfig.StepConfig;
import de.walware.docmlet.base.ui.processing.DocProcessingToolProcess;


public class CheckOutputOperation extends DocProcessingOperation {
	
	
	public static final byte REFRESH_FILE= 1;
	public static final byte REFRESH_FILE_DIRECTORY= 2;
	public static final byte REFRESH_WORKING_DIRECTORY= 3;
	
	
	public static final String ID= "de.walware.docmlet.base.docProcessing.CheckFileOperation"; //$NON-NLS-1$
	
	
	private IFile file;
	
	private byte refeshMode= REFRESH_FILE;
	
	private int notExistsSeverity= IStatus.ERROR;
	
	
	public CheckOutputOperation() {
	}
	
	public CheckOutputOperation(final IFile file) {
		this.file= file;
	}
	
	
	@Override
	public String getId() {
		return ID;
	}
	
	@Override
	public String getLabel() {
		return Messages.ProcessingOperation_CheckOutput_label;
	}
	
	
	public void setFile(final IFile file) {
		this.file= file;
	}
	
	public IFile getFile() {
		return this.file;
	}
	
	public void setRefresh(final byte mode) {
		this.refeshMode= mode;
	}
	
	public byte getRefresh() {
		return this.refeshMode;
	}
	
	public void setNotExistsSeverity(final int severity) {
		this.notExistsSeverity= severity;
	}
	
	@Override
	public void init(final StepConfig stepConfig, final Map<String, String> settings,
			final SubMonitor m) throws CoreException {
		super.init(stepConfig, settings, m);
		
		if (this.file == null) {
			this.file= stepConfig.getOutputFile();
		}
	}
	
	
	@Override
	public int getTicks() {
		switch (this.refeshMode) {
		case REFRESH_WORKING_DIRECTORY:
			return 20;
		case REFRESH_FILE_DIRECTORY:
			return 15;
		default:
			return 10;
		}
	}
	
	@Override
	public IStatus run(final DocProcessingToolProcess toolProcess,
			final SubMonitor m) throws CoreException {
		final IFile file= getFile();
		if (file == null) {
			throw new NullPointerException("file"); //$NON-NLS-1$
		}
		
		m.beginTask(NLS.bind(Messages.ProcessingOperation_CheckOutput_task,
						getStepConfig().getLabel() ),
				1 + 10 + 2 );
		
		final IResource refreshResource;
		final boolean refreshFile;
		switch (this.refeshMode) {
		case REFRESH_FILE:
			refreshResource= null;
			refreshFile= true;
			break;
		case REFRESH_FILE_DIRECTORY:
			refreshResource= file.getParent();
			refreshFile= false;
			break;
		case REFRESH_WORKING_DIRECTORY:
			refreshResource= getStepConfig().getToolConfig().getWorkingDirectory();
			refreshFile= refreshResource.getFullPath().isPrefixOf(file.getFullPath());
			break;
		default:
			refreshResource= null;
			refreshFile= false;
		}
		
		final ISchedulingRule schedulingRule;
		{	final IResourceRuleFactory ruleFactory= file.getWorkspace().getRuleFactory();
			ISchedulingRule rule= null;
			if (refreshResource != null) {
				rule= ruleFactory.refreshRule(refreshResource);
			}
			if (refreshFile) {
				rule= MultiRule.combine(rule,
						ruleFactory.refreshRule(file) );
			}
			schedulingRule= toolProcess.beginSchedulingRule(rule, m.newChild(1));
		}
		try {
			if (refreshResource != null) {
				refreshResource.refreshLocal(IResource.DEPTH_INFINITE,
						m.newChild((refreshFile) ? 8 : 10) );
			}
			if (refreshFile) {
				file.refreshLocal(IResource.DEPTH_ZERO,
						m.newChild((refreshResource != null) ? 2 : 10) );
			}
			
			if (!file.exists()) {
				return new Status(this.notExistsSeverity, DocBaseUI.PLUGIN_ID,
						NLS.bind(Messages.ProcessingOperation_CheckOutput_error_FileNotExists_message,
								file.getName(), getStepConfig().getLabel() ));
			}
			
			return Status.OK_STATUS;
		}
		finally {
			toolProcess.endSchedulingRule(schedulingRule);
		}
	}
	
}
