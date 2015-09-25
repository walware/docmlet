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
import de.walware.ecommons.ltk.core.model.INameAccessSet;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.actions.OpenDeclaration;

import de.walware.docmlet.wikitext.core.model.IWikidocModelInfo;
import de.walware.docmlet.wikitext.core.model.IWikitextSourceUnit;
import de.walware.docmlet.wikitext.core.model.WikitextModel;
import de.walware.docmlet.wikitext.core.model.WikitextNameAccess;


public class OpenMarkupElementHyperlink implements IHyperlink {
	
	
	private final ISourceEditor editor;
	private final IRegion region;
	
	private final IWikitextSourceUnit sourceUnit;
	private final WikitextNameAccess access;
	private final String label;
	
	
	public OpenMarkupElementHyperlink(final ISourceEditor editor, final IWikitextSourceUnit sourceUnit,
			final IRegion region, final String label) {
		assert (sourceUnit != null);
		assert (label != null);
		
		this.editor= editor;
		this.region= region;
		this.sourceUnit= sourceUnit;
		this.access= null;
		this.label= label;
	}
	
	public OpenMarkupElementHyperlink(final ISourceEditor editor, final IWikitextSourceUnit sourceUnit,
			final WikitextNameAccess access) {
		assert (sourceUnit != null);
		assert (access != null);
		
		this.editor= editor;
		this.region= (access.getNameNode() != null) ? access.getNameNode() : access.getNode();
		this.sourceUnit= sourceUnit;
		this.access= access;
		this.label= this.access.getSegmentName();
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
			final int type= (this.access != null) ? this.access.getType() : WikitextNameAccess.LINK_ANCHOR_LABEL;
			final INameAccessSet<WikitextNameAccess> labels;
			switch (type) {
			case WikitextNameAccess.LINK_DEF_LABEL:
				labels= modelInfo.getLinkRefLabels();
				break;
			case WikitextNameAccess.LINK_ANCHOR_LABEL:
				labels= modelInfo.getLinkAnchorLabels();
				break;
			default:
				return;
			}
			final OpenDeclaration open= new OpenDeclaration();
			final WikitextNameAccess declAccess= open.selectAccess(labels.getAllInUnit(this.label));
			if (declAccess != null) {
				open.open(this.editor, declAccess);
				return;
			}
			Display.getCurrent().beep();
		}
	}
	
}
