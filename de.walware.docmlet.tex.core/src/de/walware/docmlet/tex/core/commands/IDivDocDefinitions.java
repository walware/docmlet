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

import static de.walware.docmlet.tex.core.commands.TexCommand.C2_DOCUMENT_INCLUDE;
import static de.walware.docmlet.tex.core.commands.TexCommand.C2_DOCUMENT_LAYOUT;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_DOCUMENT_CONTENTLISTS_DEF;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_DOCUMENT_CONTENTLISTS_GEN;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_DOCUMENT_ELEMENT_IMAGES;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_DOCUMENT_ELEMENT_LISTS;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_DOCUMENT_ELEMENT_TABLES;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_DOCUMENT_INDEX_DEF;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_DOCUMENT_INDEX_GEN;

import de.walware.ecommons.collections.ConstArrayList;


public interface IDivDocDefinitions {
	
	
	TexCommand DOCUMENT_input_COMMAND= new TexCommand(C2_DOCUMENT_INCLUDE,
			"input", false, new ConstArrayList<>( //$NON-NLS-1$
					new Argument("file", Argument.REQUIRED, Argument.RESOURCE_SINGLE)
			), "Insert the content of the given file into the document");
	
	TexCommand DOCUMENT_insert_COMMAND= new TexCommand(C2_DOCUMENT_INCLUDE,
			"insert", false, new ConstArrayList<>( //$NON-NLS-1$
					new Argument("file", Argument.REQUIRED, Argument.RESOURCE_SINGLE)
			), "Includes the content of the given file with page feed into the document");
	
	TexCommand DOCUMENT_includegraphics_COMMAND= new TexCommand(C3_DOCUMENT_ELEMENT_IMAGES,
			"includegraphics", false, new ConstArrayList<>( //$NON-NLS-1$
					new Argument("options", Argument.OPTIONAL, Argument.NONE),
					new Argument("file", Argument.REQUIRED, Argument.RESOURCE_SINGLE)
			), "Includes the graphic of the given file into the document");
	
	TexCommand DOCUMENT_item_COMMAND= new TexCommand(C3_DOCUMENT_ELEMENT_LISTS,
			"item", false, new ConstArrayList<>( //$NON-NLS-1$
					new Argument("symbol/term", Argument.OPTIONAL, Argument.NONE)
			), "Adds a new item to the list");
	
	TexCommand DOCUMENT_hline_COMMAND= new TexCommand(C3_DOCUMENT_ELEMENT_TABLES,
			"hline", "Draws a horizontal Line below the current row");
	
	
	TexCommand DOCUMENT_maketitle_COMMAND= new TexCommand(TexCommand.DOCUMENT,
			"maketitle", "Inserts a Title page in the document"); //$NON-NLS-1$
	
	TexCommand DOCUMENT_addcontentsline_COMMAND= new TexCommand(C3_DOCUMENT_CONTENTLISTS_DEF,
			"addcontentsline", false, new ConstArrayList<>( //$NON-NLS-1$
					new Argument("content list", Argument.REQUIRED, Argument.NONE),
					new Argument("type of entry", Argument.REQUIRED, Argument.NONE),
					new Argument("entry", Argument.REQUIRED, Argument.NONE)
			), "Adds an extra entry to a content list (toc, lof, lot, ...)");
	
	TexCommand DOCUMENT_caption_COMMAND= new TexCommand(C3_DOCUMENT_CONTENTLISTS_DEF,
			"caption", false, new ConstArrayList<>( //$NON-NLS-1$
					new Argument("short caption", Argument.OPTIONAL, Argument.NONE),
					new Argument("caption", Argument.REQUIRED, Argument.NONE)
			), "Adds a caption for the surrounding element");
	
	TexCommand DOCUMENT_tableofcontents_COMMAND= new TexCommand(C3_DOCUMENT_CONTENTLISTS_GEN,
			"tableofcontents", "Inserts a Table of Content (toc) for the document"); //$NON-NLS-1$
	
	TexCommand DOCUMENT_listoffigures_COMMAND= new TexCommand(C3_DOCUMENT_CONTENTLISTS_GEN,
			"listoffigures", "Inserts a List of Figures (lof) in the document"); //$NON-NLS-1$
	
	TexCommand DOCUMENT_listoftables_COMMAND= new TexCommand(C3_DOCUMENT_CONTENTLISTS_GEN,
			"listoftables", "Inserts a List of Tables (lot) in the document"); //$NON-NLS-1$
	
	
	TexCommand DOCUMENT_index_COMMAND= new TexCommand(C3_DOCUMENT_INDEX_DEF,
			"index", false, new ConstArrayList<>( //$NON-NLS-1$
					new Argument("keyword", Argument.REQUIRED, Argument.NONE)
			), "Adds an entry to the index referring to the current position");
	
	TexCommand DOCUMENT_printindex_COMMAND= new TexCommand(C3_DOCUMENT_INDEX_GEN,
			"printindex", "Inserts the Index for the document"); //$NON-NLS-1$
	
	
	TexCommand DOCUMENT_vspace_COMMAND= new TexCommand(C2_DOCUMENT_LAYOUT,
			"vspace", true, new ConstArrayList<>( //$NON-NLS-1$
					new Argument("length", Argument.REQUIRED, Argument.NONE)
			), "Adds vertical space at the current positition");
	
	TexCommand DOCUMENT_hspace_COMMAND= new TexCommand(C2_DOCUMENT_LAYOUT,
			"hspace", true, new ConstArrayList<>( //$NON-NLS-1$
					new Argument("length", Argument.REQUIRED, Argument.NONE)
			), "Adds horizontal space at the current position");
	
	
}
