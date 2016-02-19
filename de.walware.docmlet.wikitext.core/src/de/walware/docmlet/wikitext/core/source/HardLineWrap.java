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

package de.walware.docmlet.wikitext.core.source;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.text.IndentUtil;
import de.walware.ecommons.text.core.sections.IDocContentSections;

import de.walware.docmlet.wikitext.core.IWikitextCoreAccess;
import de.walware.docmlet.wikitext.core.ast.Block;
import de.walware.docmlet.wikitext.core.ast.Control;
import de.walware.docmlet.wikitext.core.ast.Heading;
import de.walware.docmlet.wikitext.core.ast.SourceComponent;
import de.walware.docmlet.wikitext.core.ast.Span;
import de.walware.docmlet.wikitext.core.ast.Text;
import de.walware.docmlet.wikitext.core.ast.WikitextAstVisitor;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.source.extdoc.IExtdocMarkupLanguage;


public class HardLineWrap {
	
	
	public static final byte SELECTION_STRICT= 1;
	public static final byte SELECTION_WITH_TAIL= 2;
	public static final byte SELECTION_MERGE1= 3;
	public static final byte SELECTION_MERGE= 4;
	public static final byte PARAGRAPH= 5;
	
	
	private static final class BlockData {
		
		
		private final Block node;
		
		private final ImList<LineData> lines;
		
		private final String indentCont;
		
		
		public BlockData(final Block node, final ImList<LineData> lines, final String indentCont) {
			this.node= node;
			this.lines= lines;
			this.indentCont= indentCont;
		}
		
	}
	
	private static final class LineData {
		
		private static final byte HARD_LINE_BREAK= 1;
		
		
		private final int beginOffset;
		private final int endOffset;
		
		private final String textSource;
		
		private final List<Text> textNodes;
		
		private byte end;
		
		
		public LineData(final int beginOffset, final int endOffset, final String textSource) {
			this.beginOffset= beginOffset;
			this.endOffset= endOffset;
			this.textSource= textSource;
			this.textNodes= new ArrayList<>(8);
		}
		
		
		@Override
		public String toString() {
			final StringBuilder sb= new StringBuilder();
			sb.append('[');
			sb.append(this.beginOffset);
			sb.append(", "); //$NON-NLS-1$
			sb.append(this.endOffset);
			sb.append("): "); //$NON-NLS-1$
			sb.append(this.textSource);
			return sb.toString();
		}
		
	}
	
	
	private static final class Task extends WikitextAstVisitor {
		
		
		private final byte mode;
		
		private final IDocument document;
		
		private final IMarkupSourceFormatAdapter formatAdapter;
		private final SourceComponent sourceNode;
		private final IRegion region;
		
		private final List<BlockData> blocks= new ArrayList<>();
		
		private boolean createLineContent;
		private final List<LineData> lines= new ArrayList<>();
		private int currentLineIdx;
		
		private final int lineWidth;
		private final IndentUtil indentUtil;
		
		
		public Task(final byte mode, final IDocument document,
				final IRegion region,
				final IMarkupSourceFormatAdapter sourceAdapter, final SourceComponent sourceNode,
				final int lineWidth, final IndentUtil indentUtil) {
			this.mode= mode;
			this.document= document;
			this.formatAdapter= sourceAdapter;
			this.sourceNode= sourceNode;
			this.region= region;
			this.lineWidth= lineWidth;
			this.indentUtil= indentUtil;
		}
		
		
		@Override
		public void visit(final Block node) throws InvocationTargetException {
			if (this.createLineContent || !TextUtilities.overlaps(this.region, node)) {
				return;
			}
			switch (node.getBlockType()) {
			case PARAGRAPH:
				createTextBlockNode(node);
				return;
			case QUOTE:
			case NUMERIC_LIST:
			case BULLETED_LIST:
			case LIST_ITEM:
			case DEFINITION_LIST:
			case DEFINITION_ITEM:
				node.acceptInWikitextChildren(this);
				return;
			default:
				return;
			}
		}
		
		private void createTextBlockNode(final Block node) throws InvocationTargetException {
			try {
				this.lines.clear();
				final ImList<? extends IRegion> textRegions= node.getTextRegions();
				{	int i= 0;
					for (; i < textRegions.size(); i++) {
						final IRegion textRegion= textRegions.get(i);
						if (textRegion.getOffset() + textRegion.getLength() <= this.region.getOffset()) {
							continue;
						}
						else {
							break;
						}
					}
					for (; i < textRegions.size(); i++) {
						final IRegion textRegion= textRegions.get(i);
						if (this.mode >= SELECTION_MERGE1
								|| TextUtilities.overlaps(this.region, textRegion) ) {
							this.lines.add(new LineData(
									textRegion.getOffset(), textRegion.getOffset() + textRegion.getLength(),
									this.document.get(textRegion.getOffset(), textRegion.getLength()) ));
						}
						else {
							break;
						}
					}
				}
				if (!this.lines.isEmpty()) {
					final String indentCont= getBlockWrapIndent(node);
					if (indentCont != null) {
						this.createLineContent= true;
						this.currentLineIdx= 0;
						node.acceptInWikitextChildren(this);
						
						this.blocks.add(new BlockData(node, ImCollections.toList(this.lines), indentCont));
					}
				}
			}
			catch (final Exception e) {
				throw new InvocationTargetException(e);
			}
			finally {
				this.createLineContent= false;
			}
		}
		
		
		@Override
		public void visit(final Heading node) throws InvocationTargetException {
		}
		
