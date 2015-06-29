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

package de.walware.docmlet.wikitext.core.markup;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;

import de.walware.ecommons.ltk.core.SourceContent;


public class MarkupParser2 extends MarkupParser {
	
	
	public MarkupParser2(final IMarkupLanguage markupLanguage, final DocumentBuilder builder) {
		super((MarkupLanguage) markupLanguage, builder);
	}
	
	
	public void parse(final SourceContent markupContent, final boolean asDocument) {
		final MarkupLanguage markupLanguage= getMarkupLanguage();
		final DocumentBuilder builder= getBuilder();
		if (markupLanguage == null) {
			throw new IllegalStateException("markup language is not set"); //$NON-NLS-1$
		}
		if (builder == null) {
			throw new IllegalStateException("builder is not set"); //$NON-NLS-1$
		}
		if (markupLanguage instanceof IMarkupLanguageExtension2) {
			((IMarkupLanguageExtension2) markupLanguage).processContent(this, markupContent, asDocument);
		}
		else {
			markupLanguage.processContent(this, markupContent.getText(), asDocument);
		}
	}
	
}
