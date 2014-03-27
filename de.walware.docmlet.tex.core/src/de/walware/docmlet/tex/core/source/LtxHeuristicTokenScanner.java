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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;

import de.walware.ecommons.text.BasicHeuristicTokenScanner;
import de.walware.ecommons.text.IPartitionConstraint;
import de.walware.ecommons.text.PartitioningConfiguration;


public class LtxHeuristicTokenScanner extends BasicHeuristicTokenScanner {
	
	
	public static final int CURLY_BRACKET_TYPE= 0;
	public static final int SQUARE_BRACKET_TYPE= 1;
	public static final int PARATHESIS_TYPE= 2;
	
	
	public static int getBracketType(final char c) {
		switch (c) {
		case '{':
		case '}':
			return CURLY_BRACKET_TYPE;
		case '[':
		case ']':
			return SQUARE_BRACKET_TYPE;
		case '(':
		case ')':
			return PARATHESIS_TYPE;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public static boolean isEscaped(final IDocument document, int offset)
			throws BadLocationException {
		boolean escaped= false;
		while (offset > 0 && document.getChar(--offset) == '\\') {
			escaped= !escaped;
		}
		return escaped;
	}
	
	public static int getSafeMathPartitionOffset(final IDocumentPartitioner partitioner, int offset) throws BadLocationException, BadPartitioningException {
		int startOffset= offset;
		while (offset > 0) {
			final ITypedRegion partition= partitioner.getPartition(offset - 1);
			final String partitionType= partition.getType();
			if (partitionType == ITexDocumentConstants.LTX_DEFAULT_CONTENT_TYPE
					|| partitionType == ITexDocumentConstants.LTX_DEFAULT_EXPL_CONTENT_TYPE
					|| partitionType == ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE
					|| partitionType == ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE) {
				return startOffset;
			}
			if (partitionType == ITexDocumentConstants.LTX_MATH_CONTENT_TYPE) {
				offset= startOffset= partition.getOffset();
				continue;
			}
			offset= partition.getOffset();
			continue;
		}
		return startOffset;
	}
	
	
	private class BracketBalanceCondition extends PartitionBasedCondition {
		
		private int type;
		private boolean open;
		
		@Override
		protected boolean matchesChar() {
			switch (LtxHeuristicTokenScanner.this.fChar) {
			case '{':
				this.type= CURLY_BRACKET_TYPE;
				this.open= true;
				return true;
			case '}':
				this.type= CURLY_BRACKET_TYPE;
				this.open= false;
				return true;
			case '[':
				this.type= SQUARE_BRACKET_TYPE;
				this.open= true;
				return true;
			case ']':
				this.type= SQUARE_BRACKET_TYPE;
				this.open= false;
				return true;
			case '(':
				this.type= PARATHESIS_TYPE;
				this.open= true;
				return true;
			case ')':
				this.type= PARATHESIS_TYPE;
				this.open= false;
				this.open= false;
				return true;
			}
			return false;
		}
		
	};
	
	
	public LtxHeuristicTokenScanner() {
		super(ITexDocumentConstants.LTX_PARTITIONING_CONFIG);
	}
	
	public LtxHeuristicTokenScanner(final PartitioningConfiguration partitioning) {
		super(partitioning);
	}
	
	
	@Override
	public void configure(final IDocument document, final String partitionType) {
		if (partitionType == ITexDocumentConstants.LTX_MATH_CONTENT_TYPE) {
			super.configure(document, new IPartitionConstraint() {
				private boolean fNever;
				@Override
				public boolean matches(final String partitionType) {
					if (this.fNever) {
						return false;
					}
					if (partitionType == ITexDocumentConstants.LTX_MATH_CONTENT_TYPE) {
						return true;
					}
					if (getDefaultPartitionConstraint().matches(partitionType)) {
						this.fNever= true;
					}
					return false;
				}
			});
			return;
		}
		super.configure(document, partitionType);
	}
	
//	@Override
//	protected int createForwardBound(final int start) throws BadLocationException {
//		final IPartitionConstraint matcher= getPartitionConstraint();
//		if (matcher.matches(ITexDocumentConstants.LTX_DEFAULT_CONTENT_TYPE)) {
//			return UNBOUND;
//		}
//		final ITypedRegion partition= TextUtilities.getPartition(fDocument, getPartitioning(), start, false);
//		return partition.getOffset()+partition.getLength();
//	}
//	
//	@Override
//	protected int createBackwardBound(final int start) throws BadLocationException {
//		final IPartitionConstraint matcher= getPartitionConstraint();
//		if (matcher.matches(ITexDocumentConstants.LTX_DEFAULT_CONTENT_TYPE)) {
//			return -1;
//		}
//		final ITypedRegion partition= TextUtilities.getPartition(fDocument, getPartitioning(), start, false);
//		return partition.getOffset();
//	}
	
	
	/**
	 * Computes bracket balance
	 * 
	 * @param backwardOffset searching backward before this offset
	 * @param forwardOffset searching forward after (including) this offset
	 * @param initial initial balance (e.g. known or not yet inserted between backward and forward offset)
	 * @param searchType interesting bracket type
	 * @return
	 * @see #getBracketType(char)
	 */
	public int[] computeBracketBalance(int backwardOffset, int forwardOffset, final int[] initial, final int searchType) {
		final int[] compute= new int[3];
		final BracketBalanceCondition condition= new BracketBalanceCondition();
		int breakType= -1;
		ITER_BACKWARD : while (--backwardOffset >= 0) {
			backwardOffset= scanBackward(backwardOffset, -1, condition);
			if (backwardOffset != NOT_FOUND) {
				if (condition.open) {
					compute[condition.type]++;
					if (condition.type != searchType && compute[condition.type] > 0) {
						breakType= condition.type;
						break ITER_BACKWARD;
					}
				}
				else {
					compute[condition.type]--;
				}
			}
			else {
				break ITER_BACKWARD;
			}
		}
		final int bound= this.fDocument.getLength();
		for (int i= 0; i < compute.length; i++) {
			if (compute[i] < 0) {
				compute[i]= 0;
			}
			compute[i]= compute[i]+initial[i];
		}
		ITER_FORWARD : while (forwardOffset < bound) {
			forwardOffset= scanForward(forwardOffset, bound, condition);
			if (forwardOffset != NOT_FOUND) {
				if (condition.open) {
					compute[condition.type]++;
				}
				else {
					compute[condition.type]--;
				}
				if (breakType >= 0 && compute[breakType] == 0) {
					break ITER_FORWARD;
				}
				forwardOffset++;
			}
			else {
				break ITER_FORWARD;
			}
		}
		return compute;
	}
	
}
