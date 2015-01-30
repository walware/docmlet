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

import de.walware.ecommons.text.IStringCache;
import de.walware.ecommons.text.InternStringCache;
import de.walware.ecommons.text.SourceParseInput;


public class LtxLexer {
	
	
	//-- Types --//
	
	public static final int EOF=                            -1;
	
	protected static final int NONE=                        0;
	
	public static final int LINEBREAK=                      0x01;
	public static final int WHITESPACE=                     0x02;
	
	public static final int DEFAULT_TEXT=                   0x03;
	
	public static final int CONTROL_NONE=                   0x04;
	public static final int CONTROL_WORD=                   0x05;
	public static final int CONTROL_CHAR=                   0x06;
	
	public static final int ASTERISK=                       0x08;
	public static final int CURLY_BRACKET_OPEN=             0x09;
	public static final int CURLY_BRACKET_CLOSE=            0x0A;
	public static final int SQUARED_BRACKET_OPEN=           0x0B;
	public static final int SQUARED_BRACKET_CLOSE=          0x0C;
	
	public static final int MATH_$=                         0x0E;
	public static final int MATH_$$=                        0x0F;
	
	public static final int LINE_COMMENT=                   0x10;
	
	public static final int VERBATIM_TEXT=                  0x11;
	
	public static final int EMBEDDED=                       0x12;
	
	//-- Subtypes --//
	
	public static final int SUB_OPEN_MISSING=               0x01;
	public static final int SUB_CLOSE_MISSING=              0x02;
	
	
	//-- States --//
	
	protected static final int S_DEFAULT=                   0x00;
	
	protected static final int S_VERBATIME_ENV=             0x01;
	
	protected static final int S_VERBATIME_LINE=            0x02;
	
	protected static final int S_EMBEDDED=                  0x03;
	
	
	protected SourceParseInput input;
	
	private int foundType;
	private int foundSubtype;
	private int foundOffset;
	private int foundNum;
	private int foundLength;
	private String foundText;
	
	private boolean wasLinebreak;
	
	private int state;
	private int savedVerbatimState;
	private int savedEmbeddedState;
	
	private char[] endPattern;
	
	private boolean reportSquaredBrackets= false;
	private boolean reportStars= false;
	private boolean report$$= true;
	
	private boolean createControlTexts;
	private final IStringCache controlTextFactory;
	
	
	public LtxLexer(final SourceParseInput input) {
		this((IStringCache) null);
		setInput(input);
	}
	
	public LtxLexer() {
		this((IStringCache) null);
	}
	
	public LtxLexer(final IStringCache controlTextFactory) {
		this.controlTextFactory= (controlTextFactory != null) ? controlTextFactory : InternStringCache.INSTANCE;
	}
	
	
	private void reset() {
		this.foundOffset= this.input.getIndex();
		this.foundNum= 0;
		this.foundLength= 0;
		this.foundType= NONE;
		
		this.reportSquaredBrackets= false;
		this.reportStars= false;
		this.report$$= true;
	}
	
	public void setInput(final SourceParseInput input) {
		this.input= input;
	}
	
	public void setFull() {
		this.input.init();
		reset();
	}
	
