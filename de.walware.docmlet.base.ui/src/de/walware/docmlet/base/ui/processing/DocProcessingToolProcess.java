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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.statushandlers.StatusManager;

import de.walware.jcommons.collections.IdentityCollection;

import de.walware.ecommons.ICommonStatusConstants;
import de.walware.ecommons.debug.core.model.AbstractProcess;
import de.walware.ecommons.runtime.core.util.EnrichProgressMonitor;

import de.walware.docmlet.base.internal.ui.processing.Messages;
import de.walware.docmlet.base.ui.DocBaseUI;
import de.walware.docmlet.base.ui.processing.DocProcessingToolConfig.StepConfig;


public class DocProcessingToolProcess extends AbstractProcess {
	
	
	private static final int CONTEXT_TICKS= 10;
	private static final int STEP_TICKS= 10;
	
	
	private static String createName(final ILaunchConfiguration config, final IFile file) {
		final String[] names= new String[3];
		try {
			names[0]= config.getType().getName();
		}
		catch (final CoreException e) {}
		if (names[0] == null) {
			names[0]= "Document Processing"; //$NON-NLS-1$
		}
		names[1]= config.getName();
		names[2]= file.getName();
		
		return NLS.bind(Messages.ProcessingProcess_label, names);
	}
	
	
	private final DocProcessingToolConfig config;
	
	private final Image image;
	
	private EnrichProgressMonitor enrichMonitor;
	private SubMonitor monitor;
	private Thread workerThread;
	
	private final MultiStatus status;
	
	private List<DocProcessingToolOperationContext> contexts;
	private final DocProcessingToolOperationIterator nextContextIterator;
	private String currentContextId;
	private DocProcessingToolOperationContext currentContext;
	
	private final DocProcessingToolOperationIterator operationIterator;
	private StepConfig currentStepConfig;
	
	
	public DocProcessingToolProcess(final ILaunch launch, final DocProcessingToolConfig config) {
		super(launch, createName(launch.getLaunchConfiguration(), config.getSourceFile()));
		this.config= config;
		this.operationIterator= new DocProcessingToolOperationIterator(config.getSteps());
		this.nextContextIterator= new DocProcessingToolOperationIterator(config.getSteps());
		
		this.status= new MultiStatus(DocBaseUI.PLUGIN_ID, ICommonStatusConstants.LAUNCHING,
				getLabel(), null);
		
		this.image= fetchImage();
	}
	
	
	protected Image fetchImage() {
		try {
			final ILaunchConfiguration configuration= getLaunch().getLaunchConfiguration();
			if (configuration != null) {
				final String contentTypeId= configuration.getAttribute(
						DocProcessingUI.CONTENT_TYPE_ID_ATTR_NAME, (String) null);
				DocProcessingManager manager= null;
				if (contentTypeId != null) {
					DocProcessingUI.getDocProcessingManager(contentTypeId);
				}
				else {
					final IContentType contentType= this.config.getSourceFile()
							.getContentDescription().getContentType();
					if (contentType != null) {
						manager= DocProcessingUI.getDocProcessingManager(contentType, true);
					}
				}
				if (manager != null) {
					return manager.getActionImage((IdentityCollection<String>)
							configuration.getAttribute(
									DocProcessingUI.RUN_STEPS_ATTR_NAME, Collections.EMPTY_SET ));
				}
			}
		}
		catch (final Exception e) {}
		return null;
	}
	
	
	public Image getImage() {
		return this.image;
	}
	
	public DocProcessingToolConfig getConfig() {
		return this.config;
	}
	
	public final MultiStatus getStatus() {
		return this.status;
	}
	
	public void check(final IStatus status) throws CoreException {
		switch (status.getSeverity()) {
		case IStatus.OK:
			return;
		case IStatus.INFO:
		case IStatus.WARNING:
			log(status);
			return;
		default:
			throw new CoreException(status);
		}
	}
	
	public void log(final IStatus status) {
		synchronized (this.status) {
			if (status.getSeverity() == IStatus.CANCEL
					&& (this.status.getSeverity() == IStatus.ERROR
							|| (this.status.getSeverity() == IStatus.CANCEL && status == Status.CANCEL_STATUS) )) {
				return;
			}
			this.status.add(status);
		}
	}
	
