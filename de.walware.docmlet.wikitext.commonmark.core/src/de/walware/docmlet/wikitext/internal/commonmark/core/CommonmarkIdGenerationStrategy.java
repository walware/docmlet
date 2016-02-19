/*=============================================================================#
 # Copyright (c) 2015-2016 David Green and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     David Green - initial API and implementation in Mylyn
 #     Stephan Wahlbrink (WalWare.de) - revised API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.commonmark.core;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.markup.IdGenerationStrategy;


public class CommonmarkIdGenerationStrategy extends IdGenerationStrategy {
	
	
	private static final Pattern PATTERN= Pattern.compile("[^a-z0-9_]+");
	
	
	private final Matcher matcher= PATTERN.matcher("");
	
	
	@Override
	public String generateId(final String headingText) {
		final String id= headingText.toLowerCase(Locale.ENGLISH);
		final Matcher matcher= this.matcher.reset(id);
		
		boolean result = matcher.find();
		if (result) {
			final StringBuffer sb = new StringBuffer();
			int idx= 0;
			if (matcher.start() == 0) {
				idx= matcher.end();
				result= matcher.find();
			}
			while (result) {
				sb.append(id, idx, matcher.start());
				sb.append("-");
				idx= matcher.end();
				result = matcher.find();
			}
			if (idx < id.length()) {
				sb.append(id, idx, id.length());
			}
			else if (sb.length() == 0) {
				return "";
			}
			else {
				sb.deleteCharAt(sb.length() - 1);
			}
			return sb.toString();
		}
		return id;
	}
	
}
