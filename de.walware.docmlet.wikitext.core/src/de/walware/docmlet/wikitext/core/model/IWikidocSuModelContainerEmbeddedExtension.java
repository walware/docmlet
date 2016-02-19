/*=============================================================================#
 # Copyright (c) 2011-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.core.model;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.ecommons.ltk.IProblemRequestor;
import de.walware.ecommons.ltk.core.SourceContent;

import de.walware.docmlet.wikitext.core.ast.Embedded;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;


public interface IWikidocSuModelContainerEmbeddedExtension {
	
	
//	String getNowebType();
//	
	void reconcileEmbeddedAst(SourceContent content, List<Embedded> list,
			IMarkupLanguage markupLanguage, int level, IProgressMonitor monitor);
	
	void reconcileEmbeddedModel(SourceContent content, IWikidocModelInfo wikitextModel,
			List<EmbeddingReconcileItem> list,
			int level, IProgressMonitor monitor);
	
	void reportEmbeddedProblems(SourceContent content, IWikidocModelInfo wikitextModel,
			IProblemRequestor problemRequestor,
			int level, IProgressMonitor monitor);
	
}
