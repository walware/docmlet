/*=============================================================================#
 # Copyright (c) 2007-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.ui.config;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import de.walware.ecommons.IStatusChangeListener;
import de.walware.ecommons.databinding.IntegerValidator;
import de.walware.ecommons.databinding.jface.DataBindingSupport;
import de.walware.ecommons.preferences.Preference;
import de.walware.ecommons.preferences.ui.ManagedConfigurationBlock;
import de.walware.ecommons.text.ui.settings.IndentSettingsUI;
import de.walware.ecommons.ui.CombineStatusChangeListener;
import de.walware.ecommons.ui.components.EditableTextList;
import de.walware.ecommons.ui.util.LayoutUtil;

import de.walware.docmlet.tex.core.TexCodeStyleSettings;
import de.walware.docmlet.tex.ui.TexUI;


/**
 * A PreferenceBlock for TexCodeStyleSettings (code formatting preferences).
 */
public class TexCodeStylePreferenceBlock extends ManagedConfigurationBlock {
	// in future supporting multiple profiles?
	// -> we bind to bean not to preferences
	
	
	private class LabelEditing extends EditingSupport {
		
		private final TextCellEditor cellEditor;
		
		private final EditableTextList list;
		
		private Object last;
		private IStatusChangeListener listener;
		
		public LabelEditing(final EditableTextList list) {
			super(list.getViewer());
			this.list = list;
			this.cellEditor = new TextCellEditor(list.getViewer().getTable());
			this.cellEditor.addListener(new ICellEditorListener() {
				@Override
				public void editorValueChanged(final boolean oldValidState, final boolean newValidState) {
					if (LabelEditing.this.listener == null) {
						LabelEditing.this.listener = TexCodeStylePreferenceBlock.this.statusListener.newListener();
					}
					if (!newValidState) {
						LabelEditing.this.listener.statusChanged(new Status(Status.ERROR, TexUI.PLUGIN_ID,
								LabelEditing.this.cellEditor.getErrorMessage() ));
					}
					else {
						LabelEditing.this.listener.statusChanged(Status.OK_STATUS);
					}
				}
				@Override
				public void applyEditorValue() {
					LabelEditing.this.last = null;
					if (LabelEditing.this.listener != null) {
						TexCodeStylePreferenceBlock.this.statusListener.removeListener(LabelEditing.this.listener);
						LabelEditing.this.listener = null;
					}
				}
				@Override
				public void cancelEditor() {
					if (LabelEditing.this.last == "") { //$NON-NLS-1$
						LabelEditing.this.list.applyChange("", null); //$NON-NLS-1$
					}
					if (LabelEditing.this.listener != null) {
						TexCodeStylePreferenceBlock.this.statusListener.removeListener(LabelEditing.this.listener);
						LabelEditing.this.listener = null;
					}
				}
			});
			this.cellEditor.setValidator(new ICellEditorValidator() {
				@Override
				public String isValid(final Object value) {
					final String s = (String) value;
					for (int i = 0; i < s.length(); i++) {
						final char c = s.charAt(i);
						if (!((c >= 0x41 && c <= 0x5A)
								|| (c >= 0x61 && c <= 0x7A)
								|| (c == '*' && i == s.length()-1) )) {
							return "Invalid environment name";
						}
					}
					return null;
				}
			});
		}
		
		@Override
		protected boolean canEdit(final Object element) {
			return true;
		}
		
		@Override
		protected CellEditor getCellEditor(final Object element) {
			return this.cellEditor;
		}
		
		@Override
		protected Object getValue(final Object element) {
			this.last = element;
			return element;
		}
		
		@Override
		protected void setValue(final Object element, final Object value) {
			if (value != null) {
				this.list.applyChange(element, (value != "") ? value : null); //$NON-NLS-1$
			}
		}
		
	}
	
	
	private TexCodeStyleSettings model;
	
	private IndentSettingsUI stdIndentSettings;
	private Text indentBlockDepthControl;
	private Text indentEnvDepthControl;
	private EditableTextList indentEnvLabelsControl;
	
	private final CombineStatusChangeListener statusListener;
	
	
	public TexCodeStylePreferenceBlock(final IProject project, final IStatusChangeListener statusListener) {
		super(project);
		this.statusListener = new CombineStatusChangeListener(statusListener);
		setStatusListener(this.statusListener);
	}
	
	
	@Override
	protected void createBlockArea(final Composite pageComposite) {
		final Map<Preference<?>, String> prefs = new HashMap<>();
		
		prefs.put(TexCodeStyleSettings.TAB_SIZE_PREF, TexCodeStyleSettings.INDENT_GROUP_ID);
		prefs.put(TexCodeStyleSettings.INDENT_DEFAULT_TYPE_PREF, TexCodeStyleSettings.INDENT_GROUP_ID);
		prefs.put(TexCodeStyleSettings.INDENT_SPACES_COUNT_PREF, TexCodeStyleSettings.INDENT_GROUP_ID);
		prefs.put(TexCodeStyleSettings.REPLACE_CONVERSATIVE_PREF, TexCodeStyleSettings.INDENT_GROUP_ID);
		prefs.put(TexCodeStyleSettings.REPLACE_TABS_WITH_SPACES_PREF, TexCodeStyleSettings.INDENT_GROUP_ID);
		prefs.put(TexCodeStyleSettings.INDENT_BLOCK_DEPTH_PREF, TexCodeStyleSettings.INDENT_GROUP_ID);
		prefs.put(TexCodeStyleSettings.INDENT_ENV_DEPTH_PREF, TexCodeStyleSettings.INDENT_GROUP_ID);
		prefs.put(TexCodeStyleSettings.INDENT_ENV_LABELS_PREF, TexCodeStyleSettings.INDENT_GROUP_ID);
		
		setupPreferenceManager(prefs);
		
		this.model = new TexCodeStyleSettings(0);
		this.stdIndentSettings = new IndentSettingsUI();
		
		final Composite mainComposite = new Composite(pageComposite, SWT.NONE);
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mainComposite.setLayout((LayoutUtil.createCompositeGrid(2)));
		
		final TabFolder folder = new TabFolder(mainComposite, SWT.NONE);
		folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		{	final TabItem item = new TabItem(folder, SWT.NONE);
			item.setText(this.stdIndentSettings.getGroupLabel());
			item.setControl(createIndentControls(folder));
		}
		{	final TabItem item = new TabItem(folder, SWT.NONE);
			item.setText("&Line Wrapping");
			item.setControl(createLineControls(folder));
		}
		
		initBindings();
		updateControls();
	}
	
