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

package de.walware.docmlet.wikitext.commonmark.core;

import java.io.StringWriter;
import java.util.List;
import java.util.Objects;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.IdGenerator;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.MultiplexingDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.IdGenerationStrategy;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;

import de.walware.ecommons.ltk.core.SourceContent;

import de.walware.docmlet.wikitext.core.WikitextProblemReporter;
import de.walware.docmlet.wikitext.core.markup.IMarkupConfig;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguageExtension2;
import de.walware.docmlet.wikitext.core.markup.MarkupParser2;
import de.walware.docmlet.wikitext.core.source.MarkupEventPrinter;
import de.walware.docmlet.wikitext.internal.commonmark.core.Commonmark;
import de.walware.docmlet.wikitext.internal.commonmark.core.CommonmarkIdGenerationStrategy;
import de.walware.docmlet.wikitext.internal.commonmark.core.ContentLineSequence;
import de.walware.docmlet.wikitext.internal.commonmark.core.LineSequence;
import de.walware.docmlet.wikitext.internal.commonmark.core.NullIdGenerator;
import de.walware.docmlet.wikitext.internal.commonmark.core.ProcessingContext;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlockItem;
import de.walware.docmlet.wikitext.internal.commonmark.core.SourceBlocks;
import de.walware.docmlet.wikitext.internal.commonmark.core.inlines.InlineParser;


