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

package de.walware.docmlet.tex.core.source;

import de.walware.jcommons.collections.ImCollections;

import de.walware.ecommons.text.core.sections.AbstractDocContentSections;


public class LtxDocumentContentInfo extends AbstractDocContentSections {
	
	
	public static final String LTX=                         ITexDocumentConstants.LTX_PARTITIONING;
	
	
	public static final LtxDocumentContentInfo INSTANCE= new LtxDocumentContentInfo();
	
	
	public LtxDocumentContentInfo() {
		super(ITexDocumentConstants.LTX_PARTITIONING, LTX,
				ImCollections.newList(LTX) );
	}
	
	
	@Override
	public String getTypeByPartition(final String contentType) {
		return LTX;
	}
	
}
