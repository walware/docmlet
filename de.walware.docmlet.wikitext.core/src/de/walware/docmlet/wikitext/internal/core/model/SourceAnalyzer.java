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

package de.walware.docmlet.wikitext.internal.core.model;

import static de.walware.docmlet.wikitext.core.model.IWikitextElement.C2_SECTIONING;
import static de.walware.ecommons.ltk.core.model.IModelElement.MASK_C1;
import static de.walware.ecommons.ltk.core.model.IModelElement.MASK_C2;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.Region;

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.core.impl.NameAccessAccumulator;
import de.walware.ecommons.ltk.core.impl.NameAccessSet;
import de.walware.ecommons.ltk.core.model.INameAccessSet;

import de.walware.docmlet.wikitext.core.ast.Block;
import de.walware.docmlet.wikitext.core.ast.Embedded;
import de.walware.docmlet.wikitext.core.ast.Heading;
import de.walware.docmlet.wikitext.core.ast.Image;
import de.walware.docmlet.wikitext.core.ast.Label;
import de.walware.docmlet.wikitext.core.ast.Link;
import de.walware.docmlet.wikitext.core.ast.SourceComponent;
import de.walware.docmlet.wikitext.core.ast.Span;
import de.walware.docmlet.wikitext.core.ast.Text;
import de.walware.docmlet.wikitext.core.ast.WikitextAstNode;
import de.walware.docmlet.wikitext.core.ast.WikitextAstVisitor;
import de.walware.docmlet.wikitext.core.model.EmbeddingReconcileItem;
import de.walware.docmlet.wikitext.core.model.IWikitextSourceElement;
import de.walware.docmlet.wikitext.core.model.IWikitextSourceUnit;
import de.walware.docmlet.wikitext.core.model.WikitextElementName;
import de.walware.docmlet.wikitext.core.model.WikitextNameAccess;
import de.walware.docmlet.wikitext.internal.core.model.WikitextSourceElement.EmbeddedRef;


public class SourceAnalyzer extends WikitextAstVisitor {
	
	
	private static final Integer ONE= 1;
	
	
	private String input;
	
	private WikitextSourceElement.Container currentElement;
	
	private final StringBuilder titleBuilder= new StringBuilder();
	private boolean titleDoBuild;
	private WikitextSourceElement.Container titleElement;
	private final Map<String, Integer> structNamesCounter= new HashMap<>();
	
	private final List<EmbeddingReconcileItem> embeddedItems= new ArrayList<>();
	
	private int minSectionLevel;
	private int maxSectionLevel;
	
	private Map<String, NameAccessAccumulator<WikitextNameAccess>> linkAnchorLabels= new HashMap<>();
	private Map<String, NameAccessAccumulator<WikitextNameAccess>> linkDefLabels= new HashMap<>();
	
	
	public void clear() {
		this.input= null;
		this.currentElement= null;
		
		this.titleBuilder.setLength(0);
		this.titleDoBuild= false;
		this.titleElement= null;
		
		this.embeddedItems.clear();
		
		this.minSectionLevel= Integer.MAX_VALUE;
		this.maxSectionLevel= Integer.MIN_VALUE;
	}
	
	public WikidocSourceUnitModelInfo createModel(final IWikitextSourceUnit su, final String input,
			final AstInfo ast) {
		clear();
		this.input= input;
		if (!(ast.root instanceof WikitextAstNode)) {
			return null;
		}
		
		if (!this.linkAnchorLabels.isEmpty()) {
			this.linkAnchorLabels.clear();
		}
		if (!this.linkDefLabels.isEmpty()) {
			this.linkDefLabels.clear();
		}
		final IWikitextSourceElement root= this.currentElement= new WikitextSourceElement.SourceContainer(
				IWikitextSourceElement.C2_SOURCE_FILE, su, (WikitextAstNode) ast.root);
		try {
			((WikitextAstNode) ast.root).acceptInWikitext(this);
			
			if (this.minSectionLevel == Integer.MAX_VALUE) {
				this.minSectionLevel= 0;
				this.maxSectionLevel= 0;
			}
			final INameAccessSet<WikitextNameAccess> linkAnchorLabels;
			final INameAccessSet<WikitextNameAccess> linkDefLabels;
			if (this.linkAnchorLabels.isEmpty()) {
				linkAnchorLabels= NameAccessSet.emptySet();
			}
			else {
				linkAnchorLabels= new NameAccessSet<>(this.linkAnchorLabels);
				this.linkAnchorLabels= new HashMap<>();
			}
			if (this.linkDefLabels.isEmpty()) {
				linkDefLabels= NameAccessSet.emptySet();
			}
			else {
				linkDefLabels= new NameAccessSet<>(this.linkDefLabels);
				this.linkDefLabels= new HashMap<>();
			}
			
			final WikidocSourceUnitModelInfo model= new WikidocSourceUnitModelInfo(ast, root,
					linkAnchorLabels, linkDefLabels,
					this.minSectionLevel, this.maxSectionLevel );
			return model;
		}
		catch (final InvocationTargetException e) {
			throw new IllegalStateException();
		}
	}
	