	private Control createIndentControls(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(LayoutUtil.createTabGrid(2));
		
		this.stdIndentSettings.createControls(composite);
		LayoutUtil.addSmallFiller(composite, false);
		
		final Composite depthComposite = new Composite(composite, SWT.NONE);
		depthComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		depthComposite.setLayout(LayoutUtil.createCompositeGrid(4));
		this.indentBlockDepthControl = createIndentDepthLine(depthComposite, Messages.CodeStyle_Indent_IndentInBlocks_label);
		this.indentEnvDepthControl = createIndentDepthLine(depthComposite, Messages.CodeStyle_Indent_IndentInEnvs_label);
		
		{	final Label label = new Label(depthComposite, SWT.NONE);
			final GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
			gd.horizontalIndent = LayoutUtil.defaultIndent();
			label.setLayoutData(gd);
			label.setText("Environments to be indented:");
		}
		this.indentEnvLabelsControl = new EditableTextList();
		{	final Control control = this.indentEnvLabelsControl.create(depthComposite, new ViewerComparator());
			final GridData gd = new GridData(SWT.FILL, SWT.FILL, false, true, 3, 1);
			gd.horizontalIndent = LayoutUtil.defaultIndent();
			control.setLayoutData(gd);
			LayoutUtil.addGDDummy(depthComposite, true);
		}
		this.indentEnvLabelsControl.getColumn().setEditingSupport(new LabelEditing(this.indentEnvLabelsControl));
		
		LayoutUtil.addSmallFiller(depthComposite, false);
		
		LayoutUtil.addSmallFiller(composite, false);
		return composite;
	}
	
	private Text createIndentDepthLine(final Composite composite, final String label) {
		final Label labelControl = new Label(composite, SWT.LEFT);
		labelControl.setText(label);
		labelControl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		final Text textControl = new Text(composite, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		final GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gd.widthHint = LayoutUtil.hintWidth(textControl, 2);
		textControl.setLayoutData(gd);
		final Label typeControl = new Label(composite, SWT.LEFT);
		typeControl.setText(this.stdIndentSettings.getLevelUnitLabel());
		typeControl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		LayoutUtil.addGDDummy(composite);
		
		return textControl;
	}
	
	private Control createLineControls(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(LayoutUtil.createTabGrid(2));
		
		this.stdIndentSettings.addLineWidth(composite);
		
		return composite;
	}
	
	@Override
	protected void addBindings(final DataBindingSupport db) {
		this.stdIndentSettings.addBindings(db, this.model);
		
		db.getContext().bindValue(
				WidgetProperties.text(SWT.Modify).observe(this.indentBlockDepthControl),
				BeanProperties.value(TexCodeStyleSettings.INDENT_BLOCK_DEPTH_PROP)
						.observe(db.getRealm(), this.model),
				new UpdateValueStrategy().setAfterGetValidator(new IntegerValidator(1, 10, Messages.CodeStyle_Indent_IndentInBlocks_error_message)),
				null );
		db.getContext().bindValue(
				WidgetProperties.text(SWT.Modify).observe(this.indentEnvDepthControl),
				BeanProperties.value(TexCodeStyleSettings.INDENT_ENV_DEPTH_PROP)
						.observe(db.getRealm(), this.model),
				new UpdateValueStrategy().setAfterGetValidator(new IntegerValidator(1, 10, Messages.CodeStyle_Indent_IndentInEnvs_error_message)),
				null );
		
		final WritableSet labels = new WritableSet(db.getRealm());
		this.indentEnvLabelsControl.setInput(labels);
		db.getContext().bindSet(
				labels,
				BeanProperties.set(TexCodeStyleSettings.INDENT_ENV_LABELS_PROP, String.class)
						.observe(db.getRealm(), this.model) );
	}
	
	@Override
	protected void updateControls() {
		this.model.load(this);
		this.model.resetDirty();
		getDataBinding().getContext().updateTargets();  // required for invalid target values
		this.indentEnvLabelsControl.refresh();
	}
	
	@Override
	protected void updatePreferences() {
		if (this.model.isDirty()) {
			this.model.resetDirty();
			setPrefValues(this.model.toPreferencesMap());
		}
	}
	
}
