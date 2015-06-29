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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.Locator;

import de.walware.ecommons.ltk.core.SourceContent;
import de.walware.ecommons.text.ILineInformation;

import de.walware.docmlet.wikitext.core.markup.IWikitextLocator;


public class WeaveLanguageProcessor extends DocumentBuilder {
	
	
	public static final byte MODE_BLOCKS_ONLY= 0;
	public static final byte MODE_BLOCKS_AND_INLINE= 1;
	public static final byte MODE_ORG= 4;
	
	private static final boolean DEBUGGING= false;
	
	
	private static abstract class Embedded {
		
		
		/** begin offset in checked content */
		private final int beginOffset;
		/** end offset in checked content */
		private final int endOffset;
		
		private final int embedDescr;
		
		
		public Embedded(final int beginOffset, final int endOffset, final int embedDescr) {
			this.beginOffset= beginOffset;
			this.endOffset= endOffset;
			
			this.embedDescr= embedDescr;
		}
		
		
		public abstract WeaveParticipant getParticipant();
		
		public final int getEmbedDescr() {
			return this.embedDescr;
		}
		
		public final int getBeginOffset() {
			return this.beginOffset;
		}
		
		public final int getEndOffset() {
			return this.endOffset;
		}
		
		public final int getLength() {
			return this.endOffset - this.beginOffset;
		}
		
		public abstract int getOrgLength();
		
		public abstract int getTextLength();
		
	}
	
	private static final class EmbeddedChunk extends Embedded {
		
		
		private final BlockWeaveParticipant participant;
		
		private final int orgBeginOffset;
		private final int orgEndOffset;
		
		
		public EmbeddedChunk(final BlockWeaveParticipant participant,
				final int beginOffset, final int endOffset,
				final int orgBeginOffset, final int orgEndOffset) {
			super(beginOffset, endOffset, participant.getEmbedDescr());
			this.participant= participant;
			
			this.orgBeginOffset= orgBeginOffset;
			this.orgEndOffset= orgEndOffset;
		}
		
		
		@Override
		public BlockWeaveParticipant getParticipant() {
			return this.participant;
		}
		
		public int getShift() {
			return this.orgEndOffset - getEndOffset();
		}
		
		public int getOrgBeginOffset() {
			return this.orgBeginOffset;
		}
		
		public int getOrgEndOffset() {
			return this.orgEndOffset;
		}
		
		@Override
		public int getOrgLength() {
			return this.orgEndOffset - this.orgBeginOffset;
		}
		
		@Override
		public int getTextLength() {
			return getParticipant().getTextLength();
		}
		
	}
	
	private static final class EmbeddedInline extends Embedded {
		
		
		private final RegexInlineWeaveParticipant participant;
		
		private final int contentBeginOffset;
		private final int contentEndOffset;
		
		
		public EmbeddedInline(final RegexInlineWeaveParticipant participant,
				final int beginOffset, final int endOffset,
				final int contentBeginOffset, final int contentEndOffset) {
			super(beginOffset, endOffset, participant.getEmbedDescr());
			this.participant= participant;
			
			this.contentBeginOffset= contentBeginOffset;
			this.contentEndOffset= contentEndOffset;
		}
		
		
		@Override
		public RegexInlineWeaveParticipant getParticipant() {
			return this.participant;
		}
		
		public int getContentBeginOffset() {
			return this.contentBeginOffset;
		}
		
		public int getContentEndOffset() {
			return this.contentEndOffset;
		}
		
		@Override
		public int getOrgLength() {
			return getLength();
		}
		
		@Override
		public int getTextLength() {
			return getLength();
		}
		
	}
	
	private class ExplLocator implements Locator, IWikitextLocator {
		
		
		private int lineNumber;
		
		private int shift;
		
		private int beginOffset;
		
		private int endOffset;
		
		
		
		public void setShift(final int offset) {
			this.shift= offset;
		}
		
		
		private void setOffset(final int offset) {
			if (this.beginOffset != offset) {
				this.beginOffset= offset;
				this.lineNumber= -1;
			}
			this.endOffset= offset;
		}
		
		public void setTo(final int beginOffset) {
			setOffset(this.shift + beginOffset);
		}
		
		public void setTo(final int beginOffset, final int endOffset) {
			setOffset(this.shift + beginOffset);
			if (endOffset > beginOffset) {
				this.endOffset= this.shift + endOffset;
			}
		}
		
