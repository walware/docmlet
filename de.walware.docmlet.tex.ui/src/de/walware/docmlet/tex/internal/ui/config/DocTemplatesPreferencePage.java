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

package de.walware.docmlet.tex.internal.ui.config;

import org.eclipse.core.runtime.CoreException;

import de.walware.ecommons.ltk.ui.templates.config.CodeTemplateConfigurationBlock;
import de.walware.ecommons.ltk.ui.templates.config.CodeTemplateConfigurationRegistry;
import de.walware.ecommons.preferences.ui.ConfigurationBlock;
import de.walware.ecommons.preferences.ui.ConfigurationBlockPreferencePage;


public class DocTemplatesPreferencePage extends ConfigurationBlockPreferencePage {
	
	
	private final static String EXTENSION_POINT_ID= "de.walware.docmlet.tex.docTemplates"; //$NON-NLS-1$
	
	
	public DocTemplatesPreferencePage() {
	}
	
	
	@Override
	protected ConfigurationBlock createConfigurationBlock() throws CoreException {
		final CodeTemplateConfigurationRegistry registry= new CodeTemplateConfigurationRegistry(EXTENSION_POINT_ID);
		
		return new CodeTemplateConfigurationBlock("Sweave Document Templates", //Messages.DocTemplates_title
				CodeTemplateConfigurationBlock.ADD_ITEM
						| CodeTemplateConfigurationBlock.LAZY_LOADING
						| CodeTemplateConfigurationBlock.DEFAULT_BY_CATEGORY,
				registry.getCategories(),
				null );
	}
	
}
