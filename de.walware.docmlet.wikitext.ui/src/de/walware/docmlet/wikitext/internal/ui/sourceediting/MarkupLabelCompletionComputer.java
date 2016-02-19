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

package de.walware.docmlet.wikitext.internal.ui.sourceediting;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Point;

import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.ltk.core.model.INameAccessSet;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistProposalCollector;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssist;
import de.walware.ecommons.ltk.ui.sourceediting.assist.IAssistCompletionProposal;
import de.walware.ecommons.ltk.ui.sourceediting.assist.IContentAssistComputer;
import de.walware.ecommons.ltk.ui.sourceediting.assist.SimpleCompletionProposal;

import de.walware.docmlet.wikitext.core.ast.WikitextAstNode;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.model.IWikidocModelInfo;
import de.walware.docmlet.wikitext.core.model.IWikitextSourceElement;
import de.walware.docmlet.wikitext.core.model.WikitextNameAccess;
import de.walware.docmlet.wikitext.core.source.MarkupLanguageDocumentSetupParticipant;
import de.walware.docmlet.wikitext.ui.WikitextUI;
import de.walware.docmlet.wikitext.ui.sourceediting.IMarkupCompletionExtension;


public class MarkupLabelCompletionComputer implements IContentAssistComputer, IMarkupCompletionExtension {
	
	
	private static class LabelCompletionProposal extends SimpleCompletionProposal
			implements ICompletionProposalExtension6 {
		
		
		private final IWikitextSourceElement refElement;
		
		
		public LabelCompletionProposal(final AssistInvocationContext context,
				final String replacementString, final int replacementOffset,
				final IWikitextSourceElement refElement) {
			super(context, replacementString, replacementOffset);
			
			this.refElement= refElement;
		}
		
		
		@Override
		protected String getPluginId() {
			return WikitextUI.PLUGIN_ID;
		}
		
		@Override
		public StyledString getStyledDisplayString() {
			final StyledString styledString= new StyledString(getDisplayString());
			if (this.refElement != null ) {
				final String name= this.refElement.getElementName().getDisplayName();
				if (name != null) {
					styledString.append(" (", StyledString.DECORATIONS_STYLER); //$NON-NLS-1$
					if ((this.refElement.getElementType() & IWikitextSourceElement.MASK_C2) == IWikitextSourceElement.C2_SECTIONING) {
						styledString.append("h:" + (this.refElement.getElementType() & 0xf) + " ");
					}
					styledString.append(name, StyledString.DECORATIONS_STYLER);
					styledString.append(')', StyledString.DECORATIONS_STYLER);
				}
			}
			return styledString;
		}
		
		@Override
		public boolean isAutoInsertable() {
			return false;
		}
		
		@Override
		protected int computeReplacementLength(final int replacementOffset, final Point selection,
				final int caretOffset, final boolean overwrite) throws BadLocationException {
			int end= Math.max(caretOffset, selection.x + selection.y);
			if (overwrite) {
				final IDocument document= getInvocationContext().getDocument();
				end--;
				while (++end < document.getLength()) {
					final char c= document.getChar(end);
					if (Character.isLetterOrDigit(c) || c == '-' || c == '_' || c == ':' || c == '.') {
						continue;
					}
					break;
				}
			}
			return (end - replacementOffset);
		}
		
	}
	
	
	private IMarkupLanguage markupLanguage;
	
	private IMarkupCompletionExtension completionExtension;
	
	
	@Override
	public void sessionStarted(final ISourceEditor editor, final ContentAssist assist) {
		this.markupLanguage= MarkupLanguageDocumentSetupParticipant.getMarkupLanguage(
				editor.getViewer().getDocument(), editor.getDocumentContentInfo().getPartitioning() );
		if (this.markupLanguage != null) {
			this.completionExtension= (IMarkupCompletionExtension) Platform.getAdapterManager().getAdapter(this.markupLanguage, IMarkupCompletionExtension.class);
		}
	}
	
