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

package de.walware.docmlet.wikitext.core;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;

import de.walware.docmlet.wikitext.core.markup.IMarkupLanguageManager1;
import de.walware.docmlet.wikitext.internal.core.MarkupLanguageManager1;
import de.walware.docmlet.wikitext.internal.core.WikitextCorePlugin;


public class WikitextCore {
	
	public static final String PLUGIN_ID= "de.walware.docmlet.wikitext.core"; //$NON-NLS-1$
	
	public static final String WIKIDOC_CONTENT_ID= "org.eclipse.mylyn.wikitext"; //$NON-NLS-1$
	
	public static final IContentType WIKIDOC_CONTENT_TYPE;
	
	/**
	 * Content type id for Wikitext documents
	 */
	public static final String WIKIDOC_CONTENT_ID_NG= "de.walware.docmlet.wikitext.contentTypes.Wikidoc"; //$NON-NLS-1$
	
	
	static {
		final IContentTypeManager contentTypeManager= Platform.getContentTypeManager();
		WIKIDOC_CONTENT_TYPE= contentTypeManager.getContentType(WIKIDOC_CONTENT_ID);
	}
	
	
	public static final IWikitextCoreAccess WORKBENCH_ACCESS= WikitextCorePlugin.getInstance().getWorkbenchAccess();
	
	public static IWikitextCoreAccess getWorkbenchAccess() {
		return WORKBENCH_ACCESS;
	}
	
	public static IWikitextCoreAccess getDefaultsAccess() {
		return WikitextCorePlugin.getInstance().getDefaultsAccess();
	}
	
	
	public static IMarkupLanguageManager1 getMarkupLanguageManager() {
		return MarkupLanguageManager1.INSTANCE;
	}
	
}
