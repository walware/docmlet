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

package de.walware.docmlet.wikitext.internal.core.model;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.collections.ImList;
import de.walware.ecommons.ltk.core.impl.NameAccessAccumulator;

import de.walware.docmlet.wikitext.core.ast.WikitextAstNode;
import de.walware.docmlet.wikitext.core.model.WikitextElementName;
import de.walware.docmlet.wikitext.core.model.WikitextNameAccess;


public abstract class RefLabelAccess extends WikitextNameAccess {
	
	
	public final static int A_READ =                       0x00000000;
	public final static int A_WRITE =                      0x00000002;
	
	
	static final class LinkDef extends RefLabelAccess {
		
		protected LinkDef(final NameAccessAccumulator<WikitextNameAccess> shared, final WikitextAstNode node, final WikitextAstNode labelNode) {
			super(shared, node, labelNode);
		}
		
		@Override
		public int getType() {
			return LINK_DEF_LABEL;
		}
		
	}
	
	static final class LinkAnchor extends RefLabelAccess {
		
		protected LinkAnchor(final NameAccessAccumulator<WikitextNameAccess> shared, final WikitextAstNode node, final WikitextAstNode labelNode) {
			super(shared, node, labelNode);
		}
		
		@Override
		public int getType() {
			return LINK_ANCHOR_LABEL;
		}
		
	}
	
	
	private final NameAccessAccumulator<WikitextNameAccess> shared;
	
	private final WikitextAstNode node;
	private final WikitextAstNode nameNode;
	
	int flags;
	
	
	protected RefLabelAccess(final NameAccessAccumulator<WikitextNameAccess> shared, final WikitextAstNode node, final WikitextAstNode labelNode) {
		this.shared= shared;
		shared.getList().add(this);
		this.node = node;
		this.nameNode = labelNode;
	}
	
	
	@Override
	public String getSegmentName() {
		return this.shared.getLabel();
	}
	
	@Override
	public String getDisplayName() {
		return this.shared.getLabel();
	}
	
	@Override
	public WikitextElementName getNextSegment() {
		return null;
	}
	
	
	@Override
	public WikitextAstNode getNode() {
		return this.node;
	}
	
	@Override
	public WikitextAstNode getNameNode() {
		return this.nameNode;
	}
	
	@Override
	public ImList<? extends WikitextNameAccess> getAllInUnit() {
		return ImCollections.toList(this.shared.getList());
	}
	
	
	@Override
	public boolean isWriteAccess() {
		return ((this.flags & A_WRITE) != 0);
	}
	
}
