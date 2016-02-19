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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.osgi.util.NLS;

import de.walware.ecommons.debug.core.util.OverlayLaunchConfiguration;
import de.walware.ecommons.variables.core.VariableText2;
import de.walware.ecommons.variables.core.VariableUtils;

import de.walware.docmlet.base.internal.ui.processing.Messages;
import de.walware.docmlet.base.ui.DocBaseUI;
import de.walware.docmlet.base.ui.processing.DocProcessingConfig;


public class RunExternalProgramOperation extends AbstractLaunchConfigOperation {
	
	
	public static interface IExternalProgramLaunchConfig {
		
		String TYPE_ID= "org.eclipse.ui.externaltools.ProgramLaunchConfigurationType"; //$NON-NLS-1$
		
		String WORKING_DIRECTORY_ATTR_NAME=  "org.eclipse.ui.externaltools.ATTR_WORKING_DIRECTORY"; //$NON-NLS-1$
		String ARGUMENTS_ATTR_NAME= "org.eclipse.ui.externaltools.ATTR_TOOL_ARGUMENTS"; //$NON-NLS-1$
		
	}
	
	
	public static final String ID= "de.walware.docmlet.base.docProcessing.RunExternalProgramOperation"; //$NON-NLS-1$
	
	public static final String LAUNCH_CONFIG_NAME_ATTR_NAME= ID + '/' + LAUNCH_CONFIG_NAME_ATTR_KEY;
	
	
	public RunExternalProgramOperation() {
		super(RunExternalProgramOperation.IExternalProgramLaunchConfig.TYPE_ID);
	}
	
	
	@Override
	public String getId() {
		return ID;
	}
	
	@Override
	public String getLabel() {
		return Messages.ProcessingOperation_RunExternalProgram_label;
	}
	
	
	@Override
	protected ILaunchConfiguration preprocessConfig(final ILaunchConfiguration config) throws CoreException {
		final Map<String, Object> additionalAttributes= new HashMap<>();
		final VariableText2 variableResolver= createVariableResolver();
		
		additionalAttributes.put(IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND, false);
		
		try {
			String value= config.getAttribute(IExternalProgramLaunchConfig.WORKING_DIRECTORY_ATTR_NAME, ""); //$NON-NLS-1$
			if (value.isEmpty()) {
				value= VariableUtils.getValue(getStepConfig().getToolConfig().getVariables()
						.get(DocProcessingConfig.WD_LOC_VAR_NAME) );
				additionalAttributes.put(IExternalProgramLaunchConfig.WORKING_DIRECTORY_ATTR_NAME, value);
			}
			else {
				value= variableResolver.performStringSubstitution(value, null);
				additionalAttributes.put(IExternalProgramLaunchConfig.ARGUMENTS_ATTR_NAME, value);
			}
		}
		catch (final CoreException e) {
			throw new CoreException(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
					NLS.bind(Messages.ProcessingOperation_RunExternalProgram_Wd_error_SpecInvalid_message,
							e.getMessage() )));
		}
		
		try {
			String value= config.getAttribute(IExternalProgramLaunchConfig.ARGUMENTS_ATTR_NAME, ""); //$NON-NLS-1$
			if (!value.isEmpty()) {
				value= variableResolver.performStringSubstitution(value, null);
				additionalAttributes.put(IExternalProgramLaunchConfig.ARGUMENTS_ATTR_NAME, value);
			}
		}
		catch (final CoreException e) {
			throw new CoreException(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
					NLS.bind(Messages.ProcessingOperation_RunExternalProgram_Args_error_SpecInvalid_message,
							e.getMessage() )));
		}
		
		return new OverlayLaunchConfiguration(config, additionalAttributes);
	}
	
}
