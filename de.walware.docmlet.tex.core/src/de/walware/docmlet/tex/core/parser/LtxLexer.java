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

package de.walware.docmlet.tex.core.parser;

import de.walware.ecommons.text.IStringCache;
import de.walware.ecommons.text.InternStringCache;
import de.walware.ecommons.text.SourceParseInput;


public class LtxLexer {
	
	
	public static final int EOF = -1;
	
	protected static final int NONE = 0;
	
	public static final int LINEBREAK = 1;
	public static final int WHITESPACE = 2;
	
	public static final int CONTROL_NONE = 3;
	public static final int CONTROL_WORD = 4;
	public static final int CONTROL_CHAR = 5;
	
	public static final int ASTERISK = 9;
	public static final int CURLY_BRACKET_OPEN = 10;
	public static final int CURLY_BRACKET_CLOSE = 11;
	public static final int SQUARED_BRACKET_OPEN = 12;
	public static final int SQUARED_BRACKET_CLOSE = 13;
	
	public static final int DEFAULT_TEXT = 8;
	
	public static final int MATH_$ = 15;
	public static final int MATH_$$ = 16;
	
	public static final int LINE_COMMENT = 19;
	
	public static final int VERBATIM_TEXT = 20;
	
	public static final int EMBEDDED = 21;
	
	public static final int SUB_OPEN_MISSING = 1;
	public static final int SUB_CLOSE_MISSING = 2;
	
	
	protected static final int S_DEFAULT = 0;
	
	protected static final int S_VERBATIME_ENV = 1;
	
	protected static final int S_VERBATIME_LINE = 2;
	
	protected static final int S_EMBEDDED = 3;
	
	
	protected SourceParseInput fInput;
	
	private int fFoundType;
	private int fFoundSubtype;
	private int fFoundOffset;
	private int fFoundNum;
	private int fFoundLength;
	private String fFoundText;
	
	private boolean fWasLinebreak;
	
	private int fState;
	private int fSavedVerbatimState;
	private int fSavedEmbeddedState;
	
	private boolean fReportSquaredBrackets = false;
	private boolean fReportStars = false;
	private boolean fReport$$ = true;
	
	private char[] fEndPattern;
	
	private boolean fCreateControlTexts;
	private final IStringCache fControlTextFactory;
	
	
	public LtxLexer(final SourceParseInput input) {
		this((IStringCache) null);
		setInput(input);
	}
	
	public LtxLexer() {
		this((IStringCache) null);
	}
	
	public LtxLexer(final IStringCache controlTextFactory) {
		fControlTextFactory = (controlTextFactory != null) ? controlTextFactory : InternStringCache.INSTANCE;
	}
	
	
	private void reset() {
		fFoundOffset = fInput.getIndex();
		fFoundNum = 0;
		fFoundLength = 0;
		fFoundType = NONE;
		
		fReportSquaredBrackets = false;
		fReportStars = false;
		fReport$$ = true;
	}
	
	public void setInput(final SourceParseInput input) {
		fInput = input;
	}
	
	public void setFull() {
		fInput.init();
		reset();
	}
	
	public void setRange(final int offset, final int length) {
		fInput.init(offset, offset+length);
		reset();
	}
	
	public void setReportAsterisk(final boolean enable) {
		fReportStars = enable;
	}
	
	public void setReportSquaredBrackets(final boolean enable) {
		fReportSquaredBrackets = enable;
	}
	
	public void setReport$$(final boolean enable) {
		fReport$$ = enable;
	}
	
	public void setCreateControlTexts(final boolean enable) {
		fCreateControlTexts = enable;
	}
	
	public void setModeVerbatimEnv(final char[] pattern) {
		fState = S_VERBATIME_ENV;
		fEndPattern = pattern;
	}
	
	public void setModeVerbatimLine() {
		fSavedVerbatimState = fState;
		fState = S_VERBATIME_LINE;
	}
	
	public final int pop() {
		return (fFoundType != NONE) ? fFoundType : next();
	}
	
	public final void consume() {
		fFoundType = NONE;
	}
	
