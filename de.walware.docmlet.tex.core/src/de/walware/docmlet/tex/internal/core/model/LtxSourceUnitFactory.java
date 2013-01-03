/*******************************************************************************
 * Copyright (c) 2008-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.internal.core.model;

import org.eclipse.core.resources.IFile;

import de.walware.ecommons.ltk.ISourceUnit;
import de.walware.ecommons.ltk.core.impl.AbstractFilePersistenceSourceUnitFactory;

import de.walware.docmlet.tex.core.model.LtxSourceUnit;


/**
 * Factory for common LaTeX files
 */
public class LtxSourceUnitFactory extends AbstractFilePersistenceSourceUnitFactory {
	
	
	@Override
	protected ISourceUnit createSourceUnit(final String id, final IFile file) {
		return new LtxSourceUnit(id, file);
	}
	
}
