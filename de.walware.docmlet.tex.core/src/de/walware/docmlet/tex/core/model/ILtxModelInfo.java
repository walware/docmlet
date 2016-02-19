/*=============================================================================#
 # Copyright (c) 2011-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.model;

import java.util.Map;

import de.walware.ecommons.ltk.core.model.INameAccessSet;
import de.walware.ecommons.ltk.core.model.ISourceUnitModelInfo;

import de.walware.docmlet.tex.core.commands.TexCommand;


public interface ILtxModelInfo extends ISourceUnitModelInfo {
	
	
	int getMinSectionLevel();
	int getMaxSectionLevel();
	
	INameAccessSet<TexNameAccess> getLabels();
	Map<String, TexCommand> getCustomCommandMap();
	Map<String, TexCommand> getCustomEnvMap();
	
	
}
