/*=============================================================================#
 # Copyright (c) 2012-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.commands;

import static de.walware.docmlet.tex.core.commands.TexCommand.C2_ENV_COMMENT_BEGIN;
import static de.walware.docmlet.tex.core.commands.TexCommand.C2_ENV_DOCUMENT_BEGIN;
import static de.walware.docmlet.tex.core.commands.TexCommand.C2_ENV_MATHCONTENT_BEGIN;
import static de.walware.docmlet.tex.core.commands.TexCommand.C2_ENV_OTHER_BEGIN;
import static de.walware.docmlet.tex.core.commands.TexCommand.C2_ENV_VERBATIM_BEGIN;
import static de.walware.docmlet.tex.core.commands.TexCommand.C2_GENERICENV_BEGIN;
import static de.walware.docmlet.tex.core.commands.TexCommand.C2_GENERICENV_END;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_ENV_ELEMENT_ALIGN_BEGIN;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_ENV_ELEMENT_FLOATS_BEGIN;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_ENV_ELEMENT_IMAGES_BEGIN;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_ENV_ELEMENT_LISTS_BEGIN;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_ENV_ELEMENT_QUOTE_BEGIN;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_ENV_ELEMENT_TABLES_BEGIN;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_ENV_MATH_DISPLAY_BEGIN;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_ENV_MATH_INLINE_BEGIN;
import static de.walware.docmlet.tex.core.commands.TexCommand.VERBATIM_INLINE;

import de.walware.jcommons.collections.ImCollections;


public interface IEnvDefinitions {
	
	
	Argument GENERICENV_ENVLABEL_ARGUMENT= new Argument("environment name", Argument.REQUIRED, Argument.LABEL_ENV);
	
	
	TexCommand GENERICENV_begin_COMMAND= new TexCommand(C2_GENERICENV_BEGIN,
			"begin", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Marks the begin of a new environment" );
	TexCommand GENERICENV_end_COMMAND= new TexCommand(C2_GENERICENV_END,
			"end", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Marks the end an open environment" );
	
	
	TexCommand ENV_document_BEGIN= new TexCommand(C2_ENV_DOCUMENT_BEGIN,
			"document", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Document Content" );
	
	TexCommand ENV_math_BEGIN= new TexCommand(C3_ENV_MATH_INLINE_BEGIN,
			"math", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Inline Math Environment" );
	TexCommand ENV_displaymath_BEGIN= new TexCommand(C3_ENV_MATH_DISPLAY_BEGIN,
			"displaymath", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Math Environment" );
	TexCommand ENV_equation_BEGIN= new TexCommand(C3_ENV_MATH_DISPLAY_BEGIN,
			"equation", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Single Equation Math Environment, numbered" );
	TexCommand ENV_equationA_BEGIN= new TexCommand(C3_ENV_MATH_DISPLAY_BEGIN,
			"equation*", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Single Equation Math Environment, nonnumbered" );
	TexCommand ENV_eqnarray_BEGIN= new TexCommand(C3_ENV_MATH_DISPLAY_BEGIN,
			"eqnarray", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Multiline Math Environment, numbered" );
	TexCommand ENV_eqnarrayA_BEGIN= new TexCommand(C3_ENV_MATH_DISPLAY_BEGIN,
			"eqnarray*", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Multiline Math Environment, nonnumbered" );
	TexCommand ENV_multiline_BEGIN= new TexCommand(C3_ENV_MATH_DISPLAY_BEGIN,
			"multiline", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Single Multiline Equation Math Environment, numbered" );
	TexCommand ENV_multilineA_BEGIN= new TexCommand(C3_ENV_MATH_DISPLAY_BEGIN,
			"multiline*", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Single Multiline Equation Math Environment, nonnumbered" );
	TexCommand ENV_gather_BEGIN= new TexCommand(C3_ENV_MATH_DISPLAY_BEGIN,
			"gather", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Multiple Equation Math Environment, numbered, without alignment" );
	TexCommand ENV_gatherA_BEGIN= new TexCommand(C3_ENV_MATH_DISPLAY_BEGIN,
			"gather*", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Multiple Equation Math Environment, nonnumbered, without alignment" );
	TexCommand ENV_align_BEGIN= new TexCommand(C3_ENV_MATH_DISPLAY_BEGIN,
			"align", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Multiple Equation Math Environment, numbered, with mutual alignment" );
	TexCommand ENV_alignA_BEGIN= new TexCommand(C3_ENV_MATH_DISPLAY_BEGIN,
			"align*", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Multiple Equation Math Environment, nonnumbered, with mutual alignment" );
	TexCommand ENV_alignat_BEGIN= new TexCommand(C3_ENV_MATH_DISPLAY_BEGIN,
			"alignat", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT,
					new Argument("number of columns", Argument.REQUIRED, Argument.NUM)
			), "Multiple Equation Math Environment, numbered, with mutual alignment" );
	TexCommand ENV_alignatA_BEGIN= new TexCommand(C3_ENV_MATH_DISPLAY_BEGIN,
			"alignat*", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT,
					new Argument("number of columns", Argument.REQUIRED, Argument.NUM)
			), "Multiple Equation Math Environment, nonnumbered, with mutual alignment" );
	
	TexCommand ENV_math_BEGIN_SHORTHAND= new TexCommand(0, "(",
			"Begin Inline Math Environment (LaTeX shorthand)" );
	TexCommand ENV_displaymath_BEGIN_SHORTHAND= new TexCommand(0, "[",
			"Begin Math Environment (LaTeX shorthand)" );
	TexCommand ENV_math_END_SHORTHAND= new TexCommand(0, ")",
			"End Inline Math Environment (LaTeX shorthand)" );
	TexCommand ENV_displaymath_END_SHORTHAND= new TexCommand(0, "]",
			"End Math Environment (LaTeX shorthand)" );
	
	TexCommand ENV_verbatim_BEGIN= new TexCommand(C2_ENV_VERBATIM_BEGIN,
			"verbatim", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Verbatim Environment" );
	TexCommand ENV_verbatimA_BEGIN= new TexCommand(C2_ENV_VERBATIM_BEGIN,
			"verbatim*", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Verbatim Environment, spaces printed" );
	TexCommand ENV_lstlisting_BEGIN= new TexCommand(C2_ENV_VERBATIM_BEGIN,
			"lstlisting", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Source Code Listing" );
	
	
	TexCommand ENV_Sinput_BEGIN= new TexCommand(C2_ENV_VERBATIM_BEGIN,
			"Sinput", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Sweave Input Environment" );
	TexCommand ENV_Souput_BEGIN= new TexCommand(C2_ENV_VERBATIM_BEGIN,
			"Soutput", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Sweave Output Environment" );
	
	
	TexCommand ENV_comment_BEGIN= new TexCommand(C2_ENV_COMMENT_BEGIN,
			"comment", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Comment Environment" );
	
	
	TexCommand ENV_quote_BEGIN= new TexCommand(C3_ENV_ELEMENT_QUOTE_BEGIN,
			"quote", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Short Quotation Environment" );
	TexCommand ENV_quotation_BEGIN= new TexCommand(C3_ENV_ELEMENT_QUOTE_BEGIN,
			"quotation", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Long Quotation Environment" );
	TexCommand ENV_verse_BEGIN= new TexCommand(C3_ENV_ELEMENT_QUOTE_BEGIN,
			"verse", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Poetry Environment" );
	
	TexCommand ENV_enumerate_BEGIN= new TexCommand(C3_ENV_ELEMENT_LISTS_BEGIN,
			"enumerate", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Ordered List Environment (numbered)" );
	TexCommand ENV_itemize_BEGIN= new TexCommand(C3_ENV_ELEMENT_LISTS_BEGIN,
			"itemize", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Unordered List Environment (bullet)" );
	TexCommand ENV_description_BEGIN= new TexCommand(C3_ENV_ELEMENT_LISTS_BEGIN,
			"description", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Definition List Environment (named)" );
	
	TexCommand ENV_tabular_BEGIN= new TexCommand(C3_ENV_ELEMENT_TABLES_BEGIN,
			"tabular", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT,
					new Argument(Argument.OPTIONAL, Argument.POS),
					new Argument(Argument.REQUIRED, Argument.NONE)
					), "Table Structure Environment" );
	TexCommand ENV_tabbing_BEGIN= new TexCommand(C3_ENV_ELEMENT_TABLES_BEGIN,
			"tabbing", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
					), "Tabbing Environment" );
	
	TexCommand ENV_picture_BEGIN= new TexCommand(C3_ENV_ELEMENT_IMAGES_BEGIN,
			"picture", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
					), "Picture Drawing Environment" );
	
	TexCommand ENV_table_BEGIN= new TexCommand(C3_ENV_ELEMENT_FLOATS_BEGIN,
			"table", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT,
					new Argument("location", Argument.OPTIONAL, Argument.LOC)
			), "Table Float Environment" );
	TexCommand ENV_figure_BEGIN= new TexCommand(C3_ENV_ELEMENT_FLOATS_BEGIN,
			"figure", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT,
					new Argument("location", Argument.OPTIONAL, Argument.LOC)
			), "Figure Float Environment" );
	
	TexCommand ENV_center_BEGIN= new TexCommand(C3_ENV_ELEMENT_ALIGN_BEGIN,
			"center", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Center Aligning Environment" );
	TexCommand ENV_flushleft_BEGIN= new TexCommand(C3_ENV_ELEMENT_ALIGN_BEGIN,
			"flushleft", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Left Aligning Environment" );
	TexCommand ENV_flushright_BEGIN= new TexCommand(C3_ENV_ELEMENT_ALIGN_BEGIN,
			"flushright", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Centered Aligning Environment" );
	
	TexCommand ENV_thebibliography_BEGIN= new TexCommand(C2_ENV_OTHER_BEGIN,
			"thebibliography", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT,
					new Argument("prototype for label", Argument.REQUIRED, Argument.NONE)
			), "Centered Aligning Environment" );
	
	TexCommand ENV_array_BEGIN= new TexCommand(C2_ENV_MATHCONTENT_BEGIN,
			"array", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT,
					new Argument("position", Argument.OPTIONAL, Argument.POS),
					new Argument("columns", Argument.REQUIRED, Argument.NONE)
			), "Array Structure" );
	TexCommand ENV_matrix_BEGIN= new TexCommand(C2_ENV_MATHCONTENT_BEGIN,
			"matrix", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Matrix Structure (without brackets)" );
	TexCommand ENV_smallmatrix_BEGIN= new TexCommand(C2_ENV_MATHCONTENT_BEGIN,
			"smallmatrix", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Inline Matrix Structure" );
	TexCommand ENV_pmatrix_BEGIN= new TexCommand(C2_ENV_MATHCONTENT_BEGIN,
			"pmatrix", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Matrix, matrix surrounded by parentheses" );
	TexCommand ENV_bmatrix_BEGIN= new TexCommand(C2_ENV_MATHCONTENT_BEGIN,
			"bmatrix", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Matrix, surrounded by square brackets" );
	TexCommand ENV_Bmatrix_BEGIN= new TexCommand(C2_ENV_MATHCONTENT_BEGIN,
			"Bmatrix", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Matrix, surrounded by curly brackets" );
	TexCommand ENV_vmatrix_BEGIN= new TexCommand(C2_ENV_MATHCONTENT_BEGIN,
			"vmatrix", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Matrix, surrounded by single vertical lines" );
	TexCommand ENV_Vmatrix_BEGIN= new TexCommand(C2_ENV_MATHCONTENT_BEGIN,
			"Vmatrix", false, ImCollections.newList( //$NON-NLS-1$
					GENERICENV_ENVLABEL_ARGUMENT
			), "Matrix, surrounded by double vertical lines" );
	
	
	TexCommand VERBATIM_verb_COMMAND= new TexCommand(VERBATIM_INLINE,
			"verb", "Inserts Inline Verbatim Environment" ); //$NON-NLS-1$
	
}
