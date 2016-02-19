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

package de.walware.docmlet.tex.internal.ui.config;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.editors.text.EditorsUI;

import de.walware.ecommons.ltk.ui.sourceediting.SourceEditorViewerConfiguration;
import de.walware.ecommons.ltk.ui.util.CombinedPreferenceStore;
import de.walware.ecommons.preferences.ui.ConfigurationBlock;
import de.walware.ecommons.preferences.ui.ConfigurationBlockPreferencePage;
import de.walware.ecommons.text.ui.presentation.AbstractTextStylesConfigurationBlock;
import de.walware.ecommons.text.ui.settings.TextStyleManager;

import de.walware.docmlet.tex.core.TexCore;
import de.walware.docmlet.tex.core.source.LtxDocumentContentInfo;
import de.walware.docmlet.tex.core.source.LtxDocumentSetupParticipant;
import de.walware.docmlet.tex.internal.ui.TexUIPlugin;
import de.walware.docmlet.tex.ui.sourceediting.LtxSourceViewerConfiguration;
import de.walware.docmlet.tex.ui.text.ITexTextStyles;


public class LtxTextStylesPreferencePage extends ConfigurationBlockPreferencePage {
	
	
	public LtxTextStylesPreferencePage() {
		setPreferenceStore(TexUIPlugin.getInstance().getPreferenceStore());
	}
	
	
	@Override
	protected ConfigurationBlock createConfigurationBlock() throws CoreException {
		return new TexTextStylesBlock();
	}
	
}


class TexTextStylesBlock extends AbstractTextStylesConfigurationBlock {
	
	
	public TexTextStylesBlock() {
	}
	
	
	@Override
	protected String getSettingsGroup() {
		return ITexTextStyles.LTX_TEXTSTYLE_CONFIG_QUALIFIER;
	}
	