		@Override
		public void visit(final Span node) throws InvocationTargetException {
			if (!this.createLineContent) {
				return;
			}
			switch (node.getSpanType()) {
			case CODE:
				return;
			default:
				node.acceptInWikitextChildren(this);
				return;
			}
		}
		
		@Override
		public void visit(final Text node) throws InvocationTargetException {
			if (!this.createLineContent) {
				return;
			}
			if (node.getLength() > 0) {
				while (this.currentLineIdx < this.lines.size()) {
					final LineData lineData= this.lines.get(this.currentLineIdx);
					if (node.getEndOffset() <= lineData.beginOffset) {
						return;
					}
					if (node.getOffset() < lineData.endOffset) {
						lineData.textNodes.add(node);
					}
					if (node.getEndOffset() > lineData.endOffset) {
						this.currentLineIdx++;
					}
					else {
						break;
					}
				}
			}
		}
		
		@Override
		public void visit(final Control node) throws InvocationTargetException {
			if (!this.createLineContent) {
				return;
			}
			if (node.getText() == Control.LINE_BREAK) {
				while (this.currentLineIdx < this.lines.size()) {
					final LineData lineData= this.lines.get(this.currentLineIdx);
					if (node.getEndOffset() <= lineData.beginOffset) {
						return;
					}
					if (node.getOffset() < lineData.endOffset) {
						lineData.end= LineData.HARD_LINE_BREAK;
						break;
					}
					else {
						this.currentLineIdx++;
					}
				}
			}
		}
		
		
		public void collect() throws Exception {
			try {
				this.sourceNode.acceptInWikitextChildren(this);
				
				if (this.blocks.isEmpty()) {
					return;
				}
			}
			catch (final InvocationTargetException e) {
				throw (Exception) e.getTargetException();
			}
		}
		
		private String getBlockWrapIndent(final Block node) throws Exception {
			return this.formatAdapter.getPrefixCont(node, this.indentUtil);
		}
		
		
		private final static byte TEXT= 0;
		private final static byte ESCAPE= 1;
		private final static byte BREAK= 2;
		
		private IRegion trimText(final LineData lineData) {
			int beginIdx= 0;
			int endIdx= lineData.endOffset - lineData.beginOffset;
			ITER_OFFSET: for (; endIdx > beginIdx; endIdx--) {
				switch (lineData.textSource.charAt(endIdx - 1)) {
				case '\n':
				case '\r':
					continue ITER_OFFSET;
				default:
					break ITER_OFFSET;
				}
			}
			if (!lineData.textNodes.isEmpty()) {
				{	final Text node= lineData.textNodes.get(0);
					if (node.getOffset() <= lineData.beginOffset) {
						final int bound= Math.min(node.getEndOffset() - lineData.beginOffset, endIdx);
						ITER_OFFSET: for (; beginIdx < bound; beginIdx++) {
							switch (lineData.textSource.charAt(beginIdx)) {
							case ' ':
							case '\t':
								continue ITER_OFFSET;
							default:
								break ITER_OFFSET;
							}
						}
					}
				}
				{	final Text node= lineData.textNodes.get(lineData.textNodes.size() - 1);
					if (node.getEndOffset() >= lineData.endOffset) {
						final int bound= Math.max(node.getOffset() - lineData.beginOffset, beginIdx);
						final int savedOffset= endIdx;
						ITER_OFFSET: for (; endIdx > bound; endIdx--) {
							switch (lineData.textSource.charAt(endIdx - 1)) {
							case ' ':
							case '\t':
								continue ITER_OFFSET;
							default:
								break ITER_OFFSET;
							}
						}
						if (endIdx < savedOffset) {
							int count= 0;
							ITER_OFFSET: for (; endIdx - count > bound; count++) {
								switch (lineData.textSource.charAt(endIdx - count - 1)) {
								case '\\':
									continue ITER_OFFSET;
								default:
									break ITER_OFFSET;
								}
							}
							if (count % 2 == 1) {
								endIdx++;
							}
						}
					}
				}
			}
			return new Region(beginIdx, endIdx - beginIdx);
		}
		
