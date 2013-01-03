/*******************************************************************************
 * Copyright (c) 2008-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.internal.ui.editors;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.widgets.Composite;

import de.walware.ecommons.ltk.ui.compare.CompareMergeTextViewer;
import de.walware.ecommons.ltk.ui.sourceediting.SourceEditorViewerConfigurator;
import de.walware.ecommons.ui.SharedUIResources;

import de.walware.docmlet.tex.ui.sourceediting.LtxViewerConfiguration;
import de.walware.docmlet.tex.ui.sourceediting.LtxViewerConfigurator;
import de.walware.docmlet.tex.ui.text.LtxDocumentSetupParticipant;


public class LtxMergeViewer extends CompareMergeTextViewer {
	
	
	public LtxMergeViewer(final Composite parent, final CompareConfiguration configuration) {
		super(parent, configuration);
	}
	
	@Override
	protected IDocumentSetupParticipant createDocumentSetupParticipant() {
		return new LtxDocumentSetupParticipant();
	}
	
	@Override
	protected SourceEditorViewerConfigurator createConfigurator(final SourceViewer sourceViewer) {
		return new LtxViewerConfigurator(null,
				new LtxViewerConfiguration(null, SharedUIResources.getColors() ));
	}
	
}
