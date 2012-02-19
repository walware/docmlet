/*******************************************************************************
 * Copyright (c) 2009-2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.ui.sourceediting;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.quickassist.QuickAssistAssistant;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;

import de.walware.ecommons.ltk.ui.sourceediting.EcoReconciler2;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditorAddon;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditor1;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditorViewerConfiguration;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssist;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssistComputerRegistry;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssistProcessor;
import de.walware.ecommons.preferences.PreferencesUtil;
import de.walware.ecommons.text.ICharPairMatcher;
import de.walware.ecommons.text.IIndentSettings;
import de.walware.ecommons.text.ui.presentation.AbstractRuleBasedScanner;
import de.walware.ecommons.text.ui.presentation.SingleTokenScanner;
import de.walware.ecommons.ui.ColorManager;
import de.walware.ecommons.ui.util.DialogUtil;

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.core.TexCore;
import de.walware.docmlet.tex.core.text.ITexDocumentConstants;
import de.walware.docmlet.tex.core.text.LtxHeuristicTokenScanner;
import de.walware.docmlet.tex.internal.ui.TexUIPlugin;
import de.walware.docmlet.tex.internal.ui.editors.LtxContentAssistProcessor;
import de.walware.docmlet.tex.internal.ui.editors.LtxReconcilingStrategy;
import de.walware.docmlet.tex.ui.TexUIPreferences;
import de.walware.docmlet.tex.ui.editors.LtxQuickAssistProcessor;
import de.walware.docmlet.tex.ui.editors.TexEditorOptions;
import de.walware.docmlet.tex.ui.text.ITexTextStyles;
import de.walware.docmlet.tex.ui.text.LtxBracketPairMatcher;
import de.walware.docmlet.tex.ui.text.LtxDefaultTextStyleScanner;
import de.walware.docmlet.tex.ui.text.LtxDoubleClickStrategy;
import de.walware.docmlet.tex.ui.text.LtxMathTextStyleScanner;


/**
 * Configuration for TeX source editors.
 */
public class LtxViewerConfiguration extends SourceEditorViewerConfiguration {
	
	
	public static IPreferenceStore getTexPreferenceStore() {
		return TexUIPlugin.getDefault().getPreferenceStore();
	}
	
	
	protected LtxDefaultTextStyleScanner fDefaultScanner;
	protected LtxDefaultTextStyleScanner fMathScanner;
	protected AbstractRuleBasedScanner fCommentScanner;
	protected AbstractRuleBasedScanner fVerbatimScanner;
	
	protected LtxDoubleClickStrategy fDoubleClickStrategy;
	private LtxAutoEditStrategy fAutoEditStrategy;
	
	private ITexCoreAccess fTexCoreAccess;
	
	
	public LtxViewerConfiguration(
			final IPreferenceStore preferenceStore, final ColorManager colorManager) {
		this(null, null, preferenceStore, colorManager);
	}
	
	public LtxViewerConfiguration(final ISourceEditor editor, final ITexCoreAccess access,
			final IPreferenceStore preferenceStore, final ColorManager colorManager) {
		super(editor);
		setCoreAccess(access);
		
		setup((preferenceStore != null) ? preferenceStore : TexUIPlugin.getDefault().getEditorPreferenceStore(),
				colorManager,
				TexUIPreferences.EDITING_DECO_PREFERENCES,
				TexUIPreferences.EDITING_ASSIST_PREFERENCES);
		setScanners(createScanners());
	}
	
	protected void setCoreAccess(final ITexCoreAccess access) {
		fTexCoreAccess = (access != null) ? access : TexCore.getWorkbenchAccess();
	}
	
