/*******************************************************************************
 * Copyright (c) 2009-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.internal.ui.preferences;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.editors.text.EditorsUI;

import de.walware.ecommons.ltk.ui.sourceediting.SourceEditorViewerConfiguration;
import de.walware.ecommons.ltk.ui.util.CombinedPreferenceStore;
import de.walware.ecommons.preferences.ui.ConfigurationBlock;
import de.walware.ecommons.preferences.ui.ConfigurationBlockPreferencePage;
import de.walware.ecommons.text.ui.presentation.AbstractTextStylesConfigurationBlock;
import de.walware.ecommons.ui.ColorManager;

import de.walware.docmlet.tex.core.TexCore;
import de.walware.docmlet.tex.internal.ui.TexUIPlugin;
import de.walware.docmlet.tex.ui.sourceediting.LtxViewerConfiguration;
import de.walware.docmlet.tex.ui.text.ITexTextStyles;
import de.walware.docmlet.tex.ui.text.LtxDocumentSetupParticipant;


public class LtxTextStylesPreferencePage extends ConfigurationBlockPreferencePage<ConfigurationBlock> {
	
	
	public LtxTextStylesPreferencePage() {
		setPreferenceStore(TexUIPlugin.getDefault().getPreferenceStore());
	}
	
	
	@Override
	protected TexTextStylesBlock createConfigurationBlock() throws CoreException {
		return new TexTextStylesBlock();
	}
	
}

class TexTextStylesBlock extends AbstractTextStylesConfigurationBlock {
	
	
	public TexTextStylesBlock() {
	}
	
	
	@Override
	protected String[] getSettingsGroups() {
		return new String[] { ITexTextStyles.GROUP_ID };
	}
	
	@Override
	protected SyntaxNode[] createItems() {
		return new SyntaxNode[] {
				new CategoryNode(Messages.TexTextStyles_DefaultCodeCategory_label, new SyntaxNode[] {
					new StyleNode(Messages.TexTextStyles_Default_label, Messages.TexTextStyles_Default_description,
							ITexTextStyles.TS_DEFAULT, new SyntaxNode.UseStyle[] {
								SyntaxNode.createUseCustomStyle()
							}, null ),
					new StyleNode(Messages.TexTextStyles_ControlWord_label, Messages.TexTextStyles_ControlWord_description,
							ITexTextStyles.TS_CONTROL_WORD, new SyntaxNode.UseStyle[] {
								SyntaxNode.createUseCustomStyle()
							}, new StyleNode[] {
						new StyleNode(Messages.TexTextStyles_ControlWord_Sectioning_label, Messages.TexTextStyles_ControlWord_Sectioning_description,
								ITexTextStyles.TS_CONTROL_WORD_SUB_SECTIONING, new SyntaxNode.UseStyle[] {
									SyntaxNode.createUseNoExtraStyle(ITexTextStyles.TS_CONTROL_WORD),
									SyntaxNode.createUseCustomStyle(),
								}, null ),
					}),
					new StyleNode(Messages.TexTextStyles_ControlChar_label, Messages.TexTextStyles_ControlChar_description,
							ITexTextStyles.TS_CONTROL_CHAR, new SyntaxNode.UseStyle[] {
								SyntaxNode.createUseCustomStyle(),
								SyntaxNode.createUseOtherStyle(ITexTextStyles.TS_CONTROL_WORD, Messages.TexTextStyles_ControlWord_label)
							}, null ),
					new StyleNode(Messages.TexTextStyles_CurlyBracket_label, Messages.TexTextStyles_CurlyBracket_description,
							ITexTextStyles.TS_CURLY_BRACKETS, new SyntaxNode.UseStyle[] {
								SyntaxNode.createUseCustomStyle()
							}, null ),
					new StyleNode(Messages.TexTextStyles_Verbatim_label, Messages.TexTextStyles_Verbatim_description,
							ITexTextStyles.TS_VERBATIM, new SyntaxNode.UseStyle[] {
								SyntaxNode.createUseCustomStyle()
							}, null ),
				}),
				new CategoryNode(Messages.TexTextStyles_MathCodeCategory_label, new SyntaxNode[] {
						new StyleNode(Messages.TexTextStyles_Default_label, Messages.TexTextStyles_Default_description,
								ITexTextStyles.TS_MATH, new SyntaxNode.UseStyle[] {
									SyntaxNode.createUseCustomStyle()
								}, null ),
						new StyleNode(Messages.TexTextStyles_ControlWord_label, Messages.TexTextStyles_ControlWord_description,
								ITexTextStyles.TS_MATH_CONTROL_WORD, new SyntaxNode.UseStyle[] {
									SyntaxNode.createUseCustomStyle(),
									SyntaxNode.createUseOtherStyle(ITexTextStyles.TS_MATH, Messages.TexTextStyles_MathCodeCategory_short, Messages.TexTextStyles_Default_label),
									SyntaxNode.createUseOtherStyle(ITexTextStyles.TS_CONTROL_WORD, Messages.TexTextStyles_DefaultCodeCategory_short, Messages.TexTextStyles_ControlWord_label),
								}, null ),
						new StyleNode(Messages.TexTextStyles_ControlChar_label, Messages.TexTextStyles_ControlChar_description,
								ITexTextStyles.TS_MATH_CONTROL_CHAR, new SyntaxNode.UseStyle[] {
									SyntaxNode.createUseCustomStyle(),
									SyntaxNode.createUseOtherStyle(ITexTextStyles.TS_MATH_CONTROL_WORD, Messages.TexTextStyles_MathCodeCategory_short, Messages.TexTextStyles_ControlWord_label),
									SyntaxNode.createUseOtherStyle(ITexTextStyles.TS_CONTROL_CHAR, Messages.TexTextStyles_DefaultCodeCategory_short, Messages.TexTextStyles_ControlChar_label),
								}, null ),
						new StyleNode(Messages.TexTextStyles_CurlyBracket_label, Messages.TexTextStyles_CurlyBracket_description,
								ITexTextStyles.TS_MATH_CURLY_BRACKETS, new SyntaxNode.UseStyle[] {
									SyntaxNode.createUseCustomStyle(),
									SyntaxNode.createUseOtherStyle(ITexTextStyles.TS_MATH, Messages.TexTextStyles_MathCodeCategory_short, Messages.TexTextStyles_Default_label),
									SyntaxNode.createUseOtherStyle(ITexTextStyles.TS_CURLY_BRACKETS, Messages.TexTextStyles_DefaultCodeCategory_short, Messages.TexTextStyles_CurlyBracket_label),
								}, null ),
					}),
				new CategoryNode(Messages.TexTextStyles_CommentCategory_label, new SyntaxNode[] {
					new StyleNode(Messages.TexTextStyles_Comment_label, Messages.TexTextStyles_Comment_description,
							ITexTextStyles.TS_COMMENT, new SyntaxNode.UseStyle[] {
								SyntaxNode.createUseCustomStyle()
							}, null ),
					new StyleNode(Messages.TexTextStyles_TaskTag_label, Messages.TexTextStyles_TaskTag_description,
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
	protected SourceEditorViewerConfiguration getSourceViewerConfiguration(final ColorManager colorManager, final IPreferenceStore store) {
		return new LtxViewerConfiguration(null,
				TexCore.getDefaultsAccess(),
				CombinedPreferenceStore.createStore(
						store,
						EditorsUI.getPreferenceStore() ),
				colorManager);
	}
	
}
