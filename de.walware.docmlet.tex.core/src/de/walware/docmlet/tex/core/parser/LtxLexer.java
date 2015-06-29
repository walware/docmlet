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

package de.walware.docmlet.tex.core.parser;

import de.walware.ecommons.string.IStringFactory;
import de.walware.ecommons.text.core.input.TextParserInput;


public class LtxLexer {
	
	
/*[ Types ]====================================================================*/
	
	public static final byte EOF=                           -1;
	
	protected static final byte NONE=                       0;
	
	public static final byte LINEBREAK=                     0x01;
	public static final byte WHITESPACE=                    0x02;
	
	public static final byte DEFAULT_TEXT=                  0x03;
	
	public static final byte CONTROL_NONE=                  0x04;
	public static final byte CONTROL_WORD=                  0x05;
	public static final byte CONTROL_CHAR=                  0x06;
	
	public static final byte ASTERISK=                      0x08;
	public static final byte CURLY_BRACKET_OPEN=            0x09;
	public static final byte CURLY_BRACKET_CLOSE=           0x0A;
	public static final byte SQUARED_BRACKET_OPEN=          0x0B;
	public static final byte SQUARED_BRACKET_CLOSE=         0x0C;
	
	public static final byte MATH_$=                        0x0E;
	public static final byte MATH_$$=                       0x0F;
	
	public static final byte LINE_COMMENT=                  0x10;
	
	public static final byte VERBATIM_TEXT=                 0x11;
	
	public static final byte EMBEDDED=                      0x12;
	
	
/*[ Flags ]====================================================================*/
	
	public static final byte SUB_OPEN_MISSING=              0x01;
	public static final byte SUB_CLOSE_MISSING=             0x02;
	
	
/*[ States ]===================================================================*/
	
	protected static final byte S_DEFAULT=                  0x00;
	
	protected static final byte S_VERBATIME_ENV=            0x01;
	
	protected static final byte S_VERBATIME_LINE=           0x02;
	
	protected static final byte S_EMBEDDED=                 0x03;
	
/*=============================================================================*/
	
	
	private TextParserInput input;
	
	private byte foundType;
	private int foundFlags;
	private int foundOffset;
	private int foundNum;
	private int foundLength;
	private String foundText;
	
	private boolean wasLinebreak;
	
	private byte state;
	private byte savedVerbatimState;
	private byte savedEmbeddedState;
	
	private char[] endPattern;
	
	private boolean reportSquaredBrackets= false;
	private boolean reportStars= false;
	private boolean report$$= true;
	
	
	public LtxLexer(final TextParserInput input) {
		this();
		
		reset(input);
	}
	
	public LtxLexer() {
	}
	
	
	public void reset() {
		this.foundType= NONE;
		this.foundOffset= this.input.getIndex();
		this.foundNum= 0;
		this.foundLength= 0;
		
		this.reportSquaredBrackets= false;
		this.reportStars= false;
		this.report$$= true;
	}
	
	public void reset(final TextParserInput input) {
		this.input= input;
		reset();
	}
	
	public final TextParserInput getInput() {
		return this.input;
	}
	
	
	public void setReportAsterisk(final boolean enable) {
		this.reportStars= enable;
	}
	
	public void setReportSquaredBrackets(final boolean enable) {
		this.reportSquaredBrackets= enable;
	}
	
	public void setReport$$(final boolean enable) {
		this.report$$= enable;
	}
	
	public void setModeVerbatimEnv(final char[] pattern) {
		this.state= S_VERBATIME_ENV;
		this.endPattern= pattern;
	}
	
	public void setModeVerbatimLine() {
		this.savedVerbatimState= this.state;
		this.state= S_VERBATIME_LINE;
	}
	
	public final byte pop() {
		return (this.foundType != NONE) ? this.foundType : next();
	}
	
	public final void consume() {
		this.foundType= NONE;
	}
	
	public final void consume(final boolean clear) {
		if (clear) {
			this.input.consume(this.foundNum);
			this.foundOffset= this.input.getIndex();
			this.foundNum= 0;
			this.foundLength= 0;
		}
		this.foundType= NONE;
	}
	
