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

import static de.walware.docmlet.tex.core.commands.TexCommand.C2_PREAMBLE_CONTROLDEF;
import static de.walware.docmlet.tex.core.commands.TexCommand.C2_PREAMBLE_DOCDEF;
import static de.walware.docmlet.tex.core.commands.TexCommand.C2_PREAMBLE_MISC;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_PREAMBLE_CONTROLDEF_COMMAND;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_PREAMBLE_CONTROLDEF_ENV;

import de.walware.ecommons.collections.ConstList;


public interface IPreambleDefinitions {
	
	
	TexCommand PREAMBLE_documentclass_COMMAND = new TexCommand(C2_PREAMBLE_DOCDEF,
			"documentclass", false, new ConstList<Argument>( //$NON-NLS-1$
					new Argument("options", Argument.OPTIONAL, Argument.NONE),
					new Argument("class", Argument.REQUIRED, Argument.NONE)
			), "Sets the class of the document");
	TexCommand PREAMBLE_usepackage_COMMAND = new TexCommand(C2_PREAMBLE_DOCDEF,
			"usepackage", false, new ConstList<Argument>( //$NON-NLS-1$
					new Argument("options", Argument.OPTIONAL, Argument.NONE),
					new Argument("package name", Argument.REQUIRED, Argument.NONE)
			), "Loads given package into use");
	
	TexCommand PREAMBLE_title_COMMAND = new TexCommand(C2_PREAMBLE_DOCDEF,
			"title", false, new ConstList<Argument>( //$NON-NLS-1$
					new Argument("title", Argument.REQUIRED, Argument.TITLE)
			), "Sets the title of the document");
	TexCommand PREAMBLE_author_COMMAND = new TexCommand(C2_PREAMBLE_DOCDEF,
			"author", false, new ConstList<Argument>( //$NON-NLS-1$
					new Argument("author", Argument.REQUIRED, Argument.TITLE)
			), "Sets the author of the document");
	TexCommand PREAMBLE_date_COMMAND = new TexCommand(C2_PREAMBLE_DOCDEF,
			"date", false, new ConstList<Argument>( //$NON-NLS-1$
					new Argument("date", Argument.REQUIRED, Argument.TITLE)
			), "Sets the date of the document");
	
	TexCommand PREAMBLE_newcommand_COMMAND = new TexCommand(C3_PREAMBLE_CONTROLDEF_COMMAND,
			"newcommand", true, new ConstList<Argument>( //$NON-NLS-1$
					new Argument("command", Argument.REQUIRED, Argument.CONTROLWORD),
					new Argument("number of arguments", Argument.OPTIONAL, Argument.NUM),
					new Argument("default for 1st argument", Argument.OPTIONAL, Argument.NONE),
					new Argument("definition", Argument.REQUIRED, Argument.NONE)
			), "Defines a new command");
	TexCommand PREAMBLE_renewcommand_COMMAND = new TexCommand(C3_PREAMBLE_CONTROLDEF_COMMAND,
			"renewcommand", true, new ConstList<Argument>( //$NON-NLS-1$
					new Argument("command", Argument.REQUIRED, Argument.CONTROLWORD),
					new Argument("number of arguments", Argument.OPTIONAL, Argument.NUM),
					new Argument("default for 1st argument", Argument.OPTIONAL, Argument.NONE),
					new Argument("definition", Argument.REQUIRED, Argument.NONE)
			), "Redefines a command");
	TexCommand PREAMBLE_providecommand_COMMAND = new TexCommand(C3_PREAMBLE_CONTROLDEF_COMMAND,
			"providecommand", true, new ConstList<Argument>( //$NON-NLS-1$
					new Argument("command", Argument.REQUIRED, Argument.CONTROLWORD),
					new Argument("number of arguments", Argument.OPTIONAL, Argument.NUM),
					new Argument("default for 1st argument", Argument.OPTIONAL, Argument.NONE),
					new Argument("definition", Argument.REQUIRED, Argument.NONE)
					), "Defines a new command if not yet exists");
	TexCommand PREAMBLE_newenvironment_COMMAND = new TexCommand(C3_PREAMBLE_CONTROLDEF_ENV,
			"newenvironment", true, new ConstList<Argument>( //$NON-NLS-1$
					new Argument("environment name", Argument.REQUIRED, Argument.CONTROLWORD),
					new Argument("number of arguments", Argument.OPTIONAL, Argument.NUM),
					new Argument("default for 1st argument", Argument.OPTIONAL, Argument.NONE),
					new Argument("definition for begin", Argument.REQUIRED, Argument.NONE),
					new Argument("definition for end", Argument.REQUIRED, Argument.NONE)
			), "Defines a new environment");
	TexCommand PREAMBLE_renewenvironment_COMMAND = new TexCommand(C3_PREAMBLE_CONTROLDEF_ENV,
			"renewenvironment", true, new ConstList<Argument>( //$NON-NLS-1$
					new Argument("environment name", Argument.REQUIRED, Argument.CONTROLWORD),
					new Argument("number of arguments", Argument.OPTIONAL, Argument.NUM),
					new Argument("default for 1st argument", Argument.OPTIONAL, Argument.NONE),
					new Argument("definition for begin", Argument.REQUIRED, Argument.NONE),
					new Argument("definition for end", Argument.REQUIRED, Argument.NONE)
			), "Redefines a environment");
	TexCommand PREAMBLE_ensuremath_COMMAND = new TexCommand(C2_PREAMBLE_CONTROLDEF,
			"ensuremath", false, new ConstList<Argument>( //$NON-NLS-1$
					new Argument("definition", Argument.REQUIRED, Argument.NONE)
			), "Ensures math-mode for given definition");
	
	TexCommand PREAMBLE_insertonly_COMMAND = new TexCommand(C2_PREAMBLE_MISC,
			"insertonly", false, new ConstList<Argument>( //$NON-NLS-1$
					new Argument("file list", Argument.REQUIRED, Argument.RESOURCE_LIST)
			), "Specifies which files will be included by \\include");
	TexCommand PREAMBLE_hyphenation_COMMAND = new TexCommand(C2_PREAMBLE_MISC,
			"hyphenation", false, new ConstList<Argument>( //$NON-NLS-1$
					new Argument("word list", Argument.REQUIRED, Argument.NONE)
			), "Defines hyphenation for given words");
	
}
