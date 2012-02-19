/*******************************************************************************
 * Copyright (c) 2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.internal.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.statushandlers.StatusManager;

import de.walware.ecommons.ui.actions.SubMenuContributionItem;
import de.walware.ecommons.ui.util.UIAccess;

import de.walware.docmlet.tex.core.TexCore;
import de.walware.docmlet.tex.core.commands.LtxCommandCategories;
import de.walware.docmlet.tex.core.commands.LtxCommandCategories.Category;
import de.walware.docmlet.tex.core.commands.TexCommand;
import de.walware.docmlet.tex.internal.ui.TexUIPlugin;
import de.walware.docmlet.tex.ui.TexImages;
import de.walware.docmlet.tex.ui.editors.ILtxEditor;


public class LtxSymbolsMenuContributions extends CompoundContributionItem {
	
	
	private static class CategoryContributions extends SubMenuContributionItem implements SelectionListener {
		
		
		private final Category fCategory;
		
		
		public CategoryContributions(final Category category) {
			fCategory = category;
		}
		
		
		@Override
		protected Image getImage() {
			return null;
		}
		
		@Override
		protected String getLabel() {
			return fCategory.getLabel();
		}
		
		@Override
		protected void fillMenu(final Menu menu) {
			final ImageRegistry imageRegistry = TexImages.getImageRegistry();
			final List<TexCommand> commands = fCategory.getCommands();
			for (final TexCommand command : commands) {
				final MenuItem item = new MenuItem(menu, SWT.PUSH);
				
				final String imageKey = TexImages.getCommandImageKey(command);
				if (imageKey != null) {
					item.setImage(imageRegistry.get(imageKey));
				}
				item.setText(command.getControlWord());
				item.setData(command);
				
				item.addSelectionListener(this);
			}
		}
		
		@Override
		public void widgetDefaultSelected(final SelectionEvent event) {
		}
		
		@Override
		public void widgetSelected(final SelectionEvent event) {
			final TexCommand command = (TexCommand) event.widget.getData();
			
			final IEditorPart editor = UIAccess.getActiveWorkbenchPage(true).getActiveEditor();
			if (editor instanceof ILtxEditor) {
				final ILtxEditor texEditor = (ILtxEditor) editor;
				if (!texEditor.isEditable(true)) {
					return;
				}
				final SourceViewer viewer = ((ILtxEditor) editor).getViewer();
				Point selection = viewer.getSelectedRange();
				if (selection == null || selection.x < 0) {
					return;
				}
				try {
					final LtxAssistInvocationContext context = new LtxAssistInvocationContext(texEditor, selection.x, true, new NullProgressMonitor());
					final LtxCommandCompletionProposal proposal = new LtxCommandCompletionProposal(context, context.getInvocationOffset(), command);
					proposal.apply(viewer, (char) 0, 1, context.getInvocationOffset());
					selection = proposal.getSelection(viewer.getDocument());
					if (selection != null) {
						viewer.setSelectedRange(selection.x, selection.y);
						viewer.revealRange(selection.x, selection.y);
					}
					else {
						viewer.revealRange(context.getInvocationOffset(), 0);
					}
				}
				catch (final Exception e) {
					StatusManager.getManager().handle(new Status(IStatus.ERROR, TexUIPlugin.PLUGIN_ID,
							0, "An error occurred when inserting LaTeX symbol.", e));
				}
			}
		}
		
	}
	
	
	private LtxCommandCategories fCategories;
	
	
	public LtxSymbolsMenuContributions() {
	}
	
	
	@Override
	protected IContributionItem[] getContributionItems() {
		fCategories = new LtxCommandCategories(TexCore.getWorkbenchAccess().getTexCommandSet().getAllLtxCommands()) {
			@Override
			protected boolean include(final TexCommand command) {
				return ((command.getType() & TexCommand.MASK_MAIN) == TexCommand.MATHSYMBOL
						&& (command.getType() & TexCommand.MASK_C3) != TexCommand.C3_MATHSYMBOL_OP_NAMED);
			}
		};
		final List<Category> categories = fCategories.getCategories();
		final List<IContributionItem> items = new ArrayList<IContributionItem>(categories.size()+10);
		int sep = 0;
		for (final Category category : categories) {
			final int current = (category.getCommands().get(0).getType() & TexCommand.MASK_C2);
			if (sep != current) {
				sep = current;
				if (items.size() > 0) {
					items.add(new Separator());
				}
			}
			items.add(new CategoryContributions(category));
		}
		return items.toArray(new IContributionItem[items.size()]);
	}
	
}
