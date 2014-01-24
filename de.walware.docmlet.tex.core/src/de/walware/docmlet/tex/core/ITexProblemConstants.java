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

import de.walware.docmlet.tex.core.ast.ITexAstStatusConstants;
import de.walware.docmlet.tex.core.model.ILtxModelProblemConstants;


public interface ITexProblemConstants extends ITexAstStatusConstants, ILtxModelProblemConstants {
	
	
	int MASK_1 =                                            0x000000ff;
	int MASK_12 =                                           0x0000ffff;
	
	
}
