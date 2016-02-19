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

import java.util.Arrays;

import de.walware.ecommons.ltk.core.SourceContent;


public abstract class WeaveParticipant {
	
	
	protected static final char REPLACEMENT_CHAR= '\uFFFC';
	
	protected static final String REPLACEMENT_STRING;
	
	protected static final String REPLACEMENT_LINE;
	static {
		final char[] chars= new char[41];
		Arrays.fill(chars, 'x');
		chars[40]= '\n';
		REPLACEMENT_STRING= new String(chars, 0, 40);
		REPLACEMENT_LINE= new String(chars, 0, 41);
	}
	
	
	protected WeaveParticipant() {
	}
	
	
	public abstract String getForeignTypeId();
	
	public abstract int getEmbedDescr();
	
	public void reset(final SourceContent source) {
	}
	
}
