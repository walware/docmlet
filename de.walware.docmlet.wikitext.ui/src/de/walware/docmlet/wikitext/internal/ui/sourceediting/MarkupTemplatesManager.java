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

import java.util.HashMap;
import java.util.Map;

import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;


public class MarkupTemplatesManager {
	
	
	public static final MarkupTemplatesManager INSTANCE= new MarkupTemplatesManager();
	
	
	private final Map<String, MarkupTemplates> markupLanguageTemplates= new HashMap<>();
	
	
	private MarkupTemplatesManager() {
	}
	
	
	public synchronized MarkupTemplates getTemplates(final IMarkupLanguage markupLanguage) {
		MarkupTemplates templates= this.markupLanguageTemplates.get(markupLanguage.getName());
		if (templates == null) {
			templates= new MarkupTemplates(markupLanguage);
			this.markupLanguageTemplates.put(markupLanguage.getName(), templates);
		}
		return templates;
	}
	
}
