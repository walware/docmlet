/*=============================================================================#
 # Copyright (c) 2012-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core;

import de.walware.ecommons.preferences.IPreferenceAccess;

import de.walware.docmlet.tex.core.commands.TexCommandSet;


public interface ITexCoreAccess {
	
	
	IPreferenceAccess getPrefs();
	
	
	TexCommandSet getTexCommandSet();
	
	TexCodeStyleSettings getTexCodeStyle();
	
}
