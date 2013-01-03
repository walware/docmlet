/*******************************************************************************
 * Copyright (c) 2011-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.internal.core;

import org.eclipse.core.runtime.IAdapterFactory;

import de.walware.docmlet.tex.core.text.ITexDocumentConstants;
import de.walware.docmlet.tex.core.text.LtxHeuristicTokenScanner;


public class ModelAdapterFactory implements IAdapterFactory {
	
	
	private static final Class<?>[] ADAPTERS = new Class<?>[] {
		LtxHeuristicTokenScanner.class,
	};
	
	
	public ModelAdapterFactory() {
	}
	
	
	@Override
	public Class<?>[] getAdapterList() {
		return ADAPTERS;
	}
	
	@Override
	public Object getAdapter(final Object adaptableObject, final Class adapterType) {
		if (LtxHeuristicTokenScanner.class.equals(adapterType)) {
			return new LtxHeuristicTokenScanner(ITexDocumentConstants.LTX_PARTITIONING_CONFIG);
		}
		return null;
	}
	
}
