/*=============================================================================#
 # Copyright (c) 2015-2016 David Green and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     David Green - initial API and implementation in Mylyn
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.commonmark.core;


public class ToStringHelper {
	
	private static final String ELIPSES = "...";
	private static final int STRING_MAX_LENGTH = 20;
	
	
	public static String toStringValue(final String text) {
		if (text == null) {
			return null;
		}
		String stringValue = text;
		if (stringValue.length() > STRING_MAX_LENGTH) {
			stringValue = stringValue.substring(0, STRING_MAX_LENGTH) + ELIPSES;
		}
		return stringValue.replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r");
	}
	
	
	private ToStringHelper() {
	}
	
}
