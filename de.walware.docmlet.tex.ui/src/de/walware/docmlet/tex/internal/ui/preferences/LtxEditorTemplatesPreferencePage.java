/*******************************************************************************
 * Copyright (c) 2005-2012 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.internal.ui.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;

import de.walware.ecommons.ltk.ui.sourceediting.SourceEditorViewerConfigurator;
import de.walware.ecommons.ltk.ui.sourceediting.ViewerSourceEditorAdapter;
import de.walware.ecommons.preferences.ui.SettingsUpdater;
import de.walware.ecommons.templates.TemplateVariableProcessor;
import de.walware.ecommons.text.Partitioner;
import de.walware.ecommons.text.ui.TextViewerEditorColorUpdater;
import de.walware.ecommons.text.ui.TextViewerJFaceUpdater;

import de.walware.docmlet.tex.core.text.ITexDocumentConstants;
import de.walware.docmlet.tex.internal.ui.TexUIPlugin;
import de.walware.docmlet.tex.internal.ui.editors.LtxEditorContextType;
import de.walware.docmlet.tex.ui.sourceediting.LtxTemplateViewerConfigurator;


public class LtxEditorTemplatesPreferencePage extends TemplatePreferencePage {
	
	
	SourceEditorViewerConfigurator fViewerConfigurator;
	TemplateVariableProcessor fTemplateProcessor;
	
	SourceEditorViewerConfigurator fDialogViewerConfigurator;
	TemplateVariableProcessor fDialogTemplateProcessor;
	
	
	public LtxEditorTemplatesPreferencePage() {
		setPreferenceStore(TexUIPlugin.getDefault().getPreferenceStore());
		setTemplateStore(TexUIPlugin.getDefault().getTexEditorTemplateStore());
		setContextTypeRegistry(TexUIPlugin.getDefault().getTexEditorTemplateContextTypeRegistry());
		
		fTemplateProcessor = new TemplateVariableProcessor();
		fViewerConfigurator = new LtxTemplateViewerConfigurator(null, fTemplateProcessor);
		
		fDialogTemplateProcessor = new TemplateVariableProcessor();
		fDialogViewerConfigurator = new LtxTemplateViewerConfigurator(null, fDialogTemplateProcessor);
	}
	
	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		setTitle(Messages.TexEditorTemplates_title);
	}
	
	@Override
	protected SourceViewer createViewer(final Composite parent) {
		final SourceViewer viewer = new SourceViewer(parent, null, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.setEditable(false);	
		viewer.getTextWidget().setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));
		
		final ViewerSourceEditorAdapter adapter = new ViewerSourceEditorAdapter(viewer, null);
		fViewerConfigurator.setTarget(adapter);
		// updater
		new SettingsUpdater(fViewerConfigurator, viewer.getControl());
		new TextViewerJFaceUpdater(viewer, 
				fViewerConfigurator.getSourceViewerConfiguration().getPreferences() );
		new TextViewerEditorColorUpdater(viewer, 
				fViewerConfigurator.getSourceViewerConfiguration().getPreferences() );
		
		final IDocument document = new Document();
		fViewerConfigurator.getDocumentSetupParticipant().setup(document);
		viewer.setDocument(document);
		
		return viewer;
	}
	
	@Override
	protected void updateViewerInput() {
		super.updateViewerInput();
		
		final IStructuredSelection selection = (IStructuredSelection) getTableViewer().getSelection();
		
		if (selection.size() == 1) {
			final TemplatePersistenceData data = (TemplatePersistenceData) selection.getFirstElement();
			final Template template = data.getTemplate();
			final TemplateContextType contextType = getContextTypeRegistry().getContextType(template.getContextTypeId());
			fTemplateProcessor.setContextType(contextType);
			final AbstractDocument document = (AbstractDocument) getViewer().getDocument();
			configureContext(document, contextType, fViewerConfigurator);
		}
	}
	
	@Override
	protected Template editTemplate(final Template template, final boolean edit, final boolean isNameModifiable) {
		final de.walware.ecommons.ltk.ui.templates.EditTemplateDialog dialog = new de.walware.ecommons.ltk.ui.templates.EditTemplateDialog(
				getShell(), template, edit,
				de.walware.ecommons.ltk.ui.templates.EditTemplateDialog.EDITOR_TEMPLATE,
				fDialogViewerConfigurator, fDialogTemplateProcessor, getContextTypeRegistry()) {
			
			@Override
			protected void configureForContext(final TemplateContextType contextType) {
				super.configureForContext(contextType);
				final SourceViewer sourceViewer = getSourceViewer();
				final AbstractDocument document = (AbstractDocument) sourceViewer.getDocument();
				LtxEditorTemplatesPreferencePage.this.configureContext(document, contextType, getSourceViewerConfigurator());
			}
		};
		if (dialog.open() == Dialog.OK) {
			return dialog.getTemplate();
		}
		return null;
	}
	
	protected void configureContext(final AbstractDocument document, final TemplateContextType contextType, final SourceEditorViewerConfigurator configurator) {
		final Partitioner partitioner = (Partitioner) document.getDocumentPartitioner(configurator.getPartitioning().getPartitioning());
		if (contextType.getId().equals(LtxEditorContextType.LTX_MATH_CONTEXT_TYPE_ID)) {
			partitioner.setStartPartitionType(ITexDocumentConstants.LTX_MATH_CONTENT_TYPE);
		}
		else {
			partitioner.setStartPartitionType(ITexDocumentConstants.LTX_DEFAULT_EXPL_CONTENT_TYPE);
		}
		partitioner.disconnect();
		partitioner.connect(document);
		document.setDocumentPartitioner(configurator.getPartitioning().getPartitioning(), partitioner);
	}
	
	@Override
	protected boolean isShowFormatterSetting() {
		return false;
	}
	
	@Override
	protected String getFormatterPreferenceKey() {
		return null;
	}
	
}
