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

package de.walware.docmlet.tex.core.commands;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.ibm.icu.text.Collator;
import com.ibm.icu.text.RuleBasedCollator;


public class TexCommand implements Comparable<TexCommand> {
	
	
	public static final int MASK_MAIN = 0xf;
	public static final int MASK_C2 = 0xff;
	public static final int MASK_C3 = 0xfff;
	
	public static final int GENERICENV = 0x1;
	public static final int C2_GENERICENV_BEGIN =           GENERICENV | 0x10;
	public static final int C2_GENERICENV_END =             GENERICENV | 0x20;
	
	public static final int ENV = 0x2;
	public static final int C2_ENV_MATH_BEGIN =             ENV | 0x10;
	public static final int C3_ENV_MATH_INLINE_BEGIN =      C2_ENV_MATH_BEGIN | 0x100;
	public static final int C3_ENV_MATH_SEPARATE_BEGIN =    C2_ENV_MATH_BEGIN | 0x200;
	public static final int C2_ENV_DOCUMENT_BEGIN =         ENV | 0x20;
	public static final int C2_ENV_VERBATIM_BEGIN =         ENV | 0x30;
	public static final int C2_ENV_COMMENT_BEGIN =          ENV | 0x40;
	public static final int C2_ENV_ELEMENT_BEGIN =          ENV | 0x70;
	public static final int C3_ENV_ELEMENT_QUOTE_BEGIN =    C2_ENV_ELEMENT_BEGIN | 0x100;
	public static final int C3_ENV_ELEMENT_LISTS_BEGIN =    C2_ENV_ELEMENT_BEGIN | 0x300;
	public static final int C3_ENV_ELEMENT_TABLES_BEGIN =   C2_ENV_ELEMENT_BEGIN | 0x400;
	public static final int C3_ENV_ELEMENT_IMAGES_BEGIN =   C2_ENV_ELEMENT_BEGIN | 0x500;
	public static final int C3_ENV_ELEMENT_FLOATS_BEGIN =   C2_ENV_ELEMENT_BEGIN | 0x700;
	public static final int C3_ENV_ELEMENT_ALIGN_BEGIN =    C2_ENV_ELEMENT_BEGIN | 0x800;
	public static final int C2_ENV_MATHCONTENT_BEGIN =      ENV | 0x80;
	public static final int C2_ENV_OTHER_BEGIN =            ENV | 0xf0;
	
	public static final int VERBATIM_INLINE = 0x3;
	
	public static final int PREAMBLE = 0x4;
	public static final int C2_PREAMBLE_DOCDEF =            PREAMBLE | 0x10;
	public static final int C2_PREAMBLE_PACKAGE =           PREAMBLE | 0x20;
	public static final int C2_PREAMBLE_CONTROLDEF =        PREAMBLE | 0x30;
	public static final int C3_PREAMBLE_CONTROLDEF_COMMAND = C2_PREAMBLE_CONTROLDEF | 0x100;
	public static final int C3_PREAMBLE_CONTROLDEF_ENV =    C2_PREAMBLE_CONTROLDEF | 0x200;
	public static final int C2_PREAMBLE_MISC =              PREAMBLE | 0x60;
	
	public static final int DOCUMENT = 0x5;
	public static final int C2_DOCUMENT_INCLUDE =           DOCUMENT | 0x10;
	public static final int C2_DOCUMENT_ELEMENT =           DOCUMENT | 0x20;
	public static final int C3_DOCUMENT_ELEMENT_LISTS =     C2_DOCUMENT_ELEMENT | 0x300;
	public static final int C3_DOCUMENT_ELEMENT_TABLES =    C2_DOCUMENT_ELEMENT | 0x400;
	public static final int C3_DOCUMENT_ELEMENT_IMAGES =    C2_DOCUMENT_ELEMENT | 0x500;
	public static final int C2_DOCUMENT_CONTENTLISTS =      DOCUMENT | 0x30;
	public static final int C3_DOCUMENT_CONTENTLISTS_DEF =  C2_DOCUMENT_CONTENTLISTS | 0x100;
	public static final int C3_DOCUMENT_CONTENTLISTS_GEN =  C2_DOCUMENT_CONTENTLISTS | 0x300;
	public static final int C2_DOCUMENT_INDEX =             DOCUMENT | 0x40;
	public static final int C3_DOCUMENT_INDEX_DEF =         C2_DOCUMENT_INDEX | 0x100;
	public static final int C3_DOCUMENT_INDEX_GEN =         C2_DOCUMENT_INDEX | 0x300;
	public static final int C2_DOCUMENT_LAYOUT =            DOCUMENT | 0x70;
	
	public static final int SECTIONING = 0x6;
	public static final int PART_LEVEL = 1;
	public static final int CHAPTER_LEVEL = 2;
	public static final int SECTION_LEVEL = 3;
	public static final int SUBSECTION_LEVEL = 4;
	public static final int SUBSUBSECTION_LEVEL = 5;
	public static final int C2_SECTIONING_PART =            SECTIONING | (PART_LEVEL << 4);
	public static final int C3_SECTIONING_PART_APPENDIX =   C2_SECTIONING_PART | 0x200;
	public static final int C2_SECTIONING_CHAPTER =         SECTIONING | (CHAPTER_LEVEL << 4);
	public static final int C2_SECTIONING_SECTION =         SECTIONING | (SECTION_LEVEL << 4);
	public static final int C2_SECTIONING_SUBSECTION =      SECTIONING | (SUBSECTION_LEVEL << 4);
	public static final int C2_SECTIONING_SUBSUBSECTION =   SECTIONING | (SUBSUBSECTION_LEVEL << 4);
	
