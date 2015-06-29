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

package de.walware.docmlet.tex.core.source;

import org.eclipse.jface.text.IDocument;

import de.walware.ecommons.text.core.treepartitioner.AbstractPartitionNodeType;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNode;


public abstract class LtxPartitionNodeType extends AbstractPartitionNodeType {
	
	
	public static final LtxPartitionNodeType DEFAULT_ROOT= new LtxPartitionNodeType() {
		
		@Override
		public String getPartitionType() {
			return ITexDocumentConstants.LTX_DEFAULT_CONTENT_TYPE;
		}
		
		@Override
		public byte getScannerState() {
			return LtxPartitionNodeScanner.S_DEFAULT;
		}
		
		@Override
		public boolean prefereAtBegin(final ITreePartitionNode node, final IDocument document) {
			return true;
		}
		
		@Override
		public boolean prefereAtEnd(final ITreePartitionNode node, final IDocument document) {
			return true;
		}
		
	};
	
	public static abstract class AbstractEnv extends LtxPartitionNodeType {
		
		private final String envName;
		
		private final char[] endPattern;
		
		
		public AbstractEnv(final String envName, final String endPattern) {
			this.envName= envName;
			this.endPattern= endPattern.toCharArray();
		}
		
		
		public final String getEnvName() {
			return this.envName;
		}
		
		@Override
		protected final char[] getEndPattern() {
			return this.endPattern;
		}
		
	}
	
	public static final LtxPartitionNodeType COMMENT_LINE= new LtxPartitionNodeType() {
		
		@Override
		public String getPartitionType() {
			return ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE;
		}
		
		@Override
		public byte getScannerState() {
			return LtxPartitionNodeScanner.S_COMMENT_LINE;
		}
		
	};
	
	public static class CommentEnv extends AbstractEnv {
		
		
		public CommentEnv(final String envName) {
			super(envName, ("end{" + envName + '}')); //$NON-NLS-1$
		}
		
		
		@Override
		public String getPartitionType() {
			return ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE;
		}
		
		@Override
		public byte getScannerState() {
			return LtxPartitionNodeScanner.S_COMMENT_ENV;
		}
		
	}
	
	public static final CommentEnv COMMENT_ENV_comment= new CommentEnv("comment"); //$NON-NLS-1$
	
	
	public static final LtxPartitionNodeType MATH_SPECIAL_$= new LtxPartitionNodeType() {
		
		private final char[] endPattern= "$".toCharArray(); //$NON-NLS-1$
		
		@Override
		public String getPartitionType() {
			return ITexDocumentConstants.LTX_MATH_CONTENT_TYPE;
		}
		
		@Override
		public byte getScannerState() {
			return LtxPartitionNodeScanner.S_MATH_SPECIAL_$;
		}
		
		@Override
		protected char getEndChar() {
			return '$';
		}
		
		@Override
		protected char[] getEndPattern() {
			return this.endPattern;
		}
		
	};
	
	public static final LtxPartitionNodeType MATH_SPECIAL_$_TEMPL= new LtxPartitionNodeType() {
		
		private final char[] endPattern= "$$".toCharArray(); //$NON-NLS-1$
		
		@Override
		public String getPartitionType() {
			return ITexDocumentConstants.LTX_MATH_CONTENT_TYPE;
		}
		
		@Override
		public byte getScannerState() {
			return LtxPartitionNodeScanner.S_MATH_SPECIAL_$;
		}
		
		@Override
		protected char getEndChar() {
			return '$';
		}
		
		@Override
		protected char[] getEndPattern() {
			return this.endPattern;
		}
		
	};
	
	public static final LtxPartitionNodeType MATH_SPECIAL_$$= new LtxPartitionNodeType() {
		
		private final char[] endPattern= "$$".toCharArray(); //$NON-NLS-1$
		
		@Override
		public String getPartitionType() {
			return ITexDocumentConstants.LTX_MATH_CONTENT_TYPE;
		}
		
		@Override
		public byte getScannerState() {
			return LtxPartitionNodeScanner.S_MATH_SPECIAL_$;
		}
		
		@Override
		protected char getEndChar() {
			return '$';
		}
		
		@Override
		protected char[] getEndPattern() {
			return this.endPattern;
		}
		
	};
	
	public static final LtxPartitionNodeType MATH_SPECIAL_$$_TEMPL= new LtxPartitionNodeType() {
		
		private final char[] endPattern= "$$$$".toCharArray(); //$NON-NLS-1$
		
		@Override
		public String getPartitionType() {
			return ITexDocumentConstants.LTX_MATH_CONTENT_TYPE;
		}
		
		@Override
		public byte getScannerState() {
			return LtxPartitionNodeScanner.S_MATH_SPECIAL_$;
		}
		
		@Override
		protected char getEndChar() {
			return '$';
		}
		
		@Override
		protected char[] getEndPattern() {
			return this.endPattern;
		}
		
	};
	
	public static final LtxPartitionNodeType MATH_SPECIAL_S= new LtxPartitionNodeType() {
		
		@Override
		public String getPartitionType() {
			return ITexDocumentConstants.LTX_MATH_CONTENT_TYPE;
		}
		
		@Override
		public byte getScannerState() {
			return LtxPartitionNodeScanner.S_MATH_SPECIAL_S;
		}
		
	};
	
