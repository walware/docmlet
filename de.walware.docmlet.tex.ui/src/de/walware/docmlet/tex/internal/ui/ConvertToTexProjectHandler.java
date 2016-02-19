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

package de.walware.docmlet.tex.internal.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.statushandlers.StatusManager;

import de.walware.ecommons.ui.util.UIAccess;
import de.walware.ecommons.workbench.ui.WorkbenchUIUtil;

import de.walware.docmlet.tex.core.TexProjects;
import de.walware.docmlet.tex.ui.TexUI;


public class ConvertToTexProjectHandler extends AbstractHandler {
	
	
	public ConvertToTexProjectHandler() {
	}
	
	
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection= WorkbenchUIUtil.getCurrentSelection(event.getApplicationContext());
		final List<IProject> projects;
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection structuredSelection= (IStructuredSelection) selection;
			projects= new ArrayList<>(structuredSelection.size());
			for (final Iterator<?> iter= structuredSelection.iterator(); iter.hasNext(); ) {
				final Object obj= iter.next();
				if (obj instanceof IAdaptable) {
					final IProject project= (IProject) ((IAdaptable) obj).getAdapter(IProject.class);
					if (project != null) {
						projects.add(project);
					}
				}
			}
		}
		else {
			return null;
		}
		
		final WorkspaceModifyOperation op= new WorkspaceModifyOperation() {
			
			@Override
			protected void execute(final IProgressMonitor monitor) throws CoreException,
					InvocationTargetException, InterruptedException {
				final SubMonitor m= SubMonitor.convert(monitor,
						TexUIMessages.TexProject_ConvertTask_label,
						100 );
				try {
					final SubMonitor mProjects= m.newChild(100).setWorkRemaining(projects.size());
					for (final IProject project : projects) {
						if (m.isCanceled()) {
							throw new InterruptedException();
						}
						TexProjects.setupTexProject(project, mProjects.newChild(1));
					}
				}
				finally {
					m.done();
				}
			}
			
		};
		try {
			UIAccess.getActiveWorkbenchWindow(true).run(true, true, op);
		}
		catch (final InvocationTargetException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, TexUI.PLUGIN_ID,
					TexUIMessages.TexProject_ConvertTask_error_message, e.getTargetException() ));
		}
		catch (final InterruptedException e) {
			// cancelled
		}
		
		return null;
	}
	
}
