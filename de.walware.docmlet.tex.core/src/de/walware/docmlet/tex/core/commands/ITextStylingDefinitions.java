/*******************************************************************************
 * Copyright (c) 2012-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.core.commands;

import static de.walware.docmlet.tex.core.commands.TexCommand.C2_STYLE_TEXT;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_STYLE_TEXT_FONT_B;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_STYLE_TEXT_FONT_O;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_STYLE_TEXT_SIZE_O;

import de.walware.ecommons.collections.ConstList;


public interface ITextStylingDefinitions {
	
	
	TexCommand COMMONFONTS_rm_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_O,
			"rm", "Switches to (normal) Roman typeface"); // tex //$NON-NLS-1$
	TexCommand COMMONFONTS_sl_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_O,
			"sl", "Switches to Slanted Roman typeface"); // tex //$NON-NLS-1$
	TexCommand COMMONFONTS_tt_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_O,
			"tt", "Switches to Typewriter-like face"); // tex //$NON-NLS-1$
	TexCommand COMMONFONTS_it_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_O,
			"it", "Switches to Italic C2_STYLE_TEXT typeface"); // tex //$NON-NLS-1$
	TexCommand COMMONFONTS_bf_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_O,
			"bf", "Switches to Bold C2_STYLE_TEXT typeface"); // tex //$NON-NLS-1$
	
	TexCommand COMMONFONTS_underline_COMMAND = new LtxFontCommand(C2_STYLE_TEXT,
			"underline", new ConstList<Argument>( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Underlines the given text"); // tex
	
	TexCommand COMMONFONTS_rmfamily_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_O,
			"rmfamily", "Changes font to Roman Typeface family"); // 2e //$NON-NLS-1$
	TexCommand COMMONFONTS_sffamily_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_O,
			"sffamily", "Changes font to Sans-Serif Typeface family"); // 2e //$NON-NLS-1$
	TexCommand COMMONFONTS_ttfamily_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_O,
			"ttfamily", "Changes font to Typewriter-like Face family"); // 2e //$NON-NLS-1$
	TexCommand COMMONFONTS_mdseries_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_O,
			"mdseries", "Changes font to Medium Weight series"); // 2e //$NON-NLS-1$
	TexCommand COMMONFONTS_bfseries_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_O,
			"bfseries", "Changes font to Bold Weight series"); // 2e //$NON-NLS-1$
	TexCommand COMMONFONTS_upshape_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_O,
			"upshape", "Changes font to Upright shape"); // 2e //$NON-NLS-1$
	TexCommand COMMONFONTS_itshape_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_O,
			"itshape", "Changes font to Italic shape"); // 2e //$NON-NLS-1$
	TexCommand COMMONFONTS_slshape_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_O,
			"slshape", "Changes font to Slated shape"); // 2e //$NON-NLS-1$
	TexCommand COMMONFONTS_scshape_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_O,
			"scshape", "Changes font to Small Caps shape"); // 2e //$NON-NLS-1$
	TexCommand COMMONFONTS_em_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_O,
			"em", "Toggles font emphasize shape"); // 2e //$NON-NLS-1$
	
	TexCommand COMMONFONTS_textrm_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_B,
			"textrm", new ConstList<Argument>( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints given text using Roman Typeface font family"); // 2e
	TexCommand COMMONFONTS_textsf_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_B,
			"textsf", new ConstList<Argument>( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints given text using Sans-Serif Typeface font family"); // 2e
	TexCommand COMMONFONTS_texttt_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_B,
			"texttt", new ConstList<Argument>( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints given text using Typewriter-like Face font family"); // 2e
	TexCommand COMMONFONTS_textmd_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_B,
			"textmd", new ConstList<Argument>( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints given text using Medium Weight font series"); // 2e
	TexCommand COMMONFONTS_textbf_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_B,
			"textbf", new ConstList<Argument>( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints given text using Bold Weight font series"); // 2e
	TexCommand COMMONFONTS_textup_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_B,
			"textup", new ConstList<Argument>( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints given text using Upright font shape"); // 2e
	TexCommand COMMONFONTS_textit_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_B,
			"textit", new ConstList<Argument>( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints given text using Italic font shape"); // 2e
	TexCommand COMMONFONTS_textsl_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_B,
			"textsl", new ConstList<Argument>( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints given text using Slated font shape"); // 2e
	TexCommand COMMONFONTS_textsc_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_B,
			"textsc", new ConstList<Argument>( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints given text using Small Caps font shape"); // 2e
	TexCommand COMMONFONTS_emph_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_FONT_B,
			"emph", new ConstList<Argument>( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints given text emphasized"); // 2e
	
	TexCommand COMMONFONTS_tiny_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_SIZE_O,
			"tiny", "Changes font to tiny size"); // //$NON-NLS-1$
	TexCommand COMMONFONTS_scriptsize_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_SIZE_O,
			"scriptsize", "Changes font to size of subscript/supscript"); // //$NON-NLS-1$
	TexCommand COMMONFONTS_small_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_SIZE_O,
			"small", "Changes font to small size"); // //$NON-NLS-1$
	TexCommand COMMONFONTS_normalsize_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_SIZE_O,
			"normalsize", "Changes font to normal size"); // //$NON-NLS-1$
	TexCommand COMMONFONTS_large_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_SIZE_O,
			"large", "Changes font to large size"); // //$NON-NLS-1$
	TexCommand COMMONFONTS_Large_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_SIZE_O,
			"Large", "Changes font to very large size"); // //$NON-NLS-1$
	TexCommand COMMONFONTS_LARGE_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_SIZE_O,
			"LARGE", "Changes font to very very large size"); // //$NON-NLS-1$
	TexCommand COMMONFONTS_huge_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_SIZE_O,
			"huge", "Changes font to huge size"); // //$NON-NLS-1$
	TexCommand COMMONFONTS_Huge_COMMAND = new LtxFontCommand(C3_STYLE_TEXT_SIZE_O,
			"Huge", "Changes font to very huge size"); // //$NON-NLS-1$
	
}
