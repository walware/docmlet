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

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.actions.OpenDeclaration;

import de.walware.docmlet.tex.core.model.ITexSourceUnit;
import de.walware.docmlet.tex.core.model.TexNameAccess;


public class OpenTexElementHyperlink implements IHyperlink {
	
	
	private final ISourceEditor editor;
	private final IRegion region;
	
	private final ITexSourceUnit sourceUnit;
	private final TexNameAccess access;
	
	
	public OpenTexElementHyperlink(final ISourceEditor editor, final ITexSourceUnit sourceUnit,
			final TexNameAccess access) {
		assert (sourceUnit != null);
		assert (access != null);
		
		this.editor= editor;
		this.region= TexNameAccess.getTextRegion(access.getNameNode());
		this.sourceUnit= sourceUnit;
		this.access= access;
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
		return NLS.bind(Messages.Hyperlinks_OpenDeclaration_label, this.access.getDisplayName());
	}
	
	@Override
	public void open() {
//		try {
			final OpenDeclaration open= new OpenDeclaration();
			final TexNameAccess declAccess= open.selectAccess(this.access.getAllInUnit());
			if (declAccess != null) {
				open.open(this.editor, declAccess);
				return;
			}
			Display.getCurrent().beep();
//		}
//		catch (final PartInitException e) {
//			Display.getCurrent().beep();
//			StatusManager.getManager().handle(new Status(IStatus.INFO, SharedUIResources.PLUGIN_ID, -1,
//					NLS.bind("An error occurred when following the hyperlink and opening the editor for the declaration of ''{0}''", fAccess.getDisplayName()), e));
//		}
	}
	
}
