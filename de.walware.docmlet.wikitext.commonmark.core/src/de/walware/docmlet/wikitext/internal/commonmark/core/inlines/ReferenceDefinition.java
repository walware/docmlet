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

import java.util.Objects;

import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.LinkAttributes;

import de.walware.docmlet.wikitext.core.source.LabelInfo;
import de.walware.docmlet.wikitext.core.source.LinkRefDefinitionAttributes;
import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkLocator;
import de.walware.docmlet.wikitext.internal.commonmark.core.Line;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;
import de.walware.docmlet.wikitext.internal.commonmark.core.ToStringHelper;


public class ReferenceDefinition extends Inline {
	
	
	private final String href;
	
	private final String title;
	
	private final LabelInfo referenceLabel;
	
	
	public ReferenceDefinition(final Line line, final int offset, final int length,
			final String href, final String title, final LabelInfo referenceLabel) {
		super(line, offset, length, -1);
		this.href= checkNotNull(href);
		this.title= title;
		this.referenceLabel= checkNotNull(referenceLabel);
	}
	
	
	public String getHref() {
		return this.href;
	}
	
	@Override
	public void createContext(final ProcessingContext context) {
		context.addUriDefinition(this.referenceLabel.getLabel(), this.href, this.title);
	}
	
	@Override
	public void emit(final ProcessingContext context,
			final CommonmarkLocator locator, final DocumentBuilder builder) {
		if (context.getMode() == ProcessingContext.PARSE_SOURCE_STRUCT) {
			final LinkAttributes attributes= new LinkRefDefinitionAttributes(this.referenceLabel);
			attributes.setTitle(this.title);
			builder.link(attributes, this.href, null);
		}
		// nothing to do
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getOffset(), getLength(), this.referenceLabel, this.href, this.title);
	}
	
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		final ReferenceDefinition other= (ReferenceDefinition) obj;
		return this.href.equals(other.href) && this.referenceLabel.equals(other.referenceLabel) && Objects.equals(this.title, other.title);
	}
	
	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(ReferenceDefinition.class)
				.add("offset", getOffset())
				.add("length", getLength())
				.add("name", this.referenceLabel)
				.add("href", ToStringHelper.toStringValue(this.href))
				.add("title", this.title)
				.toString();
	}
	
}
