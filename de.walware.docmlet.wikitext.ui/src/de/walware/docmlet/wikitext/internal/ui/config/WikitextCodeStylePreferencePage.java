/*=============================================================================#
 # Copyright (c) 2007-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.ui.config;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import de.walware.ecommons.preferences.ui.ConfigurationBlock;
import de.walware.ecommons.preferences.ui.ManagedConfigurationBlock;
import de.walware.ecommons.preferences.ui.PropertyAndPreferencePage;


/**
 * A Property- and PreferencePage for RCodeStyle settings.
 */
public class WikitextCodeStylePreferencePage extends PropertyAndPreferencePage {
	
	
	public static final String PREFERENCE_PAGE_ID= "de.walware.docmlet.wikitext.preferencePages.WikitextCodeStyle"; //$NON-NLS-1$
	
	
	public WikitextCodeStylePreferencePage() {
	}
	
	
	@Override
	protected String getPreferencePageID() {
		return PREFERENCE_PAGE_ID;
	}
	
	@Override
	protected String getPropertyPageID() {
		return null;
	}
	
//	@Override
//	protected boolean isProjectSupported(final IProject project) throws CoreException {
//		return project.hasNature(WikitextProject.NATURE_ID);
//	}
	
	
	@Override
	protected ConfigurationBlock createConfigurationBlock() throws CoreException {
		return new WikitextCodeStylePreferenceBlock(getProject(), createStatusChangedListener());
	}
	
	@Override
	protected boolean hasProjectSpecificSettings(final IProject project) {
		return ((ManagedConfigurationBlock) getBlock()).hasProjectSpecificOptions(project);
	}
	
}
