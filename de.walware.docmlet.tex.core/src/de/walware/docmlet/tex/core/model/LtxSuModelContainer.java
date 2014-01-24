/*=============================================================================#
 # Copyright (c) 2009-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.model;

import de.walware.ecommons.ltk.IModelManager;
import de.walware.ecommons.ltk.core.impl.SourceUnitModelContainer;


public class LtxSuModelContainer<SuType extends ILtxSourceUnit> extends SourceUnitModelContainer<SuType, ILtxModelInfo> {
	
	
	public LtxSuModelContainer(final SuType su) {
		super(su);
	}
	
	
	@Override
	protected IModelManager getModelManager() {
		return TexModel.getModelManager();
	}
	
	
}
