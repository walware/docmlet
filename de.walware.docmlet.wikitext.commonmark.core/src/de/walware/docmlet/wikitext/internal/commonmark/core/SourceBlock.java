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

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlocks.SourceBlockBuilder;


public abstract class SourceBlock {
	
	
	protected static final void advanceLinesUpto(final LineSequence lineSequence, final int endLineNumber) {
		while (true) {
			final Line line= lineSequence.getCurrentLine();
			if (line != null && line.getLineNumber() < endLineNumber) {
				lineSequence.advance();
				continue;
			}
			break;
		}
	}
	
	protected static final void advanceBlankLines(final LineSequence lineSequence) {
		while (true) {
			final Line line= lineSequence.getCurrentLine();
			if (line != null && line.isBlank()) {
				lineSequence.advance();
				continue;
			}
			break;
		}
	}
	
	protected static final void advanceNonBlankLines(final LineSequence lineSequence) {
		while (true) {
			final Line line= lineSequence.getCurrentLine();
			if (line != null && !line.isBlank()) {
				lineSequence.advance();
				continue;
			}
			break;
		}
	}
	
	
	public abstract boolean canStart(LineSequence lineSequence);
	
	public abstract void createItem(SourceBlockBuilder builder, LineSequence lineSequence);
	
	public abstract void initializeContext(ProcessingContext context, SourceBlockItem<?> blockItem);
	
	public abstract void emit(ProcessingContext context,
			SourceBlockItem<?> blockItem, CommonmarkLocator locator, DocumentBuilder builder);
	
	
}
