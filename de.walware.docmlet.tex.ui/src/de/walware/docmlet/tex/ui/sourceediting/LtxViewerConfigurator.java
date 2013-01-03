/*******************************************************************************
 * Copyright (c) 2007-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.ui.sourceediting;

import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;

import de.walware.ecommons.ltk.ui.sourceediting.SourceEditorViewerConfigurator;
import de.walware.ecommons.preferences.IPreferenceAccess;
import de.walware.ecommons.text.PartitioningConfiguration;

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.core.TexCodeStyleSettings;
import de.walware.docmlet.tex.core.TexCore;
import de.walware.docmlet.tex.core.commands.TexCommandSet;
import de.walware.docmlet.tex.core.text.ITexDocumentConstants;
import de.walware.docmlet.tex.ui.editors.TexEditorOptions;
import de.walware.docmlet.tex.ui.text.LtxDocumentSetupParticipant;


/**
 * Configurator for LaTeX code source viewers.
 */
public class LtxViewerConfigurator extends SourceEditorViewerConfigurator
		implements ITexCoreAccess, PropertyChangeListener {
	
	
	private static final Set<String> RESET_GROUP_IDS = new HashSet<String>(Arrays.asList(new String[] {
			TexCodeStyleSettings.INDENT_GROUP_ID,
//			TaskTagsPreferences.GROUP_ID,
	}));
	
	
	private ITexCoreAccess fSourceCoreAccess;
	
	private final TexCodeStyleSettings fTexCodeStyleCopy;
	
	
	public LtxViewerConfigurator(final ITexCoreAccess coreAccess,
			final LtxViewerConfiguration config) {
		super(config);
		fTexCodeStyleCopy = new TexCodeStyleSettings(1);
		config.setCoreAccess(this);
		setSource(coreAccess);
		
		fTexCodeStyleCopy.load(fSourceCoreAccess.getTexCodeStyle());
		fTexCodeStyleCopy.resetDirty();
		fTexCodeStyleCopy.addPropertyChangeListener(this);
	}
	
	
	@Override
	public PartitioningConfiguration getPartitioning() {
		return ITexDocumentConstants.LTX_PARTITIONING_CONFIG;
	}
	
	@Override
	public IDocumentSetupParticipant getDocumentSetupParticipant() {
		return new LtxDocumentSetupParticipant();
	}
	
	@Override
	protected Set<String> getResetGroupIds() {
		return RESET_GROUP_IDS;
	}
	
	
	public void setSource(ITexCoreAccess newAccess) {
		if (newAccess == null) {
			newAccess = TexCore.getWorkbenchAccess();
		}
		if (fSourceCoreAccess != newAccess) {
			fSourceCoreAccess = newAccess;
			handleSettingsChanged(null, null);
		}
	}
	
	
	@Override
	public void handleSettingsChanged(final Set<String> groupIds, final Map<String, Object> options) {
		super.handleSettingsChanged(groupIds, options);
		
		fTexCodeStyleCopy.resetDirty();
	}
	
	@Override
	protected void checkSettingsChanges(final Set<String> groupIds, final Map<String, Object> options) {
		super.checkSettingsChanges(groupIds, options);
		
		if (groupIds.contains(TexCodeStyleSettings.INDENT_GROUP_ID)) {
			fTexCodeStyleCopy.load(fSourceCoreAccess.getTexCodeStyle());
		}
		if (groupIds.contains(TexEditorOptions.GROUP_ID)) {
			fUpdateCompleteConfig = true;
		}
	}
	
	
	@Override
	public IPreferenceAccess getPrefs() {
		return fSourceCoreAccess.getPrefs();
	}
	
	@Override
	public TexCommandSet getTexCommandSet() {
		return fSourceCoreAccess.getTexCommandSet();
	}
	
	@Override
	public TexCodeStyleSettings getTexCodeStyle() {
		return fTexCodeStyleCopy;
	}
	
}
