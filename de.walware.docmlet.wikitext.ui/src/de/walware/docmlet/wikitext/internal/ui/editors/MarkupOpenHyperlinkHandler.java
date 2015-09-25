/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.ui.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IRegion;

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.ast.AstSelection;
import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;

import de.walware.docmlet.wikitext.core.ast.Link;
import de.walware.docmlet.wikitext.core.ast.WikitextAstNode;
import de.walware.docmlet.wikitext.core.model.IWikitextSourceUnit;
import de.walware.docmlet.wikitext.core.model.WikitextModel;


public class MarkupOpenHyperlinkHandler {
	
	
	static Link searchLink(final IWikitextSourceUnit sourceUnit, final IRegion region) {
		final AstInfo astInfo= sourceUnit.getAstInfo(WikitextModel.WIKIDOC_TYPE_ID,
				true, new NullProgressMonitor() );
		if (astInfo != null) {
			final AstSelection astSelection= AstSelection.search(astInfo.root,
					region.getOffset(), region.getOffset() + region.getLength(),
					AstSelection.MODE_COVERING_SAME_LAST );
			IAstNode node= astSelection.getCovering();
			while (node instanceof WikitextAstNode) {
				if (node instanceof Link) {
					return (Link) node;
				}
				node= node.getParent();
			}
		}
		return null;
	}
	
	static List<IFile> refLocalFile(final ISourceEditor editor, final Link link) {
		final String href= link.getUri();
		if (href.indexOf(':') == -1 && href.length() > 1 && href.charAt(0) != '/') {
			try {
				final Object resource= editor.getSourceUnit().getResource();
				if (resource instanceof IFile) {
					final IFile currentFile= (IFile) resource;
					final IFile refFile= currentFile.getParent().getFile(new Path(href));
					if (refFile != null) {
						final List<IFile> files= new ArrayList<>();
						if (refFile.exists()) {
							files.add(refFile);
						}
						else {
							final IContainer parent= refFile.getParent();
							final int dotIdx;
							if (parent.exists() && (dotIdx= refFile.getName().lastIndexOf('.')) >= 0) {
								final String mainName= refFile.getName().substring(0, dotIdx + 1);
								final IResource[] children= parent.members();
								for (final IResource child : children) {
									if (child.getType() == IResource.FILE
											&& child.getName().startsWith(mainName)) {
										files.add((IFile) child);
									}
								}
							}
						}
						return files;
					}
				}
			}
			catch (final CoreException e) {}
		}
		return Collections.EMPTY_LIST;
	}
	
	
	public MarkupOpenHyperlinkHandler() {
	}
	
	
}
