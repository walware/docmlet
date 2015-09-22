/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.core.ast;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;
import org.eclipse.mylyn.wikitext.core.parser.Locator;

import de.walware.ecommons.ltk.core.SourceContent;
import de.walware.ecommons.string.IStringFactory;
import de.walware.ecommons.string.InternStringFactory;
import de.walware.ecommons.text.core.util.HtmlUtils;
import de.walware.ecommons.text.core.util.HtmlUtils.Entity;

import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.markup.IWikitextLocator;
import de.walware.docmlet.wikitext.core.markup.MarkupParser2;
import de.walware.docmlet.wikitext.core.source.EmbeddingAttributes;


public class WikidocParser extends DocumentBuilder {
	
	
	private final IStringFactory labelFactory;
	
	private IMarkupLanguage markupLanguage;
	
	private SourceContent content;
	
	private IWikitextLocator locator2;
	
	private WikitextAstNode currentNode;
	
	private boolean headingText;
	
	private int collectText;
	private Text currentText;
	private final StringBuilder textBuilder= new StringBuilder();
	
	private int depth;
	private final List<List<WikitextAstNode>> childrenStack= new ArrayList<>();
	
	private List<Embedded> embeddedList;
	
	
	public WikidocParser(final IStringFactory labelFactory) {
		this.labelFactory= (labelFactory != null) ? labelFactory : InternStringFactory.INSTANCE;
	}
	
	
	public void setMarkupLanguage(final IMarkupLanguage markupLanguage) {
		this.markupLanguage= markupLanguage;
	}
	
	@Override
	public void setLocator(final Locator locator) {
		super.setLocator(locator);
		this.locator2= (IWikitextLocator) locator;
	}
	
	public void setCollectEmebeddedNodes(final boolean enable) {
		this.embeddedList= (enable) ? new ArrayList<Embedded>(32) : null;
	}
	
	public List<Embedded> getEmbeddedNodes() {
		return this.embeddedList;
	}
	
	public void setCollectHeadingText(final boolean enable) {
		this.headingText= enable;
	}
	
	
	public SourceComponent parse(final SourceContent content) {
		try {
			if (this.embeddedList != null) {
				this.embeddedList.clear();
			}
			this.content= content;
			this.depth= -1;
			
			final MarkupParser2 markupParser= new MarkupParser2(this.markupLanguage, this);
			markupParser.disable(MarkupParser2.GENERATIVE_CONTENT);
			markupParser.enable(MarkupParser2.SOURCE_STRUCT);
			
			markupParser.parse(content, true);
			
			return (SourceComponent) this.currentNode;
		}
		finally {
			while (this.depth >= 0) {
				final List<WikitextAstNode> list= this.childrenStack.get(this.depth);
				list.clear();
				this.depth--;
			}
		}
	}
	
	
	private void addChildNode(final WikitextAstNode node) {
		final List<WikitextAstNode> children= this.childrenStack.get(this.depth);
		children.add(node);
	}
	
	private void finishNode() {
		final List<WikitextAstNode> children= this.childrenStack.get(this.depth);
		if (!children.isEmpty()) {
			((ContainerNode) this.currentNode).children= children.toArray(new WikitextAstNode[children.size()]);
			children.clear();
		}
	}
	
	private void enterNode(final WikitextAstNode node) {
		if (this.depth >= 0) {
			addChildNode(node);
		}
		
		this.depth++;
		this.currentNode= node;
		
		if (this.depth == this.childrenStack.size()) {
			this.childrenStack.add(new ArrayList<WikitextAstNode>());
		}
	}
	
	private void exitNode(final int offset) {
		finishNode();
		
//		if (offset < this.currentNode.stopOffset) {
//			System.out.println("endOffset: " + offset);
//		}
//		if (offset > this.currentNode.stopOffset) {
		this.currentNode.stopOffset= offset;
//		}
//		else {
//			offset= this.currentNode.stopOffset;
//		}
		this.currentNode= this.currentNode.parent;
//		if (offset > this.currentNode.stopOffset) {
//			this.currentNode.stopOffset= offset;
//		}
		this.depth--;
	}
	
	
	private void finishText() {
		if (this.currentText == null) {
			return;
		}
		this.currentText.text= this.textBuilder.toString();
		this.currentText= null;
	}
	
	
	@Override
	public void beginDocument() {
		enterNode(new SourceComponent(this.content.getText().length()));
	}
	
