/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.core.builder;

import de.walware.docmlet.tex.internal.core.TexProject;


public class TexBuildParticipantInternal {
	
	
	protected TexProject texProject;
	
	protected int buildType;
	
	protected boolean enabled;
	
	
	public void init() {
	}
	
}
