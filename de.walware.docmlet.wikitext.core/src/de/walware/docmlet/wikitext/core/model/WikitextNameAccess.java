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

package de.walware.docmlet.wikitext.core.model;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;

import de.walware.ecommons.ltk.core.model.INameAccess;

import de.walware.docmlet.wikitext.core.ast.WikitextAstNode;


public abstract class WikitextNameAccess extends WikitextElementName
		implements INameAccess<WikitextAstNode, WikitextNameAccess> {
	
	
	public static Position getTextPosition(final WikitextAstNode node) {
		return new Position(node.getOffset(), node.getLength());
	}
	
	public static IRegion getTextRegion(final WikitextAstNode node) {
		return new Region(node.getOffset(), node.getLength());
	}
	
	
	protected WikitextNameAccess() {
	}
	
	
}
