/*******************************************************************************
 * Copyright (c) 2011-2012 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.internal.ui.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import de.walware.ecommons.IStatusChangeListener;
import de.walware.ecommons.databinding.jface.DataBindingSupport;
import de.walware.ecommons.ltk.ui.sourceediting.ISmartInsertSettings.TabAction;
import de.walware.ecommons.ltk.ui.sourceediting.SmartInsertSettingsUI;
import de.walware.ecommons.preferences.Preference;
import de.walware.ecommons.preferences.ui.ConfigurationBlockPreferencePage;
import de.walware.ecommons.preferences.ui.ManagedConfigurationBlock;
import de.walware.ecommons.ui.util.LayoutUtil;

import de.walware.docmlet.tex.ui.editors.LtxEditorBuild;
import de.walware.docmlet.tex.ui.editors.TexEditorOptions;


public class TexEditorPreferencePage extends ConfigurationBlockPreferencePage<TexEditorConfigurationBlock> {
	
	
	public TexEditorPreferencePage() {
	}
	
	@Override
	protected TexEditorConfigurationBlock createConfigurationBlock() {
		return new TexEditorConfigurationBlock(createStatusChangedListener());
	}
	
}


class TexEditorConfigurationBlock extends ManagedConfigurationBlock {
	
	
	private Button fSmartInsertControl;
	private ComboViewer fSmartInsertTabActionControl;
	private Button fSmartInsertCloseBracketsControl;
	private Button fSmartInsertCloseParenthesisControl;
	private Button fSmartInsertCloseMathDollarControl;
	private Button fSmartInsertHardWrapTextControl;
	
	private Button fFoldingEnableControl;
	private Button fMarkOccurrencesControl;
	private Button fSpellEnableControl;
	private Button fProblemsEnableControl;
	
	
	public TexEditorConfigurationBlock(final IStatusChangeListener statusListener) {
		super(null, statusListener);
	}
	
	
	@Override
	public void createBlockArea(final Composite pageComposite) {
		final Map<Preference<?>, String> prefs = new HashMap<Preference<?>, String>();
		
		prefs.put(TexEditorOptions.SMARTINSERT_BYDEFAULT_ENABLED_PREF, TexEditorOptions.SMARTINSERT_GROUP_ID);
		prefs.put(TexEditorOptions.SMARTINSERT_TAB_ACTION_PREF, TexEditorOptions.SMARTINSERT_GROUP_ID);
		prefs.put(TexEditorOptions.SMARTINSERT_CLOSEBRACKETS_ENABLED_PREF, TexEditorOptions.SMARTINSERT_GROUP_ID);
		prefs.put(TexEditorOptions.SMARTINSERT_CLOSEPARENTHESIS_ENABLED_PREF, TexEditorOptions.SMARTINSERT_GROUP_ID);
		prefs.put(TexEditorOptions.SMARTINSERT_CLOSEMATHDOLLAR_ENABLED_PREF, TexEditorOptions.SMARTINSERT_GROUP_ID);
		prefs.put(TexEditorOptions.SMARTINSERT_HARDWRAP_TEXT_ENABLED_PREF, TexEditorOptions.SMARTINSERT_GROUP_ID);
		
		prefs.put(TexEditorOptions.FOLDING_ENABLED_PREF, null);
		
		prefs.put(TexEditorOptions.MARKOCCURRENCES_ENABLED_PREF, null);
		
		prefs.put(LtxEditorBuild.PROBLEMCHECKING_ENABLED_PREF, null);
//		prefs.put(TexEditorOptions.PREF_SPELLCHECKING_ENABLED, TexEditorOptions.GROUP_ID);
		
		setupPreferenceManager(prefs);
		
		{	final Composite composite = createSmartInsertOptions(pageComposite);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}
		
		// Code Folding
		LayoutUtil.addSmallFiller(pageComposite, false);
		{	fFoldingEnableControl = new Button(pageComposite, SWT.CHECK);
			fFoldingEnableControl.setText(Messages.EditorOptions_Folding_Enable_label);
			fFoldingEnableControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}
		
		// Annotation
		LayoutUtil.addSmallFiller(pageComposite, false);
		
		{	fMarkOccurrencesControl = new Button(pageComposite, SWT.CHECK);
			fMarkOccurrencesControl.setText(Messages.EditorOptions_MarkOccurrences_Enable_label);
			fMarkOccurrencesControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}
		
		LayoutUtil.addSmallFiller(pageComposite, false);
		
		{	fProblemsEnableControl = new Button(pageComposite, SWT.CHECK);
			fProblemsEnableControl.setText(Messages.EditorOptions_ProblemChecking_Enable_label);
			fProblemsEnableControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}
		
		LayoutUtil.addSmallFiller(pageComposite, true);
		
		{	final Link link = addLinkControl(pageComposite, Messages.EditorOptions_AnnotationAppearance_info);
			final GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
			gd.widthHint = 300;
			link.setLayoutData(gd);
		}
		
		LayoutUtil.addSmallFiller(pageComposite, false);
		
		// Binding
		initBindings();
		updateControls();
	}
	
	private Composite createSmartInsertOptions(final Composite pageComposite) {
		final Group composite = new Group(pageComposite, SWT.NONE);
		composite.setText(Messages.EditorOptions_SmartInsert_label+':');
		final int n = 4;
		composite.setLayout(LayoutUtil.applyGroupDefaults(new GridLayout(), n));
		fSmartInsertControl = new Button(composite, SWT.CHECK);
		fSmartInsertControl.setText(Messages.EditorOptions_SmartInsert_AsDefault_label);
		fSmartInsertControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, n, 1));
		{	final Link link = addLinkControl(composite, Messages.EditorOptions_SmartInsert_description);
			final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false, n, 1);
			gd.widthHint = 300;
			link.setLayoutData(gd);
		}
		
		{	final Label label = new Label(composite, SWT.NONE);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			label.setText(Messages.EditorOptions_SmartInsert_TabAction_label);
			fSmartInsertTabActionControl = new ComboViewer(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
			fSmartInsertTabActionControl.getControl().setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, n-2, 1));
			fSmartInsertTabActionControl.setContentProvider(new ArrayContentProvider());
			fSmartInsertTabActionControl.setLabelProvider(new SmartInsertSettingsUI.SettingsLabelProvider());
			fSmartInsertTabActionControl.setInput(new TabAction[] {
					TabAction.INSERT_TAB_CHAR, TabAction.INSERT_INDENT_LEVEL,
			});
			LayoutUtil.addGDDummy(composite);
		}
		
		LayoutUtil.addSmallFiller(composite, true);
//		{	Label label = new Label(composite, SWT.CENTER);
//			label.setText(Messages.REditorOptions_SmartInsert_ForEditor_header);
//			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
//			label = new Label(composite, SWT.CENTER);
//			label.setText(Messages.REditorOptions_SmartInsert_ForConsole_header);
//			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
//		}
//		fSmartInsertOnPasteControl = createOption(composite, Messages.REditorOptions_SmartInsert_OnPaste_label, null, false);
		fSmartInsertCloseBracketsControl = createSmartInsertOption(composite,
				Messages.EditorOptions_SmartInsert_CloseAuto_label,
				Messages.EditorOptions_SmartInsert_CloseBrackets_label, true );
		{	final Label dummy = new Label(composite, SWT.NONE);
			dummy.setVisible(false);
			dummy.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 4));
		}	
		fSmartInsertCloseParenthesisControl = createSmartInsertOption(composite, null,
				Messages.EditorOptions_SmartInsert_CloseParentheses_label, true );
		fSmartInsertCloseMathDollarControl = createSmartInsertOption(composite, null,
				Messages.EditorOptions_SmartInsert_CloseMathDollar_label, true );
		fSmartInsertHardWrapTextControl = createSmartInsertOption(composite,
				Messages.EditorOptions_SmartInsert_HardWrapAuto_label,
				Messages.EditorOptions_SmartInsert_HardWrapText_label, true );
		
//		fSmartInsertCloseSquareBracketsControl = createOption(composite, null, Messages.REditorOptions_SmartInsert_CloseSquare_label, true);
//		fSmartInsertCloseSpecialControl = createOption(composite, null, Messages.REditorOptions_SmartInsert_ClosePercent_label, true);
//		fSmartInsertCloseStringsControl = createOption(composite, null, Messages.REditorOptions_SmartInsert_CloseString_label, true);
		
		return composite;
	}
	
	private Button createSmartInsertOption(final Composite composite, final String text1, final String text2, final boolean console) {
		GridData gd;
		if (text1 != null) {
			final Label label = new Label(composite, SWT.NONE);
			if (text2 == null) {
				label.setText(text1+':');
				gd = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
			}
			else {
				label.setText(text1);
				gd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			}
			label.setLayoutData(gd);
		}
		else {
			LayoutUtil.addGDDummy(composite);
		}
		if (text2 != null) {
			final Label label = new Label(composite, SWT.NONE);
			label.setText(text2+':');
			gd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			label.setLayoutData(gd);
		}
		final Button button = new Button(composite, SWT.CHECK);
		gd = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		button.setLayoutData(gd);
		return button;
	}
	
	
	@Override
	protected void addBindings(final DataBindingSupport db) {
		db.getContext().bindValue(SWTObservables.observeSelection(fSmartInsertControl),
				createObservable(TexEditorOptions.SMARTINSERT_BYDEFAULT_ENABLED_PREF) );
		db.getContext().bindValue(ViewersObservables.observeSingleSelection(fSmartInsertTabActionControl),
				createObservable(TexEditorOptions.SMARTINSERT_TAB_ACTION_PREF) );
		db.getContext().bindValue(SWTObservables.observeSelection(fSmartInsertCloseBracketsControl),
				createObservable(TexEditorOptions.SMARTINSERT_CLOSEBRACKETS_ENABLED_PREF) );
		db.getContext().bindValue(SWTObservables.observeSelection(fSmartInsertCloseParenthesisControl),
				createObservable(TexEditorOptions.SMARTINSERT_CLOSEPARENTHESIS_ENABLED_PREF) );
		db.getContext().bindValue(SWTObservables.observeSelection(fSmartInsertCloseMathDollarControl),
				createObservable(TexEditorOptions.SMARTINSERT_CLOSEMATHDOLLAR_ENABLED_PREF) );
		db.getContext().bindValue(SWTObservables.observeSelection(fSmartInsertHardWrapTextControl),
				createObservable(TexEditorOptions.SMARTINSERT_HARDWRAP_TEXT_ENABLED_PREF) );
		
		db.getContext().bindValue(SWTObservables.observeSelection(fFoldingEnableControl),
				createObservable(TexEditorOptions.FOLDING_ENABLED_PREF) );
		
		db.getContext().bindValue(SWTObservables.observeSelection(fMarkOccurrencesControl),
				createObservable(TexEditorOptions.MARKOCCURRENCES_ENABLED_PREF) );
		
		db.getContext().bindValue(SWTObservables.observeSelection(fProblemsEnableControl),
				createObservable(LtxEditorBuild.PROBLEMCHECKING_ENABLED_PREF) );
	}
	
}
