/*=============================================================================#
 # Copyright (c) 2012-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.ui.sourceediting;

import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistProposalCollector;
import de.walware.ecommons.ltk.ui.sourceediting.assist.IQuickAssistComputer;
import de.walware.ecommons.ltk.ui.sourceediting.assist.QuickAssistProcessor;

import de.walware.docmlet.base.ui.sourceediting.MarkupHelpComputer;

import de.walware.docmlet.wikitext.internal.ui.editors.WikitextQuickRenameComputer;


public class WikidocQuickAssistProcessor extends QuickAssistProcessor {
	
	
	private final IQuickAssistComputer refactoringComputer= new WikitextQuickRenameComputer();
	
	private final IQuickAssistComputer helpComputer= new MarkupHelpComputer();
	
	
	public WikidocQuickAssistProcessor(final ISourceEditor editor) {
		super(editor);
	}
	
	
	@Override
	protected void addModelAssistProposals(final AssistInvocationContext context,
			final AssistProposalCollector proposals, final IProgressMonitor monitor) {
		this.refactoringComputer.computeAssistProposals(context, proposals, monitor);
		this.helpComputer.computeAssistProposals(context, proposals, monitor);
	}
	
}
