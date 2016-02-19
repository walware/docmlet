/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.commonmark.ui;

import java.util.Locale;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;

import de.walware.docmlet.wikitext.commonmark.core.ParseHelper;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.ui.sourceediting.IMarkupCompletionExtension;


public class CommonmarkCompletionExtension implements IMarkupCompletionExtension {
	
	
	private final ParseHelper helper= new ParseHelper();
	
	
	public CommonmarkCompletionExtension() {
	}
	
	
	@Override
	public CompletionType getLinkAnchorLabel(final AssistInvocationContext context, final IMarkupLanguage markupLanguage) {
		try {
			final IDocument document= context.getDocument();
			final int endOffset= context.getOffset();
			int beginOffset= endOffset;
			while (beginOffset > 0) {
				final char c= document.getChar(--beginOffset);
				if (Character.isLetterOrDigit(c) || c == '-' || c == '_' || c == ':' || c == '.') {
					continue;
				}
				if (c == '#' && beginOffset > 0 && document.getChar(beginOffset - 1) != '\\') {
					beginOffset++;
					return new CompletionType(document.get(beginOffset, endOffset - beginOffset));
				}
				break;
			}
		}
		catch (final BadLocationException e) {}
		return null;
	}
	
	@Override
	public CompletionType getLinkRefLabel(final AssistInvocationContext context, final IMarkupLanguage markupLanguage) {
		try {
			final IDocument document= context.getDocument();
			final int endOffset= context.getInvocationOffset();
			int beginOffset= endOffset;
			final int lookupBound= Math.max(beginOffset - 1000, 0);
			while (beginOffset > lookupBound) {
				final char c= document.getChar(--beginOffset);
				if (c == '[' && !isEscaped(document, beginOffset)) {
					beginOffset= forwardBlank(document, ++beginOffset, endOffset);
					final String sourcePrefix= document.get(beginOffset, endOffset - beginOffset);
					final String lookupPrefix= normalizeLabel(sourcePrefix);
					return new CompletionType(sourcePrefix, lookupPrefix);
				}
				if (c != '\n' || c != '\r') {
					continue;
				}
				break;
			}
		}
		catch (final BadLocationException e) {}
		return null;
	}
	
	private boolean isEscaped(final IDocument document, int offset) throws BadLocationException {
		int count= 0;
		while (offset > 0) {
			if (document.getChar(--offset) == '\\') {
				count++;
			}
			else {
				break;
			}
		}
		return (count % 2) == 1;
	}
	
	private int forwardBlank(final IDocument document, int offset, final int endOffset) throws BadLocationException {
		while (offset < endOffset) {
			final char c= document.getChar(offset);
			if (c == ' ' || c == '\t') {
				offset++;
				continue;
			}
			break;
		}
		return offset;
	}
	
	private String normalizeLabel(String label) {
		if (label.isEmpty()) {
			return "";
		}
		label= this.helper.collapseWhitespace(label);
		if (label.isEmpty()) {
			return "";
		}
		return label.toLowerCase(Locale.ROOT);
	}
	
}