	protected boolean shouldContinue() {
		final int severity;
		synchronized (this.status) { 
			severity= this.status.getSeverity();
			if (severity >= IStatus.ERROR) {
				return false;
			}
			final IProgressMonitor monitor= this.monitor;
			if (monitor != null && monitor.isCanceled()) {
				this.status.add(Status.CANCEL_STATUS);
				return false;
			}
			return true;
		}
	}
	
	
	public ISchedulingRule beginSchedulingRule(final ISchedulingRule rule,
			final IProgressMonitor monitor) throws OperationCanceledException {
		if (rule != null) {
			Job.getJobManager().beginRule(rule, monitor);
		}
		return rule;
	}
	
	public void endSchedulingRule(final ISchedulingRule rule) {
		if (rule != null) {
			Job.getJobManager().endRule(rule);
		}
	}
	
	
	public DocProcessingToolOperationContext getCurrentOperationContext() {
		return this.currentContext;
	}
	
	public String getCurrentStepLabel() {
		final StepConfig stepConfig= this.operationIterator.getStepConfig();
		return (stepConfig != null) ? stepConfig.getLabel() : "";
	}
	
	
	@Override
	public boolean canTerminate() {
		return (this.monitor != null);
	}
	
	@Override
	public boolean isTerminated() {
		return (this.monitor == null);
	}
	
	@Override
	public void terminate() throws DebugException {
		{	final IProgressMonitor monitor= this.monitor;
			if (monitor != null) {
				monitor.setCanceled(true);
			}
		}
		{	final DocProcessingToolOperationContext context= this.currentContext;
			if (context != null) {
				context.cancel();
			}
		}
		{	final Thread thread= this.workerThread;
			if (thread != null) {
				thread.interrupt();
			}
		}
	}
	
	
	public IStatus run(final IProgressMonitor monitor) {
		this.enrichMonitor= (monitor != null) ? new EnrichProgressMonitor(monitor) : null;
		this.monitor= SubMonitor.convert(this.enrichMonitor, getLabel(), calculateTicks());
		this.workerThread= Thread.currentThread();
		
		created();
		
		try {
			runProcessing(this.monitor);
		}
		catch (final Throwable e) {
			doSetExitValue(-1);
			log(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, 0,
					Messages.ProcessingProcess_error_UnexpectedError_message,
					e ));
		}
		finally {
			runFinished();
		}
		
