/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.model;

import de.walware.ecommons.ltk.ISourceStructElement;


public interface ILtxSourceElement extends ISourceStructElement {
	
	
	public static final int C2_PREAMBLE = C1_CLASS | 0x10;
	public static final int C2_SECTIONING = C1_CLASS | 0x20;
	
	
	@Override
	ILtxSourceElement getModelParent();
	
}
