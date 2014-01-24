/*=============================================================================#
 # Copyright (c) 2011-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.commands;

import java.util.List;


public class LtxPrintCommand extends TexCommand {
	
	
	private final String fUnicode;
	
	
	public LtxPrintCommand(final int type, final String word, final String description,
			final String unicode) {
		super(type, word, description);
		fUnicode = unicode;
	}
	
	public LtxPrintCommand(final int type, final String word, final List<Argument> arguments,
			final String description,
			final String unicode) {
		super(type, word, false, arguments, description);
		fUnicode = unicode;
	}
	
	
	public String getText() {
		return fUnicode;
	}
	
}
