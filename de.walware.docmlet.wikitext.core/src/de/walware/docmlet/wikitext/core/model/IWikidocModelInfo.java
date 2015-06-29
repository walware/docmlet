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

package de.walware.docmlet.wikitext.core.model;

import java.util.Map;

import de.walware.ecommons.ltk.core.model.ISourceUnitModelInfo;

import de.walware.docmlet.wikitext.core.ast.WikitextAstInfo;
import de.walware.docmlet.wikitext.core.ast.WikitextAstNode;


public interface IWikidocModelInfo extends ISourceUnitModelInfo {
	
	
	@Override
	WikitextAstInfo getAst();
	
	Map<String, WikitextAstNode> getLabels();
	int getMinSectionLevel();
	int getMaxSectionLevel();
	
}
