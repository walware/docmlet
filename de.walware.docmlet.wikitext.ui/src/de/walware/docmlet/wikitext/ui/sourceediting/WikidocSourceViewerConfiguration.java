/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.ui.sourceediting;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.mylyn.internal.wikitext.ui.util.WikiTextUiResources;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.IThemeManager;

import de.walware.ecommons.ltk.ui.LTKUIPreferences;
import de.walware.ecommons.ltk.ui.sourceediting.EcoReconciler2;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditorAddon;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditor1;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditorViewer;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditorViewerConfiguration;
import de.walware.ecommons.ltk.ui.sourceediting.SourceUnitReconcilingStrategy;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssist;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssistComputerRegistry;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssistProcessor;
import de.walware.ecommons.preferences.PreferencesUtil;
import de.walware.ecommons.text.IIndentSettings;
import de.walware.ecommons.text.core.sections.IDocContentSections;
import de.walware.ecommons.text.ui.presentation.ITextPresentationConstants;

import de.walware.docmlet.wikitext.core.IWikitextCoreAccess;
import de.walware.docmlet.wikitext.core.WikitextCore;
import de.walware.docmlet.wikitext.core.source.IWikitextDocumentConstants;
import de.walware.docmlet.wikitext.internal.ui.WikitextUIPlugin;
import de.walware.docmlet.wikitext.internal.ui.sourceediting.DocQuickOutlineInformationProvider;
import de.walware.docmlet.wikitext.internal.ui.sourceediting.MarkupDamagerRepairer;
import de.walware.docmlet.wikitext.internal.ui.sourceediting.MarkupDoubleClickStrategy;
import de.walware.docmlet.wikitext.internal.ui.sourceediting.WikitextContentAssistProcessor;


/**
 * Configuration for Wikitext document source editors.
 */
public class WikidocSourceViewerConfiguration extends SourceEditorViewerConfiguration {
	
	
	public static final int FIXED_LINE_HEIGHT_STYLE= 0x0000_1000;
	
	
	private static final String[] CONTENT_TYPES= IWikitextDocumentConstants.WIKIDOC_CONTENT_TYPES.toArray(
			new String[IWikitextDocumentConstants.WIKIDOC_CONTENT_TYPES.size()] );
	
	
	protected ITextDoubleClickStrategy doubleClickStrategy;
	
//	private WikidocAutoEditStrategy autoEditStrategy;
	
	private IWikitextCoreAccess coreAccess;
	
	private final int styleFlags;
	
	
	public WikidocSourceViewerConfiguration(final IDocContentSections documentContentInfo) {
		this(documentContentInfo, null, null, null, FIXED_LINE_HEIGHT_STYLE);
	}
	
	public WikidocSourceViewerConfiguration(final IDocContentSections documentContentInfo,
			final ISourceEditor editor,
			final IWikitextCoreAccess access,
			final IPreferenceStore preferenceStore,
			final int styleFlags) {
		super(documentContentInfo, editor);
		setCoreAccess(access);
		
		setup((preferenceStore != null) ? preferenceStore : WikitextEditingSettings.getPreferenceStore(),
				LTKUIPreferences.getEditorDecorationPreferences(),
				WikitextEditingSettings.getAssistPrefences() );
		this.styleFlags= styleFlags;
	}
	
	protected void setCoreAccess(final IWikitextCoreAccess access) {
		this.coreAccess= (access != null) ? access : WikitextCore.getWorkbenchAccess();
	}
	
	protected StyleConfig createStyleConfig() {
		final IThemeManager themeManager= PlatformUI.getWorkbench().getThemeManager();
		final FontRegistry fontRegistry= themeManager.getCurrentTheme().getFontRegistry();
		return new StyleConfig(
				fontRegistry.get(WikiTextUiResources.PREFERENCE_TEXT_FONT),
				fontRegistry.get(WikiTextUiResources.PREFERENCE_MONOSPACE_FONT),
				((this.styleFlags & FIXED_LINE_HEIGHT_STYLE) != 0) );
	}
	
	
	@Override
	protected void initScanners() {
		addScanner(IWikitextDocumentConstants.WIKIDOC_DEFAULT_CONTENT_TYPE,
				new MarkupTokenScanner(getConfiguredDocumentPartitioning(null),
						createStyleConfig() ));
	}
	
	@Override
	protected ITokenScanner getScanner(String contentType) {
		if (contentType == IWikitextDocumentConstants.WIKIDOC_DEFAULT_CONTENT_TYPE) {
			contentType= IWikitextDocumentConstants.WIKIDOC_DEFAULT_CONTENT_TYPE;
		}
		return super.getScanner(contentType);
	}
	
	
	@Override
	protected void initPresentationReconciler(final PresentationReconciler reconciler) {
		{	final DefaultDamagerRepairer dr= new MarkupDamagerRepairer(
					getScanner(IWikitextDocumentConstants.WIKIDOC_DEFAULT_CONTENT_TYPE) );
			reconciler.setDamager(dr, IWikitextDocumentConstants.WIKIDOC_DEFAULT_CONTENT_TYPE);
			reconciler.setRepairer(dr, IWikitextDocumentConstants.WIKIDOC_DEFAULT_CONTENT_TYPE);
		}
	}
	
	
	@Override
	public List<ISourceEditorAddon> getAddOns() {
		final List<ISourceEditorAddon> addons= super.getAddOns();
//		if (this.autoEditStrategy != null) {
//			addons.add(this.autoEditStrategy);
//		}
		return addons;
	}
	
