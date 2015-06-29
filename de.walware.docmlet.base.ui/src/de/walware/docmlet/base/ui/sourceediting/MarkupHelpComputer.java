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

package de.walware.docmlet.base.ui.sourceediting;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.texteditor.IEditorStatusLine;

import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistProposalCollector;
import de.walware.ecommons.ltk.ui.sourceediting.assist.IAssistCompletionProposal;
import de.walware.ecommons.ltk.ui.sourceediting.assist.IQuickAssistComputer;
import de.walware.ecommons.ui.util.UIAccess;

import de.walware.docmlet.base.ui.DocBaseUI;
import de.walware.docmlet.base.ui.DocBaseUIResources;
import de.walware.docmlet.base.ui.markuphelp.IMarkupHelpContextProvider;
import de.walware.docmlet.base.ui.markuphelp.IMarkupHelpView;


public class MarkupHelpComputer implements IQuickAssistComputer {
	
	
	private static class ShowHelpProposal implements IAssistCompletionProposal {
		
		
		private final ISourceEditor editor;
		
		
		public ShowHelpProposal(final ISourceEditor editor) {
			this.editor= editor;
		}
		
		@Override
		public int getRelevance() {
			return 1000;
		}
		
		@Override
		public Image getImage() {
			return DocBaseUIResources.INSTANCE.getImage(DocBaseUIResources.VIEW_MARKUP_HELP_IMAGE_ID);
		}
		
		@Override
		public String getDisplayString() {
			return "Show Markup Cheat Sheet";
		}
		
		@Override
		public String getSortingString() {
			return getDisplayString();
		}
		
		@Override
		public String getAdditionalProposalInfo() {
			return null;
		}
		
		@Override
		public void selected(final ITextViewer viewer, final boolean smartToggle) {
		}
		
		@Override
		public void unselected(final ITextViewer viewer) {
		}
		
		@Override
		public boolean validate(final IDocument document, final int offset, final DocumentEvent event) {
			return true;
		}
		
		@Override
		public void apply(final IDocument document) {
			final IMarkupHelpContextProvider contextProvider= (IMarkupHelpContextProvider) this.editor.getAdapter(IMarkupHelpContextProvider.class);
			final String contentId= contextProvider.getHelpContentId();
			if (contentId != null) {
				final IWorkbenchPage page= UIAccess.getActiveWorkbenchPage(true);
				if (page != null) {
					try {
						final IMarkupHelpView view= (IMarkupHelpView) page.showView(IMarkupHelpView.VIEW_ID);
						view.show(contentId);
					}
					catch (final PartInitException e) {
						StatusManager.getManager().handle(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, 0,
								"An error occurred while opening the Markup Help View to show a cheat sheet.",
								e ));
					}
				}
			}
			else {
				final IEditorStatusLine statusLine= (IEditorStatusLine) this.editor.getAdapter(IEditorStatusLine.class);
				if (statusLine != null) {
					statusLine.setMessage(true, "No cheat sheet available for the current markup language.", null);
				}
			}
		}
		
		@Override
		public void apply(final ITextViewer viewer, final char trigger, final int stateMask, final int offset) {
			apply(null);
		}
		
		@Override
		public Point getSelection(final IDocument document) {
			return null;
		}
		
		@Override
		public IContextInformation getContextInformation() {
			return null;
		}
		
	}
	
	
	public MarkupHelpComputer() {
	}
	
	
	@Override
	public IStatus computeAssistProposals(final AssistInvocationContext context,
			final AssistProposalCollector<IAssistCompletionProposal> proposals,
			final IProgressMonitor monitor) {
		final IMarkupHelpContextProvider contextProvider= (IMarkupHelpContextProvider) context.getEditor().getAdapter(IMarkupHelpContextProvider.class);
		if (contextProvider != null) {
			proposals.add(new ShowHelpProposal(context.getEditor()));
		}
		
		return Status.OK_STATUS;
	}
	
}
