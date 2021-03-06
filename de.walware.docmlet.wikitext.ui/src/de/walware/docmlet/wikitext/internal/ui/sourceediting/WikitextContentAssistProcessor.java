/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.ui.sourceediting;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssist;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssistComputerRegistry;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssistProcessor;


public class WikitextContentAssistProcessor extends ContentAssistProcessor {
	
	
	public WikitextContentAssistProcessor(final ContentAssist assistant, final String partition, 
			final ContentAssistComputerRegistry registry, final ISourceEditor editor) {
		super(assistant, partition, registry, editor);
	}
	
	
	@Override
	protected AssistInvocationContext createCompletionProposalContext(final int offset,
			final IProgressMonitor monitor) {
		return new WikitextAssistInvocationContext(getEditor(), offset, getContentType(),
				true, monitor );
	}
	
	@Override
	protected AssistInvocationContext createContextInformationContext(final int offset,
			final IProgressMonitor monitor) {
		return new WikitextAssistInvocationContext(getEditor(), offset, getContentType(),
				false, monitor );
	}
	
	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return new char[] { };
	}
	
	@Override
	protected IContextInformationValidator createContextInformationValidator() {
		return null;
	}
	
	@Override
	protected boolean forceContextInformation(final AssistInvocationContext context) {
		return false;
	}
	
}
