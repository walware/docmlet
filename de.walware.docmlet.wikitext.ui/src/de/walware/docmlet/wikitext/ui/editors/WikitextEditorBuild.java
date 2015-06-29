/*=============================================================================#
 # Copyright (c) 2011-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.ui.editors;

import de.walware.ecommons.preferences.Preference.BooleanPref;

import de.walware.docmlet.wikitext.ui.sourceediting.WikitextEditingSettings;


public class WikitextEditorBuild {
	
	
	public static final String GROUP_ID= "Wikitext/editor/build"; //$NON-NLS-1$
	
	
	public static final BooleanPref PROBLEMCHECKING_ENABLED_PREF= new BooleanPref(
			WikitextEditingSettings.EDITOR_OPTIONS_QUALIFIER, "ProblemChecking.enabled"); //$NON-NLS-1$
	
	
	public static final String ERROR_ANNOTATION_TYPE= "de.walware.docmlet.wikitext.ui.error"; //$NON-NLS-1$
	public static final String WARNING_ANNOTATION_TYPE= "de.walware.docmlet.wikitext.ui.warning"; //$NON-NLS-1$
	public static final String INFO_ANNOTATION_TYPE= "de.walware.docmlet.wikitext.ui.info"; //$NON-NLS-1$
	
}
