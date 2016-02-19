/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ICharacterScanner;

import de.walware.ecommons.text.CharacterScannerReader;
import de.walware.ecommons.text.core.rules.BufferedDocumentScanner;
import de.walware.ecommons.text.core.rules.OperatorRule;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNode;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNodeScan;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNodeScanner;
import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNodeType;


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
public class LtxPartitionNodeScanner implements ITreePartitionNodeScanner {
	
	
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
	
	protected static final int S_EXT_LTX= 10;
	
	/** Enum of last significant characters read. */
	protected static final int LAST_OTHER= 0;
	protected static final int LAST_EOF= 1;
	protected static final int LAST_NEWLINE= 2;
	
	private static final char[] SEQ_begin= "begin".toCharArray(); //$NON-NLS-1$
	private static final char[] SEQ_verb= "verb".toCharArray(); //$NON-NLS-1$
	
	
	protected final boolean templateMode;
	
	protected final CharacterScannerReader reader= new CharacterScannerReader(
			new BufferedDocumentScanner(1024) );
	
	private ITreePartitionNodeScan scan;
	
	/** The current node */
	private ITreePartitionNode node;
	/** The current node type */
	private LtxPartitionNodeType type;
	/** The last significant characters read. */
	protected int last;
	
	private boolean searchInternalEnvEnd;
	
	private final OperatorRule envNameRule;
	private final Map<String, LtxPartitionNodeType.AbstractEnv> envTypes;
	
	
	public LtxPartitionNodeScanner() {
		this(false);
	}
	
	/**
	 * 
	 * @param templateMode enabled mode for Eclipse template syntax with $ as prefix,
	 * so dollar must be doubled for math modes.
	 */
	public LtxPartitionNodeScanner(final boolean templateMode) {
		this.templateMode= templateMode;
		this.envNameRule= new OperatorRule(new char[] {});
		this.envTypes= new HashMap<>(24);
		
		initEnvs();
	}
	
	
	protected void addEnvRule(final LtxPartitionNodeType.AbstractEnv type) {
		this.envTypes.put(type.getEnvName(), type);
		this.envNameRule.addOp(type.getEnvName(), null);
	}
	
	protected void initEnvs() {
		addEnvRule(LtxPartitionNodeType.COMMENT_ENV_comment);
		
		addEnvRule(LtxPartitionNodeType.VERBATIM_ENV_verbatim);
		addEnvRule(LtxPartitionNodeType.VERBATIM_ENV_verbatimA);
		addEnvRule(LtxPartitionNodeType.VERBATIM_ENV_lstlisting);
		addEnvRule(LtxPartitionNodeType.VERBATIM_ENV_Sinput);
		addEnvRule(LtxPartitionNodeType.VERBATIM_ENV_Soutput);
		
		addEnvRule(LtxPartitionNodeType.MATH_ENV_equation);
		addEnvRule(LtxPartitionNodeType.MATH_ENV_eqnarray);
		addEnvRule(LtxPartitionNodeType.MATH_ENV_eqnarrayA);
		addEnvRule(LtxPartitionNodeType.MATH_ENV_math);
		addEnvRule(LtxPartitionNodeType.MATH_ENV_displaymath);
		addEnvRule(LtxPartitionNodeType.MATH_ENV_multline);
		addEnvRule(LtxPartitionNodeType.MATH_ENV_multlineA);
		addEnvRule(LtxPartitionNodeType.MATH_ENV_gather);
		addEnvRule(LtxPartitionNodeType.MATH_ENV_gatherA);
		addEnvRule(LtxPartitionNodeType.MATH_ENV_align);
		addEnvRule(LtxPartitionNodeType.MATH_ENV_alignA);
		addEnvRule(LtxPartitionNodeType.MATH_ENV_alignat);
		addEnvRule(LtxPartitionNodeType.MATH_ENV_alignatA);
		addEnvRule(LtxPartitionNodeType.MATH_ENV_flalign);
		addEnvRule(LtxPartitionNodeType.MATH_ENV_flalignA);
	}
	
	
	@Override
	public int getRestartOffset(ITreePartitionNode node, final IDocument document,
			final int offset) throws BadLocationException {
		do {
			if (node.getType() instanceof LtxPartitionNodeType.MathEnv) {
				return node.getOffset();
			}
			node= node.getParent();
		}
		while (node != null);
		
		return offset;
	}
	
