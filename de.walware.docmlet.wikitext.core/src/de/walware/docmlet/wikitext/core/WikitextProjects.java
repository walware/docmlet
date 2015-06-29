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

package de.walware.docmlet.wikitext.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osgi.util.NLS;

import de.walware.ecommons.resources.ProjectUtil;

import de.walware.docmlet.wikitext.internal.core.Messages;
import de.walware.docmlet.wikitext.internal.core.WikitextProject;


public class WikitextProjects {
	
	
	public static final String WIKITEXT_NATURE_ID= "de.walware.docmlet.wikitext.natures.Wikitext"; //$NON-NLS-1$
	
	
	public static IWikitextProject getWikitextProject(final IProject project) {
		return WikitextProject.getWikitextProject(project);
	}
	
	/**
	 * 
	 * @param project the project to setup
	 * @param monitor SubMonitor-recommended
	 * @throws CoreException
	 */
	public static void setupWikitextProject(final IProject project,
			final IProgressMonitor monitor) throws CoreException {
		final SubMonitor m= SubMonitor.convert(monitor,
				NLS.bind(Messages.WikitextProject_ConfigureTask_label, project.getName()),
				2 + 8 );
		
		final IProjectDescription description= project.getDescription();
		boolean changed= false;
		changed|= ProjectUtil.addNature(description, WIKITEXT_NATURE_ID);
		m.worked(2);
		
		if (changed) {
			project.setDescription(description, m.newChild(8));
		}
	}
	
}
