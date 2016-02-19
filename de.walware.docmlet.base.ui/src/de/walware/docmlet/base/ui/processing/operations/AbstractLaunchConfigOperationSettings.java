/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.base.ui.processing.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.statushandlers.StatusManager;

import de.walware.ecommons.ICommonStatusConstants;
import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.ui.util.UIAccess;

import de.walware.docmlet.base.internal.ui.processing.Messages;
import de.walware.docmlet.base.ui.DocBaseUI;
import de.walware.docmlet.base.ui.processing.DocProcessingConfigStepTab;
import de.walware.docmlet.base.ui.processing.DocProcessingOperationSettings;


public abstract class AbstractLaunchConfigOperationSettings extends DocProcessingOperationSettings {
	
	
	private final String launchConfigTypeId;
	
	private final String launchConfigNameAttrName;
	
	private ILaunchManager launchManager;
	private ILaunchConfigurationType launchConfigType;
	private ILaunchConfigurationListener launchConfigListener;
	private List<ILaunchConfiguration> availablelaunchConfigs;
	
	private WritableValue launchConfigNameValue;
	
	private TableViewer launchConfigViewer;
	private Button launchConfigNewControl;
	
	
	public AbstractLaunchConfigOperationSettings(final String launchConfigTypeId) {
		this.launchConfigTypeId= launchConfigTypeId;
		
		this.launchConfigNameAttrName= getId() + '/' + AbstractLaunchConfigOperation.LAUNCH_CONFIG_NAME_ATTR_KEY;
	}
	
	
	@Override
	public String getInfo() {
		final String label= getLabel();
		final String name= (String) this.launchConfigNameValue.getValue();
		return label + ":  " + ((name != null) ? limitInfo(name) : "?"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	protected void init(final DocProcessingConfigStepTab tab) {
		super.init(tab);
		
		final Realm realm= getRealm();
		this.launchConfigNameValue= new WritableValue(realm, null, String.class);
		
		this.launchManager= DebugPlugin.getDefault().getLaunchManager();
		this.launchConfigType= this.launchManager.getLaunchConfigurationType(this.launchConfigTypeId);
		if (this.launchConfigType == null) {
			throw new RuntimeException("Launch configuration type is missing: id= " + this.launchConfigTypeId); //$NON-NLS-1$
		}
		this.launchConfigListener= new ILaunchConfigurationListener() {
			@Override
			public void launchConfigurationAdded(final ILaunchConfiguration configuration) {
				updateAvailableConfigs();
			}
			@Override
			public void launchConfigurationChanged(final ILaunchConfiguration configuration) {
				updateAvailableConfigs();
			}
			@Override
			public void launchConfigurationRemoved(final ILaunchConfiguration configuration) {
				updateAvailableConfigs();
			}
		};
		this.launchManager.addLaunchConfigurationListener(this.launchConfigListener);
		updateAvailableConfigs();
	}
	
	@Override
	protected void dispose() {
		if (this.launchManager != null) {
			this.launchManager.removeLaunchConfigurationListener(this.launchConfigListener);
		}
		
		super.dispose();
	}
	
	@Override
	protected Composite createControl(final Composite parent) {
		final Composite composite= super.createControl(parent);
		composite.setLayout(LayoutUtil.createCompositeGrid(2));
		
		{	final Label label= new Label(composite, SWT.NONE);
			label.setText(Messages.ProcessingOperation_RunLaunchConfigSettings_List_label);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		}
		
		{	final TableViewer viewer= new TableViewer(composite,
					SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
			
			viewer.setLabelProvider(DebugUITools.newDebugModelPresentation());
			viewer.setContentProvider(new ArrayContentProvider());
			viewer.setInput(new Object());
			
			final GridData gd= new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			gd.heightHint= LayoutUtil.hintHeight(viewer.getTable(), 5);
			viewer.getControl().setLayoutData(gd);
			this.launchConfigViewer= viewer;
		}
		{	final Button button= new Button(composite, SWT.PUSH);
			
			button.setText(Messages.ProcessingOperation_RunLaunchConfigSettings_New_label);
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					createNewLaunchConfig();
				}
				
			});
			
			final GridData gd= new GridData(SWT.FILL, SWT.TOP, false, false);
			gd.widthHint= LayoutUtil.hintWidth(button);
			button.setLayoutData(gd);
			this.launchConfigNewControl= button;
		}
		return composite;
	}
	
