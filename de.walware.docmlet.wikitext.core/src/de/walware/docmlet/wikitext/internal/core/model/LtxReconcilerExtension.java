/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.core.model;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.ecommons.ltk.IProblemRequestor;
import de.walware.ecommons.ltk.ast.IEmbeddingAstNode;
import de.walware.ecommons.ltk.core.SourceContent;
import de.walware.ecommons.string.InternStringCache;
import de.walware.ecommons.text.core.input.StringParserInput;

import de.walware.docmlet.tex.core.TexCore;
import de.walware.docmlet.tex.core.ast.LtxParser;
import de.walware.docmlet.tex.core.ast.SourceComponent;
import de.walware.docmlet.tex.core.commands.TexCommandSet;
import de.walware.docmlet.tex.core.model.TexModel;

import de.walware.docmlet.wikitext.core.ast.Embedded;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.model.EmbeddingReconcileItem;
import de.walware.docmlet.wikitext.core.model.IWikidocModelInfo;
import de.walware.docmlet.wikitext.core.model.IWikidocSuModelContainerEmbeddedExtension;


public class LtxReconcilerExtension implements IWikidocSuModelContainerEmbeddedExtension {
	
	
	private final StringParserInput raInput= new StringParserInput(0x1000);
	private final LtxParser raParser;
	
	
	public LtxReconcilerExtension() {
		this.raParser= new LtxParser(null, new InternStringCache(0x20));
	}
	
	
	@Override
	public void reconcileEmbeddedAst(final SourceContent content, final List<Embedded> list,
			final IMarkupLanguage markupLanguage, final int level, final IProgressMonitor monitor) {
		this.raInput.reset(content.getText());
		final TexCommandSet commandSet= TexCore.getWorkbenchAccess().getTexCommandSet();
		for (final IEmbeddingAstNode embeddingNode : list) {
			if (embeddingNode.getForeignTypeId() != TexModel.LTX_TYPE_ID) {
				continue;
			}
			
			final SourceComponent component= this.raParser.parse(
					this.raInput.init(embeddingNode.getOffset(), embeddingNode.getStopOffset()),
					embeddingNode, commandSet );
			embeddingNode.setForeignNode(component);
		}
	}
	
	@Override
	public void reconcileEmbeddedModel(final SourceContent content, final IWikidocModelInfo wikitextModel,
			final List<EmbeddingReconcileItem> list, final int level, final IProgressMonitor monitor) {
	}
	
	@Override
	public void reportEmbeddedProblems(final SourceContent content, final IWikidocModelInfo wikitextModel,
			final IProblemRequestor problemRequestor, final int level, final IProgressMonitor monitor) {
	}
	
}
