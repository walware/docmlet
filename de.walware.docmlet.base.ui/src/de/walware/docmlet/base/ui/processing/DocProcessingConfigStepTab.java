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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.statushandlers.StatusManager;

import de.walware.ecommons.collections.CopyOnWriteIdentityListSet;
import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.collections.ImList;
import de.walware.ecommons.debug.core.variables.ResourceVariables;
import de.walware.ecommons.debug.ui.config.LaunchConfigTabWithDbc;
import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.ui.util.UIAccess;

import de.walware.docmlet.base.internal.ui.DocBaseUIPlugin;
import de.walware.docmlet.base.internal.ui.processing.Messages;
import de.walware.docmlet.base.ui.DocBaseUI;


public abstract class DocProcessingConfigStepTab extends LaunchConfigTabWithDbc
		implements IValueChangeListener {
	
	
	public static interface Listener {
		
		
		void changed(DocProcessingConfigStepTab source);
		
	}
	
	
	private class OperationItem {
		
		private static final byte S_INITIALIZED=            0b0_00000001;
		
		private static final byte S_CONTROL_FAILED=         0b0_00100000;
		
		
		private byte state;
		
		private DocProcessingOperationSettings operation;
		
		private Composite detailControl;
		
		
		public void init(final DocProcessingOperationSettings operation) {
			operation.init(DocProcessingConfigStepTab.this);
			this.state|= S_INITIALIZED;
			this.operation= operation;
		}
		
		public void dipose() {
			if (this.operation != null && (this.state & S_INITIALIZED) != 0) {
				this.operation.dispose();
			}
		}
		
		public String getId() {
			return this.operation.getId();
		}
		
		public DocProcessingOperationSettings getOperation() {
			return this.operation;
		}
		
		@Override // for LabelProvider
		public String toString() {
			return this.operation.getLabel();
		}
		
		public Composite enable() {
			if (this.operation != null) {
				try {
					this.operation.setSelected(true);
				}
				catch (final Exception e) {
					DocBaseUIPlugin.log(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, 0,
							NLS.bind("An error occurred when enabling settings for document processing operation ''{0}''.", //$NON-NLS-1$
									getId() ),
							e ));
				}
				
				if (this.detailControl == null && (this.state & S_CONTROL_FAILED) == 0) {
					try {
						this.detailControl= this.operation.createDetailControl(
								DocProcessingConfigStepTab.this.operationDetailControl );
					}
					catch (final Exception e) {
						this.state|= S_CONTROL_FAILED;
						StatusManager.getManager().handle(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, 0,
								NLS.bind("An error occurred when creating GUI for document processing operation ''{0}''.",
										getId() ),
								e ), (StatusManager.LOG | StatusManager.SHOW) );
					}
				}
			}
			return this.detailControl;
		}
		
		public void disable() {
			if (this.operation != null) {
				try {
					this.operation.setSelected(false);
				}
				catch (final Exception e) {
					DocBaseUIPlugin.log(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, 0,
							NLS.bind("An error occurred when disabling settings for document processing operation ''{0}''.", //$NON-NLS-1$
									getId() ),
							e ));
				}
			}
		}
		
	}
	
	
	protected static final ImList<IDynamicVariable> INPUT_RESOURCE_VAR_DEFS= ResourceVariables
			.createSingleResourceVarDefs(Messages.Variable_InFileResourceVars_description_Resource_term);
	
	
	private final OperationItem nullOperationItem= new OperationItem() {
		
		@Override
		public String getId() {
			return ""; //$NON-NLS-1$
		}
		
		@Override
		public String toString() {
			return ""; //$NON-NLS-1$
		}
		
	};
	
	private final DocProcessingConfigMainTab mainTab;
	
	private final int num;
	
	private final CopyOnWriteIdentityListSet<Listener> listeners= new CopyOnWriteIdentityListSet<>();
	
	private boolean isNotifyListenerScheduled;
	
	private final String attrQualifier;
	
	private final String stepEnabledAttrName;
	private final String operationIdAttrName;
	private final String operationSettingsAttrName;
	
	private Map<String, String> operationSettings;
	
	private final WritableValue stepEnabledValue;
	private final WritableValue operationValue;
	
	private ImList<OperationItem> operations;
	
	private Button stepEnabledControl;
	
	private final Map<String, IStringVariable> stepVariables= new HashMap<>();
	
	private ComboViewer operationSelectionViewer;
	private StackLayout operationDetailLayout;
	private Composite operationDetailControl;
	
	private final StringBuilder sBuilder= new StringBuilder(32);
	
	
	public DocProcessingConfigStepTab(final DocProcessingConfigMainTab mainTab,
			final String attrQualifier) {
		this.mainTab= mainTab;
		this.num= mainTab.addStep(this);
		this.attrQualifier= attrQualifier;
		
		this.stepEnabledAttrName= getAttrQualifier() + '/' + DocProcessingConfig.STEP_ENABLED_ATTR_KEY;
		this.operationIdAttrName= getAttrQualifier() + '/' + DocProcessingConfig.STEP_OPERATION_ID_ATTR_KEY;
		this.operationSettingsAttrName= getAttrQualifier() + '/' + DocProcessingConfig.STEP_OPERATION_SETTINGS_ATTR_KEY;
		
		final Realm realm= getRealm();
		this.stepEnabledValue= new WritableValue(realm, Boolean.TRUE, Boolean.TYPE);
		this.stepEnabledValue.addValueChangeListener(this);
		this.operationValue= new WritableValue(realm, this.nullOperationItem, OperationItem.class);
		this.operationValue.addValueChangeListener(this);
	}
	
	
	@Override
	protected Realm getRealm() {
		return super.getRealm();
	}
	
	@Override
	protected DataBindingContext getDataBindingContext() {
		return super.getDataBindingContext();
	}
	
	@Override
	public void setLaunchConfigurationDialog(final ILaunchConfigurationDialog dialog) {
		super.setLaunchConfigurationDialog(dialog);
		
		initVariables(this.stepVariables);
	}
	
	@Override
	protected ILaunchConfigurationDialog getLaunchConfigurationDialog() {
		return super.getLaunchConfigurationDialog();
	}
	
	@Override
	public void dispose() {
		if (this.operations != null) {
			for (final OperationItem item : this.operations) {
				item.dipose();
			}
		}
		
		super.dispose();
	}
	
	
	protected final StringBuilder getStringBuilder() {
		this.sBuilder.setLength(0);
		return this.sBuilder;
	}
	
	
	public final DocProcessingConfigMainTab getMainTab() {
		return this.mainTab;
	}
	
	public final int getNum() {
		return this.num;
	}
	
	protected final String getAttrQualifier() {
		return this.attrQualifier;
	}
	
	protected String createName(final String text) {
		return "&" + getNum() + ") " + text; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public abstract String getLabel();
	
	public boolean isEnabled() {
		return (Boolean) this.stepEnabledValue.getValue();
	}
	
	public abstract String getInfo();
	
	public void addListener(final Listener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(final Listener listener) {
		this.listeners.remove(listener);
	}
	
	protected void scheduleNotifyListeners() {
		if (!this.isNotifyListenerScheduled) {
			this.isNotifyListenerScheduled= true;
			UIAccess.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					DocProcessingConfigStepTab.this.isNotifyListenerScheduled= false;
					notifyListeners();
				}
			});
		}
	}
	
	protected void notifyListeners() {
		for (final Listener listener : this.listeners) {
			listener.changed(this);
		}
	}
	
	
	protected abstract void initVariables(Map<String, IStringVariable> variables);
	
	public Map<String, IStringVariable> getStepVariables() {
		return this.stepVariables;
	}
	
	
	@Override
	public void createControl(final Composite parent) {
		final Composite mainComposite= new Composite(parent, SWT.NONE);
		setControl(mainComposite);
		mainComposite.setLayout(LayoutUtil.createTabGrid(1));
		
		final Composite composite= new Composite(mainComposite, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		composite.setLayout(LayoutUtil.createCompositeGrid(2));
		{	final Label label= new Label(composite, SWT.NONE);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			label.setText(getLabel() + ':');
		}
		{	final Button button= new Button(composite, SWT.CHECK);
			button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			button.setText(Messages.StepTab_Enabled_label);
			this.stepEnabledControl= button;
		}
		
		addControls(mainComposite);
		
		Dialog.applyDialogFont(parent);
		
		initBindings();
	}
	
	protected abstract void addControls(Composite parent);
	
	
	protected void setAvailableOperations(final List<DocProcessingOperationSettings> operations) {
		if (this.operations != null) {
			throw new IllegalStateException();
		}
		
		final OperationItem[] items= new OperationItem[operations.size()];
		
		for (int i= 0; i < items.length; i++) {
			items[i]= new OperationItem();
			items[i].init(operations.get(i));
		}
		
		this.operations= ImCollections.newList(items);
	}
	
	private OperationItem getOperationItem(final String id) {
		for (final OperationItem item : this.operations) {
			if (item.getId() == id) {
				return item;
			}
		}
		return this.nullOperationItem;
	}
	
	public DocProcessingOperationSettings getOperation() {
		final OperationItem item= (OperationItem) this.operationValue.getValue();
		return (item != null) ? item.getOperation() : null;
	}
	
	
	protected String getOperationsLabel() {
		return Messages.StepTab_Operations_label;
	}
	
	protected Composite createOperationGroup(final Composite parent) {
		if (this.operations == null) {
			throw new UnsupportedOperationException();
		}
		final Group group= new Group(parent, SWT.NONE);
		group.setLayout(LayoutUtil.createGroupGrid(1));
		group.setText(getOperationsLabel());
		
		{	final ComboViewer viewer= new ComboViewer(group);
			
			viewer.setLabelProvider(new LabelProvider());
			viewer.setContentProvider(new ArrayContentProvider());
			final OperationItem[] input= this.operations.toArray(new OperationItem[this.operations.size() + 1]);
			input[input.length - 1]= this.nullOperationItem;
			viewer.setInput(input);
			
			viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			this.operationSelectionViewer= viewer;
		}
		{	final Composite composite= new Composite(group, SWT.NONE);
			
			this.operationDetailLayout= new StackLayout();
			composite.setLayout(this.operationDetailLayout);
			
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			this.operationDetailControl= composite;
		}
		
		return group;
	}
	
	protected Composite createPostGroup(final Composite parent) {
		final Group group= new Group(parent, SWT.NONE);
		group.setText(Messages.StepTab_PostActions_label);
		
		return group;
	}
	
	
	@Override
	protected void addBindings(final DataBindingContext dbc) {
		dbc.bindValue(WidgetProperties.selection().observe(this.stepEnabledControl),
				this.stepEnabledValue );
		
		dbc.bindValue(ViewerProperties.singleSelection().observe(this.operationSelectionViewer),
				this.operationValue );
	}
	
	@Override
	public void handleValueChange(final ValueChangeEvent event) {
		if (event.getObservable() == this.operationValue) {
			final OperationItem oldItem= (OperationItem) event.diff.getOldValue();
			if (oldItem != null) {
				oldItem.disable();
			}
			
			final OperationItem newItem= (OperationItem) event.diff.getNewValue();
			if (newItem != null) {
				this.operationDetailLayout.topControl= newItem.enable();
			}
			else {
				this.operationDetailLayout.topControl= null;
			}
			this.operationDetailControl.layout();
		}
		
		scheduleNotifyListeners();
	}
	
	
	protected String getDefaultOperationId() {
		return null;
	}
	
	@Override
	public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(this.stepEnabledAttrName, true);
		configuration.setAttribute(this.operationIdAttrName, getDefaultOperationId());
	}
	
	@Override
	protected void doInitialize(final ILaunchConfiguration configuration) {
		{	boolean enabled= false;
			try {
				enabled= configuration.getAttribute(this.stepEnabledAttrName, enabled);
			}
			catch (final CoreException e) {
				logReadingError(e);
			}
			this.stepEnabledValue.setValue(enabled);
		}
		{	String id= ""; //$NON-NLS-1$
			try {
				id= configuration.getAttribute(this.operationIdAttrName, id);
			}
			catch (final CoreException e) {
				logReadingError(e);
			}
			id= id.intern();
			final OperationItem item= getOperationItem(id);
			this.operationValue.setValue(item);
			
			Map<String, String> settings= null;
			try {
				settings= configuration.getAttribute(this.operationSettingsAttrName,
						(Map<String, String>) null );
			}
			catch (final CoreException e) {
				logReadingError(e);
			}
			if (settings == null) {
				settings= new HashMap<>();
			}
			this.operationSettings= settings;
			
			if (item.getOperation() != null) {
				item.getOperation().load(settings);
			}
		}
	}
	
	@Override
	protected void doSave(final ILaunchConfigurationWorkingCopy configuration) {
		{	final boolean enabled= (Boolean) this.stepEnabledValue.getValue();
			configuration.setAttribute(this.stepEnabledAttrName, enabled);
		}
		{	final OperationItem item= (OperationItem) this.operationValue.getValue();
			configuration.setAttribute(this.operationIdAttrName, item.getId());
			
			Map<String, String> settings= this.operationSettings;
			
			if (item.getOperation() != null) {
				settings= new HashMap<>(settings);
				item.getOperation().save(settings);
			}
			
			configuration.setAttribute(this.operationSettingsAttrName, settings);
		}
	}
	
}
