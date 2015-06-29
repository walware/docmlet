/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.base.ui.markuphelp;

import java.io.IOException;


public abstract class MarkupHelpContent {
	
	
	private final String id;
	
	private final String title;
	
	
	public MarkupHelpContent(final String id, final String title) {
		this.id= id;
		this.title= title;
	}
	
	
	public String getId() {
		return this.id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public abstract String getContent() throws IOException;
	
	
	@Override
	public String toString() {
		return this.title + " (" + this.id + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
}
