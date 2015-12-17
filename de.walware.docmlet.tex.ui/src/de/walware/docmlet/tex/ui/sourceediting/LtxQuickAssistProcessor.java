/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.ui.sourceediting;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;

import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistProposalCollector;
import de.walware.ecommons.ltk.ui.sourceediting.assist.IQuickAssistComputer;
import de.walware.ecommons.ltk.ui.sourceediting.assist.QuickAssistProcessor;

import de.walware.docmlet.tex.internal.ui.editors.TexQuickRenameComputer;
import de.walware.docmlet.tex.internal.ui.sourceediting.LtxAssistInvocationContext;


public class LtxQuickAssistProcessor extends QuickAssistProcessor {
	
	
	private final IQuickAssistComputer refactoringComputer= new TexQuickRenameComputer();
	
	
	public LtxQuickAssistProcessor(final ISourceEditor editor) {
		super(editor);
	}
	
	
	@Override
	protected AssistInvocationContext createContext(final IQuickAssistInvocationContext invocationContext,
			final String contentType,
			final IProgressMonitor monitor) {
		return new LtxAssistInvocationContext(getEditor(),
				invocationContext.getOffset(), contentType,
				true, monitor );
	}
	
	@Override
	protected void addModelAssistProposals(final AssistInvocationContext context,
			final AssistProposalCollector proposals, final IProgressMonitor monitor) {
		this.refactoringComputer.computeAssistProposals(context, proposals, monitor);
	}
	
	
}