	public List<EmbeddingReconcileItem> getEmbeddedItems() {
		return this.embeddedItems;
	}
	
	
	private void initElement(final WikitextSourceElement.Container element) {
		if (this.currentElement.children.isEmpty()) {
			this.currentElement.children= new ArrayList<>();
		}
		this.currentElement.children.add(element);
		this.currentElement= element;
	}
	
	private void exitContainer(final int stop, final boolean forward) {
		this.currentElement.length= ((forward) ?
						readLinebreakForward((stop >= 0) ? stop : this.currentElement.offset + this.currentElement.length, this.input.length()) :
						readLinebreakBackward((stop >= 0) ? stop : this.currentElement.offset + this.currentElement.length, 0) ) -
				this.currentElement.offset;
		final List<WikitextSourceElement> children= this.currentElement.children;
		if (!children.isEmpty()) {
			for (final WikitextSourceElement element : children) {
				if ((element.getElementType() & MASK_C2) == C2_SECTIONING) {
					final Map<String, Integer> names= this.structNamesCounter;
					final String name= element.getElementName().getDisplayName();
					final Integer occ= names.get(name);
					if (occ == null) {
						names.put(name, ONE);
					}
					else {
						names.put(name, Integer.valueOf(
								(element.occurrenceCount= occ + 1) ));
					}
				}
			}
			this.structNamesCounter.clear();
		}
		this.currentElement= this.currentElement.getModelParent();
	}
	
	private void finishTitleText() {
		{	boolean wasWhitespace= false;
			int idx= 0;
			while (idx < this.titleBuilder.length()) {
				if (this.titleBuilder.charAt(idx) == ' ') {
					if (wasWhitespace) {
						this.titleBuilder.deleteCharAt(idx);
					}
					else {
						wasWhitespace= true;
						idx++;
					}
				}
				else {
					wasWhitespace= false;
					idx++;
				}
			}
		}
		this.titleElement.name= WikitextElementName.create(WikitextElementName.TITLE, this.titleBuilder.toString());
		this.titleBuilder.setLength(0);
		this.titleElement= null;
		this.titleDoBuild= false;
	}
	
	
	private int readLinebreakForward(int offset, final int limit) {
		if (offset < limit) {
			switch(this.input.charAt(offset)) {
			case '\n':
				if (++offset < limit && this.input.charAt(offset) == '\r') {
					return ++offset;
				}
				return offset;
			case '\r':
				if (++offset < limit && this.input.charAt(offset) == '\n') {
					return ++offset;
				}
				return offset;
			}
		}
		return offset;
	}
	private int readLinebreakBackward(int offset, final int limit) {
		if (offset > limit) {
			switch(this.input.charAt(offset-1)) {
			case '\n':
				if (--offset > limit && this.input.charAt(offset-1) == '\r') {
					return --offset;
				}
				return offset;
			case '\r':
				if (--offset < limit && this.input.charAt(offset-1) == '\n') {
					return --offset;
				}
				return offset;
			}
		}
		return offset;
	}
	
	private RefLabelAccess addLinkAnchorAccess(final WikitextAstNode node) {
		final String label= node.getLabel();
		NameAccessAccumulator<WikitextNameAccess> shared= this.linkAnchorLabels.get(label);
		if (shared == null) {
			shared= new NameAccessAccumulator<>(label);
			this.linkAnchorLabels.put(label, shared);
		}
		final RefLabelAccess access= new RefLabelAccess.LinkAnchor(shared, node, null);
		node.addAttachment(access);
		return access;
	}
	
	private RefLabelAccess addLinkDefAccess(final WikitextAstNode node, final Label labelNode) {
		final String label= labelNode.getText();
		NameAccessAccumulator<WikitextNameAccess> shared= this.linkDefLabels.get(label);
		if (shared == null) {
			shared= new NameAccessAccumulator<>(label);
			this.linkDefLabels.put(label, shared);
		}
		final RefLabelAccess access= new RefLabelAccess.LinkDef(shared, node, labelNode);
		node.addAttachment(access);
		return access;
	}
	
	
	@Override
	public void visit(final SourceComponent node) throws InvocationTargetException {
		this.currentElement.offset= node.getOffset();
		node.acceptInWikitextChildren(this);
		if (this.titleElement != null) {
			finishTitleText();
		}
		while ((this.currentElement.getElementType() & MASK_C1) != IWikitextSourceElement.C1_SOURCE) {
			exitContainer(node.getEndOffset(), true);
		}
		exitContainer(node.getEndOffset(), true);
	}
	
	@Override
	public void visit(final Block node) throws InvocationTargetException {
		if (node.getLabel() != null) {
			final RefLabelAccess access= addLinkAnchorAccess(node);
			access.flags|= RefLabelAccess.A_WRITE;
		}
		
		node.acceptInWikitextChildren(this);
		
		this.currentElement.length= node.getEndOffset() - this.currentElement.getOffset();
	}
	