	@Override
	public LtxPartitionNodeType getRootType() {
		return LtxPartitionNodeType.DEFAULT_ROOT;
	}
	
	@Override
	public void execute(final ITreePartitionNodeScan scan) {
		this.scan= scan;
		
		setRange(scan.getBeginOffset(), scan.getEndOffset());
		
		this.node= null;
		this.searchInternalEnvEnd= false;
		
		init();
		
		process();
	}
	
	protected ITreePartitionNodeScan getScan() {
		return this.scan;
	}
	
	protected void setRange(final int beginOffset, final int endOffset) {
		this.reader.setRange(getScan().getDocument(), beginOffset, endOffset - beginOffset);
		updateLast();
	}
	
	protected void init() {
		final ITreePartitionNode beginNode= getScan().getBeginNode();
		if (beginNode.getType() instanceof LtxPartitionNodeType) {
			this.node= beginNode;
			this.type= (LtxPartitionNodeType) beginNode.getType();
		}
		else {
			this.node= beginNode;
			addNode(getRootType(), getScan().getBeginOffset());
		}
	}
	
	private void updateLast() {
		if (this.reader.getOffset() > 0) {
			this.last= LAST_OTHER;
			try {
				final char c= getScan().getDocument().getChar(this.reader.getOffset() - 1);
				switch (c) {
				case '\r':
				case '\n':
					this.last= LAST_NEWLINE;
					break;
				default:
					break;
				}
			}
			catch (final BadLocationException e) {}
		}
		else {
			this.last= LAST_NEWLINE;
		}
	}
	
	
	protected final void initNode(final ITreePartitionNode node, final LtxPartitionNodeType type) {
		if (this.node != null) {
			throw new IllegalStateException();
		}
		this.node= node;
		this.type= type;
	}
	
	protected final void addNode(final LtxPartitionNodeType type, final int offset) {
		this.node= this.scan.add(type, this.node, offset);
		this.type= type;
	}
	
	protected final void addNode(final ITreePartitionNodeType type, final LtxPartitionNodeType ltxType,
			final int offset) {
		this.node= this.scan.add(type, this.node, offset);
		this.type= ltxType;
	}
	
	protected final ITreePartitionNode getNode() {
		return this.node;
	}
	
	protected final void exitNode(final int offset) {
		this.scan.expand(this.node, offset, true);
		this.node= this.node.getParent();
		this.type= (LtxPartitionNodeType) this.node.getType();
	}
	
	protected final void exitNode() {
		this.node= this.node.getParent();
		this.type= (LtxPartitionNodeType) this.node.getType();
	}
	
	
	private void process() {
		while (true) {
			switch (this.last) {
			case LAST_EOF:
				handleEOF(this.type);
				this.scan.expand(this.node, this.reader.getOffset(), true);
				return;
			case LAST_NEWLINE:
				handleNewLine(this.type);
				break;
			default:
				break;
			}
			
			switch (this.type.getScannerState()) {
			case S_DEFAULT:
				processDefault();
				continue;
			case S_MATH_SPECIAL_$:
				processMathSpecial_$();
				continue;
			case S_MATH_SPECIAL_S:
				processMathSpecial_S();
				continue;
			case S_MATH_SPECIAL_P:
				processMathSpecial_P();
				continue;
			case S_MATH_ENV:
				processMathEnv();
				continue;
			case S_VERBATIM_LINE:
				processVerbatimLine();
				continue;
			case S_COMMENT_LINE:
			case S_MATHCOMMENT_LINE:
				processCommentLine();
				continue;
			case S_VERBATIM_ENV:
			case S_COMMENT_ENV:
				processVerbatimEnv();
				continue;
			default:
				processExt(this.type);
				continue;
			}
		}
	}
	
