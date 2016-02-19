/*=============================================================================#
 # Copyright (c) 2014-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.core.source;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;


/**
 * Indicating a block/span element embedding foreign code
 */
public class EmbeddingAttributes extends Attributes {
	
	
	private final String foreignType;
	private final int embedDescr;
	
	private final int contentBeginOffset;
	private final int contentEndOffset;
	
	
	public EmbeddingAttributes(final String foreignType, final int embedDescr,
			final int contentBeginOffset, final int contentEndOffset) {
		this.foreignType= foreignType;
		this.embedDescr= embedDescr;
		
		this.contentBeginOffset= contentBeginOffset;
		this.contentEndOffset= contentEndOffset;
	}
	
	
	public String getForeignType() {
		return this.foreignType;
	}
	
	public int getEmbedDescr() {
		return this.embedDescr;
	}
	
	public int getContentBeginOffset() {
		return this.contentBeginOffset;
	}
	
	public int getContentEndOffset() {
		return this.contentEndOffset;
	}
	
}
