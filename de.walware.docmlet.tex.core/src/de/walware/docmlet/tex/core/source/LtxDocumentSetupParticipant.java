/*=============================================================================#
 # Copyright (c) 2009-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.source;

import de.walware.ecommons.text.Partitioner;
import de.walware.ecommons.text.PartitionerDocumentSetupParticipant;


/**
 * The document setup participant for TeX.
 */
public class LtxDocumentSetupParticipant extends PartitionerDocumentSetupParticipant {
	
	
	private final boolean fTemplateMode;
	
	
	public LtxDocumentSetupParticipant() {
		fTemplateMode = false;
	}
	
	public LtxDocumentSetupParticipant(final boolean enableTemplateMode) {
		fTemplateMode = enableTemplateMode;
	}
	
	
	@Override
	public String getPartitioningId() {
		return ITexDocumentConstants.LTX_PARTITIONING;
	}
	
	@Override
	protected Partitioner createDocumentPartitioner() {
		return new Partitioner(
				new LtxFastPartitionScanner(ITexDocumentConstants.LTX_PARTITIONING, fTemplateMode),
				ITexDocumentConstants.LTX_PARTITION_TYPES) {
			@Override
			protected String getPrefereOpenType(final String open, final String opening) {
				if ((open == ITexDocumentConstants.LTX_MATH_CONTENT_TYPE || open == ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE)
						|| (opening == ITexDocumentConstants.LTX_MATH_CONTENT_TYPE || opening == ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE) ) {
					return ITexDocumentConstants.LTX_MATH_CONTENT_TYPE;
				}
				return super.getPrefereOpenType(open, opening);
			}
		};
	}
	
}
