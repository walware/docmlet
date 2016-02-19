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

package de.walware.docmlet.wikitext.internal.commonmark.core.inlines;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;

import de.walware.jcommons.collections.ImCollections;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

import de.walware.docmlet.wikitext.core.source.LabelInfo;
import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Cursor;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext.UriWithTitle;


public class PotentialBracketCloseDelimiter extends InlineWithText {
	
	
	private final PotentialBracketRegex shared;
	
	
	public PotentialBracketCloseDelimiter(final Line line, final int offset,
			final PotentialBracketRegex shared) {
		super(line, offset, 1, 1, "]");
		
		this.shared= shared;
	}
	
	
	@Override
	public void apply(final ProcessingContext context, final List<Inline> inlines,
			final Cursor cursor, final boolean inBlock) {
		final int openingDelimiterIndex= findLastPotentialBracketDelimiter(inlines);
		if (openingDelimiterIndex >= 0) {
			final PotentialBracketOpenDelimiter openingDelimiter= (PotentialBracketOpenDelimiter) inlines.get(openingDelimiterIndex);
			
			if (openingDelimiter.isActive()) {
				final boolean referenceDefinition= (inBlock
						&& cursor.hasNext() && cursor.getNext() == ':'
						&& isEligibleForReferenceDefinition(inlines, openingDelimiter, openingDelimiterIndex) );
				final Matcher matcher= (cursor.hasNext()) ?
						cursor.setup((referenceDefinition) ?
										this.shared.getReferenceDefinitionEndMatcher() :
										this.shared.getEndMatcher(),
								1) :
						null;
				
				final List<Inline> contents= InlineParser.secondPass(
						inlines.subList(openingDelimiterIndex + 1, inlines.size() ));
				
				if (!openingDelimiter.isLinkDelimiter() || !containsLink(contents)) {
					
					if (matcher != null /*== cursor.hasNext()*/ && matcher.matches()) {
						final LabelInfo referenceLabel= (referenceDefinition) ?
								referenceLabel(context, cursor, contents) :
								null;
						final String uri= linkUri(matcher, context, cursor);
						
						if (!referenceDefinition
								|| (referenceLabel != null && uri != null && !uri.isEmpty()) ) {
							final String title= linkTitle(matcher, context, cursor);
							
							final int closingLength= 1 + matcher.end(4) - matcher.regionStart();
							cursor.advance(closingLength);
							
							final int startOffset= openingDelimiter.getOffset();
							final int endOffset= cursor.getOffset();
							
							truncate(inlines, openingDelimiter, openingDelimiterIndex,
									referenceDefinition );
							
							if (openingDelimiter.isLinkDelimiter()) {
								inactivatePreceding(inlines);
							}
							
							if (referenceDefinition) {
								inlines.add(new ReferenceDefinition(openingDelimiter.getLine(),
										startOffset, endOffset - startOffset,
										uri, title, referenceLabel ));
							}
							else if (openingDelimiter.isImageDelimiter()) {
								inlines.add(new Image(openingDelimiter.getLine(),
										startOffset, endOffset - startOffset, uri,
										title, contents ));
							}
							else {
								inlines.add(new Link(openingDelimiter.getLine(),
										startOffset, endOffset - startOffset,
										uri, title, contents ));
							}
							
							return;
						}
					}
					else {
						int closingLength= 1;
						LabelInfo referenceLabel= referenceLabel(context, cursor, contents);
						if (cursor.hasNext()) {
							final Matcher referenceLabelMatcher= cursor.setup(
									this.shared.getReferenceLabelMatcher(),
									1 );
							if (referenceLabelMatcher.matches()) {
								final String label= context.normalizeLabel(referenceLabelMatcher.group(2));
								if (label != null) {
									final int start= cursor.getMatcherOffset(referenceLabelMatcher.start(2));
									final int end= cursor.getMatcherOffset(referenceLabelMatcher.end(2));
									referenceLabel= new LabelInfo(label, start, end);
								}
								closingLength+= referenceLabelMatcher.end(1) - referenceLabelMatcher.regionStart();
							}
						}
						
						if (referenceLabel != null) {
							if (context.getMode() == ProcessingContext.PARSE_SOURCE_STRUCT) {
								cursor.advance(closingLength);
								
								final int startOffset= openingDelimiter.getOffset();
								final int endOffset= cursor.getOffset();
								
								truncate(inlines, openingDelimiter, openingDelimiterIndex, false);
								
								if (openingDelimiter.isLinkDelimiter()) {
									inactivatePreceding(inlines);
								}
								
								if (openingDelimiter.isLinkDelimiter()) {
									inlines.add(new Link(openingDelimiter.getLine(),
											startOffset, endOffset - startOffset,
											referenceLabel, ImCollections.<Inline>emptyList() ));
								}
								else {
									inlines.add(new Image(openingDelimiter.getLine(),
											startOffset, endOffset - startOffset,
											referenceLabel, contents ));
								}
								
								return;
							}
							
							final UriWithTitle uriWithTitle= context.getNamedUri(referenceLabel.getLabel());
							if (uriWithTitle != null) {
								cursor.advance(closingLength);
								
								final int startOffset= openingDelimiter.getOffset();
								final int endOffset= cursor.getOffset();
								
								truncate(inlines, openingDelimiter, openingDelimiterIndex, false);
								
								if (openingDelimiter.isLinkDelimiter()) {
									inactivatePreceding(inlines);
								}
								
								if (openingDelimiter.isLinkDelimiter()) {
									inlines.add(new Link(openingDelimiter.getLine(),
											startOffset, endOffset - startOffset,
											uriWithTitle.getUri(), uriWithTitle.getTitle(), contents ));
								}
								else {
									inlines.add(new Image(openingDelimiter.getLine(),
											startOffset, endOffset - startOffset,
											uriWithTitle.getUri(), uriWithTitle.getTitle(), contents ));
								}
								
								return;
							}
						}
					}
				}
			}
			
			replaceDelimiter(inlines, openingDelimiterIndex, openingDelimiter);
		}
		
		if (Characters.append(inlines, inlines.size(), this)) {
			cursor.advance(getLength());
			return;
		}
		super.apply(context, inlines, cursor, inBlock);
	}
	
