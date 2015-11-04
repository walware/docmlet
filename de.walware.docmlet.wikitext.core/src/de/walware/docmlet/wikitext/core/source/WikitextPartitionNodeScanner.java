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

package de.walware.docmlet.wikitext.core.source;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.Locator;

import de.walware.ecommons.ltk.core.SourceContent;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNode;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNodeScan;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNodeScan.BreakException;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNodeScanner;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNodeType;

import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.markup.IWikitextLocator;
import de.walware.docmlet.wikitext.core.markup.MarkupParser2;


public class WikitextPartitionNodeScanner extends DocumentBuilder
		implements ITreePartitionNodeScanner {
	
	
	private IMarkupLanguage markupLanguage;
	
	private final int markupLanguageMode;
	
	private ITreePartitionNodeScan scan;
	
	/** The current node */
	private ITreePartitionNode node;
	
	private int beginOffset;
	private int endOffset;
	
	private IWikitextLocator locator2;
	
	private int ignoreCounter;
	
	
	
	public WikitextPartitionNodeScanner(final IMarkupLanguage markupLanguage) {
		this(markupLanguage, 0);
	}
	
	public WikitextPartitionNodeScanner(final IMarkupLanguage markupLanguage,
			final int markupLanguageMode) {
		this.markupLanguageMode= markupLanguageMode;
		setMarkupLanguage(markupLanguage);
	}
	
	
	public IMarkupLanguage getMarkupLanguage() {
		return this.markupLanguage;
	}
	
	public void setMarkupLanguage(final IMarkupLanguage markupLanguage) {
		if (this.markupLanguage != null
				&& this.markupLanguage.getName() == markupLanguage.getName()
				&& this.markupLanguage.getClass() == markupLanguage.getClass()) {
			this.markupLanguage.setMarkupConfig(markupLanguage.getMarkupConfig());
			if (this.markupLanguage.equals(markupLanguage)) {
				return;
			}
		}
		this.markupLanguage= markupLanguage.clone("Doc/Partitioner", this.markupLanguageMode); //$NON-NLS-1$
	}
	
	protected boolean isTemplateMode() {
		return ((this.markupLanguageMode & IMarkupLanguage.TEMPLATE_MODE) != 0);
	}
	
	
	@Override
	public void setLocator(final Locator locator) {
		super.setLocator(locator);
		this.locator2= (IWikitextLocator) locator;
	}
	
	@Override
	public int getRestartOffset(ITreePartitionNode node, final IDocument document,
			int offset) throws BadLocationException {
		final WikitextPartitionNodeType rootType= getRootType();
		ITreePartitionNode parent= node.getParent();
		if (parent != null) {
			while (parent.getType() != rootType) {
				node= parent;
				parent= node.getParent();
			}
			
			// start at line start, but never inside a child
			int idx= parent.indexOfChild(node);
			while (true) {
				final int line= document.getLineOfOffset(node.getOffset());
				offset= document.getLineOffset(line);
				if (idx > 0) {
					node= parent.getChild(--idx);
					if (offset < node.getOffset() + node.getLength()) {
						continue;
					}
				}
				break;
			}
		}
		return offset;
	}
	
	@Override
	public WikitextPartitionNodeType getRootType() {
		return WikitextPartitionNodeType.DEFAULT_ROOT;
	}
	
	@Override
	public void execute(final ITreePartitionNodeScan scan) throws BreakException {
		this.scan= scan;
		
		setRange(scan.getBeginOffset(), scan.getEndOffset());
		
		this.node= null;
		
		init();
		
		process();
	}
	
	protected ITreePartitionNodeScan getScan() {
		return this.scan;
	}
	
	protected void setRange(final int beginOffset, final int endOffset) {
		this.beginOffset= beginOffset;
		this.endOffset= endOffset;
//		this.reader.setRange(getScan().getDocument(), beginOffset, endOffset - beginOffset);
//		updateLast();
	}
	
	protected void init() {
		final ITreePartitionNode beginNode= getScan().getBeginNode();
		if (beginNode.getType() instanceof WikitextPartitionNodeType) {
			this.node= beginNode;
		}
		else {
			this.node= beginNode;
			addNode(getRootType(), getScan().getBeginOffset());
		}
	}
	
	
	protected final int getBeginOffset() {
		return this.beginOffset;
	}
	
	protected final void initNode(final ITreePartitionNode node) {
		if (this.node != null) {
			throw new IllegalStateException();
		}
		this.node= node;
	}
	
	protected final void addNode(final ITreePartitionNodeType type, final int offset) {
		this.node= this.scan.add(type, this.node, offset);
	}
	
	protected final ITreePartitionNode getNode() {
		return this.node;
	}
	
	protected final void exitNode(final int offset) {
		this.scan.expand(this.node, offset, true);
		this.node= this.node.getParent();
	}
	
	protected final void exitNode() {
		this.node= this.node.getParent();
	}
	
	protected final ITreePartitionNodeType getType() {
		return this.node.getType();
	}
	
	
	private void process() {
		try {
			final DocumentBuilder builder= this;
//			final DocumentBuilder builder= new MultiplexingDocumentBuilder(new EventLoggingDocumentBuilder(), this);
			final MarkupParser2 markupParser= new MarkupParser2(this.markupLanguage, builder);
			configure(markupParser);
			final SourceContent content= new SourceContent(0,
					this.scan.getDocument().get(this.beginOffset, this.endOffset - this.beginOffset),
					this.beginOffset );
			
			markupParser.parse(content, false);
		}
		catch (final BadLocationException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void configure(final MarkupParser2 markupParser) {
		markupParser.disable(MarkupParser2.GENERATIVE_CONTENT);
		markupParser.enable(MarkupParser2.SOURCE_STRUCT);
		markupParser.disable(MarkupParser2.INLINE_ALL);
	}
	
	
	protected final int getEventBeginOffset() {
		return this.beginOffset + this.locator2.getBeginOffset();
	}
	
	protected final int getEventEndOffset() {
		return this.beginOffset + this.locator2.getEndOffset();
	}
	
	
	@Override
	public void beginDocument() {
	}
	
	@Override
	public void endDocument() {
	}
	
	@Override
	public void beginBlock(final BlockType type, final Attributes attributes) {
		if (this.ignoreCounter > 0 || ignore(type)) {
			this.ignoreCounter++;
			return;
		}
		addNode(WikitextPartitionNodeType.BLOCK_TYPES.get(type), getEventBeginOffset());
	}
	
	private boolean ignore(final BlockType type) {
		switch (type) {
		case DEFINITION_ITEM:
		case LIST_ITEM:
		case TABLE_CELL_HEADER:
		case TABLE_CELL_NORMAL:
		case TABLE_ROW:
			return true;
		default:
			return (this.node != null
					&& this.node.getType() instanceof WikitextPartitionNodeType
					&& ((WikitextPartitionNodeType) this.node.getType()).getBlockType() == BlockType.QUOTE );
		}
	}
	
	@Override
	public void endBlock() {
		if (this.ignoreCounter > 0) {
			this.ignoreCounter--;
			return;
		}
		exitNode(getEventEndOffset());
	}
	
	@Override
	public void beginSpan(final SpanType type, final Attributes attributes) {
		this.scan.expand(this.node, getEventBeginOffset(), false);
	}
	
	@Override
	public void endSpan() {
		this.scan.expand(this.node, getEventEndOffset(), false);
	}
	
	@Override
	public void beginHeading(final int level, final Attributes attributes) {
		addNode(WikitextPartitionNodeType.HEADING_TYPES.get(level), getEventBeginOffset());
	}
	
	@Override
	public void endHeading() {
		exitNode(getEventEndOffset());
	}
	
	@Override
	public void characters(final String text) {
	}
	
	@Override
	public void charactersUnescaped(final String literal) {
	}
	
	@Override
	public void entityReference(final String entity) {
	}
	
	@Override
	public void image(final Attributes attributes, final String url) {
	}
	
	@Override
	public void link(final Attributes attributes, final String hrefOrHashName, final String text) {
	}
	
	@Override
	public void imageLink(final Attributes linkAttributes, final Attributes imageAttributes,
			final String href, final String imageUrl) {
	}
	
	@Override
	public void acronym(final String text, final String definition) {
	}
	
	@Override
	public void lineBreak() {
	}
	
}
