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

package de.walware.docmlet.tex.core.refactoring;

import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.ITypedRegion;

import de.walware.ecommons.ltk.core.ElementSet;
import de.walware.ecommons.ltk.core.model.IModelElement;
import de.walware.ecommons.ltk.core.model.ISourceElement;
import de.walware.ecommons.ltk.core.model.ISourceUnit;
import de.walware.ecommons.ltk.core.refactoring.RefactoringAdapter;
import de.walware.ecommons.ltk.core.refactoring.RefactoringDestination;
import de.walware.ecommons.ltk.core.refactoring.RefactoringDestination.Position;
import de.walware.ecommons.text.BasicHeuristicTokenScanner;

import de.walware.docmlet.tex.core.TexCore;
import de.walware.docmlet.tex.core.ast.Environment;
import de.walware.docmlet.tex.core.ast.TexAst;
import de.walware.docmlet.tex.core.ast.TexAstNode;
import de.walware.docmlet.tex.core.model.ITexSourceElement;
import de.walware.docmlet.tex.core.model.TexModel;
import de.walware.docmlet.tex.core.source.ITexDocumentConstants;
import de.walware.docmlet.tex.core.source.LtxHeuristicTokenScanner;


public class LtxRefactoringAdapter extends RefactoringAdapter {
	
	
	public LtxRefactoringAdapter() {
		super(TexModel.LTX_TYPE_ID);
	}
	
	
	@Override
	public String getPluginIdentifier() {
		return TexCore.PLUGIN_ID;
	}
	
	@Override
	public LtxHeuristicTokenScanner getScanner(final ISourceUnit su) {
		return LtxHeuristicTokenScanner.create(su.getDocumentContentInfo());
	}
	
	@Override
	public boolean canInsert(final ElementSet elements, final ISourceElement to,
			final RefactoringDestination.Position pos) {
		if (super.canInsert(elements, to, pos)) {
			if ((to.getElementType() & IModelElement.MASK_C2) == ITexSourceElement.C2_PREAMBLE) {
				for (final IModelElement element : elements.getModelElements()) {
					if ((element.getElementType() & IModelElement.MASK_C2) == ITexSourceElement.C2_SECTIONING) {
						return false;
					}
				}
			}
			if ((to.getElementType() & IModelElement.MASK_C1) == IModelElement.C1_EMBEDDED
					&& pos == RefactoringDestination.Position.INTO ) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isCommentContent(final ITypedRegion partition) {
		return (partition != null
				&& (partition.getType() == ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE
						|| partition.getType() == ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE ));
	}
	
	@Override
	protected int getInsertionOffset(final AbstractDocument document,
			final ISourceElement element, final Position pos,
			final BasicHeuristicTokenScanner scanner)
			throws BadLocationException, BadPartitioningException {
		if ((element.getElementType() & IModelElement.MASK_C2) == ITexSourceElement.C2_SOURCE_FILE
				&& pos == Position.INTO) {
			final TexAstNode astNode = (TexAstNode) element.getAdapter(TexAstNode.class);
			if (astNode != null) {
				final Environment environment = TexAst.getDocumentNode(astNode);
				if (environment.getEndNode() != null) {
					return environment.getEndNode().getOffset();
				}
			}
		}
		return super.getInsertionOffset(document, element, pos, scanner);
	}
	
}
