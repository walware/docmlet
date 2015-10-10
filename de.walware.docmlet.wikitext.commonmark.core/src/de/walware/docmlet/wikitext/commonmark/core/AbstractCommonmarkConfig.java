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

package de.walware.docmlet.wikitext.commonmark.core;

import java.util.List;

import de.walware.docmlet.wikitext.core.source.extdoc.AbstractMarkupConfig;


public abstract class AbstractCommonmarkConfig<T extends AbstractCommonmarkConfig<? super T>> extends AbstractMarkupConfig<T>
		implements ICommonmarkConfig {
	
	
	public static final String HEADER_INTERRUPT_PARAGRAPH_DISABLED_PROP= "HeaderInterruptParagraphDisabled"; //$NON-NLS-1$
	private static final String HEADER_INTERRUPT_PARAGRAPH_DISABLED_KEY= "Paragraph+Header=Blank"; //$NON-NLS-1$
	
	public static final String BLOCKQUOTE_INTERRUPT_PARAGRAPH_DISABLED_PROP= "BlockquoteInterruptParagraphDisabled"; //$NON-NLS-1$
	private static final String BLOCKQUOTE_INTERRUPT_PARAGRAPH_DISABLED_KEY= "Paragraph+Blockquote=Blank"; //$NON-NLS-1$
	
	public static final String STRIKEOUT_DTILDE_ENABLED_PROP= "StrikeoutByDTildeEnabled"; //$NON-NLS-1$
	private static final String STRIKEOUT_DTILDE_KEY= "Strikeout=~~"; //$NON-NLS-1$
	
	public static final String SUPERSCRIPT_SCIRCUMFLEX_ENABLED_PROP= "SuperscriptBySCircumflexEnabled"; //$NON-NLS-1$
	private static final String SUPERSCRIPT_SCIRCUMFLEX_KEY= "Superscript=^"; //$NON-NLS-1$
	
	public static final String SUBSCRIPT_STILDE_ENABLED_PROP= "SubscriptBySTildeEnabled"; //$NON-NLS-1$
	private static final String SUBSCRIPT_STILDE_KEY= "Subscript=~"; //$NON-NLS-1$
	
	
	private boolean isHeaderInterruptParagraphDisabled;
	
	private boolean isBlockquoteInterruptParagraphDisabled;
	
	private boolean isStrikeoutDTildeEnabled;
	private boolean isSuperscriptSCircumflexEnabled;
	private boolean isSubscriptSTildeEnabled;
	
	
	public AbstractCommonmarkConfig() {
	}
	
	
	@Override
	protected String getConfigType() {
		return "Commonmark"; //$NON-NLS-1$
	}
	
	
	@Override
	public boolean isHeaderInterruptParagraphDisabled() {
		return this.isHeaderInterruptParagraphDisabled;
	}
	
	public void setHeaderInterruptParagraphDisabled(final boolean enabled) {
		checkSeal();
		if (this.isHeaderInterruptParagraphDisabled != enabled) {
			this.isHeaderInterruptParagraphDisabled= enabled;
			afterChange(HEADER_INTERRUPT_PARAGRAPH_DISABLED_PROP);
		}
	}
	
	@Override
	public boolean isBlockquoteInterruptParagraphDisabled() {
		return this.isBlockquoteInterruptParagraphDisabled;
	}
	
	public void setBlockquoteInterruptParagraphDisabled(final boolean enabled) {
		checkSeal();
		if (this.isBlockquoteInterruptParagraphDisabled != enabled) {
			this.isBlockquoteInterruptParagraphDisabled= enabled;
			afterChange(BLOCKQUOTE_INTERRUPT_PARAGRAPH_DISABLED_PROP);
		}
	}
	
	
	@Override
	public boolean isStrikeoutByDTildeEnabled() {
		return this.isStrikeoutDTildeEnabled;
	}
	
	public void setStrikeoutDTildeEnabled(final boolean enabled) {
		checkSeal();
		if (this.isStrikeoutDTildeEnabled != enabled) {
			this.isStrikeoutDTildeEnabled= enabled;
			afterChange(STRIKEOUT_DTILDE_ENABLED_PROP);
		}
	}
	
	@Override
	public boolean isSuperscriptBySCircumflexEnabled() {
		return this.isSuperscriptSCircumflexEnabled;
	}
	
	public void setSuperscriptSCircumflexEnabled(final boolean enabled) {
		checkSeal();
		if (this.isSuperscriptSCircumflexEnabled != enabled) {
			this.isSuperscriptSCircumflexEnabled= enabled;
			afterChange(SUPERSCRIPT_SCIRCUMFLEX_ENABLED_PROP);
		}
	}
	
	@Override
	public boolean isSubscriptBySTildeEnabled() {
		return this.isSubscriptSTildeEnabled;
	}
	
	public void setSubscriptSTildeEnabled(final boolean enabled) {
		checkSeal();
		if (this.isSubscriptSTildeEnabled != enabled) {
			this.isSubscriptSTildeEnabled= enabled;
			afterChange(SUBSCRIPT_STILDE_ENABLED_PROP);
		}
	}
	
	
	@Override
	protected void createConfigString(final StringBuilder sb) {
		super.createConfigString(sb);
		
		if (isHeaderInterruptParagraphDisabled()) {
			sb.append(HEADER_INTERRUPT_PARAGRAPH_DISABLED_KEY);
		}
		if (isBlockquoteInterruptParagraphDisabled()) {
			sb.append(BLOCKQUOTE_INTERRUPT_PARAGRAPH_DISABLED_KEY);
		}
		
		if (isStrikeoutByDTildeEnabled()) {
			sb.append(STRIKEOUT_DTILDE_KEY);
		}
		if (isSuperscriptBySCircumflexEnabled()) {
			sb.append(SUPERSCRIPT_SCIRCUMFLEX_KEY);                                                                                                       
		}
		if (isSubscriptBySTildeEnabled()) {
			sb.append(SUBSCRIPT_STILDE_KEY);
		}
	}
	
	@Override
	protected void load(final String configType, final List<String> s) {
		super.load(configType, s);
		
		setHeaderInterruptParagraphDisabled(s.contains(HEADER_INTERRUPT_PARAGRAPH_DISABLED_KEY));
		setBlockquoteInterruptParagraphDisabled(s.contains(BLOCKQUOTE_INTERRUPT_PARAGRAPH_DISABLED_KEY));
		
		setStrikeoutDTildeEnabled(s.contains(STRIKEOUT_DTILDE_KEY));
		setSuperscriptSCircumflexEnabled(s.contains(SUPERSCRIPT_SCIRCUMFLEX_KEY));
		setSubscriptSTildeEnabled(s.contains(SUBSCRIPT_STILDE_KEY));
	}
	
	@Override
	public void load(final T config) {
		super.load(config);
		
		setHeaderInterruptParagraphDisabled(config.isHeaderInterruptParagraphDisabled());
		setBlockquoteInterruptParagraphDisabled(config.isBlockquoteInterruptParagraphDisabled());
		
		setStrikeoutDTildeEnabled(config.isStrikeoutByDTildeEnabled());
		setSuperscriptSCircumflexEnabled(config.isSuperscriptBySCircumflexEnabled());
		setSubscriptSTildeEnabled(config.isSubscriptBySTildeEnabled());
	}
	
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof AbstractCommonmarkConfig
				&& super.equals(obj) ) {
			final AbstractCommonmarkConfig<?> other= (AbstractCommonmarkConfig<?>) obj;
			return (isHeaderInterruptParagraphDisabled() == other.isHeaderInterruptParagraphDisabled()
					&& isBlockquoteInterruptParagraphDisabled() == other.isBlockquoteInterruptParagraphDisabled()
					&& isStrikeoutByDTildeEnabled() == other.isStrikeoutByDTildeEnabled()
					&& isSuperscriptBySCircumflexEnabled() == other.isSuperscriptBySCircumflexEnabled()
					&& isSubscriptBySTildeEnabled() == other.isSubscriptBySTildeEnabled() );
		}
		return false;
	}
	
}
