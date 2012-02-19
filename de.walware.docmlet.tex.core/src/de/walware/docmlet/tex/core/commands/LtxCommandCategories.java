/*******************************************************************************
 * Copyright (c) 2012 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.core.commands;

import java.util.ArrayList;
import java.util.List;

import de.walware.ecommons.collections.ConstList;

import de.walware.docmlet.tex.internal.core.Messages;


public class LtxCommandCategories {
	
	
	public static class Category {
		
		
		private final Cat fCat;
		
		private final List<TexCommand> fCommands;
		
		
		private Category(final Cat cat, final List<TexCommand> commands) {
			fCat = cat;
			fCommands = commands;
		}
		
		
		public String getLabel() {
			return fCat.fLabel;
		}
		
		public List<TexCommand> getCommands() {
			return fCommands;
		}
		
		
		@Override
		public String toString() {
			return fCat.fLabel + " [" + fCommands.size() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		
	}
	
	
	private static enum Cat {
		
		SECTIONING(Messages.CommandCategory_Sectioning_label),
		LABEL(Messages.CommandCategory_Label_label),
		TEXT_STYLING(Messages.CommandCategory_TextStyling_label),
		MATH_STYLING(Messages.CommandCategory_MathStyling_label),
		COMMON_SYMBOLS(Messages.CommandCategory_CommonSymbols_label),
		TEXT_SYMBOLS(Messages.CommandCategory_TextSymbols_label),
		MATHSYMBOLS_GREEK_UPPER(Messages.CommandCategory_MathSymbols_GreekUpper_label),
		MATHSYMBOLS_GREEK_LOWER(Messages.CommandCategory_MathSymbols_GreekLower_label),
		MATHSYMBOLS_BIN_OP(Messages.CommandCategory_MathSymbols_BinOp_label),
		MATHSYMBOLS_ROOTFRAC_OP(Messages.CommandCategory_MathSymbols_RootFracOp_label),
		MATHSYMBOLS_REL_STD(Messages.CommandCategory_MathSymbols_RelStd_label),
		MATHSYMBOLS_REL_ARROW(Messages.CommandCategory_MathSymbols_RelArrow_label),
		MATHSYMBOLS_REL_MISC(Messages.CommandCategory_MathSymbols_RelMisc_label),
		MATHSYMBOLS_LARGE_OP(Messages.CommandCategory_MathSymbols_LargeOp_label),
		MATHSYMBOLS_NAMED_OP(Messages.CommandCategory_MathSymbols_NamedOp_label),
		MATHSYMBOLS_MISC_ALPHA(Messages.CommandCategory_MathSymbols_MiscAlpha_label),
		MATHSYMBOLS_MISC_ORD(Messages.CommandCategory_MathSymbols_MiscOrd_label),
		MATHSYMBOLS_DOTS(Messages.CommandCategory_MathSymbols_Dots_label),
		MATHSYMBOLS_ACCENTS(Messages.CommandCategory_MathSymbols_Accents_label),
		MATHSYMBOLS_BRACKETS(Messages.CommandCategory_MathSymbols_Brackets_label);
		
		
		private final String fLabel;
		
		Cat(final String label) {
			fLabel = label;
		}
		
		public String getLabel() {
			return fLabel;
		}
		
	};
	
	
	private final List<Category> fCategories;
	
	
	public LtxCommandCategories(final List<TexCommand> list) {
		final Cat[] cats = Cat.values();
		
		final List<TexCommand>[] lists = new List[cats.length];
		for (final TexCommand command : list) {
			if (include(command)) {
				final Cat cat = getCat(command);
				if (cat != null) {
					if (lists[cat.ordinal()] == null) {
						lists[cat.ordinal()] = new ArrayList<TexCommand>();
					}
					lists[cat.ordinal()].add(command);
				}
//				else {
//					System.out.println("" + command.getType() + " " + command.getControlWord());
//				}
			}
		}
		
		final List<Category> categories = new ArrayList<LtxCommandCategories.Category>(lists.length);
		for (int i = 0; i < lists.length; i++) {
			if (lists[i] != null) {
				categories.add(new Category(cats[i], new ConstList<TexCommand>(lists[i])));
			}
		}
		fCategories = new ConstList<Category>(categories);
	}
	
	
	private Cat getCat(final TexCommand command) {
		switch (command.getType() & TexCommand.MASK_MAIN) {
		case TexCommand.SECTIONING:
			return Cat.SECTIONING;
		case TexCommand.LABEL:
			return Cat.LABEL;
		case TexCommand.STYLE:
			switch (command.getType() & TexCommand.MASK_C2) {
			case TexCommand.C2_STYLE_TEXT:
				return Cat.TEXT_STYLING;
			case TexCommand.C2_STYLE_MATH:
				return Cat.MATH_STYLING;
			default:
				return null;
			}
		case TexCommand.SYMBOL:
			switch (command.getType() & TexCommand.MASK_C2) {
			case TexCommand.C2_SYMBOL_COMMON:
				return Cat.COMMON_SYMBOLS;
			case TexCommand.C2_SYMBOL_TEXT:
				return Cat.TEXT_SYMBOLS;
			default:
				return null;
			}
		case TexCommand.MATHSYMBOL:
			switch (command.getType() & TexCommand.MASK_C3) {
			case TexCommand.C3_MATHSYMBOL_GREEK_UPPER:
				return Cat.MATHSYMBOLS_GREEK_UPPER;
			case TexCommand.C3_MATHSYMBOL_GREEK_LOWER:
				return Cat.MATHSYMBOLS_GREEK_LOWER;
			case TexCommand.C3_MATHSYMBOL_OP_BIN:
				return Cat.MATHSYMBOLS_BIN_OP;
			case TexCommand.C3_MATHSYMBOL_OP_ROOTFRAC:
				return Cat.MATHSYMBOLS_ROOTFRAC_OP;
			case TexCommand.C3_MATHSYMBOL_OP_RELSTD:
				return Cat.MATHSYMBOLS_REL_STD;
			case TexCommand.C3_MATHSYMBOL_OP_RELARROW:
				return Cat.MATHSYMBOLS_REL_ARROW;
			case TexCommand.C3_MATHSYMBOL_OP_RELMISC:
				return Cat.MATHSYMBOLS_REL_MISC;
			case TexCommand.C3_MATHSYMBOL_OP_LARGE:
				return Cat.MATHSYMBOLS_LARGE_OP;
			case TexCommand.C3_MATHSYMBOL_OP_NAMED:
				return Cat.MATHSYMBOLS_NAMED_OP;
			case TexCommand.C3_MATHSYMBOL_MISC_ALPHA:
				return Cat.MATHSYMBOLS_MISC_ALPHA;
			case TexCommand.C3_MATHSYMBOL_MISC_ORD:
				return Cat.MATHSYMBOLS_MISC_ORD;
			case TexCommand.C3_MATHSYMBOL_DOTS:
				return Cat.MATHSYMBOLS_DOTS;
			case TexCommand.C3_MATHSYMBOL_ACCENTS_:
				return Cat.MATHSYMBOLS_ACCENTS;
			case TexCommand.C3_MATHSYMBOL_BRACKETS_:
				return Cat.MATHSYMBOLS_BRACKETS;
			default:
				return null;
			}
		default:
			return null;
		}
	}
	
	
	protected boolean include(final TexCommand command) {
		return true;
	}
	
	
	public List<Category> getCategories() {
		return fCategories;
	}
	
	public Category getCategory(final TexCommand command) {
		final Cat cat = getCat(command);
		if (cat != null) {
			for (final Category category : fCategories) {
				if (category.fCat == cat) {
					return category;
				}
			}
		}
		return null;
	}
	
}
