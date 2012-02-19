/*******************************************************************************
 * Copyright (c) 2009-2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.core.ast;

import java.lang.reflect.InvocationTargetException;


public class TexAstVisitor {
	
	
	public void visit(final SourceComponent node) throws InvocationTargetException {
		node.acceptInTexChildren(this);
	}
	
	public void visit(final Group node) throws InvocationTargetException {
		node.acceptInTexChildren(this);
	}
	
	public void visit(final Environment node) throws InvocationTargetException {
		node.acceptInTexChildren(this);
	}
	
	public void visit(final ControlNode node) throws InvocationTargetException {
		node.acceptInTexChildren(this);
	}
	
	public void visit(final Text node) throws InvocationTargetException {
	}
	
	public void visit(final Label node) throws InvocationTargetException {
	}
	
	public void visit(final Math node) throws InvocationTargetException {
		node.acceptInTexChildren(this);
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
