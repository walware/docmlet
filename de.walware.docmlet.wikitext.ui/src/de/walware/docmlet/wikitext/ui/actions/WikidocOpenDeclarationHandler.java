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

package de.walware.docmlet.wikitext.ui.actions;

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

import de.walware.docmlet.wikitext.core.ast.WikitextAst;
import de.walware.docmlet.wikitext.core.ast.WikitextAstNode;
import de.walware.docmlet.wikitext.core.model.IWikidocModelInfo;
import de.walware.docmlet.wikitext.core.model.IWikitextSourceUnit;
import de.walware.docmlet.wikitext.core.model.WikitextModel;
import de.walware.docmlet.wikitext.core.model.WikitextNameAccess;


public class WikidocOpenDeclarationHandler extends AbstractOpenDeclarationHandler {
	
	
//	private static final class WikitextOpenDeclaration extends OpenDeclaration {
//		
//		@Override
//		public ILabelProvider createLabelProvider() {
//			return new WikitextLabelProvider();
//		}
//	}
	
	
	public static WikitextNameAccess searchAccess(final ISourceUnit sourceUnit, final IRegion region) {
		if (sourceUnit instanceof IWikitextSourceUnit) {
			final IWikidocModelInfo info= (IWikidocModelInfo) sourceUnit.getModelInfo(
					WikitextModel.WIKIDOC_TYPE_ID, IModelManager.MODEL_FILE, new NullProgressMonitor());
			if (info != null) {
				final AstInfo astInfo= info.getAst();
				final AstSelection selection= AstSelection.search(astInfo.root,
						region.getOffset(), region.getOffset()+region.getLength(),
						AstSelection.MODE_COVERING_SAME_LAST );
				final IAstNode covering= selection.getCovering();
				if (covering instanceof WikitextAstNode) {
					final WikitextAstNode node= (WikitextAstNode) covering;
					if (node.getNodeType() == WikitextAst.NodeType.LABEL) {
						WikitextAstNode current= node;
						do {
							for (final Object attachment : current.getAttachments()) {
								if (attachment instanceof WikitextNameAccess) {
									final WikitextNameAccess access= (WikitextNameAccess) attachment;
									if (access.getNameNode() == node) {
										return access;
									}
								}
							}
							current= current.getWikitextParent();
						} while (current != null);
					}
				}
			}
		}
		return null;
	}
	
	
	@Override
	public boolean execute(final ISourceEditor editor, final IRegion selection) {
		final WikitextNameAccess access= searchAccess(editor.getSourceUnit(), selection);
		if (access != null) {
			final OpenDeclaration open= new OpenDeclaration();
			final WikitextNameAccess declAccess= open.selectAccess(access.getAllInUnit());
			if (declAccess != null) {
				open.open(editor, declAccess);
				return true;
			}
		}
		return false;
	}
	
}
