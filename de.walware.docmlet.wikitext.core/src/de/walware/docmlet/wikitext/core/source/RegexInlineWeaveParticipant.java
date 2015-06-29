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

package de.walware.docmlet.wikitext.core.source;

import java.util.regex.Pattern;


public class RegexInlineWeaveParticipant extends WeaveParticipant {
	
	
	private final String foreignTypeId;
	private final int embedDescr;
	
	private final Pattern pattern;
	
	
	public RegexInlineWeaveParticipant(final String foreignTypeId, final int embedDescr,
			final Pattern pattern) {
		this.foreignTypeId= foreignTypeId;
		this.embedDescr= embedDescr;
		
		this.pattern= pattern;
	}
	
	
	@Override
	public String getForeignTypeId() {
		return this.foreignTypeId;
	}
	
	@Override
	public int getEmbedDescr() {
		return this.embedDescr;
	}
	
	protected Pattern getPattern() {
		return this.pattern;
	}
	
}