	@Override
	public void endDocument() {
		assert (this.currentNode instanceof SourceComponent);
		finishText();
		finishNode();
		
		this.depth--;
	}
	
	@Override
	public void beginBlock(final BlockType type, final Attributes attributes) {
		finishText();
		final WikitextAstNode node;
		if (attributes instanceof EmbeddingAttributes) {
			final EmbeddingAttributes embeddingAttributes= (EmbeddingAttributes) attributes;
			final Embedded embedded;
			node= embedded= new Embedded(this.currentNode,
					this.locator2.getBeginOffset(), this.locator2.getEndOffset(),
					embeddingAttributes.getForeignType(), embeddingAttributes.getEmbedDescr() );
			if (this.embeddedList != null) {
				this.embeddedList.add(embedded);
			}
		}
		else {
			node= new Block(this.currentNode, this.locator2.getBeginOffset(),
					type, attributes.getId() );
		}
		enterNode(node);
	}
	
	@Override
	public void endBlock() {
		finishText();
		exitNode(this.locator2.getEndOffset());
	}
	
	@Override
	public void beginSpan(final SpanType type, final Attributes attributes) {
		finishText();
		final WikitextAstNode node;
		if (attributes instanceof EmbeddingAttributes) {
			final EmbeddingAttributes embeddingAttributes= (EmbeddingAttributes) attributes;
			final Embedded embedded;
			node= embedded= new Embedded(this.currentNode,
					this.locator2.getBeginOffset(), this.locator2.getEndOffset(),
					embeddingAttributes.getForeignType(), embeddingAttributes.getEmbedDescr() );
			if (this.embeddedList != null) {
				this.embeddedList.add(embedded);
			}
		}
		else if (type == SpanType.LINK && attributes instanceof LinkAttributes) {
			node= new Link(this.currentNode,
					this.locator2.getBeginOffset(), this.locator2.getEndOffset(),
					((LinkAttributes) attributes).getHref() );
		}
		else {
			node= new Span(this.currentNode,
					this.locator2.getBeginOffset(), this.locator2.getEndOffset(),
					type, attributes.getId());
		}
		enterNode(node);
	}
	
	@Override
	public void endSpan() {
		finishText();
		exitNode(this.locator2.getEndOffset());
	}
	
	@Override
	public void beginHeading(final int level, final Attributes attributes) {
		finishText();
		enterNode(new Heading(this.currentNode, this.locator2.getBeginOffset(), level,
				attributes.getId() ));
		if (this.headingText) {
			this.collectText++;
		}
	}
	
	@Override
	public void endHeading() {
		finishText();
		if (this.headingText) {
			this.collectText--;
		}
		exitNode(this.locator2.getEndOffset());
	}
	
	@Override
	public void characters(final String text) {
		if (this.currentText == null) {
			this.currentText= new Text(this.currentNode,
					this.locator2.getBeginOffset(), this.locator2.getEndOffset() );
			addChildNode(this.currentText);
			this.textBuilder.setLength(0);
		}
		else if (this.locator2.getEndOffset() > this.currentText.stopOffset) {
			this.currentText.stopOffset= this.locator2.getEndOffset();
		}
		if (this.collectText > 0) {
			this.textBuilder.append(text);
		}
	}
	
	@Override
	public void entityReference(final String entity) {
		if (this.collectText > 0) {
			final Entity resolved= HtmlUtils.getEntity(HtmlUtils.getEntityName(entity));
			if (resolved != null) {
				characters(resolved.getString());
				return;
			}
		}
		characters(null);
	}
	
	@Override
	public void image(final Attributes attributes, final String url) {
		finishText();
	}
	
	@Override
	public void link(final Attributes attributes, final String hrefOrHashName, final String text) {
		finishText();
		addChildNode(new Link(this.currentNode,
				this.locator2.getBeginOffset(), this.locator2.getEndOffset(),
				hrefOrHashName));
	}
	
	@Override
	public void imageLink(final Attributes linkAttributes, final Attributes imageAttributes, final String href,
			final String imageUrl) {
		finishText();
	}
	
	@Override
	public void acronym(final String text, final String definition) {
		finishText();
	}
	
	@Override
	public void lineBreak() {
		if (this.collectText > 0 && this.currentText != null) {
			this.textBuilder.append('\n');
		}
	}
	
	@Override
	public void charactersUnescaped(final String literal) {
		finishText();
	}
	
}
