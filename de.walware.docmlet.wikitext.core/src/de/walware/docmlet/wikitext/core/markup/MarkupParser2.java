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

package de.walware.docmlet.wikitext.core.markup;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;

import de.walware.ecommons.ltk.core.SourceContent;


public class MarkupParser2 extends MarkupParser {
	
	
	/**
	 * Enables output of generative content like table of content.
	 * 
	 * <p>Equivalent to {@link AbstractMarkupLanguage#setFilterGenerativeContents(boolean)} 
	 * with filter= <code>false</code>
	 * </p>
	 */
	public static final int GENERATIVE_CONTENT=             0b0_0000_0001_0000_0000;
	
	/**
	 * Enables mode for editors and source analysis.
	 */
	public static final int SOURCE_STRUCT=                  0b0_0001_0000_0000_0000;
	
	public static final int INLINE_MARKUP=                  0b0_0000_0000_0001_0000;
	public static final int INLINE_EMBEDDED=                0b0_0000_0000_0010_0000;
	public static final int INLINE_ALL=                     0b0_0000_0000_0111_0000;
	
	private static final int DEFAULT=                       0b0_0000_1111_1111_0000;
	
	
	private int currentFlags= DEFAULT;
	
	
	public MarkupParser2(final IMarkupLanguage markupLanguage, final DocumentBuilder builder) {
		super((MarkupLanguage) markupLanguage, builder);
	}
	
	public MarkupParser2(final IMarkupLanguage markupLanguage, final DocumentBuilder builder,
			final int initialFlags) {
		super((MarkupLanguage) markupLanguage, builder);
		
		this.currentFlags= initialFlags;
	}
	
	public MarkupParser2(final MarkupParser parser0) {
		super(parser0.getMarkupLanguage(), parser0.getBuilder());
		
		final MarkupLanguage markupLanguage= getMarkupLanguage();
		if (markupLanguage instanceof AbstractMarkupLanguage) {
			final AbstractMarkupLanguage language= (AbstractMarkupLanguage) markupLanguage;
			setEnabled(GENERATIVE_CONTENT, !language.isFilterGenerativeContents());
			setEnabled(INLINE_ALL, !language.isBlocksOnly());
		}
	}
	
	
	public void enable(final int flags) {
		this.currentFlags|= flags;
	}
	
	public void disable(final int flags) {
		this.currentFlags&= ~flags;
	}
	
	public void setEnabled(final int flags, final boolean enabled) {
		if (enabled) {
			enable(flags);
		}
		else {
			disable(flags);
		}
	}
	
	public int getFlags() {
		return this.currentFlags;
	}
	
	public boolean isEnabled(final int flags) {
		return ((this.currentFlags & flags) != 0);
	}
	
	public boolean isDisabled(final int flags) {
		return ((this.currentFlags & flags) == 0);
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
			if (markupLanguage instanceof AbstractMarkupLanguage) {
				final AbstractMarkupLanguage language= (AbstractMarkupLanguage) markupLanguage;
				language.setFilterGenerativeContents(isDisabled(GENERATIVE_CONTENT));
				language.setBlocksOnly(isDisabled(INLINE_MARKUP));
			}
			markupLanguage.processContent(this, markupContent.getText(), asDocument);
		}
	}
	
	
	@Override
	public String toString() {
		final StringBuilder sb= new StringBuilder("MarkupParser2");
		sb.append("\n" + "language= ").append(getMarkupLanguage().toString());
		sb.append("\n" + "flags= ");
		if (this.currentFlags == DEFAULT) {
			sb.append("(DEFAULT)");
		}
		sb.append("\n\t" + "GENERATIVE_CONTENT= ").append(isEnabled(GENERATIVE_CONTENT));
		sb.append("\n\t" + "SOURCE_STRUCT=      ").append(isEnabled(SOURCE_STRUCT));
		sb.append("\n\t" + "INLINE_MARKUP=      ").append(isEnabled(INLINE_MARKUP));
		sb.append("\n\t" + "INLINE_EMBEDDED=    ").append(isEnabled(INLINE_EMBEDDED));
		sb.append("\n\t" + "GENERATIVE_CONTENT= ").append(isEnabled(GENERATIVE_CONTENT));
		return sb.toString();
	}
	
}
