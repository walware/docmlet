/*=============================================================================#
 # Copyright (c) 2011-2015 Stephan Wahlbrink (WalWare.de) and others.
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.walware.ecommons.IStatusChangeListener;
import de.walware.ecommons.databinding.jface.DataBindingSupport;
import de.walware.ecommons.preferences.core.Preference;
import de.walware.ecommons.preferences.ui.ConfigurationBlock;
import de.walware.ecommons.preferences.ui.ConfigurationBlockPreferencePage;
import de.walware.ecommons.preferences.ui.ManagedConfigurationBlock;
import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.ui.util.ViewerUtil;

import de.walware.docmlet.tex.core.commands.LtxCommandCategories;
import de.walware.docmlet.tex.core.commands.LtxCommandCategories.Category;
import de.walware.docmlet.tex.core.commands.LtxCommandDefinitions;
import de.walware.docmlet.tex.core.commands.TexCommand;
import de.walware.docmlet.tex.core.commands.TexCommandSet;
import de.walware.docmlet.tex.ui.TexCommandLabelProvider;


public class LtxCommandsPreferencePage extends ConfigurationBlockPreferencePage {
	
	
	public LtxCommandsPreferencePage() {
	}
	
	
	@Override
	protected ConfigurationBlock createConfigurationBlock() {
		return new TexCommandsConfigurationBlock(createStatusChangedListener());
	}
	
}


class TexCommandsConfigurationBlock extends ManagedConfigurationBlock {
	
	
	private static class CatContentProvider implements ITreeContentProvider {
		
		private LtxCommandCategories fCategories;
		
		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			fCategories = (LtxCommandCategories) newInput;
		}
		
		@Override
		public Object[] getElements(final Object inputElement) {
			final List<Category> categories = fCategories.getCategories();
			return categories.toArray(new Category[categories.size()]);
		}
		
		@Override
		public boolean hasChildren(final Object element) {
			return (element instanceof Category);
		}
		
		@Override
		public Object[] getChildren(final Object parentElement) {
			final List<TexCommand> commands = ((Category) parentElement).getCommands();
			return commands.toArray(new TexCommand[commands.size()]);
		}
		
		@Override
		public Object getParent(final Object element) {
			if (element instanceof TexCommand) {
				return fCategories.getCategory((TexCommand) element);
			}
			return null;
		}
		
		@Override
		public void dispose() {
			fCategories = null;
		}
		
	}
	
	
	private static final int DETAIL_CHECK_ASSIST_TEXT = 0;
	private static final int DETAIL_CHECK_ASSIST_MATH = 1;
	private static final int DETAIL_CHECK_SIZE = 2;
	
	private CheckboxTreeViewer fTreeViewer;
	private final Button[] fDetailCheckControls = new Button[DETAIL_CHECK_SIZE];
	
	private final Preference<Set<String>>[] fDetailCheckPrefs = new Preference[DETAIL_CHECK_SIZE];
	
	private final Set<String> fEnabled= new HashSet<>();
	private final Set<String>[] fDetailCheckEnabled = new HashSet[DETAIL_CHECK_SIZE];
	
	
	public TexCommandsConfigurationBlock(final IStatusChangeListener statusListener) {
		super(null, statusListener);
		
		for (int i = 0; i < DETAIL_CHECK_SIZE; i++) {
			fDetailCheckEnabled[i]= new HashSet<>();
		}
	}
	
	
	@Override
	public void createBlockArea(final Composite pageComposite) {
		final Map<Preference<?>, String> prefs= new HashMap<>();
		
		prefs.put(TexCommandSet.MASTER_COMMANDS_INCLUDE_PREF, null);
		prefs.put(fDetailCheckPrefs[DETAIL_CHECK_ASSIST_TEXT]= TexCommandSet.TEXT_COMMANDS_INCLUDE_PREF, null);
		prefs.put(fDetailCheckPrefs[DETAIL_CHECK_ASSIST_MATH]= TexCommandSet.MATH_COMMANDS_INCLUDE_PREF, null);
		
		setupPreferenceManager(prefs);
		
		final LtxCommandCategories categories = new LtxCommandCategories(LtxCommandDefinitions.getAllCommands());
		
		final Composite composite = new Composite(pageComposite, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(LayoutUtil.applyCompositeDefaults(new GridLayout(), 2));
		
		{	final Control tree = createTree(categories, composite);
			tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		}
		{	final Control detail = createDetailComposite(composite);
			detail.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		}
		
		// Binding
		updateControls();
		
		fTreeViewer.getTree().select(fTreeViewer.getTree().getItem(0));
	}
	
	private Control createTree(final LtxCommandCategories categories, final Composite composite) {
		fTreeViewer = new CheckboxTreeViewer(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		fTreeViewer.setContentProvider(new CatContentProvider());
		fTreeViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(final CheckStateChangedEvent event) {
				final Object element = event.getElement();
				Category category = null;
				if (element instanceof Category) {
					category = (Category) element;
					final List<TexCommand> commands = category.getCommands();
					if (event.getChecked()) {
						for (final TexCommand command : commands) {
							fEnabled.add(command.getControlWord());
						}
					}
					else {
						for (final TexCommand command : commands) {
							fEnabled.remove(command.getControlWord());
						}
					}
				}
				else if (element instanceof TexCommand) {
					final TexCommand command = (TexCommand) element;
					if (event.getChecked()) {
						fEnabled.add(command.getControlWord());
					}
					else {
						fEnabled.remove(command.getControlWord());
					}
					category = categories.getCategory(command);
				}
				if (category != null) {
					fTreeViewer.refresh(category, true);
				}
			}
		});
		fTreeViewer.setCheckStateProvider(new ICheckStateProvider() {
			@Override
			public boolean isChecked(final Object element) {
				if (element instanceof Category) {
					final List<TexCommand> commands = ((Category) element).getCommands();
					for (final TexCommand command : commands) {
						if (fEnabled.contains(command.getControlWord())) {
							return true;
						}
					}
					return false;
				}
				if (element instanceof TexCommand) {
					return (fEnabled.contains(((TexCommand) element).getControlWord()));
				}
				return false;
			}
			@Override
			public boolean isGrayed(final Object element) {
				if (element instanceof Category) {
					int check = 0x0;
					final List<TexCommand> commands = ((Category) element).getCommands();
					for (final TexCommand command : commands) {
						check |= fEnabled.contains(command.getControlWord()) ? 0x1 : 0x2;
						if (check == (0x1 | 0x2)) {
							return true;
						}
					}
				}
				return false;
			}
		});
		fTreeViewer.setLabelProvider(new TexCommandLabelProvider());
		fTreeViewer.setInput(categories);
		
		ViewerUtil.addDoubleClickExpansion(fTreeViewer);
		
		return fTreeViewer.getControl();
	}
	
	protected Object[] getSelectedElements() {
		return ((IStructuredSelection) fTreeViewer.getSelection()).toArray();
	}
	
	protected Control createDetailComposite(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(LayoutUtil.applyCompositeDefaults(new GridLayout(), 1));
		
		{	final Label label = new Label(composite, SWT.NONE);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			label.setText("Region:");
		}
		{	final Button button = new Button(composite, SWT.CHECK);
			final GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gd.horizontalIndent = LayoutUtil.defaultSmallIndent();
			button.setLayoutData(gd);
			button.setText("&Text");
			
			fDetailCheckControls[DETAIL_CHECK_ASSIST_TEXT] = button;
			registerDetailCheck(DETAIL_CHECK_ASSIST_TEXT);
		}
		{	final Button button = new Button(composite, SWT.CHECK);
			final GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gd.horizontalIndent = LayoutUtil.defaultSmallIndent();
			button.setLayoutData(gd);
			button.setText("&Math");
			
			fDetailCheckControls[DETAIL_CHECK_ASSIST_MATH] = button;
			registerDetailCheck(DETAIL_CHECK_ASSIST_MATH);
		}
		
		LayoutUtil.addSmallFiller(composite, true);
		
		return composite;
	}
	
	private void registerDetailCheck(final int id) {
		final Button button = fDetailCheckControls[id];
		final Set<String> enabled = fDetailCheckEnabled[id];
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				button.setGrayed(false);
				final Object[] elements = getSelectedElements();
				if (button.getSelection()) {
					for (final Object element : elements) {
						if (element instanceof Category) {
							final List<TexCommand> commands = ((Category) element).getCommands();
							for (final TexCommand command : commands) {
								enabled.add(command.getControlWord());
							}
						}
						else if (element instanceof TexCommand) {
							enabled.add(((TexCommand) element).getControlWord());
						}
					}
				}
				else {
					for (final Object element : elements) {
						if (element instanceof Category) {
							final List<TexCommand> commands = ((Category) element).getCommands();
							for (final TexCommand command : commands) {
								enabled.remove(command.getControlWord());
							}
						}
						else if (element instanceof TexCommand) {
							enabled.remove(((TexCommand) element).getControlWord());
						}
					}
				}
			}
		});
		
		fTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				updateDetail();
			}
		});
	}
	
	private void updateDetail() {
		final Object[] elements = getSelectedElements();
		if (elements.length == 0) {
			for (int i = 0; i < DETAIL_CHECK_SIZE; i++) {
				fDetailCheckControls[i].setSelection(false);
				fDetailCheckControls[i].setGrayed(false);
				fDetailCheckControls[i].setEnabled(false);
			}
		}
		
		final int[] state = new int[DETAIL_CHECK_SIZE];
		for (final Object element : elements) {
			if (element instanceof Category) {
				final List<TexCommand> commands = ((Category) element).getCommands();
				for (final TexCommand command : commands) {
					for (int i = 0; i < DETAIL_CHECK_SIZE; i++) {
						state[i] = fDetailCheckEnabled[i].contains(command.getControlWord()) ? 0x1 : 0x2;
					}
				}
			}
			else if (element instanceof TexCommand) {
				final TexCommand command = (TexCommand) element;
				for (int i = 0; i < DETAIL_CHECK_SIZE; i++) {
					state[i] = fDetailCheckEnabled[i].contains(command.getControlWord()) ? 0x1 : 0x2;
				}
			}
		}
		
		for (int i = 0; i < DETAIL_CHECK_SIZE; i++) {
			fDetailCheckControls[i].setEnabled(true);
			if (state[i] == 0x3) {
				fDetailCheckControls[i].setSelection(true);
				fDetailCheckControls[i].setGrayed(true);
			}
			else {
				fDetailCheckControls[i].setSelection(state[i] == 0x1);
				fDetailCheckControls[i].setGrayed(false);
			}
		}
	}
	
	@Override
	protected void addBindings(final DataBindingSupport db) {
	}
	
	
	@Override
	protected void updateControls() {
		super.updateControls();
		fEnabled.clear();
		fEnabled.addAll(getPreferenceValue(TexCommandSet.MASTER_COMMANDS_INCLUDE_PREF));
		for (int i = 0; i < DETAIL_CHECK_SIZE; i++) {
			fDetailCheckEnabled[i].clear();
			fDetailCheckEnabled[i].addAll(getPreferenceValue(fDetailCheckPrefs[i]));
		}
		fTreeViewer.refresh();
		updateDetail();
	}
	
	private void save() {
		setPrefValue(TexCommandSet.MASTER_COMMANDS_INCLUDE_PREF, fEnabled);
		for (int i = 0; i < DETAIL_CHECK_SIZE; i++) {
			setPrefValue(fDetailCheckPrefs[i], fDetailCheckEnabled[i]);
		}
	}
	
	@Override
	public void performApply() {
		save();
		super.performApply();
	}
	
	@Override
	public boolean performOk() {
		save();
		return super.performOk();
	}
	
}
