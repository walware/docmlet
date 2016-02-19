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

package de.walware.docmlet.wikitext.ui.sourceediting;

import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;

import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditorViewerConfigurator;
import de.walware.ecommons.preferences.core.IPreferenceAccess;

import de.walware.docmlet.wikitext.core.IWikitextCoreAccess;
import de.walware.docmlet.wikitext.core.WikitextCodeStyleSettings;
import de.walware.docmlet.wikitext.core.WikitextCore;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.source.WikidocDocumentSetupParticipant;


/**
 * Configurator for Wikitext document code source viewers.
 */
public class WikidocSourceViewerConfigurator extends SourceEditorViewerConfigurator
		implements IWikitextCoreAccess, PropertyChangeListener {
	
	
	private static final Set<String> RESET_GROUP_IDS= new HashSet<>(Arrays.asList(new String[] {
			WikitextCodeStyleSettings.INDENT_GROUP_ID,
//			TaskTagsPreferences.GROUP_ID,
	}));
	
	
	private final IMarkupLanguage markupLanguage;
	
	private IWikitextCoreAccess sourceCoreAccess;
	
	private final WikitextCodeStyleSettings wikitextCodeStyleCopy;
	
	
	public WikidocSourceViewerConfigurator(final IMarkupLanguage markupLanguage,
			final IWikitextCoreAccess coreAccess,
			final WikidocSourceViewerConfiguration config) {
		super(config);
		if (markupLanguage == null) {
			throw new NullPointerException("markupLanguage"); //$NON-NLS-1$
		}
		this.markupLanguage= markupLanguage;
		
		this.wikitextCodeStyleCopy= new WikitextCodeStyleSettings(1);
		config.setCoreAccess(this);
		setSource(coreAccess);
		
		this.wikitextCodeStyleCopy.load(this.sourceCoreAccess.getWikitextCodeStyle());
		this.wikitextCodeStyleCopy.resetDirty();
		this.wikitextCodeStyleCopy.addPropertyChangeListener(this);
	}
	
	
	@Override
	public IDocumentSetupParticipant getDocumentSetupParticipant() {
		return new WikidocDocumentSetupParticipant(this.markupLanguage);
	}
	
	@Override
	protected Set<String> getResetGroupIds() {
		return RESET_GROUP_IDS;
	}
	
	
	public void setSource(IWikitextCoreAccess newAccess) {
		if (newAccess == null) {
			newAccess= WikitextCore.getWorkbenchAccess();
		}
		if (this.sourceCoreAccess != newAccess) {
			this.sourceCoreAccess= newAccess;
			handleSettingsChanged(null, null);
		}
	}
	
	
	@Override
	public void setTarget(final ISourceEditor sourceEditor) {
		super.setTarget(sourceEditor);
		
		final SourceViewer viewer= sourceEditor.getViewer();
		viewer.getTextWidget().setData(ISourceViewer.class.getName(), viewer);
		viewer.getTextWidget().setData(MarkupLanguage.class.getName(), this.markupLanguage);
	}
	
	@Override
	public void handleSettingsChanged(final Set<String> groupIds, final Map<String, Object> options) {
		super.handleSettingsChanged(groupIds, options);
		
		this.wikitextCodeStyleCopy.resetDirty();
	}
	
	@Override
	protected void checkSettingsChanges(final Set<String> groupIds, final Map<String, Object> options) {
		super.checkSettingsChanges(groupIds, options);
		
		if (groupIds.contains(WikitextCodeStyleSettings.INDENT_GROUP_ID)) {
			this.wikitextCodeStyleCopy.load(this.sourceCoreAccess.getWikitextCodeStyle());
		}
		if (groupIds.contains(WikitextEditingSettings.EDITOR_OPTIONS_QUALIFIER)) {
			this.fUpdateCompleteConfig= true;
		}
	}
	
	
	@Override
	public IPreferenceAccess getPrefs() {
		return this.sourceCoreAccess.getPrefs();
	}
	
	@Override
	public WikitextCodeStyleSettings getWikitextCodeStyle() {
		return this.wikitextCodeStyleCopy;
	}
	
}