	protected void processDefault() {
		LOOP: while (true) {
			int c;
			switch (this.reader.read()) {
			case ICharacterScanner.EOF:
				this.last= LAST_EOF;
				return;
			case '\r':
				this.reader.read('\n');
				this.last= LAST_NEWLINE;
				return;
			case '\n':
				this.last= LAST_NEWLINE;
				return;
			case '\\':
				switch (c= this.reader.read()) {
				case ICharacterScanner.EOF:
					this.last= LAST_EOF;
					return;
				case '\r':
					this.reader.read('\n');
					this.last= LAST_NEWLINE;
					return;
				case '\n':
					this.last= LAST_NEWLINE;
					return;
				case 'b':
					if (this.reader.read2(SEQ_begin)) {
						checkForBeginEnv();
						return;
					}
					break;
				case 'v':
					if (this.reader.read2(SEQ_verb)) {
						final int c6= this.reader.read();
						if (c6 > 32 && Character.isLetter(c6)) {
							addNode(new LtxPartitionNodeType.VerbatimInline((char) c6), this.reader.getOffset() - 6);
							return;
						}
						if (c6 >= 0) {
							this.reader.unread();
						}
					}
					break;
				case '[':
					addNode(LtxPartitionNodeType.MATH_SPECIAL_S, this.reader.getOffset() - 2);
					return;
				case '(':
					addNode(LtxPartitionNodeType.MATH_SPECIAL_P, this.reader.getOffset() - 2);
					return;
				default:
					break;
				}
				if (searchExtCommand(c)) {
					return;
				}
				continue LOOP;
			case '%':
				addNode(LtxPartitionNodeType.COMMENT_LINE, this.reader.getOffset() - 1);
				this.last= LAST_OTHER;
				return;
			case '$':
				this.last= LAST_OTHER;
				if (this.templateMode) {
					if (this.reader.read('$')) {
						if (this.reader.read('$', '$')) {
							addNode(LtxPartitionNodeType.MATH_SPECIAL_$$_TEMPL, this.reader.getOffset() - 4);
							return;
						}
						addNode(LtxPartitionNodeType.MATH_SPECIAL_$_TEMPL, this.reader.getOffset() - 2);
						return;
					}
				}
				else {
					if (this.reader.read('$')) {
						addNode(LtxPartitionNodeType.MATH_SPECIAL_$$, this.reader.getOffset() - 2);
						return;
					}
					addNode(LtxPartitionNodeType.MATH_SPECIAL_$, this.reader.getOffset() - 1);
					return;
				}
				continue LOOP;
			default:
				continue LOOP;
			}
		}
	}
	
	protected void processMathSpecial_P() {
		LOOP: while (true) {
			int c;
			switch (this.reader.read()) {
			case ICharacterScanner.EOF:
				this.last= LAST_EOF;
				return;
			case '\r':
				this.reader.read('\n');
				this.last= LAST_NEWLINE;
				return;
			case '\n':
				this.last= LAST_NEWLINE;
				return;
			case '\\':
				switch (c= this.reader.read()) {
				case ICharacterScanner.EOF:
					this.last= LAST_EOF;
					return;
				case '\r':
					this.reader.read('\n');
					this.last= LAST_NEWLINE;
					return;
				case '\n':
					this.last= LAST_NEWLINE;
					return;
				case ')':
					this.last= LAST_OTHER;
					exitNode(this.reader.getOffset());
					return;
				default:
					break;
				}
				if (searchExtCommand(c)) {
					return;
				}
				continue LOOP;
			case '%':
				addNode(LtxPartitionNodeType.MATHCOMMENT_LINE, this.reader.getOffset() - 1);
				this.last= LAST_OTHER;
				return;
			default:
				continue LOOP;
			}
		}
	}
	
