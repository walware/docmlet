/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.commands;

import static de.walware.docmlet.tex.core.commands.TexCommand.C2_SYMBOL_CHAR;
import static de.walware.docmlet.tex.core.commands.TexCommand.C2_SYMBOL_COMMON;
import static de.walware.docmlet.tex.core.commands.TexCommand.C2_SYMBOL_TEXT;


public interface IDivSymbolDefinitions {
	
	
	TexCommand COMMONSYMBOL_CURLYOPEN_COMMAND= new LtxPrintCommand(C2_SYMBOL_CHAR,
			"{", "Prints left Curly Bracket", "{");
	TexCommand COMMONSYMBOL_CURLYCLOSE_COMMAND= new LtxPrintCommand(C2_SYMBOL_CHAR,
			"}", "Prints right Curly Bracket", "}");
	TexCommand COMMONSYMBOL_NUMBERSIGN_COMMAND= new LtxPrintCommand(C2_SYMBOL_CHAR,
			"#", "Prints Number Sign '#'", "#");
	TexCommand COMMONSYMBOL_PERCENTSIGN_COMMAND= new LtxPrintCommand(C2_SYMBOL_CHAR,
			"%", "Prints Percent Sign '%'", "%");
	
	TexCommand COMMONSYMBOL_S_COMMAND= new LtxPrintCommand(C2_SYMBOL_TEXT,
			"S", "Prints Section sign '§'", "\u00A7");
	TexCommand COMMONSYMBOL_dag_COMMAND= new LtxPrintCommand(C2_SYMBOL_COMMON,
			"dag", "Prints Dagger", "\u2020");
	TexCommand COMMONSYMBOL_ddag_COMMAND= new LtxPrintCommand(C2_SYMBOL_COMMON,
			"ddag", "Prints Doulbe Dagger", "\u2021");
	TexCommand COMMONSYMBOL_backslash_COMMAND= new LtxPrintCommand(C2_SYMBOL_COMMON,
			"backslash", "Prints Backslash", "\\");
	TexCommand COMMONSYMBOL_textbullet_COMMAND= new LtxPrintCommand(C2_SYMBOL_TEXT,
			"textbullet", "Prints Bullet", "\u2219"); // 2e
	TexCommand COMMONSYMBOL_textperiodcentered_COMMAND= new LtxPrintCommand(C2_SYMBOL_TEXT,
			"textperiodcentered", "Prints centered Dot", "\u22C5"); // 2e
	TexCommand COMMONSYMBOL_dots_COMMAND= new LtxPrintCommand(C2_SYMBOL_COMMON,
			"dots", "Prints horizontal Ellipsis (generic)", "\u2026");
	TexCommand COMMONSYMBOL_textbackslash_COMMAND= new LtxPrintCommand(C2_SYMBOL_COMMON,
			"textbackslash", "Prints Backslash", "\\");
	TexCommand COMMONSYMBOL_textasciicircum_COMMAND= new LtxPrintCommand(C2_SYMBOL_TEXT,
			"textasciicircum", "Prints ASCII character 'Cirum'", "^"); // 2e // u005E
	TexCommand COMMONSYMBOL_textasciitilde_COMMAND= new LtxPrintCommand(C2_SYMBOL_TEXT,
			"textasciitilde", "Prints ASCII character 'Tilde'", "\u0303"); // 2e
	TexCommand COMMONSYMBOL_tex_COMMAND= new LtxPrintCommand(C2_SYMBOL_TEXT,
			"TeX", "Prints the TeX logo", "TeX"); // std
	TexCommand COMMONSYMBOL_latex_COMMAND= new LtxPrintCommand(C2_SYMBOL_TEXT,
			"LaTeX", "Prints the LaTeX logo", "LaTeX"); // 2e
	
	TexCommand COMMONSYMBOL_ss_COMMAND= new LtxPrintCommand(C2_SYMBOL_TEXT,
			"ss", "Prints the German small letter 'Sharp S' ('\u00DF')", "\u00DF"); // std
	TexCommand COMMONSYMBOL_SS_COMMAND= new LtxPrintCommand(C2_SYMBOL_TEXT,
			"SS", "Prints the German capital letter 'Sharp S'", "\u1E9E"); // 2e
	
	
}
