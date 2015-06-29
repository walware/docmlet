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

package de.walware.docmlet.wikitext.internal.ui.sourceediting;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jface.text.templates.Template;
import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.internal.wikitext.ui.editor.assist.Templates;
import org.eclipse.mylyn.wikitext.core.parser.Attributes;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.ui.WikiText;

import de.walware.ecommons.ltk.IElementName;
import de.walware.ecommons.ltk.core.model.IModelElement;

import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.model.IWikitextElement;
import de.walware.docmlet.wikitext.core.model.WikitextModel;


public class MarkupTemplates {
	
	
	public static class MarkupInfo implements IModelElement {
		
		private final int headingLevel;
		
		private final BlockType blockType;
		
		private final int modelElementType;
		
		
		public MarkupInfo() {
			this.headingLevel= 0;
			this.blockType= null;
			this.modelElementType= 0;
		}
		
		public MarkupInfo(final int headingLevel) {
			this.headingLevel= headingLevel;
			this.blockType= null;
			this.modelElementType= IWikitextElement.C2_SECTIONING | headingLevel;
		}
		
		public MarkupInfo(final BlockType blockType) {
			this.headingLevel= 0;
			this.blockType= blockType;
			this.modelElementType= 0;
		}
		
		
		public int getHeadingLevel() {
			return this.headingLevel;
		}
		
		public BlockType getBlockType() {
			return this.blockType;
		}
		
		@Override
		public String getModelTypeId() {
			return WikitextModel.WIKIDOC_TYPE_ID;
		}
		
		@Override
		public Object getAdapter(final Class adapter) {
			return null;
		}
		
		@Override
		public int getElementType() {
			return this.modelElementType;
		}
		
		@Override
		public IElementName getElementName() {
			return null;
		}
		
		@Override
		public String getId() {
			return null;
		}
		
		@Override
		public boolean exists() {
			return false;
		}
		
		@Override
		public boolean isReadOnly() {
			return false;
		}
		
		@Override
		public IModelElement getModelParent() {
			return null;
		}
		
		@Override
		public boolean hasModelChildren(final Filter filter) {
			return false;
		}
		
		@Override
		public List<? extends IModelElement> getModelChildren(final Filter filter) {
			return Collections.emptyList();
		}
		
	}
	
	
	private static class TemplateTester extends DocumentBuilder {
		
		
		private static final Pattern VARIABLE_PATTERN= Pattern.compile("(\\$\\{\\w*\\})"); //$NON-NLS-1$
		
		
		private final MarkupParser parser;
		
		private MarkupInfo found;
		
		
		public TemplateTester(MarkupLanguage markupLanguage) {
			markupLanguage= markupLanguage.clone();
			if (markupLanguage instanceof AbstractMarkupLanguage) {
				final AbstractMarkupLanguage language= (AbstractMarkupLanguage) markupLanguage;
				language.setFilterGenerativeContents(true);
				language.setBlocksOnly(true);
			}
			
			this.parser= new MarkupParser(markupLanguage, this);
		}
		
		
		public MarkupInfo createInfo(final Template template) {
			this.found= null;
			
			String text= template.getPattern();
			text= VARIABLE_PATTERN.matcher(text).replaceAll("Abcdefg ");
			text+= "Abcdefg \n\n\n";
			try {
				this.parser.parse(text);
			}
			catch (final Exception e) {}
			
			if (this.found == null) {
				this.found= new MarkupInfo();
			}
			
			return this.found;
		}
		
		
		@Override
		public void beginDocument() {
		}
		@Override
		public void endDocument() {
		}
		
		@Override
		public void beginBlock(final BlockType type, final Attributes attributes) {
			this.found= new MarkupInfo(type);
			throw new RuntimeException();
		}
		
		@Override
		public void endBlock() {
		}
		@Override
		public void beginSpan(final SpanType type, final Attributes attributes) {
		}
		@Override
		public void endSpan() {
		}
		
		@Override
		public void beginHeading(final int level, final Attributes attributes) {
			this.found= new MarkupInfo(level);
			throw new RuntimeException();
		}
		
		@Override
		public void endHeading() {
		}
		@Override
		public void characters(final String text) {
		}
		@Override
		public void entityReference(final String entity) {
		}
		@Override
		public void image(final Attributes attributes, final String url) {
		}
		@Override
		public void link(final Attributes attributes, final String hrefOrHashName, final String text) {
		}
		@Override
		public void imageLink(final Attributes linkAttributes, final Attributes imageAttributes,
				final String href, final String imageUrl) {
		}
		@Override
		public void acronym(final String text, final String definition) {
		}
		@Override
		public void lineBreak() {
		}
		@Override
		public void charactersUnescaped(final String literal) {
		}
		
	}
	
	
	private final IMarkupLanguage markupLanguage;
	
	private final List<Template> templates;
	
	private final Map<Template, MarkupInfo> templateInfos;
	
	
	public MarkupTemplates(final IMarkupLanguage markupLanguage) {
		this.markupLanguage= markupLanguage;
		this.templateInfos= new HashMap<>();
		this.templates= init();
	}
	
	
	@SuppressWarnings("restriction")
	private List<Template> init() {
		final Templates mylynTemplates= getMylynTemplates();
		if (mylynTemplates == null) {
			return Collections.emptyList();
		}
		final List<Template> list= mylynTemplates.getTemplate();
		
		final TemplateTester tester= new TemplateTester((MarkupLanguage) this.markupLanguage);
		for (final Template template : list) {
			if (mylynTemplates.isBlock(template)) {
				this.templateInfos.put(template, tester.createInfo(template));
			}
		}
		
		return list;
	}
	
	@SuppressWarnings("restriction")
	private Templates getMylynTemplates() {
		final Map<String, Templates> map= WikiTextUiPlugin.getDefault().getTemplates();
		MarkupLanguage language= (MarkupLanguage) this.markupLanguage;
		Templates mylynTemplates;
		do {
			mylynTemplates= map.get(language.getName());
		}
		while (mylynTemplates == null && language.getExtendsLanguage() != null
				&& (language= WikiText.getMarkupLanguage(language.getExtendsLanguage())) != null );
		return mylynTemplates;
	}
	
	
	public List<Template> getTemplates() {
		return this.templates;
	}
	
	public MarkupInfo getMarkupInfo(final Template template) {
		return this.templateInfos.get(template);
	}
	
}
