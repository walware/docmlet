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

package de.walware.docmlet.tex.ui;

import de.walware.ecommons.text.ui.settings.AssistPreferences;
import de.walware.ecommons.text.ui.settings.DecorationPreferences;

import de.walware.docmlet.tex.internal.ui.TexUIPlugin;


public class TexUIPreferences {
	
	
	public static final AssistPreferences EDITING_ASSIST_PREFERENCES = new AssistPreferences(TexUIPlugin.TEX_EDITOR_QUALIFIER, TexUIPlugin.TEX_EDITOR_ASSIST_UI_GROUP_ID);
	
	public static final DecorationPreferences EDITING_DECO_PREFERENCES = new DecorationPreferences(TexUIPlugin.PLUGIN_ID);
	
	
}