	@Override
	public void visit(final Heading node) throws InvocationTargetException {
		if (node.getLabel() != null) {
			final RefLabelAccess access= addLinkAnchorAccess(node);
			access.flags|= RefLabelAccess.A_WRITE;
		}
		
		COMMAND: {
			if ((this.currentElement.getElementType() & MASK_C2) == IWikitextSourceElement.C2_SECTIONING
					|| (this.currentElement.getElementType() & MASK_C1) == IWikitextSourceElement.C1_SOURCE ) {
				final int level= node.getLevel();
				if (level > 5) {
					break COMMAND;
				}
				if (this.titleElement != null) {
					finishTitleText();
					break COMMAND;
				}
				
				while ((this.currentElement.getElementType() & MASK_C2) == IWikitextSourceElement.C2_SECTIONING
						&& (this.currentElement.getElementType() & 0xf) >= level) {
					exitContainer(node.getOffset(), false);
				}
				initElement(new WikitextSourceElement.StructContainer(
						IWikitextSourceElement.C2_SECTIONING | level, this.currentElement, node ));
				
				node.addAttachment(this.currentElement);
				
				this.minSectionLevel= Math.min(this.minSectionLevel, level);
				this.maxSectionLevel= Math.max(this.maxSectionLevel, level);
				
				final int count= node.getChildCount();
				if (count > 0) {
					this.titleElement= this.currentElement;
					this.titleDoBuild= true;
					
					final int nameOffset= node.getChild(0).getOffset();
					final int nameStopOffset= readLinebreakBackward(node.getChild(count - 1).getEndOffset(), nameOffset);
					this.titleElement.nameRegion= new Region(nameOffset, nameStopOffset - nameOffset);
					
					node.acceptInWikitextChildren(this);
					if (this.titleElement != null) {
						finishTitleText();
					}
				}
				else {
					this.currentElement.name= WikitextElementName.create(WikitextElementName.TITLE, ""); //$NON-NLS-1$
					this.currentElement.nameRegion= new Region(node.getOffset(), 0);
				}
				this.currentElement.length= Math.max(this.currentElement.length, node.getLength());
				return;
			}
		}
		
		node.acceptInWikitextChildren(this);
		
		this.currentElement.length= node.getEndOffset() - this.currentElement.getOffset();
	}
	
	@Override
	public void visit(final Span node) throws InvocationTargetException {
		if (node.getLabel() != null) {
			final RefLabelAccess access= addLinkAnchorAccess(node);
			access.flags|= RefLabelAccess.A_WRITE;
		}
		
		node.acceptInWikitextChildren(this);
	}
	
	@Override
	public void visit(final Text node) throws InvocationTargetException {
		if (this.titleDoBuild) {
			final String text= node.getText();
			if (text != null) {
				this.titleBuilder.append(text);
				if (this.titleBuilder.length() >= 100) {
					finishTitleText();
				}
			}
		}
		
		this.currentElement.length= node.getEndOffset() - this.currentElement.getOffset();
	}
	
	@Override
	public void visit(final Link node) throws InvocationTargetException {
		final RefLabelAccess access;
		switch (node.getLinkType()) {
		case Link.LINK_REF_DEFINITION:
			access= addLinkDefAccess(node, node.getReferenceLabel());
			access.flags|= RefLabelAccess.A_WRITE;
			break;
		case Link.LINK_BY_REF:
			access= addLinkDefAccess(node, node.getReferenceLabel());
			break;
		default:
			break;
		}
		super.visit(node);
	}
	
	@Override
	public void visit(final Image node) throws InvocationTargetException {
		final RefLabelAccess access;
		switch (node.getImageType()) {
		case Image.SRC_BY_REF:
			access= addLinkDefAccess(node, node.getReferenceLabel());
			break;
		default:
			break;
		}
		super.visit(node);
	}
	
	@Override
	public void visit(final Embedded node) throws InvocationTargetException {
		if (node.getForeignNode() == null) {
			super.visit(node);
			return;
		}
		if ((node.getEmbedDescr() & 0b0_00000011) == Embedded.EMBED_INLINE) {
			if (this.titleDoBuild) {
				this.titleBuilder.append(this.input, node.getOffset(), node.getEndOffset());
				if (this.titleBuilder.length() >= 100) {
					finishTitleText();
				}
			}
			this.embeddedItems.add(new EmbeddingReconcileItem(node, null));
		}
		else {
			if (this.titleElement != null) {
				finishTitleText();
			}
			if (this.currentElement.children.isEmpty()) {
				this.currentElement.children= new ArrayList<>();
			}
			final EmbeddedRef element= new WikitextSourceElement.EmbeddedRef(node.getText(),
					this.currentElement, node );
			element.offset= node.getOffset();
			element.length= node.getLength();
			element.name= WikitextElementName.create(0, ""); //$NON-NLS-1$
			this.currentElement.children.add(element);
			this.embeddedItems.add(new EmbeddingReconcileItem(node, element));
		}
		
		this.currentElement.length= node.getEndOffset() - this.currentElement.getOffset();
	}
	
}
