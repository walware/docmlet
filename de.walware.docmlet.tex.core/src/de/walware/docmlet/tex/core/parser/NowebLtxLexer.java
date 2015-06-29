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

package de.walware.docmlet.tex.core.parser;

import de.walware.ecommons.text.core.input.TextParserInput;


public class NowebLtxLexer extends LtxLexer {
	
	
	private String nowebType;
	
	
	public NowebLtxLexer() {
		super();
	}
	
	
	public void setNowebType(final String modelTypeId) {
		this.nowebType = modelTypeId;
	}
	
	
	@Override
	protected void handleNewLine(final int offset, int n) {
		super.handleNewLine(offset, n);
		
		final TextParserInput in= getInput();
		if (this.nowebType != null
				&& in.get(n++) == '<' && in.get(n++) == '<') {
			setEmbeddedBegin();
		}
	}
	
	@Override
	protected void searchEmbedded() {
		final TextParserInput in= getInput();
		int n= 2;
		CHUNK_CONTENT: while (true) {
			switch (in.get(n++)) {
			case TextParserInput.EOF:
				setEmbeddedEnd(n - 1, this.nowebType);
				return;
			case '\r':
				if (in.get(n) == '\n') {
					n++;
				}
				//$FALL-THROUGH$
			case '\n':
				super.handleNewLine(in.getIndex() + in.getLengthInSource(n), n);
				if (in.get(n) == '@') {
					n++;
					CHUNK_END: while (true) {
						switch (in.get(n++)) {
						case TextParserInput.EOF:
							setEmbeddedEnd(n - 1, this.nowebType);
							return;
						case '\r':
							if (in.get(n) == '\n') {
								n++;
							}
							//$FALL-THROUGH$
						case '\n':
							setEmbeddedEnd(n, this.nowebType);
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
