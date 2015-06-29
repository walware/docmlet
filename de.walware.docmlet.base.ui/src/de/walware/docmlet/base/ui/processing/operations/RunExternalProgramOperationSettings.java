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

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import de.walware.ecommons.debug.core.variables.ResourceVariables;

import de.walware.docmlet.base.internal.ui.processing.Messages;
import de.walware.docmlet.base.ui.processing.operations.RunExternalProgramOperation.IExternalProgramLaunchConfig;


public class RunExternalProgramOperationSettings extends AbstractLaunchConfigOperationSettings {
	
	
	public RunExternalProgramOperationSettings() {
		super(IExternalProgramLaunchConfig.TYPE_ID);
	}
	
	
	@Override
	public String getId() {
		return RunExternalProgramOperation.ID;
	}
	
	@Override
	public String getLabel() {
		return Messages.ProcessingOperation_RunExternalProgram_label;
	}
	
	
	@Override
	protected void initializeNewLaunchConfig(final ILaunchConfigurationWorkingCopy config) {
		config.setAttribute(IExternalProgramLaunchConfig.ARGUMENTS_ATTR_NAME,
				"${" + ResourceVariables.RESOURCE_LOC_VAR_NAME + "}" ); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
}
