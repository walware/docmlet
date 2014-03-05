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

package de.walware.docmlet.tex.core.parser;

import de.walware.ecommons.text.IStringCache;
import de.walware.ecommons.text.SourceParseInput;


public class NowebLtxLexer extends LtxLexer {
	
	
	private String nowebType;
	
	
	public NowebLtxLexer() {
		super((IStringCache) null);
	}
	
	public NowebLtxLexer(final IStringCache stringCache) {
		super(stringCache);
	}
	
	
	public void setNowebType(final String modelTypeId) {
		this.nowebType = modelTypeId;
	}
	
	
	@Override
	protected void handleNewLine(final int offset, int num) {
		super.handleNewLine(offset, num);
		if (this.nowebType != null
				&& this.input.get(++num) == '<' && this.input.get(++num) == '<') {
			setEmbeddedBegin();
		}
	}
	
	@Override
	protected void searchEmbedded() {
		int num = 2;
		CHUNK_CONTENT: while (true) {
			switch (this.input.get(++num)) {
			case SourceParseInput.EOF:
				setEmbeddedEnd(num-1, this.nowebType);
				return;
			case '\r':
				if (this.input.get(num+1) == '\n') {
					num++;
				}
				//$FALL-THROUGH$
			case '\n':
				super.handleNewLine(this.input.getIndex() + this.input.getLength(num), num);
				if (this.input.get(num+1) == '@') {
					num++;
					CHUNK_END: while (true) {
						switch (this.input.get(++num)) {
						case SourceParseInput.EOF:
							setEmbeddedEnd(num-1, this.nowebType);
							return;
						case '\r':
							if (this.input.get(num+1) == '\n') {
								num++;
							}
							//$FALL-THROUGH$
						case '\n':
							setEmbeddedEnd(num, this.nowebType);
							return;
						default:
							continue CHUNK_END;
						}
					}
				}
				continue CHUNK_CONTENT;
			default:
				continue CHUNK_CONTENT;
			}
		}
	}
	
}
