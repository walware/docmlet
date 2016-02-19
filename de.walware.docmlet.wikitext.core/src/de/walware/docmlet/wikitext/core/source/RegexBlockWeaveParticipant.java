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

package de.walware.docmlet.wikitext.core.source;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.walware.ecommons.ltk.core.SourceContent;


public class RegexBlockWeaveParticipant extends BlockWeaveParticipant {
	
	
	private final String foreignTypeId;
	private final byte embedDescr;
	
	private final Matcher startMatcher;
	private final Matcher endMatcher;
	
	
	public RegexBlockWeaveParticipant(final String foreignTypeId, final byte embedDescr,
			final Pattern startPattern, final Pattern endPattern) {
		this.foreignTypeId= foreignTypeId;
		this.embedDescr= embedDescr;
		
		this.startMatcher= startPattern.matcher(""); //$NON-NLS-1$
		this.endMatcher= endPattern.matcher(""); //$NON-NLS-1$
	}
	
	
	@Override
	public String getForeignTypeId() {
		return this.foreignTypeId;
	}
	
	@Override
	public int getEmbedDescr() {
		return this.embedDescr;
	}
	
	@Override
	public void reset(final SourceContent source) {
		final String text= (source != null) ? source.getText() : ""; //$NON-NLS-1$
		this.startMatcher.reset(text);
		this.endMatcher.reset(text);
	}
	
	@Override
	public boolean checkStartLine(final int beginOffset, final int endOffset) {
		this.startMatcher.region(beginOffset, endOffset);
		return this.startMatcher.matches();
	}
	
	@Override
	public int getStartOffset() {
		return this.startMatcher.start();
	}
	
	@Override
	public boolean checkEndLine(final int beginOffset, final int endOffset) {
		this.endMatcher.region(beginOffset, endOffset);
		return this.endMatcher.matches();
	}
	
}
