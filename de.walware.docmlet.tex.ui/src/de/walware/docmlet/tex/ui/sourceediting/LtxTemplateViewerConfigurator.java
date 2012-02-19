/*******************************************************************************
 * Copyright (c) 2009-2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.ui.sourceediting;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;

import de.walware.ecommons.templates.TemplateVariableProcessor;
import de.walware.ecommons.ui.ColorManager;
import de.walware.ecommons.ui.SharedUIResources;

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.ui.text.LtxDocumentSetupParticipant;


public class LtxTemplateViewerConfigurator extends LtxViewerConfigurator {
	
	
	private static class LtxTemplateViewerConfiguration extends LtxViewerConfiguration {
		
		
		private final TemplateVariableProcessor fProcessor;
		
		
		public LtxTemplateViewerConfiguration(
				final TemplateVariableProcessor variableProcessor,
				final IPreferenceStore preferenceStore, final ColorManager colorManager) {
			super(preferenceStore, colorManager);
			fProcessor = variableProcessor;
		}
		
		
		@Override
		protected ContentAssistant createContentAssistant(final ISourceViewer sourceViewer) {
			return createTemplateVariableContentAssistant(sourceViewer, fProcessor);
		}
		
		@Override
		public int[] getConfiguredTextHoverStateMasks(final ISourceViewer sourceViewer, final String contentType) {
			return new int[] { ITextViewerExtension2.DEFAULT_HOVER_STATE_MASK };
		}
		
		@Override
		public ITextHover getTextHover(final ISourceViewer sourceViewer, final String contentType, final int stateMask) {
			return new TemplateVariableTextHover(fProcessor);
		}
		
	}
	
	
	public LtxTemplateViewerConfigurator(final ITexCoreAccess coreAccess,
			final TemplateVariableProcessor processor) {
		super(coreAccess, new LtxTemplateViewerConfiguration(processor,
				null, SharedUIResources.getColors() ));
	}
	
	
	@Override
	public IDocumentSetupParticipant getDocumentSetupParticipant() {
		return new LtxDocumentSetupParticipant(true);
	}
	
}
