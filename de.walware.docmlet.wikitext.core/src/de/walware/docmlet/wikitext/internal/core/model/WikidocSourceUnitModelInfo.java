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

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.core.impl.AbstractSourceModelInfo;
import de.walware.ecommons.ltk.core.model.INameAccessSet;

import de.walware.docmlet.wikitext.core.ast.WikitextAstInfo;
import de.walware.docmlet.wikitext.core.model.IWikidocModelInfo;
import de.walware.docmlet.wikitext.core.model.IWikitextSourceElement;
import de.walware.docmlet.wikitext.core.model.WikitextNameAccess;


public class WikidocSourceUnitModelInfo extends AbstractSourceModelInfo implements IWikidocModelInfo {
	
	
	private final IWikitextSourceElement sourceElement;
	
	private final INameAccessSet<WikitextNameAccess> linkAnchorLabels;
	private final INameAccessSet<WikitextNameAccess> linkDefLabels;
	
	private final int minSectionLevel;
	private final int maxSectionLevel;
	
	
	WikidocSourceUnitModelInfo(final AstInfo ast, final IWikitextSourceElement unitElement,
			final INameAccessSet<WikitextNameAccess> linkAnchorLabels,
			final INameAccessSet<WikitextNameAccess> linkDefLabels,
			final int minSectionLevel, final int maxSectionLevel) {
		super(ast);
		this.sourceElement= unitElement;
		
		this.linkAnchorLabels= linkAnchorLabels;
		this.linkDefLabels= linkDefLabels;
		
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
	public INameAccessSet<WikitextNameAccess> getLinkAnchorLabels() {
		return this.linkAnchorLabels;
	}
	
	@Override
	public INameAccessSet<WikitextNameAccess> getLinkRefLabels() {
		return this.linkDefLabels;
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
