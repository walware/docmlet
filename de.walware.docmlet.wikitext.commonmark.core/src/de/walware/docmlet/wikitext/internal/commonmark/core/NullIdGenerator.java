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

package de.walware.docmlet.wikitext.internal.commonmark.core;

import org.eclipse.mylyn.wikitext.core.parser.IdGenerator;


public class NullIdGenerator extends IdGenerator {
	
	
	public NullIdGenerator() {
	}
	
	
	@Override
	public String newId(final String type, final String text) {
		return null;
	}
	
}
