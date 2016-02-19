/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.core;

import de.walware.ecommons.ltk.core.impl.SourceModelStamp;

import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;


public class MarkupSourceModelStamp extends SourceModelStamp {
	
	
	private final IMarkupLanguage markupLanguage;
	
	
	public MarkupSourceModelStamp(final long sourceStamp, final IMarkupLanguage markupLanguage) {
		super(sourceStamp);
		
		this.markupLanguage= markupLanguage;
	}
	
	
//	public final IMarkupLanguage getLanguage() {
//		return this.language;
//	}
	
	
	@Override
	public int hashCode() {
		return super.hashCode() ^ this.markupLanguage.hashCode();
	}
	
	@Override
	public boolean equals(final Object other) {
		return (super.equals(other)
				&& this.markupLanguage == ((MarkupSourceModelStamp) other).markupLanguage );
	}
	
}
