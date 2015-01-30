/*=============================================================================#
 # Copyright (c) 2011-2015 Stephan Wahlbrink (WalWare.de) and others.
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


public class LtxFontCommand extends TexCommand {
	
	
	public LtxFontCommand(final int type, final String word, final String description) {
		super(type, word, description);
	}
	
	public LtxFontCommand(final int type, final String word, final List<Argument> arguments,
			final String description) {
		super(type, word, false, arguments, description);
	}
	
}