	@Override
	protected SyntaxNode[] createItems() {
		return new SyntaxNode[] {
			new CategoryNode(Messages.TextStyles_DefaultCodeCategory_label, new SyntaxNode[] {
				new StyleNode(Messages.TextStyles_Default_label, Messages.TextStyles_Default_description,
						ITexTextStyles.TS_DEFAULT, new SyntaxNode.UseStyle[] {
							SyntaxNode.createUseCustomStyle()
						}, null ),
				new StyleNode(Messages.TextStyles_ControlWord_label, Messages.TextStyles_ControlWord_description,
						ITexTextStyles.TS_CONTROL_WORD, new SyntaxNode.UseStyle[] {
							SyntaxNode.createUseCustomStyle()
						}, new StyleNode[] {
					new StyleNode(Messages.TextStyles_ControlWord_Sectioning_label, Messages.TextStyles_ControlWord_Sectioning_description,
							ITexTextStyles.TS_CONTROL_WORD_SUB_SECTIONING, new SyntaxNode.UseStyle[] {
								SyntaxNode.createUseNoExtraStyle(ITexTextStyles.TS_CONTROL_WORD),
								SyntaxNode.createUseCustomStyle(),
							}, null ),
				}),
				new StyleNode(Messages.TextStyles_ControlChar_label, Messages.TextStyles_ControlChar_description,
						ITexTextStyles.TS_CONTROL_CHAR, new SyntaxNode.UseStyle[] {
							SyntaxNode.createUseCustomStyle(),
							SyntaxNode.createUseOtherStyle(ITexTextStyles.TS_CONTROL_WORD, Messages.TextStyles_ControlWord_label)
						}, null ),
				new StyleNode(Messages.TextStyles_CurlyBracket_label, Messages.TextStyles_CurlyBracket_description,
						ITexTextStyles.TS_CURLY_BRACKETS, new SyntaxNode.UseStyle[] {
							SyntaxNode.createUseCustomStyle()
						}, null ),
				new StyleNode(Messages.TextStyles_Verbatim_label, Messages.TextStyles_Verbatim_description,
						ITexTextStyles.TS_VERBATIM, new SyntaxNode.UseStyle[] {
							SyntaxNode.createUseCustomStyle()
						}, null ),
			}),
			new CategoryNode(Messages.TextStyles_MathCodeCategory_label, new SyntaxNode[] {
				new StyleNode(Messages.TextStyles_Default_label, Messages.TextStyles_Default_description,
						ITexTextStyles.TS_MATH, new SyntaxNode.UseStyle[] {
							SyntaxNode.createUseCustomStyle()
						}, null ),
				new StyleNode(Messages.TextStyles_ControlWord_label, Messages.TextStyles_ControlWord_description,
						ITexTextStyles.TS_MATH_CONTROL_WORD, new SyntaxNode.UseStyle[] {
							SyntaxNode.createUseCustomStyle(),
							SyntaxNode.createUseOtherStyle(ITexTextStyles.TS_MATH, Messages.TextStyles_MathCodeCategory_short, Messages.TextStyles_Default_label),
							SyntaxNode.createUseOtherStyle(ITexTextStyles.TS_CONTROL_WORD, Messages.TextStyles_DefaultCodeCategory_short, Messages.TextStyles_ControlWord_label),
						}, null ),
				new StyleNode(Messages.TextStyles_ControlChar_label, Messages.TextStyles_ControlChar_description,
						ITexTextStyles.TS_MATH_CONTROL_CHAR, new SyntaxNode.UseStyle[] {
							SyntaxNode.createUseCustomStyle(),
							SyntaxNode.createUseOtherStyle(ITexTextStyles.TS_MATH_CONTROL_WORD, Messages.TextStyles_MathCodeCategory_short, Messages.TextStyles_ControlWord_label),
							SyntaxNode.createUseOtherStyle(ITexTextStyles.TS_CONTROL_CHAR, Messages.TextStyles_DefaultCodeCategory_short, Messages.TextStyles_ControlChar_label),
						}, null ),
				new StyleNode(Messages.TextStyles_CurlyBracket_label, Messages.TextStyles_CurlyBracket_description,
						ITexTextStyles.TS_MATH_CURLY_BRACKETS, new SyntaxNode.UseStyle[] {
							SyntaxNode.createUseCustomStyle(),
							SyntaxNode.createUseOtherStyle(ITexTextStyles.TS_MATH, Messages.TextStyles_MathCodeCategory_short, Messages.TextStyles_Default_label),
							SyntaxNode.createUseOtherStyle(ITexTextStyles.TS_CURLY_BRACKETS, Messages.TextStyles_DefaultCodeCategory_short, Messages.TextStyles_CurlyBracket_label),
						}, null ),
			}),
			new CategoryNode(Messages.TextStyles_CommentCategory_label, new SyntaxNode[] {
				new StyleNode(Messages.TextStyles_Comment_label, Messages.TextStyles_Comment_description,
						ITexTextStyles.TS_COMMENT, new SyntaxNode.UseStyle[] {
							SyntaxNode.createUseCustomStyle()
						}, null ),
				new StyleNode(Messages.TextStyles_TaskTag_label, Messages.TextStyles_TaskTag_description,
						ITexTextStyles.TS_TASK_TAG, new SyntaxNode.UseStyle[] {
							SyntaxNode.createUseCustomStyle()
						}, null ),
			}),
		};
	}
	
	@Override
	protected String getPreviewFileName() {
		return "LtxTextStylesPreviewCode.txt"; //$NON-NLS-1$
	}
	
	@Override
	protected IDocumentSetupParticipant getDocumentSetupParticipant() {
		return new LtxDocumentSetupParticipant();
	}
	
	@Override
	protected SourceEditorViewerConfiguration getSourceEditorViewerConfiguration(
			final IPreferenceStore preferenceStore, final TextStyleManager textStyles) {
		return new LtxSourceViewerConfiguration(LtxDocumentContentInfo.INSTANCE, null,
				TexCore.getDefaultsAccess(),
				CombinedPreferenceStore.createStore(
						preferenceStore,
						EditorsUI.getPreferenceStore() ),
				textStyles );
	}
	
}