	public byte next() {
		this.foundType= NONE;
		SEARCH_NEXT: while (this.foundType == NONE) {
			this.input.consume(this.foundNum);
			this.foundOffset= this.input.getIndex();
			if (this.wasLinebreak) {
				this.wasLinebreak= false;
				handleNewLine(this.foundOffset, 0);
			}
			switch (this.state) {
			case S_DEFAULT:
				searchDefault();
				continue SEARCH_NEXT;
			case S_VERBATIME_ENV:
				searchVerbatimEnv();
				continue SEARCH_NEXT;
			case S_VERBATIME_LINE:
				searchVerbatimLine();
				continue SEARCH_NEXT;
			case S_EMBEDDED:
				searchEmbedded();
				continue SEARCH_NEXT;
			}
		}
		return this.foundType;
	}
	
	public final int getType() {
		return this.foundType;
	}
	
	public final int getFlags() {
		return this.foundFlags;
	}
	
	public final int getOffset() {
		return this.foundOffset;
	}
	
	public final int getLength() {
		return this.foundLength;
	}
	
	public final int getStopOffset() {
		return this.foundOffset + this.foundLength;
	}
	
	public final String getText() {
		switch (this.foundType) {
		case EOF:
			return null;
		case LINEBREAK:
			return "\n"; //$NON-NLS-1$
		case WHITESPACE:
			return " "; //$NON-NLS-1$
		case CONTROL_NONE:
			return null;
		case CONTROL_CHAR:
			return (this.wasLinebreak) ? "\n" : //$NON-NLS-1$
					this.input.getString(1, 1);
		case CONTROL_WORD:
			return this.input.getString(1, this.foundNum - 1);
		case EMBEDDED:
			return this.foundText;
		default:
			return null;
		}
	}
	
	public final String getText(final IStringFactory textFactory) {
		switch (this.foundType) {
		case EOF:
			return null;
		case LINEBREAK:
			return "\n"; //$NON-NLS-1$
		case WHITESPACE:
			return " "; //$NON-NLS-1$
		case CONTROL_NONE:
			return null;
		case CONTROL_CHAR:
			return (this.wasLinebreak) ? "\n" : //$NON-NLS-1$
				this.input.getString(1, 1, textFactory);
		case CONTROL_WORD:
			return this.input.getString(1, this.foundNum - 1, textFactory);
		case EMBEDDED:
			return this.foundText;
		default:
			return null;
		}
	}
	
	public final String getFullText(final IStringFactory factory) {
		return this.input.getString(0, this.foundNum, factory);
	}
	
	protected int getNum() {
		return this.foundNum;
	}
	
	
	private void foundEOF(final TextParserInput in) {
		this.foundType= EOF;
		this.foundFlags= 0;
		this.foundLength= in.getLengthInSource(this.foundNum= 0);
	}
	
	private void foundLineComment(final TextParserInput in, final int n) {
		this.foundType= LINE_COMMENT;
		this.foundFlags= 0;
		this.foundLength= in.getLengthInSource(this.foundNum= n);
	}
	
	private void foundLinebreak(final TextParserInput in, final int n) {
		this.foundType= LINEBREAK;
		this.foundFlags= 0;
		this.foundLength= in.getLengthInSource(this.foundNum= n);
		this.wasLinebreak= true;
	}
	
	private void foundWhitespace(final TextParserInput in, final int n) {
		this.foundType= WHITESPACE;
		this.foundFlags= 0;
		this.foundLength= in.getLengthInSource(this.foundNum= n);
	}
	
	private void foundControlLinebreak(final TextParserInput in, final int n) {
		this.foundType= CONTROL_CHAR;
		this.foundFlags= 0;
		this.foundLength= in.getLengthInSource(this.foundNum= n);
		this.wasLinebreak= true;
	}
	
	private void found1(final TextParserInput in, final byte type) {
		this.foundType= type;
		this.foundFlags= 0;
		this.foundLength= in.getLengthInSource(this.foundNum= 1);
	}
	
	private void found2(final TextParserInput in, final byte type) {
		this.foundType= type;
		this.foundFlags= 0;
		this.foundLength= in.getLengthInSource(this.foundNum= 2);
	}
	
	private void found(final TextParserInput in, final byte type, final int n) {
		this.foundType= type;
		this.foundFlags= 0;
		this.foundLength= in.getLengthInSource(this.foundNum= n);
	}
	
