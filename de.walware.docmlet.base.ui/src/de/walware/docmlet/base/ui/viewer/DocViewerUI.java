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

package de.walware.docmlet.base.ui.viewer;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.variables.IStringVariable;

import de.walware.ecommons.ui.workbench.ResourceVariableUtil;

import de.walware.docmlet.base.internal.ui.DocBaseUIPlugin;
import de.walware.docmlet.base.internal.ui.viewer.DocViewerCloseDelegate;
import de.walware.docmlet.base.internal.ui.viewer.DocViewerLaunchConfig;

public class DocViewerUI {
	
	
/*[ DocViewerLaunchConfig Attributes ]=========================================*/
	
	public static final String TARGET_PATH_ATTR_NAME= DocViewerUI.BASE_RUN_ATTR_QUALIFIER + '/' + "Target.path"; //$NON-NLS-1$
	
	public static final String BASE_RUN_ATTR_QUALIFIER= "de.walware.docmlet.base/run"; //$NON-NLS-1$
	
	
/*[ Actions ]==================================================================*/
	
	public static void runPreProduceOutputTask(final ResourceVariableUtil outputFileUtil,
			final Map<String, ? extends IStringVariable> extraVariables,
			final SubMonitor m) throws CoreException {
		if (!DocViewerCloseDelegate.isAvailable()) {
			return;
		}
		
		final DocViewerLaunchConfig config= new DocViewerLaunchConfig(outputFileUtil, extraVariables);
		final DocViewerCloseDelegate delegate= DocBaseUIPlugin.getInstance().getDocViewerCloseDelegate();
		delegate.run(config, m);
	}
	
	
}
