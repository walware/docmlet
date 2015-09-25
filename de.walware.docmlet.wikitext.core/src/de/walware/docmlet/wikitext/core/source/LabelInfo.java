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

package de.walware.docmlet.wikitext.core.source;


public class LabelInfo {
	
	
	private final String label;
	
	private final int beginOffset;
	private final int endOffset;
	
	
	public LabelInfo(final String label, final int beginOffset, final int endOffset) {
		this.label= label;
		this.beginOffset= beginOffset;
		this.endOffset= endOffset;
	}
	
	
	public String getLabel() {
		return this.label;
	}
	
	public int getOffset() {
		return this.beginOffset;
	}
	
	public int getEndOffset() {
		return this.endOffset;
	}
	
	public int getLength() {
		return this.endOffset - this.beginOffset;
	}
	
}
