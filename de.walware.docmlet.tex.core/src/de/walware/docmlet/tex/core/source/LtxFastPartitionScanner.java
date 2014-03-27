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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import de.walware.ecommons.collections.IntArrayMap;
import de.walware.ecommons.collections.IntMap;
import de.walware.ecommons.text.IPartitionScannerCallbackExt;
import de.walware.ecommons.text.IPartitionScannerConfigExt;
import de.walware.ecommons.text.Partitioner;
import de.walware.ecommons.text.core.rules.BufferedDocumentScanner;
import de.walware.ecommons.text.core.rules.OperatorRule;

import de.walware.docmlet.tex.core.TexCore;
import de.walware.docmlet.tex.internal.core.TexCorePlugin;


/**
 * 
 * Comment (only in default partitions, at moment):
 * %... EOL
 * \begin{comment} ... \end{comment}
 *
 * Math:
 * \begin{math} ... \end{math}
 * \begin{displaymath} ... \end{displaymath}
 * \begin{equation} ... \end{equation}
 * \( ... \)      - math
 * \[ ... \]      - displaymath
 * $ ... $        - math
 * $$ ... $$      - displaymath
 * \begin{eqnarray} ... \end{eqnarray}
 * \begin{align} ... \end{align}
 * \begin{alignat} ... \end{alignat}
 * \begin{flalign} ... \end{flalign}
 * \begin{multline} ... \end{multline}
 * \begin{gather} ... \end{gather}
 * 
 * Verbatim:
 * \begin{verbatim} ... \end{verbatim}
 * \begin{lstlisting} ... \end{lstlisting}
 * \verb? ... ?
 */
