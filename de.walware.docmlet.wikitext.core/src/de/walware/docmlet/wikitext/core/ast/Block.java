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

package de.walware.docmlet.wikitext.core.ast;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.text.IRegion;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

import de.walware.jcommons.collections.ImList;

import de.walware.docmlet.wikitext.core.ast.WikitextAst.NodeType;


public abstract class Block extends ContainerNode {
	
	
	static final class TextBlock extends Block {
		
		
		private final ImList<? extends IRegion> textRegions;
		
		TextBlock(final WikitextAstNode parent, final int offset, final BlockType blockType,
				final String label, final ImList<? extends IRegion> textRegions) {
			super(parent, offset, blockType, label);
			this.textRegions= textRegions;
		}
		
		
		@Override
		public ImList<? extends IRegion> getTextRegions() {
			return this.textRegions;
		}
		
	}
	
	static final class Common extends Block {
		
		Common(final WikitextAstNode parent, final int offset, final BlockType blockType,
				final String label) {
			super(parent, offset, blockType, label);
		}
		
		
		@Override
		public ImList<? extends IRegion> getTextRegions() {
			return null;
		}
		
	}
	
	
	private final BlockType blockType;
	
	private final String label;
	
	
	private Block(final WikitextAstNode parent, final int offset, final BlockType blockType,
			final String label) {
		super(parent, offset, offset);
		
		this.blockType= blockType;
		this.label= label;
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.BLOCK;
	}
	
	public BlockType getBlockType() {
		return this.blockType;
	}
	
	@Override
	public String getLabel() {
		return this.label;
	}
	
	public abstract ImList<? extends IRegion> getTextRegions();
	
	
	@Override
	public void acceptInWikitext(final WikitextAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	
}
