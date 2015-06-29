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

package de.walware.docmlet.wikitext.ui;

import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;

import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.internal.ui.WikitextMarkupHelpProvider;


public class WikitextUI {
	
	
	public static final String PLUGIN_ID= "de.walware.docmlet.wikitext.ui"; //$NON-NLS-1$
	
	
	public static final String EDITOR_CONTEXT_ID= "de.walware.docmlet.wikitext.contexts.WikitextEditor"; //$NON-NLS-1$
	
	
	public static final String BASE_PREF_PAGE_ID= "de.walware.docmlet.wikitext.preferencePages.Wikitext"; //$NON-NLS-1$
	
	public static final String EDITOR_PREF_PAGE_ID= "de.walware.docmlet.wikitext.preferencePages.WikitextEditor"; //$NON-NLS-1$
	
	
	public static String getMarkupHelpContentIdFor(final IMarkupLanguage markupLanguage) {
		return WikitextMarkupHelpProvider.getContentIdFor((MarkupLanguage) markupLanguage);
	}
	
}
