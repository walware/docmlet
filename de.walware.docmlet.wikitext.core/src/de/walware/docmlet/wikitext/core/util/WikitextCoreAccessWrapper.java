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

package de.walware.docmlet.wikitext.core.util;

import org.eclipse.core.runtime.preferences.IScopeContext;

import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.preferences.core.IPreferenceAccess;
import de.walware.ecommons.preferences.core.util.PreferenceAccessWrapper;

import de.walware.docmlet.wikitext.core.IWikitextCoreAccess;
import de.walware.docmlet.wikitext.core.WikitextCodeStyleSettings;


public class WikitextCoreAccessWrapper extends PreferenceAccessWrapper
		implements IWikitextCoreAccess {
	
	
	private IWikitextCoreAccess parent;
	
	
	public WikitextCoreAccessWrapper(final IWikitextCoreAccess wikitextCoreAccess) {
		if (wikitextCoreAccess == null) {
			throw new NullPointerException("wikitextCoreAccess"); //$NON-NLS-1$
		}
		
		updateParent(null, wikitextCoreAccess);
	}
	
	
	public synchronized IWikitextCoreAccess getParent() {
		return this.parent;
	}
	
	public synchronized boolean setParent(final IWikitextCoreAccess wikitextCoreAccess) {
		if (wikitextCoreAccess == null) {
			throw new NullPointerException("wikitextCoreAccess"); //$NON-NLS-1$
		}
		if (wikitextCoreAccess != this.parent) {
			updateParent(this.parent, wikitextCoreAccess);
			return true;
		}
		return false;
	}
	
	protected void updateParent(final IWikitextCoreAccess oldParent, final IWikitextCoreAccess newParent) {
		this.parent= newParent;
		
		super.setPreferenceContexts(newParent.getPrefs().getPreferenceContexts());
	}
	
	@Override
	public void setPreferenceContexts(final ImList<IScopeContext> contexts) {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public IPreferenceAccess getPrefs() {
		return this;
	}
	
	@Override
	public WikitextCodeStyleSettings getWikitextCodeStyle() {
		return this.parent.getWikitextCodeStyle();
	}
	
}
