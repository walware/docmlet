/*******************************************************************************
 * Copyright (c) 2009-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.internal.ui.preferences;

import org.eclipse.core.runtime.CoreException;

import de.walware.ecommons.ltk.ui.sourceediting.assist.AdvancedContentAssistConfigurationBlock;
import de.walware.ecommons.preferences.ui.ConfigurationBlockPreferencePage;

import de.walware.docmlet.tex.internal.ui.TexUIPlugin;


public class LtxAdvancedContentAssistConfigurationPage extends ConfigurationBlockPreferencePage<AdvancedContentAssistConfigurationBlock> {
	
	
	public LtxAdvancedContentAssistConfigurationPage() {
	}
	
	
	@Override
	protected AdvancedContentAssistConfigurationBlock createConfigurationBlock() throws CoreException {
		return new AdvancedContentAssistConfigurationBlock(
				TexUIPlugin.getDefault().getTexEditorContentAssistRegistry(),
				createStatusChangedListener());
	}
	
}
