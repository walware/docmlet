/*******************************************************************************
 * Copyright (c) 2011-2012 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.ui.editors;

import de.walware.ecommons.preferences.Preference.BooleanPref;


public class LtxEditorBuild {
	
	
	public static final String GROUP_ID = "tex/tex.editor/build.options"; //$NON-NLS-1$
	
	
	public static final BooleanPref PROBLEMCHECKING_ENABLED_PREF = new BooleanPref(
			TexEditorOptions.TEXEDITOR_NODE, "ProblemChecking.enabled"); //$NON-NLS-1$
	
	
	public static final String ERROR_ANNOTATION_TYPE = "de.walware.docmlet.tex.ui.error"; //$NON-NLS-1$
	public static final String WARNING_ANNOTATION_TYPE = "de.walware.docmlet.tex.ui.warning"; //$NON-NLS-1$
	public static final String INFO_ANNOTATION_TYPE = "de.walware.docmlet.tex.ui.info"; //$NON-NLS-1$
	
}
