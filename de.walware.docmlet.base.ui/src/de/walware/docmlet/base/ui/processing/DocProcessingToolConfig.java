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

import static de.walware.docmlet.base.ui.processing.DocProcessingConfig.WD_LOC_VAR_NAME;
import static de.walware.docmlet.base.ui.processing.DocProcessingConfig.WD_PATH_VAR_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPage;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.collections.ImList;
import de.walware.ecommons.debug.core.variables.ResourceVariableResolver;
import de.walware.ecommons.debug.core.variables.ResourceVariables;
import de.walware.ecommons.io.FileValidator;
import de.walware.ecommons.ui.util.UIAccess;
import de.walware.ecommons.ui.workbench.ResourceVariableUtil;
import de.walware.ecommons.variables.core.DynamicVariable;
import de.walware.ecommons.variables.core.StaticVariable;
import de.walware.ecommons.variables.core.VariableText2;
import de.walware.ecommons.variables.core.VariableUtils;

import de.walware.docmlet.base.internal.ui.processing.Messages;
import de.walware.docmlet.base.ui.DocBaseUI;
import de.walware.docmlet.base.ui.processing.DocProcessingConfig.Format;
import de.walware.docmlet.base.ui.processing.operations.CheckOutputOperation;
import de.walware.docmlet.base.ui.processing.operations.OpenUsingEclipseOperation;


public abstract class DocProcessingToolConfig {
	
	
	public static class StepConfig {
		
		public static final byte RUN_NO= 0;
		public static final byte RUN_DEFAULT= 1;
		public static final byte RUN_EXPLICITE= 2;
		
		
		private final DocProcessingToolConfig config;
		
		private final String id;
		
		private final String label;
		
		private byte run;
		
		private boolean isEnabled;
		
		private IFile inputFile;
		private ResourceVariableUtil inputFileUtil;
		private IFile outputFile;
		
		private DocProcessingOperation operation;
		
		private List<DocProcessingOperation> preOperations;
		private List<DocProcessingOperation> postOperations;
		
		private VariableText2 variableText;
		
		
		public StepConfig(final DocProcessingToolConfig config, final String id, final String label) {
			this.config= config;
			this.id= id;
			this.label= label;
			this.run= RUN_NO;
		}
		
		
		public final DocProcessingToolConfig getToolConfig() {
			return this.config;
		}
		
		public final String getId() {
			return this.id;
		}
		
		public String getLabel() {
			return this.label;
		}
		
		public void initRun(final byte run, final ILaunchConfiguration configuration)
				throws CoreException {
			this.isEnabled= configuration.getAttribute(
					getId() + '/' + DocProcessingConfig.STEP_ENABLED_ATTR_KEY, true);
			if (run == StepConfig.RUN_DEFAULT) {
				this.run= (this.isEnabled) ? StepConfig.RUN_DEFAULT : StepConfig.RUN_NO;
			}
			else {
				this.run= run;
			}
		}
		
		public final byte getRun() {
			return this.run;
		}
		
		public final boolean isRun() {
			return (this.run != 0);
		}
		
		public boolean isEnabled() {
			return this.isEnabled;
		}
		
