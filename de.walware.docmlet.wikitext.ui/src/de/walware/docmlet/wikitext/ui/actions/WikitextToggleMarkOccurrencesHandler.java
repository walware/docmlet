/*=============================================================================#
 # Copyright (c) 2008-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.ui.actions;

import de.walware.ecommons.ui.actions.TogglePreferenceEnablementHandler;

import de.walware.docmlet.wikitext.ui.sourceediting.WikitextEditingSettings;


/**
 * Toggles enablement of mark occurrences in Wikitext editors.
 */
public class WikitextToggleMarkOccurrencesHandler extends TogglePreferenceEnablementHandler {
	
	
	public WikitextToggleMarkOccurrencesHandler() {
		super(	WikitextEditingSettings.MARKOCCURRENCES_ENABLED_PREF,
				"org.eclipse.jdt.ui.edit.text.java.toggleMarkOccurrences"); //$NON-NLS-1$
	}
	
}
