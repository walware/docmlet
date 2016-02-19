/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.ui.sourceediting;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import de.walware.ecommons.ltk.IModelManager;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;

import de.walware.docmlet.tex.core.ast.ControlNode;
import de.walware.docmlet.tex.core.ast.Group;
import de.walware.docmlet.tex.core.ast.Label;
import de.walware.docmlet.tex.core.ast.TexAst;
import de.walware.docmlet.tex.core.ast.TexAstNode;
import de.walware.docmlet.tex.core.ast.Text;


public class LtxAssistInvocationContext extends AssistInvocationContext {
	
	
	private ControlNode fInvoControlNode;
	private TexAstNode[] fInvoArgNodes;
	private int fInvoArgIdx = -2;
	
	
	public LtxAssistInvocationContext(final ISourceEditor editor,
			final int offset, final String contentType,
			final boolean isProposal, final IProgressMonitor monitor) {
		super(editor, offset, contentType,
				(isProposal) ? IModelManager.MODEL_FILE : IModelManager.NONE, monitor );
	}
	
	
	@Override
	protected String computeIdentifierPrefix(int offset) {
		final IDocument document= getDocument();
		
		try {
			int start = offset;
			SEARCH_START: while (offset > 0) {
				final char c = document.getChar(offset - 1);
				switch (c) {
				case 'a':
				case 'b':
				case 'c':
				case 'd':
				case 'e':
				case 'f':
				case 'g':
				case 'h':
				case 'i':
				case 'j':
				case 'k':
				case 'l':
				case 'm':
				case 'n':
				case 'o':
				case 'p':
				case 'q':
				case 'r':
				case 's':
				case 't':
				case 'u':
				case 'v':
				case 'w':
				case 'x':
				case 'y':
				case 'z':
				case 'A':
				case 'B':
				case 'C':
				case 'D':
				case 'E':
				case 'F':
				case 'G':
				case 'H':
				case 'I':
				case 'J':
				case 'K':
				case 'L':
				case 'M':
				case 'N':
				case 'O':
				case 'P':
				case 'Q':
				case 'R':
				case 'S':
				case 'T':
				case 'U':
				case 'V':
				case 'W':
				case 'X':
				case 'Y':
				case 'Z':
					start = --offset;
					continue SEARCH_START;
				case '\\':
					start = offset;
					while (true) {
						if (--offset <= 0 || document.getChar(offset - 1) != '\\') {
							start--;
							break SEARCH_START;
						}
						if (--offset <= 0 || document.getChar(offset -1) != '\\') {
							break SEARCH_START;
						}
					}
				default:
					break SEARCH_START;
				}
			}
			return document.get(start, getInvocationOffset() - start);
		}
		catch (final BadLocationException e) {
			return ""; //$NON-NLS-1$
		}
	}
	
	
	private void computeInvArgInfo() {
		fInvoArgIdx = -1;
		if (getAstSelection().getCovering() instanceof TexAstNode) {
			TexAstNode texNode = (TexAstNode) getAstSelection().getCovering();
			if (texNode instanceof Label || texNode instanceof Text) {
				texNode= texNode.getTexParent();
			}
			final int offset = getInvocationOffset();
			if (texNode instanceof Group && texNode.getParent() instanceof ControlNode
					&& (fInvoControlNode = (ControlNode) texNode.getParent()).getCommand() != null) {
				fInvoArgNodes = TexAst.resolveArguments(fInvoControlNode);
				fInvoArgIdx = TexAst.getIndexAt(fInvoArgNodes, offset);
			}
		}
	}
	
	public ControlNode getInvocationControlNode() {
		if (fInvoArgIdx == -2) {
			computeInvArgInfo();
		}
		return fInvoControlNode;
	}
	
	public TexAstNode[] getInvocationArgNodes() {
		if (fInvoArgIdx == -2) {
			computeInvArgInfo();
		}
		return fInvoArgNodes;
	}
	
	public int getInvocationArgIdx() {
		if (fInvoArgIdx == -2) {
			computeInvArgInfo();
		}
		return fInvoArgIdx;
	}
	
}