public class LtxFastPartitionScanner implements IPartitionTokenScanner, IPartitionScannerConfigExt,
		IPartitionScannerCallbackExt {
	
	
	/**
	 * Enum of states of the scanner.
	 * Note: id is index in array of tokens
	 * 0-11 are reserved for this class.
	 **/
	protected static final int S_DEFAULT= 0;
	protected static final int S_MATH_SPECIAL_$= 1;
	protected static final int S_MATH_SPECIAL_S= 2;
	protected static final int S_MATH_SPECIAL_P= 3;
	protected static final int S_MATH_ENV= 4;
	protected static final int S_VERBATIM_LINE= 5;
	protected static final int S_VERBATIM_ENV= 6;
	protected static final int S_COMMENT_LINE= 7;
	protected static final int S_COMMENT_ENV= 8;
	protected static final int S_MATHCOMMENT_LINE= 9;
	protected static final int S_INTERNAL_ENDENV= 10;
	
	protected final static IToken T_DEFAULT= new Token(ITexDocumentConstants.LTX_DEFAULT_EXPL_CONTENT_TYPE);
	protected final static IToken T_MATH= new Token(ITexDocumentConstants.LTX_MATH_CONTENT_TYPE);
	protected final static IToken T_VERBATIM= new Token(ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE);
	protected final static IToken T_COMMENT= new Token(ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE);
	protected final static IToken T_MATHCOMMENT= new Token(ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE);
	
	
	/** Enum of last significant characters read. */
	protected static final int LAST_OTHER= 0;
	protected static final int LAST_BACKSLASH= 1;
	protected static final int LAST_NEWLINE= 2;
	
	private static final char[] SEQ_begin= "begin".toCharArray(); //$NON-NLS-1$
	private static final char[] SEQ_end= "end".toCharArray(); //$NON-NLS-1$
	private static final char[] SEQ_verb= "verb".toCharArray(); //$NON-NLS-1$
	private static final char[] SEQ_$= "$".toCharArray(); //$NON-NLS-1$
	private static final char[] SEQ_$$= "$$".toCharArray(); //$NON-NLS-1$
	private static final char[] SEQ_$$$$= "$$$$".toCharArray(); //$NON-NLS-1$
	private static final char[] SEQ_dummy= "\u0000\u0000\u0000".toCharArray(); //$NON-NLS-1$
	
	
	private static class EnvType {
		final String name;
		final char[] endPattern;
		final int state;
		
		public EnvType(final String envName, final int state) {
			this.name= envName;
			switch (state) {
			case S_VERBATIM_ENV:
			case S_COMMENT_ENV:
				this.endPattern= ("end{"+envName+"}").toCharArray(); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			default:
				this.endPattern= envName.toCharArray();
				break;
			}
			this.state= state;
		}
	}
	
	
	/** The scanner. */
	private final BufferedDocumentScanner fScanner= new BufferedDocumentScanner(1000);	// faster implementation
	private final boolean fTemplateMode;
	
	private IDocument document;
	
	private IToken token;
	/** The offset of the last returned token. */
	private int tokenOffset;
	/** The length of the last returned token. */
	private int tokenLength;
	
	private int startPartitionState= S_DEFAULT;
	/** The current state of the scanner. */
	private int state;
	/** The last significant characters read. */
	protected int last;
	/** The amount of characters already read on first call to nextToken(). */
	private int prefixLength;
	
	private int mathState;
	
	private int rangeStart;
	private int rangeEnd;
	
	private final IToken[] stateTokens;
	
	private final OperatorRule envNameRule;
	private final Map<String, EnvType> envStates;
	private char[] envEndPattern;
	
	private char verbEndPattern;
	
	private Partitioner partitioner;
	
	
	public LtxFastPartitionScanner(final String partitioning) {
		this(partitioning, false);
	}
	
	/**
	 * 
	 * @param templateMode enabled mode for Eclipse template syntax with $ as prefix,
	 * so dollar must be doubled for math modes.
	 */
	public LtxFastPartitionScanner(final String partitioning, final boolean templateMode) {
		this.fTemplateMode= templateMode;
		this.envNameRule= new OperatorRule(new char[] {});
		this.envStates= new HashMap<>();
		
		final IntArrayMap<IToken> list= new IntArrayMap<>();
		initTokens(list);
		final int count= list.getMaxKey()+1;
		this.stateTokens= new IToken[count];
		for (int i= 0; i < count; i++) {
			this.stateTokens[i]= list.get(i);
		}
	}
	
	@Override
	public void setPartitionerCallback(final Partitioner partitioner) {
		this.partitioner= partitioner;
	}
	
	
	protected void addEnvRule(final String name, final int state) {
		this.envStates.put(name, new EnvType(name, state));
		this.envNameRule.addOp(name, null);
	}
	
	protected void initTokens(final IntMap<IToken> states) {
		states.put(S_DEFAULT, T_DEFAULT);
		states.put(S_MATH_SPECIAL_$, T_MATH);
		states.put(S_MATH_SPECIAL_S, T_MATH);
		states.put(S_MATH_SPECIAL_P, T_MATH);
		states.put(S_MATH_ENV, T_MATH);
		states.put(S_VERBATIM_LINE, T_VERBATIM);
		states.put(S_VERBATIM_ENV, T_VERBATIM);
		states.put(S_COMMENT_LINE, T_COMMENT);
		states.put(S_COMMENT_ENV, T_COMMENT);
		states.put(S_MATHCOMMENT_LINE, T_MATHCOMMENT);
		states.put(S_INTERNAL_ENDENV, T_DEFAULT); // type is replace if required
		
		addEnvRule("comment", S_COMMENT_ENV); //$NON-NLS-1$
		
		addEnvRule("verbatim", S_VERBATIM_ENV); //$NON-NLS-1$
		addEnvRule("verbatim*", S_VERBATIM_ENV); //$NON-NLS-1$
		addEnvRule("lstlisting", S_VERBATIM_ENV); //$NON-NLS-1$
		addEnvRule("Sinput", S_VERBATIM_ENV); //$NON-NLS-1$
		addEnvRule("Soutput", S_VERBATIM_ENV); //$NON-NLS-1$
		
		addEnvRule("equation", S_MATH_ENV); //$NON-NLS-1$
		addEnvRule("eqnarray", S_MATH_ENV); //$NON-NLS-1$
		addEnvRule("eqnarray*", S_MATH_ENV); //$NON-NLS-1$
		addEnvRule("math", S_MATH_ENV); //$NON-NLS-1$
		addEnvRule("displaymath", S_MATH_ENV); //$NON-NLS-1$
		//AMSMath environments
		addEnvRule("multline", S_MATH_ENV); //$NON-NLS-1$
		addEnvRule("multline*", S_MATH_ENV); //$NON-NLS-1$
		addEnvRule("gather", S_MATH_ENV); //$NON-NLS-1$
		addEnvRule("gather*", S_MATH_ENV); //$NON-NLS-1$
		addEnvRule("align", S_MATH_ENV); //$NON-NLS-1$
		addEnvRule("align*", S_MATH_ENV); //$NON-NLS-1$
		addEnvRule("alignat", S_MATH_ENV); //$NON-NLS-1$
		addEnvRule("alignat*", S_MATH_ENV); //$NON-NLS-1$
		addEnvRule("flalign", S_MATH_ENV); //$NON-NLS-1$
		addEnvRule("flalign*", S_MATH_ENV); //$NON-NLS-1$
	}
	
	/**
	 * Sets explicitly the partition type on position 0.
	 * 
	 * @param contentType
	 */
	@Override
	public void setStartPartitionType(final String contentType) {
		this.startPartitionState= getState(contentType);
	}
	
	@Override
	public void setRange(final IDocument document, final int offset, final int length) {
		setPartialRange(document, offset, length, null, -1);
	}
	
	@Override
	public void setPartialRange(final IDocument document, final int offset, final int length, final String contentType, int partitionOffset) {
		if (partitionOffset < 0) {
			partitionOffset= offset;
		}
		this.document= document;
		this.rangeStart= this.tokenOffset= partitionOffset;
		this.rangeEnd= offset+length;
		
		if (offset > 0) {
			try {
				initState(contentType, offset, offset-partitionOffset);
				return;
			}
			catch (final Exception e) {
				TexCorePlugin.log(new Status(IStatus.ERROR, TexCore.PLUGIN_ID,
						"An error occured when detecting initial state for the tex partition scanner.",
						e ));
				this.state= S_DEFAULT;
				prepareScan(partitionOffset, 0);
			}
			finally {
				this.partitioner.resetCache();
			}
		}
		else {
			prepareScan(0, 0);
			this.state= this.startPartitionState;
			this.envEndPattern= SEQ_dummy;
		}
	}
	
	private void prepareScan(final int offset, final int prefixLength) {
		this.tokenLength= 0;
		if (offset > 0) {
			this.last= LAST_OTHER;
			try {
				final char c= this.document.getChar(offset-1);
				switch (c) {
				case '\r':
				case '\n':
					this.last= LAST_NEWLINE;
					break;
				}
			}
			catch (final BadLocationException e) {
			}
		}
		else {
			this.last= LAST_NEWLINE;
		}
		this.prefixLength= prefixLength;
		this.fScanner.setRange(this.document, offset, this.rangeEnd-offset);
	}
	
	
	@Override
	public int getTokenLength() {
		return this.tokenLength;
	}
	
	@Override
	public int getTokenOffset() {
		return this.tokenOffset;
	}
	
	
	@Override
	public IToken nextToken() {
		while (true) {
			this.token= null;
			this.tokenOffset += this.tokenLength;
			this.tokenLength= this.prefixLength;
			
			do {
				// characters
				switch (this.state) {
				case -1:
					this.token= Token.EOF;
					this.tokenLength= 0;
					break;
				case S_DEFAULT:
					searchDefault();
					break;
				case S_MATH_SPECIAL_$:
				case S_MATH_SPECIAL_S:
				case S_MATH_SPECIAL_P:
					searchMathSpecial();
					break;
				case S_MATH_ENV:
					searchMathEnv();
					break;
				case S_VERBATIM_LINE:
					searchVerbatimLine(this.verbEndPattern, S_DEFAULT);
					break;
				case S_COMMENT_LINE:
					searchCommentLine();
					break;
				case S_MATHCOMMENT_LINE:
					searchMathCommentLine();
					break;
				case S_VERBATIM_ENV:
				case S_COMMENT_ENV:
					searchVerbatimEnv();
					break;
				case S_INTERNAL_ENDENV:
					searchInternalEndEnv();
					break;
				default:
					searchExtState(this.state);
					break;
				}
			} while (this.token == null);
			
			if ((this.tokenLength > 0) ?
					(this.tokenOffset + this.tokenLength <= this.rangeStart) :
					(this.tokenOffset < this.rangeStart) ) {
				continue;
			}
//			if (fTokenOffset < fRangeStart) {
//				fTokenLength -= fRangeStart - fTokenOffset;
//				fTokenOffset= fRangeStart;
//			}
//			try {
//				System.out.println("Range [" + fRangeStart + "," + fRangeEnd + "]");
//				System.out.println("Token " + fToken.getData() + " " + fTokenOffset + "," + fTokenLength);
//				System.out.println("Doc " + fDocument.get(fTokenOffset, fTokenLength));
//				System.out.println("Next " + fDocument.get(fTokenOffset + fTokenLength, Math.min(100, fDocument.getLength() - fTokenOffset - fTokenLength)));
//			}
//			catch (Exception e) {}
			return this.token;
		}
	}
	
	protected void searchDefault() {
		LOOP: while (true) {
			int c;
			switch (this.fScanner.read()) {
			case ICharacterScanner.EOF:
				this.last= LAST_OTHER;
				newState(-1);
				return;
			case '\r':
				this.tokenLength++;
				readChar('\n');
				this.last= LAST_NEWLINE;
				return;
			case '\n':
				this.tokenLength++;
				this.last= LAST_NEWLINE;
				return;
			case '\\':
				this.tokenLength++;
				switch (c= this.fScanner.read()) {
				case ICharacterScanner.EOF:
					this.last= LAST_OTHER;
					newState(-1);
					return;
				case '\r':
					this.tokenLength++;
					readChar('\n');
					this.last= LAST_NEWLINE;
					return;
				case '\n':
					this.tokenLength++;
					this.last= LAST_NEWLINE;
					return;
				case 'b':
					this.tokenLength++;
					if (readSeq2(SEQ_begin)) {
						checkForBeginEnv();
						return;
					}
					break;
				case 'v':
					this.tokenLength++;
					if (readSeq2(SEQ_verb)) {
						final int c6= this.fScanner.read();
						if (c6 <= 32 | Character.isLetter(c6)) {
							if (c6 >= 0) {
								this.fScanner.unread();
							}
							return;
						}
						this.tokenLength++;
						this.verbEndPattern= (char) c6;
						newState(S_VERBATIM_LINE, 6); // \verb
						return;
					}
					break;
				case '[':
					this.tokenLength++;
					newState(S_MATH_SPECIAL_S, 2);
					return;
				case '(':
					this.tokenLength++;
					newState(S_MATH_SPECIAL_P, 2);
					return;
				default:
					this.tokenLength++;
					break;
				}
				if (searchExtCommand(c)) {
					return;
				}
				continue LOOP;
			case '%':
				this.tokenLength++;
				this.last= LAST_OTHER;
				newState(S_COMMENT_LINE, 1);
				return;
			case '$':
				this.tokenLength++;
				this.last= LAST_OTHER;
				if (readChar('$')) {
					if (this.fTemplateMode && readChar('$')) {
						if (readChar('$')) {
							this.envEndPattern= SEQ_$$$$;
							newState(S_MATH_SPECIAL_$, 4);
							return;
						}
						this.envEndPattern= SEQ_$$;
						newState(S_MATH_SPECIAL_$, 3);
						return;
					}
					this.envEndPattern= SEQ_$$;
					newState(S_MATH_SPECIAL_$, 2);
					return;
				}
				if (!this.fTemplateMode) {
					this.envEndPattern= SEQ_$;
					newState(S_MATH_SPECIAL_$, 1);
					return;
				}
				continue LOOP;
			default:
				this.tokenLength++;
				continue LOOP;
			}
		}
	}
	
	protected void searchMathSpecial() {
		LOOP: while (true) {
			int c;
			switch (this.fScanner.read()) {
			case ICharacterScanner.EOF:
				this.last= LAST_OTHER;
				newState(-1);
				return;
			case '\r':
				this.tokenLength++;
				readChar('\n');
				this.last= LAST_NEWLINE;
				return;
			case '\n':
				this.tokenLength++;
				this.last= LAST_NEWLINE;
				return;
			case '\\':
				this.tokenLength++;
				switch (c= this.fScanner.read()) {
				case ICharacterScanner.EOF:
					this.last= LAST_OTHER;
					newState(-1);
					return;
				case '\r':
					this.tokenLength++;
					readChar('\n');
					this.last= LAST_NEWLINE;
					return;
				case '\n':
					this.tokenLength++;
					this.last= LAST_NEWLINE;
					return;
				case ')':
					this.tokenLength++;
					if (this.state == S_MATH_SPECIAL_P) {
						this.last= LAST_OTHER;
						newState(S_DEFAULT);
						return;
					}
					continue LOOP;
				case ']':
					this.tokenLength++;
					if (this.state == S_MATH_SPECIAL_S) {
						this.last= LAST_OTHER;
						newState(S_DEFAULT);
						return;
					}
					continue LOOP;
				default:
					this.tokenLength++;
					break;
				}
				if (searchExtCommand(c)) {
					return;
				}
				continue LOOP;
			case '%':
				this.tokenLength++;
				this.last= LAST_OTHER;
				this.mathState= this.state;
				newState(S_MATHCOMMENT_LINE, 1);
				return;
			case '$':
				this.tokenLength++;
				if (this.state == S_MATH_SPECIAL_$ && readSeq2Consuming(this.envEndPattern)) {
					this.last= LAST_OTHER;
					newState(S_DEFAULT);
					return;
				}
				continue LOOP;
			default:
				this.tokenLength++;
				continue LOOP;
			}
		}
	}
	
	protected void searchMathEnv() {
		LOOP: while (true) {
			int c;
			switch (this.fScanner.read()) {
			case ICharacterScanner.EOF:
				this.last= LAST_OTHER;
				newState(-1);
				return;
			case '\r':
				this.tokenLength++;
				readChar('\n');
				this.last= LAST_NEWLINE;
				return;
			case '\n':
				this.tokenLength++;
				this.last= LAST_NEWLINE;
				return;
			case '\\':
				this.tokenLength++;
				switch (c= this.fScanner.read()) {
				case ICharacterScanner.EOF:
					this.last= LAST_OTHER;
					newState(-1);
					return;
				case '\r':
					this.tokenLength++;
					readChar('\n');
					this.last= LAST_NEWLINE;
					return;
				case '\n':
					this.tokenLength++;
					this.last= LAST_NEWLINE;
					return;
				case 'e':
					this.tokenLength++;
					if (readCharsConsuming('n', 'd')) {
						c= this.fScanner.read();
						if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
							this.tokenLength++;
							continue LOOP;
						}
						if (c == ICharacterScanner.EOF) {
							this.last= LAST_OTHER;
							newState(-1);
							return;
						}
						this.fScanner.unread();
						this.stateTokens[S_INTERNAL_ENDENV]= this.stateTokens[this.state];
						this.mathState= this.state;
						this.state= S_INTERNAL_ENDENV;
						searchInternalEndEnv();
						return;
					}
					break;
				default:
					this.tokenLength++;
					break;
				}
				if (searchExtCommand(c)) {
					return;
				}
				continue LOOP;
			case '%':
				this.tokenLength++;
				this.last= LAST_OTHER;
				this.mathState= this.state;
				newState(S_MATHCOMMENT_LINE, 1);
				return;
			default:
				this.tokenLength++;
				continue LOOP;
			}
		}
	}
	
	protected void searchVerbatimEnv() {
		LOOP: while (true) {
			switch (this.fScanner.read()) {
			case ICharacterScanner.EOF:
				this.last= LAST_OTHER;
				newState(-1);
				return;
			case '\r':
				this.tokenLength++;
				readChar('\n');
				this.last= LAST_NEWLINE;
				return;
			case '\n':
				this.tokenLength++;
				this.last= LAST_NEWLINE;
				return;
			case '\\':
				this.tokenLength++;
				if (readSeqConsuming(this.envEndPattern)) {
					this.last= LAST_OTHER;
					newState(S_DEFAULT);
					return;
				}
				continue LOOP;
			default:
				this.tokenLength++;
				continue LOOP;
			}
		}
	}
	
	protected void searchVerbatimLine(final int end, final int nextState) {
		LOOP: while (true) {
			final int c= this.fScanner.read();
			switch (c) {
			case ICharacterScanner.EOF:
				this.last= LAST_OTHER;
				newState(-1);
				return;
			case '\r':
				this.tokenLength++;
				if (readChar('\n')) {
					this.last= LAST_NEWLINE;
					newState(nextState, 2);
					return;
				}
				this.last= LAST_NEWLINE;
				newState(nextState, 1);
				return;
			case '\n':
				this.tokenLength++;
				this.last= LAST_NEWLINE;
				newState(nextState, 1);
				return;
			default:
				this.tokenLength++;
				if (c == end) {
					this.last= LAST_OTHER;
					if (this.state == S_VERBATIM_LINE) {
						newState(nextState);
					}
					else {
						newState(nextState, 1);
					}
					return;
				}
				continue LOOP;
			}
		}
	}
	
	protected void searchCommentLine() {
		LOOP: while (true) {
			switch (this.fScanner.read()) {
			case ICharacterScanner.EOF:
				this.last= LAST_OTHER;
				newState(-1);
				return;
			case '\r':
				this.tokenLength++;
				readChar('\n');
				this.last= LAST_NEWLINE;
				newState(S_DEFAULT);
				return;
			case '\n':
				this.tokenLength++;
				this.last= LAST_NEWLINE;
				newState(S_DEFAULT);
				return;
			default:
				this.tokenLength++;
				continue LOOP;
			}
		}
	}
	
	protected void searchMathCommentLine() {
		LOOP: while (true) {
			switch (this.fScanner.read()) {
			case ICharacterScanner.EOF:
				this.last= LAST_OTHER;
				newState(-1);
				return;
			case '\r':
				this.tokenLength++;
				readChar('\n');
				this.last= LAST_NEWLINE;
				newState(this.mathState);
				return;
			case '\n':
				this.tokenLength++;
				this.last= LAST_NEWLINE;
				newState(this.mathState);
				return;
			default:
				this.tokenLength++;
				continue LOOP;
			}
		}
	}
	
	protected void searchInternalEndEnv() {
		LOOP: while (true) {
			switch (this.fScanner.read()) {
			case ICharacterScanner.EOF:
				this.last= LAST_OTHER;
				newState(-1);
				return;
			case '\r':
				this.tokenLength++;
				readChar('\n');
				this.last= LAST_NEWLINE;
				return;
			case '\n':
				this.tokenLength++;
				this.last= LAST_NEWLINE;
				return;
			case ' ':
			case '\t':
				this.tokenLength++;
				continue LOOP;
			case '{':
				if (readSeqConsuming(this.envEndPattern)) {
					this.tokenLength++;
					readChar('}');
					this.last= LAST_OTHER;
					newState(S_DEFAULT);
					return;
				}
				//$FALL-THROUGH$
			default:
				this.fScanner.unread();
				this.last= LAST_OTHER;
				this.state= this.mathState;
				return;
			}
		}
	}
	
	protected final boolean readSeqConsuming(final char[] seq) {
		final int n= seq.length;
		for (int i= 0; i < n; i++) {
			final int c= this.fScanner.read();
			if (c != seq[i]) {
				this.tokenLength += i;
				if (c >= 0) {
					this.fScanner.unread();
				}
				return false;
			}
		}
		this.tokenLength += n;
		return true;
	}
	
	protected final boolean readSeq2Consuming(final char[] seq) {
		final int n= seq.length;
		for (int i= 1; i < n; i++) {
			final int c= this.fScanner.read();
			if (c != seq[i]) {
				this.tokenLength += i - 1;
				if (c >= 0) {
					this.fScanner.unread();
				}
				return false;
			}
		}
		this.tokenLength += n-1;
		return true;
	}
	
	protected final boolean readSeqTemp(final char[] seq) {
		for (int i= 0; i < seq.length; i++) {
			final int c= this.fScanner.read();
			if (c != seq[i]) {
				unread((c >= 0) ? (i+1) : (i));
				return false;
			}
		}
		return true;
	}
	
	protected final boolean readSeq2(final char[] seq) {
		for (int i= 1; i < seq.length; i++) {
			final int c= this.fScanner.read();
			if (c != seq[i]) {
				unread((c >= 0) ? i : (i-1));
				return false;
			}
		}
		this.tokenLength += seq.length-1;
		return true;
	}
	
	protected final boolean readChar(final char c1) {
		final int c= this.fScanner.read();
		if (c == c1) {
			this.tokenLength ++;
			return true;
		}
		if (c >= 0) {
			this.fScanner.unread();
		}
		return false;
	}
	
	protected final boolean readCharsConsuming(final char c1, final char c2) {
		int c= this.fScanner.read();
		if (c == c1) {
			c= this.fScanner.read();
			if (c == c2) {
				this.tokenLength += 2;
				return true;
			}
		}
		if (c >= 0) {
			this.fScanner.unread();
		}
		return false;
	}
	
	protected final boolean readCharsTemp(final char c1, final char c2) {
		int c= this.fScanner.read();
		if (c == c1) {
			c= this.fScanner.read();
			if (c == c2) {
				return true;
			}
			this.fScanner.unread();
		}
		if (c >= 0) {
			this.fScanner.unread();
		}
		return false;
	}
	
	protected final void readWhitespaceConsuming() {
		int readed= 0;
		while (true) {
			final int c= this.fScanner.read();
			if (c != ' ' && c != '\t') {
				if (c >= 0) {
					this.fScanner.unread();
				}
				this.tokenLength += readed;
				return;
			}
			readed++;
		}
	}
	
	protected final int readTempWhitespace() {
		int readed= 0;
		do {
			final int c= this.fScanner.read();
			if (c == ' ' || c == '\r' || c == '\n' || c == '\t') {
				readed++;
				continue;
			}
			if (c >= 0) {
				this.fScanner.unread();
			}
			return readed;
		} while (true);
	}
	
	private void unread(int count) {
		while (count-- > 0) {
			this.fScanner.unread();
		}
	}
	
	private void checkForBeginEnv() {
		int count= readTempWhitespace();
		
		count ++;
		int c= this.fScanner.read();
		if (c == '*') {
			count += readTempWhitespace();
			count ++;
			c= this.fScanner.read();
		}
		if (c != '{') {
			unread((c >= 0) ? count : (count-1));
			return;
		}
		
		final String name= this.envNameRule.searchString(this.fScanner);
		if (name == null) {
			unread(count);
			return;
		}
		count += name.length();
		
		count ++;
		c= this.fScanner.read();
		if (c != '}') {
			unread((c >= 0) ? count : (count-1));
			return;
		}
		
		unread(count);
		final EnvType envType= this.envStates.get(name);
		this.envEndPattern= envType.endPattern;
		newState(envType.state, 6); // \begin Note: we don't prefix all, because of new line handling for chunks
	}
	
	
	protected final int getState() {
		return this.state;
	}
	
	protected void searchExtState(final int state) {
		throw new IllegalStateException("state= " + state); //$NON-NLS-1$
	}
	
	protected final void newState(final int newState) {
		if (this.tokenLength > 0) {
			this.token= this.stateTokens[this.state];
			this.state= newState;
			this.prefixLength= 0;
			return;
		}
		this.state= newState;
		this.prefixLength= 0;
	}
	
	protected final void newState(final int newState, final int prefixLength) {
		if (this.tokenLength-prefixLength > 0) {
			this.token= this.stateTokens[this.state];
			this.state= newState;
			this.tokenLength -= prefixLength;
			this.prefixLength= prefixLength;
			return;
		}
		this.state= newState;
		this.tokenLength= prefixLength;
		this.prefixLength= 0;
	}
	
	protected final void forceReturn(final int prefixLength) {
		this.token= this.stateTokens[this.state];
		this.tokenLength -= prefixLength;
		this.prefixLength= prefixLength;
		return;
	}
	
	protected final void initState(String contentType, final int offset, final int prefixLength) throws BadLocationException, BadPartitioningException {
		int startOffset= this.tokenOffset;
		DEFAULT: if (contentType == null
				|| (contentType= contentType.intern()) == ITexDocumentConstants.LTX_DEFAULT_EXPL_CONTENT_TYPE
				|| contentType == IDocument.DEFAULT_CONTENT_TYPE) {
			int partitionOffset= this.tokenOffset;
			while (partitionOffset > 0) {
				final ITypedRegion partition= this.partitioner.getPartition(partitionOffset-1);
				final String partitionType= partition.getType();
				if (partitionType != ITexDocumentConstants.LTX_DEFAULT_CONTENT_TYPE
						&& partitionType != ITexDocumentConstants.LTX_DEFAULT_EXPL_CONTENT_TYPE
						&& partitionType != ITexDocumentConstants.LTX_MATH_CONTENT_TYPE
						&& partitionType != ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE
						&& partitionType != ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE
						&& partitionType != ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE) {
					partitionOffset= partition.getOffset();
					continue;
				}
				if (partitionOffset <= this.tokenOffset
						&& (partitionType == ITexDocumentConstants.LTX_MATH_CONTENT_TYPE
								|| partitionType == ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE )) {
					contentType= ITexDocumentConstants.LTX_MATH_CONTENT_TYPE;
					startOffset= partitionOffset;
					break DEFAULT;
				}
				break;
			}
			this.state= S_DEFAULT;
			prepareScan(offset, prefixLength);
			return;
		}
		if (contentType == ITexDocumentConstants.LTX_MATH_CONTENT_TYPE
				|| contentType == ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE ) {
			startOffset= LtxHeuristicTokenScanner.getSafeMathPartitionOffset(
					this.partitioner, startOffset );
			this.state= S_DEFAULT;
			this.tokenOffset= startOffset;
			prepareScan(startOffset, 0);
			return;
		}
		else if (contentType == ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE
				|| contentType == ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE ) {
			// we need the start pattern;
			if (prefixLength > 0) {
				this.state= S_DEFAULT;
				prepareScan(this.tokenOffset, 0);
				this.tokenLength= 1; // to skip parsing after detecting the start
				searchDefault();
				this.token= null;
				if (prefixLength > 100) {
					prepareScan(offset, prefixLength);
				}
				else {
//					fPrefixLength= fTokenLength - 1;
					this.tokenLength= 0;
				}
				return;
			}
			this.state= S_DEFAULT;
			prepareScan(offset, 0);
			return;
		}
		this.state= getExtState(contentType);
		prepareScan(offset, prefixLength);
	}
	
	protected final int getState(final String contentType) {
		if (contentType == null) {
			return S_DEFAULT;
		}
		if (contentType == ITexDocumentConstants.LTX_DEFAULT_EXPL_CONTENT_TYPE || contentType == IDocument.DEFAULT_CONTENT_TYPE) {
			return S_DEFAULT;
		}
		if (contentType == ITexDocumentConstants.LTX_MATH_CONTENT_TYPE) {
			return S_MATH_ENV;
		}
		if (contentType == ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE) {
			return S_VERBATIM_ENV;
		}
		if (contentType == ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE) {
			return S_COMMENT_ENV;
		}
		if (contentType == ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE) {
			return S_MATH_ENV;
		}
		return getExtState(contentType);
	}
	
	protected boolean searchExtCommand(final int c) {
		return false;
	}
	
	protected int getExtState(final String contentType) {
		return S_DEFAULT;
	}
	
}
