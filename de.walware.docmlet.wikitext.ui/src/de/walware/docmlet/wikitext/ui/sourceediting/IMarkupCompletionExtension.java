/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.ui.sourceediting;

import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;

import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;


public interface IMarkupCompletionExtension {
	
	
	static class CompletionType {
		
		private final String sourcePrefix;
		
		private final String lookupPrefix;
		
		
		public CompletionType(final String prefix) {
			this(prefix, prefix);
		}
		
		public CompletionType(final String sourcePrefix, final String lookupPrefix) {
			this.sourcePrefix= sourcePrefix;
			this.lookupPrefix= lookupPrefix;
		}
		
		
		public String getSourcePrefix() {
			return this.sourcePrefix;
		}
		
		public String getLookupPrefix() {
			return this.lookupPrefix;
		}
		
	}
	
	
	CompletionType getLinkAnchorLabel(AssistInvocationContext context, IMarkupLanguage markupLanguage);
	
	CompletionType getLinkRefLabel(AssistInvocationContext context, IMarkupLanguage markupLanguage);
	
}
