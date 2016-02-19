/*=============================================================================#
 # Copyright (c) 2011-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.ui.config;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import de.walware.ecommons.IStatusChangeListener;
import de.walware.ecommons.databinding.jface.DataBindingSupport;
import de.walware.ecommons.ltk.ui.sourceediting.ISmartInsertSettings.TabAction;
import de.walware.ecommons.ltk.ui.sourceediting.SmartInsertSettingsUI;
import de.walware.ecommons.preferences.core.Preference;
import de.walware.ecommons.preferences.ui.ConfigurationBlock;
import de.walware.ecommons.preferences.ui.ConfigurationBlockPreferencePage;
import de.walware.ecommons.preferences.ui.ManagedConfigurationBlock;
import de.walware.ecommons.ui.util.LayoutUtil;

import de.walware.docmlet.wikitext.ui.editors.WikitextEditorBuild;
import de.walware.docmlet.wikitext.ui.sourceediting.WikitextEditingSettings;


public class WikitextEditorPreferencePage extends ConfigurationBlockPreferencePage {
	
	
	public WikitextEditorPreferencePage() {
	}
	
	
	@Override
	protected ConfigurationBlock createConfigurationBlock() {
		return new WikitextEditorConfigurationBlock(createStatusChangedListener());
	}
	
}


class WikitextEditorConfigurationBlock extends ManagedConfigurationBlock {
	
	
	private Button smartInsertControl;
	private ComboViewer smartInsertTabActionControl;
	private Button smartInsertCloseBracketsControl;
	private Button smartInsertCloseParenthesisControl;
	private Button smartInsertCloseMathDollarControl;
	private Button smartInsertHardWrapTextControl;
	
	private Button foldingEnableControl;
	private Button foldingRestoreStateControl;
	
	private Button markOccurrencesControl;
	
	private Button problemsEnableControl;
	
	
	public WikitextEditorConfigurationBlock(final IStatusChangeListener statusListener) {
		super(null, statusListener);
	}
	
	
	@Override
	public void createBlockArea(final Composite pageComposite) {
		final Map<Preference<?>, String> prefs= new HashMap<>();
		
		prefs.put(WikitextEditingSettings.SMARTINSERT_BYDEFAULT_ENABLED_PREF, WikitextEditingSettings.SMARTINSERT_GROUP_ID);
		prefs.put(WikitextEditingSettings.SMARTINSERT_TAB_ACTION_PREF, WikitextEditingSettings.SMARTINSERT_GROUP_ID);
		prefs.put(WikitextEditingSettings.SMARTINSERT_CLOSEBRACKETS_ENABLED_PREF, WikitextEditingSettings.SMARTINSERT_GROUP_ID);
		prefs.put(WikitextEditingSettings.SMARTINSERT_CLOSEPARENTHESIS_ENABLED_PREF, WikitextEditingSettings.SMARTINSERT_GROUP_ID);
		prefs.put(WikitextEditingSettings.SMARTINSERT_CLOSEMATHDOLLAR_ENABLED_PREF, WikitextEditingSettings.SMARTINSERT_GROUP_ID);
		prefs.put(WikitextEditingSettings.SMARTINSERT_HARDWRAP_TEXT_ENABLED_PREF, WikitextEditingSettings.SMARTINSERT_GROUP_ID);
		prefs.put(WikitextEditingSettings.SMARTINSERT_HARDWRAP_MODE_PREF, WikitextEditingSettings.SMARTINSERT_GROUP_ID);
		
		prefs.put(WikitextEditingSettings.FOLDING_ENABLED_PREF, null);
		prefs.put(WikitextEditingSettings.FOLDING_RESTORE_STATE_ENABLED_PREF, WikitextEditingSettings.FOLDING_SHARED_GROUP_ID);
		
		prefs.put(WikitextEditingSettings.MARKOCCURRENCES_ENABLED_PREF, null);
		
		prefs.put(WikitextEditorBuild.PROBLEMCHECKING_ENABLED_PREF, null);
//		prefs.put(WikitextEditorOptions.PREF_SPELLCHECKING_ENABLED, WikitextEditorOptions.GROUP_ID);
		
		setupPreferenceManager(prefs);
		
		{	final Composite composite= createSmartInsertOptions(pageComposite);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}
		
		// Code Folding
		LayoutUtil.addSmallFiller(pageComposite, false);
		
		{	this.foldingEnableControl= new Button(pageComposite, SWT.CHECK);
			this.foldingEnableControl.setText(Messages.EditorOptions_Folding_Enable_label);
			this.foldingEnableControl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		}
		{	this.foldingRestoreStateControl= new Button(pageComposite, SWT.CHECK);
			this.foldingRestoreStateControl.setText(Messages.EditorOptions_Folding_RestoreState_Enable_label);
			final GridData gd= new GridData(SWT.FILL, SWT.CENTER, true, false);
			gd.horizontalIndent= LayoutUtil.defaultIndent();
			this.foldingRestoreStateControl.setLayoutData(gd);
		}
		
		// Annotation
		LayoutUtil.addSmallFiller(pageComposite, false);
		
		{	this.markOccurrencesControl= new Button(pageComposite, SWT.CHECK);
			this.markOccurrencesControl.setText(Messages.EditorOptions_MarkOccurrences_Enable_label);
			this.markOccurrencesControl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		}
		
		LayoutUtil.addSmallFiller(pageComposite, false);
		
		{	this.problemsEnableControl= new Button(pageComposite, SWT.CHECK);
			this.problemsEnableControl.setText(Messages.EditorOptions_ProblemChecking_Enable_label);
			this.problemsEnableControl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		}
		
		LayoutUtil.addSmallFiller(pageComposite, true);
		
		{	final Link link= addLinkControl(pageComposite, Messages.EditorOptions_AnnotationAppearance_info);
			final GridData gd= new GridData(SWT.FILL, SWT.TOP, true, false);
			gd.widthHint= 300;
			link.setLayoutData(gd);
		}
		
		LayoutUtil.addSmallFiller(pageComposite, false);
		
		// Binding
		initBindings();
		updateControls();
	}
	
	private Composite createSmartInsertOptions(final Composite pageComposite) {
		final Group composite= new Group(pageComposite, SWT.NONE);
		composite.setText(Messages.EditorOptions_SmartInsert_label+':');
		final int n= 4;
		composite.setLayout(LayoutUtil.createGroupGrid(n));
		this.smartInsertControl= new Button(composite, SWT.CHECK);
		this.smartInsertControl.setText(Messages.EditorOptions_SmartInsert_AsDefault_label);
		this.smartInsertControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, n, 1));
		{	final Link link= addLinkControl(composite, Messages.EditorOptions_SmartInsert_description);
			final GridData gd= new GridData(SWT.FILL, SWT.FILL, true, false, n, 1);
			gd.widthHint= 300;
			link.setLayoutData(gd);
		}
		
		{	final Label label= new Label(composite, SWT.NONE);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			label.setText(Messages.EditorOptions_SmartInsert_TabAction_label);
			this.smartInsertTabActionControl= new ComboViewer(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
			this.smartInsertTabActionControl.getControl().setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, n-2, 1));
			this.smartInsertTabActionControl.setContentProvider(new ArrayContentProvider());
			this.smartInsertTabActionControl.setLabelProvider(new SmartInsertSettingsUI.SettingsLabelProvider());
			this.smartInsertTabActionControl.setInput(new TabAction[] {
					TabAction.INSERT_TAB_CHAR, TabAction.INSERT_TAB_LEVEL, TabAction.INSERT_INDENT_LEVEL, TabAction.CORRECT_INDENT,
			});
			LayoutUtil.addGDDummy(composite);
		}
		
		LayoutUtil.addSmallFiller(composite, true);
//		{	Label label= new Label(composite, SWT.CENTER);
//			label.setText(Messages.REditorOptions_SmartInsert_ForEditor_header);
//			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
//			label= new Label(composite, SWT.CENTER);
//			label.setText(Messages.REditorOptions_SmartInsert_ForConsole_header);
//			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
//		}
//		fSmartInsertOnPasteControl= createOption(composite, Messages.REditorOptions_SmartInsert_OnPaste_label, null, false);
		this.smartInsertCloseBracketsControl= createSmartInsertOption(composite,
				Messages.EditorOptions_SmartInsert_CloseAuto_label,
				Messages.EditorOptions_SmartInsert_CloseBrackets_label, true );
		{	final Label dummy= new Label(composite, SWT.NONE);
			dummy.setVisible(false);
			dummy.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 4));
		}	
		this.smartInsertCloseParenthesisControl= createSmartInsertOption(composite, null,
				Messages.EditorOptions_SmartInsert_CloseParentheses_label, true );
		this.smartInsertCloseMathDollarControl= createSmartInsertOption(composite, null,
				Messages.EditorOptions_SmartInsert_CloseMathDollar_label, true );
		this.smartInsertHardWrapTextControl= createSmartInsertOption(composite,
				Messages.EditorOptions_SmartInsert_HardWrapAuto_label,
				Messages.EditorOptions_SmartInsert_HardWrapText_label, true );
		
		return composite;
	}
	
	private Button createSmartInsertOption(final Composite composite, final String text1, final String text2, final boolean console) {
		GridData gd;
		if (text1 != null) {
			final Label label= new Label(composite, SWT.NONE);
			if (text2 == null) {
				label.setText(text1 + "\u200A:"); //$NON-NLS-1$
				gd= new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
			}
			else {
				label.setText(text1);
				gd= new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			}
			label.setLayoutData(gd);
		}
		else {
			LayoutUtil.addGDDummy(composite);
		}
		if (text2 != null) {
			final Label label= new Label(composite, SWT.NONE);
			label.setText(text2+':');
			gd= new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			label.setLayoutData(gd);
		}
		final Button button= new Button(composite, SWT.CHECK);
		gd= new GridData(SWT.CENTER, SWT.CENTER, false, false);
		button.setLayoutData(gd);
		return button;
	}
	
	
	@Override
	protected void addBindings(final DataBindingSupport db) {
		db.getContext().bindValue(
				SWTObservables.observeSelection(this.smartInsertControl),
				createObservable(WikitextEditingSettings.SMARTINSERT_BYDEFAULT_ENABLED_PREF) );
		db.getContext().bindValue(
				ViewersObservables.observeSingleSelection(this.smartInsertTabActionControl),
				createObservable(WikitextEditingSettings.SMARTINSERT_TAB_ACTION_PREF) );
		db.getContext().bindValue(
				SWTObservables.observeSelection(this.smartInsertCloseBracketsControl),
				createObservable(WikitextEditingSettings.SMARTINSERT_CLOSEBRACKETS_ENABLED_PREF) );
		db.getContext().bindValue(
				SWTObservables.observeSelection(this.smartInsertCloseParenthesisControl),
				createObservable(WikitextEditingSettings.SMARTINSERT_CLOSEPARENTHESIS_ENABLED_PREF) );
		db.getContext().bindValue(
				SWTObservables.observeSelection(this.smartInsertCloseMathDollarControl),
				createObservable(WikitextEditingSettings.SMARTINSERT_CLOSEMATHDOLLAR_ENABLED_PREF) );
		db.getContext().bindValue(
				SWTObservables.observeSelection(this.smartInsertHardWrapTextControl),
				createObservable(WikitextEditingSettings.SMARTINSERT_HARDWRAP_TEXT_ENABLED_PREF) );
		
		db.getContext().bindValue(
				SWTObservables.observeSelection(this.foldingEnableControl),
				createObservable(WikitextEditingSettings.FOLDING_ENABLED_PREF) );
		db.getContext().bindValue(
				SWTObservables.observeSelection(this.foldingRestoreStateControl),
				createObservable(WikitextEditingSettings.FOLDING_RESTORE_STATE_ENABLED_PREF) );
		
		db.getContext().bindValue(
				SWTObservables.observeSelection(this.markOccurrencesControl),
				createObservable(WikitextEditingSettings.MARKOCCURRENCES_ENABLED_PREF) );
		
		db.getContext().bindValue(
				SWTObservables.observeSelection(this.problemsEnableControl),
				createObservable(WikitextEditorBuild.PROBLEMCHECKING_ENABLED_PREF) );
	}
	
}
