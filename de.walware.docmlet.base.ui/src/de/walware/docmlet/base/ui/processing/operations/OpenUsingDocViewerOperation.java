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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

import de.walware.docmlet.base.internal.ui.processing.Messages;
import de.walware.docmlet.base.internal.ui.viewer.DocViewerLaunchDelegate;
import de.walware.docmlet.base.ui.processing.DocProcessingToolConfig.StepConfig;
import de.walware.docmlet.base.ui.processing.DocProcessingToolProcess;
import de.walware.docmlet.base.ui.viewer.DocViewerConfig;


public class OpenUsingDocViewerOperation extends AbstractLaunchConfigOperation {
	
	
	public static final String ID= "de.walware.docmlet.base.docProcessing.OpenUsingDocViewerOperation"; //$NON-NLS-1$
	
	public static final String LAUNCH_CONFIG_NAME_ATTR_NAME= ID + '/' + LAUNCH_CONFIG_NAME_ATTR_KEY;
	
	
	public OpenUsingDocViewerOperation() {
		super(DocViewerConfig.TYPE_ID);
	}
	
	
	@Override
	public String getId() {
		return ID;
	}
	
	@Override
	public String getLabel() {
		return Messages.ProcessingOperation_OpenUsingDocViewer_label;
	}
	 
	
	@Override
	protected void launch(final ILaunchConfigurationDelegate delegate,
			final ILaunchConfiguration config, final String launchMode,
			final DocProcessingToolProcess toolProcess,
			final SubMonitor m) throws CoreException {
		final StepConfig stepConfig= getStepConfig();
		
		((DocViewerLaunchDelegate) delegate).launch(stepConfig.getInputFileUtil(),
				stepConfig.getVariableResolver().getExtraVariables(), config,
				launchMode, toolProcess.getLaunch(), m );
	}
	
}
