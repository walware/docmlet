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

package de.walware.docmlet.wikitext.ui.editors;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;

import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.ltk.ast.ICommonAstVisitor;
import de.walware.ecommons.ltk.core.model.ISourceElement;
import de.walware.ecommons.ltk.core.model.ISourceStructElement;
import de.walware.ecommons.ltk.ui.sourceediting.folding.FoldingAnnotation;
import de.walware.ecommons.ltk.ui.sourceediting.folding.FoldingEditorAddon.FoldingStructureComputationContext;
import de.walware.ecommons.ltk.ui.sourceediting.folding.FoldingProvider;
import de.walware.ecommons.ltk.ui.sourceediting.folding.NodeFoldingProvider;
import de.walware.ecommons.ltk.ui.sourceediting.folding.SimpleFoldingPosition;
import de.walware.ecommons.preferences.PreferencesUtil;
import de.walware.ecommons.preferences.core.IPreferenceAccess;

import de.walware.docmlet.wikitext.core.ast.Embedded;
import de.walware.docmlet.wikitext.core.ast.WikitextAstVisitor;
import de.walware.docmlet.wikitext.core.model.IWikitextSourceElement;
import de.walware.docmlet.wikitext.core.model.WikitextModel;
import de.walware.docmlet.wikitext.ui.sourceediting.WikitextEditingSettings;


/**
 * Code folding provider for Tex documents.
 */
public class WikidocDefaultFoldingProvider implements FoldingProvider {
	
	
	private static final String TYPE_SECTION= "de.walware.docmlet.wikitext.Section"; //$NON-NLS-1$
	private static final String TYPE_EMBEDDED= "de.walware.docmlet.wikitext.Embedded"; //$NON-NLS-1$
	
	
	private static class ElementFinder extends WikitextAstVisitor {
		
		private final FoldingStructureComputationContext context;
		private final FoldingConfiguration config;
		
		private final NodeFoldingProvider.VisitorMap embeddedVisitors;
		
		
		public ElementFinder(final FoldingStructureComputationContext ctx,
				final FoldingConfiguration config,
				final Map<String, ? extends NodeFoldingProvider> embeddedProviders) {
			this.context= ctx;
			this.config= config;
			this.embeddedVisitors= (!embeddedProviders.isEmpty()) ?
					new NodeFoldingProvider.VisitorMap(embeddedProviders) :
					null;
		}
		
		
		public void visit(final ISourceStructElement element) throws InvocationTargetException {
			if (element.getModelTypeId() == WikitextModel.WIKIDOC_TYPE_ID) {
				if ((element.getElementType() & ISourceElement.MASK_C1) == IWikitextSourceElement.C1_EMBEDDED) {
					final IRegion region= element.getSourceRange();
					createEmbeddedRegion(region.getOffset(), region.getOffset()+region.getLength());
					
					if (this.embeddedVisitors != null) {
						final IAstNode node= (IAstNode) element.getAdapter(IAstNode.class);
						if (node instanceof Embedded) {
							final Embedded embedded= (Embedded) node;
							final ICommonAstVisitor visitor= this.embeddedVisitors.get(embedded.getForeignTypeId());
							if (visitor != null) {
								visitor.visit(embedded.getForeignNode());
							}
						}
					}
					return;
				}
				else if ((element.getElementType() & ISourceElement.MASK_C2) == IWikitextSourceElement.C2_SECTIONING) {
					final IRegion region= element.getSourceRange();
					createSectionRegion(region.getOffset(), region.getOffset()+region.getLength());
				}
			}
			
			final List<? extends ISourceStructElement> children= element.getSourceChildren(null);
			for (final ISourceStructElement child : children) {
				visit(child);
			}
		}
		
