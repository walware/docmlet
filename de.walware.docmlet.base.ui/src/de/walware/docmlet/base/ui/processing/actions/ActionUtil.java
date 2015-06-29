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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.services.IServiceLocator;

import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;

import de.walware.docmlet.base.ui.processing.DocProcessingManager;
import de.walware.docmlet.base.ui.processing.DocProcessingUI;


class ActionUtil {
	
	
/*[ Action Parameters ]========================================================*/
	
	public static final String CONTENT_TYPE_PAR_NAME=              "contentTypeId"; //$NON-NLS-1$
	
	public static final String LAUNCH_FLAGS_PAR_NAME=              "launchFlags"; //$NON-NLS-1$
	
	
/*[ Action Modes ]=============================================================*/
	
	public static final byte ACTIVE_EDITOR_MODE=            1;
	public static final byte ACTIVE_MENU_SELECTION_MODE=    2;
	
	
	static void activateActiveEditor(final IWorkbenchWindow window) {
		final IWorkbenchPage page= window.getActivePage();
		final IEditorPart activeEditor= page.getActiveEditor();
		if (activeEditor != null && activeEditor != page.getActivePart()) {
			page.activate(activeEditor);
		}
	}
	
	
	private byte mode;
	
	private IContentType contentType; // for fix type
	
	private IServiceLocator serviceLocator;
	
	
	public ActionUtil(final byte initialMode) {
		this.mode= initialMode;
	}
	
	
	public void setMode(final byte mode) {
		this.mode= mode;
	}
	
	public byte getMode() {
		return this.mode;
	}
	
	public void setContentType(final IContentType contentType) {
		this.contentType= contentType;
	}
	
	public void setServiceLocator(final IServiceLocator serviceLocator) {
		this.serviceLocator= serviceLocator;
	}
	
	
	public IServiceLocator getServiceLocator() {
		return this.serviceLocator;
	}
	
	public IWorkbenchWindow getWindow() {
		if (this.serviceLocator != null) {
			return (IWorkbenchWindow) this.serviceLocator.getService(IWorkbenchWindow.class);
		}
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}
	
	public IFile getFile(final IWorkbenchWindow window) {
		switch (getMode()) {
		case ActionUtil.ACTIVE_EDITOR_MODE:
			if (window != null) {
				final IEditorPart activeEditor= window.getActivePage().getActiveEditor();
				if (activeEditor instanceof ISourceEditor) {
					return ResourceUtil.getFile(activeEditor.getEditorInput());
				}
			}
			break;
		case ActionUtil.ACTIVE_MENU_SELECTION_MODE:
			if (this.serviceLocator != null) {
				final IEclipseContext eContext= (IEclipseContext) this.serviceLocator
						.getService(IEclipseContext.class);
				if (eContext != null) {
					final Object selection= eContext.get("activeMenuSelection"); //$NON-NLS-1$
					if (selection instanceof IStructuredSelection) {
						final IStructuredSelection structSelection= (IStructuredSelection) selection;
						if (structSelection.size() == 1) {
							final Object element= structSelection.getFirstElement();
							if (element instanceof IFile) {
								return (IFile) element;
							}
							if (element instanceof IAdaptable) {
								final IResource resource= (IResource) (((IAdaptable) element)
										.getAdapter(IResource.class) );
								return (resource instanceof IFile) ? (IFile) resource : null;
							}
						}
					}
				}
			}
			break;
		default:
			break;
		}
		return null;
	}
	
	public IContentType getContentType(final IWorkbenchWindow window, final IFile file) {
		IContentType contentType= this.contentType;
		if (contentType == null) {
			switch (getMode()) {
			case ActionUtil.ACTIVE_EDITOR_MODE:
				if (window != null) {
					final IEditorPart activeEditor= window.getActivePage().getActiveEditor();
					if (activeEditor instanceof ISourceEditor) {
						contentType= ((ISourceEditor) activeEditor).getContentType();
					}
				}
				break;
			case ActionUtil.ACTIVE_MENU_SELECTION_MODE:
				if (file != null) {
					try {
						final IContentDescription contentDescription= file.getContentDescription();
						if (contentDescription != null) {
							contentType= contentDescription.getContentType();
						}
					}
					catch (final CoreException e) {}
				}
				break;
			default:
				break;
			}
		}
		return contentType;
	}
	
	public DocProcessingManager getManager(final IContentType contentType) {
		return (contentType != null) ?
				DocProcessingUI.getDocProcessingManager(contentType, true) :
				null;
	}
	
}
