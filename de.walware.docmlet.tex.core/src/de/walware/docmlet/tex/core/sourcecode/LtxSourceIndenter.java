/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.sourcecode;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import de.walware.ecommons.ltk.ast.AstSelection;
import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.text.IndentUtil;
import de.walware.ecommons.text.IndentUtil.IndentEditAction;

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.core.TexCodeStyleSettings;
import de.walware.docmlet.tex.core.TexCore;
import de.walware.docmlet.tex.core.ast.TexAst.NodeType;
import de.walware.docmlet.tex.core.ast.TexAstNode;


public class LtxSourceIndenter {
	
	private TexCodeStyleSettings fCodeStyle;
	
	private AbstractDocument fDocument;
	private TexAstNode fRootNode;
	
	private int fRefLine;
	private int fFirstLine;
	private int fLastLine;
	
	private IndentUtil fUtil;
	
	private int[] fLineColumns;
	
	
	public void setup(final ITexCoreAccess coreAccess) {
		fCodeStyle = coreAccess.getTexCodeStyle();
	}
	
	private void init(final AbstractDocument document, final TexAstNode node,
			final int firstLine, final int lastLine) throws BadLocationException {
		if (document == null) {
			throw new NullPointerException("document");
		}
		if (node == null) {
			throw new NullPointerException("node");
		}
		if (firstLine < 0 || lastLine < 0 || firstLine > lastLine) {
			throw new IllegalArgumentException("line index");
		}
		fDocument = document;
		fRootNode = node;
		fFirstLine = firstLine;
		fLastLine = lastLine;
		
		final int count = fDocument.getNumberOfLines(0, fDocument.getLineOffset(fLastLine));
		fLineColumns = new int[count + 2];
		Arrays.fill(fLineColumns, -1);
		
		fUtil = new IndentUtil(document, fCodeStyle);
	}
	
	public void clear() {
		fDocument = null;
		fRootNode = null;
		fCodeStyle = null;
		fUtil = null;
		fLineColumns = null;
	}
	
	
	public int getNewIndentColumn(final int line) throws BadLocationException {
		return fLineColumns[line];
	}
	
	public int getNewIndentOffset(final int line) {
		try {
			return fUtil.getIndentedOffsetAt(line, fLineColumns[line]);
		} catch (final BadLocationException e) {
			return -1;
		}
	}
	
	public TextEdit getIndentEdits(final AbstractDocument document, final TexAstNode node,
			final int codeOffset, final int firstLine, final int lastLine) throws CoreException {
		try {
			init(document, node, firstLine, lastLine);
			computeIndent(codeOffset);
			return createEdits();
		}
		catch (final BadLocationException e) {
			throw createFailedException(e);
		}
	}
	
	protected void computeIndent(final int codeOffset)
			throws BadLocationException {
		fRefLine = fFirstLine - 1;
		if (fRefLine >= 0) {
			fLineColumns[fRefLine] = fUtil.getLineIndent(fRefLine, false)[IndentUtil.COLUMN_IDX];
			for (int i = fRefLine + 1; i < fFirstLine; i++) {
				fLineColumns[i] = fLineColumns[fRefLine];
			}
		}
		
		int line = fFirstLine;
		while (line <= fLastLine) {
			boolean addEnv = false;
			if (line > 0 && fCodeStyle.getIndentEnvDepth() > 0) {
				fLineColumns[line] = fLineColumns[line - 1];
				final IRegion prevLine = fDocument.getLineInformation(line - 1);
				final IRegion currentLine = fDocument.getLineInformation(line);
				final int currentLineStop = currentLine.getOffset() + currentLine.getLength();
				IAstNode node = AstSelection.search(fRootNode,
						prevLine.getOffset()+prevLine.getLength(),
						prevLine.getOffset()+prevLine.getLength(),
						AstSelection.MODE_COVERING_SAME_LAST ).getCovering();
				while (node != null && !(node instanceof TexAstNode)) {
					node = node.getParent();
				}
				if (node != null) {
					TexAstNode texNode = (TexAstNode) node;
					final Set<String> envs = fCodeStyle.getIndentEnvLabels();
					while (texNode != null
							&& (texNode.getOffset() >= prevLine.getOffset()
									|| texNode.getStopOffset() <= currentLineStop )) {
						if (texNode.getNodeType() == NodeType.ENVIRONMENT) {
							if (envs == null || envs.contains(texNode.getText())) {
								if (texNode.getOffset() >= prevLine.getOffset()
										&& texNode.getStopOffset() > currentLineStop ) {
									addEnv = true;
								}
								else if (texNode.getOffset() < prevLine.getOffset()
										&& texNode.getStopOffset() <= currentLineStop ) {
									final int beginLine = fDocument.getLineOfOffset(texNode.getOffset());
									if (fLineColumns[beginLine] < 0) {
										fLineColumns[beginLine] = fUtil.getLineIndent(beginLine, false)[IndentUtil.COLUMN_IDX];
									}
									fLineColumns[line] = fLineColumns[beginLine];
								}
								break;
							}
						}
						texNode = texNode.getParent();
					}
				}
				if (addEnv) {
					fLineColumns[line] += fCodeStyle.getIndentEnvDepth() * fUtil.getLevelColumns();
				}
				line ++;
			}
		}
	}
	
	protected MultiTextEdit createEdits() throws BadLocationException, CoreException {
		final MultiTextEdit edits = new MultiTextEdit();
		final IndentEditAction action = new IndentEditAction() {
			@Override
			public int getIndentColumn(final int line, final int lineOffset) throws BadLocationException {
				return getNewIndentColumn(line);
			}
			@Override
			public void doEdit(final int line, final int offset, final int length, final StringBuilder text)
					throws BadLocationException {
				if (text != null) {
					edits.addChild(new ReplaceEdit(offset, length, text.toString()));
				}
			}
		};
		fUtil.changeIndent(fFirstLine, fLastLine, action);
		return edits;
	}
	
	protected CoreException createFailedException(final Throwable e) {
		return new CoreException(new Status(Status.ERROR, TexCore.PLUGIN_ID, -1, "Indentation failed", e));
	}
	
}