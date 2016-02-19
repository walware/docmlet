/*=============================================================================#
 # Copyright (c) 2014-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.ui.sourceediting;

import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.swt.graphics.Image;

import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssist;
import de.walware.ecommons.ltk.ui.sourceediting.assist.TemplateProposal;
import de.walware.ecommons.ui.SharedUIResources;

import de.walware.docmlet.wikitext.internal.ui.sourceediting.MarkupTemplates.MarkupInfo;
import de.walware.docmlet.wikitext.ui.WikitextLabelProvider;


public class MarkupTemplateAssistComputer extends WikitextTemplateAssistComputer {
	
	
	private MarkupTemplates markupTemplates;
	
	private final WikitextLabelProvider labelProvider;

	private IRegion startLineInfo;
	
	
	public MarkupTemplateAssistComputer() {
		this.labelProvider= new WikitextLabelProvider();
	}
	
	
	@Override
	public void sessionStarted(final ISourceEditor editor, final ContentAssist assist) {
		super.sessionStarted(editor, assist);
		
		this.markupTemplates= MarkupTemplatesManager.INSTANCE.getTemplates(getMarkupLanguage());
	}
	
	@Override
	protected boolean handleRequest(final int mode, final String prefix) {
		return true;
	}
	
	@Override
	protected List<Template> getTemplates(final String contextTypeId) {
		return this.markupTemplates.getTemplates();
	}
	
	private boolean isBlockTemplate(final Template template) {
		return (this.markupTemplates.getMarkupInfo(template) != null);
	}
	
	@Override
	protected boolean include(final Template template, final String prefix) {
		return (!isBlockTemplate(template) || super.include(template, prefix));
	}
	
	@Override
	protected Image getImage(final Template template) {
		final MarkupInfo markupInfo= this.markupTemplates.getMarkupInfo(template);
		if (markupInfo != null) {
			if (markupInfo.getElementType() != 0) {
				return this.labelProvider.getImage(markupInfo);
			}
		}
		return SharedUIResources.getImages().get(SharedUIResources.PLACEHOLDER_IMAGE_ID);
	}
	
	@Override
	protected DocumentTemplateContext createTemplateContext(final AssistInvocationContext context,
			final IRegion region) {
		try {
			this.startLineInfo= context.getDocument().getLineInformationOfOffset(context.getInvocationOffset());
		}
		catch (final Exception e) {
			this.startLineInfo= null;
		}
		
		return super.createTemplateContext(context, region);
	}
	
	@Override
	protected TemplateProposal createProposal(final Template template, final DocumentTemplateContext context,
			final String prefix, final IRegion region, int relevance) {
		if (relevance > 0
				&& this.startLineInfo != null && this.startLineInfo.getOffset() == context.getStart()
				&& isBlockTemplate(template) ) {
			// block at line start
			relevance+= 5;
		}
		return super.createProposal(template, context, prefix, region, relevance);
	}
	
}
