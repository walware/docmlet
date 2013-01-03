/*******************************************************************************
 * Copyright (c) 2009-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.ui.text;

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
import org.eclipse.ui.statushandlers.StatusManager;

import de.walware.ecommons.collections.IntArrayMap;
import de.walware.ecommons.collections.IntMap;
import de.walware.ecommons.text.IPartitionScannerCallbackExt;
import de.walware.ecommons.text.IPartitionScannerConfigExt;
import de.walware.ecommons.text.Partitioner;
import de.walware.ecommons.text.ui.BufferedDocumentScanner;
import de.walware.ecommons.text.ui.OperatorRule;

import de.walware.docmlet.tex.core.text.ITexDocumentConstants;
import de.walware.docmlet.tex.core.text.LtxHeuristicTokenScanner;
import de.walware.docmlet.tex.internal.ui.TexUIPlugin;


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
	protected static final int S_DEFAULT = 0;
	protected static final int S_MATH_SPECIAL_$ = 1;
	protected static final int S_MATH_SPECIAL_S = 2;
	protected static final int S_MATH_SPECIAL_P = 3;
	protected static final int S_MATH_ENV = 4;
	protected static final int S_VERBATIM_LINE = 5;
	protected static final int S_VERBATIM_ENV = 6;
	protected static final int S_COMMENT_LINE = 7;
	protected static final int S_COMMENT_ENV = 8;
	protected static final int S_MATHCOMMENT_LINE = 9;
	protected static final int S_INTERNAL_ENDENV = 10;
	
	protected final static IToken T_DEFAULT = new Token(ITexDocumentConstants.LTX_DEFAULT_EXPL_CONTENT_TYPE);
	protected final static IToken T_MATH = new Token(ITexDocumentConstants.LTX_MATH_CONTENT_TYPE);
	protected final static IToken T_VERBATIM = new Token(ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE);
	protected final static IToken T_COMMENT = new Token(ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE);
	protected final static IToken T_MATHCOMMENT = new Token(ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE);
	
	
	/** Enum of last significant characters read. */
	protected static final int LAST_OTHER = 0;
	protected static final int LAST_BACKSLASH = 1;
	protected static final int LAST_NEWLINE = 2;
	
	private static final char[] SEQ_begin = "begin".toCharArray();
	private static final char[] SEQ_end = "end".toCharArray();
	private static final char[] SEQ_verb = "verb".toCharArray();
	private static final char[] SEQ_$ = "$".toCharArray();
	private static final char[] SEQ_$$ = "$$".toCharArray();
	private static final char[] SEQ_$$$$ = "$$$$".toCharArray();
	private static final char[] SEQ_dummy = "\u0000\u0000\u0000".toCharArray();
	
	
	private static class EnvType {
		final String name;
		final char[] endPattern;
		final int state;
		
		public EnvType(final String envName, final int state) {
			this.name = envName;
			switch (state) {
			case S_VERBATIM_ENV:
			case S_COMMENT_ENV:
				this.endPattern = ("end{"+envName+"}").toCharArray();
				break;
			default:
				this.endPattern = envName.toCharArray();
				break;
			}
			this.state = state;
		}
	}
	
	
	/** The scanner. */
	private final BufferedDocumentScanner fScanner = new BufferedDocumentScanner(1000);	// faster implementation
	private final boolean fTemplateMode;
	
	private IDocument fDocument;
	
	private IToken fToken;
	/** The offset of the last returned token. */
	private int fTokenOffset;
	/** The length of the last returned token. */
	private int fTokenLength;
	
	private int fStartPartitionState = S_DEFAULT;
	/** The current state of the scanner. */
	private int fState;
	/** The last significant characters read. */
	protected int fLast;
	/** The amount of characters already read on first call to nextToken(). */
	private int fPrefixLength;
	
	private int fMathState;
	
	private int fRangeStart;
	private int fRangeEnd;
	
	private final IToken[] fStateTokens;
	
	private final OperatorRule fEnvNameRule;
	private final Map<String, EnvType> fEnvStates;
	private char[] fEnvEndPattern;
	
	private char fVerbEndPattern;
	
	private Partitioner fPartitioner;
	
	
	public LtxFastPartitionScanner(final String partitioning) {
		this(partitioning, false);
	}
	
	/**
	 * 
	 * @param templateMode enabled mode for Eclipse template syntax with $ as prefix,
	 * so dollar must be doubled for math modes.
	 */
	public LtxFastPartitionScanner(final String partitioning, final boolean templateMode) {
		fTemplateMode = templateMode;
		fEnvNameRule = new OperatorRule(new char[] {});
		fEnvStates = new HashMap<String, EnvType>();
		
		final IntArrayMap<IToken> list = new IntArrayMap<IToken>();
		initTokens(list);
		final int count = list.getMaxKey()+1;
		fStateTokens = new IToken[count];
		for (int i = 0; i < count; i++) {
			fStateTokens[i] = list.get(i);
		}
	}
	
	@Override
	public void setPartitionerCallback(final Partitioner partitioner) {
		fPartitioner = partitioner;
	}
	
	
	protected void addEnvRule(final String name, final int state) {
		fEnvStates.put(name, new EnvType(name, state));
		fEnvNameRule.addOp(name, null);
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
		
		addEnvRule("comment", S_COMMENT_ENV);
		
		addEnvRule("verbatim", S_VERBATIM_ENV);
		addEnvRule("verbatim*", S_VERBATIM_ENV);
		addEnvRule("lstlisting", S_VERBATIM_ENV);
		addEnvRule("Sinput", S_VERBATIM_ENV);
		addEnvRule("Soutput", S_VERBATIM_ENV);
		
		addEnvRule("equation", S_MATH_ENV);
		addEnvRule("eqnarray", S_MATH_ENV);
		addEnvRule("eqnarray*", S_MATH_ENV);
		addEnvRule("math", S_MATH_ENV);
		addEnvRule("displaymath", S_MATH_ENV);
		//AMSMath environments
		addEnvRule("multline", S_MATH_ENV);
		addEnvRule("multline*", S_MATH_ENV);
		addEnvRule("gather", S_MATH_ENV);
		addEnvRule("gather*", S_MATH_ENV);
		addEnvRule("align", S_MATH_ENV);
		addEnvRule("align*", S_MATH_ENV);
		addEnvRule("alignat", S_MATH_ENV);
		addEnvRule("alignat*", S_MATH_ENV);
		addEnvRule("flalign", S_MATH_ENV);
		addEnvRule("flalign*", S_MATH_ENV);
	}
	
	/**
	 * Sets explicitly the partition type on position 0.
	 * 
	 * @param contentType
	 */
	@Override
	public void setStartPartitionType(final String contentType) {
		fStartPartitionState = getState(contentType);
	}
	
	@Override
	public void setRange(final IDocument document, final int offset, final int length) {
		setPartialRange(document, offset, length, null, -1);
	}
	
	@Override
	public void setPartialRange(final IDocument document, final int offset, final int length, final String contentType, int partitionOffset) {
		if (partitionOffset < 0) {
			partitionOffset = offset;
		}
		fDocument = document;
		fRangeStart = fTokenOffset = partitionOffset;
		fRangeEnd = offset+length;
		
		if (offset > 0) {
			try {
				initState(contentType, offset, offset-partitionOffset);
				return;
			}
			catch (final Exception e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, TexUIPlugin.PLUGIN_ID,
						"An error occured when detecting initial state for the tex partition scanner.", e));
				fState = S_DEFAULT;
				prepareScan(partitionOffset, 0);
			}
			finally {
				fPartitioner.resetCache();
			}
		}
		else {
			prepareScan(0, 0);
			fState = fStartPartitionState;
			fEnvEndPattern = SEQ_dummy;
		}
	}
	
	private void prepareScan(final int offset, final int prefixLength) {
		fTokenLength = 0;
		if (offset > 0) {
			fLast = LAST_OTHER;
			try {
				final char c = fDocument.getChar(offset-1);
				switch (c) {
				case '\r':
				case '\n':
					fLast = LAST_NEWLINE;
					break;
				}
			}
			catch (final BadLocationException e) {
			}
		}
		else {
			fLast = LAST_NEWLINE;
		}
		fPrefixLength = prefixLength;
		fScanner.setRange(fDocument, offset, fRangeEnd-offset);
	}
	
	
	@Override
	public int getTokenLength() {
		return fTokenLength;
	}
	
	@Override
	public int getTokenOffset() {
		return fTokenOffset;
	}
	
	
	@Override
	public IToken nextToken() {
		while (true) {
			fToken = null;
			fTokenOffset += fTokenLength;
			fTokenLength = fPrefixLength;
			
			do {
				// characters
				switch (fState) {
				case -1:
					fToken = Token.EOF;
					fTokenLength = 0;
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
					searchVerbatimLine(fVerbEndPattern, S_DEFAULT);
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
					searchExtState(fState);
					break;
				}
			} while (fToken == null);
			
			if ((fTokenLength > 0) ?
					(fTokenOffset + fTokenLength <= fRangeStart) :
					(fTokenOffset < fRangeStart) ) {
				continue;
			}
//			if (fTokenOffset < fRangeStart) {
//				fTokenLength -= fRangeStart - fTokenOffset;
//				fTokenOffset = fRangeStart;
//			}
//			try {
//				System.out.println("Range [" + fRangeStart + "," + fRangeEnd + "]");
//				System.out.println("Token " + fToken.getData() + " " + fTokenOffset + "," + fTokenLength);
//				System.out.println("Doc " + fDocument.get(fTokenOffset, fTokenLength));
//				System.out.println("Next " + fDocument.get(fTokenOffset + fTokenLength, Math.min(100, fDocument.getLength() - fTokenOffset - fTokenLength)));
//			}
//			catch (Exception e) {}
			return fToken;
		}
	}
	
	protected void searchDefault() {
		LOOP: while (true) {
			int c;
			switch (fScanner.read()) {
			case ICharacterScanner.EOF:
				fLast = LAST_OTHER;
				newState(-1);
				return;
			case '\r':
				fTokenLength++;
				readChar('\n');
				fLast = LAST_NEWLINE;
				return;
			case '\n':
				fTokenLength++;
				fLast = LAST_NEWLINE;
				return;
			case '\\':
				fTokenLength++;
				switch (c = fScanner.read()) {
				case ICharacterScanner.EOF:
					fLast = LAST_OTHER;
					newState(-1);
					return;
				case '\r':
					fTokenLength++;
					readChar('\n');
					fLast = LAST_NEWLINE;
					return;
				case '\n':
					fTokenLength++;
					fLast = LAST_NEWLINE;
					return;
				case 'b':
					fTokenLength++;
					if (readSeq2(SEQ_begin)) {
						checkForBeginEnv();
						return;
					}
					break;
				case 'v':
					fTokenLength++;
					if (readSeq2(SEQ_verb)) {
						final int c6 = fScanner.read();
						if (c6 <= 32 | Character.isLetter(c6)) {
							if (c6 >= 0) {
								fScanner.unread();
							}
							return;
						}
						fTokenLength++;
						fVerbEndPattern = (char) c6;
						newState(S_VERBATIM_LINE, 6); // \verb
						return;
					}
					break;
				case '[':
					fTokenLength++;
					newState(S_MATH_SPECIAL_S, 2);
					return;
				case '(':
					fTokenLength++;
					newState(S_MATH_SPECIAL_P, 2);
					return;
				default:
					fTokenLength++;
					break;
				}
				if (searchExtCommand(c)) {
					return;
				}
				continue LOOP;
			case '%':
				fTokenLength++;
				fLast = LAST_OTHER;
				newState(S_COMMENT_LINE, 1);
				return;
			case '$':
				fTokenLength++;
				fLast = LAST_OTHER;
				if (readChar('$')) {
					if (fTemplateMode && readChar('$')) {
						if (readChar('$')) {
							fEnvEndPattern = SEQ_$$$$;
							newState(S_MATH_SPECIAL_$, 4);
							return;
						}
						fEnvEndPattern = SEQ_$$;
						newState(S_MATH_SPECIAL_$, 3);
						return;
					}
					fEnvEndPattern = SEQ_$$;
					newState(S_MATH_SPECIAL_$, 2);
					return;
				}
				if (!fTemplateMode) {
					fEnvEndPattern = SEQ_$;
					newState(S_MATH_SPECIAL_$, 1);
					return;
				}
				continue LOOP;
			default:
				fTokenLength++;
				continue LOOP;
			}
		}
	}
	
	protected void searchMathSpecial() {
		LOOP: while (true) {
			int c;
			switch (fScanner.read()) {
			case ICharacterScanner.EOF:
				fLast = LAST_OTHER;
				newState(-1);
				return;
			case '\r':
				fTokenLength++;
				readChar('\n');
				fLast = LAST_NEWLINE;
				return;
			case '\n':
				fTokenLength++;
				fLast = LAST_NEWLINE;
				return;
			case '\\':
				fTokenLength++;
				switch (c = fScanner.read()) {
				case ICharacterScanner.EOF:
					fLast = LAST_OTHER;
					newState(-1);
					return;
				case '\r':
					fTokenLength++;
					readChar('\n');
					fLast = LAST_NEWLINE;
					return;
				case '\n':
					fTokenLength++;
					fLast = LAST_NEWLINE;
					return;
				case ')':
					fTokenLength++;
					if (fState == S_MATH_SPECIAL_P) {
						fLast = LAST_OTHER;
						newState(S_DEFAULT);
						return;
					}
					continue LOOP;
				case ']':
					fTokenLength++;
					if (fState == S_MATH_SPECIAL_S) {
						fLast = LAST_OTHER;
						newState(S_DEFAULT);
						return;
					}
					continue LOOP;
				default:
					fTokenLength++;
					break;
				}
				if (searchExtCommand(c)) {
					return;
				}
				continue LOOP;
			case '%':
				fTokenLength++;
				fLast = LAST_OTHER;
				fMathState = fState;
				newState(S_MATHCOMMENT_LINE, 1);
				return;
			case '$':
				fTokenLength++;
				if (fState == S_MATH_SPECIAL_$ && readSeq2Consuming(fEnvEndPattern)) {
					fLast = LAST_OTHER;
					newState(S_DEFAULT);
					return;
				}
				continue LOOP;
			default:
				fTokenLength++;
				continue LOOP;
			}
		}
	}
	
	protected void searchMathEnv() {
		LOOP: while (true) {
			int c;
			switch (fScanner.read()) {
			case ICharacterScanner.EOF:
				fLast = LAST_OTHER;
				newState(-1);
				return;
			case '\r':
				fTokenLength++;
				readChar('\n');
				fLast = LAST_NEWLINE;
				return;
			case '\n':
				fTokenLength++;
				fLast = LAST_NEWLINE;
				return;
			case '\\':
				fTokenLength++;
				switch (c = fScanner.read()) {
				case ICharacterScanner.EOF:
					fLast = LAST_OTHER;
					newState(-1);
					return;
				case '\r':
					fTokenLength++;
					readChar('\n');
					fLast = LAST_NEWLINE;
					return;
				case '\n':
					fTokenLength++;
					fLast = LAST_NEWLINE;
					return;
				case 'e':
					fTokenLength++;
					if (readCharsConsuming('n', 'd')) {
						c = fScanner.read();
						if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
							fTokenLength++;
							continue LOOP;
						}
						if (c == ICharacterScanner.EOF) {
							fLast = LAST_OTHER;
							newState(-1);
							return;
						}
						fScanner.unread();
						fStateTokens[S_INTERNAL_ENDENV] = fStateTokens[fState];
						fMathState = fState;
						fState = S_INTERNAL_ENDENV;
						searchInternalEndEnv();
						return;
					}
					break;
				default:
					fTokenLength++;
					break;
				}
				if (searchExtCommand(c)) {
					return;
				}
				continue LOOP;
			case '%':
				fTokenLength++;
				fLast = LAST_OTHER;
				fMathState = fState;
				newState(S_MATHCOMMENT_LINE, 1);
				return;
			default:
				fTokenLength++;
				continue LOOP;
			}
		}
	}
	
	protected void searchVerbatimEnv() {
		LOOP: while (true) {
			switch (fScanner.read()) {
			case ICharacterScanner.EOF:
				fLast = LAST_OTHER;
				newState(-1);
				return;
			case '\r':
				fTokenLength++;
				readChar('\n');
				fLast = LAST_NEWLINE;
				return;
			case '\n':
				fTokenLength++;
				fLast = LAST_NEWLINE;
				return;
			case '\\':
				fTokenLength++;
				if (readSeqConsuming(fEnvEndPattern)) {
					fLast = LAST_OTHER;
					newState(S_DEFAULT);
					return;
				}
				continue LOOP;
			default:
				fTokenLength++;
				continue LOOP;
			}
		}
	}
	
	protected void searchVerbatimLine(final int end, final int nextState) {
		LOOP: while (true) {
			final int c = fScanner.read();
			switch (c) {
			case ICharacterScanner.EOF:
				fLast = LAST_OTHER;
				newState(-1);
				return;
			case '\r':
				fTokenLength++;
				if (readChar('\n')) {
					fLast = LAST_NEWLINE;
					newState(nextState, 2);
					return;
				}
				fLast = LAST_NEWLINE;
				newState(nextState, 1);
				return;
			case '\n':
				fTokenLength++;
				fLast = LAST_NEWLINE;
				newState(nextState, 1);
				return;
			default:
				fTokenLength++;
				if (c == end) {
					fLast = LAST_OTHER;
					if (fState == S_VERBATIM_LINE) {
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
			switch (fScanner.read()) {
			case ICharacterScanner.EOF:
				fLast = LAST_OTHER;
				newState(-1);
				return;
			case '\r':
				fTokenLength++;
				readChar('\n');
				fLast = LAST_NEWLINE;
				newState(S_DEFAULT);
				return;
			case '\n':
				fTokenLength++;
				fLast = LAST_NEWLINE;
				newState(S_DEFAULT);
				return;
			default:
				fTokenLength++;
				continue LOOP;
			}
		}
	}
	
	protected void searchMathCommentLine() {
		LOOP: while (true) {
			switch (fScanner.read()) {
			case ICharacterScanner.EOF:
				fLast = LAST_OTHER;
				newState(-1);
				return;
			case '\r':
				fTokenLength++;
				readChar('\n');
				fLast = LAST_NEWLINE;
				newState(fMathState);
				return;
			case '\n':
				fTokenLength++;
				fLast = LAST_NEWLINE;
				newState(fMathState);
				return;
			default:
				fTokenLength++;
				continue LOOP;
			}
		}
	}
	
	protected void searchInternalEndEnv() {
		LOOP: while (true) {
			switch (fScanner.read()) {
			case ICharacterScanner.EOF:
				fLast = LAST_OTHER;
				newState(-1);
				return;
			case '\r':
				fTokenLength++;
				readChar('\n');
				fLast = LAST_NEWLINE;
				return;
			case '\n':
				fTokenLength++;
				fLast = LAST_NEWLINE;
				return;
			case ' ':
			case '\t':
				fTokenLength++;
				continue LOOP;
			case '{':
				if (readSeqConsuming(fEnvEndPattern)) {
					fTokenLength++;
					readChar('}');
					fLast = LAST_OTHER;
					newState(S_DEFAULT);
					return;
				}
				//$FALL-THROUGH$
			default:
				fScanner.unread();
				fLast = LAST_OTHER;
				fState = fMathState;
				return;
			}
		}
	}
	
	protected final boolean readSeqConsuming(final char[] seq) {
		final int n = seq.length;
		for (int i = 0; i < n; i++) {
			final int c = fScanner.read();
			if (c != seq[i]) {
				fTokenLength += i;
				if (c >= 0) {
					fScanner.unread();
				}
				return false;
			}
		}
		fTokenLength += n;
		return true;
	}
	
	protected final boolean readSeq2Consuming(final char[] seq) {
		final int n = seq.length;
		for (int i = 1; i < n; i++) {
			final int c = fScanner.read();
			if (c != seq[i]) {
				fTokenLength += i - 1;
				if (c >= 0) {
					fScanner.unread();
				}
				return false;
			}
		}
		fTokenLength += n-1;
		return true;
	}
	
	protected final boolean readSeqTemp(final char[] seq) {
		for (int i = 0; i < seq.length; i++) {
			final int c = fScanner.read();
			if (c != seq[i]) {
				unread((c >= 0) ? (i+1) : (i));
				return false;
			}
		}
		return true;
	}
	
	protected final boolean readSeq2(final char[] seq) {
		for (int i = 1; i < seq.length; i++) {
			final int c = fScanner.read();
			if (c != seq[i]) {
				unread((c >= 0) ? i : (i-1));
				return false;
			}
		}
		fTokenLength += seq.length-1;
		return true;
	}
	
	protected final boolean readChar(final char c1) {
		final int c = fScanner.read();
		if (c == c1) {
			fTokenLength ++;
			return true;
		}
		if (c >= 0) {
			fScanner.unread();
		}
		return false;
	}
	
	protected final boolean readCharsConsuming(final char c1, final char c2) {
		int c = fScanner.read();
		if (c == c1) {
			c = fScanner.read();
			if (c == c2) {
				fTokenLength += 2;
				return true;
			}
		}
		if (c >= 0) {
			fScanner.unread();
		}
		return false;
	}
	
	protected final boolean readCharsTemp(final char c1, final char c2) {
		int c = fScanner.read();
		if (c == c1) {
			c = fScanner.read();
			if (c == c2) {
				return true;
			}
			fScanner.unread();
		}
		if (c >= 0) {
			fScanner.unread();
		}
		return false;
	}
	
	protected final void readWhitespaceConsuming() {
		int readed = 0;
		while (true) {
			final int c = fScanner.read();
			if (c != ' ' && c != '\t') {
				if (c >= 0) {
					fScanner.unread();
				}
				fTokenLength += readed;
				return;
			}
			readed++;
		}
	}
	
	protected final int readTempWhitespace() {
		int readed = 0;
		do {
			final int c = fScanner.read();
			if (c == ' ' || c == '\r' || c == '\n' || c == '\t') {
				readed++;
				continue;
			}
			if (c >= 0) {
				fScanner.unread();
			}
			return readed;
		} while (true);
	}
	
	private void unread(int count) {
		while (count-- > 0) {
			fScanner.unread();
		}
	}
	
	private void checkForBeginEnv() {
		int count = readTempWhitespace();
		
		count ++;
		int c = fScanner.read();
		if (c == '*') {
			count += readTempWhitespace();
			count ++;
			c = fScanner.read();
		}
		if (c != '{') {
			unread((c >= 0) ? count : (count-1));
			return;
		}
		
		final String name = fEnvNameRule.searchString(fScanner);
		if (name == null) {
			unread(count);
			return;
		}
		count += name.length();
		
		count ++;
		c = fScanner.read();
		if (c != '}') {
			unread((c >= 0) ? count : (count-1));
			return;
		}
		
		unread(count);
		final EnvType envType = fEnvStates.get(name);
		fEnvEndPattern = envType.endPattern;
		newState(envType.state, 6); // \begin Note: we don't prefix all, because of new line handling for chunks
	}
	
	
	protected final int getState() {
		return fState;
	}
	
	protected void searchExtState(final int state) {
		throw new IllegalStateException(""+state);
	}
	
	protected final void newState(final int newState) {
		if (fTokenLength > 0) {
			fToken = fStateTokens[fState];
			fState = newState;
			fPrefixLength = 0;
			return;
		}
		fState = newState;
		fPrefixLength = 0;
	}
	
	protected final void newState(final int newState, final int prefixLength) {
		if (fTokenLength-prefixLength > 0) {
			fToken = fStateTokens[fState];
			fState = newState;
			fTokenLength -= prefixLength;
			fPrefixLength = prefixLength;
			return;
		}
		fState = newState;
		fTokenLength = prefixLength;
		fPrefixLength = 0;
	}
	
	protected final void forceReturn(final int prefixLength) {
		fToken = fStateTokens[fState];
		fTokenLength -= prefixLength;
		fPrefixLength = prefixLength;
		return;
	}
	
	protected final void initState(String contentType, final int offset, final int prefixLength) throws BadLocationException, BadPartitioningException {
		int startOffset = fTokenOffset;
		DEFAULT: if (contentType == null
				|| (contentType = contentType.intern()) == ITexDocumentConstants.LTX_DEFAULT_EXPL_CONTENT_TYPE
				|| contentType == IDocument.DEFAULT_CONTENT_TYPE) {
			int partitionOffset = fTokenOffset;
			while (partitionOffset > 0) {
				final ITypedRegion partition = fPartitioner.getPartition(partitionOffset-1);
				final String partitionType = partition.getType();
				if (partitionType != ITexDocumentConstants.LTX_DEFAULT_CONTENT_TYPE
						&& partitionType != ITexDocumentConstants.LTX_DEFAULT_EXPL_CONTENT_TYPE
						&& partitionType != ITexDocumentConstants.LTX_MATH_CONTENT_TYPE
						&& partitionType != ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE
						&& partitionType != ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE
						&& partitionType != ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE) {
					partitionOffset = partition.getOffset();
					continue;
				}
				if (partitionOffset <= fTokenOffset
						&& (partitionType == ITexDocumentConstants.LTX_MATH_CONTENT_TYPE
								|| partitionType == ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE )) {
					contentType = ITexDocumentConstants.LTX_MATH_CONTENT_TYPE;
					startOffset = partitionOffset;
					break DEFAULT;
				}
				break;
			}
			fState = S_DEFAULT;
			prepareScan(offset, prefixLength);
			return;
		}
		if (contentType == ITexDocumentConstants.LTX_MATH_CONTENT_TYPE
				|| contentType == ITexDocumentConstants.LTX_MATHCOMMENT_CONTENT_TYPE ) {
			startOffset = LtxHeuristicTokenScanner.getSafeMathPartitionOffset(
					fPartitioner, startOffset );
			fState = S_DEFAULT;
			fTokenOffset = startOffset;
			prepareScan(startOffset, 0);
			return;
		}
		else if (contentType == ITexDocumentConstants.LTX_VERBATIM_CONTENT_TYPE
				|| contentType == ITexDocumentConstants.LTX_COMMENT_CONTENT_TYPE ) {
			// we need the start pattern;
			if (prefixLength > 0) {
				fState = S_DEFAULT;
				prepareScan(fTokenOffset, 0);
				fTokenLength = 1; // to skip parsing after detecting the start
				searchDefault();
				fToken = null;
				if (prefixLength > 100) {
					prepareScan(offset, prefixLength);
				}
				else {
//					fPrefixLength = fTokenLength - 1;
					fTokenLength = 0;
				}
				return;
			}
			fState = S_DEFAULT;
			prepareScan(offset, 0);
			return;
		}
		fState = getExtState(contentType);
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
	
	protected boolean searchExtCommand(int c) {
		return false;
	}
	
	protected int getExtState(final String contentType) {
		return S_DEFAULT;
	}
	
}