	@Override
	protected void addBindings(final DataBindingContext dbc) {
		dbc.bindValue(ViewerProperties.singleSelection().observe(this.launchConfigViewer),
				this.launchConfigNameValue,
				new UpdateValueStrategy().setAfterGetValidator(new IValidator() {
					@Override
					public IStatus validate(final Object value) {
						if (value == null) {
							return ValidationStatus.error(NLS.bind(
									Messages.ProcessingOperation_RunLaunchConfigSettings_error_NoConfigSelected_message,
									getLabel() ));
						}
						return ValidationStatus.ok();
					}
				}).setConverter(new IConverter() {
					@Override
					public Object getFromType() {
						return ILaunchConfiguration.class;
					}
					@Override
					public Object getToType() {
						return String.class;
					}
					@Override
					public Object convert(final Object fromObject) {
						return ((ILaunchConfiguration) fromObject).getName();
					}
				}),
				new UpdateValueStrategy().setConverter(new IConverter() {
					@Override
					public Object getFromType() {
						return String.class;
					}
					@Override
					public Object getToType() {
						return ILaunchConfiguration.class;
					}
					@Override
					public Object convert(final Object fromObject) {
						return findLaunchConfig((String) fromObject);
					}
				}));
	}
	
	private ILaunchConfiguration findLaunchConfig(final String name) {
		for (final ILaunchConfiguration config : this.availablelaunchConfigs) {
			if (config.getName().equals(name)) {
				return config;
			}
		}
		return null;
	}
	
	private void updateAvailableConfigs() {
		try {
			final ILaunchConfiguration[] allConfigs= this.launchManager.getLaunchConfigurations(
					this.launchConfigType );
			final ArrayList<ILaunchConfiguration> filteredConfigs= new ArrayList<>(allConfigs.length);
			for (final ILaunchConfiguration config : allConfigs) {
				if (includeLaunchConfig(config)) {
					filteredConfigs.add(config);
				}
			}
			this.availablelaunchConfigs= filteredConfigs;
			if (UIAccess.isOkToUse(this.launchConfigViewer)) {
				this.launchConfigViewer.setInput(this.availablelaunchConfigs);
			}
		}
		catch (final CoreException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, ICommonStatusConstants.LAUNCHCONFIG_ERROR,
					NLS.bind("An error occurred while updating list of launch configurations for ''{0}''.",
							getLabel() ),
					e ));
		}
	}
	
	protected boolean includeLaunchConfig(final ILaunchConfiguration config) {
		return true;
	}
	
	protected void createNewLaunchConfig() {
		try {
			final String name= getLaunchConfigurationDialog().generateName(getNewLaunchConfigName());
			
			final ILaunchConfigurationWorkingCopy newConfig= this.launchConfigType.newInstance(null, name);
			
			initializeNewLaunchConfig(newConfig);
			
			this.launchConfigNameValue.setValue(name);
			
			newConfig.doSave();
		}
		catch (final CoreException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, ICommonStatusConstants.LAUNCHCONFIG_ERROR,
					NLS.bind(Messages.ProcessingOperation_RunLaunchConfigSettings_error_NewConfigFailed_message,
							getLabel() ),
					e ), StatusManager.LOG | StatusManager.SHOW);
		}
	}
	
	protected String getNewLaunchConfigName() {
		return null;
	}
	
	protected void initializeNewLaunchConfig(final ILaunchConfigurationWorkingCopy config) {
	}
	
	@Override
	protected void load(final Map<String, String> config) {
		final String name= config.get(this.launchConfigNameAttrName);
		this.launchConfigNameValue.setValue(name);
	}
	
	@Override
	protected void save(final Map<String, String> config) {
		final String name= (String) this.launchConfigNameValue.getValue();
		config.put(this.launchConfigNameAttrName, name);
	}
	
}
