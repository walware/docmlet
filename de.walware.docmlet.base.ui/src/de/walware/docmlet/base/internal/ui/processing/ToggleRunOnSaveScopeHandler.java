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

package de.walware.docmlet.base.internal.ui.processing;

import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.menus.UIElement;

import de.walware.ecommons.ui.actions.AbstractScopeHandler;

import de.walware.docmlet.base.ui.processing.actions.RunDocProcessingOnSaveExtension;
import de.walware.docmlet.base.ui.sourceediting.IDocEditor;


public class ToggleRunOnSaveScopeHandler extends AbstractScopeHandler {
	
	
	private Boolean currentChecked;
	
	
	public ToggleRunOnSaveScopeHandler(final Object scope, final String commandId) {
		super(scope, commandId);
	}
	
	
	private IWorkbenchWindow getWindow() {
		return (IWorkbenchWindow) getScope();
	}
	
	private RunDocProcessingOnSaveExtension getSaveExtension() {
		final IWorkbenchWindow window= getWindow();
		final IEditorPart editor= window.getActivePage().getActiveEditor();
		if (editor instanceof IDocEditor) {
			return (RunDocProcessingOnSaveExtension) editor.getAdapter(RunDocProcessingOnSaveExtension.class);
		}
		return null;
	}
	
	
	private boolean isChecked(final RunDocProcessingOnSaveExtension saveExtension) {
		return (saveExtension != null && saveExtension.isAutoRunEnabled());
	}
	
	@Override
	public void setEnabled(final IEvaluationContext context) {
		final RunDocProcessingOnSaveExtension saveExtension= getSaveExtension();
		
		setBaseEnabled(saveExtension != null);
		if (this.currentChecked != isChecked(saveExtension)) {
			refreshElements();
		}
	}
	
	@Override
	public void updateElement(final UIElement element, final Map parameters) {
		final RunDocProcessingOnSaveExtension saveExtension= getSaveExtension();
		
		element.setChecked(this.currentChecked= isChecked(saveExtension));
	}
	
	
	@Override
	public Object execute(final ExecutionEvent event, final IEvaluationContext context)
			throws ExecutionException {
		final RunDocProcessingOnSaveExtension saveExtension= getSaveExtension();
		if (saveExtension != null) {
			saveExtension.setAutoRunEnabled(!saveExtension.isAutoRunEnabled());
		}
		return null;
	}
	
}
