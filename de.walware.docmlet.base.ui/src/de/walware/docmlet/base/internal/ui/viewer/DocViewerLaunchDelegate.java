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

package de.walware.docmlet.base.internal.ui.viewer;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import de.walware.ecommons.debug.core.util.LaunchUtils;
import de.walware.ecommons.io.win.DDE;
import de.walware.ecommons.io.win.DDEClient;
import de.walware.ecommons.ui.workbench.ResourceVariableUtil;

import de.walware.docmlet.base.internal.ui.DocBaseUIPlugin;
import de.walware.docmlet.base.internal.ui.viewer.DocViewerLaunchConfig.DDETask;
import de.walware.docmlet.base.ui.DocBaseUI;
import de.walware.docmlet.base.ui.viewer.DocViewerConfig;


public class DocViewerLaunchDelegate extends LaunchConfigurationDelegate {
	
	
	public DocViewerLaunchDelegate() {
	}
	
	
	@Override
	public void launch(final ILaunchConfiguration configuration, final String mode,
			final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
		launch(null, configuration, mode, launch, monitor);
	}
	
	public void launch(final ResourceVariableUtil sourceFileUtil,
			final Map<String, ? extends IStringVariable> extraVariables,
			final ILaunchConfiguration configuration, final String mode,
			final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
		launch(new DocViewerLaunchConfig(sourceFileUtil, extraVariables), configuration,
				mode, launch, monitor );
	}
	
	private void launch(DocViewerLaunchConfig config, 
			final ILaunchConfiguration configuration, final String mode,
			final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
		final SubMonitor m= LaunchUtils.initProgressMonitor(configuration, monitor,
				1 + 1 + 2 + 10 + 2 + 2);
		final long timestamp= System.currentTimeMillis();
		
		DocBaseUIPlugin.getInstance().getDocViewerCloseDelegate().cancelFocus();
		
		try {
			if (config == null) {
				config= new DocViewerLaunchConfig();
				config.initOutputFile(configuration, m.newChild(1));
			}
			
			final DDETask ddeTask= config.loadDDETask(configuration,
					DocViewerConfig.TASK_VIEW_OUTPUT_ATTR_QUALIFIER,
					Messages.DDE_ViewOutput_label,
					m.newChild(1) );
			if (ddeTask != null) {
				try {
					ddeTask.exec();
					return;
				}
				catch (final CoreException e) {
					switch (e.getStatus().getCode()) {
					case DDEClient.CONNECT_FAILED:
						break;
					default:
						throw e;
					}
				}
				m.worked(2);
			}
			
			if (m.isCanceled()) {
				return;
			}
			if (ddeTask == null) {
				m.setWorkRemaining(10);
			}
			
			{	final ProcessBuilder processBuilder= config.initProgram(configuration);
				
				final Process runtimeProcess;
				try {
					runtimeProcess= processBuilder.start();
				}
				catch (final IOException e) {
					throw new CoreException(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
							"An error occurred when launching document viewer.", e ));
				}
				
				final String processName = processBuilder.command().get(0) + ' ' + LaunchUtils.createProcessTimestamp(timestamp);
				
				final IProcess process= DebugPlugin.newProcess(launch, runtimeProcess, processName);
				
				process.setAttribute(IProcess.ATTR_CMDLINE, LaunchUtils.generateCommandLine(
						processBuilder.command() ));
			}
			
			if (ddeTask != null) {
				final int max= 4;
				for (int i = 1; DDE.isSupported() && i <= max; i++) {
					if (m.isCanceled()) {
						return;
					}
					
					try {
						Thread.sleep(500);
					}
					catch (final InterruptedException e) {
						Thread.interrupted();
					}
					try {
						ddeTask.exec();
						return;
					}
					catch (final CoreException e) {
						switch (e.getStatus().getCode()) {
						case DDEClient.CONNECT_FAILED:
							if (i < max) {
								continue;
							}
							//$FALL-THROUGH$
						default:
							throw e;
						}
					}
				}
			}
		}
		finally {
			m.done();
		}
	}
	
}
