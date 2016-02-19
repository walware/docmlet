/*=============================================================================#
 # Copyright (c) 2014-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import org.eclipse.mylyn.internal.wikitext.ui.WikiTextUiPlugin;
import org.eclipse.mylyn.internal.wikitext.ui.editor.help.HelpContent;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.ui.WikiText;

import de.walware.docmlet.base.ui.markuphelp.IMarkupHelpContentProvider;
import de.walware.docmlet.base.ui.markuphelp.MarkupHelpContent;


@SuppressWarnings("restriction")
public class WikitextMarkupHelpProvider implements IMarkupHelpContentProvider {
	
	
	public static final String NS= "wikitext"; //$NON-NLS-1$
	
	private static final String NS_= NS + ':';
	
	
	private static class WikitextHelpTopic extends MarkupHelpContent {
		
		
		private HelpContent mylynHelp;
		
		private String content;
		
		
		public WikitextHelpTopic(final HelpContent mylynHelp) {
			super(NS_ + mylynHelp.getMarkupLanguageName(), mylynHelp.getMarkupLanguageName());
			
			this.mylynHelp= mylynHelp;
		}
		
		@Override
		public String getContent() throws IOException {
			if (this.mylynHelp != null) {
				try {
					this.content= this.mylynHelp.getContent();
				}
				finally {
					this.mylynHelp= null;
				}
			}
			return this.content;
		}
		
	}
	
	public static String getContentIdFor(MarkupLanguage markupLanguage) {
		final SortedMap<String, HelpContent> mylynHelps= WikiTextUiPlugin.getDefault().getCheatSheets();
		
		while (markupLanguage != null
				&& !mylynHelps.containsKey(markupLanguage.getName()) ) {
			markupLanguage= WikiText.getMarkupLanguage(markupLanguage.getExtendsLanguage());
		}
		
		return (markupLanguage != null) ? (NS_ + markupLanguage.getName()) : null;
	}
	
	
	public WikitextMarkupHelpProvider() {
	}
	
	
	@Override
	public synchronized Collection<MarkupHelpContent> getHelpTopics() {
		final SortedMap<String, HelpContent> mylynHelps= WikiTextUiPlugin.getDefault().getCheatSheets();
		
		final List<MarkupHelpContent> topics= new ArrayList<>(mylynHelps.size());
		for (final HelpContent mylynHelp : mylynHelps.values()) {
			topics.add(new WikitextHelpTopic(mylynHelp));
		}
		return topics;
	}
	
}