		private void createSectionRegion(final int startOffset, final int stopOffset)
				throws InvocationTargetException {
			try {
				final AbstractDocument doc= this.context.document;
				final int startLine= doc.getLineOfOffset(startOffset);
				int stopLine= doc.getLineOfOffset(stopOffset);
				final IRegion stopLineInfo= doc.getLineInformation(stopLine);
				if (stopLineInfo.getOffset() + stopLineInfo.getLength() > stopOffset) {
					stopLine--;
				}
				if (stopLine - startLine + 1 >= this.config.minLines) {
					final int offset= doc.getLineOffset(startLine);
					this.context.addFoldingRegion(new FoldingAnnotation(TYPE_SECTION, false,
							new SimpleFoldingPosition(offset, doc.getLineOffset(stopLine) + doc.getLineLength(stopLine) - offset) ));
				}
			}
			catch (final BadLocationException e) {
				throw new InvocationTargetException(e);
			}
		}
		
		private void createEmbeddedRegion(final int startOffset, final int stopOffset)
				throws InvocationTargetException {
			try {
				final AbstractDocument doc= this.context.document;
				final int startLine= doc.getLineOfOffset(startOffset);
				int stopLine= doc.getLineOfOffset(stopOffset);
				final IRegion stopLineInfo= doc.getLineInformation(stopLine);
				if (stopLineInfo.getOffset() >= stopOffset) {
					stopLine--;
				}
				if (stopLine - startLine + 1 >= this.config.minLines) {
					final int offset= doc.getLineOffset(startLine);
					this.context.addFoldingRegion(new FoldingAnnotation(TYPE_EMBEDDED, false,
							new SimpleFoldingPosition(offset, doc.getLineOffset(stopLine) + doc.getLineLength(stopLine) - offset) ));
				}
			}
			catch (final BadLocationException e) {
				throw new InvocationTargetException(e);
			}
		}
		
	}
	
	protected static final class FoldingConfiguration {
		
		public int minLines;
		
		public boolean isRestoreStateEnabled;
		
	}
	
	
	private FoldingConfiguration config;
	
	private final Map<String, ? extends NodeFoldingProvider> embeddedProviders;
	
	
	/**
	 * Creates new provider.
	 */
	public WikidocDefaultFoldingProvider() {
		this.embeddedProviders= Collections.emptyMap();
	}
	
	/**
	 * Creates new provider with support for additional code folding in embedded elements.
	 * 
	 * @param embeddedProviders provider for embedded elements
	 */
	public WikidocDefaultFoldingProvider(final Map<String, ? extends NodeFoldingProvider> embeddedProviders) {
		this.embeddedProviders= embeddedProviders;
	}
	
	
	@Override
	public boolean checkConfig(final Set<String> groupIds) {
		boolean changed= false;
		if (groupIds == null
				|| groupIds.contains(WikitextEditingSettings.FOLDING_SHARED_GROUP_ID) ) {
			final FoldingConfiguration config= new FoldingConfiguration();
			final IPreferenceAccess prefs= PreferencesUtil.getInstancePrefs();
			
			config.isRestoreStateEnabled= prefs.getPreferenceValue(
					WikitextEditingSettings.FOLDING_RESTORE_STATE_ENABLED_PREF );
			config.minLines= 2;
			
			this.config= config;
			changed |= true;
		}
		for (final NodeFoldingProvider provider : this.embeddedProviders.values()) {
			changed |= provider.checkConfig(groupIds);
		}
		return changed;
	}
	
	@Override
	public boolean isRestoreStateEnabled() {
		return this.config.isRestoreStateEnabled;
	}
	
	@Override
	public boolean requiresModel() {
		return true;
	}
	
	@Override
	public void collectRegions(final FoldingStructureComputationContext ctx) throws InvocationTargetException {
		try {
			final ElementFinder elementFinder= new ElementFinder(ctx, this.config,
					this.embeddedProviders );
			elementFinder.visit(ctx.model.getSourceElement());
		}
		catch (final RuntimeException e) {
			throw new InvocationTargetException(e);
		}
	}
	
}
