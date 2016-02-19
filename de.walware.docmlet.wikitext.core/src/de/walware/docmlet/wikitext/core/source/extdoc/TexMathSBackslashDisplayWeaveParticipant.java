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

package de.walware.docmlet.wikitext.core.source.extdoc;

import java.util.regex.Pattern;

import de.walware.docmlet.wikitext.core.source.RegexInlineWeaveParticipant;


public class TexMathSBackslashDisplayWeaveParticipant extends RegexInlineWeaveParticipant {
	
	
	/**
	 * \(...\), no empty line
	 * \\\[(?:(?!\n[\r \t]*\n|\\\))\p{all})+\\\]
	 */
	private final static Pattern DEFAULT_PATTERN= Pattern.compile("(\\\\\\[(?:(?!\\n[\\r \\t]*\\n|\\\\\\))\\p{all})+\\\\\\])"); //$NON-NLS-1$
	
	
	public TexMathSBackslashDisplayWeaveParticipant() {
		super(IExtdocMarkupLanguage.EMBEDDED_LTX, IExtdocMarkupLanguage.EMBEDDED_TEX_MATH_SBACKSLASH_DISPLAY_DESCR,
				DEFAULT_PATTERN);
	}
	
}
