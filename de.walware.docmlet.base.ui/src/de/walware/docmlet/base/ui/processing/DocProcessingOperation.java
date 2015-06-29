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

package de.walware.docmlet.base.ui.processing;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;

import de.walware.docmlet.base.ui.processing.DocProcessingToolConfig.StepConfig;


public abstract class DocProcessingOperation {
	
	
	private StepConfig stepConfig;
	
	
	public DocProcessingOperation() {
	}
	
	
	public abstract String getId();
	
	public abstract String getLabel();
	
	
	protected StepConfig getStepConfig() {
		return this.stepConfig;
	}
	
	
	public void init(final StepConfig stepConfig, final Map<String, String> settings,
			final SubMonitor m) throws CoreException {
		this.stepConfig= stepConfig;
	}
	
	
	public String getContextId() {
		return null;
	}
	
	public DocProcessingToolOperationContext createContext() {
		return null;
	}
	
	public int getTicks() {
		return 50;
	}
	
	
	public abstract IStatus run(final DocProcessingToolProcess toolProcess,
			final SubMonitor m) throws CoreException, OperationCanceledException;
	
	
}
