/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.util;

import org.eclipse.core.runtime.preferences.IScopeContext;

import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.preferences.core.IPreferenceAccess;
import de.walware.ecommons.preferences.core.util.PreferenceAccessWrapper;

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.core.TexCodeStyleSettings;
import de.walware.docmlet.tex.core.commands.TexCommandSet;


public class TexCoreAccessWrapper extends PreferenceAccessWrapper
		implements ITexCoreAccess {
	
	
	private ITexCoreAccess parent;
	
	
	public TexCoreAccessWrapper(final ITexCoreAccess texCoreAccess) {
		if (texCoreAccess == null) {
			throw new NullPointerException("texCoreAccess"); //$NON-NLS-1$
		}
		
		updateParent(null, texCoreAccess);
	}
	
	
	public synchronized ITexCoreAccess getParent() {
		return this.parent;
	}
	
	public final synchronized boolean setParent(final ITexCoreAccess texCoreAccess) {
		if (texCoreAccess == null) {
			throw new NullPointerException("texCoreAccess"); //$NON-NLS-1$
		}
		if (texCoreAccess != this.parent) {
			updateParent(this.parent, texCoreAccess);
			return true;
		}
		return false;
	}
	
	protected void updateParent(final ITexCoreAccess oldParent, final ITexCoreAccess newParent) {
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
	public TexCommandSet getTexCommandSet() {
		return this.parent.getTexCommandSet();
	}
	
	@Override
	public TexCodeStyleSettings getTexCodeStyle() {
		return this.parent.getTexCodeStyle();
	}
	
}
