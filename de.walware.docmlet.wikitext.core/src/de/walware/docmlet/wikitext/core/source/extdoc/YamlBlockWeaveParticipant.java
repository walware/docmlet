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

import de.walware.ecommons.ltk.core.SourceContent;

import de.walware.docmlet.wikitext.core.source.BlockWeaveParticipant;


public class YamlBlockWeaveParticipant extends BlockWeaveParticipant {
	
	
	private SourceContent sourceContent;
	
	private int start;
	private int startMarker;
	
	
	public YamlBlockWeaveParticipant() {
	}
	
	
	@Override
	public String getForeignTypeId() {
		return IExtdocMarkupLanguage.EMBEDDED_YAML;
	}
	
	@Override
	public int getEmbedDescr() {
		return IExtdocMarkupLanguage.EMBEDDED_YAML_METADATA_CHUNK_DESCR;
	}
	
	@Override
	public void reset(final SourceContent content) {
		this.sourceContent= content;
		this.start= -1;
	}
	
	@Override
	public boolean checkStartLine(final int beginOffset, final int endOffset) {
		final String text= this.sourceContent.getText();
		int offset= beginOffset;
		
		switch (endOffset - beginOffset) {
		case 1:
			if (text.charAt(offset) == '\n') {
				offset++;
				break;
			}
			return false;
		case 2:
			if (text.charAt(offset) == '\r' && text.charAt(offset + 1) == '\n') {
				offset+= 2;
				break;
			}
			return false;
		default:
			if (beginOffset == 0 && this.sourceContent.getBeginOffset() == 0) {
				break;
			}
			return false;
		}
		
		if (text.regionMatches(offset, "---", 0, 3)) { //$NON-NLS-1$
			this.start= beginOffset;
			this.startMarker= offset;
			return true;
		}
		
		return false;
	}
	
	@Override
	public int getStartOffset() {
		return this.start;
	}
	
	@Override
	public boolean checkEndLine(final int beginOffset, final int endOffset) {
		if (beginOffset <= this.startMarker) {
			return false;
		}
		final String text= this.sourceContent.getText();
		return text.regionMatches(beginOffset, "---", 0, 3) //$NON-NLS-1$
				|| text.regionMatches(beginOffset, "...", 0, 3); //$NON-NLS-1$
	}
	
	@Override
	protected void appendReplacement(final StringBuilder sb,
			final String source, final int beginOffset, final int endOffset) {
		sb.append("\n\n"); //$NON-NLS-1$
	}
	
	@Override
	protected int getTextLength() {
		return 0;
	}
	
}
