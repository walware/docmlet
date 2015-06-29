/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.ui.sourceediting;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;

import de.walware.ecommons.templates.TemplateVariableProcessor;

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.core.source.LtxDocumentSetupParticipant;


public class LtxTemplateSourceViewerConfigurator extends LtxSourceViewerConfigurator {
	
	
	private static class LtxTemplateViewerConfiguration extends LtxSourceViewerConfiguration {
		
		
		private final TemplateVariableProcessor fProcessor;
		
		
		public LtxTemplateViewerConfiguration(
				final TemplateVariableProcessor variableProcessor) {
			super();
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
	
	
	public LtxTemplateSourceViewerConfigurator(final ITexCoreAccess coreAccess,
			final TemplateVariableProcessor processor) {
		super(coreAccess, new LtxTemplateViewerConfiguration(processor));
	}
	
	
	@Override
	public IDocumentSetupParticipant getDocumentSetupParticipant() {
		return new LtxDocumentSetupParticipant(true);
	}
	
}