	private void foundVerbatimText(final TextParserInput in, final int n,
			final byte newState) {
		this.foundType= VERBATIM_TEXT;
		this.foundFlags= 0;
		this.foundLength= in.getLengthInSource(this.foundNum= n);
		this.state= newState;
	}
	
	private void foundVerbatimText(final TextParserInput in, final byte flags, final int n,
			final byte newState) {
		this.foundType= VERBATIM_TEXT;
		this.foundFlags= flags;
		this.foundLength= in.getLengthInSource(this.foundNum= n);
		this.state= newState;
	}
	
	
	protected void setEmbeddedBegin() {
		this.savedEmbeddedState= this.state;
		this.state= S_EMBEDDED;
	}
	
	protected void setEmbeddedEnd(final int n, final String text) {
		this.foundType= EMBEDDED;
		this.foundNum= n;
		this.foundLength= this.input.getLengthInSource(n);
		this.foundText= text;
		if (n > 0) {
			switch (this.input.get(n - 1)) {
			case '\r':
			case '\n':
				this.wasLinebreak= true;
			}
		}
		this.state= this.savedEmbeddedState;
	}
	
	
	protected final void searchDefault() {
		final TextParserInput in= this.input;
		int n;
		C0: switch (in.get(0)) {
		// eof
		case TextParserInput.EOF:
			foundEOF(in);
			return;
		// linebreak
		case '\r':
			if (in.get(1) == '\n') {
				foundLinebreak(in, 2);
				return;
			}
			foundLinebreak(in, 1);
			return;
		case '\n':
			foundLinebreak(in, 1);
			return;
		// whitespace
		case '\f':
		case ' ':
		case '\t':
			n= 1;
			ITER_CN: while (true) {
				switch (in.get(n++)) {
				case ' ':
				case '\t':
					continue ITER_CN;
				default:
					foundWhitespace(in, n - 1);
					return;
				}
			}
		
		case '\\':
			switch (in.get(1)) {
			case TextParserInput.EOF:
				found1(in, CONTROL_NONE);
				return;
			case '\r':
				if (in.get(2) == '\n') {
					foundControlLinebreak(in, 3);
					return;
				}
				//$FALL-THROUGH$
			case '\n':
				foundControlLinebreak(in, 2);
				return;
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
			case 'G':
			case 'H':
			case 'I':
			case 'J':
			case 'K':
			case 'L':
			case 'M':
			case 'N':
			case 'O':
			case 'P':
			case 'Q':
			case 'R':
			case 'S':
			case 'T':
			case 'U':
			case 'V':
			case 'W':
			case 'X':
			case 'Y':
			case 'Z':
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'g':
			case 'h':
			case 'i':
			case 'j':
			case 'k':
			case 'l':
			case 'm':
			case 'n':
			case 'o':
			case 'p':
			case 'q':
			case 'r':
			case 's':
			case 't':
			case 'u':
			case 'v':
			case 'w':
			case 'x':
			case 'y':
			case 'z':
				n= 2;
				ITER_CN: while (true) {
					switch (in.get(n++)) {
					case 'A':
					case 'B':
					case 'C':
					case 'D':
					case 'E':
					case 'F':
					case 'G':
					case 'H':
					case 'I':
					case 'J':
					case 'K':
					case 'L':
					case 'M':
					case 'N':
					case 'O':
					case 'P':
					case 'Q':
					case 'R':
					case 'S':
					case 'T':
					case 'U':
					case 'V':
					case 'W':
					case 'X':
					case 'Y':
					case 'Z':
					case 'a':
					case 'b':
					case 'c':
					case 'd':
					case 'e':
					case 'f':
					case 'g':
					case 'h':
					case 'i':
					case 'j':
					case 'k':
					case 'l':
					case 'm':
					case 'n':
					case 'o':
					case 'p':
					case 'q':
					case 'r':
					case 's':
					case 't':
					case 'u':
					case 'v':
					case 'w':
					case 'x':
					case 'y':
					case 'z':
						continue ITER_CN;
					default:
						found(in, CONTROL_WORD, n - 1);
						return;
					}
				}
			
			default:
				found2(in, CONTROL_CHAR);
				return;
			}
		
		// star
		case '*':
			if (this.reportStars) {
				found1(in, ASTERISK);
				return;
			}
			break C0;
		
		// brackets
		case '{':
			found1(in, CURLY_BRACKET_OPEN);
			return;
		case '}':
			found1(in, CURLY_BRACKET_CLOSE);
			return;
		
		case '[':
			if (this.reportSquaredBrackets) {
				found1(in, SQUARED_BRACKET_OPEN);
				return;
			}
			break C0;
		case ']':
			if (this.reportSquaredBrackets) {
				found1(in, SQUARED_BRACKET_CLOSE);
				return;
			}
			break C0;
		
		// math
		case '$':
			if (this.report$$ && in.get(1) == '$') {
				found2(in, MATH_$$);
				return;
			}
			found1(in, MATH_$);
			return;
			
		// line comment - in tex including linebreak
		case '%':
			n= 1;
			ITER_CN: while (true) {
				switch (in.get(n++)) {
				case TextParserInput.EOF:
					foundLineComment(in, n - 1);
					return;
				case '\r':
					if (in.get(n) == '\n') {
						n++;
					}
					//$FALL-THROUGH$
				case '\n':
					foundLineComment(in, n);
					this.wasLinebreak= true;
					return;
				default:
					continue ITER_CN;
				}
			}
			
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'G':
		case 'H':
		case 'I':
		case 'J':
		case 'K':
		case 'L':
		case 'M':
		case 'N':
		case 'O':
		case 'P':
		case 'Q':
		case 'R':
		case 'S':
		case 'T':
		case 'U':
		case 'V':
		case 'W':
		case 'X':
		case 'Y':
		case 'Z':
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'g':
		case 'h':
		case 'i':
		case 'j':
		case 'k':
		case 'l':
		case 'm':
		case 'n':
		case 'o':
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 't':
		case 'u':
		case 'v':
		case 'w':
		case 'x':
		case 'y':
		case 'z':
		default:
			break C0;
		}
		
		// consume text
		{	int tmp= n= 1;
			ITER_CN: while (true) {
				switch (in.get(tmp++)) {
				case TextParserInput.EOF:
				case '\r':
				case '\n':
				case '\f':
				case '\\':
				case '{':
				case '}':
				case '%':
				case '$':
					found(in, DEFAULT_TEXT, n);
					return;
				case '[':
				case ']':
					if (this.reportSquaredBrackets) {
						found(in, DEFAULT_TEXT, n);
						return;
					}
					continue ITER_CN;
				case ' ':
				case '\t':
					continue ITER_CN;
				default:
					n= tmp;
					continue ITER_CN;
				}
			}
		}
	}
	