	protected void processMathSpecial_S() {
		LOOP: while (true) {
			int c;
			switch (this.reader.read()) {
			case ICharacterScanner.EOF:
				this.last= LAST_EOF;
				return;
			case '\r':
				this.reader.read('\n');
				this.last= LAST_NEWLINE;
				return;
			case '\n':
				this.last= LAST_NEWLINE;
				return;
			case '\\':
				switch (c= this.reader.read()) {
				case ICharacterScanner.EOF:
					this.last= LAST_EOF;
					return;
				case '\r':
					this.reader.read('\n');
					this.last= LAST_NEWLINE;
					return;
				case '\n':
					this.last= LAST_NEWLINE;
					return;
				case ']':
					exitNode(this.reader.getOffset());
					this.last= LAST_OTHER;
					return;
				default:
					break;
				}
				if (searchExtCommand(c)) {
					return;
				}
				continue LOOP;
			case '%':
				addNode(LtxPartitionNodeType.MATHCOMMENT_LINE, this.reader.getOffset() - 1);
				this.last= LAST_OTHER;
				return;
			default:
				continue LOOP;
			}
		}
	}
	
	protected void processMathSpecial_$() {
		LOOP: while (true) {
			final int c;
			switch (this.reader.read()) {
			case ICharacterScanner.EOF:
				this.last= LAST_EOF;
				return;
			case '\r':
				this.reader.read('\n');
				this.last= LAST_NEWLINE;
				return;
			case '\n':
				this.last= LAST_NEWLINE;
				return;
			case '\\':
				switch (c= this.reader.read()) {
				case ICharacterScanner.EOF:
					this.last= LAST_EOF;
					return;
				case '\r':
					this.reader.read('\n');
					this.last= LAST_NEWLINE;
					return;
				case '\n':
					this.last= LAST_NEWLINE;
					return;
				default:
					break;
				}
				if (searchExtCommand(c)) {
					return;
				}
				continue LOOP;
			case '%':
				addNode(LtxPartitionNodeType.MATHCOMMENT_LINE, this.reader.getOffset() - 1);
				this.last= LAST_OTHER;
				return;
			case '$':
				if (this.reader.readConsuming2(this.type.getEndPattern())) {
					exitNode(this.reader.getOffset());
					this.last= LAST_OTHER;
					return;
				}
				continue LOOP;
			default:
				continue LOOP;
			}
		}
	}
	
	protected void processMathEnv() {
		if (this.searchInternalEnvEnd) {
			searchInternalEnvEnd();
			return;
		}
		
		LOOP: while (true) {
			int c;
			switch (this.reader.read()) {
			case ICharacterScanner.EOF:
				this.last= LAST_EOF;
				return;
			case '\r':
				this.reader.read('\n');
				this.last= LAST_NEWLINE;
				return;
			case '\n':
				this.last= LAST_NEWLINE;
				return;
			case '\\':
				switch (c= this.reader.read()) {
				case ICharacterScanner.EOF:
					this.last= LAST_EOF;
					return;
				case '\r':
					this.reader.read('\n');
					this.last= LAST_NEWLINE;
					return;
				case '\n':
					this.last= LAST_NEWLINE;
					return;
				case 'e':
					if (this.reader.readConsuming('n', 'd')) {
						this.searchInternalEnvEnd= true;
						searchInternalEnvEnd();
						return;
					}
					break;
				default:
					break;
				}
				if (searchExtCommand(c)) {
					return;
				}
				continue LOOP;
			case '%':
				addNode(LtxPartitionNodeType.MATHCOMMENT_LINE, this.reader.getOffset() - 1);
				this.last= LAST_OTHER;
				return;
			default:
				continue LOOP;
			}
		}
	}
	
	protected void processVerbatimEnv() {
		LOOP: while (true) {
			switch (this.reader.read()) {
			case ICharacterScanner.EOF:
				this.last= LAST_EOF;
				return;
			case '\r':
				this.reader.read('\n');
				this.last= LAST_NEWLINE;
				return;
			case '\n':
				this.last= LAST_NEWLINE;
				return;
			case '\\':
				if (this.reader.readConsuming(this.type.getEndPattern())) {
					exitNode(this.reader.getOffset());
					this.last= LAST_OTHER;
					return;
				}
				continue LOOP;
			default:
				continue LOOP;
			}
		}
	}
	
