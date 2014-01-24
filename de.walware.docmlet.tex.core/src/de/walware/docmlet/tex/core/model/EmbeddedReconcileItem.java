/*=============================================================================#
 # Copyright (c) 2009-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.model;

import de.walware.ecommons.ltk.ISourceStructElement;

import de.walware.docmlet.tex.core.ast.Embedded;
import de.walware.docmlet.tex.internal.core.model.LtxSourceElement;
import de.walware.docmlet.tex.internal.core.model.LtxSourceElement.EmbeddedRef;


public class EmbeddedReconcileItem {
	
	
	private final Embedded fNode;
	private final LtxSourceElement.EmbeddedRef fElement;
	
	
	public EmbeddedReconcileItem(final Embedded node, final EmbeddedRef element) {
		fNode = node;
		fElement = element;
	}
	
	
	public String getTypeId() {
		return fNode.getText();
	}
	
	public Embedded getAstNode() {
		return fNode;
	}
	
	public ILtxSourceElement getModelRefElement() {
		return fElement;
	}
	
	public void setModelTypeElement(final ISourceStructElement element) {
		fElement.setForeign(element);
	}
	
}