	protected final void searchVerbatimEnv(	) {
		final TextParserInput in= this.input;
		int n= 1;
		ITER_CN: while (true) {
			switch (in.get(n++)) {
			case TextParserInput.EOF:
				foundVerbatimText(in, SUB_CLOSE_MISSING, n - 1, S_DEFAULT);
				return;
			case '\r':
				if (in.get(n) == '\n') {
					n++;
				}
				//$FALL-THROUGH$
			case '\n':
				this.foundLength= in.getLengthInSource(this.foundNum= n);
				handleNewLine(this.foundOffset + this.foundLength, n);
				if (this.state != S_VERBATIME_ENV) {
					foundVerbatimText(in, n, this.state);
					return;
				}
				continue ITER_CN;
			case '\\':
				if (in.matches(n, this.endPattern)) {
					foundVerbatimText(in, n - 1, S_DEFAULT);
					return;
				}
				continue ITER_CN;
			default:
				continue ITER_CN;
			}
		}
	}
	
	protected final void searchVerbatimLine() {
		final TextParserInput in= this.input;
		final int end= in.get(0);
		if (end < 0x20) {
			foundVerbatimText(in, SUB_OPEN_MISSING, 0, this.savedVerbatimState);
			return;
		}
		int n= 1;
		ITER_CN: while (true) {
			final int c= in.get(n++);
			switch (c) {
			case TextParserInput.EOF:
			case '\r':
			case '\n':
				foundVerbatimText(in, SUB_CLOSE_MISSING, n - 1, this.savedVerbatimState);
				return;
			default:
				if (c == end) {
					foundVerbatimText(in, n, this.savedVerbatimState);
					return;
				}
				continue ITER_CN;
			}
		}
	}
	
	protected void searchEmbedded() {
	}
	
	protected void handleNewLine(final int offset, final int n) {
	}
	
}
