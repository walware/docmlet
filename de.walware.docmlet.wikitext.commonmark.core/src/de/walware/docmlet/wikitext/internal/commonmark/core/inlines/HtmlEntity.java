/*=============================================================================#
 # Copyright (c) 2015 David Green and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     David Green - initial API and implementation in Mylyn
 #     Stephan Wahlbrink (WalWare.de) - revised API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.commonmark.core.inlines;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

import de.walware.ecommons.text.core.util.HtmlUtils;

import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;


public class HtmlEntity extends InlineWithText {
	
	
	public HtmlEntity(final Line line, final int offset, final int length, final String entity) {
		super(line, offset, length, length, entity);
	}
	
	
	@Override
	public void emit(final ProcessingContext context,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		if (context.getMode() == ProcessingContext.EMIT_DOCUMENT) {
			try {
				final String replacement= HtmlUtils.resolveEntity(this.text);
				if (replacement != null && replacement.charAt(0) == 0) {
					throw new IllegalArgumentException();
				}
			}
			catch (final IllegalArgumentException e) {
				builder.characters("\uFFFD");
				return;
			}
		}
		builder.entityReference(this.text);
	}
	
}
