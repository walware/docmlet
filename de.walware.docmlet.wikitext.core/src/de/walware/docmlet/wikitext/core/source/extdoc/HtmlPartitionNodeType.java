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

package de.walware.docmlet.wikitext.core.source.extdoc;

import de.walware.ecommons.text.core.treepartitioner.AbstractPartitionNodeType;

import de.walware.docmlet.wikitext.core.source.IWikitextDocumentConstants;


public abstract class HtmlPartitionNodeType extends AbstractPartitionNodeType {
	
	
	public static final class Default extends HtmlPartitionNodeType {
		
		@Override
		public String getPartitionType() {
			return IWikitextDocumentConstants.WIKIDOC_HTML_DEFAULT_CONTENT_TYPE;
		}
		
	};
	
	
	public static final HtmlPartitionNodeType COMMENT= new HtmlPartitionNodeType() {
		
		@Override
		public String getPartitionType() {
			return IWikitextDocumentConstants.WIKIDOC_HTML_COMMENT_CONTENT_TYPE;
		}
		
	};
	
}
