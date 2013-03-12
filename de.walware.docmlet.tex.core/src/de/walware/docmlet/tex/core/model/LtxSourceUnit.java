/*******************************************************************************
 * Copyright (c) 2009-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.core.model;

import org.eclipse.core.resources.IFile;

import de.walware.ecommons.ltk.core.impl.GenericResourceSourceUnit;

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.core.TexCore;


public class LtxSourceUnit extends GenericResourceSourceUnit implements ILtxSourceUnit {
	
	
	public LtxSourceUnit(final String id, final IFile file) {
		super(id, file);
	}
	
	
	@Override
	public String getModelTypeId() {
		return TexModel.LTX_TYPE_ID;
	}
	
	@Override
	public String getContentTypeId() {
		return TexCore.LTX_CONTENT_ID;
	}
	
	
	@Override
	public ITexCoreAccess getTexCoreAccess() {
		return TexCore.getWorkbenchAccess();
	}
	
}
