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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.ecommons.ltk.IProblemRequestor;
import de.walware.ecommons.ltk.ast.IEmbeddingAstNode;
import de.walware.ecommons.ltk.core.SourceContent;
import de.walware.ecommons.ltk.core.model.ISourceStructElement;

import de.walware.eutils.yaml.core.ast.SourceComponent;
import de.walware.eutils.yaml.core.ast.YamlAstNode;
import de.walware.eutils.yaml.core.ast.YamlParser;
import de.walware.eutils.yaml.core.model.IYamlModelInfo;
import de.walware.eutils.yaml.core.model.YamlChunkElement;
import de.walware.eutils.yaml.core.model.YamlElementName;
import de.walware.eutils.yaml.core.model.YamlModel;
import de.walware.eutils.yaml.core.model.YamlProblemReporter;

import de.walware.docmlet.wikitext.core.ast.Embedded;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.model.EmbeddingReconcileItem;
import de.walware.docmlet.wikitext.core.model.IWikidocModelInfo;
import de.walware.docmlet.wikitext.core.model.IWikidocSuModelContainerEmbeddedExtension;


public class YamlReconcilerExtension implements IWikidocSuModelContainerEmbeddedExtension {
	
	
	private static final YamlElementName METADATA_ELEMENT_NAME= YamlElementName.create(
			YamlElementName.OTHER, "Metadata (YAML)" );
	
	
	private final YamlParser parser= new YamlParser();
	
	private final YamlProblemReporter problemReporter= new YamlProblemReporter();
	
	
	public YamlReconcilerExtension() {
	}
	
	
	@Override
	public void reconcileEmbeddedAst(final SourceContent content, final List<Embedded> list,
			final IMarkupLanguage markupLanguage, final int level, final IProgressMonitor monitor) {
		for (final IEmbeddingAstNode embeddingNode : list) {
			if (embeddingNode.getForeignTypeId() != YamlModel.YAML_TYPE_ID) {
				continue;
			}
			
			final String text= content.getText();
			int offset= embeddingNode.getOffset();
			while (offset < text.length() && Character.isWhitespace(text.charAt(offset))) {
				offset++;
			}
			final SourceComponent component= this.parser.parse(
					text.substring(offset, embeddingNode.getEndOffset()),
					embeddingNode, offset );
			embeddingNode.setForeignNode(component);
		}
	}
	
	@Override
	public void reconcileEmbeddedModel(final SourceContent content, final IWikidocModelInfo wikitextModel,
			final List<EmbeddingReconcileItem> list, final int level, final IProgressMonitor monitor) {
		final ISourceStructElement sourceElement= wikitextModel.getSourceElement();
		
		int metadataCount= 0;
		final List<YamlChunkElement> chunkElements= new ArrayList<>();
		for (final EmbeddingReconcileItem item : list) {
			if (item.getForeignTypeId() != YamlModel.YAML_TYPE_ID) {
				continue;
			}
			
			final YamlChunkElement element= new YamlChunkElement(item.getModelRefElement(),
					(YamlAstNode) item.getAstNode().getForeignNode(),
					METADATA_ELEMENT_NAME, metadataCount++ );
			item.setModelTypeElement(element);
			chunkElements.add(element);
		}
		
		if (metadataCount == 0) {
			return;
		}
		
		final IYamlModelInfo modelInfo= YamlModel.getYamlModelManager().reconcile(
				sourceElement.getSourceUnit(), wikitextModel, chunkElements, level, monitor );
		if (modelInfo != null) {
			wikitextModel.addAttachment(modelInfo);
		}
	}
	
	@Override
	public void reportEmbeddedProblems(final SourceContent content, final IWikidocModelInfo wikitextModel,
			final IProblemRequestor problemRequestor, final int level, final IProgressMonitor monitor) {
		final IYamlModelInfo yamlModel= YamlModel.getYamlModelInfo(wikitextModel);
		if (yamlModel == null) {
			return;
		}
		
		this.problemReporter.run(yamlModel, content, problemRequestor, level, monitor);
	}
	
}
