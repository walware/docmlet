/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.source;

import org.eclipse.jface.text.IDocumentPartitioner;

import de.walware.ecommons.text.PartitionerDocumentSetupParticipant;
import de.walware.ecommons.text.core.treepartitioner.TreePartitioner;


/**
 * The document setup participant for TeX.
 */
public class LtxDocumentSetupParticipant extends PartitionerDocumentSetupParticipant {
	
	
	private final boolean templateMode;
	
	
	public LtxDocumentSetupParticipant() {
		this.templateMode = false;
	}
	
	public LtxDocumentSetupParticipant(final boolean enableTemplateMode) {
		this.templateMode = enableTemplateMode;
	}
	
	
	@Override
	public String getPartitioningId() {
		return ITexDocumentConstants.LTX_PARTITIONING;
	}
	
	@Override
	protected IDocumentPartitioner createDocumentPartitioner() {
		return new TreePartitioner(getPartitioningId(),
				new LtxPartitionNodeScanner(this.templateMode),
				ITexDocumentConstants.LTX_CONTENT_TYPES );
	}
	
}
