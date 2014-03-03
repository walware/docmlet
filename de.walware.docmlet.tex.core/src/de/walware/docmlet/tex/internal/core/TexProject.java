/*=============================================================================#
 # Copyright (c) 2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

import de.walware.ecommons.resources.AbstractProjectNature;
import de.walware.ecommons.resources.ProjectUtil;

import de.walware.docmlet.tex.core.ITexProject;
import de.walware.docmlet.tex.core.TexProjects;
import de.walware.docmlet.tex.internal.core.builder.TexProjectBuilder;


public class TexProject extends AbstractProjectNature implements ITexProject {
	
	
	public static TexProject getTexProject(final IProject project) {
		try {
			return (project != null) ? (TexProject) project.getNature(TexProjects.TEX_NATURE_ID) : null;
		}
		catch (final CoreException e) {
			TexCorePlugin.log(e.getStatus());
			return null;
		}
	}
	
	
	public TexProject() {
	}
	
	
	@Override
	public void addBuilders() throws CoreException {
		final IProject project= getProject();
		final IProjectDescription description= project.getDescription();
		boolean changed= false;
		changed|= ProjectUtil.addBuilder(description, TexProjectBuilder.BUILDER_ID);
		
		if (changed) {
			project.setDescription(description, null);
		}
	}
	
	@Override
	public void removeBuilders() throws CoreException {
		final IProject project= getProject();
		final IProjectDescription description= project.getDescription();
		boolean changed= false;
		changed|= ProjectUtil.removeBuilder(description, TexProjectBuilder.BUILDER_ID);
		
		if (changed) {
			project.setDescription(description, null);
		}
	}
	
}