	@Override
	public void sessionEnded() {
		this.markupLanguage= null;
		this.completionExtension= null;
	}
	
	protected IMarkupLanguage getMarkupLanguage() {
		return this.markupLanguage;
	}
	
	
	@Override
	public IStatus computeCompletionProposals(final AssistInvocationContext context, final int mode,
			final AssistProposalCollector proposals, final IProgressMonitor monitor) {
		final IMarkupLanguage markupLanguage= this.markupLanguage;
		final IMarkupCompletionExtension ext= (this.completionExtension != null) ? this.completionExtension : this;
		final IWikidocModelInfo modelInfo= (IWikidocModelInfo) context.getModelInfo();
		
		{	final CompletionType type= ext.getLinkAnchorLabel(context, markupLanguage);
			if (type != null) {
				addLabelProposals(context, type, modelInfo.getLinkAnchorLabels(), proposals);
			}
		}
		{	final CompletionType type= ext.getLinkRefLabel(context, markupLanguage);
			if (type != null) {
				addLabelProposals(context, type, modelInfo.getLinkRefLabels(), proposals);
			}
		}
		
		return Status.OK_STATUS;
	}
	
	@Override
	public IStatus computeInformationProposals(final AssistInvocationContext context,
			final AssistProposalCollector proposals, final IProgressMonitor monitor) {
		return null;
	}
	
	
	private void addLabelProposals(final AssistInvocationContext context,
			final CompletionType type, final INameAccessSet<WikitextNameAccess> labels,
			final AssistProposalCollector proposals) {
		for (final String label : labels.getNames()) {
			if (label.startsWith(type.getLookupPrefix())) {
				final ImList<WikitextNameAccess> accessList= labels.getAllInUnit(label);
				if (accessList.size() == 1
						&& isCurrent(accessList.get(0).getNameNode(), context.getInvocationOffset()) ) {
					continue;
				}
				
				WikitextNameAccess defAccess= null;
				IWikitextSourceElement defElement= null;
				ITER_ACCESS: for (final WikitextNameAccess access : accessList) {
					if (access.isWriteAccess()) {
						for (final Object attachment : access.getNode().getAttachments()) {
							if (attachment instanceof IWikitextSourceElement) {
								defAccess= access;
								defElement= (IWikitextSourceElement) attachment;
								break ITER_ACCESS;
							}
						}
						if (defAccess == null) {
							defAccess= access;
						}
					}
				}
				if (defAccess == null) {
					defAccess= accessList.get(0);
				}
				proposals.add(createProposal(defAccess.getDisplayName(), context,
						type.getSourcePrefix(), defElement ));
			}
		}
	}
	
	private boolean isCurrent(final WikitextAstNode node, final int offset) {
		return (node != null
				&& node.getOffset() <= offset && node.getEndOffset() >= offset);
	}
	
	
	protected IAssistCompletionProposal createProposal(final String name,
			final AssistInvocationContext context, final String prefix,
			final IWikitextSourceElement element) {
		return new LabelCompletionProposal(context, name,
				context.getInvocationOffset() - prefix.length(), element );
	}
	
	@Override
	public CompletionType getLinkAnchorLabel(final AssistInvocationContext context,
			final IMarkupLanguage markupLanguage) {
		try {
			final IDocument document= context.getDocument();
			final int endOffset= context.getOffset();
			int beginOffset= endOffset;
			while (beginOffset > 0) {
				final char c= document.getChar(--beginOffset);
				if (Character.isLetterOrDigit(c) || c == '-' || c == '_' || c == ':' || c == '.') {
					continue;
				}
				if (c == '#') {
					beginOffset++;
					return new CompletionType(document.get(beginOffset, endOffset - beginOffset));
				}
			}
		}
		catch (final BadLocationException e) {}
		return null;
	}
	
	@Override
	public CompletionType getLinkRefLabel(final AssistInvocationContext context,
			final IMarkupLanguage markupLanguage) {
		return null;
	}
	
}
