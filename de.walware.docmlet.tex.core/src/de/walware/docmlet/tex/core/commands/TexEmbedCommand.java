/*=============================================================================#
 # Copyright (c) 2012-2016 Stephan Wahlbrink (WalWare.de) and others.
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

import de.walware.docmlet.tex.core.parser.ICustomScanner;


public class TexEmbedCommand extends TexCommand {
	
	
	private final String embeddedType;
	
	
	public TexEmbedCommand(final int type, final String embeddedType,
			final String word, final boolean asterisk, final List<Argument> arguments,
			final String description) {
		super(type, word, asterisk, arguments, description);
		this.embeddedType= embeddedType;
	}
	
	
	public String getEmbeddedType(final int argIdx) {
		return this.embeddedType;
	}
	
	public ICustomScanner getArgumentScanner(final int argIdx) {
		return null;
	}
	
}
