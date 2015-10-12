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

import java.lang.reflect.InvocationTargetException;


public class WikitextAstVisitor {
	
	
	public void visit(final SourceComponent node) throws InvocationTargetException {
		node.acceptInWikitextChildren(this);
	}
	
	public void visit(final Block node) throws InvocationTargetException {
		node.acceptInWikitextChildren(this);
	}
	
	public void visit(final Heading node) throws InvocationTargetException {
		node.acceptInWikitextChildren(this);
	}
	
	public void visit(final Span node) throws InvocationTargetException {
		node.acceptInWikitextChildren(this);
	}
	
	public void visit(final Text node) throws InvocationTargetException {
	}
	
	public void visit(final Link node) throws InvocationTargetException {
		node.acceptInWikitextChildren(this);
	}
	
	public void visit(final Image node) throws InvocationTargetException {
	}
	
	public void visit(final Label node) throws InvocationTargetException {
	}
	
	public void visit(final Control node) throws InvocationTargetException {
	}
	
	
	public void visit(final Verbatim node) throws InvocationTargetException {
	}
	
	public void visit(final Comment node) throws InvocationTargetException {
	}
	
	public void visit(final Dummy node) throws InvocationTargetException {
	}
	
	public void visit(final Embedded node) throws InvocationTargetException {
	}
	
}
