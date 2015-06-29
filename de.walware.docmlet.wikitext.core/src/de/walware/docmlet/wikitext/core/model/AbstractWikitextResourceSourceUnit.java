/*=============================================================================#
 # Copyright (c) 2007-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.core.model;

import org.eclipse.core.resources.IFile;

import de.walware.ecommons.ltk.core.impl.GenericResourceSourceUnit2;
import de.walware.ecommons.ltk.core.impl.SourceUnitModelContainer;
import de.walware.ecommons.ltk.core.model.ISourceUnitModelInfo;


public abstract class AbstractWikitextResourceSourceUnit<M extends SourceUnitModelContainer<? extends IWikitextSourceUnit, ? extends ISourceUnitModelInfo>>
		extends GenericResourceSourceUnit2<M> implements IWikitextSourceUnit {
	
	
	public AbstractWikitextResourceSourceUnit(final String id, final IFile file) {
		super(id, file);
	}
	
	
}
