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

import org.eclipse.jface.text.IDocument;

import de.walware.ecommons.text.IPartitionConstraint;
import de.walware.ecommons.text.PartitioningConfiguration;


public interface ITexDocumentConstants {
	
	
	public final static String LTX_PARTITIONING= "ltx_walware"; //$NON-NLS-1$
	
	public final static String LTX_DEFAULT_CONTENT_TYPE= IDocument.DEFAULT_CONTENT_TYPE;
	public final static String LTX_DEFAULT_EXPL_CONTENT_TYPE= "ltx.default"; //$NON-NLS-1$
	public final static String LTX_COMMENT_CONTENT_TYPE= "ltx.comment"; //$NON-NLS-1$
	public static final String LTX_MATH_CONTENT_TYPE= "ltx.math"; //$NON-NLS-1$
	public final static String LTX_MATHCOMMENT_CONTENT_TYPE= "ltx.mathcomment"; //$NON-NLS-1$
	public static final String LTX_VERBATIM_CONTENT_TYPE= "ltx.verbatim"; //$NON-NLS-1$
	
	
	public static final String[] LTX_PARTITION_TYPES= new String[] {
			LTX_DEFAULT_CONTENT_TYPE,
			LTX_COMMENT_CONTENT_TYPE,
			LTX_MATH_CONTENT_TYPE,
			LTX_MATHCOMMENT_CONTENT_TYPE,
			LTX_VERBATIM_CONTENT_TYPE,
	};
	
	public static final IPartitionConstraint LTX_DEFAULT_CONSTRAINT= new IPartitionConstraint() {
				@Override
				public boolean matches(final String partitionType) {
					return (partitionType == LTX_DEFAULT_CONTENT_TYPE
							|| partitionType == LTX_DEFAULT_EXPL_CONTENT_TYPE );
				}
	};
	
	public static final IPartitionConstraint LTX_DEFAULT_OR_MATH_CONSTRAINT= new IPartitionConstraint() {
		@Override
		public boolean matches(final String partitionType) {
			return (partitionType == LTX_DEFAULT_CONTENT_TYPE
					|| partitionType == LTX_DEFAULT_EXPL_CONTENT_TYPE
					|| partitionType == LTX_MATH_CONTENT_TYPE );
		}
	};
	
	public static final PartitioningConfiguration LTX_PARTITIONING_CONFIG= new PartitioningConfiguration(
			LTX_PARTITIONING, LTX_DEFAULT_CONSTRAINT);
	
}