		/**
		 * 
		 * @param lineData the line
		 * @param beginIdx begin index in line
		 * @param endIdx end index (exclusive) in line
		 * @param beginColumn column at beginIdx
		 * @param fallback
		 * @return region of break in line
		 */
		private IRegion getBreak(final LineData lineData, final int beginIdx, final int endIdx,
				final int beginColumn, final boolean fallback) {
			int brIdx= -1;
			int brLength= 0;
			byte state= TEXT;
			
			int chIdx= beginIdx;
			int column= beginColumn;
			for (; chIdx < endIdx && (column <= this.lineWidth || (fallback && brIdx < 0) );
					chIdx++) {
				switch (lineData.textSource.charAt(chIdx)) {
				case ' ':
					if (isTextOffset(lineData, chIdx)) {
						switch (state) {
						case TEXT:
							brIdx= chIdx;
							brLength= 1;
							state= BREAK;
							break;
						case ESCAPE:
							state= TEXT;
							break;
						case BREAK:
							brLength++;
							break;
						}
					}
					else {
						state= TEXT;
					}
					column++;
					continue;
				case '\t':
					if (isTextOffset(lineData, chIdx)) {
						switch (state) {
						case TEXT:
							brIdx= chIdx;
							brLength= 1;
							state= BREAK;
							break;
						case ESCAPE:
							state= TEXT;
							break;
						case BREAK:
							brLength++;
							break;
						}
					}
					else {
						state= TEXT;
					}
					column+= this.indentUtil.getTabWidth() - (column % this.indentUtil.getTabWidth());
					continue;
				case '\\':
					if (isTextOffset(lineData, chIdx)) {
						switch (state) {
						case ESCAPE:
							state= TEXT;
							break;
						default:
							state= ESCAPE;
							break;
						}
					}
					else {
						state= TEXT;
					}
					column++;
					continue;
				default:
					state= TEXT;
					column++;
					continue;
				}
			}
			if (chIdx == endIdx && column <= this.lineWidth) {
				return new Region(endIdx, 0);
			}
			return (brIdx >= 0) ? new Region(brIdx, brLength) : null;
		}
		
		private boolean isTextOffset(final LineData lineData, final int chIdx) {
			final int offset= lineData.beginOffset + chIdx;
			for (final Text node : lineData.textNodes) {
				if (node.getOffset() > offset) {
					break;
				}
				if (node.getEndOffset() > offset) {
					return true;
				}
			}
			return false;
		}
		
	}
	
	
	private final IDocContentSections documentContentInfo;
	
	private final IWikitextCoreAccess wikitextCoreAccess;
	
	
	public HardLineWrap(final IDocContentSections documentContentInfo, final IWikitextCoreAccess coreAccess) {
		this.documentContentInfo= documentContentInfo;
		this.wikitextCoreAccess= coreAccess;
	}
	
	
	public IWikitextCoreAccess getWikitextCoreAccess() {
		return this.wikitextCoreAccess;
	}
	
	public void addTextEdits(final IDocument document, final SourceComponent sourceNode,
			final IRegion region, final byte mode, final IMarkupSourceFormatAdapter formatAdapter,
			final TextEdit rootEdit,
			IndentUtil indentUtil) throws Exception {
		if (indentUtil != null) {
			if (indentUtil.getDocument() != document) {
				throw new IllegalArgumentException("indentUtil.document != document"); //$NON-NLS-1$
			}
		}
		else {
			indentUtil= new IndentUtil(document, this.wikitextCoreAccess.getWikitextCodeStyle());
		}
		final Task task= new Task(mode, document, region,
				formatAdapter, sourceNode,
				this.wikitextCoreAccess.getWikitextCodeStyle().getLineWidth(), indentUtil );
		task.collect();
		
		processBlocks(task, rootEdit);
	}
	
	public TextEdit createTextEdit(final IDocument document, final SourceComponent sourceNode,
			final IRegion region, final byte mode, final IMarkupSourceFormatAdapter formatAdapter,
			final IndentUtil indentUtil) throws Exception {
		final MultiTextEdit rootEdit= new MultiTextEdit();
		addTextEdits(document, sourceNode, region, mode, formatAdapter, rootEdit, indentUtil);
		return (rootEdit.getChildrenSize() > 0) ? rootEdit : null;
	}
	
	public void addTextEdits(final IDocument document,
			final AstInfo ast, final IRegion region, final byte mode,
			final TextEdit rootEdit,
			final IndentUtil indentUtil) throws Exception {
		final IExtdocMarkupLanguage markupLanguage= getMarkupLanguage(document);
		final IMarkupSourceFormatAdapter formatAdapter;
		if (markupLanguage == null
				|| (formatAdapter= markupLanguage.getSourceFormatAdapter()) == null
				|| !(ast.root instanceof SourceComponent) ) {
			return;
		}
		
		addTextEdits(document, (SourceComponent) ast.root, region, mode, formatAdapter,
				rootEdit, indentUtil);
	}
	
