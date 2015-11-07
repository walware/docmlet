/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.ui.sourceediting;

import org.eclipse.swt.graphics.RGB;

import de.walware.ecommons.preferences.core.Preference;
import de.walware.ecommons.preferences.ui.RGBPref;

import de.walware.docmlet.wikitext.ui.WikitextUI;


public class EmbeddedHtml {
	
	
	public static final String QUALIFIER= WikitextUI.PLUGIN_ID;
	
	public static final String HTML_COMMENT_COLOR_KEY= "html_ts_Comment.Font.color"; //$NON-NLS-1$
	public static final Preference<RGB> HTML_COMMENT_COLOR= new RGBPref(QUALIFIER, HTML_COMMENT_COLOR_KEY);
	
	public static final String HTML_BACKGROUND_COLOR_KEY= "html_ts_Default.Background.color"; //$NON-NLS-1$
	public static final Preference<RGB> HTML_BACKGROUND_COLOR= new RGBPref(QUALIFIER, HTML_BACKGROUND_COLOR_KEY);
	
	
}
