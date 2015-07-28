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

import java.util.EnumMap;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

import de.walware.ecommons.collections.IntArrayMap;
import de.walware.ecommons.collections.IntMap;
import de.walware.ecommons.text.core.treepartitioner.AbstractPartitionNodeType;


public class WikitextPartitionNodeType extends AbstractPartitionNodeType {
	
	
	public static final WikitextPartitionNodeType DEFAULT_ROOT= new WikitextPartitionNodeType();
	
	
	public static class Block extends WikitextPartitionNodeType {
		
		
		private final BlockType blockType;
		
		
		public Block(final BlockType blockType) {
			this.blockType= blockType;
		}
		
		
		@Override
		public BlockType getBlockType() {
			return this.blockType;
		}
		
		
		@Override
		public String toString() {
			return getPartitionType() + ":" + this.blockType;
		}
		
	}
	
//	public static class BlockWithStyle extends Block {
//		
//		
//		private final String cssStyle;
//		
//		
//		public BlockWithStyle(BlockType blockType, String cssStyle) {
//			super(blockType);
//			this.cssStyle= cssStyle;
//		}
//		
//		
//		public String getCssStyle() {
//			return this.cssStyle;
//		}
//		
//		
//		@Override
//		public int hashCode() {
//			return getBlockType().hashCode() * getCssStyle().hashCode();
//		}
//		
//		@Override
//		public boolean equals(Object obj) {
//			if (this == obj) {
//				return true;
//			}
//			if (!(obj instanceof BlockWithStyle)) {
//				return false;
//			}
//			final BlockWithStyle other= (BlockWithStyle) obj;
//			return getBlockType() == other.getBlockType()
//					&& getCssStyle().equals(other.getCssStyle() );
//		}
//		
//	}
	
	public static final EnumMap<BlockType, Block> BLOCK_TYPES= new EnumMap<>(BlockType.class);
	static {
		for (final BlockType blockType : BlockType.values()) {
			BLOCK_TYPES.put(blockType, new Block(blockType));
		}
	}
	
	
	public static class Heading extends WikitextPartitionNodeType {
		
		
		private final int level;
		
		
		public Heading(final int level) {
			this.level= level;
		}
		
		
		public int getHeadingLevel() {
			return this.level;
		}
		
		
		@Override
		public String toString() {
			return getPartitionType() + ":HEADING-" + this.level;
		}
		
	}
	
	
	public static final IntMap<Heading> HEADING_TYPES= new IntArrayMap<>();
	static {
		for (int level= 1; level <= 6; level++) {
			HEADING_TYPES.put(level, new Heading(level));
		}
	}
	
	
	public WikitextPartitionNodeType() {
	}
	
	
	@Override
	public String getPartitionType() {
		return IWikitextDocumentConstants.WIKIDOC_DEFAULT_CONTENT_TYPE;
	}
	
	
	public BlockType getBlockType() {
		return null;
	}
	
}
