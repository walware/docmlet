/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.commonmark.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import de.walware.docmlet.wikitext.commonmark.core.AbstractCommonmarkConfig;
import de.walware.docmlet.wikitext.internal.commonmark.ui.Messages;
import de.walware.docmlet.wikitext.ui.config.AbstractMarkupConfigDialog;


public class AbstractCommonmarkConfigDialog<T extends AbstractCommonmarkConfig<? super T>> extends AbstractMarkupConfigDialog<T> {
	
	
	public AbstractCommonmarkConfigDialog(final Shell parent,
			final String contextLabel, final boolean isContextEnabled, final T customConfig) {
		super(parent, contextLabel, isContextEnabled, customConfig);
	}
	
	
	@Override
	protected void addProperty(final Composite parent, final String propertyName) {
		switch (propertyName) {
		case AbstractCommonmarkConfig.HEADER_INTERRUPT_PARAGRAPH_DISABLED_PROP:
			addProperty(parent, propertyName,
					Messages.MarkupConfig_BlankBeforeHeader_Enable_label );
			return;
		case AbstractCommonmarkConfig.BLOCKQUOTE_INTERRUPT_PARAGRAPH_DISABLED_PROP:
			addProperty(parent, propertyName,
					Messages.MarkupConfig_BlankBeforeBlockquote_Enable_label );
			return;
		case AbstractCommonmarkConfig.STRIKEOUT_DTILDE_ENABLED_PROP:
			addProperty(parent, propertyName,
					Messages.MarkupConfig_StrikeoutByDTilde_Enable_label );
			return;
		case AbstractCommonmarkConfig.SUPERSCRIPT_SCIRCUMFLEX_ENABLED_PROP:
			addProperty(parent, propertyName,
					Messages.MarkupConfig_SuperscriptBySCircumflex_Enable_label );
			return;
		case AbstractCommonmarkConfig.SUBSCRIPT_STILDE_ENABLED_PROP:
			addProperty(parent, propertyName,
					Messages.MarkupConfig_SubscriptBySTilde_Enable_label );
			return;
		}
		super.addProperty(parent, propertyName);
	}
	
}
