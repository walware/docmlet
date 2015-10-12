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

import de.walware.ecommons.ltk.ast.IEmbeddingAstNode;

import de.walware.docmlet.tex.core.model.TexModel;

import de.walware.eutils.yaml.core.model.YamlModel;

import de.walware.docmlet.wikitext.core.WikitextProblemReporter;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguageExtension2;
import de.walware.docmlet.wikitext.core.source.IMarkupSourceFormatAdapter;


public interface IExtdocMarkupLanguage extends IMarkupLanguage, IMarkupLanguageExtension2 {
	
	
	String EMBEDDED_HTML= "Html"; //$NON-NLS-1$
	
	int EMBEDDED_HTML_OTHER_BLOCK_DESCR=                    IEmbeddingAstNode.EMBED_CHUNK;
	int EMBEDDED_HTML_DISTINCT_MASK=                        0x000000_7_0;
	int EMBEDDED_HTML_DISTINCT_SHIFT=                       Integer.lowestOneBit(EMBEDDED_HTML_DISTINCT_MASK);
	int EMBEDDED_HTML_COMMENT_FLAG=                         0x000000_8_0;
	int EMBEDDED_HTML_COMMENT_BLOCK_DESCR=                  IEmbeddingAstNode.EMBED_CHUNK | EMBEDDED_HTML_COMMENT_FLAG;
	
	int EMBEDDED_HTML_OTHER_INLINE_DESCR=                   IEmbeddingAstNode.EMBED_INLINE;
	int EMBEDDED_HTML_COMMENT_INLINE_DESCR=                 IEmbeddingAstNode.EMBED_INLINE | EMBEDDED_HTML_COMMENT_FLAG;
	
	
	String EMBEDDED_YAML= YamlModel.YAML_TYPE_ID;
	
	int EMBEDDED_YAML_METADATA_FLAG=                        0x000000_1_0;
	int EMBEDDED_YAML_METADATA_CHUNK_DESCR=                 IEmbeddingAstNode.EMBED_CHUNK | EMBEDDED_YAML_METADATA_FLAG;
	
	
	String EMBEDDED_LTX= TexModel.LTX_TYPE_ID;
	
	int EMBEDDED_TEX_MATH_FLAG=                             0x000000_2_0;
	int EMBEDDED_TEX_MATH_DOLLARS_INLINE_DESCR=             IEmbeddingAstNode.EMBED_INLINE | EMBEDDED_TEX_MATH_FLAG |
	                                                        0x0000_01_0_0;
	int EMBEDDED_TEX_MATH_DOLLARS_DISPLAY_DESCR=            IEmbeddingAstNode.EMBED_INLINE | EMBEDDED_TEX_MATH_FLAG |
	                                                        0x0000_02_0_0;
	int EMBEDDED_TEX_MATH_SBACKSLASH_INLINE_DESCR=          IEmbeddingAstNode.EMBED_INLINE | EMBEDDED_TEX_MATH_FLAG |
	                                                        0x0000_03_0_0;
	int EMBEDDED_TEX_MATH_SBACKSLASH_DISPLAY_DESCR=         IEmbeddingAstNode.EMBED_INLINE | EMBEDDED_TEX_MATH_FLAG |
	                                                        0x0000_04_0_0;
	
	
	@Override
	IExtdocMarkupLanguage clone();
	@Override
	IExtdocMarkupLanguage clone(String scopeKey, int mode);
	
	
	WikitextProblemReporter getProblemReporter();
	
	IMarkupSourceFormatAdapter getSourceFormatAdapter();
	
}
