/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.base.ui.processing;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.statushandlers.StatusManager;

import de.walware.ecommons.collections.ImList;
import de.walware.ecommons.databinding.core.observable.WritableEqualityValue;
import de.walware.ecommons.databinding.core.util.UpdateableErrorValidator;
import de.walware.ecommons.debug.core.variables.ResourceVariableResolver;
import de.walware.ecommons.debug.core.variables.ResourceVariables;
import de.walware.ecommons.debug.ui.config.LaunchConfigTabWithDbc;
import de.walware.ecommons.ui.components.DropDownButton;
import de.walware.ecommons.ui.util.DialogUtil;
import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.ui.util.MessageUtil;
import de.walware.ecommons.ui.util.UIAccess;
import de.walware.ecommons.ui.workbench.ResourceInputComposite;
import de.walware.ecommons.ui.workbench.ResourceVariableUtil;
import de.walware.ecommons.variables.core.ObservableValueVariable;
import de.walware.ecommons.variables.core.VariableText2;
import de.walware.ecommons.variables.core.VariableUtils;

import de.walware.docmlet.base.internal.ui.processing.Messages;
import de.walware.docmlet.base.ui.DocBaseUI;


public class DocProcessingConfigMainTab extends LaunchConfigTabWithDbc
		implements IValueChangeListener {
	
	
	private static final String WORKING_DIRECTORY_DEFAULT_VALUE=
			"${" + ResourceVariables.CONTAINER_PATH_VAR_NAME + "}"; //$NON-NLS-1$ //$NON-NLS-2$
	
	
	private class StepItem implements DocProcessingConfigStepTab.Listener {
		
		private final DocProcessingConfigStepTab tab;
		
		private Button enabledControl;
		private Label infoControl;
		
		
		public StepItem(final DocProcessingConfigStepTab tab) {
			this.tab= tab;
			
			tab.addListener(this);
		}
		
		
		private void updateInfo() {
			if (!UIAccess.isOkToUse(getControl())) {
				return;
			}
			this.enabledControl.setSelection(this.tab.isEnabled());
			this.infoControl.setText(this.tab.getInfo());
		}
		
		@Override
		public void changed(final DocProcessingConfigStepTab source) {
			updateInfo();
		}
		
	}
	
	
	private final List<StepItem> stepItems;
	
	private final IObservableValue workingDirectoryValue;
	
	private final ResourceVariableUtil resolvedSourceFileVariableUtil;
	private final Map<String, IStringVariable> resolvedSourceFileVariables= new HashMap<>();
	private IObservableValue resolvedWorkingDirectoryResourceValue;
	private final ObservableValueVariable resolvedWorkingDirectoryPathVariable;
	
	private ResourceInputComposite workingDirectoryControl;
	
	private ImList<ILaunchConfiguration> presets;
	
	private Map<String, Object> presetToLoad;
	private ILaunchConfiguration presetLoaded;
	
	
	public DocProcessingConfigMainTab(final DocProcessingConfigPresets presets) {
		this.stepItems= new ArrayList<>();
		
		final Realm realm= getRealm();
		this.workingDirectoryValue= new WritableValue(realm, null, String.class);
		
		setPresets(presets);
		
		this.resolvedSourceFileVariableUtil= new ResourceVariableUtil() {
			@Override
			protected IResource fetchResource() {
				final IResource resource= super.fetchResource();
				return (resource instanceof IFile) ? resource : null;
			}
		};
		
		this.resolvedWorkingDirectoryPathVariable= new ObservableValueVariable(
				DocProcessingConfig.WD_PATH_VAR_NAME, null,
				new WritableEqualityValue(realm, null, String.class) );
	}
	
	
	int addStep(final DocProcessingConfigStepTab stepTab) {
		final StepItem item= new StepItem(stepTab);
		this.stepItems.add(item);
		return this.stepItems.size();
	}
	
	@Override
	public String getName() {
		return Messages.MainTab_name;
	}
	
	protected void setPresets(final DocProcessingConfigPresets presets) {
		this.presets= presets.toList();
	}
	
	
	@Override
	public void setLaunchConfigurationDialog(final ILaunchConfigurationDialog dialog) {
		super.setLaunchConfigurationDialog(dialog);
		
		initVariables(this.resolvedSourceFileVariables);
	}
	
	protected void initVariables(final Map<String, IStringVariable> variables) {
		VariableUtils.add(variables,
				ResourceVariables.getSingleResourceVariables(),
				new ResourceVariableResolver(this.resolvedSourceFileVariableUtil) );
		VariableUtils.add(variables,
				VariableUtils.toStaticVariable(DocProcessingConfig.SOURCE_FILE_PATH_VAR,
						variables.get(ResourceVariables.RESOURCE_PATH_VAR_NAME) ) );
	}
	
	public IFile getSourceFile() {
		return (IFile) this.resolvedSourceFileVariableUtil.getResource();
	}
	
	public ResourceVariableUtil getSourceFileVariableUtil() {
		return this.resolvedSourceFileVariableUtil;
	}
	
	public Map<String, IStringVariable> getSourceFileVariables() {
		return this.resolvedSourceFileVariables;
	}
	
	public ObservableValueVariable getWorkingDirectoryPathVariable() {
		return this.resolvedWorkingDirectoryPathVariable;
	}
	
	@Override
	public void handleValueChange(final ValueChangeEvent event) {
		if (event.getObservable() == this.resolvedWorkingDirectoryResourceValue) {
			final IResource resource= (IResource) event.diff.getNewValue();
			this.resolvedWorkingDirectoryPathVariable.getObservable().setValue(
					(resource != null) ? resource.getFullPath().toString() : null );
			return;
		}
	}
	
	
	@Override
	public void createControl(final Composite parent) {
		final Composite mainComposite= new Composite(parent, SWT.NONE);
		setControl(mainComposite);
		mainComposite.setLayout(LayoutUtil.createTabGrid(1));
		
		if (this.presets != null) {
			final DropDownButton button= new DropDownButton(mainComposite, SWT.SINGLE);
			button.setText(Messages.MainTab_LoadPreset_label);
			final GridData gd= new GridData(SWT.RIGHT, SWT.FILL, true, false);
			button.setLayoutData(gd);
			
			final Menu menu= button.getDropDownMenu();
			final SelectionListener selectionListener= new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					loadPreset((ILaunchConfiguration) e.widget.getData());
				}
			};
			for (final ILaunchConfiguration preset : this.presets) {
				final MenuItem item= new MenuItem(menu, SWT.PUSH);
				item.setText(DocProcessingConfigPresets.getName(preset));
				item.setData(preset);
				item.addSelectionListener(selectionListener);
			}
		}
		
		{	final Composite composite= createOverviewGroup(mainComposite);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}
		{	final Composite composite= createWorkingDirectoryGroup(mainComposite);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}
		
		Dialog.applyDialogFont(parent);
		
		initBindings();
	}
	
	protected Composite createOverviewGroup(final Composite parent) {
		final Group group= new Group(parent, SWT.NONE);
		group.setLayout(LayoutUtil.createGroupGrid(3));
		group.setText(Messages.MainTab_Overview_label);
		
		{	final Label label= new Label(group, SWT.NONE);
			label.setText(Messages.MainTab_Overview_Step_header);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		}
		{	final Label label= new Label(group, SWT.NONE);
			label.setText(Messages.MainTab_Overview_Run_header);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		}
		{	final Label label= new Label(group, SWT.NONE);
			label.setText(Messages.MainTab_Overview_Detail_header);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		}
		{	final Label filler = new Label(group, SWT.NONE);
			filler.addPaintListener(new PaintListener() {
				@Override
				public void paintControl(final PaintEvent e) {
					final GC gc= e.gc;
					gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
					gc.drawLine(e.x, 0, e.width, 0);
				}
			});
			final GridData gd= new GridData(SWT.FILL, SWT.FILL, false, false);
			gd.horizontalSpan= 3;
			gd.heightHint= LayoutUtil.defaultVSpacing() / 2;
			filler.setLayoutData(gd);
		}
		
		for (final StepItem stepItem : this.stepItems) {
			final Label label= new Label(group, SWT.NONE);
			label.setText(stepItem.tab.getNum() + ")\u2002" + stepItem.tab.getLabel()); //$NON-NLS-1$
			label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
			
			final Button button= new Button(group, SWT.CHECK);
			button.setEnabled(false);
			button.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false));
			stepItem.enabledControl= button;
			
			final Label info= new Label(group, SWT.NONE);
			info.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			stepItem.infoControl= info;
			
			stepItem.updateInfo();
			
			LayoutUtil.addSmallFiller(group, false);
		}
		
		return group;
	}
	
	protected Composite createWorkingDirectoryGroup(final Composite parent) {
		final ResourceInputComposite pathInput= new ResourceInputComposite(parent,
				ResourceInputComposite.STYLE_GROUP | ResourceInputComposite.STYLE_TEXT,
				ResourceInputComposite.MODE_DIRECTORY | ResourceInputComposite.MODE_OPEN | ResourceInputComposite.MODE_WS_ONLY,
				Messages.MainTab_WorkingDir_label + " (workspace path)");
		pathInput.getValidator().setResourceLabel(
				MessageUtil.removeMnemonics(Messages.MainTab_WorkingDir_label) );
		pathInput.setShowInsertVariable(true, DialogUtil.DEFAULT_INTERACTIVE_RESOURCE_FILTERS, null);
		
		final Map<String, IStringVariable> variables= new HashMap<>();
		variables.putAll(getSourceFileVariables());
		pathInput.getValidator().setVariableResolver(new VariableText2(variables));
		this.workingDirectoryControl= pathInput;
		
		this.resolvedWorkingDirectoryResourceValue= pathInput.getValidator().getWorkspaceResourceObservable();
		this.resolvedWorkingDirectoryResourceValue.addValueChangeListener(this);
		
		return pathInput;
	}
	
	
	protected void loadPreset(final ILaunchConfiguration preset) {
		try {
			this.presetToLoad= preset.getAttributes();
			setDirty(true);
			updateLaunchConfigurationDialog();
			
			if (this.presetLoaded != null) {
				final ILaunchConfiguration config= this.presetLoaded;
				final BitSet presetSteps= DocProcessingConfigPresets.getSteps(preset);
				for (final StepItem stepItem : this.stepItems) {
					if (presetSteps.get(stepItem.tab.getNum())) {
						stepItem.tab.initializeFrom(config);
					}
				}
			}
		}
		catch (final Exception e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, 0,
						"An error occurred while loading the document processing preset.", e ),
					(StatusManager.LOG | StatusManager.SHOW) );
		}
		finally {
			this.presetToLoad= null;
			this.presetLoaded= null;
		}
	}
	
	@Override
	public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(DocProcessingConfig.WORKING_DIRECTORY_ATTR_NAME, WORKING_DIRECTORY_DEFAULT_VALUE);
	}
	
	@Override
	protected void addBindings(final DataBindingContext dbc) {
		dbc.bindValue(this.workingDirectoryControl.getObservable(),
				this.workingDirectoryValue,
				new UpdateValueStrategy().setAfterGetValidator(
						new UpdateableErrorValidator(this.workingDirectoryControl.getValidator()) ),
				null);
	}
	
	@Override
	protected void doInitialize(final ILaunchConfiguration configuration) {
		{	String path= WORKING_DIRECTORY_DEFAULT_VALUE;
			try {
				path= configuration.getAttribute(DocProcessingConfig.WORKING_DIRECTORY_ATTR_NAME, path);
			}
			catch (final CoreException e) {
				logReadingError(e);
			}
			this.workingDirectoryValue.setValue(path);
		}
	}
	
	@Override
	protected void doSave(final ILaunchConfigurationWorkingCopy configuration) {
		{	final String path= (String) this.workingDirectoryValue.getValue();
			configuration.setAttribute(DocProcessingConfig.WORKING_DIRECTORY_ATTR_NAME, path);
		}
		
		if (this.presetToLoad != null) {
			try {
				for (final Entry<String, Object> entry : this.presetToLoad.entrySet()) {
					final String name= entry.getKey();
					if (DocProcessingConfigPresets.isInternalArgument(name)) {
						continue;
					}
					final Object value= entry.getValue();
					if (value instanceof String) {
						configuration.setAttribute(name, (String) value);
					}
					else if (value instanceof Integer) {
						configuration.setAttribute(name, (Integer) value);
					}
					else if (value instanceof Boolean) {
						configuration.setAttribute(name, (Boolean) value);
					}
					else if (value instanceof List) {
						configuration.setAttribute(name, (List) value);
					}
					else if (value instanceof Map) {
						configuration.setAttribute(name, (Map) value);
					}
				}
				this.presetLoaded= configuration;
			}
			finally {
				this.presetToLoad= null;
			}
		}
	}

}
