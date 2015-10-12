/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.commonmark.core;

import org.eclipse.core.runtime.IAdapterFactory;

import de.walware.docmlet.wikitext.core.source.IMarkupSourceFormatAdapter;
import de.walware.docmlet.wikitext.internal.commonmark.core.source.CommonmarkSourceFormatAdapter;


public class CoreAdapterFactory implements IAdapterFactory {
	
	
	private static final Class<?>[] ADAPTERS= new Class[] {
			IMarkupSourceFormatAdapter.class
	};
	
	
	private IMarkupSourceFormatAdapter sourceAdapter;
	
	
	public CoreAdapterFactory() {
	}
	
	
	@Override
	public Class[] getAdapterList() {
		return ADAPTERS;
	}
	
	@Override
	public Object getAdapter(final Object adaptableObject, final Class adapterType) {
		if (adapterType == IMarkupSourceFormatAdapter.class) {
			synchronized (this) {
				if (this.sourceAdapter == null) {
					this.sourceAdapter= new CommonmarkSourceFormatAdapter();
				}
				return this.sourceAdapter;
			}
		}
		return null;
	}
	
}
