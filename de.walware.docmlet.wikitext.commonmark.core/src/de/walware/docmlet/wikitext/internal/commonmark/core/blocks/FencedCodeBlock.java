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

package de.walware.docmlet.wikitext.internal.commonmark.core.blocks;

import static com.google.common.base.Preconditions.checkState;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;

import de.walware.jcommons.collections.ImList;

import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.LineSequence;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlock;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlockItem;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlocks.SourceBlockBuilder;


public class FencedCodeBlock extends SourceBlock {
	
	
	private static final Pattern START_PATTERN= Pattern.compile(
			"(?:`{3,}|~{3,})[^`]*",
			Pattern.DOTALL );
	
	private static final Pattern OPEN_PATTERN= Pattern.compile(
			"(`{3,}|~{3,})[ \t]*([^ \t`]+)?.*",
			Pattern.DOTALL );
	
	private static final Pattern CLOSE_BACKTICK_PATTERN= Pattern.compile(
			"(`{3,})[ \t]*",
			Pattern.DOTALL );
	private static final Pattern CLOSE_TILDE_PATTERN= Pattern.compile(
			"(~{3,})[ \t]*",
			Pattern.DOTALL );
	
	
	static final class CodeBlockItem extends SourceBlockItem<FencedCodeBlock> {
		
		private String infoText;
		private boolean isClosed;
		
		public CodeBlockItem(final FencedCodeBlock type, final SourceBlockBuilder builder) {
			super(type, builder);
		}
		
	}
	
	
	private final Matcher startMatcher= START_PATTERN.matcher("");
	
	private Matcher openMatcher;
	private Matcher closeBacktickMatcher;
	private Matcher closeTildeMatcher;
	
	
	public FencedCodeBlock() {
	}
	
	
	@Override
	public boolean canStart(final LineSequence lineSequence) {
		final Line currentLine= lineSequence.getCurrentLine();
		return (currentLine != null
				&& !currentLine.isBlank() && currentLine.getIndent() < 4
				&& currentLine.setupIndent(this.startMatcher).matches() );
	}
	
	@Override
	public void createItem(final SourceBlockBuilder builder, final LineSequence lineSequence) {
		final CodeBlockItem codeBlockItem= new CodeBlockItem(this, builder);
		
		final Line startLine= lineSequence.getCurrentLine();
		lineSequence.advance();
		
		final Matcher openMatcher= startLine.setup(getOpenMatcher(), true, false);
		checkState(openMatcher.matches());
		final int minCount= openMatcher.end(1) - openMatcher./*start(1)*/regionStart();
		codeBlockItem.infoText= getInfoText(openMatcher);
		final Matcher closeMatcher= getCloseMatcher(startLine, openMatcher);
		
		while (true) {
			final Line line= lineSequence.getCurrentLine();
			if (line != null) {
				lineSequence.advance();
				
				if (!line.isBlank() && line.getIndent() < 4
						&& (line.setupIndent(closeMatcher)).matches()
						&& closeMatcher.end(1) - closeMatcher./*start(1)*/regionStart() >= minCount ) {
					codeBlockItem.isClosed= true;
					break;
				}
				continue;
			}
			break;
		}
	}
	
	@Override
	public void initializeContext(final ProcessingContext context, final SourceBlockItem<?> blockItem) {
	}
	
	@Override
	public void emit(final ProcessingContext context, final SourceBlockItem<?> blockItem,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		final CodeBlockItem codeBlockItem= (CodeBlockItem) blockItem;
		final ImList<Line> lines= blockItem.getLines();
		
		final Line startLine= lines.get(0);
		final Matcher openMatcher= startLine.setup(getOpenMatcher(), true, false);
		checkState(openMatcher.matches());
		
		final Attributes codeAttributes= new Attributes();
		if (codeBlockItem.infoText != null) {
			final String language= context.getHelper().replaceEscaping(codeBlockItem.infoText);
			codeAttributes.setCssClass("language-" + language);
		}
		locator.setBlockBegin(blockItem);
		builder.beginBlock(BlockType.CODE, codeAttributes);
		
		final int startIndent= startLine.getIndent();
		for (final Line line : lines.subList(1, (codeBlockItem.isClosed) ? lines.size() - 1 : lines.size())) {
			final Line codeSegment;
			if (startIndent > 0 && line.getIndent() > 0) {
				codeSegment= line.segmentByIndent(Math.min(startIndent, line.getIndent()));
			}
			else {
				codeSegment= line;
			}
			locator.setLine(codeSegment);
			builder.characters(codeSegment.getText());
			builder.characters("\n");
		}
		
		locator.setBlockEnd(blockItem);
		builder.endBlock();
	}
	
	
	private String getInfoText(final Matcher matcher) {
		final String infoText= matcher.group(2);
		if (infoText != null && !infoText.isEmpty()) {
			return infoText;
		}
		return null;
	}
	
	private Matcher getOpenMatcher() {
		if (this.openMatcher == null) {
			this.openMatcher= OPEN_PATTERN.matcher("");
		}
		return this.openMatcher;
	}
	
	private Matcher getCloseMatcher(final Line line, final Matcher matcher) {
		switch (line.getText().charAt(matcher.start(1))) {
		case '`':
			if (this.closeBacktickMatcher == null) {
				this.closeBacktickMatcher= CLOSE_BACKTICK_PATTERN.matcher("");
			}
			return this.closeBacktickMatcher;
		case '~':
			if (this.closeTildeMatcher == null) {
				this.closeTildeMatcher= CLOSE_TILDE_PATTERN.matcher("");
			}
			return this.closeTildeMatcher;
		default:
			throw new IllegalStateException();
		}
	}
	
}
