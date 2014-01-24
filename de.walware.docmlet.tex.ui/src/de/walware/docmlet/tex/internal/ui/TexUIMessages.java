/*=============================================================================#
 # Copyright (c) 2008-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.ui;

import org.eclipse.osgi.util.NLS;


public class TexUIMessages extends NLS {
	
	
	public static String Proposal_RenameInFile_label;
	public static String Proposal_RenameInFile_description;
	
	
	static {
		NLS.initializeMessages(TexUIMessages.class.getName(), TexUIMessages.class);
	}
	private TexUIMessages() {}
	
}