	protected ITokenScanner[] createScanners() {
		final IPreferenceStore preferenceStore = getPreferences();
		final ColorManager colorManager = getColorManager();
		
		fDefaultScanner = new LtxDefaultTextStyleScanner(colorManager, preferenceStore);
		fMathScanner = new LtxMathTextStyleScanner(colorManager, preferenceStore);
		fCommentScanner = new SingleTokenScanner(colorManager, preferenceStore,
				ITexTextStyles.GROUP_ID, ITexTextStyles.TS_COMMENT);
		fVerbatimScanner = new SingleTokenScanner(colorManager, preferenceStore,
				ITexTextStyles.GROUP_ID, ITexTextStyles.TS_VERBATIM);
		
		return new ITokenScanner[] {
				fDefaultScanner,
				fMathScanner,
				fCommentScanner,
				fVerbatimScanner,
		};
	}
	
	
	@Override
	public List<ISourceEditorAddon> getAddOns() {
		final List<ISourceEditorAddon> addons = super.getAddOns();
		if (fAutoEditStrategy != null) {
			addons.add(fAutoEditStrategy);
		}
		return addons;
	}
	
	@Override
	public void handleSettingsChanged(final Set<String> groupIds, final Map<String, Object> options) {
		super.handleSettingsChanged(groupIds, options);
		if (fAutoEditStrategy != null) {
			fAutoEditStrategy.fSettings.updateSettings();
		}
	}
	
	
	@Override
	public String getConfiguredDocumentPartitioning(final ISourceViewer sourceViewer) {
		return ITexDocumentConstants.LTX_PARTITIONING;
	}
	
	@Override
	public String[] getConfiguredContentTypes(final ISourceViewer sourceViewer) {
		return ITexDocumentConstants.LTX_PARTITION_TYPES;
	}
	
	
	@Override
	public ICharPairMatcher createPairMatcher() {
		return new LtxBracketPairMatcher();
	}
	
	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(final ISourceViewer sourceViewer, final String contentType) {
		if (fDoubleClickStrategy == null) {
			fDoubleClickStrategy = new LtxDoubleClickStrategy(new LtxHeuristicTokenScanner());
		}
		return fDoubleClickStrategy;
	}
	
	
	@Override
	public IPresentationReconciler getPresentationReconciler(final ISourceViewer sourceViewer) {
		final PresentationReconciler reconciler = new PresentationReconciler();
		reconciler.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		
		initDefaultPresentationReconciler(reconciler);
		
		return reconciler;
	}
	
	public void initDefaultPresentationReconciler(final PresentationReconciler reconciler) {
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(fDefaultScanner);
		reconciler.setDamager(dr, ITexDocumentConstants.LTX_DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, ITexDocumentConstants.LTX_DEFAULT_CONTENT_TYPE);
		reconciler.setDamager(dr, ITexDocumentConstants.LTX_DEFAULT_EXPL_CONTENT_TYPE);
		reconciler.setRepairer(dr, ITexDocumentConstants.LTX_DEFAULT_EXPL_CONTENT_TYPE);
		
		dr = new DefaultDamagerRepairer(fMathScanner);
		reconciler.setDamager(dr, ITexDocumentConstants.LTX_MATH_CONTENT_TYPE);
		reconciler.setRepairer(dr, ITexDocumentConstants.LTX_MATH_CONTENT_TYPE);
		
		dr = new DefaultDamagerRepairer(fCommentScanner);
		reconciler.setDamager(dr, ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE);
		reconciler.setRepairer(dr, ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE);
		
		dr = new DefaultDamagerRepairer(fCommentScanner);
		reconciler.setDamager(dr, ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE);
		reconciler.setRepairer(dr, ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE);
		
		dr = new DefaultDamagerRepairer(fVerbatimScanner);
		reconciler.setDamager(dr, ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE);
		reconciler.setRepairer(dr, ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE);
	}
	
	
	@Override
	protected IIndentSettings getIndentSettings() {
		return fTexCoreAccess.getTexCodeStyle();
	}
	
