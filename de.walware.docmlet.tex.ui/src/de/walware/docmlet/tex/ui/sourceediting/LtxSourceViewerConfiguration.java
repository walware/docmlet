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

package de.walware.docmlet.tex.ui.sourceediting;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.ISourceViewer;

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
import de.walware.ecommons.text.ICharPairMatcher;
import de.walware.ecommons.text.IIndentSettings;
import de.walware.ecommons.text.core.sections.IDocContentSections;
import de.walware.ecommons.text.ui.presentation.SingleTokenScanner;
import de.walware.ecommons.text.ui.settings.TextStyleManager;

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.core.TexCore;
import de.walware.docmlet.tex.core.source.ITexDocumentConstants;
import de.walware.docmlet.tex.core.source.LtxBracketPairMatcher;
import de.walware.docmlet.tex.core.source.LtxDocumentContentInfo;
import de.walware.docmlet.tex.core.source.LtxHeuristicTokenScanner;
import de.walware.docmlet.tex.internal.ui.TexUIPlugin;
import de.walware.docmlet.tex.internal.ui.sourceediting.LtxAutoEditStrategy;
import de.walware.docmlet.tex.internal.ui.sourceediting.LtxContentAssistProcessor;
import de.walware.docmlet.tex.internal.ui.sourceediting.LtxQuickOutlineInformationProvider;
import de.walware.docmlet.tex.ui.text.ITexTextStyles;
import de.walware.docmlet.tex.ui.text.LtxDefaultTextStyleScanner;
import de.walware.docmlet.tex.ui.text.LtxDoubleClickStrategy;
import de.walware.docmlet.tex.ui.text.LtxMathTextStyleScanner;


/**
 * Configuration for TeX source editors.
 */
public class LtxSourceViewerConfiguration extends SourceEditorViewerConfiguration {
	
	
	private static final String[] CONTENT_TYPES= ITexDocumentConstants.LTX_CONTENT_TYPES.toArray(
			new String[ITexDocumentConstants.LTX_CONTENT_TYPES.size()] );
	
	
	protected ITextDoubleClickStrategy doubleClickStrategy;
	
	private LtxAutoEditStrategy autoEditStrategy;
	
	private ITexCoreAccess coreAccess;
	
	
	public LtxSourceViewerConfiguration() {
		this(LtxDocumentContentInfo.INSTANCE, null, null, null, null);
	}
	
	public LtxSourceViewerConfiguration(final IDocContentSections documentContentInfo,
			final ISourceEditor editor,
			final ITexCoreAccess access,
			final IPreferenceStore preferenceStore, final TextStyleManager textStyles) {
		super(documentContentInfo, editor);
		setCoreAccess(access);
		
		setup((preferenceStore != null) ? preferenceStore : TexUIPlugin.getInstance().getEditorPreferenceStore(),
				LTKUIPreferences.getEditorDecorationPreferences(),
				TexEditingSettings.getAssistPrefences() );
		setTextStyles(textStyles);
	}
	
	protected void setCoreAccess(final ITexCoreAccess access) {
		this.coreAccess= (access != null) ? access : TexCore.getWorkbenchAccess();
	}
	
	
	@Override
	protected void initTextStyles() {
		setTextStyles(TexUIPlugin.getInstance().getLtxTextStyles());
	}
	
	@Override
	protected void initScanners() {
		final TextStyleManager textStyles= getTextStyles();
		
		addScanner(ITexDocumentConstants.LTX_DEFAULT_CONTENT_TYPE,
				new LtxDefaultTextStyleScanner(textStyles) );
		addScanner(ITexDocumentConstants.LTX_MATH_CONTENT_TYPE,
				new LtxMathTextStyleScanner(textStyles) );
		addScanner(ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE,
				new SingleTokenScanner(textStyles, ITexTextStyles.TS_COMMENT) );
		addScanner(ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE,
				new SingleTokenScanner(textStyles, ITexTextStyles.TS_COMMENT) );
		addScanner(ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE,
				new SingleTokenScanner(textStyles, ITexTextStyles.TS_VERBATIM) );
	}
	
	
	@Override
	public List<ISourceEditorAddon> getAddOns() {
		final List<ISourceEditorAddon> addons= super.getAddOns();
		if (this.autoEditStrategy != null) {
			addons.add(this.autoEditStrategy);
		}
		return addons;
	}
	
