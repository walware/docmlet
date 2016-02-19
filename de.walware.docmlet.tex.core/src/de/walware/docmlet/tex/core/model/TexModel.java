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

package de.walware.docmlet.tex.core.model;

import de.walware.ecommons.ltk.IModelManager;

import de.walware.docmlet.tex.internal.core.TexCorePlugin;


public class TexModel {
	
	
	public static final String LTX_TYPE_ID = "Ltx"; //$NON-NLS-1$
	
	
	public static IModelManager getLtxModelManager() {
		return TexCorePlugin.getInstance().getLtxModelManager();
	}
	
}
