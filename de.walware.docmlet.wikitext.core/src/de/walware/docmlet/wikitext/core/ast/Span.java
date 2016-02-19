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

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;

import de.walware.docmlet.wikitext.core.ast.WikitextAst.NodeType;


public final class Span extends ContainerNode {
	
	
	private final SpanType spanType;
	
	private final String label;
	
	
	Span(final WikitextAstNode parent, final int beginOffset, final int endOffset,
			final SpanType spanType, final String label) {
		super(parent, beginOffset, endOffset);
		this.spanType= spanType;
		this.label= label;
	}
	
	
	@Override
	public NodeType getNodeType() {
		return NodeType.SPAN;
	}
	
	public SpanType getSpanType() {
		return this.spanType;
	}
	
	@Override
	public String getLabel() {
		return this.label;
	}
	
	
	@Override
	public void acceptInWikitext(final WikitextAstVisitor visitor) throws InvocationTargetException {
		visitor.visit(this);
	}
	
	
}
