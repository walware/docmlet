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

import static de.walware.docmlet.tex.core.commands.TexCommand.C2_BIB_INCLUDE;
import static de.walware.docmlet.tex.core.commands.TexCommand.C2_BIB_REF;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_LABEL_COUNTER_DEF;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_LABEL_COUNTER_REF;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_LABEL_REFLABEL_DEF;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_LABEL_REFLABEL_REF;

import de.walware.ecommons.collections.ConstArrayList;


public interface IRefDefinitions {
	
	
	TexCommand LABEL_label_COMMAND = new TexCommand(C3_LABEL_REFLABEL_DEF,
			"label", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("marker", Argument.REQUIRED, Argument.LABEL_REFLABEL_DEF)
			), "Marks the current element/line with the given label");
	TexCommand LABEL_ref_COMMAND = new TexCommand(C3_LABEL_REFLABEL_REF,
			"ref", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("marker", Argument.REQUIRED, Argument.LABEL_REFLABEL_REF)
			), "Prints a Reference (number) to the given label");
	TexCommand LABEL_pageref_COMMAND = new TexCommand(C3_LABEL_REFLABEL_REF,
			"pageref", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("marker", Argument.REQUIRED, Argument.LABEL_REFLABEL_REF)
			), "Prints a Page Reference (page number) to the given label");
	
	TexCommand LABEL_eqref_COMMAND = new TexCommand(C3_LABEL_REFLABEL_REF,
			"eqref", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("marker", Argument.REQUIRED, Argument.LABEL_REFLABEL_REF)
			), "Prints a Reference (number) to the given equation label");
	
	
	TexCommand LABEL_newcounter_COMMAND = new TexCommand(C3_LABEL_COUNTER_DEF,
			"newcounter", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("counter", Argument.REQUIRED, Argument.LABEL_COUNTER_DEF),
					new Argument("superordinated counter", Argument.OPTIONAL, Argument.LABEL_COUNTER_REF)
			), "Defines a new counter");
	
	TexCommand LABEL_setcounter_COMMAND = new TexCommand(C3_LABEL_COUNTER_REF,
			"setcounter", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("counter", Argument.REQUIRED, Argument.LABEL_COUNTER_SET),
					new Argument("value", Argument.REQUIRED, Argument.NONE)
			), "Sets the counter to the given value");
	
	TexCommand LABEL_addtocounter_COMMAND = new TexCommand(C3_LABEL_COUNTER_REF,
			"addtocounter", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("counter", Argument.REQUIRED, Argument.LABEL_COUNTER_SET),
					new Argument("value", Argument.REQUIRED, Argument.NONE)
			), "Increments the counter by the given value");
	
	TexCommand LABEL_stepcounter_COMMAND = new TexCommand(C3_LABEL_COUNTER_REF,
			"stepcounter", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("counter", Argument.REQUIRED, Argument.LABEL_COUNTER_SET)
			), "Increments the counter by one");
	
	TexCommand LABEL_Alph_COMMAND = new TexCommand(C3_LABEL_COUNTER_REF,
			"Alph", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("counter", Argument.REQUIRED, Argument.LABEL_COUNTER_REF)
			), "Prints the current value of the counter in alphabetic uppercase letters (A, B, C,...)");
	
	TexCommand LABEL_alph_COMMAND = new TexCommand(C3_LABEL_COUNTER_REF,
			"alph", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("counter", Argument.REQUIRED, Argument.LABEL_COUNTER_REF)
			), "Prints the current value of the counter in alphabetic lowercase letters (a, b, c,...)");
	
	TexCommand LABEL_Roman_COMMAND = new TexCommand(C3_LABEL_COUNTER_REF,
			"Roman", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("counter", Argument.REQUIRED, Argument.LABEL_COUNTER_REF)
			), "Prints the current value of the counter in uppercase roman numbers (I, II, III,...)");
	
	TexCommand LABEL_roman_COMMAND = new TexCommand(C3_LABEL_COUNTER_REF,
			"roman", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("counter", Argument.REQUIRED, Argument.LABEL_COUNTER_REF)
			), "Prints the current value of the counter in lowercase roman numbers (i, ii, iii,...)");
	
	TexCommand LABEL_arabic_COMMAND = new TexCommand(C3_LABEL_COUNTER_REF,
			"arabic", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("counter", Argument.REQUIRED, Argument.LABEL_COUNTER_REF)
			), "Prints the current value of the counter in arabic numbers (1, 2, 3,...)");
	
	TexCommand LABEL_value_COMMAND = new TexCommand(C3_LABEL_COUNTER_REF,
			"value", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("counter", Argument.REQUIRED, Argument.LABEL_COUNTER_REF)
			), "Returns the current value of the counter as number");
	
	
	TexCommand BIB_bibitem_COMMAND = new TexCommand(TexCommand.C2_BIB_DEF,
			"bibitem", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("label to print", Argument.OPTIONAL, Argument.NONE),
					new Argument("key", Argument.REQUIRED, Argument.LABEL_BIB_DEF)
			), "Adds an entry to the bibliography");
	
	TexCommand BIB_cite_COMMAND = new TexCommand(C2_BIB_REF,
			"cite", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("annotation", Argument.OPTIONAL, Argument.NONE),
					new Argument("key", Argument.REQUIRED, Argument.LABEL_BIB_REF)
			), "Prints a literature reference to the given bibliography entry");
	
	TexCommand BIB_nocite_COMMAND = new TexCommand(C2_BIB_REF,
			"nocite", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("key", Argument.REQUIRED, Argument.LABEL_BIB_REF)
			), "Ensures that the given literature reference appears in the bibliography of the document");
	
	TexCommand BIB_bibliography_COMMAND = new TexCommand(C2_BIB_INCLUDE,
			"bibliography", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("files", Argument.REQUIRED, Argument.RESOURCE_LIST)
			), "Includes the given bibliography(s)");
	
	TexCommand BIB_bibliographystyle_COMMAND = new TexCommand(TexCommand.BIB,
			"bibliographystyle", false, new ConstArrayList<Argument>( //$NON-NLS-1$
					new Argument("style", Argument.REQUIRED, Argument.NONE)
			), "Includes the given bibliography(s)");
	
	
}
