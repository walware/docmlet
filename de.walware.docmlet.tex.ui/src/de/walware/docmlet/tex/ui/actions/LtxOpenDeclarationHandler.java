/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.ui.actions;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IRegion;

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.IModelManager;
import de.walware.ecommons.ltk.ast.AstSelection;
import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.ltk.core.model.ISourceUnit;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.actions.AbstractOpenDeclarationHandler;
import de.walware.ecommons.ltk.ui.sourceediting.actions.OpenDeclaration;

import de.walware.docmlet.tex.core.ast.TexAst;
import de.walware.docmlet.tex.core.ast.TexAstNode;
import de.walware.docmlet.tex.core.model.ILtxModelInfo;
import de.walware.docmlet.tex.core.model.ITexSourceUnit;
import de.walware.docmlet.tex.core.model.TexModel;
import de.walware.docmlet.tex.core.model.TexNameAccess;


public class LtxOpenDeclarationHandler extends AbstractOpenDeclarationHandler {
	
	
//	private static final class TexOpenDeclaration extends OpenDeclaration {
//		
//		@Override
//		public ILabelProvider createLabelProvider() {
//			return new TexLabelProvider();
//		}
//	}
	
	
	public static TexNameAccess searchAccess(final ISourceEditor editor, final IRegion region) {
//		try {
//			final IDocument document = editor.getViewer().getDocument();
			final ISourceUnit su = editor.getSourceUnit();
			if (su instanceof ITexSourceUnit) {
				final ILtxModelInfo info = (ILtxModelInfo) su.getModelInfo(TexModel.LTX_TYPE_ID, IModelManager.MODEL_FILE, new NullProgressMonitor());
				if (info != null) {
					final AstInfo astInfo = info.getAst();
					final AstSelection selection = AstSelection.search(astInfo.root,
							region.getOffset(), region.getOffset()+region.getLength(),
							AstSelection.MODE_COVERING_SAME_LAST );
					final IAstNode covering = selection.getCovering();
					if (covering instanceof TexAstNode) {
						final TexAstNode node = (TexAstNode) covering;
						if (node.getNodeType() == TexAst.NodeType.LABEL) {
							TexAstNode current = node;
							do {
								for (final Object attachment : current.getAttachments()) {
									if (attachment instanceof TexNameAccess) {
										final TexNameAccess access = (TexNameAccess) attachment;
										if (access.getNameNode() == node) {
											return access;
										}
									}
								}
								current= current.getTexParent();
							} while (current != null);
						}
					}
				}
			}
//		}
//		catch (final BadLocationException e) {
//		}
		return null;
	}
	
	
	@Override
	public boolean execute(final ISourceEditor editor, final IRegion selection) {
		final TexNameAccess access= searchAccess(editor, selection);
		if (access != null) {
			final OpenDeclaration open= new OpenDeclaration();
			final TexNameAccess declAccess= open.selectAccess(access.getAllInUnit());
			if (declAccess != null) {
				open.open(editor, declAccess);
				return true;
			}
		}
		return false;
	}
	
}
