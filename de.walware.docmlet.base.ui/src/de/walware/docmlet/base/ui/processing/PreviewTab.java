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

import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.walware.jcommons.collections.ImCollections;

import de.walware.ecommons.databinding.core.observable.WritableEqualityValue;
import de.walware.ecommons.debug.core.variables.ObservableResourcePathVariable;
import de.walware.ecommons.debug.core.variables.ResourceVariableResolver;
import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.ui.util.UIAccess;
import de.walware.ecommons.variables.core.VariableUtils;

import de.walware.docmlet.base.internal.ui.processing.Messages;
import de.walware.docmlet.base.ui.DocBaseUIResources;
import de.walware.docmlet.base.ui.processing.DocProcessingConfig.Format;
import de.walware.docmlet.base.ui.processing.operations.OpenUsingDocViewerOperationSettings;
import de.walware.docmlet.base.ui.processing.operations.OpenUsingEclipseOperation;
import de.walware.docmlet.base.ui.processing.operations.OpenUsingEclipseOperationSettings;
import de.walware.docmlet.base.ui.processing.operations.RunExternalProgramOperationSettings;


public class PreviewTab extends DocProcessingConfigStepTab
		implements DocProcessingConfigStepTab.Listener {
	
	
	private final DocProcessingConfigIOStepTab produceTab;
	
	private final IObservableValue inputFormatValue;
	
	private final IObservableValue resolvedInputFileValue;
	
	private Label inputControl;
	
	
	public PreviewTab(final DocProcessingConfigMainTab mainTab,
			final DocProcessingConfigIOStepTab produceTab) {
		super(mainTab, DocProcessingConfig.BASE_PREVIEW_ATTR_QUALIFIER);
		
		this.produceTab= produceTab;
		this.produceTab.addListener(this);
		
		final Realm realm= getRealm();
		this.inputFormatValue= new WritableValue(realm, null, Format.class);
		this.inputFormatValue.addValueChangeListener(this);
		
		this.resolvedInputFileValue= new WritableEqualityValue(realm, null, IFile.class);
		
		setAvailableOperations(ImCollections.newList(
				new OpenUsingEclipseOperationSettings(),
				new OpenUsingDocViewerOperationSettings(),
				new RunExternalProgramOperationSettings() ));
		
		produceTab.getOutputFileValue().addValueChangeListener(this);
		
		changed(this.produceTab);
	}
	
	
	@Override
	public Image getImage() {
		return DocBaseUIResources.INSTANCE.getImage(DocBaseUIResources.TOOL_PREVIEW_IMAGE_ID);
	}
	
	@Override
	public String getName() {
		return createName(Messages.PreviewTab_name);
	}
	
	@Override
	public String getLabel() {
		return Messages.Preview_label;
	}
	
	
	@Override
	protected void initVariables(final Map<String, IStringVariable> variables) {
		variables.putAll(getMainTab().getSourceFileVariables());
		
		VariableUtils.add(variables, new ObservableResourcePathVariable( // required for updates
				DocProcessingConfig.IN_FILE_PATH_VAR,
				this.resolvedInputFileValue ));
		VariableUtils.add(variables, INPUT_RESOURCE_VAR_DEFS,
				new ResourceVariableResolver(new ResourceVariableResolver.IResolveContext() {
					@Override
					public IResource getResource() {
						return (IResource) PreviewTab.this.resolvedInputFileValue.getValue();
					}
				}, ResourceVariableResolver.EXISTS_NEVER ));
	}
	
	protected void setInput(final Format format, final IFile file) {
		if (format != null && format.equals(this.inputFormatValue.getValue())) {
			this.resolvedInputFileValue.setValue(file);
		}
		else if (file == null || !file.equals(this.resolvedInputFileValue.getValue())) {
			this.resolvedInputFileValue.setValue(null);
			this.inputFormatValue.setValue(format);
			this.resolvedInputFileValue.setValue(file);
		}
		else {
			this.inputFormatValue.setValue(format);
		}
	}
	
	public Format getInputFormat() {
		return (Format) this.inputFormatValue.getValue();
	}
	
	public IFile getInputFile() {
		return (IFile) this.resolvedInputFileValue.getValue();
	}
	
	public IObservableValue getInputFileValue() {
		return this.resolvedInputFileValue;
	}
	
	@Override
	public String getInfo() {
		final StringBuilder sb= getStringBuilder();
		
		final Format inputFormat= getInputFormat();
		sb.append((inputFormat != null) ? inputFormat.getInfoLabel() : "?"); //$NON-NLS-1$
		
		sb.append('\n');
		final DocProcessingOperationSettings operation= getOperation();
		sb.append((operation != null) ? operation.getInfo() : " "); //$NON-NLS-1$
		
		return sb.toString();
	}
	
	@Override
	public void changed(final DocProcessingConfigStepTab source) {
		if (source == this.produceTab) {
			updateInput();
		}
	}
	
	@Override
	public void handleValueChange(final ValueChangeEvent event) {
		if (event.getObservable() == this.produceTab.getOutputFileValue()) {
			updateInput();
			return;
		}
		if (event.getObservable() == this.inputFormatValue) {
			updateInputText();
		}
		super.handleValueChange(event);
	}
	
	private void updateInput() {
		final Format format= this.produceTab.getOutputFormat();
		setInput((format != null) ?
						DocProcessingConfig.createOutputFormat(format) :
						null,
				this.produceTab.getOutputFile() );
	}
	
	
	@Override
	protected void addControls(final Composite parent) {
		{	final Composite composite= createFormatGroup(parent);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}
		{	final Composite composite= createOperationGroup(parent);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		}
	}
	
	protected Composite createFormatGroup(final Composite parent) {
		final Group group= new Group(parent, SWT.NONE);
		group.setLayout(LayoutUtil.createGroupGrid(4));
		group.setText("IO"); //$NON-NLS-1$
		
		{	final Label label= new Label(group, SWT.NONE);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			label.setText(Messages.StepTab_In_label);
		}
		{	final Label label= new Label(group, SWT.NONE);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
			
			this.inputControl= label;
			updateInputText();
		}
		
		LayoutUtil.addSmallFiller(group, false);
		
		return group;
	}
	
	@Override
	protected String getOperationsLabel() {
		return Messages.PreviewTab_Operations_label;
	}
	
	protected void updateInputText() {
		final Format inputFormat= getInputFormat();
		if (!UIAccess.isOkToUse(this.inputControl) || inputFormat == null) {
			return;
		}
		this.inputControl.setText(inputFormat.getInfoLabel());
		this.inputControl.getParent().layout(true);
	}
	
	@Override
	protected void addBindings(final DataBindingContext dbc) {
		super.addBindings(dbc);
	}
	
	
	@Override
	protected String getDefaultOperationId() {
		return OpenUsingEclipseOperation.ID;
	}
	
	@Override
	public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
		super.setDefaults(configuration);
	}
	
	@Override
	protected void doInitialize(final ILaunchConfiguration configuration) {
		super.doInitialize(configuration);
	}
	
	@Override
	protected void doSave(final ILaunchConfigurationWorkingCopy configuration) {
		super.doSave(configuration);
	}
	
}