		protected boolean resolveOutputFile() {
			return true;
		}
		
		
		public VariableText2 getVariableResolver() {
			if (this.variableText == null) {
				final Map<String, IStringVariable> variables= new HashMap<>();
				variables.putAll(this.config.getVariables());
				this.variableText= new VariableText2(variables);
			}
			
			return this.variableText;
		}
		
		
		public void initIOFiles(final IFile inputFile, final ILaunchConfiguration configuration,
				final SubMonitor m) throws CoreException {
			m.setWorkRemaining(1 + 3);
			
			if (inputFile == null) {
				throw new NullPointerException("inputFile"); //$NON-NLS-1$
			}
			setInputFile(inputFile);
			
			if (!resolveOutputFile()) {
				return;
			}
			
			m.worked(1);
			if (m.isCanceled()) {
				throw new CoreException(Status.CANCEL_STATUS);
			}
			
			final String formatAttrName= getId() + '/' + DocProcessingConfig.STEP_OUTPUT_FORMAT_ATTR_KEY;
			final String fileAttrName= getId() + '/' + DocProcessingConfig.STEP_OUTPUT_FILE_PATH_ATTR_KEY;
			
			final String formatKey= configuration.getAttribute(formatAttrName, (String) null);
			if (formatKey == null) {
				createMissingConfigAttr(formatAttrName);
			}
			final String filePath= configuration.getAttribute(fileAttrName, (String) null);
			if (filePath == null) {
				createMissingConfigAttr(fileAttrName);
			}
			
			m.worked(1);
			if (m.isCanceled()) {
				throw new CoreException(Status.CANCEL_STATUS);
			}
			
			String outputExt;
			try {
				outputExt= getToolConfig().getOutputExt(this, formatKey, m.newChild(1));
				if (outputExt == null) {
					throw new NullPointerException("outputExt"); //$NON-NLS-1$
				}
				
				final Map<String, IStringVariable> variables= getVariableResolver().getExtraVariables();
				VariableUtils.add(variables, new StaticVariable(
						DocProcessingConfig.OUT_FILE_EXT_VAR,
						outputExt ));
				
				m.worked(0);
				if (m.isCanceled()) {
					throw new CoreException(Status.CANCEL_STATUS);
				}
				
				final FileValidator validator= new FileValidator(false);
				validator.setResourceLabel("output file");
				validator.setRequireWorkspace(true, true);
				validator.setOnDirectory(IStatus.ERROR);
				validator.setRelative(getToolConfig().getVariables().get(WD_PATH_VAR_NAME), -1);
				validator.setVariableResolver(getVariableResolver());
				
				validator.setExplicit(filePath);
				
				if (validator.getStatus().getSeverity() == IStatus.ERROR) {
					throw createValidationFailed(validator);
				}
				setOutputFile((IFile) validator.getWorkspaceResource());
			}
			catch (final Exception e) {
				throw new CoreException(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
						NLS.bind("Failed to initialize IO configuration for {0}.",
								getLabel() ),
						e ));
			}
		}
		
		protected void setInputFile(final IFile file) {
			if (file == null) {
				throw new NullPointerException("file"); //$NON-NLS-1$
			}
			this.inputFile= file;
			this.inputFileUtil= new ResourceVariableUtil(
					getToolConfig().getSourceFileVariableUtil(),
					file );
			
			{	final Map<String, IStringVariable> variables= getVariableResolver().getExtraVariables();
				VariableUtils.add(variables, new StaticVariable(
						DocProcessingConfig.IN_FILE_PATH_VAR,
						file.getFullPath().toString() ));
				VariableUtils.add(variables,
						ResourceVariables.getSingleResourceVariables(),
						new ResourceVariableResolver(this.inputFileUtil) );
			}
		}
		
		protected void setOutputFile(final IFile file) {
			if (file == null) {
				throw new NullPointerException("file"); //$NON-NLS-1$
			}
			this.outputFile= file;
			
			final Map<String, IStringVariable> variables= getVariableResolver().getExtraVariables();
			VariableUtils.add(variables, new StaticVariable(
					DocProcessingConfig.OUT_FILE_PATH_VAR,
					file.getFullPath().toString() ));
		}
		
		public IFile getInputFile() {
			return this.inputFile;
		}
		
		public ResourceVariableUtil getInputFileUtil() {
			return this.inputFileUtil;
		}
		
