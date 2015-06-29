/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
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

import de.walware.ecommons.ltk.IModelManager;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;


public class WikitextAssistInvocationContext extends AssistInvocationContext {
	
	
	public WikitextAssistInvocationContext(final ISourceEditor editor, final int offset,
			final boolean isProposal, final IProgressMonitor monitor) {
		super(editor, offset, (isProposal) ? IModelManager.MODEL_FILE : IModelManager.NONE,
				monitor);
	}
	
	
}
