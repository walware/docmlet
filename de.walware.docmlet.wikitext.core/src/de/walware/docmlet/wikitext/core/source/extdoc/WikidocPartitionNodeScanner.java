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

package de.walware.docmlet.wikitext.core.source.extdoc;

import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNodeScanner;

import de.walware.docmlet.tex.core.source.LtxPartitionNodeScanner;
import de.walware.docmlet.tex.core.source.LtxPartitionNodeType;

import de.walware.eutils.yaml.core.source.YamlPartitionNodeScanner;

import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.source.EmbeddingAttributes;
import de.walware.docmlet.wikitext.core.source.IWikitextDocumentConstants;
import de.walware.docmlet.wikitext.core.source.WikitextPartitionNodeType;
import de.walware.docmlet.wikitext.core.source.WikitextWeavePartitionNodeScanner;


public class WikidocPartitionNodeScanner extends WikitextWeavePartitionNodeScanner {
	
	
	public static final WikitextPartitionNodeType YAML_CHUNK_WIKITEXT_TYPE= new WikitextPartitionNodeType();
	
	public static final LtxPartitionNodeType TEX_BASE_TEX_TYPE= new LtxPartitionNodeType() {
		
		@Override
		public String getPartitionType() {
			return IWikitextDocumentConstants.WIKIDOC_DEFAULT_CONTENT_TYPE;
		}
		
		@Override
		public byte getScannerState() {
			return LtxPartitionNodeType.DEFAULT_ROOT.getScannerState();
		}
		
	};
	public static final WikitextPartitionNodeType TEX_MATH_WIKITEXT_TYPE= new WikitextPartitionNodeType();
	
	
	private YamlPartitionNodeScanner yamlScanner;
	
	private LtxPartitionNodeScanner ltxScanner;
	
	
	public WikidocPartitionNodeScanner(final IMarkupLanguage markupLanguage) {
		super(markupLanguage);
	}
	
	public WikidocPartitionNodeScanner(final IMarkupLanguage markupLanguage,
			final boolean templateMode) {
		super(markupLanguage, templateMode);
	}
	
	
	@Override
	protected void configure(final IMarkupLanguage markupLanguage) {
		super.configure(markupLanguage);
		
		if (markupLanguage instanceof IExtdocMarkupLanguage) {
			((IExtdocMarkupLanguage) markupLanguage).setBlocksOnly(true, true);
		}
	}
	
	
	private ITreePartitionNodeScanner getYamlScanner() {
		if (this.yamlScanner == null) {
			this.yamlScanner= new YamlPartitionNodeScanner();
		}
		return this.yamlScanner;
	}
	
	private ITreePartitionNodeScanner getLtxScanner() {
		if (this.ltxScanner == null) {
			this.ltxScanner= new LtxPartitionNodeScanner(isTemplateMode());
		}
		return this.ltxScanner;
	}
	
	@Override
	protected void beginEmbeddingBlock(final BlockType type, final EmbeddingAttributes attributes) {
		if (type == BlockType.CODE
				&& attributes.getForeignType() == IExtdocMarkupLanguage.EMBEDDED_YAML) {
			addNode(YAML_CHUNK_WIKITEXT_TYPE, YAML_CHUNK_WIKITEXT_TYPE, getEventBeginOffset());
			setEmbedded(getNode(), attributes);
			return;
		}
		super.beginEmbeddingBlock(type, attributes);
	}
	
	
	@Override
	protected void endEmbeddingBlock(final WikitextPartitionNodeType type, final EmbeddingAttributes attributes) {
		if (type == YAML_CHUNK_WIKITEXT_TYPE) {
			executeForeignScanner(getYamlScanner());
			exitNode(getEventEndOffset());
			return;
		}
		super.endEmbeddingBlock(type, attributes);
	}
	
	@Override
	protected void beginEmbeddingSpan(final SpanType type, final EmbeddingAttributes attributes) {
		if (type == SpanType.CODE
				&& attributes.getForeignType() == IExtdocMarkupLanguage.EMBEDDED_LTX) {
			addNode(TEX_BASE_TEX_TYPE, TEX_MATH_WIKITEXT_TYPE, getEventBeginOffset());
			setEmbedded(getNode(), attributes);
			return;
		}
		super.beginEmbeddingSpan(type, attributes);
	}
	
	@Override
	protected void endEmbeddingSpan(final WikitextPartitionNodeType type, final EmbeddingAttributes attributes) {
		if (type == TEX_MATH_WIKITEXT_TYPE) {
			executeForeignScanner(getLtxScanner());
			exitNode(getEventEndOffset());
			return;
		}
		super.endEmbeddingSpan(type, attributes);
	}
	
}