public class CommonmarkLanguage extends MarkupLanguage implements IMarkupLanguage,
		IMarkupLanguageExtension2 {
	
	
	protected static final String COMMONMARK_LANGUAGE_NAME= "CommonMark\u2002[StatET]"; //$NON-NLS-1$
	
	
	public static final int MARKDOWN_COMPAT_MODE=            1 << 16;
	
	
	private static final boolean DEBUG_LOG_EVENTS= "true".equalsIgnoreCase( //$NON-NLS-1$
			Platform.getDebugOption("de.walware.docmlet.wikitext.commonmark/debug/Parser/logEvents")); //$NON-NLS-1$
	
	
	private /*final*/ String scope;
	
	private /*final*/ int mode;
	
	private ICommonmarkConfig config;
	
	private SourceBlocks sourceBlocks;
	private InlineParser inlineParser;
	
	
	public CommonmarkLanguage() {
		this(null, 0, null);
	}
	
	public CommonmarkLanguage(final String scope, final int mode, final IMarkupConfig config) {
		this.scope= scope;
		this.mode= mode;
		setName(COMMONMARK_LANGUAGE_NAME);
		
		setMarkupConfig(config);
	}
	
	
	@Override
	public CommonmarkLanguage clone() {
		final CommonmarkLanguage clone= (CommonmarkLanguage) super.clone();
		clone.mode = this.mode;
		clone.config= this.config;
		return clone;
	}
	
	@Override
	public CommonmarkLanguage clone(final String scope, final int mode) {
		final CommonmarkLanguage clone= (CommonmarkLanguage) super.clone();
		clone.scope= scope;
		clone.mode= mode;
		clone.config= this.config;
		return clone;
	}
	
	
	@Override
	public String getScope() {
		return this.scope;
	}
	
	
	@Override
	public int getMode() {
		return this.mode;
	}
	
	@Override
	public boolean isModeEnabled(final int modeMask) {
		return ((this.mode & modeMask) != 0);
	}
	
	
	@Override
	public void setMarkupConfig(final IMarkupConfig config) {
		if (config != null) {
			config.seal();
		}
		if (this.config != config) {
			this.config= (ICommonmarkConfig) config;
			
			this.sourceBlocks= null;
			this.inlineParser= null;
		}
	}
	
	@Override
	public ICommonmarkConfig getMarkupConfig() {
		return this.config;
	}
	
	
	@Override
	public void processContent(final MarkupParser2 parser, final SourceContent content, final boolean asDocument) {
		if (parser == null) {
			throw new NullPointerException("parser"); //$NON-NLS-1$
		}
		if (content == null) {
			throw new NullPointerException("content"); //$NON-NLS-1$
		}
		if (parser.getBuilder() == null) {
			throw new NullPointerException("parser.builder"); //$NON-NLS-1$
		}
		
		if (DEBUG_LOG_EVENTS) {
			final StringWriter out= new StringWriter();
			try {
				final MarkupEventPrinter printer= new MarkupEventPrinter(content.getText(), this, out);
				final MarkupParser2 debugParser= new MarkupParser2(this,
						new MultiplexingDocumentBuilder(printer, parser.getBuilder()),
						parser.getFlags() );
				doProcessContent(debugParser, content, asDocument);
				System.out.println(out.toString());
			}
			catch (final Exception e) {
				System.out.println(out.toString());
				e.printStackTrace();
			}
		}
		else {
			doProcessContent(parser, content, asDocument);
		}
	}
	
	protected void doProcessContent(final MarkupParser2 parser, final SourceContent content,
			final boolean asDocument) {
		final DocumentBuilder builder= parser.getBuilder();
		final LineSequence lineSequence= new ContentLineSequence(content.getText(), content.getLines());
		
		final ProcessingContext context= createContext();
		
		if (asDocument) {
			builder.beginDocument();
		}
		
		if (parser.isEnabled(MarkupParser2.SOURCE_STRUCT)) {
			context.getSourceBlocks().parseSourceStruct(context, lineSequence, builder);
		}
		else {
			final List<SourceBlockItem<?>> items= context.getSourceBlocks().createItems(lineSequence);
			context.getSourceBlocks().initializeContext(context, items);
			
			context.getSourceBlocks().emit(context, items, builder);
		}
		
		if (asDocument) {
			builder.endDocument();
		}
	}
	
	@Override
	public void processContent(final MarkupParser parser, final String markupContent, final boolean asDocument) {
		processContent(new MarkupParser2(parser), new SourceContent(0, markupContent), asDocument);
	}
	
	
	private WikitextProblemReporter validator;
	
	@Override
	public WikitextProblemReporter getProblemReporter() {
		if (this.validator == null) {
			this.validator= new WikitextProblemReporter();
		}
		return this.validator;
	}
	
	
	@Override
	public IdGenerationStrategy getIdGenerationStrategy() {
		return ((this.mode & MYLYN_COMPAT_MODE) != 0) ? new CommonmarkIdGenerationStrategy() : null;
	}
	
	private IdGenerator createIdGenerator() {
		final IdGenerationStrategy idGenerationStrategy= getIdGenerationStrategy();
		if (idGenerationStrategy != null) {
			final IdGenerator generator= new IdGenerator();
			generator.setGenerationStrategy(idGenerationStrategy);
			return generator;
		}
		return new NullIdGenerator();
	}
	
	private ProcessingContext createContext() {
		SourceBlocks sourceBlocks= this.sourceBlocks;
		if (sourceBlocks == null) {
			sourceBlocks= Commonmark.newSourceBlocks(this.config);
			this.sourceBlocks= sourceBlocks;
		}
		InlineParser inlineParser= this.inlineParser;
		if (inlineParser == null) {
			inlineParser= ((this.mode & MARKDOWN_COMPAT_MODE) != 0) ?
						Commonmark.newInlineParserMarkdown() :
						Commonmark.newInlineParserCommonMark(this.config);
			this.inlineParser= inlineParser;
		}
		
		final ProcessingContext context= new ProcessingContext(sourceBlocks, inlineParser,
				createIdGenerator(), ProcessingContext.INITIALIZE_CONTEXT );
		
		return context;
	}
	
	
	@Override
	public int hashCode() {
		return getName().hashCode() + this.mode;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}
		final CommonmarkLanguage other= (CommonmarkLanguage) obj;
		return (getName().equals(other.getName())
				&& this.mode == other.mode
				&& Objects.equals(this.config, other.getMarkupConfig()) );
	}
	
}
