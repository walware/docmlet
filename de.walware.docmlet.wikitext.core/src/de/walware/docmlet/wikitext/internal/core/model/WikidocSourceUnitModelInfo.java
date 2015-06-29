/*=============================================================================#
 # Copyright (c) 2008-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.core.model;

import java.util.Map;

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.core.impl.AbstractSourceModelInfo;

import de.walware.docmlet.wikitext.core.ast.WikitextAstInfo;
import de.walware.docmlet.wikitext.core.ast.WikitextAstNode;
import de.walware.docmlet.wikitext.core.model.IWikidocModelInfo;
import de.walware.docmlet.wikitext.core.model.IWikitextSourceElement;


public class WikidocSourceUnitModelInfo extends AbstractSourceModelInfo implements IWikidocModelInfo {
	
	
	private final IWikitextSourceElement sourceElement;
	
	private final Map<String, WikitextAstNode> labels;
	
	private final int minSectionLevel;
	private final int maxSectionLevel;
	
	
	WikidocSourceUnitModelInfo(final AstInfo ast, final IWikitextSourceElement unitElement,
			final Map<String, WikitextAstNode> labels,
			final int minSectionLevel, final int maxSectionLevel) {
		super(ast);
		this.sourceElement= unitElement;
		
		this.labels= labels;
		
		this.minSectionLevel= minSectionLevel;
		this.maxSectionLevel= maxSectionLevel;
	}
	
	
	@Override
	public IWikitextSourceElement getSourceElement() {
		return this.sourceElement;
	}
	
	@Override
	public WikitextAstInfo getAst() {
		return (WikitextAstInfo) super.getAst();
	}
	
	
	@Override
	public Map<String, WikitextAstNode> getLabels() {
		return this.labels;
	}
	
	@Override
	public int getMinSectionLevel() {
		return this.minSectionLevel;
	}
	
	@Override
	public int getMaxSectionLevel() {
		return this.maxSectionLevel;
	}
	
}