		public void reset() {
			this.shift= 0;
			this.beginOffset= 0;
			this.endOffset= 0;
			this.lineNumber= -1;
		}
		
		@Override
		public int getLineNumber() {
			try {
				if (this.lineNumber < 0) {
					final ILineInformation lines= WeaveLanguageProcessor.this.orgContent.getLines();
					this.lineNumber= lines.getLineOfOffset(this.beginOffset);
				}
				return this.lineNumber;
			}
			catch (final BadLocationException e) {
				throw new RuntimeException(e);
			}
		}
		
		private ILineInformation getLines() throws BadLocationException {
			final ILineInformation lines= WeaveLanguageProcessor.this.orgContent.getLines();
			if (this.lineNumber < 0) {
				this.lineNumber= lines.getLineOfOffset(this.beginOffset);
			}
			return lines;
		}
		
		@Override
		public int getLineOffset() {
			try {
				return getLines().getLineOffset(this.lineNumber);
			}
			catch (final BadLocationException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public int getLineDocumentOffset() {
			return getLineOffset();
		}
		
		@Override
		public int getLineLength() {
			try {
				return getLines().getLineLength(this.lineNumber);
			}
			catch (final BadLocationException e) {
				throw new RuntimeException(e);
			}
		}
		
		public int getLineEndOffset() {
			try {
				final ILineInformation lines= getLines();
				return lines.getLineEndOffset(this.lineNumber);
			}
			catch (final BadLocationException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public int getDocumentOffset() {
			return this.beginOffset;
		}
		
		@Override
		public int getBeginOffset() {
			return this.beginOffset;
		}
		
		@Override
		public int getLineCharacterOffset() {
			return this.beginOffset - getLineOffset();
		}
		
		@Override
		public int getEndOffset() {
			return this.endOffset;
		}
		
		@Override
		public int getLineSegmentEndOffset() {
			return this.endOffset - getLineOffset();
		}
		
	}
	
	
	private SourceContent orgContent;
	
	private final List<BlockWeaveParticipant> chunkParticipants= new ArrayList<>();
	private final List<RegexInlineWeaveParticipant> inlineParticipants= new ArrayList<>();
	
	private Matcher inlineMatcher;
	
	private final List<Embedded> embeddedList= new ArrayList<>();
	private int embeddedNextIdx;
	private int embeddedNextOffset;
	
	private int maxOffset;
	private int lastOffset;
	
//	private final Matcher chunkStartMatcher;
//	private final Matcher chunkEndMatcher;
	
	
	private int[] endSpanFix= new int[16];
	private int endSpanFixIdx;
	
	private int ignoreCounter;
	
	private final ExplLocator explLocator= new ExplLocator();
	
	private byte mode;
	private DocumentBuilder builder;
	private boolean finished;
	
	private final StringBuilder sBuilder= new StringBuilder();
	
	
	public WeaveLanguageProcessor() {
	}
	
	
	public void clearConfig() {
		this.chunkParticipants.clear();
		this.inlineParticipants.clear();
		this.inlineMatcher= null;
	}
	
	public void addChunkParticipant(final BlockWeaveParticipant participant) {
		if (participant == null) {
			throw new NullPointerException("participant"); //$NON-NLS-1$
		}
		this.chunkParticipants.add(participant);
	}
	
	public void addInlineParticipants(final RegexInlineWeaveParticipant participant) {
		if (participant == null) {
			throw new NullPointerException("participant"); //$NON-NLS-1$
		}
		this.inlineParticipants.add(participant);
		this.inlineMatcher= null;
	}
	
	private Pattern combineInlineParticipants() {
		final int n= this.inlineParticipants.size();
		if (n == 0) {
			return null;
		}
		else if (n == 1) {
			return this.inlineParticipants.get(0).getPattern();
		}
		else {
			this.sBuilder.setLength(0);
			for (int i= 0; i < n; i++) {
				this.sBuilder.append("|(?:"); //$NON-NLS-1$
				this.sBuilder.append(this.inlineParticipants.get(i).getPattern().pattern());
				this.sBuilder.append(')');
			}
			return Pattern.compile(this.sBuilder.substring(1));
		}
	}
	
	
	public String preprocess(final SourceContent content, final DocumentBuilder builder, final byte mode) {
		this.orgContent= content;
		this.builder= builder;
		this.builder.setLocator(this.explLocator);
		this.mode= mode;
		this.explLocator.reset();
		this.finished= false;
		
		this.embeddedList.clear();
		this.embeddedNextIdx= 0;
		this.embeddedNextOffset= Integer.MAX_VALUE;
		this.lastOffset= 0;
		
		this.endSpanFixIdx= -1;
		
		String checkedContent;
		
		final String source= content.getText();
		
		try {
			for (int iPart= 0; iPart < this.chunkParticipants.size(); iPart++) {
				this.chunkParticipants.get(iPart).reset(content);
			}
			for (int iPart= 0; iPart < this.inlineParticipants.size(); iPart++) {
				this.inlineParticipants.get(iPart).reset(content);
			}
			if (this.inlineMatcher == null) {
				final Pattern pattern= combineInlineParticipants();
				if (pattern != null) {
					this.inlineMatcher= pattern.matcher(source);
					if (DEBUGGING && this.inlineMatcher.groupCount() != this.inlineParticipants.size()) {
						System.out.println(this.inlineMatcher.pattern());
					}
				}
			}
			else {
				this.inlineMatcher.reset(source);
			}
			
			this.sBuilder.setLength(0);
			this.sBuilder.ensureCapacity(source.length() + 40);
			int endOffset= 0;
			final ILineInformation lines= content.getLines();
			int lineEndOffset= lines.getLineOffset(0);
			final int numLines= lines.getNumberOfLines();
			for (int line= 0; line < numLines; line++) {
				int lineOffset= lineEndOffset;
				lineEndOffset= lines.getLineEndOffset(line);
				final int nPart= this.chunkParticipants.size();
				for (int iPart= 0; iPart < nPart; iPart++) {
					final BlockWeaveParticipant part= this.chunkParticipants.get(iPart);
					if (part.checkStartLine(lineOffset, lineEndOffset)) {
						append(source, endOffset, part.getStartOffset());
						final int checkedBeginOffset= this.sBuilder.length();
						
						part.appendReplacement(this.sBuilder, source, part.getStartOffset(), lineEndOffset);
						final int checkedEndOffset= this.sBuilder.length();
						
						endOffset= source.length();
						while (++line < numLines) {
							lineOffset= lineEndOffset;
							lineEndOffset= lines.getLineEndOffset(line);
							if (part.checkEndLine(lineOffset, lineEndOffset)) {
								endOffset= lineEndOffset;
								break;
							}
						}
						
						this.embeddedList.add(new EmbeddedChunk(part,
								checkedBeginOffset, checkedEndOffset,
								part.getStartOffset(), endOffset ));
					}
				}
			}
			
			if (this.sBuilder.length() > 0 || (this.inlineMatcher != null && this.inlineMatcher.find())) {
				append(source, endOffset, source.length());
				
				if (!this.embeddedList.isEmpty()) {
					this.embeddedNextOffset= this.embeddedList.get(0).getBeginOffset();
				}
				
				checkedContent= this.sBuilder.toString();
			}
			else {
				checkedContent= source;
			}
		}
		catch (final BadLocationException e) {
			throw new RuntimeException(e);
		}
		
		this.ignoreCounter= 0;
		
		this.maxOffset= checkedContent.length();
		return checkedContent;
	}
	
	private void append(final String source, final int begin, final int end) {
		if (begin < end) {
			final int shift= begin - this.sBuilder.length();
			int offset= begin;
			
			final Matcher matcher= this.inlineMatcher;
			if (matcher != null) {
				matcher.region(begin, end);
				final int nPart= this.inlineParticipants.size();
				while (matcher.find()) {
					final int matchStart= matcher.start();
					if (offset < matchStart) {
						this.sBuilder.append(source.substring(offset, matchStart));
					}
					offset= matcher.end();
					
					int length= offset - matchStart;
					if (length > 0) {
						this.sBuilder.append(WeaveParticipant.REPLACEMENT_CHAR);
						length--;
						while (length > 0) {
							final int l= Math.min(length, 40);
							this.sBuilder.append(WeaveParticipant.REPLACEMENT_STRING, 0, l);
		//					sb.append("Xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", 0, l);
							length-= l;
						}
					}
					
					for (int iPart= 0; iPart < nPart; iPart++) {
						final int groupNum= iPart + 1;
						if (matcher.start(groupNum) >= 0) {
							this.embeddedList.add(new EmbeddedInline(this.inlineParticipants.get(iPart),
									matchStart - shift, offset - shift,
									matcher.start(groupNum), matcher.end(groupNum) ));
							break;
						}
					}
				}
			}
			
			this.sBuilder.append(source.substring(offset, end));
		}
	}
	
	public void clear() {
		this.orgContent= null;
		for (int iPart= 0; iPart < this.chunkParticipants.size(); iPart++) {
			this.chunkParticipants.get(iPart).reset(null);
		}
		for (int iPart= 0; iPart < this.inlineParticipants.size(); iPart++) {
			this.inlineParticipants.get(iPart).reset(null);
		}
		this.builder= null;
	}
	
	
	private void processEmbedded(final int toOffset) {
		if (toOffset < this.lastOffset) {
			return;
		}
		while (toOffset > this.embeddedNextOffset) {
			final Embedded embedded= this.embeddedList.get(this.embeddedNextIdx++);
			if (embedded instanceof EmbeddedChunk) {
				sendChunk((EmbeddedChunk) embedded);
			}
			else {
				sendInline((EmbeddedInline) embedded);
			}
			this.lastOffset= embedded.getEndOffset();
			
			this.embeddedNextOffset= (this.embeddedNextIdx < this.embeddedList.size()) ?
					this.embeddedList.get(this.embeddedNextIdx).getBeginOffset() : Integer.MAX_VALUE;
		}
	}
	
	private void sendChunk(final EmbeddedChunk chunk) {
		final WeaveParticipant part= chunk.getParticipant();
		this.explLocator.setTo(chunk.getBeginOffset());
		this.builder.beginBlock(BlockType.CODE, new EmbeddingAttributes(
				part.getForeignTypeId(), chunk.getEmbedDescr(),
				chunk.getOrgBeginOffset(), chunk.getOrgEndOffset() ));
		this.explLocator.setShift(chunk.getShift());
		this.explLocator.setTo(chunk.getEndOffset());
		this.builder.endBlock();
	}
	
	private void sendInline(final EmbeddedInline inline) {
		if (this.mode >= MODE_BLOCKS_AND_INLINE) {
			final WeaveParticipant part= inline.getParticipant();
			this.explLocator.setTo(inline.getBeginOffset(), inline.getEndOffset());
			this.builder.beginSpan(SpanType.CODE, new EmbeddingAttributes(
					part.getForeignTypeId(), inline.getEmbedDescr(),
					inline.getContentBeginOffset(), inline.getContentEndOffset() ));
			this.explLocator.setTo(inline.getEndOffset());
			this.builder.endSpan();
		}
	}
	
	
	private int getSegmentBeginOffset() {
		return Math.min(
				this.locator.getLineDocumentOffset() + this.locator.getLineCharacterOffset(),
				this.maxOffset );
	}
	
	private int getSegmentEndOffset() {
		return Math.min(
				this.locator.getLineDocumentOffset() + this.locator.getLineSegmentEndOffset(),
				this.maxOffset );
	}
	
	@Override
	public void beginDocument() {
		this.explLocator.setTo(0);
		this.builder.beginDocument();
	}
	
	@Override
	public void endDocument() {
		this.finished= true;
		processEmbedded(Integer.MAX_VALUE);
		final int endOffset= this.locator.getDocumentOffset();
		this.lastOffset= endOffset;
		this.explLocator.setTo(endOffset);
		this.builder.endDocument();
	}
	
	public void finish() {
		if (!this.finished) {
			this.finished= true;
			processEmbedded(Integer.MAX_VALUE);
		}
	}
	
	@Override
	public void beginBlock(final BlockType type, final Attributes attributes) {
		final int beginOffset= this.locator.getDocumentOffset();
		processEmbedded(beginOffset);
		if (beginOffset < this.lastOffset) {
			this.ignoreCounter++;
			return;
		}
		this.lastOffset= beginOffset;
		this.explLocator.setTo(beginOffset);
		this.builder.beginBlock(type, attributes);
	}
	
	@Override
	public void endBlock() {
		if (this.ignoreCounter > 0) {
			this.ignoreCounter--;
			return;
		}
		final int endOffset= this.locator.getLineDocumentOffset();
		
		if (DEBUGGING) {
			if (endOffset > this.maxOffset) {
				System.out.println("endOffset > source.length: " + endOffset);
			}
			if (endOffset < this.lastOffset) {
				System.out.println("endOffset < lastOffset: " + endOffset);
			}
		}
		
		processEmbedded(endOffset);
		if (endOffset > this.lastOffset) {
			this.lastOffset= endOffset;
		}
		this.explLocator.setTo(this.lastOffset);
		this.builder.endBlock();
	}
	
	@Override
	public void beginSpan(final SpanType type, final Attributes attributes) {
		if (this.mode <= MODE_BLOCKS_AND_INLINE) {
			return;
		}
		final int beginOffset= getSegmentBeginOffset();
		final int endOffset= getSegmentEndOffset();
		processEmbedded(beginOffset);
		if (beginOffset < this.lastOffset) {
			this.ignoreCounter++;
			return;
		}
		if (++this.endSpanFixIdx >= this.endSpanFix.length) {
			this.endSpanFix= Arrays.copyOf(this.endSpanFix, this.endSpanFix.length + 16);
		}
		this.endSpanFix[this.endSpanFixIdx]= endOffset;
		this.lastOffset= beginOffset;
		this.explLocator.setTo(beginOffset, endOffset);
		this.builder.beginSpan(type, attributes);
	}
	
	@Override
	public void endSpan() {
		if (this.mode <= MODE_BLOCKS_AND_INLINE) {
			return;
		}
		if (this.ignoreCounter > 0) {
			this.ignoreCounter--;
			return;
		}
		final int endOffset= Math.max(
				getSegmentEndOffset(),
				this.endSpanFix[this.endSpanFixIdx--] );
		
		if (DEBUGGING) {
			if (endOffset > this.maxOffset) {
				System.out.println("endOffset > source.length: " + endOffset);
			}
			if (endOffset < this.lastOffset) {
				System.out.println("endOffset < lastOffset: " + endOffset);
			}
		}
		
		processEmbedded(endOffset);
		if (endOffset > this.lastOffset) {
			this.lastOffset= endOffset;
		}
		this.explLocator.setTo(this.lastOffset);
		this.builder.endSpan();
	}
	
	@Override
	public void beginHeading(final int level, final Attributes attributes) {
		final int beginOffset= this.locator.getDocumentOffset();
		processEmbedded(beginOffset);
		if (beginOffset < this.lastOffset) {
			this.ignoreCounter++;
			return;
		}
		this.lastOffset= beginOffset;
		this.explLocator.setTo(beginOffset);
		this.builder.beginHeading(level, attributes);
	}
	
	@Override
	public void endHeading() {
		if (this.ignoreCounter > 0) {
			this.ignoreCounter--;
			return;
		}
		// Workaround if language (Markdown) doesn't set end correctly
		this.explLocator.setTo(getSegmentEndOffset());
		final int endOffset= this.explLocator.getLineEndOffset() - this.explLocator.shift;
		
		if (DEBUGGING) {
			if (endOffset > this.maxOffset) {
				System.out.println("endOffset > source.length: " + endOffset);
			}
			if (endOffset < this.lastOffset) {
				System.out.println("endOffset < lastOffset: " + endOffset);
			}
		}
		
		processEmbedded(endOffset);
		if (endOffset > this.lastOffset) {
			this.lastOffset= endOffset;
		}
		this.explLocator.setTo(this.lastOffset);
		this.builder.endHeading();
	}
	
	@Override
	public void characters(final String text) {
		if (this.mode <= MODE_BLOCKS_AND_INLINE) {
			return;
		}
		int offset= this.locator.getDocumentOffset();
		int endOffset= getSegmentEndOffset();
		int lastIdx= 0;
		while (endOffset > this.embeddedNextOffset) {
			final Embedded embedded= this.embeddedList.get(this.embeddedNextIdx++);
			final int textIdx= (lastIdx < text.length()) ? text.indexOf(WeaveParticipant.REPLACEMENT_CHAR, lastIdx) : -1;
			if (textIdx < 0 || embedded.getTextLength() <= 0) {
				this.embeddedNextIdx--;
				endOffset= this.embeddedNextOffset;
				lastIdx= text.length();
				break;
			}
			
			this.explLocator.setTo(offset, embedded.getBeginOffset());
			this.builder.characters(text.substring(lastIdx, textIdx));
			lastIdx= textIdx + embedded.getTextLength();
			
			if (embedded instanceof EmbeddedChunk) {
				sendChunk((EmbeddedChunk) embedded);
			}
			else {
				sendInline((EmbeddedInline) embedded);
			}
			
			this.lastOffset= offset= embedded.getEndOffset();
			
			this.embeddedNextOffset= (this.embeddedNextIdx < this.embeddedList.size()) ?
					this.embeddedList.get(this.embeddedNextIdx).getBeginOffset() : Integer.MAX_VALUE;
		}
		
		if (offset < endOffset && lastIdx < text.length()) {
			this.lastOffset= endOffset;
			this.explLocator.setTo(offset, endOffset);
			this.builder.characters(text.substring(lastIdx, text.length()));
		}
	}
	
	@Override
	public void entityReference(final String entity) {
		if (this.mode <= MODE_BLOCKS_AND_INLINE) {
			return;
		}
		final int beginOffset= getSegmentBeginOffset();
		final int endOffset= getSegmentEndOffset();
		if (beginOffset >= endOffset) {
			return;
		}
		processEmbedded(endOffset);
		if (beginOffset > this.lastOffset) {
			return;
		}
		this.explLocator.setTo(beginOffset, endOffset);
		this.builder.entityReference(entity);
	}
	
	@Override
	public void image(final Attributes attributes, final String url) {
		if (this.mode <= MODE_BLOCKS_AND_INLINE) {
			return;
		}
		final int beginOffset= getSegmentBeginOffset();
		final int endOffset= getSegmentEndOffset();
		if (beginOffset >= endOffset) {
			return;
		}
		processEmbedded(endOffset);
		if (beginOffset > this.lastOffset) {
			return;
		}
		this.explLocator.setTo(beginOffset, endOffset);
		this.builder.image(attributes, url);
	}
	
	@Override
	public void link(final Attributes attributes, final String hrefOrHashName, final String text) {
		if (this.mode <= MODE_BLOCKS_AND_INLINE) {
			return;
		}
		final int beginOffset= getSegmentBeginOffset();
		final int endOffset= getSegmentEndOffset();
		if (beginOffset >= endOffset) {
			return;
		}
		processEmbedded(endOffset);
		if (beginOffset < this.lastOffset) {
			return;
//			if (text.indexOf(REPLACEMENT_CHAR) >= 0) {
//				text= replaceText(text, locator.getLineDocumentOffset() + locator.getLineCharacterOffset());
//			}
		}
		this.lastOffset= endOffset;
		this.explLocator.setTo(beginOffset, endOffset);
		this.builder.link(attributes, hrefOrHashName, text);
	}
	
	@Override
	public void imageLink(final Attributes linkAttributes, final Attributes imageAttributes,
			final String href, final String imageUrl) {
		if (this.mode <= MODE_BLOCKS_AND_INLINE) {
			return;
		}
		final int beginOffset= getSegmentBeginOffset();
		final int endOffset= getSegmentEndOffset();
		if (beginOffset >= endOffset) {
			return;
		}
		processEmbedded(endOffset);
		if (beginOffset < this.lastOffset) {
			return;
		}
		this.lastOffset= endOffset;
		this.explLocator.setTo(beginOffset, endOffset);
		this.builder.imageLink(linkAttributes, imageAttributes, href, imageUrl);
	}
	
	@Override
	public void acronym(final String text, final String definition) {
		if (this.mode <= MODE_BLOCKS_AND_INLINE) {
			return;
		}
		final int beginOffset= getSegmentBeginOffset();
		final int endOffset= getSegmentEndOffset();
		if (beginOffset >= endOffset) {
			return;
		}
		processEmbedded(endOffset);
		if (beginOffset < this.lastOffset) {
			return;
		}
		this.lastOffset= endOffset;
		this.explLocator.setTo(beginOffset, endOffset);
		this.builder.acronym(text, definition);
	}
	
	@Override
	public void lineBreak() {
		if (this.mode <= MODE_BLOCKS_AND_INLINE) {
			return;
		}
		final int beginOffset= this.locator.getDocumentOffset();
		this.explLocator.setTo(beginOffset);
		this.builder.lineBreak();
	}
	
	@Override
	public void charactersUnescaped(final String literal) {
		if (this.mode <= MODE_BLOCKS_AND_INLINE) {
			return;
		}
		final int beginOffset= getSegmentBeginOffset();
		final int endOffset= getSegmentEndOffset();
		if (beginOffset >= endOffset) {
			return;
		}
		processEmbedded(endOffset);
		if (beginOffset < this.lastOffset) {
			return;
		}
		this.lastOffset= endOffset;
		this.explLocator.setTo(beginOffset, endOffset);
		this.builder.charactersUnescaped(literal);
	}
	
}
