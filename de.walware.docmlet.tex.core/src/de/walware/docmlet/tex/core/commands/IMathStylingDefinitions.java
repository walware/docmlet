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

import static de.walware.docmlet.tex.core.commands.TexCommand.C2_STYLE_MATH;

import de.walware.ecommons.collections.ConstArrayList;


public interface IMathStylingDefinitions {
	
	
	TexCommand STYLE_mathnormal_COMMAND= new LtxFontCommand(C2_STYLE_MATH,
			"mathnormal", new ConstArrayList<>(new Argument(Argument.REQUIRED, Argument.NONE)),
			"Prints given text using Normal Math font"); // 2e
	TexCommand STYLE_mathrm_COMMAND= new LtxFontCommand(C2_STYLE_MATH,
			"mathrm", new ConstArrayList<>(new Argument(Argument.REQUIRED, Argument.NONE)),
			"Prints given text using Roman Typeface font family"); // 2e
	TexCommand STYLE_mathsf_COMMAND= new LtxFontCommand(C2_STYLE_MATH,
			"mathsf", new ConstArrayList<>(new Argument(Argument.REQUIRED, Argument.NONE)),
			"Prints given text using Sans-Serif Typeface font family"); // 2e
	TexCommand STYLE_mathtt_COMMAND= new LtxFontCommand(C2_STYLE_MATH,
			"mathtt", new ConstArrayList<>(new Argument(Argument.REQUIRED, Argument.NONE)),
			"Prints given text using Typewriter-like Face font family"); // 2e
	TexCommand STYLE_mathcal_COMMAND= new LtxFontCommand(C2_STYLE_MATH,
			"mathcal", new ConstArrayList<>(new Argument(Argument.REQUIRED, Argument.NONE)),
			"Prints given text using Calligraphic font family"); // 2e
	TexCommand STYLE_mathbf_COMMAND= new LtxFontCommand(C2_STYLE_MATH,
			"mathbf", new ConstArrayList<>(new Argument(Argument.REQUIRED, Argument.NONE)),
			"Prints given text using Bold Weight font series"); // 2e
	TexCommand STYLE_mathit_COMMAND= new LtxFontCommand(C2_STYLE_MATH,
			"mathit", new ConstArrayList<>(new Argument(Argument.REQUIRED, Argument.NONE)),
			"Prints given text using Italic font shape"); // 2e
	
	
	TexCommand MISC_nonumber_COMMAND= new TexCommand(0,
			"nonumber", "Disables numbering of the current equation");
	
}