		public IFile getOutputFile() {
			return this.outputFile;
		}
		
		
		public void initOperation(final ILaunchConfiguration configuration,
				final SubMonitor m) throws CoreException {
			m.setWorkRemaining(2 + 2);
			
			final String idAttrName= getId() + '/' + DocProcessingConfig.STEP_OPERATION_ID_ATTR_KEY;
			final String settingsAttrName= getId() + '/' + DocProcessingConfig.STEP_OPERATION_SETTINGS_ATTR_KEY;
			
			final String id= configuration.getAttribute(idAttrName, (String) null);
			if (id == null) {
				throw createMissingConfigAttr(idAttrName);
			}
			if (id.isEmpty()) {
				return;
			}
			try {
				this.operation= getToolConfig().createStepOperation(id);
				if (this.operation == null) {
					throw new UnsupportedOperationException("operationId= " + id); //$NON-NLS-1$
				}
				m.worked(2);
				
				final Map<String, String> settings= configuration.getAttribute(settingsAttrName,
						Collections.<String, String>emptyMap() );
				this.operation.init(this, settings, m.newChild(2));
			}
			catch (final Exception e) {
				throw new CoreException(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
						NLS.bind(Messages.ProcessingConfig_InitOperation_error_Failed_message,
								(this.operation != null) ? this.operation.getLabel() : "?",
								getLabel() ),
						e ));
			}
		}
		
		public DocProcessingOperation getOperation() {
			return this.operation;
		}
		
		
		public void initPre(final ILaunchConfiguration configuration,
				final SubMonitor m) throws CoreException {
		}
		
		protected void addPre(final DocProcessingOperation operation) {
			if (this.preOperations == null) {
				this.preOperations= new ArrayList<>(4);
			}
			this.preOperations.add(operation);
		}
		
		public List<DocProcessingOperation> getPre() {
			return this.preOperations;
		}
		
		
		public void initPost(final ILaunchConfiguration configuration,
				final SubMonitor m) throws CoreException {
		}
		
		protected void addPost(final DocProcessingOperation operation) {
			if (this.postOperations == null) {
				this.postOperations= new ArrayList<>(4);
			}
			this.postOperations.add(operation);
		}
		
		public List<DocProcessingOperation> getPost() {
			return this.postOperations;
		}
		
		protected boolean isOptionEnabled(final String key) {
			switch (key) {
			case "always": //$NON-NLS-1$
				return isRun();
			case "step_only": //$NON-NLS-1$
				return (getRun() == RUN_EXPLICITE);
			default:
				return false;
			}
		}
		
		
		@Override
		public String toString() {
			final StringBuilder sb= new StringBuilder("StepConfig"); //$NON-NLS-1$
			sb.append(" {").append("id= ").append(getId()).append("}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return sb.toString();
		}
		
	}
	
	public static class ProcessingStepConfig extends StepConfig {
		
		public ProcessingStepConfig(final DocProcessingToolConfig config, final String id,
				final String label) {
			super(config, id, label);
		}
		
		
		@Override
		public void initPost(final ILaunchConfiguration configuration,
				final SubMonitor m) throws CoreException {
			if (isOptionEnabled(configuration.getAttribute(
					getId() + '/' + DocProcessingConfig.STEP_POST_OPEN_OUTPUT_ENABLED_ATTR_KEY, "always" ))) { //$NON-NLS-1$
				final CheckOutputOperation operation= new CheckOutputOperation();
				operation.init(this, Collections.EMPTY_MAP, m);
				addPost(operation);
			}
			
			m.setWorkRemaining(1);
			
			if (isOptionEnabled(configuration.getAttribute(
					getId() + '/' + DocProcessingConfig.STEP_POST_OPEN_OUTPUT_ENABLED_ATTR_KEY, "disabled" ))) { //$NON-NLS-1$
				final OpenUsingEclipseOperation operation= new OpenUsingEclipseOperation(
						getOutputFile() );
				operation.init(this, Collections.EMPTY_MAP, m);
				operation.setFailSeverity((getRun() == StepConfig.RUN_EXPLICITE) ?
						IStatus.ERROR : IStatus.WARNING );
				addPost(operation);
			}
		}
		
	}
	
	public static class PreviewStepConfig extends StepConfig {
		
		
		public PreviewStepConfig(final DocProcessingToolConfig config) {
			super(config, DocProcessingConfig.BASE_PREVIEW_ATTR_QUALIFIER, Messages.Preview_label);
		}
		
		
		@Override
		protected boolean resolveOutputFile() {
			return false;
		}
		
		
	}
	
	
	protected static CoreException createMissingConfigAttr(final String attrName) {
		return new CoreException(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
				NLS.bind("Invalid configuration: configuration attribute ''{0}'' is missing.", attrName) ));
	}
	
	protected static CoreException createValidationFailed(final FileValidator validator) {
		final IStatus status= validator.getStatus();
		return new CoreException(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
				status.getMessage() ));
	}
	
	
	private ImList<StepConfig> steps;
	
	private Map<String, IStringVariable> globalVariables;
	
	private IFile sourceFile;
	private ResourceVariableUtil sourceFileUtil;
	
	private IContainer workingDirectory;
	
	
	public DocProcessingToolConfig() {
	}
	
	
	protected void setSteps(final StepConfig... steps) {
		this.steps= ImCollections.newList(steps);
	}
	
	public ImList<StepConfig> getSteps() {
		return this.steps;
	}
	
	public StepConfig getStep(final String stepId) {
		for (final StepConfig aStep : this.steps) {
			if (aStep.getId() == stepId) {
				return aStep;
			}
		}
		return null;
	}
	
	public Map<String, IStringVariable> getVariables() {
		if (this.globalVariables == null) {
			this.globalVariables= new HashMap<>();
		}
		return this.globalVariables;
	}
	
	
	public void initSourceFile(final ILaunchConfiguration configuration,
			final SubMonitor m) throws CoreException {
		final FileValidator validator= new FileValidator(true);
		validator.setResourceLabel("source document");
		validator.setRequireWorkspace(true, true);
		validator.setOnDirectory(IStatus.ERROR);
		
		final String path= configuration.getAttribute(DocProcessingUI.TARGET_PATH_ATTR_NAME,
				(String) null );
		if (path != null) {
			validator.setExplicit(path);
		}
		else {
			UIAccess.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					final ResourceVariableUtil util= new ResourceVariableUtil();
					util.getResource();
					DocProcessingToolConfig.this.sourceFileUtil= util;
				}
			});
			if (this.sourceFileUtil.getResource() == null) {
				throw new CoreException(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
						"No resource for 'source document' selected in the active Workbench window." ));
			}
			validator.setExplicit(this.sourceFileUtil.getResource());
		}
		
		if (validator.getStatus().getSeverity() == IStatus.ERROR) {
			throw createValidationFailed(validator);
		}
		
		setSourceFile((IFile) validator.getWorkspaceResource());
	}
	
	protected void setSourceFile(final IFile file) {
		this.sourceFile= file;
		
		if (this.sourceFileUtil == null) {
			UIAccess.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					final ResourceVariableUtil util= new ResourceVariableUtil(file);
					DocProcessingToolConfig.this.sourceFileUtil= util;
				}
			});
		}
		
		{	final Map<String, IStringVariable> variables= getVariables();
			VariableUtils.add(variables,
					ResourceVariables.getSingleResourceVariables(),
					new ResourceVariableResolver(this.sourceFileUtil) );
			VariableUtils.add(variables, new StaticVariable(
					DocProcessingConfig.SOURCE_FILE_PATH_VAR,
					file.getFullPath().toString() ));
		}
	}
	
	public IWorkbenchPage getWorkbenchPage() {
		return this.sourceFileUtil.getWorkbenchPage();
	}
	
	public IFile getSourceFile() {
		return this.sourceFile;
	}
	
	public ResourceVariableUtil getSourceFileVariableUtil() {
		return this.sourceFileUtil;
	}
	
	
	public void initWorkingDirectory(final ILaunchConfiguration configuration,
			final SubMonitor m) throws CoreException {
		final String wdAttrName= DocProcessingConfig.WORKING_DIRECTORY_ATTR_NAME;
		
		final FileValidator validator= new FileValidator(true);
		validator.setResourceLabel("working directory");
		validator.setRequireWorkspace(true, true);
		validator.setOnFile(IStatus.ERROR);
		validator.setVariableResolver(new VariableText2(getVariables()));
		
		final String path= configuration.getAttribute(wdAttrName, (String) null);
		if (path != null && !path.isEmpty()) {
			validator.setExplicit(path);
		}
		else {
			throw createMissingConfigAttr(wdAttrName);
		}
		
		if (validator.getStatus().getSeverity() == IStatus.ERROR) {
			throw createValidationFailed(validator);
		}
		
		setWorkingDirectory((IContainer) validator.getWorkspaceResource());
	}
	
	protected void setWorkingDirectory(final IContainer directory) {
		this.workingDirectory= directory;
		
		final ResourceVariableResolver resolver= new ResourceVariableResolver() {
			@Override
			public String resolveValue(final IDynamicVariable variable, final String argument)
					throws CoreException {
				switch (variable.getName()) {
				case WD_LOC_VAR_NAME:
					return toLocValue(variable, getWorkingDirectory());
				case WD_PATH_VAR_NAME:
					return toPathValue(variable, getWorkingDirectory());
				default:
					throw new UnsupportedOperationException(variable.getName());
				}
			}
		};
		final Map<String, IStringVariable> variables= getVariables();
		VariableUtils.add(variables, new DynamicVariable.ResolverVariable(
				WD_LOC_VAR_NAME, null, false, resolver ));
		VariableUtils.add(variables, new DynamicVariable.ResolverVariable(
				WD_PATH_VAR_NAME, null, false, resolver ));
	}
	
	public IContainer getWorkingDirectory() {
		return this.workingDirectory;
	}
	
	
	protected String getOutputExt(final StepConfig stepConfig, final String formatKey,
			final SubMonitor subMonitor) throws CoreException {
		if (formatKey.startsWith(Format.EXT_TYPE + ":")) {
			return formatKey.substring((Format.EXT_TYPE + ":").length());
		}
		throw new UnsupportedOperationException("formatKey= " + formatKey); //$NON-NLS-1$
	}
	
	protected String resolveExt(final StepConfig stepConfig, final Format format) {
		return format.getExt(stepConfig.getInputFile().getFileExtension());
	}
	
	protected abstract DocProcessingOperation createStepOperation(String id);
	
}
