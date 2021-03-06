/*=============================================================================#
 # Copyright (c) 2014-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.ui.sourceediting;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SearchPattern;

import de.walware.ecommons.ltk.core.model.IModelElement.Filter;
import de.walware.ecommons.ltk.ui.sourceediting.QuickOutlineInformationControl;
import de.walware.ecommons.ltk.ui.sourceediting.actions.OpenDeclaration;
import de.walware.ecommons.ui.content.ITextElementFilter;
import de.walware.ecommons.ui.content.TextElementFilter;

import de.walware.docmlet.tex.core.model.TexModel;
import de.walware.docmlet.tex.ui.TexLabelProvider;
import de.walware.docmlet.tex.ui.util.TexNameSearchPattern;


public class LtxQuickOutlineInformationControl extends QuickOutlineInformationControl {
	
	
	public LtxQuickOutlineInformationControl(final Shell parent, final String commandId) {
		super(parent, commandId, 1, new OpenDeclaration());
	}
	
	
	@Override
	public String getModelTypeId() {
		return TexModel.LTX_TYPE_ID;
	}
	
	@Override
	protected Filter getContentFilter() {
		return null;
	}
	
	@Override
	protected ITextElementFilter createNameFilter() {
		return new TextElementFilter() {
			@Override
			protected SearchPattern createSearchPattern() {
				return new TexNameSearchPattern();
			}
		};
	}
	
	@Override
	protected void configureViewer(final TreeViewer viewer) {
		super.configureViewer(viewer);
		
		viewer.setLabelProvider(new TexLabelProvider());
	}
	
}
