/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.ui.editors;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

import de.walware.ecommons.ltk.IModelManager;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;

import de.walware.docmlet.wikitext.core.ast.WikitextAstNode;
import de.walware.docmlet.wikitext.core.model.IWikidocModelInfo;
import de.walware.docmlet.wikitext.core.model.IWikitextSourceUnit;
import de.walware.docmlet.wikitext.core.model.WikitextModel;


public class OpenMarkupElementHyperlink implements IHyperlink {
	
	
	private final ISourceEditor editor;
	private final IRegion region;
	
	private final IWikitextSourceUnit sourceUnit;
	private final String label;
	
	
	public OpenMarkupElementHyperlink(final ISourceEditor editor, final IWikitextSourceUnit su,
			final IRegion region, final String label) {
		assert (su != null);
		assert (label != null);
		
		this.editor= editor;
		this.region= region;
		this.sourceUnit= su;
		this.label= label;
	}
	
	
	@Override
	public String getTypeLabel() {
		return null;
	}
	
	@Override
	public IRegion getHyperlinkRegion() {
		return this.region;
	}
	
	@Override
	public String getHyperlinkText() {
		return NLS.bind(Messages.Hyperlinks_OpenDeclaration_label, this.label);
	}
	
	@Override
	public void open() {
		final IWikidocModelInfo modelInfo= (IWikidocModelInfo) this.sourceUnit.getModelInfo(
				WikitextModel.WIKIDOC_TYPE_ID, IModelManager.MODEL_FILE, new NullProgressMonitor() );
		if (modelInfo != null) {
			final WikitextAstNode node= modelInfo.getLabels().get(this.label);
			if (node != null) {
				this.editor.selectAndReveal(node.getOffset(), 0);
				return;
			}
			Display.getCurrent().beep();
		}
	}
	
}
