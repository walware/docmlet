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
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.util.OpenWorkspaceFileHyperlink;

import de.walware.docmlet.wikitext.core.ast.Link;
import de.walware.docmlet.wikitext.core.model.IWikitextSourceUnit;
import de.walware.docmlet.wikitext.core.model.WikitextModel;
import de.walware.docmlet.wikitext.core.model.WikitextNameAccess;


public class MarkupHyperlinkDetector extends AbstractHyperlinkDetector {
	
	
	public MarkupHyperlinkDetector() {
	}
	
	
	@Override
	public IHyperlink[] detectHyperlinks(final ITextViewer textViewer, final IRegion region,
			final boolean canShowMultipleHyperlinks) {
		final ISourceEditor editor= (ISourceEditor) getAdapter(ISourceEditor.class);
		if (editor == null) {
			return null;
		}
		final IWikitextSourceUnit sourceUnit= WikitextModel.asWikitextSourceUnit(editor.getSourceUnit());
		if (sourceUnit == null) {
			return null;
		}
		
		final List<IHyperlink> hyperlinks= new ArrayList<>(4);
		
		final Link link= MarkupOpenHyperlinkHandler.searchLink(sourceUnit, region);
		if (link != null) {
			if (link.getLinkType() == Link.LINK_BY_REF) {
				for (final Object attachment : link.getAttachments()) {
					if (attachment instanceof WikitextNameAccess) {
						hyperlinks.add(new OpenMarkupElementHyperlink(editor, sourceUnit,
								(WikitextNameAccess) attachment ));
						break;
					}
				}
			}
			else if (link.getUri() != null && !link.getUri().isEmpty()) {
				if (link.getUri().charAt(0) == '#') {
					if (link.getUri().length() > 1) {
						hyperlinks.add(new OpenMarkupElementHyperlink(editor, sourceUnit,
								link, link.getUri().substring(1) ));
					}
				}
				else {
					final List<IFile> files= MarkupOpenHyperlinkHandler.refLocalFile(editor, link);
					for (final IFile file : files) {
						hyperlinks.add(new OpenWorkspaceFileHyperlink(link, file));
					}
				}
			}
		}
		
		if (!hyperlinks.isEmpty()) {
			return hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
		}
		return null;
	}
	
}
