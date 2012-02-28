/*******************************************************************************
 * Copyright (c) 2012 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.core.commands;

import java.util.List;


public class TexEmbedCommand extends TexCommand {
	
	
	private final String fEmbeddedType;
	
	
	public TexEmbedCommand(final int type, final String embeddedType,
			final String word, final boolean asterisk, final List<Argument> arguments,
			final String description) {
		super(type, word, asterisk, arguments, description);
		fEmbeddedType = embeddedType;
	}
	
	
	public String getEmbeddedType() {
		return fEmbeddedType;
	}
	
}
