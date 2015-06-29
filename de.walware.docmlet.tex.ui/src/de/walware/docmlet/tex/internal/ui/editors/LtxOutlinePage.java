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

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.services.IServiceLocator;

import de.walware.ecommons.ltk.core.model.IModelElement;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditor1;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditor2OutlinePage;
import de.walware.ecommons.ltk.ui.util.ViewerDragSupport;
import de.walware.ecommons.ltk.ui.util.ViewerDropSupport;
import de.walware.ecommons.ui.actions.HandlerCollection;
import de.walware.ecommons.ui.util.DialogUtil;

import de.walware.docmlet.tex.core.model.TexModel;
import de.walware.docmlet.tex.core.refactoring.TexRefactoring;
import de.walware.docmlet.tex.internal.ui.TexUIPlugin;
import de.walware.docmlet.tex.ui.TexLabelProvider;


/**
 * Outline page for R sources
 */
public class LtxOutlinePage extends SourceEditor2OutlinePage {
	
	private class ContentFilter implements IModelElement.Filter {
		
		public ContentFilter() {
		}
		
		@Override
		public boolean include(final IModelElement element) {
			switch (element.getElementType()) {
			default:
				return true;
			}
		};
		
	}
	
	
	private final ContentFilter fFilter = new ContentFilter();
	
	
	public LtxOutlinePage(final SourceEditor1 editor) {
		super(editor, TexModel.LTX_TYPE_ID, TexRefactoring.getLtxFactory(),
				"de.walware.r.menu.TexOutlineViewContextMenu"); //$NON-NLS-1$
	}
	
	
	@Override
	protected IDialogSettings getDialogSettings() {
		return DialogUtil.getDialogSettings(TexUIPlugin.getInstance(), "TexOutlineView"); //$NON-NLS-1$
	}
	
	@Override
	protected IModelElement.Filter getContentFilter() {
		return fFilter;
	}
	
	@Override
	protected void configureViewer(final TreeViewer viewer) {
		super.configureViewer(viewer);
		
		viewer.setLabelProvider(new TexLabelProvider());
		
		final ViewerDropSupport drop = new ViewerDropSupport(viewer, this,
				getRefactoringFactory() );
		drop.init();
		final ViewerDragSupport drag = new ViewerDragSupport(viewer);
		drag.init();
	}
	
	@Override
	protected void contributeToActionBars(final IServiceLocator serviceLocator,
			final IActionBars actionBars, final HandlerCollection handlers) {
		super.contributeToActionBars(serviceLocator, actionBars, handlers);
		
//		final IToolBarManager toolBarManager = actionBars.getToolBarManager();
//		
//		toolBarManager.appendToGroup(ECommonsUI.VIEW_SORT_MENU_ID,
//				new AlphaSortAction());
//		toolBarManager.appendToGroup(ECommonsUI.VIEW_FILTER_MENU_ID,
//				new FilterCommonVariables());
//		toolBarManager.appendToGroup(ECommonsUI.VIEW_FILTER_MENU_ID,
//				new FilterLocalDefinitions());
	}
	
}