	public static final LtxPartitionNodeType MATH_SPECIAL_P= new LtxPartitionNodeType() {
		
		@Override
		public String getPartitionType() {
			return ITexDocumentConstants.LTX_MATH_CONTENT_TYPE;
		}
		
		@Override
		public byte getScannerState() {
			return LtxPartitionNodeScanner.S_MATH_SPECIAL_P;
		}
		
	};
	
	
	public static class MathEnv extends AbstractEnv {
		
		
		public MathEnv(final String envName) {
			super(envName, envName);
		}
		
		
		@Override
		public String getPartitionType() {
			return ITexDocumentConstants.LTX_MATH_CONTENT_TYPE;
		}
		
		@Override
		public byte getScannerState() {
			return LtxPartitionNodeScanner.S_MATH_ENV;
		}
		
	};
	
	public static final MathEnv MATH_ENV_equation= new MathEnv("equation"); //$NON-NLS-1$
	public static final MathEnv MATH_ENV_eqnarray= new MathEnv("eqnarray"); //$NON-NLS-1$
	public static final MathEnv MATH_ENV_eqnarrayA= new MathEnv("eqnarray*"); //$NON-NLS-1$
	public static final MathEnv MATH_ENV_math= new MathEnv("math"); //$NON-NLS-1$
	public static final MathEnv MATH_ENV_displaymath= new MathEnv("displaymath"); //$NON-NLS-1$
	//AMSMath environments
	public static final MathEnv MATH_ENV_multline= new MathEnv("multline"); //$NON-NLS-1$
	public static final MathEnv MATH_ENV_multlineA= new MathEnv("multline*"); //$NON-NLS-1$
	public static final MathEnv MATH_ENV_gather= new MathEnv("gather"); //$NON-NLS-1$
	public static final MathEnv MATH_ENV_gatherA= new MathEnv("gather*"); //$NON-NLS-1$
	public static final MathEnv MATH_ENV_align= new MathEnv("align"); //$NON-NLS-1$
	public static final MathEnv MATH_ENV_alignA= new MathEnv("align*"); //$NON-NLS-1$
	public static final MathEnv MATH_ENV_alignat= new MathEnv("alignat"); //$NON-NLS-1$
	public static final MathEnv MATH_ENV_alignatA= new MathEnv("alignat*"); //$NON-NLS-1$
	public static final MathEnv MATH_ENV_flalign= new MathEnv("flalign"); //$NON-NLS-1$
	public static final MathEnv MATH_ENV_flalignA= new MathEnv("flalign*"); //$NON-NLS-1$
	
	public static final MathEnv MATH_ENV_xxx= new MathEnv("          "); //$NON-NLS-1$
	
	
	public static final LtxPartitionNodeType MATHCOMMENT_LINE= new LtxPartitionNodeType() {
		
		@Override
		public String getPartitionType() {
			return ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE;
		}
		
		@Override
		public byte getScannerState() {
			return LtxPartitionNodeScanner.S_MATHCOMMENT_LINE;
		}
		
	};
	
	public static class VerbatimInline extends LtxPartitionNodeType {
		
		
		private final char endChar;
		
		
		public VerbatimInline(final char endChar) {
			this.endChar= endChar;
		}
		
		
		@Override
		public String getPartitionType() {
			return ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE;
		}
		
		@Override
		public byte getScannerState() {
			return LtxPartitionNodeScanner.S_VERBATIM_LINE;
		}
		
		@Override
		protected char getEndChar() {
			return this.endChar;
		}
		
		@Override
		public int hashCode() {
			return 2986209 | this.endChar * 7;
		}
		
		@Override
		public boolean equals(final Object obj) {
			return (this == obj
					|| (getClass() == obj.getClass()
							&& this.endChar == ((VerbatimInline) obj).endChar ));
		}
		
	};
	
	public static class VerbatimEnv extends AbstractEnv {
		
		
		public VerbatimEnv(final String envName) {
			super(envName, ("end{" + envName + '}')); //$NON-NLS-1$
		}
		
		
		@Override
		public String getPartitionType() {
			return ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE;
		}
		
		@Override
		public byte getScannerState() {
			return LtxPartitionNodeScanner.S_VERBATIM_ENV;
		}
		
	};
	
	public static final VerbatimEnv VERBATIM_ENV_verbatim= new VerbatimEnv("verbatim"); //$NON-NLS-1$
	public static final VerbatimEnv VERBATIM_ENV_verbatimA= new VerbatimEnv("verbatim*"); //$NON-NLS-1$
	public static final VerbatimEnv VERBATIM_ENV_lstlisting= new VerbatimEnv("lstlisting"); //$NON-NLS-1$
	public static final VerbatimEnv VERBATIM_ENV_Sinput= new VerbatimEnv("Sinput"); //$NON-NLS-1$
	public static final VerbatimEnv VERBATIM_ENV_Soutput= new VerbatimEnv("Soutput"); //$NON-NLS-1$
	
	
	protected LtxPartitionNodeType() {
	}
	
	
	public abstract byte getScannerState();
	
	protected char[] getEndPattern() {
		return null;
	}
	
	protected char getEndChar() {
		return 0;
	}
	
}
