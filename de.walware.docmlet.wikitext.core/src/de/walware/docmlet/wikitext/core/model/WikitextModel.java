/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
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
import de.walware.ecommons.ltk.core.model.ISourceUnit;

import de.walware.docmlet.wikitext.internal.core.WikitextCorePlugin;


public class WikitextModel {
	
	
	public static final String WIKIDOC_TYPE_ID= "Wikidoc"; //$NON-NLS-1$
	
	
	public static IModelManager getWikidocModelManager() {
		return WikitextCorePlugin.getInstance().getWikidocModelManager();
	}
	
	
	public static IWikitextSourceUnit asWikitextSourceUnit(final ISourceUnit su) {
		if (su instanceof IWikitextSourceUnit) {
			return (IWikitextSourceUnit) su;
		}
		return null;
	}
	
}
