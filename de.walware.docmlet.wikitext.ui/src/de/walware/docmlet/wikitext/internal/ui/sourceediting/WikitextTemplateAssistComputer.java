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

package de.walware.docmlet.wikitext.internal.ui.sourceediting;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.mylyn.internal.wikitext.ui.editor.assist.SourceTemplateContextType;
import org.eclipse.mylyn.internal.wikitext.ui.editor.assist.WikiTextTemplateAccess;

import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssist;
import de.walware.ecommons.ltk.ui.sourceediting.assist.TemplatesCompletionComputer;

import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.source.MarkupLanguageDocumentSetupParticipant;


public class WikitextTemplateAssistComputer extends TemplatesCompletionComputer {
	// org.eclipse.mylyn.internal.wikitext.ui.editor.assist.MarkupTemplateCompletionProcessor
	
	
	private IMarkupLanguage markupLanguage;
	
	
	@SuppressWarnings("restriction")
	public WikitextTemplateAssistComputer() {
		super(WikiTextTemplateAccess.getInstance().getTemplateStore(),
				WikiTextTemplateAccess.getInstance().getContextTypeRegistry() );
	}
	
	
	@Override
	public void sessionStarted(final ISourceEditor editor, final ContentAssist assist) {
		super.sessionStarted(editor, assist);
		
		this.markupLanguage= MarkupLanguageDocumentSetupParticipant.getMarkupLanguage(
				editor.getViewer().getDocument(), editor.getDocumentContentInfo().getPartitioning() );
	}
	
	@Override
	public void sessionEnded() {
		super.sessionEnded();
		
		this.markupLanguage= null;
	}
	
	protected IMarkupLanguage getMarkupLanguage() {
		return this.markupLanguage;
	}
	
	
	@SuppressWarnings("restriction")
	@Override
	protected TemplateContextType getContextType(final AssistInvocationContext context, final IRegion region) {
		return getTypeRegistry().getContextType(SourceTemplateContextType.ID);
	}
	
}
