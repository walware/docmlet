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

package de.walware.docmlet.base.ui.processing.actions;

import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.menus.UIElement;

import de.walware.jcommons.collections.ImIdentitySet;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.ui.actions.AbstractScopeHandler;
import de.walware.ecommons.ui.util.MessageUtil;
import de.walware.ecommons.ui.util.UIAccess;

import de.walware.docmlet.base.ui.processing.DocProcessingManager;
import de.walware.docmlet.base.ui.processing.DocProcessingManager.IProcessingListener;
import de.walware.docmlet.base.ui.processing.DocProcessingUI;
import de.walware.docmlet.base.ui.sourceediting.IDocEditor;


public class RunActiveConfigScopeHandler extends AbstractScopeHandler implements IProcessingListener {
	
	private static final byte UNCHANGED= 0;
	private static final byte CHANGED_NULL= 1;
	private static final byte CHANGED_OK= 2;
	
	
	private final ActionUtil util= new ActionUtil(ActionUtil.ACTIVE_EDITOR_MODE);
	
	private final ImIdentitySet<String> launchFlags;
	
	private String lastTypeId;
	private DocProcessingManager manager;
	
	private String tooltip;
	
	
	public RunActiveConfigScopeHandler(final Object scope, final String commandId,
			final ImIdentitySet<String> launchFlags) {
		super(scope, commandId);
		
		this.launchFlags= launchFlags;
	}
	
	
	@Override
	public void dispose() {
		updateManager(null);
	}
	
	
	private IWorkbenchWindow getWindow() {
		return (IWorkbenchWindow) getScope();
	}
	
	private IContentType getType() {
		final IWorkbenchWindow window= getWindow();
		final IEditorPart editor= window.getActivePage().getActiveEditor();
		if (editor instanceof IDocEditor) {
			return ((IDocEditor) editor).getContentType();
		}
		return null;
	}
	
	private synchronized byte updateManager(final IContentType type) {
		final DocProcessingManager manager;
		if (type != null) {
			if (type.getId() == this.lastTypeId) {
				return UNCHANGED;
			}
			this.lastTypeId= type.getId();
			manager= DocProcessingUI.getDocProcessingManager(type, true);
		}
		else {
			this.lastTypeId= null;
			manager= null;
		}
		
		if (manager == this.manager) {
			return UNCHANGED;
		}
		
		if (this.manager != null) {
			this.manager.removeProcessingListener(this);
		}
		this.manager= manager;
		if (manager != null) {
			manager.addProcessingListener(this);
			updateInfo(manager, manager.getActiveConfig());
		}
		else {
			updateInfo(null, null);
		}
		return ((manager != null) ? CHANGED_OK : CHANGED_NULL);
	}
	
	private void updateInfo(final DocProcessingManager manager, final ILaunchConfiguration config) {
		if (manager != null && config != null) {
			this.tooltip= MessageUtil.escapeForTooltip(manager.getLabel(
					config, this.launchFlags, true ));
		}
		else {
			this.tooltip= null;
		}
	}
	
	@Override
	public void availableConfigChanged(final ImList<ILaunchConfiguration> configs) {
	}
	
	@Override
	public void activeConfigChanged(final ILaunchConfiguration config) {
		synchronized (this) {
			updateInfo(this.manager, config);
		}
		
		refreshElements();
	}
	
	
	@Override
	public void setEnabled(final IEvaluationContext context) {
		final byte changed= updateManager(getType());
		if (changed != 0) {
			setBaseEnabled((changed > CHANGED_NULL));
			refreshElements();
		}
	}
	
	@Override
	public void updateElement(final UIElement element, final Map parameters) {
		element.setTooltip(this.tooltip);
	}
	
	
	@Override
	public Object execute(final ExecutionEvent event, final IEvaluationContext context)
			throws ExecutionException {
		final IWorkbenchWindow window= getWindow();
		final IFile file= this.util.getFile(window);
		final DocProcessingManager manager= this.manager;
		if (manager == null) {
			return null;
		}
		
		final ILaunchConfiguration config= manager.getActiveConfig();
		if (config != null) {
			manager.launch(config, file, this.launchFlags);
		}
		else {
			final Runnable runnable= new Runnable() {
				@Override
				public void run() {
					ActionUtil.activateActiveEditor(window);
					manager.openConfigurationDialog(window.getShell(), null);
				};
			};
			UIAccess.getDisplay().asyncExec(runnable);
		}
		return null;
	}
	
}
