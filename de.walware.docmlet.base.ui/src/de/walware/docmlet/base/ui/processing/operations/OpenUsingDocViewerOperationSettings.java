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

package de.walware.docmlet.base.ui.processing.operations;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import de.walware.docmlet.base.internal.ui.processing.Messages;
import de.walware.docmlet.base.ui.viewer.DocViewerConfig;


public class OpenUsingDocViewerOperationSettings extends AbstractLaunchConfigOperationSettings {
	
	
	public OpenUsingDocViewerOperationSettings() {
		super(DocViewerConfig.TYPE_ID);
	}
	
	
	@Override
	public String getId() {
		return OpenUsingDocViewerOperation.ID;
	}
	
	@Override
	public String getLabel() {
		return Messages.ProcessingOperation_OpenUsingDocViewer_label;
	}
	
	
	@Override
	protected void initializeNewLaunchConfig(final ILaunchConfigurationWorkingCopy config) {
	}
	
}
