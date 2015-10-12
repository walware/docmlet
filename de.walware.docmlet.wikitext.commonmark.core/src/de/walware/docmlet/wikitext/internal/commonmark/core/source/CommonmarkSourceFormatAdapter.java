/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.commonmark.core.source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import de.walware.ecommons.text.IndentUtil;

import de.walware.docmlet.wikitext.core.ast.Block;
import de.walware.docmlet.wikitext.core.ast.WikitextAstNode;
import de.walware.docmlet.wikitext.core.source.IMarkupSourceFormatAdapter;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.blocks.BlockQuoteBlock;
import de.walware.docmlet.wikitext.internal.commonmark.core.blocks.ListBlock;


public class CommonmarkSourceFormatAdapter implements IMarkupSourceFormatAdapter {
	
	
	private final Matcher quoteMatcher= BlockQuoteBlock.PATTERN.matcher(""); //$NON-NLS-1$
	private final Matcher listItemMatcher= ListBlock.PATTERN.matcher(""); //$NON-NLS-1$
	
	private final StringBuilder indentBuilder= new StringBuilder();
	
	
	public CommonmarkSourceFormatAdapter() {
	}
	
	
	@Override
	public String getPrefixCont(WikitextAstNode node,
			final IndentUtil indentUtil) throws Exception {
		if (indentUtil.getTabWidth() != 4) {
			return null;
		}
		
		final List<Block> blocks= new ArrayList<>(8);
		ITER_NODE: for (; node != null; node= node.getWikitextParent()) {
			switch (node.getNodeType()) {
			case SOURCELINES:
				if (node.getParent() == null) {
					break ITER_NODE;
				}
				return null;
			case BLOCK:
				switch (((Block) node).getBlockType()) {
				case BULLETED_LIST:
				case NUMERIC_LIST:
					continue ITER_NODE;
				case PARAGRAPH:
				case QUOTE:
				case LIST_ITEM:
					blocks.add((Block) node);
					continue ITER_NODE;
				default:
					break;
				}
				return null;
			default:
				return null;
			}
		}
		
		switch (blocks.size()) {
		case 0:
			return null;
		case 1:
			break;
		default:
			Collections.reverse(blocks);
			break;
		}
		return createIndent(blocks, indentUtil);
	}
	
	private String createIndent(final List<Block> blocks,
			final IndentUtil indentUtil) throws Exception {
		this.indentBuilder.setLength(0);
		int column= 0;
		for (final Block block : blocks) {
			final Line nodeLine;
			final Matcher matcher;
			switch (block.getBlockType()) {
			case PARAGRAPH:
				break;
			case QUOTE:
				nodeLine= createLine(indentUtil, block.getOffset());
				matcher= nodeLine.setupIndent(this.quoteMatcher);
				if (!matcher.matches()) {
					return null;
				}
				indentUtil.appendIndent(this.indentBuilder, column,
						column= nodeLine.getIndent() );
				this.indentBuilder.append("> "); //$NON-NLS-1$
				break;
			case LIST_ITEM:
				nodeLine= createLine(indentUtil, block.getOffset());
				matcher= nodeLine.setupIndent(this.listItemMatcher);
				if (!matcher.matches()) {
					return null;
				}
				indentUtil.appendIndent(this.indentBuilder, column,
						column= ListBlock.computeItemLineIndent(nodeLine, matcher) );
				break;
			default:
				assert (false);
			}
		}
		return this.indentBuilder.toString();
	}
	
	private Line createLine(final IndentUtil indentUtil, final int offset)
			throws BadLocationException {
		final IDocument doc= indentUtil.getDocument();
		final int lineNum= doc.getLineOfOffset(offset);
		final IRegion lineInfo= doc.getLineInformation(lineNum);
		return new Line(lineNum, offset, indentUtil.getColumn(lineNum, offset),
				doc.get(offset, lineInfo.getOffset() + lineInfo.getLength() - offset), "\n"); //$NON-NLS-1$
	}
	
}
