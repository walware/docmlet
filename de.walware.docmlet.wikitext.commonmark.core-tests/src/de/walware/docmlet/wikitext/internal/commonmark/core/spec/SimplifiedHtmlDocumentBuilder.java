/*=============================================================================#
 # Copyright (c) 2015 David Green and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     David Green - initial API and implementation in Mylyn
 #     Stephan Wahlbrink (WalWare.de)
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.commonmark.core.spec;

import java.io.Writer;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;

import de.walware.ecommons.text.core.util.HtmlUtils;

import com.google.common.base.Objects;


public class SimplifiedHtmlDocumentBuilder extends HtmlDocumentBuilder {
	
	
	private boolean isResolveEntityReferencesEnabled;
	
	
	public SimplifiedHtmlDocumentBuilder(Writer out) {
		super(out, false);
	}
	
	@Override
	public void image(Attributes attributes, String url) {
		writer.writeEmptyElement(getHtmlNsUri(), "img"); //$NON-NLS-1$
		writer.writeAttribute("src", makeUrlAbsolute(url)); //$NON-NLS-1$
		if (attributes instanceof ImageAttributes) {
			ImageAttributes imageAttributes = (ImageAttributes) attributes;
			writer.writeAttribute(getHtmlNsUri(), "alt", Objects.firstNonNull(imageAttributes.getAlt(), ""));
			if (imageAttributes.getTitle() != null) {
				writer.writeAttribute(getHtmlNsUri(), "title", imageAttributes.getTitle());
			}
		}
	}
	
	
	public void setResolveEntityReferences(final boolean enable) {
		this.isResolveEntityReferencesEnabled= enable;
	}
	
	@Override
	public void entityReference(final String entity) {
		if (this.isResolveEntityReferencesEnabled) {
			try {
				final String resolved= HtmlUtils.resolveEntity(entity);
				if (resolved != null) {
					characters(resolved);
					return;
				}
			}
			catch (final IllegalArgumentException e) {
			}
		}
		super.entityReference(entity);
	}
	
}
