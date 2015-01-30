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

package de.walware.docmlet.tex.internal.ui.editors;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IViewerCreator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

import de.walware.ecommons.ltk.ui.compare.CompareTextViewer;
import de.walware.ecommons.ui.SharedUIResources;

import de.walware.docmlet.tex.ui.sourceediting.LtxViewerConfiguration;
import de.walware.docmlet.tex.ui.sourceediting.LtxViewerConfigurator;


public class LtxContentViewerCreator implements IViewerCreator {
	
	
	@Override
	public Viewer createViewer(final Composite parent, final CompareConfiguration config) {
		final LtxViewerConfigurator viewerConfigurator = new LtxViewerConfigurator(null,
				new LtxViewerConfiguration(null, SharedUIResources.getColors() ));
		return new CompareTextViewer(parent, config, viewerConfigurator);
	}
	
}