		return getStatus();
	}
	
	protected void stepChanged(final StepConfig finishedStepConfig, final StepConfig nextStepConfig) {
		if (finishedStepConfig != null) {
			final SubMonitor m= this.monitor;
			if (m != null) {
				m.worked(STEP_TICKS);
			}
		}
		
		this.currentStepConfig= nextStepConfig;
		
		{	final EnrichProgressMonitor m= this.enrichMonitor;
			if (m != null) {
				m.setPrefix((nextStepConfig != null) ?
						'[' + nextStepConfig.getLabel() + "] " : //$NON-NLS-1$
						null );
			}
		}
	}
	
	protected void runFinished() {
		if (this.status.getSeverity() > IStatus.WARNING) {
			int exitValue= doGetExitValue();
			if (exitValue == 0) {
				doSetExitValue(exitValue= 1);
			}
			log(new Status(IStatus.INFO, DocBaseUI.PLUGIN_ID,
					"Exit code= " + exitValue ));
		}
		if (this.status.getSeverity() > IStatus.INFO) {
			StatusManager.getManager().handle(this.status, StatusManager.LOG);
		}
		
		this.enrichMonitor= null;
		this.monitor= null;
		this.workerThread= null;
		
		terminated();
	}
	
	
	protected int calculateTicks() {
		try {
			int ticks= 0;
			String currentContextId= null;
			String currentStepId= null;
			while (this.operationIterator.next()) {
				final DocProcessingOperation operation= this.operationIterator.getOperation();
				// new step
				final String stepId= this.operationIterator.getStepConfig().getId();
				if (currentStepId != stepId) {
					ticks+= STEP_TICKS;
					currentStepId= stepId;
				}
				// new context
				if (isContextRequired(this.operationIterator)) {
					final String contextId= operation.getContextId();
					if (contextId != currentContextId) {
						ticks+= CONTEXT_TICKS;
						currentContextId= contextId;
					}
				}
				ticks+= operation.getTicks();
			}
			return ticks;
		}
		finally {
			this.operationIterator.reset();
		}
	}
	
	protected void runProcessing(final SubMonitor progress) {
		this.operationIterator.reset();
		this.operationIterator.hasNext();
		
		stepChanged(null, this.operationIterator.getStepConfig());
		executeOperations();
	}
	
	protected void executeOperations() {
		while (this.operationIterator.hasNext() && shouldContinue()) {
			final String contextId= getNextRequiredContextId();
			if (contextId != this.currentContextId) {
				if (this.currentContextId == null) { // contextId != null
					startNextRequiredContext();
					continue;
				}
				else { // exit current context
					return;
				}
			}
			
			executeNextOperation();
			continue;
		}
	}
	
	private boolean isContextRequired(final DocProcessingToolOperationIterator iterator) {
		return (iterator.getOperation().getContextId() != null
				|| iterator.getStepPart() == DocProcessingToolOperationIterator.MAIN );
	}
	
	private String getNextRequiredContextId() {
		if (this.nextContextIterator.compareTo(this.operationIterator) < 0) {
			this.nextContextIterator.reset(this.operationIterator);
		}
		while (this.nextContextIterator.hasNext()) {
			if (isContextRequired(this.nextContextIterator)) {
				return this.nextContextIterator.getOperation().getContextId();
			}
			this.nextContextIterator.next(); // not required
		}
		return null;
	}
	
	private void startNextRequiredContext() {
		final IStatus status= startContext(getContext(this.nextContextIterator.getOperation()));
		if (status.getSeverity() != IStatus.OK) {
			log(status);
		}
	}
	
	protected DocProcessingToolOperationContext getContext(final DocProcessingOperation operation) {
		final String id= operation.getContextId();
		if (id == null) {
			return null;
		}
		if (this.contexts == null) {
			this.contexts= new ArrayList<>(4);
		}
		else {
			for (final DocProcessingToolOperationContext aContext : this.contexts) {
				if (id == aContext.getId()) {
					return aContext;
				}
			}
		}
		final DocProcessingToolOperationContext context= operation.createContext();
		if (context == null) {
			throw new NullPointerException("context: id= " + id); //$NON-NLS-1$
		}
		
		this.contexts.add(context);
		return context;
	}
	
	protected IStatus startContext(final DocProcessingToolOperationContext context) {
		assert (this.currentContext == null);
		
		this.currentContext= context;
		this.currentContextId= context.getId();
		try {
			this.currentContext.start(this, new Runnable() {
				@Override
				public void run() {
					executeOperations();
				}
			}, this.monitor.newChild(CONTEXT_TICKS, SubMonitor.SUPPRESS_NONE) );
			
			return Status.OK_STATUS;
		}
		catch (final CoreException e) {
			if (e.getStatus().getSeverity() == IStatus.CANCEL) {
				return e.getStatus();
			}
			return new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
					NLS.bind(Messages.ProcessingProcess_RunInContext_error_Failed_message,
							context.getLabel() ),
					e );
		}
		catch (final Exception e) {
			return new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, 
					NLS.bind(Messages.ProcessingProcess_RunInContext_error_UnexpectedError_message,
							context.getLabel() ),
					e );
		}
		finally {
			this.currentContext= null;
			this.currentContextId= null;
		}
	}
	
	private void executeNextOperation() {
		try {
			final IStatus status= executeOperation(this.operationIterator.getOperation());
			if (status.getSeverity() != IStatus.OK) {
				log(status);
			}
		}
		finally {
			this.operationIterator.next();
			
			final StepConfig nextStepConfig= this.operationIterator.getStepConfig();
			if (nextStepConfig != this.currentStepConfig) {
				stepChanged(this.currentStepConfig, nextStepConfig);
			}
		}
	}
	
	protected IStatus executeOperation(final DocProcessingOperation operation) {
		final SubMonitor m= this.monitor.newChild(operation.getTicks(), SubMonitor.SUPPRESS_NONE);
		try {
			return operation.run(this, m);
		}
		catch (final CoreException e) {
			if (e.getStatus().getSeverity() == IStatus.CANCEL) {
				return e.getStatus();
			}
			return new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
					NLS.bind(Messages.ProcessingProcess_RunOperation_error_Failed_message,
							operation.getLabel(), getCurrentStepLabel() ),
					e );
		}
		catch (final OperationCanceledException e) {
			return new Status(IStatus.CANCEL, DocBaseUI.PLUGIN_ID,
					NLS.bind(Messages.ProcessingProcess_RunOperation_error_Cancelled_message,
							operation.getLabel(), getCurrentStepLabel() ),
					e );
		}
		catch (final Exception e) {
			return new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, 
					NLS.bind(Messages.ProcessingProcess_RunOperation_error_UnexpectedError_message,
							operation.getLabel(), getCurrentStepLabel() ),
					e );
		}
		finally {
			m.done();
		}
	}
	
}
