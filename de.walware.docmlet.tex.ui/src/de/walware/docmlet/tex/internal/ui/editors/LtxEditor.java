/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.ui.editors;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.ltk.ast.AstSelection;
import de.walware.ecommons.ltk.core.model.ISourceUnitModelInfo;
import de.walware.ecommons.ltk.ui.sourceediting.AbstractMarkOccurrencesProvider;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditorAddon;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditor1;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditor1OutlinePage;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditorViewerConfigurator;
import de.walware.ecommons.ltk.ui.sourceediting.folding.FoldingEditorAddon;

import de.walware.docmlet.tex.core.TexCore;
import de.walware.docmlet.tex.core.model.ITexSourceUnit;
import de.walware.docmlet.tex.core.model.TexModel;
import de.walware.docmlet.tex.core.source.ITexDocumentConstants;
import de.walware.docmlet.tex.core.source.LtxDocumentContentInfo;
import de.walware.docmlet.tex.internal.ui.TexUIPlugin;
import de.walware.docmlet.tex.ui.TexUI;
import de.walware.docmlet.tex.ui.editors.ILtxEditor;
import de.walware.docmlet.tex.ui.editors.LtxDefaultFoldingProvider;
import de.walware.docmlet.tex.ui.editors.TexMarkOccurrencesLocator;
import de.walware.docmlet.tex.ui.sourceediting.LtxSourceViewerConfiguration;
import de.walware.docmlet.tex.ui.sourceediting.LtxSourceViewerConfigurator;
import de.walware.docmlet.tex.ui.sourceediting.TexEditingSettings;


public class LtxEditor extends SourceEditor1 implements ILtxEditor {
	
	
	private static final ImList<String> KEY_CONTEXTS= ImCollections.newIdentityList(
			TexUI.EDITOR_CONTEXT_ID );
	
	private static final ImList<String> CONTEXT_IDS= ImCollections.concatList(
			ACTION_SET_CONTEXT_IDS, KEY_CONTEXTS );
	
	
	private static class ThisMarkOccurrencesProvider extends AbstractMarkOccurrencesProvider {
		
		
		private final TexMarkOccurrencesLocator fLocator = new TexMarkOccurrencesLocator();
		
		
		public ThisMarkOccurrencesProvider(final SourceEditor1 editor) {
			super(editor, ITexDocumentConstants.LTX_DEFAULT_OR_MATH_CONTENT_CONSTRAINT);
		}
		
		@Override
		protected void doUpdate(final RunData run, final ISourceUnitModelInfo info,
				final AstSelection astSelection, final ITextSelection orgSelection)
				throws BadLocationException, BadPartitioningException, UnsupportedOperationException {
			fLocator.run(run, info, astSelection, orgSelection);
		}
		
	}
	
	
	private LtxSourceViewerConfigurator fTexConfig;
	
	
	public LtxEditor() {
		super(TexCore.LTX_CONTENT_TYPE);
	}
	
	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		
		setEditorContextMenuId("de.walware.docmlet.tex.menus.LtxEditorContextMenu"); //$NON-NLS-1$
	}
	
	@Override
	protected SourceEditorViewerConfigurator createConfiguration() {
		setDocumentProvider(TexUIPlugin.getInstance().getTexDocumentProvider());
		
		enableStructuralFeatures(TexModel.getLtxModelManager(),
				TexEditingSettings.FOLDING_ENABLED_PREF,
				TexEditingSettings.MARKOCCURRENCES_ENABLED_PREF );
		
		fTexConfig = new LtxSourceViewerConfigurator(null,
				new LtxSourceViewerConfiguration(LtxDocumentContentInfo.INSTANCE, this,
						null, null, null ));
		return fTexConfig;
	}
	
	
	@Override
	protected void initializeKeyBindingScopes() {
		setContexts(CONTEXT_IDS);
	}
	
	@Override
	protected ISourceEditorAddon createCodeFoldingProvider() {
		return new FoldingEditorAddon(new LtxDefaultFoldingProvider());
	}
	
	@Override
	protected ISourceEditorAddon createMarkOccurrencesProvider() {
		return new ThisMarkOccurrencesProvider(this);
	}
	
	
	@Override
	public ITexSourceUnit getSourceUnit() {
		return (ITexSourceUnit) super.getSourceUnit();
	}
	
	@Override
	protected void setupConfiguration(final IEditorInput newInput) {
		super.setupConfiguration(newInput);
		
		final ITexSourceUnit su = getSourceUnit();
		fTexConfig.setSource((su != null) ? su.getTexCoreAccess() : null);
	}
	
	
	@Override
	protected void handlePreferenceStoreChanged(final org.eclipse.jface.util.PropertyChangeEvent event) {
		if (AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH.equals(event.getProperty())
				|| AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS.equals(event.getProperty())) {
			return;
		}
		super.handlePreferenceStoreChanged(event);
	}
	
	
	@Override
	protected boolean isTabsToSpacesConversionEnabled() {
		return false;
	}
	
	
	@Override
	protected void collectContextMenuPreferencePages(final List<String> pageIds) {
		super.collectContextMenuPreferencePages(pageIds);
		pageIds.add(TexUI.EDITOR_PREF_PAGE_ID);
		pageIds.add("de.walware.docmlet.tex.preferencePages.LtxTextStyles"); //$NON-NLS-1$
		pageIds.add("de.walware.docmlet.tex.preferencePages.LtxEditorTemplates"); //$NON-NLS-1$
		pageIds.add("de.walware.docmlet.tex.preferencePages.TexCodeStyle"); //$NON-NLS-1$
	}
	
	@Override
	protected SourceEditor1OutlinePage createOutlinePage() {
		return new LtxOutlinePage(this);
	}
	
	
	@Override
	public String[] getShowInTargetIds() {
		return new String[] { IPageLayout.ID_PROJECT_EXPLORER, IPageLayout.ID_OUTLINE };
	}
	
}