	public int next() {
		fFoundType = NONE;
		SEARCH_NEXT: while (fFoundType == NONE) {
			fInput.consume(fFoundNum, fFoundLength);
			fFoundOffset = fInput.getIndex();
			if (fWasLinebreak) {
				fWasLinebreak = false;
				handleNewLine(fFoundOffset, 0);
			}
			switch (fState) {
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
		return fFoundType;
	}
	
	public final int getType() {
		return fFoundType;
	}
	
	public final int getSubtype() {
		return fFoundSubtype;
	}
	
	public final int getOffset() {
		return fFoundOffset;
	}
	
	public final int getLength() {
		return fFoundLength;
	}
	
	public final int getStopOffset() {
		return fFoundOffset + fFoundLength;
	}
	
	public final String getText() {
		return fFoundText;
	}
	
	public final String getFullText(final IStringCache factory) {
		return (factory != null) ?
				fInput.substring(1, fFoundNum, factory) :
				fInput.substring(1, fFoundNum);
	}
	
	protected int getNum() {
		return fFoundNum;
	}
	
	protected void setEmbeddedBegin() {
		fSavedEmbeddedState = fState;
		fState = S_EMBEDDED;
	}
	protected void setEmbeddedEnd(final int num, final String text) {
		fFoundType = EMBEDDED;
		fFoundNum = num;
		fFoundLength = fInput.getLength(num);
		fFoundText = text;
		switch (fInput.get(num)) {
		case '\r':
		case '\n':
			fWasLinebreak = true;
		}
		fState = fSavedEmbeddedState;
	}
	
	protected void searchDefault() {
		int num;
		switch (fInput.get(1)) {
		// eof
		case SourceParseInput.EOF:
			fFoundType = EOF;
			fFoundLength = fInput.getLength(fFoundNum = 0);
			fFoundText = null;
			return;
		// linebreak
		case '\r':
			if (fInput.get(2) == '\n') {
				fFoundType = LINEBREAK;
				fFoundLength = fInput.getLength(fFoundNum = 2);
				fFoundText = null;
				fWasLinebreak = true;
				return;
			}
			//$FALL-THROUGH$
		case '\n':
			fFoundType = LINEBREAK;
			fFoundLength = fInput.getLength(fFoundNum = 1);
			fFoundText = null;
			fWasLinebreak = true;
			return;
		// whitespace
		case '\f':
		case ' ':
		case '\t':
			num = 1;
			LOOP : while (true) {
				switch (fInput.get(++num)) {
				case ' ':
				case '\t':
					continue LOOP;
				default:
					fFoundType = WHITESPACE;
					fFoundLength = fInput.getLength(fFoundNum = num-1);
					fFoundText = null;
					return;
				}
			}
		
		case '\\':
			switch (fFoundSubtype = fInput.get(2)) {
			case SourceParseInput.EOF:
				fFoundType = CONTROL_NONE;
				fFoundLength = fInput.getLength(fFoundNum = 1);
				fFoundText = null;
				return;
			case '\r':
				if (fInput.get(3) == '\n') {
					fFoundType = CONTROL_CHAR;
					fFoundLength = fInput.getLength(fFoundNum = 3);
					fFoundText = "\n";
					fWasLinebreak = true;
					return;
				}
				//$FALL-THROUGH$
			case '\n':
				fFoundType = CONTROL_CHAR;
				fFoundLength = fInput.getLength(fFoundNum = 2);
				fFoundText = "\n";
				fWasLinebreak = true;
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
				num = 2;
				LOOP: while (true) {
					switch (fInput.get(++num)) {
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
						fFoundType = CONTROL_WORD;
						fFoundLength = fInput.getLength(fFoundNum = num - 1);
						fFoundText = (fCreateControlTexts) ? fInput.substring(2, num - 2, fControlTextFactory) : null;
						return;
					}
				}
			
			default:
				fFoundType = CONTROL_CHAR;
				fFoundLength = fInput.getLength(fFoundNum = 2);
				fFoundText = (fCreateControlTexts) ? fInput.substring(2, 1, fControlTextFactory) : null;
				return;
			}
		
		// star
		case '*':
			if (fReportStars) {
				fFoundType = ASTERISK;
				fFoundLength = fInput.getLength(fFoundNum = 1);
				fFoundText = null;
				return;
			}
			break;
		
		// brackets
		case '{':
			fFoundType = CURLY_BRACKET_OPEN;
			fFoundLength = fInput.getLength(fFoundNum = 1);
			fFoundText = null;
			return;
		case '}':
			fFoundType = CURLY_BRACKET_CLOSE;
			fFoundLength = fInput.getLength(fFoundNum = 1);
			fFoundText = null;
			return;
		
		case '[':
			if (fReportSquaredBrackets) {
				fFoundType = SQUARED_BRACKET_OPEN;
				fFoundLength = fInput.getLength(fFoundNum = 1);
				fFoundText = null;
				return;
			}
			break;
		case ']':
			if (fReportSquaredBrackets) {
				fFoundType = SQUARED_BRACKET_CLOSE;
				fFoundLength = fInput.getLength(fFoundNum = 1);
				fFoundText = null;
				return;
			}
			break;
		
		// math
		case '$':
			if (fReport$$ && fInput.get(2) == '$') {
				fFoundType = MATH_$$;
				fFoundLength = fInput.getLength(fFoundNum = 2);
				fFoundText = null;
				return;
			}
			fFoundType = MATH_$;
			fFoundLength = fInput.getLength(fFoundNum = 1);
			fFoundText = null;
			return;
			
		// line comment - in tex including linebreak
		case '%':
			num = 1;
			LOOP: while (true) {
				switch (fInput.get(++num)) {
				case SourceParseInput.EOF:
					fFoundType = LINE_COMMENT;
					fFoundLength = fInput.getLength(fFoundNum = num - 1);
					fFoundText = null;
					return;
				case '\r':
					if (fInput.get(num+1) == '\n') {
						num++;
					}
					//$FALL-THROUGH$
				case '\n':
					fFoundType = LINE_COMMENT;
					fFoundLength = fInput.getLength(fFoundNum = num);
					fFoundText = null;
					fWasLinebreak = true;
					return;
				default:
					continue LOOP;
				}
			}
			
		default:
			break;
		}
		
		// consume text
		{	int tmp = num = 1;
			LOOP: while (true) {
				switch (fInput.get(++tmp)) {
				case SourceParseInput.EOF:
				case '\r':
				case '\n':
				case '\f':
				case '\\':
				case '{':
				case '}':
				case '%':
				case '$':
					fFoundType = DEFAULT_TEXT;
					fFoundLength = fInput.getLength(fFoundNum = num);
					fFoundText = null;
					return;
				case '[':
				case ']':
					if (fReportSquaredBrackets) {
						fFoundType = DEFAULT_TEXT;
						fFoundLength = fInput.getLength(fFoundNum = num);
						fFoundText = null;
						return;
					}
					continue LOOP;
				case ' ':
				case '\t':
					continue LOOP;
				default:
					num = tmp;
					continue LOOP;
				}
			}
		}
	}
	
	protected void searchVerbatimEnv() {
		int num = 1;
		LOOP: while (true) {
			switch (fInput.get(++num)) {
			case SourceParseInput.EOF:
				fFoundType = VERBATIM_TEXT;
				fFoundSubtype = SUB_CLOSE_MISSING;
				fFoundLength = fInput.getLength(fFoundNum = num - 1);
				fFoundText = null;
				fState = S_DEFAULT;
				return;
			case '\r':
				if (fInput.get(num+1) == '\n') {
					num++;
				}
				//$FALL-THROUGH$
			case '\n':
				fFoundLength = fInput.getLength(fFoundNum = num);
				handleNewLine(fFoundOffset + fFoundLength, num);
				if (fState != S_VERBATIME_ENV) {
					fFoundType = VERBATIM_TEXT;
					fFoundSubtype = 0;
					fFoundLength = fInput.getLength(fFoundNum = num);
					fFoundText = null;
					return;
				}
				continue LOOP;
			case '\\':
				if (fInput.subequals(num+1, fEndPattern)) {
					fFoundType = VERBATIM_TEXT;
					fFoundSubtype = 0;
					fFoundLength = fInput.getLength(fFoundNum = num - 1);
					fFoundText = null;
					fState = S_DEFAULT;
					return;
				}
				continue LOOP;
			default:
				continue LOOP;
			}
		}
	}
	
	protected void searchVerbatimLine() {
		final int end = fInput.get(1);
		if (end < 0x20) {
			fFoundType = VERBATIM_TEXT;
			fFoundSubtype = SUB_OPEN_MISSING;
			fFoundLength = fFoundNum = 0;
			fFoundText = null;
			fState = fSavedVerbatimState;
			return;
		}
		int num = 1;
		LOOP: while (true) {
			final int c = fInput.get(++num);
			switch (c) {
			case SourceParseInput.EOF:
			case '\r':
			case '\n':
				fFoundType = VERBATIM_TEXT;
				fFoundSubtype = SUB_CLOSE_MISSING;
				fFoundLength = fInput.getLength(fFoundNum = num - 1);
				fFoundText = null;
//				fFoundText = (fCreateControlTexts) ? fInput.substring(1, 1) : null;
				return;
			default:
				if (c == end) {
					fFoundType = VERBATIM_TEXT;
					fFoundSubtype = 0;
					fFoundLength = fInput.getLength(fFoundNum = num);
					fFoundText = null;
//					fFoundText = (fCreateControlTexts) ? fInput.substring(1, 1) : null;
					fState = fSavedVerbatimState;
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
