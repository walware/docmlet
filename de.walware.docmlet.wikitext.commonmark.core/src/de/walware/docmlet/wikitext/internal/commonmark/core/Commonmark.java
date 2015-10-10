/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #     Stephan Wahlbrink (WalWare.de) - revised API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.commonmark.core;

import java.util.ArrayList;
import java.util.List;

import de.walware.ecommons.collections.ImCollections;

import de.walware.docmlet.wikitext.commonmark.core.CommonmarkLanguage;
import de.walware.docmlet.wikitext.commonmark.core.ICommonmarkConfig;
import de.walware.docmlet.wikitext.internal.commonmark.core.blocks.AtxHeaderBlock;
import de.walware.docmlet.wikitext.internal.commonmark.core.blocks.BlockQuoteBlock;
import de.walware.docmlet.wikitext.internal.commonmark.core.blocks.EmptyBlock;
import de.walware.docmlet.wikitext.internal.commonmark.core.blocks.FencedCodeBlock;
import de.walware.docmlet.wikitext.internal.commonmark.core.blocks.HorizontalRuleBlock;
import de.walware.docmlet.wikitext.internal.commonmark.core.blocks.HtmlBlock;
import de.walware.docmlet.wikitext.internal.commonmark.core.blocks.IndentedCodeBlock;
import de.walware.docmlet.wikitext.internal.commonmark.core.blocks.ListBlock;
import de.walware.docmlet.wikitext.internal.commonmark.core.blocks.ParagraphBlock;
import de.walware.docmlet.wikitext.internal.commonmark.core.blocks.SetextHeaderBlock;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.AutoLinkSpan;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.AutoLinkWithoutDemarcationSpan;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.BackslashEscapeSpan;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.CodeSpan;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.HtmlEntitySpan;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.HtmlTagSpan;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.InlineParser;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.LineBreakSpan;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.PotentialBracketSpan;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.PotentialStyleSpan;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.SourceSpan;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.StringCharactersSpan;


public class Commonmark {
	
	
	public static class MylynLanguage extends CommonmarkLanguage {
		
		public MylynLanguage() {
			super("Mylyn", CommonmarkLanguage.MYLYN_COMPAT_MODE, null);
		}
		
	}
	
	
	public static SourceBlocks newSourceBlocks() {
		return new SourceBlocks(
				new BlockQuoteBlock(),
				new AtxHeaderBlock(),
				new HorizontalRuleBlock(),
				new ListBlock(),
				new SetextHeaderBlock(),
				new FencedCodeBlock(),
				new IndentedCodeBlock(),
				new HtmlBlock(),
				new ParagraphBlock(),
				new EmptyBlock() );
	}
	
	public static SourceBlocks newSourceBlocks(final ICommonmarkConfig config) {
		if (config == null) {
			return newSourceBlocks();
		}
		
		final List<Class<? extends SourceBlock>> interruptParagraphExclusions;
		if (config.isHeaderInterruptParagraphDisabled()
				|| config.isBlockquoteInterruptParagraphDisabled() ) {
			interruptParagraphExclusions= new ArrayList<>();
			interruptParagraphExclusions.addAll(ParagraphBlock.DEFAULT_INTERRUPT_EXCLUSIONS);
			if (config.isHeaderInterruptParagraphDisabled()) {
				interruptParagraphExclusions.add(AtxHeaderBlock.class);
			}
			if (config.isBlockquoteInterruptParagraphDisabled()) {
				interruptParagraphExclusions.add(BlockQuoteBlock.class);
			}
		}
		else {
			interruptParagraphExclusions= ParagraphBlock.DEFAULT_INTERRUPT_EXCLUSIONS;
		}
		
		return new SourceBlocks(
				new BlockQuoteBlock(),
				new AtxHeaderBlock(),
				new HorizontalRuleBlock(),
				new ListBlock(),
				new SetextHeaderBlock(),
				new FencedCodeBlock(),
				new IndentedCodeBlock(),
				new HtmlBlock(),
				new ParagraphBlock(interruptParagraphExclusions),
				new EmptyBlock() );
	}
	
	public static InlineParser newInlineParser() {
		return newInlineParserCommonMarkStrict();
	}
	
	
	public static InlineParser newInlineParserCommonMarkStrict() {
		return newInlineParserCommonMark(null);
	}
	
	public static InlineParser newInlineParserCommonMark(final ICommonmarkConfig config) {
		final ArrayList<SourceSpan> spans = new ArrayList<>();
		spans.add(new LineBreakSpan());
		spans.add(new BackslashEscapeSpan());
		spans.add(new CodeSpan());
		spans.add(new AutoLinkSpan());
		spans.add(new HtmlTagSpan());
		spans.add(new HtmlEntitySpan());
		final PotentialStyleSpan styleSpan= (config != null) ?
				new PotentialStyleSpan(
						config.isStrikeoutByDTildeEnabled(),
						config.isSuperscriptBySCircumflexEnabled(),
						config.isSubscriptBySTildeEnabled() ) :
				new PotentialStyleSpan();
		spans.add(styleSpan);
		spans.add(new PotentialBracketSpan());
		spans.add(new StringCharactersSpan(styleSpan.getControlChars()));
		return new InlineParser(ImCollections.toList(spans));
	}
	
	public static InlineParser newInlineParserMarkdown() {
		final ArrayList<SourceSpan> spans = new ArrayList<>();
		spans.add(new LineBreakSpan());
		spans.add(new BackslashEscapeSpan());
		spans.add(new CodeSpan());
		spans.add(new AutoLinkSpan());
		spans.add(new AutoLinkWithoutDemarcationSpan());
		spans.add(new HtmlTagSpan());
		spans.add(new HtmlEntitySpan());
		spans.add(new PotentialStyleSpan());
		spans.add(new PotentialBracketSpan());
		spans.add(new StringCharactersSpan("h"));
		return new InlineParser(ImCollections.toList(spans));
	}
	
}
