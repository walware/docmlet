/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.core.commands;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;


public class LtxCommandDefinitions implements IEnvDefinitions, IPreambleDefinitions,
		IDivSymbolDefinitions, IDivDocDefinitions,
		ITextStylingDefinitions, ITextSectioningDefinitions,
		IMathStylingDefinitions, IMathSymbolDefinitions,
		IRefDefinitions {
	
	
	private static Map<String, TexCommand> gWordCommandMap;
	private static ImList<TexCommand> gAllCommands;
	
	private static Map<String, TexCommand> gWordEnvMap;
	private static ImList<TexCommand> gAllEnvs;
	
	
	static final void add(final Map<String, TexCommand> map, final TexCommand command) {
		assert (!map.containsKey(command.getControlWord())) : command.getControlWord();
		map.put(command.getControlWord(), command);
	}
	
	private static List<TexCommand> createCommandList(final Map<String, TexCommand> map, final List<String> words) {
		final TexCommand[] commands = new TexCommand[words.size()];
		final int i = 0;
		for (final String word : words) {
			final TexCommand command = map.get(word);
			if (command != null) {
				commands[i] = command;
			}
		}
		return ImCollections.newList(commands, 0, i);
	}
	
	static {
		{	final LinkedHashMap<String, TexCommand> map = new LinkedHashMap<>();
			
			add(map, ENV_document_BEGIN);
			
			add(map, ENV_math_BEGIN);
			add(map, ENV_displaymath_BEGIN);
			add(map, ENV_equation_BEGIN);
			add(map, ENV_equationA_BEGIN);
			add(map, ENV_eqnarray_BEGIN);
			add(map, ENV_eqnarrayA_BEGIN);
			add(map, ENV_multiline_BEGIN);
			add(map, ENV_multilineA_BEGIN);
			add(map, ENV_gather_BEGIN);
			add(map, ENV_gatherA_BEGIN);
			add(map, ENV_align_BEGIN);
			add(map, ENV_alignA_BEGIN);
			add(map, ENV_alignat_BEGIN);
			add(map, ENV_alignatA_BEGIN);
			
			add(map, ENV_verbatim_BEGIN);
			add(map, ENV_verbatimA_BEGIN);
			add(map, ENV_lstlisting_BEGIN);
			
			add(map, ENV_Sinput_BEGIN);
			add(map, ENV_Souput_BEGIN);
			
			add(map, ENV_comment_BEGIN);
			
			add(map, ENV_quote_BEGIN);
			add(map, ENV_quotation_BEGIN);
			add(map, ENV_verse_BEGIN);
			
			add(map, ENV_enumerate_BEGIN);
			add(map, ENV_itemize_BEGIN);
			add(map, ENV_description_BEGIN);
			
			add(map, ENV_tabular_BEGIN);
			add(map, ENV_tabbing_BEGIN);
			
			add(map, ENV_picture_BEGIN);
			
			add(map, ENV_table_BEGIN);
			add(map, ENV_figure_BEGIN);
			
			add(map, ENV_center_BEGIN);
			add(map, ENV_flushleft_BEGIN);
			add(map, ENV_flushright_BEGIN);
			
			add(map, ENV_thebibliography_BEGIN);
			
			add(map, ENV_array_BEGIN);
			add(map, ENV_matrix_BEGIN);
			add(map, ENV_smallmatrix_BEGIN);
			add(map, ENV_pmatrix_BEGIN);
			add(map, ENV_bmatrix_BEGIN);
			add(map, ENV_Bmatrix_BEGIN);
			add(map, ENV_vmatrix_BEGIN);
			add(map, ENV_Vmatrix_BEGIN);
			
			gWordEnvMap = map;
			gAllEnvs= ImCollections.toList(map.values());
		}
		{	
			final LinkedHashMap<String, TexCommand> map = new LinkedHashMap<>();
			
			add(map, PREAMBLE_documentclass_COMMAND);
			add(map, PREAMBLE_usepackage_COMMAND);
			add(map, PREAMBLE_title_COMMAND);
			add(map, PREAMBLE_author_COMMAND);
			add(map, PREAMBLE_date_COMMAND);
			
			add(map, PREAMBLE_newcommand_COMMAND);
			add(map, PREAMBLE_renewcommand_COMMAND);
			add(map, PREAMBLE_providecommand_COMMAND);
			add(map, PREAMBLE_newenvironment_COMMAND);
			add(map, PREAMBLE_renewenvironment_COMMAND);
			add(map, PREAMBLE_ensuremath_COMMAND);
			
			add(map, PREAMBLE_insertonly_COMMAND);
			add(map, PREAMBLE_hyphenation_COMMAND);
			
			add(map, DOCUMENT_input_COMMAND);
			add(map, DOCUMENT_insert_COMMAND);
			
			add(map, DOCUMENT_includegraphics_COMMAND);
			add(map, DOCUMENT_item_COMMAND);
			add(map, DOCUMENT_hline_COMMAND);
			
			add(map, DOCUMENT_maketitle_COMMAND);
			add(map, DOCUMENT_addcontentsline_COMMAND);
			add(map, DOCUMENT_caption_COMMAND);
			add(map, DOCUMENT_tableofcontents_COMMAND);
			add(map, DOCUMENT_listoffigures_COMMAND);
			add(map, DOCUMENT_listoftables_COMMAND);
			
			add(map, DOCUMENT_index_COMMAND);
			add(map, DOCUMENT_printindex_COMMAND);
			
			add(map, DOCUMENT_hspace_COMMAND);
			add(map, DOCUMENT_vspace_COMMAND);
			
			add(map, SECTIONING_part_COMMAND);
			add(map, SECTIONING_chapter_COMMAND);
			add(map, SECTIONING_section_COMMAND);
			add(map, SECTIONING_subsection_COMMAND);
			add(map, SECTIONING_subsubsection_COMMAND);
			add(map, SECTIONING_paragraph_COMMAND);
			add(map, SECTIONING_subparagraph_COMMAND);
			
			add(map, LABEL_label_COMMAND);
			add(map, LABEL_ref_COMMAND);
			add(map, LABEL_pageref_COMMAND);
			add(map, LABEL_eqref_COMMAND);
			
			add(map, LABEL_newcounter_COMMAND);
			add(map, LABEL_setcounter_COMMAND);
			add(map, LABEL_addtocounter_COMMAND);
			add(map, LABEL_stepcounter_COMMAND);
			add(map, LABEL_Alph_COMMAND);
			add(map, LABEL_alph_COMMAND);
			add(map, LABEL_Roman_COMMAND);
			add(map, LABEL_roman_COMMAND);
			add(map, LABEL_arabic_COMMAND);
			add(map, LABEL_value_COMMAND);
			
			add(map, COMMONSYMBOL_CURLYOPEN_COMMAND);
			add(map, COMMONSYMBOL_CURLYCLOSE_COMMAND);
			add(map, COMMONSYMBOL_NUMBERSIGN_COMMAND);
			add(map, COMMONSYMBOL_PERCENTSIGN_COMMAND);
			
			add(map, COMMONSYMBOL_S_COMMAND);
			add(map, COMMONSYMBOL_dag_COMMAND);
			add(map, COMMONSYMBOL_ddag_COMMAND);
			add(map, COMMONSYMBOL_backslash_COMMAND);
			add(map, COMMONSYMBOL_textbullet_COMMAND);
			add(map, COMMONSYMBOL_textperiodcentered_COMMAND);
			add(map, COMMONSYMBOL_dots_COMMAND);
			add(map, COMMONSYMBOL_ldots_COMMAND);
			add(map, COMMONSYMBOL_textbackslash_COMMAND);
			add(map, COMMONSYMBOL_textasciicircum_COMMAND);
			add(map, COMMONSYMBOL_textasciitilde_COMMAND);
			add(map, COMMONSYMBOL_tex_COMMAND);
			add(map, COMMONSYMBOL_latex_COMMAND);
			
			add(map, COMMONSYMBOL_ss_COMMAND);
			add(map, COMMONSYMBOL_SS_COMMAND);
			
			add(map, GENERICENV_begin_COMMAND);
			add(map, GENERICENV_end_COMMAND);
			
			add(map, VERBATIM_verb_COMMAND);
			
			add(map, COMMONFONTS_rm_COMMAND);
			add(map, COMMONFONTS_sl_COMMAND);
			add(map, COMMONFONTS_tt_COMMAND);
			add(map, COMMONFONTS_it_COMMAND);
			add(map, COMMONFONTS_bf_COMMAND);
			
			add(map, COMMONFONTS_rmfamily_COMMAND);
			add(map, COMMONFONTS_sffamily_COMMAND);
			add(map, COMMONFONTS_ttfamily_COMMAND);
			add(map, COMMONFONTS_mdseries_COMMAND);
			add(map, COMMONFONTS_bfseries_COMMAND);
			add(map, COMMONFONTS_upshape_COMMAND);
			add(map, COMMONFONTS_itshape_COMMAND);
			add(map, COMMONFONTS_slshape_COMMAND);
			add(map, COMMONFONTS_scshape_COMMAND);
			add(map, COMMONFONTS_em_COMMAND);
			
			add(map, COMMONFONTS_textrm_COMMAND);
			add(map, COMMONFONTS_textsf_COMMAND);
			add(map, COMMONFONTS_texttt_COMMAND);
			add(map, COMMONFONTS_textmd_COMMAND);
			add(map, COMMONFONTS_textbf_COMMAND);
			add(map, COMMONFONTS_textup_COMMAND);
			add(map, COMMONFONTS_textit_COMMAND);
			add(map, COMMONFONTS_textsl_COMMAND);
			add(map, COMMONFONTS_textsc_COMMAND);
			add(map, COMMONFONTS_emph_COMMAND);
			
			add(map, COMMONFONTS_underline_COMMAND);
			
			add(map, COMMONFONTS_tiny_COMMAND);
			add(map, COMMONFONTS_scriptsize_COMMAND);
			add(map, COMMONFONTS_small_COMMAND);
			add(map, COMMONFONTS_normalsize_COMMAND);
			add(map, COMMONFONTS_large_COMMAND);
			add(map, COMMONFONTS_Large_COMMAND);
			add(map, COMMONFONTS_LARGE_COMMAND);
			add(map, COMMONFONTS_huge_COMMAND);
			add(map, COMMONFONTS_Huge_COMMAND);
			
			// style
			add(map, STYLE_mathnormal_COMMAND);
			add(map, STYLE_mathrm_COMMAND);
			add(map, STYLE_mathsf_COMMAND);
			add(map, STYLE_mathtt_COMMAND);
			add(map, STYLE_mathcal_COMMAND);
			add(map, STYLE_mathbf_COMMAND);
			add(map, STYLE_mathit_COMMAND);
			
			//
			add(map, MISC_nonumber_COMMAND);
			
			// greek
			add(map, MATHSYMBOL_Alpha_COMMAND);
			add(map, MATHSYMBOL_alpha_COMMAND);
			add(map, MATHSYMBOL_Beta_COMMAND);
			add(map, MATHSYMBOL_beta_COMMAND);
			add(map, MATHSYMBOL_Gamma_COMMAND);
			add(map, MATHSYMBOL_gamma_COMMAND);
			add(map, MATHSYMBOL_Delta_COMMAND);
			add(map, MATHSYMBOL_delta_COMMAND);
			add(map, MATHSYMBOL_Epsilon_COMMAND);
			add(map, MATHSYMBOL_epsilon_COMMAND);
			add(map, MATHSYMBOL_varepsilon_COMMAND);
			add(map, MATHSYMBOL_Zeta_COMMAND);
			add(map, MATHSYMBOL_zeta_COMMAND);
			add(map, MATHSYMBOL_Eta_COMMAND);
			add(map, MATHSYMBOL_eta_COMMAND);
			add(map, MATHSYMBOL_Theta_COMMAND);
			add(map, MATHSYMBOL_theta_COMMAND);
			add(map, MATHSYMBOL_vartheta_COMMAND);
			add(map, MATHSYMBOL_Iota_COMMAND);
			add(map, MATHSYMBOL_iota_COMMAND);
			add(map, MATHSYMBOL_Kappa_COMMAND);
			add(map, MATHSYMBOL_kappa_COMMAND);
			add(map, MATHSYMBOL_varkappa_COMMAND);
			add(map, MATHSYMBOL_Lambda_COMMAND);
			add(map, MATHSYMBOL_lambda_COMMAND);
			add(map, MATHSYMBOL_Mu_COMMAND);
			add(map, MATHSYMBOL_mu_COMMAND);
			add(map, MATHSYMBOL_Nu_COMMAND);
			add(map, MATHSYMBOL_nu_COMMAND);
			add(map, MATHSYMBOL_Xi_COMMAND);
			add(map, MATHSYMBOL_xi_COMMAND);
			add(map, MATHSYMBOL_Omicron_COMMAND);
			add(map, MATHSYMBOL_omicron_COMMAND);
			add(map, MATHSYMBOL_Pi_COMMAND);
			add(map, MATHSYMBOL_pi_COMMAND);
			add(map, MATHSYMBOL_varpi_COMMAND);
			add(map, MATHSYMBOL_Rho_COMMAND);
			add(map, MATHSYMBOL_rho_COMMAND);
			add(map, MATHSYMBOL_varrho_COMMAND);
			add(map, MATHSYMBOL_Sigma_COMMAND);
			add(map, MATHSYMBOL_sigma_COMMAND);
			add(map, MATHSYMBOL_varsigma_COMMAND);
			add(map, MATHSYMBOL_Tau_COMMAND);
			add(map, MATHSYMBOL_tau_COMMAND);
			add(map, MATHSYMBOL_Upsilon_COMMAND);
			add(map, MATHSYMBOL_upsilon_COMMAND);
			add(map, MATHSYMBOL_Phi_COMMAND);
			add(map, MATHSYMBOL_phi_COMMAND);
			add(map, MATHSYMBOL_varphi_COMMAND);
			add(map, MATHSYMBOL_Chi_COMMAND);
			add(map, MATHSYMBOL_chi_COMMAND);
			add(map, MATHSYMBOL_Psi_COMMAND);
			add(map, MATHSYMBOL_psi_COMMAND);
			add(map, MATHSYMBOL_Omega_COMMAND);
			add(map, MATHSYMBOL_omega_COMMAND);
			
			// op bin
			add(map, MATHSYMBOL_pm_COMMAND);
			add(map, MATHSYMBOL_mp_COMMAND);
			add(map, MATHSYMBOL_setminus_COMMAND);
			add(map, MATHSYMBOL_cdot_COMMAND);
			add(map, MATHSYMBOL_times_COMMAND);
			add(map, MATHSYMBOL_ast_COMMAND);
			add(map, MATHSYMBOL_star_COMMAND);
			add(map, MATHSYMBOL_diamond_COMMAND);
			add(map, MATHSYMBOL_circ_COMMAND);
			add(map, MATHSYMBOL_bullet_COMMAND);
			add(map, MATHSYMBOL_div_COMMAND);
			add(map, MATHSYMBOL_cap_COMMAND);
			add(map, MATHSYMBOL_cup_COMMAND);
			add(map, MATHSYMBOL_uplus_COMMAND);
			add(map, MATHSYMBOL_sqcap_COMMAND);
			add(map, MATHSYMBOL_sqcup_COMMAND);
			add(map, MATHSYMBOL_triangleleft_COMMAND);
			add(map, MATHSYMBOL_triangleright_COMMAND);
			add(map, MATHSYMBOL_wr_COMMAND);
			add(map, MATHSYMBOL_wedge_COMMAND);
			add(map, MATHSYMBOL_land_COMMAND);
			add(map, MATHSYMBOL_vee_COMMAND);
			add(map, MATHSYMBOL_lor_COMMAND);
			add(map, MATHSYMBOL_oplus_COMMAND);
			add(map, MATHSYMBOL_ominus_COMMAND);
			add(map, MATHSYMBOL_otimes_COMMAND);
			add(map, MATHSYMBOL_oslash_COMMAND);
			add(map, MATHSYMBOL_odot_COMMAND);
			add(map, MATHSYMBOL_dagger_COMMAND);
			add(map, MATHSYMBOL_ddagger_COMMAND);
			add(map, MATHSYMBOL_amalg_COMMAND);
			
			// op root, frac, ...
			add(map, MATHSYMBOL_sqrt_COMMAND);
			add(map, MATHSYMBOL_frac_COMMAND);
			add(map, MATHSYMBOL_dfrac_COMMAND);
			add(map, MATHSYMBOL_tfrac_COMMAND);
			
			// op rel std
			add(map, MATHSYMBOL_equiv_COMMAND);
			add(map, MATHSYMBOL_sim_COMMAND);
			add(map, MATHSYMBOL_simeq_COMMAND);
			add(map, MATHSYMBOL_asymp_COMMAND);
			add(map, MATHSYMBOL_approx_COMMAND);
			add(map, MATHSYMBOL_cong_COMMAND);
			add(map, MATHSYMBOL_leq_COMMAND);
			add(map, MATHSYMBOL_geq_COMMAND);
			add(map, MATHSYMBOL_ll_COMMAND);
			add(map, MATHSYMBOL_gg_COMMAND);
			add(map, MATHSYMBOL_prec_COMMAND);
			add(map, MATHSYMBOL_succ_COMMAND);
			add(map, MATHSYMBOL_succeq_COMMAND);
			add(map, MATHSYMBOL_preceq_COMMAND);
			add(map, MATHSYMBOL_subset_COMMAND);
			add(map, MATHSYMBOL_supset_COMMAND);
			add(map, MATHSYMBOL_subseteq_COMMAND);
			add(map, MATHSYMBOL_supseteq_COMMAND);
			add(map, MATHSYMBOL_sqsubset_COMMAND);
			add(map, MATHSYMBOL_sqsupset_COMMAND);
			add(map, MATHSYMBOL_sqsubseteq_COMMAND);
			add(map, MATHSYMBOL_sqsupseteq_COMMAND);
			add(map, MATHSYMBOL_bowtie_COMMAND);
			add(map, MATHSYMBOL_in_COMMAND);
			add(map, MATHSYMBOL_ni_COMMAND);
			add(map, MATHSYMBOL_leqq_COMMAND);
			add(map, MATHSYMBOL_geqq_COMMAND);
			add(map, MATHSYMBOL_leqslant_COMMAND);
			add(map, MATHSYMBOL_geqslant_COMMAND);
			add(map, MATHSYMBOL_eqslantless_COMMAND);
			add(map, MATHSYMBOL_eqslantgtr_COMMAND);
			add(map, MATHSYMBOL_lesssim_COMMAND);
			add(map, MATHSYMBOL_gtrsim_COMMAND);
			add(map, MATHSYMBOL_lessapprox_COMMAND);
			add(map, MATHSYMBOL_gtrapprox_COMMAND);
			add(map, MATHSYMBOL_approxeq_COMMAND);
			add(map, MATHSYMBOL_eqsim_COMMAND);
			add(map, MATHSYMBOL_lessdot_COMMAND);
			add(map, MATHSYMBOL_gtrdot_COMMAND);
			add(map, MATHSYMBOL_llless_COMMAND);
			add(map, MATHSYMBOL_gggtr_COMMAND);
			add(map, MATHSYMBOL_lessgtr_COMMAND);
			add(map, MATHSYMBOL_gtrless_COMMAND);
			add(map, MATHSYMBOL_lesseqgtr_COMMAND);
			add(map, MATHSYMBOL_gtreqless_COMMAND);
			
			add(map, MATHSYMBOL_propto_COMMAND);
			add(map, MATHSYMBOL_mid_COMMAND);
			add(map, MATHSYMBOL_vdash_COMMAND);
			add(map, MATHSYMBOL_dashv_COMMAND);
			add(map, MATHSYMBOL_models_COMMAND);
			add(map, MATHSYMBOL_vDash_COMMAND);
			add(map, MATHSYMBOL_Vdash_COMMAND);
			add(map, MATHSYMBOL_Vvdash_COMMAND);
			add(map, MATHSYMBOL_vartriangleleft_COMMAND);
			add(map, MATHSYMBOL_vartriangleright_COMMAND);
			add(map, MATHSYMBOL_trianglelefteq_COMMAND);
			add(map, MATHSYMBOL_trianglerighteq_COMMAND);
			add(map, MATHSYMBOL_parallel_COMMAND);
			add(map, MATHSYMBOL_perp_COMMAND);
			add(map, MATHSYMBOL_frown_COMMAND);
			add(map, MATHSYMBOL_smile_COMMAND);
			
			// op rel arrow
			add(map, MATHSYMBOL_leftarrow_COMMAND);
			add(map, MATHSYMBOL_rightarrow_COMMAND);
			add(map, MATHSYMBOL_uparrow_COMMAND);
			add(map, MATHSYMBOL_downarrow_COMMAND);
			add(map, MATHSYMBOL_leftrightarrow_COMMAND);
			add(map, MATHSYMBOL_updownarrow_COMMAND);
			add(map, MATHSYMBOL_nwarrow_COMMAND);
			add(map, MATHSYMBOL_nearrow_COMMAND);
			add(map, MATHSYMBOL_searrow_COMMAND);
			add(map, MATHSYMBOL_swarrow_COMMAND);
			add(map, MATHSYMBOL_Leftarrow_COMMAND);
			add(map, MATHSYMBOL_Rightarrow_COMMAND);
			add(map, MATHSYMBOL_Uparrow_COMMAND);
			add(map, MATHSYMBOL_Downarrow_COMMAND);
			add(map, MATHSYMBOL_Leftrightarrow_COMMAND);
			add(map, MATHSYMBOL_Updownarrow_COMMAND);
			add(map, MATHSYMBOL_longleftarrow_COMMAND);
			add(map, MATHSYMBOL_longrightarrow_COMMAND);
			add(map, MATHSYMBOL_longleftrightarrow_COMMAND);
			add(map, MATHSYMBOL_Longleftarrow_COMMAND);
			add(map, MATHSYMBOL_Longrightarrow_COMMAND);
			add(map, MATHSYMBOL_Longleftrightarrow_COMMAND);
			add(map, MATHSYMBOL_mapsto_COMMAND);
			add(map, MATHSYMBOL_longmapsto_COMMAND);
			add(map, MATHSYMBOL_hookleftarrow_COMMAND);
			add(map, MATHSYMBOL_hookrightarrow_COMMAND);
			add(map, MATHSYMBOL_leftharpoonup_COMMAND);
			add(map, MATHSYMBOL_leftharpoondown_COMMAND);
			add(map, MATHSYMBOL_rightharpoonup_COMMAND);
			add(map, MATHSYMBOL_rightharpoondown_COMMAND);
			add(map, MATHSYMBOL_rightleftharpoons_COMMAND);
			
			// op large
			add(map, MATHSYMBOL_sum_COMMAND);
			add(map, MATHSYMBOL_prod_COMMAND);
			add(map, MATHSYMBOL_coprod_COMMAND);
			add(map, MATHSYMBOL_int_COMMAND);
			add(map, MATHSYMBOL_oint_COMMAND);
			add(map, MATHSYMBOL_bigcap_COMMAND);
			add(map, MATHSYMBOL_bigcup_COMMAND);
			add(map, MATHSYMBOL_bigsqcup_COMMAND);
			add(map, MATHSYMBOL_bigwedge_COMMAND);
			add(map, MATHSYMBOL_bigvee_COMMAND);
			add(map, MATHSYMBOL_bigodot_COMMAND);
			add(map, MATHSYMBOL_bigoplus_COMMAND);
			add(map, MATHSYMBOL_bigotimes_COMMAND);
			add(map, MATHSYMBOL_biguplus_COMMAND);
			
			// op fun
			add(map, MATHSYMBOL_exp_COMMAND);
			add(map, MATHSYMBOL_log_COMMAND);
			add(map, MATHSYMBOL_ln_COMMAND);
			add(map, MATHSYMBOL_lg_COMMAND);
			add(map, MATHSYMBOL_arg_COMMAND);
			
			add(map, MATHSYMBOL_sin_COMMAND);
			add(map, MATHSYMBOL_cos_COMMAND);
			add(map, MATHSYMBOL_tan_COMMAND);
			add(map, MATHSYMBOL_cot_COMMAND);
			add(map, MATHSYMBOL_sec_COMMAND);
			add(map, MATHSYMBOL_csc_COMMAND);
			add(map, MATHSYMBOL_arcsin_COMMAND);
			add(map, MATHSYMBOL_arccos_COMMAND);
			add(map, MATHSYMBOL_arctan_COMMAND);
			add(map, MATHSYMBOL_sinh_COMMAND);
			add(map, MATHSYMBOL_cosh_COMMAND);
			add(map, MATHSYMBOL_tanh_COMMAND);
			add(map, MATHSYMBOL_coth_COMMAND);
			
			add(map, MATHSYMBOL_min_COMMAND);
			add(map, MATHSYMBOL_max_COMMAND);
			add(map, MATHSYMBOL_inf_COMMAND);
			add(map, MATHSYMBOL_sup_COMMAND);
			add(map, MATHSYMBOL_liminf_COMMAND);
			add(map, MATHSYMBOL_limsup_COMMAND);
			add(map, MATHSYMBOL_lim_COMMAND);
			
			add(map, MATHSYMBOL_dim_COMMAND);
			add(map, MATHSYMBOL_det_COMMAND);
			add(map, MATHSYMBOL_ker_COMMAND);
			add(map, MATHSYMBOL_hom_COMMAND);
			add(map, MATHSYMBOL_deg_COMMAND);
			
			add(map, MATHSYMBOL_gcd_COMMAND);
			add(map, MATHSYMBOL_Pr_COMMAND);
			
			// misc alpha
			add(map, MATHSYMBOL_aleph_COMMAND);
			add(map, MATHSYMBOL_beth_COMMAND);
			add(map, MATHSYMBOL_gimel_COMMAND);
			add(map, MATHSYMBOL_daleth_COMMAND);
			add(map, MATHSYMBOL_imath_COMMAND);
			add(map, MATHSYMBOL_jmath_COMMAND);
			add(map, MATHSYMBOL_complement_COMMAND);
			add(map, MATHSYMBOL_ell_COMMAND);
			add(map, MATHSYMBOL_eth_COMMAND);
			add(map, MATHSYMBOL_hbar_COMMAND);
			add(map, MATHSYMBOL_hslash_COMMAND);
			add(map, MATHSYMBOL_mho_COMMAND);
			add(map, MATHSYMBOL_partial_COMMAND);
			add(map, MATHSYMBOL_wp_COMMAND);
			add(map, MATHSYMBOL_Re_COMMAND);
			add(map, MATHSYMBOL_Im_COMMAND);
			add(map, MATHSYMBOL_Finv_COMMAND);
			
			// misc ord
			add(map, MATHSYMBOL_prime_COMMAND);
			add(map, MATHSYMBOL_backprime_COMMAND);
			add(map, MATHSYMBOL_infty_COMMAND);
			add(map, MATHSYMBOL_emptyset_COMMAND);
			add(map, MATHSYMBOL_varnothing_COMMAND);
			add(map, MATHSYMBOL_nabla_COMMAND);
			add(map, MATHSYMBOL_surd_COMMAND);
			add(map, MATHSYMBOL_top_COMMAND);
			add(map, MATHSYMBOL_bot_COMMAND);
			add(map, MATHSYMBOL_angle_COMMAND);
			add(map, MATHSYMBOL_measuredangle_COMMAND);
			add(map, MATHSYMBOL_sphericalangle_COMMAND);
			add(map, MATHSYMBOL_blacktriangle_COMMAND);
			add(map, MATHSYMBOL_triangle_COMMAND);
			add(map, MATHSYMBOL_blacktriangledown_COMMAND);
			add(map, MATHSYMBOL_triangledown_COMMAND);
			add(map, MATHSYMBOL_blacksquare_COMMAND);
			add(map, MATHSYMBOL_square_COMMAND);
			add(map, MATHSYMBOL_blacklozenge_COMMAND);
			add(map, MATHSYMBOL_lozenge_COMMAND);
			add(map, MATHSYMBOL_forall_COMMAND);
			add(map, MATHSYMBOL_exists_COMMAND);
			add(map, MATHSYMBOL_nexists_COMMAND);
			add(map, MATHSYMBOL_neg_COMMAND);
			add(map, MATHSYMBOL_flat_COMMAND);
			add(map, MATHSYMBOL_natural_COMMAND);
			add(map, MATHSYMBOL_sharp_COMMAND);
			add(map, MATHSYMBOL_spadesuit_COMMAND);
			add(map, MATHSYMBOL_heartsuit_COMMAND);
			add(map, MATHSYMBOL_diamondsuit_COMMAND);
			add(map, MATHSYMBOL_clubsuit_COMMAND);
			
			// dots
			add(map, MATHSYMBOL_cdots_COMMAND);
			add(map, MATHSYMBOL_dotsc_COMMAND);
			add(map, MATHSYMBOL_dotsb_COMMAND);
			add(map, MATHSYMBOL_dotsm_COMMAND);
			add(map, MATHSYMBOL_dotsi_COMMAND);
			add(map, MATHSYMBOL_dotso_COMMAND);
			add(map, MATHSYMBOL_vdots_COMMAND);
			add(map, MATHSYMBOL_adots_COMMAND);
			add(map, MATHSYMBOL_ddots_COMMAND);
			
			// delim brackets
			add(map, MATHSYMBOL_lbrack_COMMAND);
			add(map, MATHSYMBOL_rbrack_COMMAND);
			add(map, MATHSYMBOL_lceil_COMMAND);
			add(map, MATHSYMBOL_rceil_COMMAND);
			add(map, MATHSYMBOL_lfloor_COMMAND);
			add(map, MATHSYMBOL_rfloor_COMMAND);
			add(map, MATHSYMBOL_lbrace_COMMAND);
			add(map, MATHSYMBOL_rbrace_COMMAND);
			add(map, MATHSYMBOL_langle_COMMAND);
			add(map, MATHSYMBOL_rangle_COMMAND);
			
			// accents
			add(map, MATHSYMBOL_grave_COMMAND);
			add(map, MATHSYMBOL_acute_COMMAND);
			add(map, MATHSYMBOL_hat_COMMAND);
			add(map, MATHSYMBOL_tilde_COMMAND);
			add(map, MATHSYMBOL_bar_COMMAND);
			add(map, MATHSYMBOL_overline_COMMAND);
			add(map, MATHSYMBOL_breve_COMMAND);
			add(map, MATHSYMBOL_check_COMMAND);
			add(map, MATHSYMBOL_dot_COMMAND);
			add(map, MATHSYMBOL_ddot_COMMAND);
			add(map, MATHSYMBOL_dddot_COMMAND);
			add(map, MATHSYMBOL_vec_COMMAND);
			add(map, MATHSYMBOL_widehat_COMMAND);
			add(map, MATHSYMBOL_widetilde_COMMAND);
			
			add(map, BIB_bibitem_COMMAND);
			add(map, BIB_cite_COMMAND);
			add(map, BIB_nocite_COMMAND);
			add(map, BIB_bibliography_COMMAND);
			
			add(map, BIB_bibliographystyle_COMMAND);
			
			gWordCommandMap = map;
			gAllCommands= ImCollections.toList(gWordCommandMap.values());
			
//			System.out.println(gAllEnvs.size());
//			System.out.println(gAllCommands.size());
//			TexCommand[] array = gAllCommands.toArray(new TexCommand[gAllCommands.size()]);
//			Arrays.sort(array);
//			StringBuilder all = new StringBuilder("all=");
//			StringBuilder text = new StringBuilder("text=");
//			StringBuilder math = new StringBuilder("math=");
//			for (int i = 0; i < array.length; i++) {
//				all.append(array[i].getControlWord());
//				all.append(',');
//				
//				if ((array[i].getType() & TexCommand.MASK_MAIN) == TexCommand.MATHSYMBOL
//						|| (array[i].getType() & TexCommand.MASK_C2) == TexCommand.C2_STYLE_MATH
//						) {
//					math.append(array[i].getControlWord());
//					math.append(',');
//				}
//				else if ((array[i].getType() & TexCommand.MASK_C2) == TexCommand.C2_SYMBOL_COMMON
//						|| (array[i].getType() & TexCommand.MASK_MAIN) == TexCommand.C2_GENERICENV
//						|| (array[i].getType() & TexCommand.MASK_C2) == TexCommand.C2_LABEL_DEF
//						|| (array[i].getType() & TexCommand.MASK_C2) == TexCommand.C2_LABEL_REF
//						) {
//					math.append(array[i].getControlWord());
//					math.append(',');
//					text.append(array[i].getControlWord());
//					text.append(',');
//				}
//				else {
//					text.append(array[i].getControlWord());
//					text.append(',');
//				}
//			}
//			System.out.println(all.substring(0, all.length()-1));
//			System.out.println(text.substring(0, text.length()-1));
//			System.out.println(math.substring(0, math.length()-1));
		}
	}
	
	
	public static List<TexCommand> getAllCommands() {
		return gAllCommands;
	}
	
	public static TexCommand getCommand(final String controlWord) {
		return gWordCommandMap.get(controlWord);
	}
	
	public static List<TexCommand> getCommands(final List<String> controlWords) {
		return createCommandList(gWordCommandMap, controlWords);
	}
	
	
	public static List<TexCommand> getAllEnvs() {
		return gAllEnvs;
	}
	
	public static TexCommand getEnv(final String controlWord) {
		return gWordEnvMap.get(controlWord);
	}
	
	public static List<TexCommand> getEnvs(final List<String> names) {
		return createCommandList(gWordCommandMap, names);
	}
	
}