	@Override
	public void emit(final ProcessingContext context,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		builder.characters(this.text);
	}
	
	
	private LabelInfo referenceLabel(final ProcessingContext context, final Cursor cursor,
			final List<Inline> contents) {
		if (contents.isEmpty()) {
			return null;
		}
		final int start= contents.get(0).getOffset();
		final int end= getOffset();
		String name= cursor.getText(
				cursor.toCursorOffset(start),
				cursor.toCursorOffset(end) );
		if (this.shared.getReferenceNameMatcher().reset(name).matches()) {
			name= context.normalizeLabel(name);
			if (name != null) {
				return new LabelInfo(name, start, end);
			}
		}
		return null;
	}
	
	private boolean containsLink(final List<Inline> contents) {
		for (final Inline inline : contents) {
			if (inline instanceof Link) {
				return true;
			} else if (inline instanceof InlineWithNestedContents
					&& containsLink(((InlineWithNestedContents) inline).getContents())) {
				return true;
			}
		}
		return false;
	}
	
	private int findLastPotentialBracketDelimiter(final List<Inline> inlines) {
		for (int idx= inlines.size() - 1; idx >= 0; --idx) {
			final Inline inline= inlines.get(idx);
			if (inline instanceof PotentialBracketOpenDelimiter) {
				return idx;
			}
		}
		return -1;
	}
	
	private boolean isIndentInline(final Inline inline, final Line line) {
		return (inline instanceof Characters
				&& inline.getOffset() == line.getOffset()
				&& inline.getLength() == line.getIndentLength() );
	}
	
