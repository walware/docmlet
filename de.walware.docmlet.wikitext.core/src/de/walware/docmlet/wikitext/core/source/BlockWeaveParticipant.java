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


public abstract class BlockWeaveParticipant extends WeaveParticipant {
	
	
	private static final int TEXT_LENGTH= 1 + REPLACEMENT_STRING.length();
	
	
	public BlockWeaveParticipant() {
	}
	
	
	public abstract boolean checkStartLine(int beginOffset, int endOffset);
	
	public abstract int getStartOffset();
	
	public abstract boolean checkEndLine(int beginOffset, int endOffset);
	
	protected void appendReplacement(final StringBuilder sb,
			final String source, final int beginOffset, final int endOffset) {
		sb.append(REPLACEMENT_CHAR);
		sb.append(REPLACEMENT_LINE);
	}
	
	protected int getTextLength() {
		return TEXT_LENGTH;
	}
	
}
