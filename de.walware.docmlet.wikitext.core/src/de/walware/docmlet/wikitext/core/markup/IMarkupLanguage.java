/*=============================================================================#
 # Copyright (c) 2014-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.core.markup;


public interface IMarkupLanguage extends Cloneable {
	
	
	int TEMPLATE_MODE=                  1 << 4;
	
	int MYLYN_COMPAT_MODE=              1 << 8;
	
	
	String getName();
	
	IMarkupLanguage clone();
	
	String getScope();
	IMarkupLanguage clone(String scope, int mode);
	
	IMarkupConfig getMarkupConfig();
	void setMarkupConfig(IMarkupConfig config);
	
	int getMode();
	boolean isModeEnabled(int modeMask);
	
}
