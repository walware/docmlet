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

package de.walware.docmlet.wikitext.core.source;

import java.util.List;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.text.core.IPartitionConstraint;

import de.walware.eutils.yaml.core.source.IYamlDocumentConstants;


public interface IWikitextDocumentConstants {
	
	
	/**
	 * The id of partitioning of Wikitext documents.
	 */
	String WIKIDOC_PARTITIONING= "Wikidoc_walware"; //$NON-NLS-1$
	
	
	String WIKIDOC_DEFAULT_CONTENT_TYPE= "Wikitext.Default"; //$NON-NLS-1$
	
	String WIKIDOC_YAML_CHUNK_CONTENT_TYPE= "WikidocYamlConfChunk"; //$NON-NLS-1$
	
	String WIKIDOC_HTML_DEFAULT_CONTENT_TYPE= "Html.Default"; //$NON-NLS-1$
	String WIKIDOC_HTML_COMMENT_CONTENT_TYPE= "Html.Comment"; //$NON-NLS-1$
	
	
	/**
	 * List with all partition content types of Wikitext documents.
	 */
	List<String> WIKIDOC_CONTENT_TYPES= ImCollections.newList(
			WIKIDOC_DEFAULT_CONTENT_TYPE,
			WIKIDOC_HTML_DEFAULT_CONTENT_TYPE,
			WIKIDOC_HTML_COMMENT_CONTENT_TYPE );
	
	List<String> YAML_CHUNK_CONTENT_TYPES= ImCollections.newList(
			WIKIDOC_YAML_CHUNK_CONTENT_TYPE );
	
	List<String> WIKIDOC_EXT_CONTENT_TYPES= ImCollections.concatList(
			WIKIDOC_CONTENT_TYPES,
			YAML_CHUNK_CONTENT_TYPES,
			IYamlDocumentConstants.YAML_CONTENT_TYPES );
	
	
	IPartitionConstraint WIKIDOC_DEFAULT_CONTENT_CONSTRAINT= new IPartitionConstraint() {
		@Override
		public boolean matches(final String partitionType) {
			return (partitionType == WIKIDOC_DEFAULT_CONTENT_TYPE);
		}
	};
	
}