	@Override
	public String[] getDefaultPrefixes(final ISourceViewer sourceViewer, final String contentType) {
		return new String[] { "%", "" }; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	
	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(final ISourceViewer sourceViewer, final String contentType) {
		if (getSourceEditor() == null) {
			return super.getAutoEditStrategies(sourceViewer, contentType);
		}
		if (fAutoEditStrategy == null) {
			fAutoEditStrategy = createTexAutoEditStrategy();
		}
		return new IAutoEditStrategy[] { fAutoEditStrategy };
	}
	
	protected LtxAutoEditStrategy createTexAutoEditStrategy() {
		return new LtxAutoEditStrategy(fTexCoreAccess, getSourceEditor());
	}
	
	
	@Override
	public IReconciler getReconciler(final ISourceViewer sourceViewer) {
		final ISourceEditor editor = getSourceEditor();
		if (!(editor instanceof SourceEditor1)) {
			return null;
		}
		final EcoReconciler2 reconciler = new EcoReconciler2(editor);
		reconciler.setDelay(500);
		reconciler.addReconcilingStrategy(new LtxReconcilingStrategy());
		
//		final IReconcilingStrategy spellingStrategy = getSpellingStrategy(sourceViewer);
//		if (spellingStrategy != null) {
//			reconciler.addReconcilingStrategy(spellingStrategy);
//		}
		
		return reconciler;
	}
	
	
	@Override
	protected ContentAssistant createContentAssistant(final ISourceViewer sourceViewer) {
		if (getSourceEditor() != null) {
			final ContentAssist assistant = new ContentAssist();
			assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
			assistant.setRestoreCompletionProposalSize(DialogUtil.getDialogSettings(TexUIPlugin.getDefault(), "TexContentAssist.Proposal.size")); //$NON-NLS-1$
			
			initDefaultContentAssist(assistant);
			return assistant;
		}
		return null;
	}
	
	public void initDefaultContentAssist(final ContentAssist assistant) {
		final ContentAssistComputerRegistry registry = TexUIPlugin.getDefault().getTexEditorContentAssistRegistry();
		
		{	final ContentAssistProcessor processor = new LtxContentAssistProcessor(assistant,
					ITexDocumentConstants.LTX_DEFAULT_EXPL_CONTENT_TYPE, registry, getSourceEditor());
			processor.setCompletionProposalAutoActivationCharacters(new char[] { '\\' });
			assistant.setContentAssistProcessor(processor, ITexDocumentConstants.LTX_DEFAULT_EXPL_CONTENT_TYPE);
			assistant.setContentAssistProcessor(processor, ITexDocumentConstants.LTX_DEFAULT_CONTENT_TYPE);
		}
		{	final ContentAssistProcessor processor = new LtxContentAssistProcessor(assistant,
					ITexDocumentConstants.LTX_MATH_CONTENT_TYPE, registry, getSourceEditor());
			processor.setCompletionProposalAutoActivationCharacters(new char[] { '\\' });
			assistant.setContentAssistProcessor(processor, ITexDocumentConstants.LTX_MATH_CONTENT_TYPE);
		}
		{	final ContentAssistProcessor processor = new LtxContentAssistProcessor(assistant,
					ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE, registry, getSourceEditor());
			assistant.setContentAssistProcessor(processor, ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE);
		}
		{	final ContentAssistProcessor processor = new LtxContentAssistProcessor(assistant,
					ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE, registry, getSourceEditor());
			assistant.setContentAssistProcessor(processor, ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE);
		}
		{	final ContentAssistProcessor processor = new LtxContentAssistProcessor(assistant,
					ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE, registry, getSourceEditor());
			assistant.setContentAssistProcessor(processor, ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE);
		}
	}
	
	@Override
	protected IQuickAssistAssistant createQuickAssistant(final ISourceViewer sourceViewer) {
		final QuickAssistAssistant assistant = new QuickAssistAssistant();
		assistant.setQuickAssistProcessor(new LtxQuickAssistProcessor(getSourceEditor()));
		assistant.enableColoredLabels(true);
		return assistant;
	}
	
	
	@Override
	protected Map getHyperlinkDetectorTargets(final ISourceViewer sourceViewer) {
		final Map<String, Object> targets = super.getHyperlinkDetectorTargets(sourceViewer);
		targets.put("de.walware.docmlet.tex.editorHyperlinks.TexEditorTarget", getSourceEditor()); //$NON-NLS-1$
		return targets;
	}
	
	@Override
	public boolean isSmartInsertSupported() {
		return true;
	}
	
	@Override
	public boolean isSmartInsertByDefault() {
		return PreferencesUtil.getInstancePrefs().getPreferenceValue(
				TexEditorOptions.SMARTINSERT_BYDEFAULT_ENABLED_PREF );
	}
	
	
}
