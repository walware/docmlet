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

package de.walware.docmlet.base.ui.processing.actions;

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.commands.IElementUpdater;

import de.walware.jcommons.collections.CollectionUtils;
import de.walware.jcommons.collections.ImIdentitySet;

import de.walware.ecommons.ui.actions.WorkbenchScopingHandler;

import de.walware.docmlet.base.ui.DocBaseUI;
import de.walware.docmlet.base.ui.processing.DocProcessingUI;
import de.walware.docmlet.base.ui.processing.DocProcessingUI.CommonFlags;


/**
 * Handlers for document output creation toolchain running with the active configuration.
 */
public class RunActiveConfigWorkbenchHandler extends WorkbenchScopingHandler
		implements IElementUpdater, IExecutableExtension {
	
	
	private ImIdentitySet<String> launchFlags;
	
	
	/** For instantiation via plugin.xml */
	public RunActiveConfigWorkbenchHandler() {
	}
	
	
	@Override
	public void setInitializationData(final IConfigurationElement config,
			final String propertyName, final Object data) throws CoreException {
		super.setInitializationData(config, propertyName, data);
		try {
			final Map<String, String> parameters= (data instanceof Map) ?
					(Map<String, String>) data : Collections.<String, String>emptyMap();
//			{	final String s= parameters.get(DocProcessingUI.CONTENT_TYPE_PAR_NAME);
//				if (s != null) {
//				}
//			}
			{	final String s= parameters.get(ActionUtil.LAUNCH_FLAGS_PAR_NAME);
				if (s != null) {
					this.launchFlags= CollectionUtils.toIdentifierSet(s.split(";")); //$NON-NLS-1$
				}
				else if (this.launchFlags == null) {
					if (getCommandId() != null) {
						final ImIdentitySet<String> flags= getCommandLaunchFlags(getCommandId());
						if (flags != null) {
							this.launchFlags= flags;
						}
					}
					else {
						throw new IllegalArgumentException(ActionUtil.LAUNCH_FLAGS_PAR_NAME + "= <missing>"); //$NON-NLS-1$
					}
				}
			}
		}
		catch (final IllegalArgumentException e) {
			throw new CoreException(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, 0,
					NLS.bind("Invalid declaration of contribution by ''{0}''.", //$NON-NLS-1$
							config.getContributor().getName() ),
					e ));
		}
	}
	
	protected ImIdentitySet<String> getLaunchFlags() {
		return this.launchFlags;
	}
	
	protected ImIdentitySet<String> getCommandLaunchFlags(final String commandId) {
		switch (commandId) {
		case DocProcessingUI.PROCESS_AND_PREVIEW_DOC_DEFAULT_COMMAND_ID:
			return CommonFlags.PROCESS_AND_PREVIEW;
		case DocProcessingUI.PROCESS_DOC_DEFAULT_COMMAND_ID:
			return CommonFlags.PROCESS;
		case DocProcessingUI.PREVIEW_DOC_DEFAULT_COMMAND_ID:
			return CommonFlags.OPEN_OUTPUT;
		default:
			return null;
		}
	}
	
	@Override
	protected RunActiveConfigScopeHandler createScopeHandler(final Object scope) {
		return new RunActiveConfigScopeHandler(scope, getCommandId(), this.launchFlags);
	}
	
}
