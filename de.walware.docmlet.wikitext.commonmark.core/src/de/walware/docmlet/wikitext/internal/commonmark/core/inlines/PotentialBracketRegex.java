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

package de.walware.docmlet.wikitext.internal.commonmark.core.inlines;

import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.CNTRL_OR_SPACE;
import static de.walware.docmlet.wikitext.internal.commonmark.core.CommonRegex.LINE_END;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PotentialBracketRegex {
	
	private static final String D_QUOTED_TITLE_REGEX= "\"(?:\\\\.|[^\"])*\"";
	private static final String S_QUOTED_TITLE_REGEX= "'(?:\\\\.|[^'])*'";
	private static final String PARENS_TITLE_REGEX= "\\((?:\\\\.|[^\\)])*\\)";
	
	static final String TITLE_REGEX= D_QUOTED_TITLE_REGEX + "|" + S_QUOTED_TITLE_REGEX + "|" + PARENS_TITLE_REGEX;
	
	static final String BRACKETS_URI_REGEX= "<((?:\\\\[^\r\n]|[^<>\r\n]|)*)>";
	
	private static final String INNER_PARENS_REGEX= "\\((?:\\\\[^" + CNTRL_OR_SPACE + "]|[^" + CNTRL_OR_SPACE + "()])*\\)";
	static final String NOBRACKET_URI_REGEX= "((?:\\\\[^" + CNTRL_OR_SPACE + "]|[^" + CNTRL_OR_SPACE + "()]|" + INNER_PARENS_REGEX + ")+)";
	
	static final String URI_M2_REGEX= "(?:" + BRACKETS_URI_REGEX + "|" + NOBRACKET_URI_REGEX + ")";
	
	private static final Pattern END_PATTERN= Pattern.compile("\\(" +
				"\\s*" + URI_M2_REGEX + "?(?:\\s+(" + TITLE_REGEX + "))?" +
				"\\s*" + "(\\)).*",
			Pattern.DOTALL );
	
	private static final Pattern REFERENCE_DEFINITION_END_PATTERN= Pattern.compile("\\:" + 
				"\\s*" + URI_M2_REGEX + "" +
				"(?:\\s+(" + TITLE_REGEX + ")" + "[ \t]*(?=" + LINE_END + "))?" +
				"[ \t]*(" + LINE_END + ").*",
			Pattern.DOTALL );
	
	private static final String REFERENCE_NAME_REGEX= "(?:\\\\.|[^\\]]){0,1000}";
	
	private static final Pattern REFERENCE_LABEL_PATTERN= Pattern.compile(
			"(\\s*\\[(" + REFERENCE_NAME_REGEX + ")\\]).*",
			Pattern.DOTALL );
	
	private static final Pattern REFERENCE_NAME_PATTERN= Pattern.compile(REFERENCE_NAME_REGEX,
			Pattern.DOTALL );
	
	
	private final Matcher endMatcher= END_PATTERN.matcher("");
	private final Matcher referenceDefinitionEndMatcher= REFERENCE_DEFINITION_END_PATTERN.matcher("");
	private final Matcher referenceLabelMatcher= REFERENCE_LABEL_PATTERN.matcher("");
	private final Matcher referenceNameMatcher= REFERENCE_NAME_PATTERN.matcher("");
	
	
	public Matcher getEndMatcher() {
		return this.endMatcher;
	}
	
	public Matcher getReferenceDefinitionEndMatcher() {
		return this.referenceDefinitionEndMatcher;
	}
	
	public Matcher getReferenceLabelMatcher() {
		return this.referenceLabelMatcher;
	}
	
	public Matcher getReferenceNameMatcher() {
		return this.referenceNameMatcher;
	}
	
}