	public TextEdit createTextEdit(final IDocument document,
			final AstInfo ast, final IRegion region, final byte mode,
			final IndentUtil indentUtil) throws Exception {
		final MultiTextEdit rootEdit= new MultiTextEdit();
		addTextEdits(document, ast, region, mode, rootEdit, indentUtil);
		return (rootEdit.getChildrenSize() > 0) ? rootEdit : null;
	}
	
	
	protected final IExtdocMarkupLanguage getMarkupLanguage(final IDocument document) {
		final IMarkupLanguage markupLanguage= WikidocDocumentSetupParticipant.getMarkupLanguage(document,
				this.documentContentInfo.getPartitioning() );
		return (markupLanguage instanceof IExtdocMarkupLanguage) ? (IExtdocMarkupLanguage) markupLanguage : null;
	}
	
	
	private void processBlocks(final Task task, final TextEdit rootEdit) throws BadLocationException {
		ITER_BLOCKS: for (final BlockData blockData : task.blocks) {
			String lineWrap= null;
			int lineWrapColumns= -1;
			int openOffset= -1;
			int beginColumn= -1;
			int endColumn= -1;
			byte lastChange= 1;
			boolean lineInRegion= true;
			ITER_LINES: for (final LineData lineData : blockData.lines) {
				if (lineInRegion) {
					lineInRegion= (lineData.beginOffset < task.region.getOffset() + task.region.getLength());
				}
				else if (task.mode <= SELECTION_MERGE && lastChange == 0) {
					break ITER_BLOCKS;
				}
				if (!lineInRegion && task.mode <= SELECTION_MERGE1 && lastChange <= 1) {
					break ITER_BLOCKS;
				}
				final IRegion textRegion= task.trimText(lineData);
				int textIdx= textRegion.getOffset();
				final int textEndIdx= textRegion.getOffset() + textRegion.getLength();
				String remainingSource= lineData.textSource;
				IRegion br= null;
				if (openOffset >= 0
						&& (br= task.getBreak(lineData, textIdx, textEndIdx, endColumn + 1, false)) != null) {
					if (task.mode <= SELECTION_WITH_TAIL
							&& !isInRegion(task.region, lineData.beginOffset + textRegion.getOffset())) {
						break ITER_BLOCKS;
					}
					
					if (task.document.getChar(openOffset) == ' ') {
						rootEdit.addChild(new DeleteEdit(
								openOffset + 1, lineData.beginOffset - openOffset - 1 ));
					}
					else {
						rootEdit.addChild(new ReplaceEdit(
								openOffset, lineData.beginOffset - openOffset, " " )); //$NON-NLS-1$
					}
					beginColumn= endColumn + 1;
					endColumn= task.indentUtil.getColumn(remainingSource, textEndIdx - textIdx, beginColumn);
					lastChange= 1;
				}
				else {
					beginColumn= task.indentUtil.getColumn(lineData.beginOffset + textIdx);
					endColumn= task.indentUtil.getColumn(remainingSource, textEndIdx - textIdx, beginColumn);
					lastChange= 0;
					if (beginColumn >= task.lineWidth) {
						openOffset= -1;
						continue ITER_LINES;
					}
				}
				while (endColumn > task.lineWidth) {
					if (br == null) {
						br= task.getBreak(lineData, textIdx, textEndIdx, beginColumn, true);
					}
					if (br == null || br.getLength() == 0) {
						break;
					}
					if (task.mode <= SELECTION_STRICT && !isInRegion(task.region, lineData.beginOffset + br.getOffset())) {
						break ITER_BLOCKS;
					}
					
					if (lineWrap == null) {
						lineWrapColumns= task.indentUtil.getColumn(blockData.indentCont, blockData.indentCont.length());
						lineWrap= TextUtilities.getDefaultLineDelimiter(task.document) + blockData.indentCont;
					}
					rootEdit.addChild(new ReplaceEdit(
							lineData.beginOffset + br.getOffset(), br.getLength(), lineWrap ));
					textIdx= br.getOffset() + br.getLength();
					remainingSource= lineData.textSource.substring(textIdx);
					beginColumn= lineWrapColumns;
					endColumn= task.indentUtil.getColumn(remainingSource, textEndIdx - textIdx, beginColumn);
					lastChange= 2;
					
					br= null;
				}
				openOffset= (endColumn < task.lineWidth && lineData.end != LineData.HARD_LINE_BREAK) ?
						(lineData.beginOffset + textEndIdx) : -1;
			}
		}
	}
	
	private boolean isInRegion(final IRegion region, final int offset) {
		return (offset >= region.getOffset()
				&& offset < region.getOffset() + region.getLength() );
	}
	
}
