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

package de.walware.docmlet.wikitext.core;

import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.ecommons.ltk.IProblemRequestor;
import de.walware.ecommons.ltk.core.SourceContent;

import de.walware.docmlet.wikitext.core.model.IWikidocModelInfo;
import de.walware.docmlet.wikitext.core.model.IWikitextSourceUnit;


public class WikitextProblemReporter {
	
	
	public WikitextProblemReporter() {
	}
	
	
	public void run(final IWikitextSourceUnit su, final SourceContent content, 
			final IWikidocModelInfo model,
			final IProblemRequestor requestor, final int level, final IProgressMonitor monitor) {
	}
	
}
