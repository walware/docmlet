/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;

import de.walware.docmlet.tex.internal.core.TexCorePlugin;


public class TexCore {
	
	public static final String PLUGIN_ID= "de.walware.docmlet.tex.core"; //$NON-NLS-1$
	
	
	public static final String LTX_CONTENT_ID= "net.sourceforge.texlipse.contentTypes.Latex"; //$NON-NLS-1$
	
	public static final IContentType LTX_CONTENT_TYPE;
	
	/**
	 * Content type id for LaTeX documents
	 */
	public static final String LTX_CONTENT_ID_NG= "de.walware.docmlet.tex.contentTypes.Ltx"; //$NON-NLS-1$
	
	static {
		final IContentTypeManager contentTypeManager= Platform.getContentTypeManager();
		LTX_CONTENT_TYPE= contentTypeManager.getContentType(LTX_CONTENT_ID);
	}
	
	
	public static ITexCoreAccess getWorkbenchAccess() {
		return TexCorePlugin.getInstance().getWorkbenchAccess();
	}
	
	public static ITexCoreAccess getDefaultsAccess() {
		return TexCorePlugin.getInstance().getDefaultsAccess();
	}
	
}
