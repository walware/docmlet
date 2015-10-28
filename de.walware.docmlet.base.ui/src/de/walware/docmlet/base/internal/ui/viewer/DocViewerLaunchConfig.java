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

package de.walware.docmlet.base.internal.ui.viewer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPage;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.debug.core.util.LaunchUtils;
import de.walware.ecommons.debug.core.variables.ResourceVariableResolver;
import de.walware.ecommons.debug.core.variables.ResourceVariables;
import de.walware.ecommons.io.FileValidator;
import de.walware.ecommons.io.win.DDE;
import de.walware.ecommons.io.win.DDEClient;
import de.walware.ecommons.ui.util.UIAccess;
import de.walware.ecommons.ui.workbench.ResourceVariableUtil;
import de.walware.ecommons.variables.core.StaticVariable;
import de.walware.ecommons.variables.core.VariableText2;
import de.walware.ecommons.variables.core.VariableUtils;

import de.walware.docmlet.base.ui.DocBaseUI;
import de.walware.docmlet.base.ui.processing.DocProcessingConfig;
import de.walware.docmlet.base.ui.viewer.DocViewerConfig;
import de.walware.docmlet.base.ui.viewer.DocViewerUI;


public class DocViewerLaunchConfig {
	
	
	protected static CoreException createMissingConfigAttr(final String attrName) {
		return new CoreException(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
				NLS.bind("Invalid configuration: configuration attribute ''{0}'' is missing.", attrName) ));
	}
	
	protected static CoreException createValidationFailed(final FileValidator validator) {
		final IStatus status= validator.getStatus();
		return new CoreException(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
				status.getMessage() ));
	}
	
	protected static CoreException createValidationFailed(final String message) {
		return new CoreException(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
				message ));
	}
	
	
	public static class DDETask {
		
		private final String command;
		private final String application;
		private final String topic;
		
		
		public DDETask(final String command, final String application, final String topic) {
			this.command= command;
			this.application= application;
			this.topic= topic;
		}
		
		
		public void exec() throws CoreException {
			DDEClient.execute(this.application, this.topic, this.command);
		}
		
		
	}
	
	
	private IFile outputFile;
	private ResourceVariableUtil outputFileUtil;
	
	private IPath programPath;
	
	private VariableText2 variableText;
	
	
	
	public DocViewerLaunchConfig() {
	}
	
	public DocViewerLaunchConfig(final ResourceVariableUtil sourceFileUtil,
			final Map<String, ? extends IStringVariable> extraVariables) {
		this.outputFileUtil= sourceFileUtil;
		setOutputFile((IFile) sourceFileUtil.getResource());
		
		if (extraVariables != null) {
			getVariableResolver().getExtraVariables().putAll(extraVariables);
		}
	}
	
	
	public VariableText2 getVariableResolver() {
		if (this.variableText == null) {
			final Map<String, IStringVariable> variables= new HashMap<>();
			this.variableText= new VariableText2(variables);
		}
		
		return this.variableText;
	}
	
	
	public void initOutputFile(final ILaunchConfiguration configuration,
			final SubMonitor m) throws CoreException {
		final FileValidator validator= new FileValidator(true);
		validator.setResourceLabel("document");
		validator.setRequireWorkspace(true, true);
		validator.setOnDirectory(IStatus.ERROR);
		
		final String path= configuration.getAttribute(DocViewerUI.TARGET_PATH_ATTR_NAME, (String) null);
		if (path != null) {
			validator.setExplicit(path);
		}
		else {
			UIAccess.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					final ResourceVariableUtil util= new ResourceVariableUtil();
					util.getResource();
					DocViewerLaunchConfig.this.outputFileUtil= util;
				}
			});
			if (this.outputFileUtil.getResource() == null) {
				throw new CoreException(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
						"No resource for 'document' to view selected in the active Workbench window." ));
			}
			validator.setExplicit(this.outputFileUtil.getResource());
		}
		
		if (validator.getStatus().getSeverity() == IStatus.ERROR) {
			throw createValidationFailed(validator);
		}
		
		if (this.outputFileUtil == null) {
		}
		setOutputFile((IFile) validator.getWorkspaceResource());
	}
	
	protected void setOutputFile(final IFile file) {
		this.outputFile= file;
		
		if (this.outputFileUtil == null) {
			UIAccess.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					final ResourceVariableUtil util= new ResourceVariableUtil(file);
					DocViewerLaunchConfig.this.outputFileUtil= util;
				}
			});
		}
		
		{	final Map<String, IStringVariable> variables= getVariableResolver().getExtraVariables();
			VariableUtils.add(variables,
					ResourceVariables.getSingleResourceVariables(),
					new ResourceVariableResolver(this.outputFileUtil) );
			VariableUtils.add(variables, new StaticVariable(
					DocProcessingConfig.SOURCE_FILE_PATH_VAR,
					file.getFullPath().toString() ));
		}
	}
	
	public IWorkbenchPage getWorkbenchPage() {
		return this.outputFileUtil.getWorkbenchPage();
	}
	
	public IFile getSourceFile() {
		return this.outputFile;
	}
	
	public ResourceVariableUtil getSourceFileVariableUtil() {
		return this.outputFileUtil;
	}
	
	
	public ProcessBuilder initProgram(final ILaunchConfiguration configuration) throws CoreException {
		{	final FileValidator validator= new FileValidator(true);
			validator.setResourceLabel("program location");
			validator.setOnDirectory(IStatus.ERROR);
			validator.setVariableResolver(getVariableResolver());
			
			final String path= configuration.getAttribute(DocViewerConfig.PROGRAM_FILE_ATTR_NAME,
					(String) null );
			if (path == null) {
				throw createMissingConfigAttr(DocViewerConfig.PROGRAM_FILE_ATTR_NAME);
			}
			validator.setExplicit(path);
			
			if (validator.getStatus().getSeverity() == IStatus.ERROR) {
				throw createValidationFailed(validator);
			}
			
			this.programPath= URIUtil.toPath(validator.getFileStore().toURI());
		}
		
		final ProcessBuilder processBuilder= new ProcessBuilder(this.programPath.toOSString());
		
		{	final ImList<String> arguments= getProgramArguments(configuration, getVariableResolver());
			if (!arguments.isEmpty()) {
				processBuilder.command().addAll(arguments);
			}
		}
		{	final Map<String, String> environment= processBuilder.environment();
			environment.clear();
			environment.putAll(LaunchUtils.createEnvironment(configuration, null));
		}
		
		return processBuilder;
	}
	
	private ImList<String> getProgramArguments(final ILaunchConfiguration configuration,
			final VariableText2 variableResolver) throws CoreException {
		String arguments= configuration.getAttribute(DocViewerConfig.PROGRAM_ARGUMENTS_ATTR_NAME,
				"" ); //$NON-NLS-1$
		if (arguments.isEmpty()) {
			return ImCollections.emptyList();
		}
		try {
			arguments= variableResolver.performStringSubstitution(arguments, null);
			return ImCollections.newList(DebugPlugin.parseArguments(arguments));
		}
		catch (final CoreException e) {
			throw createValidationFailed(NLS.bind(Messages.ProgramArgs_error_Other_message,
					e.getMessage() ));
		}
	}
	
	
	public DDETask loadDDETask(final ILaunchConfiguration configuration, final String attrQualifier,
			String taskLabel, final SubMonitor m) throws CoreException {
		if (DDE.isSupported()) {
			if (taskLabel == null) {
				taskLabel= "DDE"; //$NON-NLS-1$
			}
			String command= configuration.getAttribute(attrQualifier + '/' + DocViewerConfig.DDE_COMMAND_ATTR_KEY,
					(String) null );
			if (command != null && !command.isEmpty()) {
				String application= configuration.getAttribute(attrQualifier + '/' + DocViewerConfig.DDE_APPLICATION_ATTR_KEY,
						"" ); //$NON-NLS-1$
				String topic= configuration.getAttribute(attrQualifier + '/' + DocViewerConfig.DDE_TOPIC_ATTR_KEY,
						"" ); //$NON-NLS-1$
				
				final VariableText2 variableResolver= getVariableResolver();
				try {
					command= variableResolver.performStringSubstitution(command, null);
				}
				catch (final CoreException e) {
					createValidationFailed(NLS.bind(Messages.DDECommand_error_Other_message,
							taskLabel, e.getMessage() )); 
				}
				try {
					application= variableResolver.performStringSubstitution(application, null);
				}
				catch (final CoreException e) {
					throw createValidationFailed(NLS.bind(Messages.DDEApplication_error_Other_message,
							taskLabel, e.getMessage() )); 
				}
				try {
					topic= variableResolver.performStringSubstitution(topic, null);
				}
				catch (final CoreException e) {
					throw createValidationFailed(NLS.bind(Messages.DDETopic_error_Other_message,
							taskLabel, e.getMessage() )); 
				}
				
				return new DDETask(command, application, topic);
			}
		}
		return null;
	}
	
}
