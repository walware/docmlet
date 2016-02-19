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

package de.walware.docmlet.wikitext.ui.sourceediting;

import org.eclipse.swt.graphics.Font;


public class StyleConfig {
	
	
	private final Font defaultFont;
	private final Font monospaceFont;
	
	private final boolean isFixedLineHeight;
	
	
	/**
	 * @param defaultFont the default font, must not be null.
	 * @param defaultMonospaceFont the default monospace font, or null if a suitable default should
	 *     be selected
	 * @param isFixedLineHeight if font size styles should be processed
	 */
	public StyleConfig(final Font defaultFont, final Font monospaceFont,
			final boolean isFixedLineHeight) {
		if (defaultFont == null) {
			throw new NullPointerException("defaultFont"); //$NON-NLS-1$
		}
		this.defaultFont= defaultFont;
		this.monospaceFont= monospaceFont;
		this.isFixedLineHeight= isFixedLineHeight;
	}
	
	
	public Font getDefaultFont() {
		return this.defaultFont;
	}
	
	public Font getMonospaceFont() {
		return this.monospaceFont;
	}
	
	public boolean isFixedLineHeight() {
		return this.isFixedLineHeight;
	}
	
}
