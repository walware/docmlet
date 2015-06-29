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

package de.walware.docmlet.tex.internal.ui.editors;

import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;

import de.walware.docmlet.tex.core.ast.TexAstNode;
import de.walware.docmlet.tex.core.model.ITexSourceUnit;
import de.walware.docmlet.tex.core.model.TexLabelAccess;


public class OpenTexElementHyperlink implements IHyperlink {
	
	
	private final ISourceEditor editor;
	private final IRegion region;
	
	private final ITexSourceUnit su;
	private final TexLabelAccess access;
	
	
	public OpenTexElementHyperlink(final ISourceEditor editor, final ITexSourceUnit su,
			final TexLabelAccess access) {
		assert (su != null);
		assert (access != null);
		
		this.editor= editor;
		this.region= TexLabelAccess.getTextRegion(access.getNameNode());
		this.su= su;
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
			final List<? extends TexLabelAccess> all= this.access.getAllInUnit();
			for (final TexLabelAccess cand : all) {
				if (cand.isWriteAccess()) {
					final TexAstNode node= cand.getNode();
					this.editor.selectAndReveal(node.getOffset(), node.getLength());
					return;
				}
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
