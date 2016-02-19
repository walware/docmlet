/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.base.ui.processing;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.debug.ui.DebugUITools;


public abstract class DocProcessingToolLaunchDelegate extends LaunchConfigurationDelegate {
	
	
	protected DocProcessingToolLaunchDelegate() {
	}
	
	
	@Override
	protected IProject[] getBuildOrder(final ILaunchConfiguration configuration, final String mode) throws CoreException {
		final IResource resource= DebugUITools.getSelectedResource();
		if (resource != null && resource.getProject() != null) {
			return computeReferencedBuildOrder(new IProject[] { resource.getProject() });
		}
		return null;
	}
	
	
}
