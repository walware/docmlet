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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.osgi.util.NLS;

import de.walware.ecommons.debug.core.util.LaunchUtils;
import de.walware.ecommons.variables.core.VariableText2;

import de.walware.docmlet.base.internal.ui.processing.Messages;
import de.walware.docmlet.base.ui.DocBaseUI;
import de.walware.docmlet.base.ui.processing.DocProcessingConfig;
import de.walware.docmlet.base.ui.processing.DocProcessingOperation;
import de.walware.docmlet.base.ui.processing.DocProcessingToolConfig.StepConfig;
import de.walware.docmlet.base.ui.processing.DocProcessingToolProcess;


public abstract class AbstractLaunchConfigOperation extends DocProcessingOperation {
	
	
	protected static final String LAUNCH_CONFIG_NAME_ATTR_KEY= "LaunchConfig.name"; //$NON-NLS-1$
	
	
	private final String launchConfigTypeId;
	
	private final String launchConfigNameAttrName;
	
	private ILaunchManager launchManager;
	private ILaunchConfigurationType launchConfigType;
	
	private ILaunchConfiguration launchConfig;
	
	
	public AbstractLaunchConfigOperation(final String launchConfigTypeId) {
		this.launchConfigTypeId= launchConfigTypeId;
		
		this.launchConfigNameAttrName= getId() + '/' + LAUNCH_CONFIG_NAME_ATTR_KEY;
	}
	
	
	@Override
	public void init(final StepConfig stepConfig, final Map<String, String> settings,
			final SubMonitor m) throws CoreException {
		super.init(stepConfig, settings, m);
		
		this.launchManager= DebugPlugin.getDefault().getLaunchManager();
		this.launchConfigType= this.launchManager.getLaunchConfigurationType(this.launchConfigTypeId);
		if (this.launchConfigType == null) {
			throw new RuntimeException("Launch configuration type is missing: id= " + this.launchConfigTypeId); //$NON-NLS-1$
		}
		
		final String name= settings.get(this.launchConfigNameAttrName);
		if (name == null || name.isEmpty()) {
			throw new CoreException(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
					Messages.ProcessingOperation_RunLaunchConfig_Config_error_SpecMissing_message ));
		}
		
		final ILaunchConfiguration orgConfig= LaunchUtils.findLaunchConfiguration(
				this.launchManager.getLaunchConfigurations(), this.launchConfigType, name);
		
		if (orgConfig == null) {
			throw new CoreException(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
					NLS.bind(Messages.ProcessingOperation_RunLaunchConfig_Config_error_DefMissing_message,
							name )));
		}
		
		this.launchConfig= preprocessConfig(orgConfig);
	}
	
	
	protected String getLaunchMode() {
		return ILaunchManager.RUN_MODE;
	}
	
	@Override
	public IStatus run(final DocProcessingToolProcess toolProcess,
			final SubMonitor m) throws CoreException {
		final ILaunchConfiguration config= this.launchConfig;
		if (config == null) {
			throw new NullPointerException("launchConfig"); //$NON-NLS-1$
		}
		m.beginTask(getTaskLabel(config), 1 + 10);
		
		final String launchMode= getLaunchMode();
		final ILaunchConfigurationDelegate delegate= LaunchUtils.getLaunchConfigurationDelegate(
				this.launchConfig, launchMode, toolProcess.getStatus() );
		m.worked(1);
		
		delegate.launch(this.launchConfig, launchMode, toolProcess.getLaunch(), m.newChild(10));
		
		return Status.OK_STATUS;
	}
	
	protected String getTaskLabel(final ILaunchConfiguration config) {
		final StepConfig stepConfig= getStepConfig();
		if (stepConfig.getId() == DocProcessingConfig.BASE_PREVIEW_ATTR_QUALIFIER) {
			return NLS.bind(Messages.ProcessingOperation_RunLaunchConfig_ForPreview_task,
					config.getName(), stepConfig.getInputFile().getName() );
		}
		return NLS.bind(Messages.ProcessingOperation_RunLaunchConfig_task,
				config.getName() );
	}
	
	
	protected VariableText2 createVariableResolver() {
		final Map<String, IStringVariable> variables= new HashMap<>();
		
		variables.putAll(getStepConfig().getVariableResolver().getExtraVariables());
		
		return new VariableText2(variables);
	}
	
	protected ILaunchConfiguration preprocessConfig(final ILaunchConfiguration config) throws CoreException {
		return config;
	}
	
}
