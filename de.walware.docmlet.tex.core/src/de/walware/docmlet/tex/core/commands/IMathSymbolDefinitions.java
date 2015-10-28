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

import static de.walware.docmlet.tex.core.commands.TexCommand.C3_MATHSYMBOL_ACCENTS_;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_MATHSYMBOL_BRACKETS_;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_MATHSYMBOL_DOTS;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_MATHSYMBOL_GREEK_LOWER;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_MATHSYMBOL_GREEK_UPPER;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_MATHSYMBOL_MISC_ALPHA;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_MATHSYMBOL_MISC_ORD;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_MATHSYMBOL_OP_BIN;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_MATHSYMBOL_OP_LARGE;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_MATHSYMBOL_OP_NAMED;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_MATHSYMBOL_OP_RELARROW;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_MATHSYMBOL_OP_RELMISC;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_MATHSYMBOL_OP_RELSTD;
import static de.walware.docmlet.tex.core.commands.TexCommand.C3_MATHSYMBOL_OP_ROOTFRAC;

import de.walware.jcommons.collections.ImCollections;


public interface IMathSymbolDefinitions {
	
	//-- Greek Letters --
	TexCommand MATHSYMBOL_Alpha_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Alpha", "Prints greek capital letter Alpha", "\u0391" );
	TexCommand MATHSYMBOL_alpha_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"alpha", "Prints greek small letter Alpha", "\u03B1" ); // std
	TexCommand MATHSYMBOL_Beta_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Beta", "Prints greek capital letter Beta", "\u0392" );
	TexCommand MATHSYMBOL_beta_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"beta", "Prints greek small letter Beta", "\u03B2" ); // std
	TexCommand MATHSYMBOL_Gamma_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Gamma", "Prints greek capital letter Gamma", "\u0393" ); // std
	TexCommand MATHSYMBOL_gamma_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"gamma", "Prints greek small letter Gamma", "\u03B3" ); // std
	TexCommand MATHSYMBOL_Delta_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Delta", "Prints greek capital letter Delta", "\u0394" ); // std
	TexCommand MATHSYMBOL_delta_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"delta", "Prints greek small letter Delta", "\u03B4" ); // std
	TexCommand MATHSYMBOL_Epsilon_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Epsilon", "Prints greek capital letter Epsilon", "\u03B5" );
	TexCommand MATHSYMBOL_epsilon_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"epsilon", "Prints greek small letter Epsilon", "\u0395" ); // std
	TexCommand MATHSYMBOL_varepsilon_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"varepsilon", "Prints greek small letter Epsilon variant", "\u03F5" ); // std
	TexCommand MATHSYMBOL_Zeta_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Zeta", "Prints greek capital letter Zeta", "\u0396" );
	TexCommand MATHSYMBOL_zeta_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"zeta", "Prints greek small letter Zeta", "\u03B6" ); // std
	TexCommand MATHSYMBOL_Eta_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Eta", "Prints greek capital letter Eta", "\u0397" );
	TexCommand MATHSYMBOL_eta_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"eta", "Prints greek small letter Eta", "\u03B7" ); // std
	TexCommand MATHSYMBOL_Theta_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Theta", "Prints greek capital letter Theta", "\u0398" ); // std
	TexCommand MATHSYMBOL_theta_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"theta", "Prints greek small letter Theta", "\u03B8" ); // std
	TexCommand MATHSYMBOL_vartheta_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"vartheta", "Prints greek small letter Theta variant", "\u03D1" ); // std
	TexCommand MATHSYMBOL_Iota_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Iota", "Prints greek small letter Iota", "\u0399" );
	TexCommand MATHSYMBOL_iota_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"iota", "Prints greek small letter Iota", "\u03B9" ); // std
	TexCommand MATHSYMBOL_Kappa_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Kappa", "Prints greek capital letter Kappa", "\u039A" );
	TexCommand MATHSYMBOL_kappa_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"kappa", "Prints greek small letter Kappa", "\u03BA" ); // std
	TexCommand MATHSYMBOL_varkappa_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"varkappa", "Prints greek small letter Kappa variant", "\u03F0" ); // ams
	TexCommand MATHSYMBOL_Lambda_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Lambda", "Prints greek capital letter Lambda", "\u039B" ); // std
	TexCommand MATHSYMBOL_lambda_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"lambda", "Prints greek small letter Lambda", "\u03BB" ); // std
	TexCommand MATHSYMBOL_Mu_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Mu", "Prints greek capital letter Mu", "\u039C" );
	TexCommand MATHSYMBOL_mu_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"mu", "Prints greek small letter Mu", "\u03BC" ); // std
	TexCommand MATHSYMBOL_Nu_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Nu", "Prints greek capital letter Nu", "\u039D" );
	TexCommand MATHSYMBOL_nu_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"nu", "Prints greek small letter Nu", "\u03BD" ); // std
	TexCommand MATHSYMBOL_Xi_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Xi", "Prints greek capital letter Xi", "\u039E" ); // std
	TexCommand MATHSYMBOL_xi_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"xi", "Prints greek small letter Xi", "\u03BE" ); // std
	TexCommand MATHSYMBOL_Omicron_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Omicron", "Prints greek capital letter Omicron", "\u039F" );
	TexCommand MATHSYMBOL_omicron_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"omicron", "Prints greek small letter Omicron", "\u03BF" );
	TexCommand MATHSYMBOL_Pi_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Pi", "Prints greek capital letter Pi", "\u03A0" ); // std
	TexCommand MATHSYMBOL_pi_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"pi", "Prints greek small letter Pi", "\u03C0" ); // std
	TexCommand MATHSYMBOL_varpi_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"varpi", "Prints greek small letter Pi variant", "\u03D6" ); // std
	TexCommand MATHSYMBOL_Rho_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Rho", "Prints greek capital letter Rho", "\u03A1" );
	TexCommand MATHSYMBOL_rho_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"rho", "Prints greek small letter Rho", "\u03C1" ); // std
	TexCommand MATHSYMBOL_varrho_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"varrho", "Prints greek small letter Rho variant", "\u03F1" ); // std
	TexCommand MATHSYMBOL_Sigma_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Sigma", "Prints greek capital letter Sigma", "\u03A3" ); // std
	TexCommand MATHSYMBOL_sigma_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"sigma", "Prints greek small letter Sigma", "\u03C3" ); // std
	TexCommand MATHSYMBOL_varsigma_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"varsigma", "Prints greek small letter Sigma final variant", "\u03C2" ); // std
	TexCommand MATHSYMBOL_Tau_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Tau", "Prints greek capital letter Tau", "\u03A4" );
	TexCommand MATHSYMBOL_tau_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"tau", "Prints greek small letter Tau", "\u03C4" ); // std
	TexCommand MATHSYMBOL_Upsilon_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Upsilon", "Prints greek capital letter Upsilon", "\u03A5" );
	TexCommand MATHSYMBOL_upsilon_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"upsilon", "Prints greek small letter Upsilon", "\u03C5" ); // std
	TexCommand MATHSYMBOL_Phi_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Phi", "Prints greek capital letter Phi", "\u03A6" ); // std
	TexCommand MATHSYMBOL_phi_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"phi", "Prints greek small letter Phi", "\u03D5" ); // std
	TexCommand MATHSYMBOL_varphi_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"varphi", "Prints greek small letter Phi variant", "\u03C6" ); // std
	TexCommand MATHSYMBOL_Chi_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Chi", "Prints greek capital letter Chi", "\u03A7" );
	TexCommand MATHSYMBOL_chi_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"chi", "Prints greek small letter Chi", "\u03C7" ); // std
	TexCommand MATHSYMBOL_Psi_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Psi", "Prints greek capital letter Psi", "\u03A8" ); // std
	TexCommand MATHSYMBOL_psi_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"psi", "Prints greek small letter Psi", "\u03C8" ); // std
	TexCommand MATHSYMBOL_Omega_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_UPPER,
			"Omega", "Prints greek capital letter Omega", "\u03A9" ); // std
	TexCommand MATHSYMBOL_omega_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_GREEK_LOWER,
			"omega", "Prints greek small letter Omega", "\u03C9" ); // std
	
	//-- Bin Op --
	TexCommand MATHSYMBOL_pm_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"pm", "Prints operator sign 'Plus-Or-Minus'", "\u00B1" ); // std
	TexCommand MATHSYMBOL_mp_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"mp", "Prints operator sign 'Minus-Or-Plus'", "\u2213" ); // std
	TexCommand MATHSYMBOL_cdot_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"cdot", "Prints operator sign 'Dot'", "\u22C5" ); // std
	TexCommand MATHSYMBOL_times_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"times", "Prints operator sign 'Times'", "\u00D7" ); // std
	TexCommand MATHSYMBOL_ast_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"ast", "Prints operator sign 'Asterisk'", "\u2217" ); // std
	TexCommand MATHSYMBOL_star_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"star", "Prints operator sign 'Star'", "\u22C6" ); // std
	TexCommand MATHSYMBOL_diamond_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"diamond", "Prints operator sign 'Diamond'", "\u22C4" ); // std
	TexCommand MATHSYMBOL_circ_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"circ", "Prints operator sign 'Circle'/'Ring'", "\u2218" ); // std
	TexCommand MATHSYMBOL_bullet_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"bullet", "Prints operator sign 'Bullet'", "\u2219" ); // std
	TexCommand MATHSYMBOL_div_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"div", "Prints operator sign 'Division'", "\u00F7" ); // std
	TexCommand MATHSYMBOL_cap_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"cap", "Prints operator sign 'Cap' / 'Intersection'", "\u2229" ); // std
	TexCommand MATHSYMBOL_cup_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"cup", "Prints operator sign 'Cup' / 'Union'", "\u222A" ); // std
	TexCommand MATHSYMBOL_setminus_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"setminus", "Prints operator sign 'Minus' for sets", "\u2216" ); // std
	TexCommand MATHSYMBOL_uplus_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"uplus", "Prints operator sign 'Union with Plus'", "\u228E" ); // std
	TexCommand MATHSYMBOL_sqcap_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"sqcap", "Prints operator sign 'Square Cap' / 'Square Intersection'", "\u2293" ); // std
	TexCommand MATHSYMBOL_sqcup_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"sqcup", "Prints operator sign 'Square Cup' / 'Square Union'", "\u2294" ); // std
	TexCommand MATHSYMBOL_triangleleft_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"triangleleft", "Prints operator sign 'Triangle Left'", "\u25C1" ); // std
	TexCommand MATHSYMBOL_triangleright_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"triangleright", "Prints operator sign 'Triangle Right'", "\u25B7" ); // std
	TexCommand MATHSYMBOL_wr_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"wr", "Prints operator sign 'Wreath Product'", "\u2240" ); // std
	TexCommand MATHSYMBOL_wedge_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"wedge", "Prints operator sign 'Wedge' / 'Logical And'", "\u2228" ); // std
	TexCommand MATHSYMBOL_land_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"land", "Prints operator sign 'Logical And'", "\u2228" ); // std alias
	TexCommand MATHSYMBOL_vee_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"vee", "Prints operator sign 'Vee' / 'Logical Or'", "\u2227" ); // std
	TexCommand MATHSYMBOL_lor_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"lor", "Prints operator sign 'Logical Or'", "\u2227" ); // std alias
	TexCommand MATHSYMBOL_oplus_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"oplus", "Prints operator sign 'Circled Plus'", "\u2295" ); // std
	TexCommand MATHSYMBOL_ominus_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"ominus", "Prints operator sign 'Circled Minus'", "\u2296" ); // std
	TexCommand MATHSYMBOL_otimes_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"otimes", "Prints operator sign 'Circled Times'", "\u2297" ); // std
	TexCommand MATHSYMBOL_oslash_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"oslash", "Prints operator sign 'Circled Slash'", "\u2298" ); // std
	TexCommand MATHSYMBOL_odot_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"odot", "Prints operator sign 'Circled Dot'", "\u2299" ); // std
	TexCommand MATHSYMBOL_dagger_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"dagger", "Prints operator sign 'Dagger'", "\u2220" ); // std
	TexCommand MATHSYMBOL_ddagger_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"ddagger", "Prints operator sign 'Double Dagger'", "\u2221" ); // std
	TexCommand MATHSYMBOL_amalg_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_BIN,
			"amalg", "Prints operator sign 'Amalgation' / 'Coproduct'", "\u2A3F" ); // std
	
	//-- Roots, Fractions, ... --
	TexCommand MATHSYMBOL_sqrt_COMMAND= new TexCommand(C3_MATHSYMBOL_OP_ROOTFRAC,
			"sqrt", false, ImCollections.newList( //$NON-NLS-1$
					new Argument(Argument.OPTIONAL, Argument.NONE),
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints Root of given value and optional radix" ); // std
	TexCommand MATHSYMBOL_frac_COMMAND= new TexCommand(C3_MATHSYMBOL_OP_ROOTFRAC,
			"frac", false, ImCollections.newList( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE),
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints Fraction of given numerator and denominator" ); // std
	TexCommand MATHSYMBOL_dfrac_COMMAND= new TexCommand(C3_MATHSYMBOL_OP_ROOTFRAC,
			"dfrac", false, ImCollections.newList( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE),
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints Fraction (display-style sized) of given numerator and denominator" ); // std
	TexCommand MATHSYMBOL_tfrac_COMMAND= new TexCommand(C3_MATHSYMBOL_OP_ROOTFRAC,
			"tfrac", false, ImCollections.newList( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE),
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints Fraction (text-style sized) of given numerator and denominator" ); // std
	
	//-- Rel Std --
	TexCommand MATHSYMBOL_equiv_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"equiv", "Prints sign 'Equivalent To'/'Identical To'", "\u2261" ); // std
	TexCommand MATHSYMBOL_sim_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"sim", "Prints sign 'Similar To' (Tilde operator)", "\u223C" ); // std
	TexCommand MATHSYMBOL_simeq_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"simeq", "Prints sign 'Similar or Equal To'", "\u2243" );  // std
	TexCommand MATHSYMBOL_asymp_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"asymp", "Prints sign 'Asymptotic'/'Equivalent To'", "\u224D" ); // std
	TexCommand MATHSYMBOL_approx_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"approx", "Prints sign 'Approximately' / 'Almost Equal To'", "\u2248" ); // std
	TexCommand MATHSYMBOL_approxeq_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"approxeq", "Prints sign 'Approximately Equal To'", "\u224A" ); // ams
	TexCommand MATHSYMBOL_eqsim_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"eqsim", "Prints sign 'Equal To or Equivalent/Similar To'", "\u2242" ); // ams
	TexCommand MATHSYMBOL_cong_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"cong", "Prints sign 'Congruent To'", "\u2245" ); // std
	TexCommand MATHSYMBOL_doteq_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"doteq", "Prints sign 'Dot, Equal' / 'Approaches The Limit'", "\u2250" );  // std // \u227D
	
	TexCommand MATHSYMBOL_leq_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"leq", "Prints sign 'Less-Than or Equal To'", "\u2264" ); // std
	TexCommand MATHSYMBOL_le_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"le", "Prints sign 'Less-Than or Equal To'", "\u2264" ); // std alias
	TexCommand MATHSYMBOL_geq_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"geq", "Prints sign 'Greater-Than or Equal To'", "\u2265" ); // std
	TexCommand MATHSYMBOL_ge_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"ge", "Prints sign 'Greater-Than or Equal To'", "\u2265" ); // std alias
	TexCommand MATHSYMBOL_ll_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"ll", "Prints sign 'Much Less-Than'", "\u226A" ); // std
	TexCommand MATHSYMBOL_gg_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"gg", "Prints sign 'Much Greater-Than'", "\u226B" ); // std
	TexCommand MATHSYMBOL_leqq_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"leqq", "Prints sign 'Less-Than Over Equal To'", "\u2266" ); // ams
	TexCommand MATHSYMBOL_geqq_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"geqq", "Prints sign 'Less-Than Over Equal To'", "\u2267" ); // ams
	TexCommand MATHSYMBOL_leqslant_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"leqslant", "Prints sign 'Less-Than or Slanted Equal To'", "\u2A7D" ); // ams
	TexCommand MATHSYMBOL_geqslant_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"geqslant", "Prints sign 'Greater-Than or Slanted Equal To'", "\u2A7E" ); // ams
	TexCommand MATHSYMBOL_eqslantless_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"eqslantless", "Prints sign 'Slanted Equal To or Less-Than'", "\u2A95" ); // ams
	TexCommand MATHSYMBOL_eqslantgtr_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"eqslantgtr", "Prints sign 'Slanted Equal To or Greater-Than'", "\u2A96" ); // ams
	TexCommand MATHSYMBOL_lesssim_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"lesssim", "Prints sign 'Less-Than or Equivalent/Similar To'", "\u2272" ); // ams
	TexCommand MATHSYMBOL_gtrsim_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"gtrsim", "Prints sign 'Greater-Than or Equivalent/Similar To'", "\u2273" ); // ams
	TexCommand MATHSYMBOL_lessapprox_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"lessapprox", "Prints sign 'Less-Than or Approximate'", "\u2A85" ); // ams
	TexCommand MATHSYMBOL_gtrapprox_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"gtrapprox", "Prints sign 'Greater-Than or Approximate'", "\u2A86" ); // ams
	TexCommand MATHSYMBOL_lessdot_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"lessdot", "Prints sign 'Less-Than' with Dot", "\u22D6" ); // ams
	TexCommand MATHSYMBOL_gtrdot_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"gtrdot", "Prints sign 'Greater-Than' with Dot", "\u22D7" ); // ams
	TexCommand MATHSYMBOL_llless_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"llless", "Prints sign 'Very Much Less-Than'", "\u22D8" ); // ams
	TexCommand MATHSYMBOL_gggtr_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"gggtr", "Prints sign 'Very Much Greater-Than'", "\u22D9" ); // ams
	
	TexCommand MATHSYMBOL_lessgtr_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"lessgtr", "Prints sign 'Less-Than or Greater-Than'", "\u2276" ); // ams
	TexCommand MATHSYMBOL_gtrless_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"gtrless", "Prints sign 'Greater-Than or Less-Than'", "\u2277" ); // ams
	TexCommand MATHSYMBOL_lesseqgtr_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"lesseqgtr", "Prints sign 'Less-Than, Equal To or Greater-Than'", "\u22DA" ); // ams
	TexCommand MATHSYMBOL_gtreqless_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"gtreqless", "Prints sign 'Less-Than, Equal To or Greater-Than'", "\u22DB" ); // ams
	
	TexCommand MATHSYMBOL_prec_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"prec", "Prints sign 'Precides'", "\u227A" ); // std
	TexCommand MATHSYMBOL_succ_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"succ", "Prints sign 'Succeeds'", "\u227B" ); // std
	TexCommand MATHSYMBOL_preceq_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"preceq", "Prints sign 'Precides or Equal To'", "\u2AAF" );  // std // \u227C
	TexCommand MATHSYMBOL_succeq_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELSTD,
			"succeq", "Prints sign 'Succeeds or Equal To'", "\u2AB0" );  // std // \u227D
	
	//-- Rel Misc --
	TexCommand MATHSYMBOL_in_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"in", "Prints sign 'Element Of'", "\u2208" ); // std
	TexCommand MATHSYMBOL_ni_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"ni", "Prints sign 'Contains As Element'", "\u220B" ); // std
	TexCommand MATHSYMBOL_subset_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"subset", "Prints sign 'Subset Of'", "\u2282" ); // std
	TexCommand MATHSYMBOL_supset_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"supset", "Prints sign 'Superset Of'", "\u2283" ); // std
	TexCommand MATHSYMBOL_subseteq_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"subseteq", "Prints sign 'Subset Of or Equal To'", "\u2286" ); // std
	TexCommand MATHSYMBOL_supseteq_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"supseteq", "Prints sign 'Superset Of or Equal To'", "\u2287" ); // std
	TexCommand MATHSYMBOL_sqsubset_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"sqsubset", "Prints sign 'Square Subset/Image Of'", "\u228F" ); // ams and others
	TexCommand MATHSYMBOL_sqsupset_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"sqsupset", "Prints sign 'Square Superset/Original Of'", "\u2290" ); // ams and others
	TexCommand MATHSYMBOL_sqsubseteq_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"sqsubseteq", "Prints sign 'Square Subset/Image Of or Equal To'", "\u2291" ); // std
	TexCommand MATHSYMBOL_sqsupseteq_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"sqsupseteq", "Prints sign 'Square Superset/Original Of or Equal To'", "\u2292" ); // std
	TexCommand MATHSYMBOL_bowtie_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"bowtie", "Prints sign 'Bowtie' / 'Natural Join'", "\u22C8" ); // std
	
	TexCommand MATHSYMBOL_propto_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"propto", "Prints relation sign 'Proportional To'", "\u221D" ); // std
	TexCommand MATHSYMBOL_mid_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"mid", "Prints relation symbol 'Vertical Bar'", "\u2223" ); // std
	TexCommand MATHSYMBOL_vdash_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"vdash", "Prints relation sign 'Vertical, Dash' / 'Proves'", "\u22A2" ); // std
	TexCommand MATHSYMBOL_dashv_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"dashv", "Prints relation sign 'Dash, Vertical'", "\u22A3" ); // std
	TexCommand MATHSYMBOL_models_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"models", "Prints relation sign 'Models'", "\u22A7" ); // std
	TexCommand MATHSYMBOL_vDash_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"vDash", "Prints relation sign 'Vertical, Double Dash' / 'True'", "\u22A8" ); // ams
	TexCommand MATHSYMBOL_Vdash_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"Vdash", "Prints relation sign 'Double Dash, Vertical' / 'Forces'", "\u22A9" ); // ams
	TexCommand MATHSYMBOL_Vvdash_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"Vvdash", "Prints relation sign 'Triple Dash, Vertical'", "\u22AA" ); // ams
	TexCommand MATHSYMBOL_vartriangleleft_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"vartriangleleft", "Prints sign 'Normal Subgroup Of'", "\u22B2" ); // std
	TexCommand MATHSYMBOL_vartriangleright_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"vartriangleright", "Prints sign 'Contains As Normal Subgroup'", "\u22B3" ); // std
	TexCommand MATHSYMBOL_trianglelefteq_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"trianglelefteq", "Prints sign 'Normal Subgroup Of or Equal To'", "\u22B4" ); // std
	TexCommand MATHSYMBOL_trianglerighteq_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"trianglerighteq", "Prints sign 'Contains As Normal Subgroup or Equal To'", "\u22B5" ); // std
	TexCommand MATHSYMBOL_parallel_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"parallel", "Prints relation sign 'Parallel To'", "\u2225" ); // std
	TexCommand MATHSYMBOL_perp_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"perp", "Prints relation sign 'Perpendicular To'", "\u27C2" ); // std
	TexCommand MATHSYMBOL_frown_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"frown", "Prints symbol 'Frown'", "\u2322" ); // std
	TexCommand MATHSYMBOL_smile_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELMISC,
			"smile", "Prints symbol 'Smile'", "\u2323" ); // std
	
	//-- Rel Arrow --
	TexCommand MATHSYMBOL_leftarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"leftarrow", "Prints leftwards arrow", "\u2190" ); // std
	TexCommand MATHSYMBOL_gets_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"gets", "Prints leftwards arrow", "\u2190" ); // std alias
	TexCommand MATHSYMBOL_rightarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"rightarrow", "Prints rightwards arrow", "\u2192" ); // std
	TexCommand MATHSYMBOL_to_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"to", "Prints rightwards arrow", "\u2192" ); // std alias
	TexCommand MATHSYMBOL_uparrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"uparrow", "Prints upwards arrow", "\u2191" ); // std
	TexCommand MATHSYMBOL_downarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"downarrow", "Prints downwards arrow", "\u2193" ); // std
	TexCommand MATHSYMBOL_leftrightarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"leftrightarrow", "Prints left-right arrow", "\u2194" ); // std
	TexCommand MATHSYMBOL_updownarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"updownarrow", "Prints up-down arrow", "\u2195" ); // std
	TexCommand MATHSYMBOL_nwarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"nwarrow", "Prints north-west arrow", "\u2196" ); // std
	TexCommand MATHSYMBOL_nearrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"nearrow", "Prints north-east arrow", "\u2197" ); // std
	TexCommand MATHSYMBOL_searrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"searrow", "Prints south-east arrow", "\u2198" ); // std
	TexCommand MATHSYMBOL_swarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"swarrow", "Prints south-west arrow", "\u2199" ); // std
	TexCommand MATHSYMBOL_Leftarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"Leftarrow", "Prints leftwards double arrow", "\u21D0" ); // std
	TexCommand MATHSYMBOL_Rightarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"Rightarrow", "Prints rightwards double arrow", "\u21D2" ); // std
	TexCommand MATHSYMBOL_Uparrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"Uparrow", "Prints upwards double arrow", "\u21D1" ); // std
	TexCommand MATHSYMBOL_Downarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"Downarrow", "Prints downwards double arrow", "\u21D3" ); // std
	TexCommand MATHSYMBOL_Leftrightarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"Leftrightarrow", "Prints left-right double arrow", "\u21D4" ); // std
	TexCommand MATHSYMBOL_Updownarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"Updownarrow", "Prints up-down double arrow", "\u21D5" ); // std
	TexCommand MATHSYMBOL_longleftarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"longleftarrow", "Prints long leftwards arrow", "\u27F5" ); // std
	TexCommand MATHSYMBOL_longrightarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"longrightarrow", "Prints long rightwards arrow", "\u27F6" ); // std
	TexCommand MATHSYMBOL_longleftrightarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"longleftrightarrow", "Prints long left-right arrow", "\u27F7" ); // std
	TexCommand MATHSYMBOL_Longleftarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"Longleftarrow", "Prints long leftwards double arrow", "\u27F8" ); // std
	TexCommand MATHSYMBOL_Longrightarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"Longrightarrow", "Prints long rightwards double arrow", "\u27F9" ); // std
	TexCommand MATHSYMBOL_Longleftrightarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"Longleftrightarrow", "Prints long left-right double arrow", "\u27FA" ); // std
	TexCommand MATHSYMBOL_mapsto_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"mapsto", "Prints 'Maps To' arrow (rightwards from bar)", "\u27A6" ); // std
	TexCommand MATHSYMBOL_longmapsto_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"longmapsto", "Prints long 'Maps To' arrow (rightwards from bar)", "\u27FC" ); // std
	TexCommand MATHSYMBOL_hookleftarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"hookleftarrow", "Prints leftwards arrow with hook", "\u21A9" ); // std
	TexCommand MATHSYMBOL_hookrightarrow_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"hookrightarrow", "Prints rightwards arrow with hook", "\u21AA" ); // std
	TexCommand MATHSYMBOL_leftharpoonup_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"leftharpoonup", "Prints leftwards harpoon with barb upwards", "\u21BC" ); // std
	TexCommand MATHSYMBOL_leftharpoondown_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"leftharpoondown", "Prints leftwards harpoon with barb downwards", "\u21BD" ); // std
	TexCommand MATHSYMBOL_rightharpoonup_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"rightharpoonup", "Prints rightwards harpoon with barb upwards", "\u21C0" ); // std
	TexCommand MATHSYMBOL_rightharpoondown_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"rightharpoondown", "Prints rightwards harpoon with barb downwards", "\u21C1" ); // std
	TexCommand MATHSYMBOL_rightleftharpoons_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_RELARROW,
			"rightleftharpoons", "Prints rightwards harpoon over leftwards harpoon", "\u21CC" ); // std
	
	//-- Large Op --
	TexCommand MATHSYMBOL_sum_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_LARGE,
			"sum", "Prints operator sign (N-Ary) 'Sum'", "\u2211" ); // std
	TexCommand MATHSYMBOL_prod_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_LARGE,
			"prod", "Prints operator sign (N-Ary) 'Product'", "\u220F" ); // std
	TexCommand MATHSYMBOL_coprod_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_LARGE,
			"coprod", "Prints operator sign (N-Ary) 'Coproduct'", "\u2210" ); // std
	TexCommand MATHSYMBOL_int_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_LARGE,
			"int", "Prints operator sign (N-Ary) 'Integral'", "\u222B" ); // std
	TexCommand MATHSYMBOL_oint_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_LARGE,
			"oint", "Prints operator sign (N-Ary) 'Contour Integral'", "\u222E" ); // std
	TexCommand MATHSYMBOL_bigcap_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_LARGE,
			"bigcap", "Prints large operator sign 'Cap' / (N-Ary) 'Intersection'", "\u22C2" ); // std
	TexCommand MATHSYMBOL_bigcup_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_LARGE,
			"bigcup", "Prints large operator sign 'Cup' / (N-Ary) 'Union'", "\u22C3" ); // std
	TexCommand MATHSYMBOL_bigsqcup_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_LARGE,
			"bigsqcup", "Prints operator sign 'Square Cup' / (N-Ary) 'Square Union'", "\u2A06" ); // std
	TexCommand MATHSYMBOL_bigwedge_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_LARGE,
			"bigwedge", "Prints operator sign 'Wedge' / (N-Ary) 'Logical And'", "\u22C0" ); // std
	TexCommand MATHSYMBOL_bigvee_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_LARGE,
			"bigvee", "Prints operator sign 'Vee' / (N-Ary) 'Logical Or'", "\u22C1" ); // std
	TexCommand MATHSYMBOL_bigodot_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_LARGE,
			"bigodot", "Prints operator sign (N-Ary) 'Circled Dot'", "\u2A00" ); // std
	TexCommand MATHSYMBOL_bigoplus_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_LARGE,
			"bigoplus", "Prints operator sign (N-Ary) 'Circled Plus'", "\u2A01" ); // std
	TexCommand MATHSYMBOL_bigotimes_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_LARGE,
			"bigotimes", "Prints operator sign (N-Ary) 'Circled Times'", "\u2A02" ); // std
	TexCommand MATHSYMBOL_biguplus_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_LARGE,
			"biguplus", "Prints operator sign / (N-Ary) 'Union with Plus'", "\u2A04" ); // std
	
	//-- Named Op (Fun) --
	TexCommand MATHSYMBOL_exp_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"exp", "Prints token for Exponential function", "exp" ); // std
	TexCommand MATHSYMBOL_log_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"log", "Prints token for Logarithm function", "log" ); // std
	TexCommand MATHSYMBOL_ln_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"ln", "Prints token for Natural Logarithm function", "ln" ); // std
	TexCommand MATHSYMBOL_lg_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"lg", "Prints token for Common Logarithm function", "lg" ); // std
	TexCommand MATHSYMBOL_arg_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"arg", "Prints token for Argument function", "arg" ); // std
	
	TexCommand MATHSYMBOL_sin_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"sin", "Prints token for Sine function", "sin" ); // std
	TexCommand MATHSYMBOL_cos_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"cos", "Prints token for Cosine function", "cos" ); // std
	TexCommand MATHSYMBOL_tan_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"tan", "Prints token for Tangent function", "tan" ); // std
	TexCommand MATHSYMBOL_cot_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"cot", "Prints token for Cotangent function", "cot" ); // std
	TexCommand MATHSYMBOL_sec_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"sec", "Prints token for Secant function", "sec" ); // std
	TexCommand MATHSYMBOL_csc_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"csc", "Prints token for Cosecant function", "csc" ); // std
	TexCommand MATHSYMBOL_arcsin_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"arcsin", "Prints token for Arcsine function", "arcsin" ); // std
	TexCommand MATHSYMBOL_arccos_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"arccos", "Prints token for Arccosine function", "arccos" ); // std
	TexCommand MATHSYMBOL_arctan_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"arctan", "Prints token for Arctangent function", "arctan" ); // std
	TexCommand MATHSYMBOL_sinh_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"sinh", "Prints token for Hyperbolic Sine function", "sinh" ); // std
	TexCommand MATHSYMBOL_cosh_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"cosh", "Prints token for Hyperbolic Cosine function", "cosh" ); // std
	TexCommand MATHSYMBOL_tanh_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"tanh", "Prints token for Hyperbolic Tangent function", "tanh" ); // std
	TexCommand MATHSYMBOL_coth_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"coth", "Prints token for Hyperbolic Cotangent function", "coth" ); // std
	
	TexCommand MATHSYMBOL_min_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"min", "Prints token for Minimum", "min" ); // std
	TexCommand MATHSYMBOL_max_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"max", "Prints token for Maximum", "max" ); // std
	TexCommand MATHSYMBOL_inf_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"inf", "Prints token for Infimum", "inf" ); // std
	TexCommand MATHSYMBOL_sup_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"sup", "Prints token for Supremum", "sup" ); // std
	TexCommand MATHSYMBOL_liminf_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"liminf", "Prints token for Limit Inferior", "lim\u2009inf" ); // std
	TexCommand MATHSYMBOL_limsup_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"limsup", "Prints token for Limit Superior", "lim\u2009sup" ); // std
	TexCommand MATHSYMBOL_lim_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"lim", "Prints token for Limit", "lim" ); // std
	
	TexCommand MATHSYMBOL_dim_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"dim", "Prints token for Dimension", "dim" ); // std
	TexCommand MATHSYMBOL_det_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"det", "Prints token for Determinant", "det" ); // std
	TexCommand MATHSYMBOL_ker_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"ker", "Prints token for Kernel", "ker" ); // std
	TexCommand MATHSYMBOL_hom_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"hom", "Prints token for Homomorphism", "hom" ); // std
	TexCommand MATHSYMBOL_deg_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"deg", "Prints token for Degree function", "deg" ); // std
	
	TexCommand MATHSYMBOL_gcd_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"gcd", "Prints token for Greatest Common Divisor", "gcd" ); // std
	TexCommand MATHSYMBOL_Pr_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_OP_NAMED,
			"Pr", "Prints token for Probability", "Pr" ); // std
	
	//-- Misc Letters --
	TexCommand MATHSYMBOL_aleph_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ALPHA,
			"aleph", "Prints hebrew letter Aleph", "\u2135" ); // std
	TexCommand MATHSYMBOL_beth_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ALPHA,
			"beth", "Prints hebrew letter Beth", "\u2136" ); // ams
	TexCommand MATHSYMBOL_gimel_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ALPHA,
			"gimel", "Prints hebrew letter Gimel", "\u2137" ); // ams
	TexCommand MATHSYMBOL_daleth_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ALPHA,
			"daleth", "Prints hebrew letter Daleth", "\u2138" ); // ams
	TexCommand MATHSYMBOL_imath_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ALPHA,
			"imath", "Prints mathematical small dotless letter 'i'", "\ud835\udea4" ); // std // 1D6A4
	TexCommand MATHSYMBOL_jmath_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ALPHA,
			"jmath", "Prints mathematical small dotless letter 'j'", "\ud835\udea5" ); // std // 1D6A5
	TexCommand MATHSYMBOL_complement_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ALPHA,
			"complement", "Prints Complement sign", "\u2201" ); // ams
	TexCommand MATHSYMBOL_ell_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ALPHA,
			"ell", "Prints cursive small letter 'l'", "\u2113" ); // std
	TexCommand MATHSYMBOL_eth_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ALPHA,
			"eth", "Prints latin small letter Eth", "\u00F0" ); // ams
	TexCommand MATHSYMBOL_hbar_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ALPHA,
			"hbar", "Prints mathematical small letter 'h' with bar", "\u0127" ); // std
	TexCommand MATHSYMBOL_hslash_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ALPHA,
			"hslash", "Prints mathematical small letter 'h' with stroke / 'Planck Constant over Two Pi'", "\u210F" ); // ams
	TexCommand MATHSYMBOL_mho_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ALPHA,
			"mho", "Prints inverted Ohm sign", "\u2127" ); // ams
	TexCommand MATHSYMBOL_partial_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ALPHA,
			"partial", "Prints Partial Differential sign", "\u2202" ); // std
	TexCommand MATHSYMBOL_wp_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ALPHA,
			"wp", "Prints Weierstrass P / script capital 'P'", "\u2118" ); // std
	TexCommand MATHSYMBOL_Re_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ALPHA,
			"Re", "Prints sign Real Part", "\u211C" ); // std
	TexCommand MATHSYMBOL_Im_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ALPHA,
			"Im", "Prints sign Imaginary Part", "\u2111" ); // std
	TexCommand MATHSYMBOL_Finv_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ALPHA,
			"Finv", "Prints inverted capital F sign", "\u2132" ); // std
	
	//-- Misc Ord --
	TexCommand MATHSYMBOL_prime_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"prime", "Prints Prime", "\u2032" ); // std
	TexCommand MATHSYMBOL_backprime_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"backprime", "Prints Reversed Prime", "\u2035" ); // ams
	TexCommand MATHSYMBOL_infty_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"infty", "Prints Infinity sign", "\u221E" ); // ams
	TexCommand MATHSYMBOL_emptyset_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"emptyset", "Prints sign 'Empty Set'", "\u2205" ); // ams
	TexCommand MATHSYMBOL_varnothing_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"varnothing", "Prints sign 'Empty Set' variant", "\u2205" ); // ams
	TexCommand MATHSYMBOL_nabla_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"nabla", "Prints Nabla sign", "\u2207" ); // std
	TexCommand MATHSYMBOL_surd_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"surd", "Prints Surd/Radical sign (Square Root)", "\u221A" ); // std
	TexCommand MATHSYMBOL_top_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"top", "Prints Top sign (Down Tack)", "\u22A4" ); // std
	TexCommand MATHSYMBOL_bot_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"bot", "Prints Bottom sign (Up Tack)", "\u22A5" ); // std
	TexCommand MATHSYMBOL_angle_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"angle", "Prints Angle symbol", "\u2220" ); // std
	TexCommand MATHSYMBOL_measuredangle_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"measuredangle", "Prints Measured Angle symbol", "\u2221" ); // ams
	TexCommand MATHSYMBOL_sphericalangle_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"sphericalangle", "Prints Spherical Angle symbol", "\u2222" ); // ams
	
	TexCommand MATHSYMBOL_blacktriangle_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"blacktriangle", "Prints Black Up-Pointing Triangle", "\u25B2" ); // std
	TexCommand MATHSYMBOL_triangle_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"triangle", "Prints White Up-Pointing Triangle", "\u25B3" ); // std
	TexCommand MATHSYMBOL_blacktriangledown_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"blacktriangledown", "Prints Black Down-Pointing Triangle", "\u25BC" ); // ams
	TexCommand MATHSYMBOL_triangledown_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"triangledown", "Prints White Down-Pointing Triangle", "\u25BD" ); // ams
	TexCommand MATHSYMBOL_blacksquare_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"blacksquare", "Prints Black Square", "\u25A0" ); // ams
	TexCommand MATHSYMBOL_square_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"square", "Prints White Square", "\u25A1" ); // ams
	TexCommand MATHSYMBOL_blacklozenge_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"blacklozenge", "Prints Black Lozenge", "\u29EB" ); // ams
	TexCommand MATHSYMBOL_lozenge_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"lozenge", "Prints White Lozenge", "\u25CA" ); // ams
	
	TexCommand MATHSYMBOL_forall_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"forall", "Prints sign 'For All'", "\u2200" ); // std
	TexCommand MATHSYMBOL_exists_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"exists", "Prints sign 'There Exists'", "\u2203" ); // std
	TexCommand MATHSYMBOL_nexists_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"nexists", "Prints sign 'There Does Not Exist'", "\u2204" ); // ams
	TexCommand MATHSYMBOL_neg_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"neg", "Prints sign 'Not'", "\u00AC" ); // std
	TexCommand MATHSYMBOL_flat_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"flat", "Prints sign music sign 'Flat'", "\u266D" ); // std
	TexCommand MATHSYMBOL_natural_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"natural", "Prints sign music sign 'Natural'", "\u266E" ); // std
	TexCommand MATHSYMBOL_sharp_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"sharp", "Prints sign music sign 'Sharp'", "\u266F" ); // std
	TexCommand MATHSYMBOL_spadesuit_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"spadesuit", "Prints Black 'Spade' suit symbol", "\u2660" ); // std
	TexCommand MATHSYMBOL_heartsuit_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"heartsuit", "Prints White 'Heart' suit symbol", "\u2661" ); // std
	TexCommand MATHSYMBOL_diamondsuit_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"diamondsuit", "Prints White 'Diamond' suit symbol", "\u2662" ); // std
	TexCommand MATHSYMBOL_clubsuit_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_MISC_ORD,
			"clubsuit", "Prints Black 'Club' suit symbol", "\u2663" ); // std
	
	TexCommand COMMONSYMBOL_ldots_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_DOTS,
			"ldots", "Prints horizontal Ellipsis (on line)", "\u2026" ); // common symbol vs dot category?
	
	TexCommand MATHSYMBOL_cdots_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_DOTS,
			"cdots", "Prints midline horizontal Ellipsis", "\u22EF" ); // std
	TexCommand MATHSYMBOL_dotsc_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_DOTS,
			"dotsc", "Prints horizontal Ellipsis for comma separated lists", "\u2026" ); // ams
	TexCommand MATHSYMBOL_dotsb_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_DOTS,
			"dotsb", "Prints horizontal Ellipsis for binary operations/relations", "\u22EF" ); // ams
	TexCommand MATHSYMBOL_dotsm_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_DOTS,
			"dotsm", "Prints horizontal Ellipsis for dot operators (multiplication)", "\u22C5\u22C5\u22C5" ); // ams
	TexCommand MATHSYMBOL_dotsi_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_DOTS,
			"dotsi", "Prints horizontal Ellipsis for integrals", "\u22EF" ); // ams
	TexCommand MATHSYMBOL_dotso_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_DOTS,
			"dotso", "Prints horizontal Ellipsis (other dots)", "\u22EF" ); // ams
	TexCommand MATHSYMBOL_vdots_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_DOTS,
			"vdots", "Prints vertical Ellipsis", "\u22EE" ); // std
	TexCommand MATHSYMBOL_adots_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_DOTS,
			"adots", "Prints up right diagonal Ellipsis", "\u22F0" ); // std
	TexCommand MATHSYMBOL_ddots_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_DOTS,
			"ddots", "Prints down right diagonal Ellipsis", "\u22F1" ); // std
	
	//-- Brackets --
	TexCommand MATHSYMBOL_lbrack_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_BRACKETS_,
			"lbrack", "Prints left Square Bracket", "\u005B" ); // std
	TexCommand MATHSYMBOL_rbrack_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_BRACKETS_,
			"rbrack", "Prints right Square Bracket", "\u005D" ); // std
	TexCommand MATHSYMBOL_lceil_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_BRACKETS_,
			"lceil", "Prints left Ceiling delimiter", "\u2308" ); // std
	TexCommand MATHSYMBOL_rceil_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_BRACKETS_,
			"rceil", "Prints right Ceiling delimiter", "\u2309" ); // std
	TexCommand MATHSYMBOL_lfloor_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_BRACKETS_,
			"lfloor", "Prints left Floor delimiter", "\u230A" ); // std
	TexCommand MATHSYMBOL_rfloor_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_BRACKETS_,
			"rfloor", "Prints right Floor delimiter", "\u230B" ); // std
	TexCommand MATHSYMBOL_lbrace_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_BRACKETS_,
			"lbrace", "Prints left Curly Bracket", "\u007B" ); // std
	TexCommand MATHSYMBOL_rbrace_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_BRACKETS_,
			"rbrace", "Prints right Curly Bracket", "\u007D" ); // std
	TexCommand MATHSYMBOL_langle_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_BRACKETS_,
			"langle", "Prints left Angle Bracket", "\u2329" ); // std
	TexCommand MATHSYMBOL_rangle_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_BRACKETS_,
			"rangle", "Prints right Angle Bracket", "\u232A" ); // std
	
	//-- Accents --
	TexCommand MATHSYMBOL_grave_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_ACCENTS_,
			"grave", ImCollections.newList( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints accent Grave above given text", "\u0300" ); // std \u02CB
	TexCommand MATHSYMBOL_acute_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_ACCENTS_,
			"acute", ImCollections.newList( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
					), "Prints accent Acute above given text", "\u0301" ); // std u02CA
	TexCommand MATHSYMBOL_hat_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_ACCENTS_,
			"hat", ImCollections.newList( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints accent Circumflex (hat) above given text", "\u0302" ); // std u02C6
	TexCommand MATHSYMBOL_tilde_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_ACCENTS_,
			"tilde", ImCollections.newList( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints Tilde above given text", "\u0303" ); // std u02DC
	TexCommand MATHSYMBOL_bar_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_ACCENTS_,
			"bar", ImCollections.newList( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints Macron (bar) above given text", "\u0304" ); // std u00AF
	TexCommand MATHSYMBOL_overline_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_ACCENTS_,
			"overline", ImCollections.newList( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
					), "Prints Line above given text", "\u0305" ); // std u00AF
	TexCommand MATHSYMBOL_breve_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_ACCENTS_,
			"breve", ImCollections.newList( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints Breve above given text", "\u0306" ); // std u02D8
	TexCommand MATHSYMBOL_check_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_ACCENTS_,
			"check", ImCollections.newList( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints Carot (inversed hat) above given text", "\u030C" ); // std u02C7
	TexCommand MATHSYMBOL_dot_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_ACCENTS_,
			"dot", ImCollections.newList( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints Dot above given text", "\u0307" ); // std u02D9
	TexCommand MATHSYMBOL_ddot_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_ACCENTS_,
			"ddot", ImCollections.newList( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints Diaeresis (two dots) above given text", "\u0308" ); // std u00A8
	TexCommand MATHSYMBOL_dddot_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_ACCENTS_,
			"dddot", ImCollections.newList( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints Three Dots above given text", "\u20DB" ); // ams
	TexCommand MATHSYMBOL_vec_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_ACCENTS_,
			"vec", ImCollections.newList( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints Vector indicator/Right Arrow above given text", "\u20D7" ); // std
	TexCommand MATHSYMBOL_widehat_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_ACCENTS_,
			"widehat", ImCollections.newList( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints wide accent Circumflex (hat) above given text", null ); // std
	TexCommand MATHSYMBOL_widetilde_COMMAND= new LtxPrintCommand(C3_MATHSYMBOL_ACCENTS_,
			"widetilde", ImCollections.newList( //$NON-NLS-1$
					new Argument(Argument.REQUIRED, Argument.NONE)
			), "Prints wide Tilde above given text", null ); // std
	
	
}