	@Override
	public void handleSettingsChanged(final Set<String> groupIds, final Map<String, Object> options) {
		super.handleSettingsChanged(groupIds, options);
		if (this.autoEditStrategy != null) {
			this.autoEditStrategy.getSettings().handleSettingsChanged(groupIds, options);
		}
	}
	
	
	@Override
	public String[] getConfiguredContentTypes(final ISourceViewer sourceViewer) {
		return CONTENT_TYPES;
	}
	
	
	@Override
	public ICharPairMatcher createPairMatcher() {
		return new LtxBracketPairMatcher(
				LtxHeuristicTokenScanner.create(getDocumentContentInfo()) );
	}
	
	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(final ISourceViewer sourceViewer, final String contentType) {
		if (this.doubleClickStrategy == null) {
			this.doubleClickStrategy= new LtxDoubleClickStrategy(
					LtxHeuristicTokenScanner.create(getDocumentContentInfo()) );
		}
		return this.doubleClickStrategy;
	}
	
	
	@Override
	protected IIndentSettings getIndentSettings() {
		return this.coreAccess.getTexCodeStyle();
	}
	
	@Override
	public String[] getDefaultPrefixes(final ISourceViewer sourceViewer, final String contentType) {
		return new String[] { "%", "" }; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	
	@Override
	public boolean isSmartInsertSupported() {
		return true;
	}
	
	@Override
	public boolean isSmartInsertByDefault() {
		return PreferencesUtil.getInstancePrefs().getPreferenceValue(
				TexEditingSettings.SMARTINSERT_BYDEFAULT_ENABLED_PREF );
	}
	
	
	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(final ISourceViewer sourceViewer, final String contentType) {
		if (getSourceEditor() == null) {
			return super.getAutoEditStrategies(sourceViewer, contentType);
		}
		if (this.autoEditStrategy == null) {
			this.autoEditStrategy= createTexAutoEditStrategy();
		}
		return new IAutoEditStrategy[] { this.autoEditStrategy };
	}
	
	protected LtxAutoEditStrategy createTexAutoEditStrategy() {
		return new LtxAutoEditStrategy(this.coreAccess, getSourceEditor());
	}
	
	
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
		final ContentAssistComputerRegistry registry= TexUIPlugin.getInstance().getTexEditorContentAssistRegistry();
		
		{	final ContentAssistProcessor processor= new LtxContentAssistProcessor(assistant,
					ITexDocumentConstants.LTX_DEFAULT_CONTENT_TYPE, registry, getSourceEditor() );
			processor.setCompletionProposalAutoActivationCharacters(new char[] { '\\' });
			assistant.setContentAssistProcessor(processor, ITexDocumentConstants.LTX_DEFAULT_CONTENT_TYPE);
		}
		{	final ContentAssistProcessor processor= new LtxContentAssistProcessor(assistant,
					ITexDocumentConstants.LTX_MATH_CONTENT_TYPE, registry, getSourceEditor() );
			processor.setCompletionProposalAutoActivationCharacters(new char[] { '\\' });
			assistant.setContentAssistProcessor(processor, ITexDocumentConstants.LTX_MATH_CONTENT_TYPE);
		}
		{	final ContentAssistProcessor processor= new LtxContentAssistProcessor(assistant,
					ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE, registry, getSourceEditor() );
			assistant.setContentAssistProcessor(processor, ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE);
		}
		{	final ContentAssistProcessor processor= new LtxContentAssistProcessor(assistant,
					ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE, registry, getSourceEditor() );
			assistant.setContentAssistProcessor(processor, ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE);
		}
		{	final ContentAssistProcessor processor= new LtxContentAssistProcessor(assistant,
					ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE, registry, getSourceEditor() );
			assistant.setContentAssistProcessor(processor, ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE);
		}
	}
	
	@Override
	protected IQuickAssistProcessor createQuickAssistProcessor() {
		final ISourceEditor editor= getSourceEditor();
		if (editor != null) {
			return new LtxQuickAssistProcessor(editor);
		}
		return null;
	}
	
	
	@Override
	protected void collectHyperlinkDetectorTargets(final Map<String, IAdaptable> targets,
			final ISourceViewer sourceViewer) {
		targets.put("de.walware.docmlet.tex.editorHyperlinks.TexEditorTarget", getSourceEditor()); //$NON-NLS-1$
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
			return new LtxQuickOutlineInformationProvider(editor, operation);
		default:
			return null;
		}
	}
	
}
