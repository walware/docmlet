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

package de.walware.docmlet.wikitext.core.source.extdoc;

import java.util.regex.Pattern;

import de.walware.docmlet.wikitext.core.source.RegexInlineWeaveParticipant;


public class TexMathDollarsInlineWeaveParticipant extends RegexInlineWeaveParticipant {
	
	
	/**
	 * $...$, no whitespace after start/before end, no empty line, max 3 lines
	 * \$[^\$\s](?:[^\$\n]*(?:\n(?![\r \t]*\n)[^\$\n]*){0,2}[^\$\s])?\$
	 */
	private final static Pattern DEFAULT_PATTERN= Pattern.compile("(\\$[^\\$\\s](?:[^\\$\\n]*(?:\\n(?![\\r \\t]*\\n)[^\\$\\n]*){0,2}[^\\$\\s])?\\$)"); //$NON-NLS-1$
	
	
	/**
	 * 
	 * \$\$[^\$\s](?:[^\$\n]*(?:\n[^\$\n]*){0,2}[^\$\s])?\$\$
	 */
	private final static Pattern TEMPLATE_PATTERN= Pattern.compile("(\\$\\$[^\\$\\s](?:[^\\$\\n]*(?:\\n(?![\\r \\t]*\\n)[^\\$\\n]*){0,2}[^\\$\\s])?\\$\\$)"); //$NON-NLS-1$
	
	
	public TexMathDollarsInlineWeaveParticipant(final boolean templateMode) {
		super(IExtdocMarkupLanguage.EMBEDDED_LTX, IExtdocMarkupLanguage.EMBEDDED_TEX_MATH_DOLLARS_INLINE_DESCR,
				(templateMode) ? TEMPLATE_PATTERN : DEFAULT_PATTERN);
	}
	
}
