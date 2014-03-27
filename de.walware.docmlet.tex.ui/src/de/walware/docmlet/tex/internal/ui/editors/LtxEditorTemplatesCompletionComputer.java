/*=============================================================================#
 # Copyright (c) 2008-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.ui.editors;

import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.templates.TemplateContextType;

import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;
import de.walware.ecommons.ltk.ui.sourceediting.assist.TemplatesCompletionComputer;

import de.walware.docmlet.tex.core.source.ITexDocumentConstants;
import de.walware.docmlet.tex.internal.ui.TexUIPlugin;


public class LtxEditorTemplatesCompletionComputer extends TemplatesCompletionComputer {
	
	
	public LtxEditorTemplatesCompletionComputer() {
		super(TexUIPlugin.getDefault().getTexEditorTemplateStore(), TexUIPlugin.getDefault().getTexEditorTemplateContextTypeRegistry());
	}
	
	
	@Override
	protected String extractPrefix(final AssistInvocationContext context) {
		try {
			final IDocument document = context.getSourceViewer().getDocument();
			final int end = context.getInvocationOffset();
			final int start = Math.max(end-50, 0);
			final String text = document.get(start, end-start);
			int i = text.length()-1;
			while (i >= 0) {
				final char c = text.charAt(i);
				if (Character.isLetterOrDigit(c) || c == '.' || c == '_') {
					i--;
					continue;
				}
				if (c == '\\') {
					return text.substring(i);
				}
				return text.substring(i+1);
			}
		}
		catch (final BadLocationException e) {}
		return ""; //$NON-NLS-1$
	}
	
	@Override
	protected TemplateContextType getContextType(final AssistInvocationContext context, final IRegion region) {
		try {
			final ISourceEditor editor = context.getEditor();
			final AbstractDocument document = (AbstractDocument) context.getSourceViewer().getDocument();
			final ITypedRegion partition = document.getPartition(editor.getPartitioning().getPartitioning(), region.getOffset(), true);
			if (partition.getType() == ITexDocumentConstants.LTX_MATH_CONTENT_TYPE) {
				return getTypeRegistry().getContextType(LtxEditorContextType.LTX_MATH_CONTEXT_TYPE_ID);
			}
			else {
				return getTypeRegistry().getContextType(LtxEditorContextType.LTX_DEFAULT_CONTEXT_TYPE_ID);
			}
		}
		catch (final BadPartitioningException e) {} 
		catch (final BadLocationException e) {}
		return null;
	}
	
}
