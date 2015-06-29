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

import org.eclipse.mylyn.internal.wikitext.core.util.css.CssRule;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.CssStyleManager;
import org.eclipse.mylyn.internal.wikitext.ui.viewer.FontState;
import org.eclipse.swt.custom.StyleRange;

import de.walware.docmlet.wikitext.ui.sourceediting.StyleConfig;


public class MarkupCssStyleManager extends CssStyleManager {
	
	
	private final boolean disableFontConfig;
	
	
	public MarkupCssStyleManager(final StyleConfig config) {
		super(config.getDefaultFont(), config.getMonospaceFont());
		
		this.disableFontConfig= config.isFixedLineHeight();
	}
	
	
	@Override
	public void processCssStyles(final FontState fontState, final FontState parentFontState, final CssRule rule) {
		if (this.disableFontConfig && 
				(RULE_FONT_SIZE.equals(rule.name) || RULE_FONT_FAMILY.equals(rule.name)) ) {
			return;
		}
		super.processCssStyles(fontState, parentFontState, rule);
	}
	
	@Override
	public StyleRange createStyleRange(final FontState fontState, final int offset, final int length) {
//		if (this.disableFontSize && fontState.sizeFactor != 1.0f) {
//			fontState= new FontState(fontState);
//			fontState.sizeFactor= 1.0f;
//		}
		return super.createStyleRange(fontState, offset, length);
	}
	
}
