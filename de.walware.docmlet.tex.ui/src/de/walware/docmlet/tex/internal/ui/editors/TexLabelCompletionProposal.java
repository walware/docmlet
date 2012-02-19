/*******************************************************************************
 * Copyright (c) 2009-2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.internal.ui.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import de.walware.ecommons.ltk.LTK;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;
import de.walware.ecommons.ltk.ui.sourceediting.assist.CompletionProposalWithOverwrite;

import de.walware.docmlet.tex.core.model.TexLabelAccess;
import de.walware.docmlet.tex.core.text.LtxHeuristicTokenScanner;
import de.walware.docmlet.tex.internal.ui.TexUIPlugin;


public class TexLabelCompletionProposal extends CompletionProposalWithOverwrite
		implements ICompletionProposalExtension6 {
	
	
	static final class ApplyData {
		
		private final AssistInvocationContext fContext;
		private final SourceViewer fViewer;
		private final IDocument fDocument;
		
		private LtxHeuristicTokenScanner fScanner;
		
		ApplyData(final AssistInvocationContext context) {
			fContext = context;
			fViewer = context.getSourceViewer();
			fDocument = fViewer.getDocument();
		}
		
		public SourceViewer getViewer() {
			return fViewer;
		}
		
		public IDocument getDocument() {
			return fDocument;
		}
		
		public LtxHeuristicTokenScanner getScanner() {
			if (fScanner == null) {
				fScanner = (LtxHeuristicTokenScanner) LTK.getModelAdapter(
						fContext.getEditor().getModelTypeId(), LtxHeuristicTokenScanner.class );
			}
			return fScanner;
		}
		
	}
	
	
	protected final TexLabelAccess fAccess;
	protected StyledString fDisplayString;
	
	private final int fRelevance;
	
	/** The cursor position after this proposal has been applied. */
	private int fCursorPosition = -1;
	
	private ApplyData fApplyData;
	
	
	protected TexLabelCompletionProposal(final AssistInvocationContext context, final int startOffset,
			final TexLabelAccess access, final int relevance) {
		super(context, startOffset);
		fAccess = access;
		fRelevance = relevance;
	}
	
	
	@Override
	protected String getPluginId() {
		return TexUIPlugin.PLUGIN_ID;
	}
	
	@Override
	public int getRelevance() {
		return fRelevance;
	}
	
	@Override
	public String getSortingString() {
		return fAccess.getSegmentName();
	}
	
	@Override
	public String getDisplayString() {
		return getStyledDisplayString().getString();
	}
	
	@Override
	public Image getImage() {
		return null;
//		return TexImages.getImageRegistry().get(key);
	}
	
	@Override
	public StyledString getStyledDisplayString() {
		if (fDisplayString == null) {
			final StyledString s = new StyledString(fAccess.getDisplayName());
			fDisplayString = s;
		}
		return fDisplayString;
	}
	
	protected final ApplyData getApplyData() {
		if (fApplyData == null) {
			fApplyData = new ApplyData(fContext);
		}
		return fApplyData;
	}
	
	@Override
	protected int computeReplacementLength(final int replacementOffset, final Point selection, final int caretOffset, final boolean overwrite) throws BadLocationException {
		int end = Math.max(caretOffset, selection.x + selection.y);
		if (overwrite) {
			final ApplyData data = getApplyData();
			final IDocument document = data.getDocument();
			end--;
			SEARCH_END: while (++end < document.getLength()) {
				final char c = document.getChar(end);
				if (c <= 0x20 || c == '\\' || c == '{' || c == '}' || c == '%'
						|| Character.isWhitespace(c)) {
					break SEARCH_END;
				}
				continue SEARCH_END;
			}
		}
		return (end - replacementOffset);
	}
	
	@Override
	public boolean validate(final IDocument document, final int offset, final DocumentEvent event) {
		try {
			final int start = getReplacementOffset();
			final String prefix = document.get(start, offset - start);
			return prefix.regionMatches(true, 0, fAccess.getDisplayName(), 0, prefix.length());
		}
		catch (final BadLocationException e) {
			return false;
		}
	}
	
	@Override
	public String getAdditionalProposalInfo() {
		return null;
	}
	
	@Override
	public boolean isAutoInsertable() {
		return true;
	}
	
	@Override
	protected void doApply(final char trigger, final int stateMask, final int caretOffset,
			final int replacementOffset, final int replacementLength) throws BadLocationException {
		final ApplyData data = getApplyData();
		final IDocument document = data.getDocument();
		
		final StringBuilder replacement = new StringBuilder(fAccess.getDisplayName());
		final int cursor = replacement.length();
		
		document.replace(replacementOffset, replacementLength, replacement.toString());
		setCursorPosition(replacementOffset + cursor);
	}
	
	
	protected void setCursorPosition(final int offset) {
		fCursorPosition = offset;
	}
	
	
	@Override
	public Point getSelection(final IDocument document) {
		if (fCursorPosition >= 0) {
			return new Point(fCursorPosition, 0);
		}
		return null;
	}
	
	@Override
	public IContextInformation getContextInformation() {
		return null;
	}
	
}
