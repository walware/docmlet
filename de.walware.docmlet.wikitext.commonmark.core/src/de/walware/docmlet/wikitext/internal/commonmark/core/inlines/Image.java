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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.ImageAttributes;

import de.walware.docmlet.wikitext.core.source.ImageByRefAttributes;
import de.walware.docmlet.wikitext.core.source.LabelInfo;
import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;
import de.walware.docmlet.wikitext.internal.commonmark.core.ToStringHelper;


public class Image extends InlineWithNestedContents {
	
	
	private final String src;
	private final LabelInfo labelReference;
	
	private final String title;
	
	
	public Image(final Line line, final int offset, final int length,
			final String src, final String title, final List<Inline> contents) {
		super(line, offset, length, -1, contents);
		this.src= checkNotNull(src);
		this.labelReference= null;
		this.title= title;
	}
	
	public Image(final Line line, final int offset, final int length,
			final LabelInfo labelReference, final List<Inline> contents) {
		super(line, offset, length, -1, contents);
		this.src= ImageByRefAttributes.REF_SCHEME + ':' + labelReference.getLabel();
		this.labelReference= labelReference;
		this.title= null;
	}
	
	
	public String getHref() {
		return this.src;
	}
	
	@Override
	public void emit(final ProcessingContext context,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		final ImageAttributes attributes= (this.labelReference != null) ?
				new ImageByRefAttributes(this.labelReference) : new ImageAttributes();
		attributes.setTitle(this.title);
		
		final List<Inline> contents= getContents();
		if (!contents.isEmpty()) {
			attributes.setAlt(InlineParser.toStringContent(context, contents));
		}
		
		builder.image(attributes, this.src);
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(getOffset(), getLength(), getContents(), this.src, this.title);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		final Image other= (Image) obj;
		return Objects.equals(this.src, other.src)
				&& Objects.equals(this.labelReference, other.labelReference)
				&& Objects.equals(this.title, other.title);
	}
	
	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(Image.class)
				.add("offset", getOffset())
				.add("length", getLength())
				.add("src", ToStringHelper.toStringValue(this.src))
				.add("title", this.title)
				.add("contents", getContents())
				.toString();
	}
	
}
