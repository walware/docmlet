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

package de.walware.docmlet.wikitext.internal.ui.sourceediting;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Point;

import de.walware.ecommons.collections.ImList;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistProposalCollector;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssist;
import de.walware.ecommons.ltk.ui.sourceediting.assist.IAssistCompletionProposal;
import de.walware.ecommons.ltk.ui.sourceediting.assist.IAssistInformationProposal;
import de.walware.ecommons.ltk.ui.sourceediting.assist.IContentAssistComputer;
import de.walware.ecommons.ltk.ui.sourceediting.assist.SimpleCompletionProposal;

import de.walware.docmlet.wikitext.core.ast.WikitextAstNode;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.model.IWikidocModelInfo;
import de.walware.docmlet.wikitext.core.model.IWikitextSourceElement;
import de.walware.docmlet.wikitext.core.source.MarkupLanguageDocumentSetupParticipant;
import de.walware.docmlet.wikitext.ui.WikitextUI;


public class MarkupLabelAssistComputer implements IContentAssistComputer {
	
	
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
	
	
	@Override
	public void sessionStarted(final ISourceEditor editor, final ContentAssist assist) {
		this.markupLanguage= MarkupLanguageDocumentSetupParticipant.getMarkupLanguage(
				editor.getViewer().getDocument(), editor.getDocumentContentInfo().getPartitioning() );
	}
	
	@Override
	public void sessionEnded() {
		this.markupLanguage= null;
	}
	
	protected IMarkupLanguage getMarkupLanguage() {
		return this.markupLanguage;
	}
	
	
	@Override
	public IStatus computeCompletionProposals(final AssistInvocationContext context, final int mode,
			final AssistProposalCollector<IAssistCompletionProposal> proposals, final IProgressMonitor monitor) {
		final String prefix= extractPrefix(context);
		if (prefix == null) {
			return null;
		}
		
		final IWikidocModelInfo modelInfo= (IWikidocModelInfo) context.getModelInfo();
		final Map<String, WikitextAstNode> labels= modelInfo.getLabels();
		for (final Entry<String, WikitextAstNode> entry : labels.entrySet()) {
			if (entry.getKey().startsWith(prefix)) {
				final ImList<Object> attachments= entry.getValue().getAttachments();
				IWikitextSourceElement element= null;
				for (final Object attachment : attachments) {
					if (attachment instanceof IWikitextSourceElement) {
						element= (IWikitextSourceElement) attachment;
						break;
					}
				}
				proposals.add(createProposal(entry.getKey(), context, prefix, element));
			}
		}
		
		return Status.OK_STATUS;
	}
	
	@Override
	public IStatus computeContextInformation(final AssistInvocationContext context,
			final AssistProposalCollector<IAssistInformationProposal> proposals, final IProgressMonitor monitor) {
		return null;
	}
	
	
	protected String extractPrefix(final AssistInvocationContext context) {
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
					return document.get(beginOffset, endOffset - beginOffset);
				}
			}
		}
		catch (final BadLocationException e) {}
		return null;
	}
	
	protected IAssistCompletionProposal createProposal(final String name,
			final AssistInvocationContext context, final String prefix,
			final IWikitextSourceElement element) {
		return new LabelCompletionProposal(context, name,
				context.getInvocationOffset() - prefix.length(), element );
	}
	
}
