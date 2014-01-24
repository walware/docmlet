/*=============================================================================#
 # Copyright (c) 2012-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.commands;

import static de.walware.docmlet.tex.core.commands.TexCommand.C2_SECTIONING_CHAPTER;
import static de.walware.docmlet.tex.core.commands.TexCommand.C2_SECTIONING_PART;
import static de.walware.docmlet.tex.core.commands.TexCommand.C2_SECTIONING_SECTION;
import static de.walware.docmlet.tex.core.commands.TexCommand.C2_SECTIONING_SUBSECTION;
import static de.walware.docmlet.tex.core.commands.TexCommand.C2_SECTIONING_SUBSUBSECTION;
import static de.walware.docmlet.tex.core.commands.TexCommand.SECTIONING;

import de.walware.ecommons.collections.ConstList;


public interface ITextSectioningDefinitions {
	
	
	TexCommand SECTIONING_part_COMMAND = new TexCommand(C2_SECTIONING_PART,
			"part", true, new ConstList<Argument>( //$NON-NLS-1$
					new Argument(Argument.OPTIONAL, Argument.TITLE),
					new Argument(Argument.REQUIRED, Argument.TITLE)
			), "Starts a new Part");
	TexCommand SECTIONING_chapter_COMMAND = new TexCommand(C2_SECTIONING_CHAPTER,
			"chapter", true, new ConstList<Argument>( //$NON-NLS-1$
					new Argument(Argument.OPTIONAL, Argument.TITLE),
					new Argument(Argument.REQUIRED, Argument.TITLE)
			), "Starts a new Chapter");
	TexCommand SECTIONING_section_COMMAND = new TexCommand(C2_SECTIONING_SECTION,
			"section", true, new ConstList<Argument>( //$NON-NLS-1$
					new Argument(Argument.OPTIONAL, Argument.TITLE),
					new Argument(Argument.REQUIRED, Argument.TITLE)
			), "Starts a new Section");
	TexCommand SECTIONING_subsection_COMMAND = new TexCommand(C2_SECTIONING_SUBSECTION,
			"subsection", true, new ConstList<Argument>(
					new Argument(Argument.OPTIONAL, Argument.TITLE),
					new Argument(Argument.REQUIRED, Argument.TITLE)
			), "Starts a new SubSection");
	TexCommand SECTIONING_subsubsection_COMMAND = new TexCommand(C2_SECTIONING_SUBSUBSECTION,
			"subsubsection", true, new ConstList<Argument>( //$NON-NLS-1$
					new Argument(Argument.OPTIONAL, Argument.TITLE),
					new Argument(Argument.REQUIRED, Argument.TITLE)
			), "Starts new SubSubSection");
	TexCommand SECTIONING_paragraph_COMMAND = new TexCommand(SECTIONING | 0x60,
			"paragraph", true, new ConstList<Argument>( //$NON-NLS-1$
					new Argument(Argument.OPTIONAL, Argument.TITLE),
					new Argument(Argument.REQUIRED, Argument.TITLE)
			), "Starts a new Paragraph");
	TexCommand SECTIONING_subparagraph_COMMAND = new TexCommand(SECTIONING | 0x70,
			"subparagraph", true, new ConstList<Argument>( //$NON-NLS-1$
					new Argument(Argument.OPTIONAL, Argument.TITLE),
					new Argument(Argument.REQUIRED, Argument.TITLE)
			), "Starts a new SubParagraph");
	
	
}
