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

package de.walware.docmlet.wikitext.internal.commonmark.core.inlines;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;

import de.walware.docmlet.wikitext.core.source.LabelInfo;
import de.walware.docmlet.wikitext.core.source.LinkByRefAttributes;
import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;
import de.walware.docmlet.wikitext.internal.commonmark.core.ToStringHelper;


public class Link extends InlineWithNestedContents {
	
	
	private final String href;
	private final LabelInfo labelReference;
	
	private final String title;
	
	
	public Link(final Line line, final int offset, final int length, final int cursorLength,
			final String href, final String title, final List<? extends Inline> contents) {
		super(line, offset, length, cursorLength, contents);
		this.href= checkNotNull(href);
		this.labelReference= null;
		this.title= title;
	}
	
	public Link(final Line line, final int offset, final int length,
			final String href, final String title, final List<Inline> contents) {
		this(line, offset, length, -1, href, title, contents);
	}
	
	public Link(final Line line, final int offset, final int length,
			final LabelInfo labelReference, final List<Inline> contents) {
		super(line, offset, length, -1, contents);
		this.href= LinkByRefAttributes.REF_SCHEME + ':' + labelReference.getLabel();
		this.labelReference= labelReference;
		this.title= null;
	}
	
	
	public String getHref() {
		return this.href;
	}
	
	@Override
	public void emit(final ProcessingContext context,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		final LinkAttributes attributes= (this.labelReference != null) ?
				new LinkByRefAttributes(this.labelReference) : new LinkAttributes();
		attributes.setTitle(this.title);
		attributes.setHref(this.href);
		builder.beginSpan(SpanType.LINK, attributes);
		
		InlineParser.emit(context, getContents(), locator, builder);
		
		builder.endSpan();
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(getOffset(), getLength(), getContents(), this.href, this.title);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		final Link other= (Link) obj;
		return this.href.equals(other.href) && getContents().equals(other.getContents())
				&& Objects.equals(this.title, other.title);
	}
	
	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(Link.class)
				.add("offset", getOffset())
				.add("length", getLength())
				.add("href", ToStringHelper.toStringValue(this.href))
				.add("title", this.title)
				.add("contents", getContents())
				.toString();
	}
	
}
