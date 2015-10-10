/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.commonmark.core;

import java.util.regex.Pattern;

public class CommonRegex {
	
	
	public static final String TAG_NAME_REGEX= "\\p{Alpha}[\\p{Alnum}-]*";
	
	public static final String ATTR_NAME_REGEX= "[\\p{Alpha}_:][\\p{Alnum}_.:-]*";
	public static final String ATTR_VALUE_D_QUOTED= "\"[^\"]*\"";
	public static final String ATTR_VALUE_S_QUOTED= "'[^']*'";
	public static final String ATTR_VALUE_U_QUOTED= "[^\\s\"'=><`]+";
	public static final String ATTR_VALUE_REGEX= ATTR_VALUE_D_QUOTED + "|" + ATTR_VALUE_S_QUOTED + "|" + ATTR_VALUE_U_QUOTED;
	public static final String ATTR_SPEC_REGEX= "\\s+" + ATTR_NAME_REGEX + "(?:\\s*=\\s*(?:" + ATTR_VALUE_REGEX + "))?";
	
	public static final String COMMENT_START1_REGEX= "!--";
	public static final String COMMENT_END_REGEX= "-->";
	public static final String COMMENT_1_REGEX= COMMENT_START1_REGEX +
			"(?:" + COMMENT_END_REGEX + "|-?[^>-](?:-?[^-])*" + COMMENT_END_REGEX + ")";
	
	public static final String PI_START1_REGEX= "\\?";
	public static final String PI_END_REGEX= "\\?>";
	public static final String PI_1_REGEX= PI_START1_REGEX + ".*?" + PI_END_REGEX;
	
	public static final String DECL_START1_REGEX= "!\\p{Upper}+";
	public static final String DECL_END_REGEX= ">";
	public static final String DECL_1_REGEX= DECL_START1_REGEX + "\\s[^>]*" + DECL_END_REGEX;
	
	public static final String CDATA_START1_REGEX= "!\\[CDATA\\[";
	public static final String CDATA_END_REGEX= "\\]\\]>";
	public static final String CDATA_1_REGEX= CDATA_START1_REGEX + ".*?" + CDATA_END_REGEX;
	
	public static final String OPEN_TAG_1_REGEX= TAG_NAME_REGEX +
			"(?:" + ATTR_SPEC_REGEX + ")" +
			"*\\s*/?>";
	public static final String CLOSE_TAG_1_REGEX="/" + TAG_NAME_REGEX +
			"\\s*>";
	
	public static final String HTML_ENTITY_REGEX=
			"&(#[Xx]\\p{XDigit}{1,8}|#\\p{Digit}{1,8}|\\p{Alpha}\\p{Alnum}{1,31});";
	
	public static final Pattern HTML_ENTITY_PATTERN= Pattern.compile(HTML_ENTITY_REGEX,
			Pattern.DOTALL );
	
	
	public static final String CNTRL_OR_SPACE= "\\x00-\\x20\\x7F";
	
//	public static final String LINE_END= "\n|\r\n?|$";
	public static final String LINE_END= "\n|$";
	
	
}
