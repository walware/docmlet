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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;

import de.walware.ecommons.text.core.ILineInformation;


public class ContentLineSequence extends LineSequence {
	
	
	private final String content;
	private final ILineInformation lineInfos;
	
	private Line currentLine;
	
	private final List<Line> followingLines= new ArrayList<>();
	
	
	public ContentLineSequence(final String content, final ILineInformation lineInfos) {
		this.content= content;
		this.lineInfos= lineInfos;
		
		this.currentLine= createLine(0);
	}
	
	
	private Line createLine(final int lineNumber) {
		if (lineNumber >= this.lineInfos.getNumberOfLines()) {
			return null;
		}
		try {
			final int beginOffset= this.lineInfos.getLineOffset(lineNumber);
			int endOffset= this.lineInfos.getLineEndOffset(lineNumber);
			final String delimiter;
			switch ((endOffset > beginOffset) ? this.content.charAt(endOffset - 1) : 0) {
			case '\n':
				endOffset--;
				if (endOffset > beginOffset && this.content.charAt(endOffset - 1) == '\r') {
					endOffset--;
					delimiter= "\r\n";
				}
				else {
					delimiter= "\n";
				}
				break;
			case '\r':
				endOffset--;
				delimiter= "\r";
				break;
			default:
				delimiter= "";
				break;
			}
			
			return new Line(lineNumber, beginOffset, 0, this.content.substring(beginOffset, endOffset), delimiter);
		}
		catch (final BadLocationException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Line getCurrentLine() {
		return this.currentLine;
	}
	
	@Override
	public Line getNextLine() {
		return getNextLine(0);
	}
	
	public Line getNextLine(final int index) {
		checkArgument(index >= 0);
		if (this.currentLine != null) {
			while (index >= this.followingLines.size()) {
				final Line line= createLine(this.currentLine.getLineNumber() + this.followingLines.size() + 1);
				if (line == null) {
					break;
				}
				this.followingLines.add(line);
			}
		}
		return (index < this.followingLines.size()) ? this.followingLines.get(index) : null;
	}
	
	@Override
	public void advance() {
		this.currentLine= getNextLine(0);
		if (this.currentLine != null) {
			this.followingLines.remove(0);
		}
	}
	
	@Override
	public LineSequence lookAhead() {
		return new LookAheadLineSequence(this);
	}
	
}
