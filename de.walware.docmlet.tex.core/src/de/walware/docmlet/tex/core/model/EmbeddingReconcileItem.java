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

package de.walware.docmlet.tex.core.model;

import de.walware.ecommons.ltk.core.model.IEmbeddingReconcileItem;
import de.walware.ecommons.ltk.core.model.ISourceStructElement;

import de.walware.docmlet.tex.core.ast.Embedded;
import de.walware.docmlet.tex.internal.core.model.LtxSourceElement.EmbeddedRef;


public class EmbeddingReconcileItem implements IEmbeddingReconcileItem<Embedded, ITexSourceElement> {
	
	
	private final Embedded node;
	private final EmbeddedRef element;
	
	
	public EmbeddingReconcileItem(final Embedded node, final EmbeddedRef element) {
		this.node = node;
		this.element = element;
	}
	
	
	@Override
	public String getForeignTypeId() {
		return this.node.getForeignTypeId();
	}
	
	@Override
	public Embedded getAstNode() {
		return this.node;
	}
	
	@Override
	public ITexSourceElement getModelRefElement() {
		return this.element;
	}
	
	@Override
	public void setModelTypeElement(final ISourceStructElement element) {
		if (element != null) {
			this.element.setForeign(element);
		}
	}
	
}
