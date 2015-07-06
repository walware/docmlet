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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import de.walware.docmlet.base.internal.ui.processing.Messages;
import de.walware.docmlet.base.ui.processing.DocProcessingConfig;
import de.walware.docmlet.base.ui.processing.DocProcessingOperation;
import de.walware.docmlet.base.ui.processing.DocProcessingToolConfig.StepConfig;
import de.walware.docmlet.base.ui.processing.DocProcessingToolProcess;
import de.walware.docmlet.base.ui.viewer.DocViewerUI;


public class CloseInDocViewerOperation extends DocProcessingOperation {
	
	
	public static final String ID= "de.walware.docmlet.base.docProcessing.CloseInDocViewerOperation"; //$NON-NLS-1$
	
	
	public CloseInDocViewerOperation() {
	}
	
	
	@Override
	public String getId() {
		return ID;
	}
	
	@Override
	public String getLabel() {
		return Messages.ProcessingOperation_CloseInDocViewer_label;
	}
	
	@Override
	public IStatus run(final DocProcessingToolProcess toolProcess, final SubMonitor m)
			throws CoreException, OperationCanceledException {
		final StepConfig previewConfig= getStepConfig().getToolConfig().getStep(DocProcessingConfig.BASE_PREVIEW_ATTR_QUALIFIER);
		
		DocViewerUI.runPreProduceOutputTask(previewConfig.getInputFileUtil(),
				previewConfig.getVariableResolver().getExtraVariables(), m );
		
		return Status.OK_STATUS;
	}
	
}
