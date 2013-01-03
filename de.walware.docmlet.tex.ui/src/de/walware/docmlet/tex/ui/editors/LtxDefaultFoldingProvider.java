/*******************************************************************************
 * Copyright (c) 2007-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.ui.editors;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;

import de.walware.ecommons.ltk.ISourceElement;
import de.walware.ecommons.ltk.ISourceStructElement;
import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.ltk.ast.ICommonAstVisitor;
import de.walware.ecommons.ltk.ui.sourceediting.FoldingEditorAddon.FoldingAnnotation;
import de.walware.ecommons.ltk.ui.sourceediting.FoldingEditorAddon.FoldingProvider;
import de.walware.ecommons.ltk.ui.sourceediting.FoldingEditorAddon.FoldingStructureComputationContext;
import de.walware.ecommons.ltk.ui.sourceediting.FoldingEditorAddon.NodeFoldingProvider;

import de.walware.docmlet.tex.core.ast.Embedded;
import de.walware.docmlet.tex.core.ast.TexAstVisitor;
import de.walware.docmlet.tex.core.model.ILtxSourceElement;
import de.walware.docmlet.tex.core.model.TexModel;


/**
 * Code folding provider for Tex documents.
 */
public class LtxDefaultFoldingProvider implements FoldingProvider {
	
	
	private static final String TYPE_SECTION = "de.walware.docmlet.tex.Section";
	private static final String TYPE_EMBEDDED = "de.walware.docmlet.tex.Embedded";
	
	
	private static class ElementFinder extends TexAstVisitor {
		
		private final FoldingStructureComputationContext fContext;
		private final FoldingConfiguration fConfig;
		private final ICommonAstVisitor fEmbeddedVisitor;
		
		public ElementFinder(final FoldingStructureComputationContext ctx,
				final FoldingConfiguration config, final ICommonAstVisitor embeddedVisitor) {
			fContext = ctx;
			fConfig = config;
			fEmbeddedVisitor = embeddedVisitor;
		}
		
		public void visit(final ISourceStructElement element) throws InvocationTargetException {
			if (element.getModelTypeId() == TexModel.LTX_TYPE_ID) {
				if ((element.getElementType() & ISourceElement.MASK_C1) == ILtxSourceElement.C1_EMBEDDED) {
					final IRegion region = element.getSourceRange();
					createEmbeddedRegion(region.getOffset(), region.getOffset()+region.getLength());
					
					if (fEmbeddedVisitor != null) {
						final IAstNode node = (IAstNode) element.getAdapter(IAstNode.class);
						if (node instanceof Embedded) {
							fEmbeddedVisitor.visit(((Embedded) node).getForeignNode());
						}
					}
					return;
				}
				else if ((element.getElementType() & ISourceElement.MASK_C2) == ILtxSourceElement.C2_SECTIONING) {
					final IRegion region = element.getSourceRange();
					createSectionRegion(region.getOffset(), region.getOffset()+region.getLength());
				}
			}
			
			final List<? extends ISourceStructElement> children = element.getSourceChildren(null);
			for (final ISourceStructElement child : children) {
				visit(child);
			}
		}
		
		private void createSectionRegion(final int startOffset, final int stopOffset)
				throws InvocationTargetException {
			try {
				final AbstractDocument doc = fContext.document;
				final int startLine = doc.getLineOfOffset(startOffset);
				int stopLine = doc.getLineOfOffset(stopOffset);
				final IRegion stopLineInfo = doc.getLineInformation(stopLine);
				if (stopLineInfo.getOffset() + stopLineInfo.getLength() > stopOffset) {
					stopLine--;
				}
				if (stopLine - startLine + 1 >= fConfig.minLines) {
					final int offset = doc.getLineOffset(startLine);
					fContext.addFoldingRegion(
							new Position(offset, doc.getLineOffset(stopLine)+doc.getLineLength(stopLine) - offset),
							new FoldingAnnotation(TYPE_SECTION, false) );
				}
			}
			catch (final BadLocationException e) {
				throw new InvocationTargetException(e);
			}
		}
		
		private void createEmbeddedRegion(final int startOffset, final int stopOffset)
				throws InvocationTargetException {
			try {
				final AbstractDocument doc = fContext.document;
				final int startLine = doc.getLineOfOffset(startOffset);
				int stopLine = doc.getLineOfOffset(stopOffset);
				final IRegion stopLineInfo = doc.getLineInformation(stopLine);
				if (stopLineInfo.getOffset() >= stopOffset) {
					stopLine--;
				}
				if (stopLine - startLine + 1 >= fConfig.minLines) {
					final int offset = doc.getLineOffset(startLine);
					fContext.addFoldingRegion(
							new Position(offset, doc.getLineOffset(stopLine)+doc.getLineLength(stopLine) - offset),
							new FoldingAnnotation(TYPE_EMBEDDED, false) );
				}
			}
			catch (final BadLocationException e) {
				throw new InvocationTargetException(e);
			}
		}
		
	}
	
	protected static final class FoldingConfiguration {
		
		public int minLines;
		
	}
	
	
	private FoldingConfiguration fConfig;
	
	private final NodeFoldingProvider fEmbeddedProvider;
	
	
	/**
	 * Creates new provider.
	 */
	public LtxDefaultFoldingProvider() {
		fEmbeddedProvider = null;
	}
	
	/**
	 * Creates new provider with support for additional code folding in embedded elements.
	 * 
	 * @param embeddedProvider provider for embedded elements
	 */
	public LtxDefaultFoldingProvider(final NodeFoldingProvider embeddedProvider) {
		fEmbeddedProvider = embeddedProvider;
	}
	
	
	@Override
	public boolean checkConfig(final Set<String> groupIds) {
		boolean changed = false;
		if (groupIds == null) {
			final FoldingConfiguration config = new FoldingConfiguration();
			fConfig = config;
			changed |= true;
		}
		if (fEmbeddedProvider != null) {
			changed |= fEmbeddedProvider.checkConfig(groupIds);
		}
		return changed;
	}
	
	@Override
	public boolean requiresModel() {
		return true;
	}
	
	@Override
	public void collectRegions(final FoldingStructureComputationContext ctx) throws InvocationTargetException {
		try {
			final ICommonAstVisitor embeddedVisitor = (fEmbeddedProvider != null) ?
					fEmbeddedProvider.createVisitor(ctx) : null;
			final ElementFinder elementFinder = new ElementFinder(ctx, fConfig, embeddedVisitor);
			elementFinder.visit(ctx.model.getSourceElement());
		}
		catch (final RuntimeException e) {
			throw new InvocationTargetException(e);
		}
	}
	
}
