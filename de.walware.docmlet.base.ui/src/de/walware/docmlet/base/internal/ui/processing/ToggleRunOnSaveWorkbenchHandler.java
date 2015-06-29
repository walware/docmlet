/*=============================================================================#

 # Copyright (c) 2008-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.base.internal.ui.processing;

import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.ui.commands.IElementUpdater;

import de.walware.ecommons.ui.actions.WorkbenchScopingHandler;

import de.walware.docmlet.base.ui.processing.DocProcessingUI;


/**
 * Handlers for document output creation toolchain running with the active configuration.
 */
public class ToggleRunOnSaveWorkbenchHandler extends WorkbenchScopingHandler
		implements IElementUpdater, IExecutableExtension {
	
	
	/** For instantiation via plugin.xml */
	public ToggleRunOnSaveWorkbenchHandler() {
		super(DocProcessingUI.TOGGLE_RUN_ON_SAVE_COMMAND_ID);
	}
	
	
	@Override
	protected ToggleRunOnSaveScopeHandler createScopeHandler(final Object scope) {
		return new ToggleRunOnSaveScopeHandler(scope, getCommandId());
	}
	
}
