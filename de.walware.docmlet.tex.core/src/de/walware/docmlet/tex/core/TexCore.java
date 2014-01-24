/*=============================================================================#
 # Copyright (c) 2012-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core;

import de.walware.docmlet.tex.internal.core.TexCorePlugin;


public class TexCore {
	
	public static final String PLUGIN_ID = "de.walware.docmlet.tex.core"; //$NON-NLS-1$
	
	public static final String LTX_CONTENT_ID = "net.sourceforge.texlipse.contentTypes.Latex"; //$NON-NLS-1$
	
	
	public static ITexCoreAccess getWorkbenchAccess() {
		return TexCorePlugin.getDefault().getWorkbenchAccess();
	}
	
	public static ITexCoreAccess getDefaultsAccess() {
		return null;
	}
	
}