	boolean isEligibleForReferenceDefinition(final List<Inline> inlines,
			final PotentialBracketOpenDelimiter openingDelimiter, final int openingDelimiterIndex) {
		final Line openingLine= openingDelimiter.getLine();
		if (openingDelimiter.isLinkDelimiter()
				&& openingLine.getIndent() < 4
				&& openingDelimiter.getOffset() == openingLine.getOffset() + openingLine.getIndentLength() ) {
			if (openingDelimiterIndex > 0) {
				int idx= openingDelimiterIndex - 1;
				{	final Inline inline= inlines.get(idx--);
					if (!(inline instanceof ReferenceDefinition
							|| isIndentInline(inline, openingLine) )) {
						return false;
					}
				}
				while (idx >= 0) {
					final Inline inline= inlines.get(idx--);
					if (!(inline instanceof ReferenceDefinition)) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}
	
	private String linkTitle(final Matcher matcher, final ProcessingContext context, final Cursor cursor) {
		final int start= matcher.start(3);
		if (start != -1) {
			final int end= matcher.end(3);
			final String title= cursor.getText(start + 1, end - 1);
			return context.getHelper().replaceEscaping(title);
		}
		return "";
	}
	
	private String linkUri(final Matcher matcher, final ProcessingContext context, final Cursor cursor) {
		String uriWithEscapes= matcher.group(1);
		if (uriWithEscapes == null) {
			uriWithEscapes= matcher.group(2);
			if (uriWithEscapes == null) {
				uriWithEscapes= "";
			}
		}
		final String uri= context.getHelper().replaceEscaping(uriWithEscapes);
		return normalizeUri(uri);
	}
	
	private String normalizeUri(final String uri) {
		try {
			final String decoded= URLDecoder.decode(uri, StandardCharsets.UTF_8.name());
			final Escaper escaper= UrlEscapers.urlFragmentEscaper();
			final int indexOfHash= decoded.indexOf('#');
			if (indexOfHash != -1) {
				String uriWithHash= escaper.escape(decoded.substring(0, indexOfHash)) + '#';
				if ((indexOfHash + 1) < decoded.length()) {
					uriWithHash += escaper.escape(decoded.substring(indexOfHash + 1));
				}
				return uriWithHash;
			}
			return escaper.escape(decoded);
		} catch (final Exception e) {
			return uri;
		}
	}
	
	public void truncate(final List<Inline> inlines, 
			final PotentialBracketOpenDelimiter openingDelimiter, final int indexOfOpeningDelimiter,
			final boolean removeIndent) {
		while (inlines.size() > indexOfOpeningDelimiter) {
			inlines.remove(indexOfOpeningDelimiter);
		}
		if (removeIndent && indexOfOpeningDelimiter > 0
				&& isIndentInline(inlines.get(indexOfOpeningDelimiter - 1), openingDelimiter.getLine()) ) {
			inlines.remove(indexOfOpeningDelimiter - 1);
		}
	}
	
	private void inactivatePreceding(final List<Inline> inlines) {
		for (int idx= inlines.size() - 1; idx >= 0; idx--) {
			final Inline inline= inlines.get(idx);
			if (inline instanceof PotentialBracketOpenDelimiter) {
				final PotentialBracketOpenDelimiter openDelimiter= (PotentialBracketOpenDelimiter) inline;
				if (openDelimiter.isLinkDelimiter()) {
					if (openDelimiter.isActive()) {
						openDelimiter.setInactive();
					}
					else {
						return;
					}
				}
			}
		}
	}
	
	private void replaceDelimiter(final List<Inline> inlines,
			final int index, final PotentialBracketOpenDelimiter delimiter) {
		if (Characters.append(inlines, index, delimiter)) {
			inlines.remove(index);
			return;
		}
		inlines.set(index,
				new Characters(delimiter.getLine(),
						delimiter.getOffset(), delimiter.getLength(), delimiter.getCursorLength(),
						delimiter.getText() ));
	}
	
}
