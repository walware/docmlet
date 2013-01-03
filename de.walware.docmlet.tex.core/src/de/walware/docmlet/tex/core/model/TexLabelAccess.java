/*******************************************************************************
 * Copyright (c) 2012-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.core.model;

import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;

import de.walware.docmlet.tex.core.ast.TexAstNode;


public abstract class TexLabelAccess extends TexElementName {
	
	
	public static final Comparator<TexLabelAccess> NAME_POSITION_COMPARATOR = 
			new Comparator<TexLabelAccess>() {
				@Override
				public int compare(final TexLabelAccess o1, final TexLabelAccess o2) {
					return (o1.getNameNode().getOffset() - o2.getNameNode().getOffset()); 
			}
	};
	
	
	public static Position getTextPosition(final TexAstNode node) {
		return new Position(node.getOffset(), node.getLength());
	}
	
	public static IRegion getTextRegion(final TexAstNode node) {
		return new Region(node.getOffset(), node.getLength());
	}
	
	
	protected TexLabelAccess() {
	}
	
	
	public abstract TexAstNode getNode();
	
	public abstract TexAstNode getNameNode();
	
	public abstract List<? extends TexLabelAccess> getAllInUnit();
	
	
	public abstract boolean isWriteAccess();
	
}
