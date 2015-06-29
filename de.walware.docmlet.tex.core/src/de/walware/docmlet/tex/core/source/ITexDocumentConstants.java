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

import java.util.List;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.text.core.IPartitionConstraint;


public interface ITexDocumentConstants {
	
	
	/**
	 * The id of partitioning of LaTeX documents.
	 */
	String LTX_PARTITIONING= "Ltx_walware"; //$NON-NLS-1$
	
	String LTX_DEFAULT_CONTENT_TYPE= "Ltx.Default"; //$NON-NLS-1$
	String LTX_COMMENT_CONTENT_TYPE= "Ltx.Comment"; //$NON-NLS-1$
	String LTX_MATH_CONTENT_TYPE= "Ltx.Math"; //$NON-NLS-1$
	String LTX_MATHCOMMENT_CONTENT_TYPE= "Ltx.MathComment"; //$NON-NLS-1$
	String LTX_VERBATIM_CONTENT_TYPE= "Ltx.Verbatim"; //$NON-NLS-1$
	
	/**
	 * List with all partition content types of LaTeX documents.
	 */
	List<String> LTX_CONTENT_TYPES= ImCollections.newList(
			LTX_DEFAULT_CONTENT_TYPE,
			LTX_COMMENT_CONTENT_TYPE,
			LTX_MATH_CONTENT_TYPE,
			LTX_MATHCOMMENT_CONTENT_TYPE,
			LTX_VERBATIM_CONTENT_TYPE );
	
	
	IPartitionConstraint LTX_DEFAULT_CONTENT_CONSTRAINT= new IPartitionConstraint() {
				@Override
				public boolean matches(final String partitionType) {
					return (partitionType == LTX_DEFAULT_CONTENT_TYPE);
				}
	};
	
	IPartitionConstraint LTX_DEFAULT_OR_MATH_CONTENT_CONSTRAINT= new IPartitionConstraint() {
		@Override
		public boolean matches(final String partitionType) {
			return (partitionType == LTX_DEFAULT_CONTENT_TYPE
					|| partitionType == LTX_MATH_CONTENT_TYPE );
		}
	};
	
	IPartitionConstraint LTX_ANY_CONTENT_CONSTRAINT= new IPartitionConstraint() {
		@Override
		public boolean matches(final String partitionType) {
//			return (partitionType.startsWith("Ltx.")); //$NON-NLS-1$
			return (partitionType == LTX_DEFAULT_CONTENT_TYPE
					|| partitionType == LTX_MATH_CONTENT_TYPE
					|| partitionType == LTX_COMMENT_CONTENT_TYPE
					|| partitionType == LTX_MATHCOMMENT_CONTENT_TYPE
					|| partitionType == LTX_VERBATIM_CONTENT_TYPE );
		}
	};
	
}
