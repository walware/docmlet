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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;

import de.walware.docmlet.tex.core.model.ITexSourceUnit;
import de.walware.docmlet.tex.core.model.TexNameAccess;
import de.walware.docmlet.tex.ui.actions.LtxOpenDeclarationHandler;


public class TexElementHyperlinkDetector extends AbstractHyperlinkDetector {
	
	
	public TexElementHyperlinkDetector() {
	}
	
	
	@Override
	public IHyperlink[] detectHyperlinks(final ITextViewer textViewer,
			final IRegion region, final boolean canShowMultipleHyperlinks) {
		final ISourceEditor editor= (ISourceEditor) getAdapter(ISourceEditor.class);
		if (editor == null) {
			return null;
		}
		
		final List<IHyperlink> hyperlinks = new ArrayList<>(4);
		
		{	final TexNameAccess access = LtxOpenDeclarationHandler.searchAccess(editor, region);
			if (access != null) {
				hyperlinks.add(new OpenTexElementHyperlink(editor,
						(ITexSourceUnit) editor.getSourceUnit(), access));
			}
		}
		
		if (!hyperlinks.isEmpty()) {
			return hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
		}
		return null;
	}
	
}