	protected void processVerbatimLine() {
		final char end= this.type.getEndChar();
		LOOP: while (true) {
			int c;
			switch (c= this.reader.read()) {
			case ICharacterScanner.EOF:
				exitNode(this.reader.getOffset()); // required for rweave
				this.last= LAST_EOF;
				return;
			case '\r':
				exitNode(this.reader.getOffset() - 1);
				this.reader.read('\n');
				this.last= LAST_NEWLINE;
				return;
			case '\n':
				exitNode(this.reader.getOffset() - 1);
				this.last= LAST_NEWLINE;
				return;
			default:
				if (c == end) {
					exitNode(this.reader.getOffset());
					this.last= LAST_OTHER;
					return;
				}
				continue LOOP;
			}
		}
	}
	
	protected void processCommentLine() {
		LOOP: while (true) {
			switch (this.reader.read()) {
			case ICharacterScanner.EOF:
				this.last= LAST_EOF;
				return;
			case '\r':
				this.reader.read('\n');
				this.last= LAST_NEWLINE;
				exitNode(this.reader.getOffset());
				return;
			case '\n':
				this.last= LAST_NEWLINE;
				exitNode(this.reader.getOffset());
				return;
			default:
				continue LOOP;
			}
		}
	}
	
	protected void searchInternalEnvEnd() {
		LOOP: while (true) {
			switch (this.reader.read()) {
			case ICharacterScanner.EOF:
				this.last= LAST_EOF;
				return;
			case '\r':
				this.reader.read('\n');
				this.last= LAST_NEWLINE;
				return;
			case '\n':
				this.last= LAST_NEWLINE;
				return;
			case ' ':
			case '\t':
				continue LOOP;
			case '{':
				if (this.reader.readConsuming(this.type.getEndPattern())) {
					this.reader.read('}');
					exitNode(this.reader.getOffset());
					this.last= LAST_OTHER;
					return;
				}
				this.searchInternalEnvEnd= false;
				this.last= LAST_OTHER;
				return;
			default:
				this.reader.unread();
				this.searchInternalEnvEnd= false;
				this.last= LAST_OTHER;
				return;
			}
		}
	}
	
	protected void processExt(final LtxPartitionNodeType type) {
		throw new IllegalStateException("type= " + type); //$NON-NLS-1$
	}
	
	
	private void checkForBeginEnv() {
		final BufferedDocumentScanner scanner= this.reader.getScanner();
		int count= readWhitespace(scanner);
		
		count++;
		int c= scanner.read();
		if (c == '*') {
			count+= readWhitespace(scanner);
			count++;
			c= scanner.read();
		}
		if (c != '{') {
			this.reader.unreadRaw((c >= 0) ? count : (count - 1));
			return;
		}
		
		final String name= this.envNameRule.searchString(scanner);
		if (name == null) {
			this.reader.unreadRaw(count);
			return;
		}
		count+= name.length();
		
		count++;
		c= scanner.read();
		if (c != '}') {
			this.reader.unreadRaw((c >= 0) ? count : (count - 1));
			return;
		}
		
		// Note: we don't prefix all, because of new line handling for chunks
		this.reader.unreadRaw(count);
		addNode(this.envTypes.get(name), this.reader.getOffset() - 6);
	}
	
	public final int readWhitespace(final BufferedDocumentScanner scanner) {
		int readed= 0;
		do {
			final int c= scanner.read();
			if (c == ' ' || c == '\r' || c == '\n' || c == '\t') {
				readed++;
				continue;
			}
			if (c >= 0) {
				scanner.unread();
			}
			return readed;
		} while (true);
	}
	
	
	protected boolean searchExtCommand(final int c) {
		return false;
	}
	
	protected void handleNewLine(final LtxPartitionNodeType type) {
	}
	
	protected void handleEOF(final LtxPartitionNodeType type) {
	}
	
}