	public static final int LABEL = 0x7;
	public static final int C2_LABEL_REFLABEL =             LABEL | 0x10;
	public static final int C3_LABEL_REFLABEL_DEF =         C2_LABEL_REFLABEL | 0x100;
	public static final int C3_LABEL_REFLABEL_REF =         C2_LABEL_REFLABEL | 0x700;
	public static final int C2_LABEL_COUNTER =              LABEL | 0x70;
	public static final int C3_LABEL_COUNTER_DEF =          C2_LABEL_COUNTER | 0x100;
	public static final int C3_LABEL_COUNTER_REF =          C2_LABEL_COUNTER | 0x700;
	
	public static final int STYLE = 0x8;
//	public static final int C2_STYLE_COMMON =               STYLE | 0x10;
	public static final int C2_STYLE_TEXT =                 STYLE | 0x30;
	public static final int C3_STYLE_TEXT_FONT_O =          C2_STYLE_TEXT | 0x100;
	public static final int C3_STYLE_TEXT_FONT_B =          C2_STYLE_TEXT | 0x200;
	public static final int C3_STYLE_TEXT_SIZE_O =          C2_STYLE_TEXT | 0x300;
	public static final int C2_STYLE_MATH =                 STYLE | 0x50;
	
	public static final int SYMBOL = 0xA;
	public static final int C2_SYMBOL_COMMON =              SYMBOL | 0x10;
	public static final int C2_SYMBOL_CHAR =                SYMBOL | 0x20;
	public static final int C2_SYMBOL_TEXT =                SYMBOL | 0x30;
	
	public static final int MATHSYMBOL = 0xB;
	public static final int C2_MATHSYMBOL_GREEK =           MATHSYMBOL | 0x10;
	public static final int C3_MATHSYMBOL_GREEK_UPPER =     C2_MATHSYMBOL_GREEK | 0x100;
	public static final int C3_MATHSYMBOL_GREEK_LOWER =     C2_MATHSYMBOL_GREEK | 0x200;
	public static final int C2_MATHSYMBOL_OP =              MATHSYMBOL | 0x50;
	public static final int C3_MATHSYMBOL_OP_BIN =          C2_MATHSYMBOL_OP | 0x100;
	public static final int C3_MATHSYMBOL_OP_ROOTFRAC =     C2_MATHSYMBOL_OP | 0x200;
	public static final int C3_MATHSYMBOL_OP_RELSTD =       C2_MATHSYMBOL_OP | 0x300;
	public static final int C3_MATHSYMBOL_OP_RELARROW =     C2_MATHSYMBOL_OP | 0x400;
	public static final int C3_MATHSYMBOL_OP_RELMISC =      C2_MATHSYMBOL_OP | 0x600;
	public static final int C3_MATHSYMBOL_OP_LARGE =        C2_MATHSYMBOL_OP | 0x700;
	public static final int C3_MATHSYMBOL_OP_NAMED =        C2_MATHSYMBOL_OP | 0xB00;
	public static final int C2_MATHSYMBOL_MISC =            MATHSYMBOL | 0x60;
	public static final int C3_MATHSYMBOL_MISC_ALPHA =      C2_MATHSYMBOL_MISC | 0x100;
	public static final int C3_MATHSYMBOL_MISC_ORD =        C2_MATHSYMBOL_MISC | 0x200;
	public static final int C3_MATHSYMBOL_DOTS =            C2_MATHSYMBOL_MISC | 0x600;
	public static final int C3_MATHSYMBOL_ACCENTS_ =        C2_MATHSYMBOL_MISC | 0x700;
	public static final int C2_MATHSYMBOL_BRACKETS =        MATHSYMBOL | 0x70;
	public static final int C3_MATHSYMBOL_BRACKETS_ =       C2_MATHSYMBOL_OP | 0x000;
	
	public static final int BIB = 0xD;
	public static final int C2_BIB_DEF =                    BIB | 0x10;
	public static final int C2_BIB_REF =                    BIB | 0x20;
	public static final int C2_BIB_INCLUDE =                BIB | 0x30;
	
	
	private static final List<Argument> NO_ARGUMENTS = Collections.emptyList();
	
	private static final Collator COLLATOR = Collator.getInstance(Locale.ENGLISH);
	static {
		((RuleBasedCollator) COLLATOR).setUpperCaseFirst(true);
	}
	
	
	private final int fType;
	private final String fWord;
	private final boolean fSupportAserisk;
	private final List<Argument> fArguments;
	
	private final String fDescription;
	
	
	public TexCommand(final int type,
			final String word, final boolean asterisk, final List<Argument> arguments,
			final String description) {
		fType = type;
		fWord = word;
		fSupportAserisk = asterisk;
		fArguments = arguments;
		fDescription = description;
	}
	
	public TexCommand(final int type, final String word, final String description) {
		fType = type;
		fWord = word;
		fSupportAserisk = false;
		fArguments = NO_ARGUMENTS;
		fDescription = description;
	}
	
	
	public int getType() {
		return fType;
	}
	
	public String getControlWord() {
		return fWord;
	}
	
	public boolean supportAsterisk() {
		return fSupportAserisk;
	}
	
	public List<Argument> getArguments() {
		return fArguments;
	}
	
	public String getDescription() {
		return fDescription;
	}
	
	
	@Override
	public int compareTo(final TexCommand other) {
		return COLLATOR.compare(fWord, other.fWord);
	}
	
	@Override
	public String toString() {
		return fWord;
	}
	
}