	@Override
	public void handleSettingsChanged(final Set<String> groupIds, final Map<String, Object> options) {
		super.handleSettingsChanged(groupIds, options);
//		if (this.autoEditStrategy != null) {
//			this.autoEditStrategy.fSettings.updateSettings();
//		}
		if (groupIds.contains(WikitextEditingSettings.TEXTSTYLE_CONFIG_QUALIFIER)) {
			final MarkupTokenScanner scanner= (MarkupTokenScanner) getScanner(
					IWikitextDocumentConstants.WIKIDOC_DEFAULT_CONTENT_TYPE );
			scanner.setStyleConfig(createStyleConfig());
			options.put(ITextPresentationConstants.SETTINGSCHANGE_AFFECTSPRESENTATION_KEY, Boolean.TRUE);
		}
	}
	
	
	@Override
	public String[] getConfiguredContentTypes(final ISourceViewer sourceViewer) {
		return CONTENT_TYPES;
	}
	
	
//	@Override
//	public ICharPairMatcher createPairMatcher() {
//		return new WikitextBracketPairMatcher();
//	}
	
	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(final ISourceViewer sourceViewer, final String contentType) {
		if (this.doubleClickStrategy == null) {
			this.doubleClickStrategy= new MarkupDoubleClickStrategy(
					getConfiguredDocumentPartitioning(sourceViewer) );
		}
		return this.doubleClickStrategy;
	}
	
	
	@Override
	protected IIndentSettings getIndentSettings() {
		return this.coreAccess.getWikitextCodeStyle();
	}
	
	@Override
	public String[] getDefaultPrefixes(final ISourceViewer sourceViewer, final String contentType) {
		return new String[] { "<!--", "" }; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	
	@Override
	public boolean isSmartInsertSupported() {
		return true;
	}
	
	@Override
	public boolean isSmartInsertByDefault() {
		return PreferencesUtil.getInstancePrefs().getPreferenceValue(
				WikitextEditingSettings.SMARTINSERT_BYDEFAULT_ENABLED_PREF );
	}
	
	
	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(final ISourceViewer sourceViewer, final String contentType) {
//		if (getSourceEditor() == null) {
			return super.getAutoEditStrategies(sourceViewer, contentType);
//		}
//		if (this.autoEditStrategy == null) {
//			this.autoEditStrategy= createAutoEditStrategy();
//		}
//		return new IAutoEditStrategy[] { this.autoEditStrategy };
	}
	
//	protected WikidocAutoEditStrategy createAutoEditStrategy() {
//		return new WikidocAutoEditStrategy(this.coreAccess, getSourceEditor());
//	}
	
	
	@Override
	public IReconciler getReconciler(final ISourceViewer sourceViewer) {
		final ISourceEditor editor= getSourceEditor();
		if (!(editor instanceof SourceEditor1)) {
			return null;
		}
		final EcoReconciler2 reconciler= new EcoReconciler2(editor);
		reconciler.setDelay(500);
		reconciler.addReconcilingStrategy(new SourceUnitReconcilingStrategy());
		
//		final IReconcilingStrategy spellingStrategy= getSpellingStrategy(sourceViewer);
//		if (spellingStrategy != null) {
//			reconciler.addReconcilingStrategy(spellingStrategy);
//		}
		
		return reconciler;
	}
	
	
	@Override
	public void initContentAssist(final ContentAssist assistant) {
		final ContentAssistComputerRegistry registry= WikitextUIPlugin.getInstance().getWikidocEditorContentAssistRegistry();
		
		{	final ContentAssistProcessor processor= new WikitextContentAssistProcessor(assistant,
					IWikitextDocumentConstants.WIKIDOC_DEFAULT_CONTENT_TYPE, registry,
					getSourceEditor() );
			assistant.setContentAssistProcessor(processor, IWikitextDocumentConstants.WIKIDOC_DEFAULT_CONTENT_TYPE);
		}
	}
	
	@Override
	protected IQuickAssistProcessor createQuickAssistProcessor() {
		return new WikidocQuickAssistProcessor(getSourceEditor());
	}
	
	
	@Override
	protected void collectHyperlinkDetectorTargets(final Map<String, IAdaptable> targets,
			final ISourceViewer sourceViewer) {
		targets.put("de.walware.docmlet.wikitext.editorHyperlinks.WikitextEditorTarget", getSourceEditor()); //$NON-NLS-1$
	}
	
	
	@Override
	protected IInformationProvider getQuickInformationProvider(final ISourceViewer sourceViewer,
			final int operation) {
		final ISourceEditor editor= getSourceEditor();
		if (editor == null) {
			return null;
		}
		switch (operation) {
		case SourceEditorViewer.SHOW_SOURCE_OUTLINE:
			return new DocQuickOutlineInformationProvider(editor, operation);
		default:
			return null;
		}
	}
	
}
