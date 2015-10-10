/*=============================================================================#
 # Copyright (c) 2015 David Green and others.
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

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.mylyn.wikitext.core.parser.Locator;

import de.walware.ecommons.collections.ImList;

import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.Inline;


public class CommonmarkLocator implements Locator {
	
	
	private int lineNumber;
	
	private int lineOffset;
	
	private int lineLength;
	
	private int lineSegmentStartOffset;
	
	private int lineSegmentEndOffset;
	
	
	public CommonmarkLocator() {
	}
	
	
	public void setLine(final Line line) {
		checkNotNull(line);
		this.lineNumber= line.getLineNumber() + 1;
		this.lineOffset= line.getOffset();
		this.lineLength= line.getLength();
		this.lineSegmentStartOffset= 0;
		this.lineSegmentEndOffset= this.lineLength;
	}
	
	public void setBlockBegin(final SourceBlockItem<?> blockItem) {
		final ImList<Line> lines= blockItem.getLines();
		setLine(lines.get(0));
	}
	
	public void setBlockEnd(final SourceBlockItem<?> blockItem) {
		final ImList<Line> lines= blockItem.getLines();
		final Line lastLine= lines.get(lines.size() - 1);
		this.lineNumber= lastLine.getLineNumber();
		this.lineOffset= lastLine.getOffset() + lastLine.getLength();
		this.lineLength= 0;
		this.lineSegmentStartOffset= 0;
		this.lineSegmentEndOffset= 0;
	}
	
	public void setInline(final Inline inline) {
		final Line line= inline.getLine();
		this.lineNumber= line.getLineNumber() + 1;
		this.lineOffset= line.getOffset();
		this.lineLength= line.getText().length();
		this.lineSegmentStartOffset= inline.getOffset() - this.lineOffset;
		this.lineSegmentEndOffset= this.lineSegmentStartOffset + inline.getLength();
	}
	
	
	@Override
	public int getLineNumber() {
		return this.lineNumber;
	}
	
	@Override
	public int getLineDocumentOffset() {
		return this.lineOffset;
	}
	
	@Override
	public int getDocumentOffset() {
		return getLineDocumentOffset() + getLineCharacterOffset();
	}
	
	@Override
	public int getLineLength() {
		return this.lineLength;
	}
	
	@Override
	public int getLineCharacterOffset() {
		return this.lineSegmentStartOffset;
	}
	
	@Override
	public int getLineSegmentEndOffset() {
		return this.lineSegmentEndOffset;
	}
	
	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(Locator.class)
				.add("lineNumber", this.lineNumber)
				.add("lineDocumentOffset", this.lineOffset)
				.add("lineLength", this.lineLength)
				.add("lineCharacterOffset", this.lineSegmentStartOffset)
				.add("lineSegmentEndOffset", this.lineSegmentEndOffset)
				.toString();
	}
	
}