	public void setRange(final int offset, final int length) {
		this.input.init(offset, offset+length);
		reset();
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
	
	public void setCreateControlTexts(final boolean enable) {
		this.createControlTexts= enable;
	}
	
	public void setModeVerbatimEnv(final char[] pattern) {
		this.state= S_VERBATIME_ENV;
		this.endPattern= pattern;
	}
	
	public void setModeVerbatimLine() {
		this.savedVerbatimState= this.state;
		this.state= S_VERBATIME_LINE;
	}
	
	public final int pop() {
		return (this.foundType != NONE) ? this.foundType : next();
	}
	
	public final void consume() {
		this.foundType= NONE;
	}
	
	public int next() {
		this.foundType= NONE;
		SEARCH_NEXT: while (this.foundType == NONE) {
			this.input.consume(this.foundNum, this.foundLength);
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
	
	public final int getSubtype() {
		return this.foundSubtype;
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
		return this.foundText;
	}
	
	public final String getFullText(final IStringCache factory) {
		return (factory != null) ?
				this.input.substring(1, this.foundNum, factory) :
				this.input.substring(1, this.foundNum);
	}
	
	protected int getNum() {
		return this.foundNum;
	}
	
	protected void setEmbeddedBegin() {
		this.savedEmbeddedState= this.state;
		this.state= S_EMBEDDED;
	}
	protected void setEmbeddedEnd(final int num, final String text) {
		this.foundType= EMBEDDED;
		this.foundNum= num;
		this.foundLength= this.input.getLength(num);
		this.foundText= text;
		switch (this.input.get(num)) {
		case '\r':
		case '\n':
			this.wasLinebreak= true;
		}
		this.state= this.savedEmbeddedState;
	}
	
	protected void searchDefault() {
		int num;
		switch (this.input.get(1)) {
		// eof
		case SourceParseInput.EOF:
			this.foundType= EOF;
			this.foundLength= this.input.getLength(this.foundNum= 0);
			this.foundText= null;
			return;
		// linebreak
		case '\r':
			if (this.input.get(2) == '\n') {
				this.foundType= LINEBREAK;
				this.foundLength= this.input.getLength(this.foundNum= 2);
				this.foundText= null;
				this.wasLinebreak= true;
				return;
			}
			//$FALL-THROUGH$
		case '\n':
			this.foundType= LINEBREAK;
			this.foundLength= this.input.getLength(this.foundNum= 1);
			this.foundText= null;
			this.wasLinebreak= true;
			return;
		// whitespace
		case '\f':
		case ' ':
		case '\t':
			num= 1;
			LOOP : while (true) {
				switch (this.input.get(++num)) {
				case ' ':
				case '\t':
					continue LOOP;
				default:
					this.foundType= WHITESPACE;
					this.foundLength= this.input.getLength(this.foundNum= num-1);
					this.foundText= null;
					return;
				}
			}
		
		case '\\':
			switch (this.foundSubtype= this.input.get(2)) {
			case SourceParseInput.EOF:
				this.foundType= CONTROL_NONE;
				this.foundLength= this.input.getLength(this.foundNum= 1);
				this.foundText= null;
				return;
			case '\r':
				if (this.input.get(3) == '\n') {
					this.foundType= CONTROL_CHAR;
					this.foundLength= this.input.getLength(this.foundNum= 3);
					this.foundText= "\n"; //$NON-NLS-1$
					this.wasLinebreak= true;
					return;
				}
				//$FALL-THROUGH$
			case '\n':
				this.foundType= CONTROL_CHAR;
				this.foundLength= this.input.getLength(this.foundNum= 2);
				this.foundText= "\n"; //$NON-NLS-1$
				this.wasLinebreak= true;
				return;
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
				num= 2;
				LOOP: while (true) {
					switch (this.input.get(++num)) {
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
						continue LOOP;
					default:
						this.foundType= CONTROL_WORD;
						this.foundLength= this.input.getLength(this.foundNum= num - 1);
						this.foundText= (this.createControlTexts) ? this.input.substring(2, num - 2, this.controlTextFactory) : null;
						return;
					}
				}
			
			default:
				this.foundType= CONTROL_CHAR;
				this.foundLength= this.input.getLength(this.foundNum= 2);
				this.foundText= (this.createControlTexts) ? this.input.substring(2, 1, this.controlTextFactory) : null;
				return;
			}
		
		// star
		case '*':
			if (this.reportStars) {
				this.foundType= ASTERISK;
				this.foundLength= this.input.getLength(this.foundNum= 1);
				this.foundText= null;
				return;
			}
			break;
		
		// brackets
		case '{':
			this.foundType= CURLY_BRACKET_OPEN;
			this.foundLength= this.input.getLength(this.foundNum= 1);
			this.foundText= null;
			return;
		case '}':
			this.foundType= CURLY_BRACKET_CLOSE;
			this.foundLength= this.input.getLength(this.foundNum= 1);
			this.foundText= null;
			return;
		
		case '[':
			if (this.reportSquaredBrackets) {
				this.foundType= SQUARED_BRACKET_OPEN;
				this.foundLength= this.input.getLength(this.foundNum= 1);
				this.foundText= null;
				return;
			}
			break;
		case ']':
			if (this.reportSquaredBrackets) {
				this.foundType= SQUARED_BRACKET_CLOSE;
				this.foundLength= this.input.getLength(this.foundNum= 1);
				this.foundText= null;
				return;
			}
			break;
		
		// math
		case '$':
			if (this.report$$ && this.input.get(2) == '$') {
				this.foundType= MATH_$$;
				this.foundLength= this.input.getLength(this.foundNum= 2);
				this.foundText= null;
				return;
			}
			this.foundType= MATH_$;
			this.foundLength= this.input.getLength(this.foundNum= 1);
			this.foundText= null;
			return;
			
		// line comment - in tex including linebreak
		case '%':
			num= 1;
			LOOP: while (true) {
				switch (this.input.get(++num)) {
				case SourceParseInput.EOF:
					this.foundType= LINE_COMMENT;
					this.foundLength= this.input.getLength(this.foundNum= num - 1);
					this.foundText= null;
					return;
				case '\r':
					if (this.input.get(num+1) == '\n') {
						num++;
					}
					//$FALL-THROUGH$
				case '\n':
					this.foundType= LINE_COMMENT;
					this.foundLength= this.input.getLength(this.foundNum= num);
					this.foundText= null;
					this.wasLinebreak= true;
					return;
				default:
					continue LOOP;
				}
			}
			
		default:
			break;
		}
		
		// consume text
		{	int tmp= num= 1;
			LOOP: while (true) {
				switch (this.input.get(++tmp)) {
				case SourceParseInput.EOF:
				case '\r':
				case '\n':
				case '\f':
				case '\\':
				case '{':
				case '}':
				case '%':
				case '$':
					this.foundType= DEFAULT_TEXT;
					this.foundLength= this.input.getLength(this.foundNum= num);
					this.foundText= null;
					return;
				case '[':
				case ']':
					if (this.reportSquaredBrackets) {
						this.foundType= DEFAULT_TEXT;
						this.foundLength= this.input.getLength(this.foundNum= num);
						this.foundText= null;
						return;
					}
					continue LOOP;
				case ' ':
				case '\t':
					continue LOOP;
				default:
					num= tmp;
					continue LOOP;
				}
			}
		}
	}
	
	protected void searchVerbatimEnv() {
		int num= 1;
		LOOP: while (true) {
			switch (this.input.get(++num)) {
			case SourceParseInput.EOF:
				this.foundType= VERBATIM_TEXT;
				this.foundSubtype= SUB_CLOSE_MISSING;
				this.foundLength= this.input.getLength(this.foundNum= num - 1);
				this.foundText= null;
				this.state= S_DEFAULT;
				return;
			case '\r':
				if (this.input.get(num+1) == '\n') {
					num++;
				}
				//$FALL-THROUGH$
			case '\n':
				this.foundLength= this.input.getLength(this.foundNum= num);
				handleNewLine(this.foundOffset + this.foundLength, num);
				if (this.state != S_VERBATIME_ENV) {
					this.foundType= VERBATIM_TEXT;
					this.foundSubtype= 0;
					this.foundLength= this.input.getLength(this.foundNum= num);
					this.foundText= null;
					return;
				}
				continue LOOP;
			case '\\':
				if (this.input.subequals(num+1, this.endPattern)) {
					this.foundType= VERBATIM_TEXT;
					this.foundSubtype= 0;
					this.foundLength= this.input.getLength(this.foundNum= num - 1);
					this.foundText= null;
					this.state= S_DEFAULT;
					return;
				}
				continue LOOP;
			default:
				continue LOOP;
			}
		}
	}
	
	protected void searchVerbatimLine() {
		final int end= this.input.get(1);
		if (end < 0x20) {
			this.foundType= VERBATIM_TEXT;
			this.foundSubtype= SUB_OPEN_MISSING;
			this.foundLength= this.foundNum= 0;
			this.foundText= null;
			this.state= this.savedVerbatimState;
			return;
		}
		int num= 1;
		LOOP: while (true) {
			final int c= this.input.get(++num);
			switch (c) {
			case SourceParseInput.EOF:
			case '\r':
			case '\n':
				this.foundType= VERBATIM_TEXT;
				this.foundSubtype= SUB_CLOSE_MISSING;
				this.foundLength= this.input.getLength(this.foundNum= num - 1);
				this.foundText= null;
//				this.foundText= (this.createControlTexts) ? this.input.substring(1, 1) : null;
				return;
			default:
				if (c == end) {
					this.foundType= VERBATIM_TEXT;
					this.foundSubtype= 0;
					this.foundLength= this.input.getLength(this.foundNum= num);
					this.foundText= null;
//					this.foundText= (this.createControlTexts) ? this.input.substring(1, 1) : null;
					this.state= this.savedVerbatimState;
					return;
				}
				continue LOOP;
			}
		}
	}
	
	protected void searchEmbedded() {
	}
	
	protected void handleNewLine(final int offset, final int num) {
	}
}
