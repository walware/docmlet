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

package de.walware.docmlet.wikitext.core.model;

import de.walware.ecommons.ltk.IModelManager;
import de.walware.ecommons.ltk.core.impl.SourceUnitModelContainer;


public class WikidocSuModelContainer<U extends IWikitextSourceUnit>
		extends SourceUnitModelContainer<U, IWikidocModelInfo> {
	
	
	public WikidocSuModelContainer(final U su) {
		super(su);
	}
	
	
	@Override
	public boolean isContainerFor(final String modelTypeId) {
		return (modelTypeId == WikitextModel.WIKIDOC_TYPE_ID);
	}
	
	@Override
	public Class<?> getAdapterClass() {
		return WikidocSuModelContainer.class;
	}
	
	@Override
	protected IModelManager getModelManager() {
		return WikitextModel.getWikidocModelManager();
	}
	
}
