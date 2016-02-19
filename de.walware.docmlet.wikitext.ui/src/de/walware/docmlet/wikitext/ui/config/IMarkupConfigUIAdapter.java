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

package de.walware.docmlet.wikitext.ui.config;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Shell;

import de.walware.docmlet.wikitext.core.markup.IMarkupConfig;


public interface IMarkupConfigUIAdapter {
	
	
	boolean edit(String contextLabel, AtomicBoolean isContextEnabled, IMarkupConfig config,
			Shell parent );
	
}
