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

package de.walware.docmlet.base.ui.processing;

import static de.walware.docmlet.base.ui.processing.DocProcessingConfig.OUT_FILE_EXT_VAR_NAME;
import static de.walware.docmlet.base.ui.processing.DocProcessingConfig.STEP_OUTPUT_FILE_PATH_ATTR_KEY;
import static de.walware.docmlet.base.ui.processing.DocProcessingConfig.STEP_OUTPUT_FORMAT_ATTR_KEY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.databinding.core.observable.WritableEqualityValue;
import de.walware.ecommons.databinding.core.util.UpdateableErrorValidator;
import de.walware.ecommons.debug.core.variables.ObservableResourcePathVariable;
import de.walware.ecommons.debug.core.variables.ResourceVariableResolver;
import de.walware.ecommons.debug.core.variables.ResourceVariables;
import de.walware.ecommons.ui.util.DialogUtil;
import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.ui.util.UIAccess;
import de.walware.ecommons.ui.workbench.ResourceInputComposite;
import de.walware.ecommons.variables.core.ObservableValueVariable;
import de.walware.ecommons.variables.core.VariableText2;
import de.walware.ecommons.variables.core.VariableUtils;

import de.walware.docmlet.base.internal.ui.processing.Messages;
import de.walware.docmlet.base.ui.processing.DocProcessingConfig.CustomExtFormat;
import de.walware.docmlet.base.ui.processing.DocProcessingConfig.Format;


public abstract class DocProcessingConfigIOStepTab extends DocProcessingConfigStepTab {
	
	
	private static final String OUTPUT_PATH_DEFAULT_VALUE=
			"${" + ResourceVariables.FILE_NAME_BASE_VAR_NAME + "}" + //$NON-NLS-1$ //$NON-NLS-2$
			".${" + DocProcessingConfig.OUT_FILE_EXT_VAR_NAME + "}"; //$NON-NLS-1$ //$NON-NLS-2$
	
	
	private final String outputFormatAttrName;
	private final String outputFilePathAttrName;
	
	private final IObservableValue inputFormatValue;
	private final IObservableValue outputFormatValue;
	private final IObservableValue outputFileExtValue;
	private final IObservableValue outputFilePathValue;
	
	private ImList<Format> availableOutputFormats;
	private Format defaultOutputFormat;
	
	private final IObservableValue resolvedInputFileValue;
	private final IObservableValue resolvedOutputFileExtValue;
	private final IObservableValue resolvedOutputFileValue;
	private final Matcher validExtMatcher= DocProcessingConfig.VALID_EXT_PATTERN.matcher(""); //$NON-NLS-1$
	
	private Label inputControl;
	private ComboViewer outputViewer;
	private Text outputExtControl;
	private ResourceInputComposite outputPathControl;
	private IObservableValue outputPathResourceValue;
	
	
	public DocProcessingConfigIOStepTab(final DocProcessingConfigMainTab mainTab,
			final String stepEnabledAttrName) {
		super(mainTab, stepEnabledAttrName);
		
		this.outputFormatAttrName= getAttrQualifier() + '/' + STEP_OUTPUT_FORMAT_ATTR_KEY;
		this.outputFilePathAttrName= getAttrQualifier() + '/' + STEP_OUTPUT_FILE_PATH_ATTR_KEY;
		
		final Realm realm= getRealm();
		this.inputFormatValue= new WritableValue(realm, null, Format.class);
		this.inputFormatValue.addValueChangeListener(this);
		this.outputFormatValue= new WritableValue(realm, null, Format.class);
		this.outputFormatValue.addValueChangeListener(this);
		this.outputFileExtValue= new WritableValue(realm, null, String.class);
		this.outputFileExtValue.addValueChangeListener(this);
		this.outputFilePathValue= new WritableValue(realm, null, String.class);
		
		this.resolvedInputFileValue= new WritableEqualityValue(realm, null, IFile.class);
		this.resolvedInputFileValue.addValueChangeListener(this);
		this.resolvedOutputFileExtValue= new WritableEqualityValue(realm, null, String.class);
		this.resolvedOutputFileValue= new WritableEqualityValue(realm, null, IFile.class);
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
						return (IResource) DocProcessingConfigIOStepTab.this.resolvedInputFileValue.getValue();
					}
				}, ResourceVariableResolver.EXISTS_NEVER ));
		VariableUtils.add(variables, new ObservableValueVariable(
				DocProcessingConfig.OUT_FILE_EXT_VAR,
				this.resolvedOutputFileExtValue ));
		VariableUtils.add(variables, new ObservableResourcePathVariable(
				DocProcessingConfig.OUT_FILE_PATH_VAR,
				this.resolvedOutputFileValue ));
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
	
	protected void setAvailableOutputFormats(final List<Format> formats, final String defaultKey) {
		if (formats == null) {
			throw new NullPointerException("formats"); //$NON-NLS-1$
		}
		if (defaultKey == null) {
			throw new NullPointerException("defaultKey"); //$NON-NLS-1$
		}
		this.availableOutputFormats= ImCollections.toList(formats);
		this.defaultOutputFormat= DocProcessingConfig.getFormat(formats, null, defaultKey);
		if (this.defaultOutputFormat == null) {
			this.availableOutputFormats= null;
			throw new IllegalArgumentException("defaultKey: default format not found"); //$NON-NLS-1$
		}
		
		if (this.outputViewer != null) {
			this.outputViewer.setInput(formats);
		}
	}
	
	protected Format getOutputFormat(final String key, final boolean fallbackDefault) {
		return DocProcessingConfig.getFormat(this.availableOutputFormats,
				(fallbackDefault) ? this.defaultOutputFormat : null,
				key );
	}
	
	protected void setOutputFormat(final Format format) {
		this.outputFormatValue.setValue(format);
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
	
	public Format getOutputFormat() {
		return (Format) this.outputFormatValue.getValue();
	}
	
	public IObservableValue getOutputFileExtValue() {
		return this.resolvedOutputFileExtValue;
	}
	
	public IFile getOutputFile() {
		return (IFile) this.resolvedOutputFileValue.getValue();
	}
	
	public IObservableValue getOutputFileValue() {
		return this.resolvedOutputFileValue;
	}
	
	
	@Override
	public String getInfo() {
		final StringBuilder sb= getStringBuilder();
		
		final Format inputFormat= getInputFormat();
		final Format outputFormat= getOutputFormat();
		sb.append((inputFormat != null) ? inputFormat.getInfoLabel() : "?"); //$NON-NLS-1$
		sb.append("\u2002\u2192\u2002"); //$NON-NLS-1$
		sb.append((outputFormat != null) ? outputFormat.getInfoLabel() : "?"); //$NON-NLS-1$
		
		sb.append('\n');
		final DocProcessingOperationSettings operation= getOperation();
		sb.append((operation != null) ? operation.getInfo() : " "); //$NON-NLS-1$
		
		return sb.toString();
	}
	
	@Override
	public void handleValueChange(final ValueChangeEvent event) {
		if (event.getObservable() == this.inputFormatValue) {
			updateInputText();
			resolveInputFile();
		}
		if (event.getObservable() == this.outputFormatValue) {
			final Format format= (Format) event.diff.getNewValue();
			this.outputExtControl.setEditable(format instanceof CustomExtFormat);
			this.outputFileExtValue.setValue((format != null) ? format.getExt() : ""); //$NON-NLS-1$
			resolveOutputFileExt();
		}
		if (event.getObservable() == this.outputFileExtValue) {
			Format format= (Format) this.outputFormatValue.getValue();
			if (format instanceof CustomExtFormat) {
				final String ext= (String) event.diff.getNewValue();
				if (!ext.equals(format.getExt())) {
					format= new CustomExtFormat((CustomExtFormat) format, ext);
					this.outputFormatValue.setValue(format);
				}
			}
			return;
		}
		
		if (event.getObservable() == this.resolvedInputFileValue) {
			resolveOutputFileExt();
			return;
		}
		if (event.getObservable() == this.outputPathResourceValue) {
			final IResource resource= (IResource) event.diff.getNewValue();
			this.resolvedOutputFileValue.setValue((resource instanceof IFile) ? resource : null);
			return;
		}
		
		super.handleValueChange(event);
	}
	
	protected void resolveInputFile() {
		IFile file= null;
		try {
			final Format format= getInputFormat();
			if (format != null) {
				if (format.matches(DocProcessingConfig.SOURCE_FORMAT_KEY)) {
					file= getMainTab().getSourceFile();
				}
				else {
					file= getInputFile(format);
				}
			}
		}
		catch (final Exception e) {}
		this.resolvedInputFileValue.setValue(file);
	}
	
	protected IFile getInputFile(final Format format) throws CoreException {
		return null;
	}
	
	protected void resolveOutputFileExt() {
		String ext= null;
		try {
			final Format format= getOutputFormat();
			if (format != null) {
				final IFile inputFile= getInputFile();
				ext= format.getExt(getValidExt((inputFile != null) ? inputFile.getName() : null));
			}
			if (ext != null && !this.validExtMatcher.reset(ext).matches()) {
				ext= null;
			}
		}
		catch (final Exception e) {}
		this.resolvedOutputFileExtValue.setValue(ext);
	}
	
	private String getValidExt(final String name) {
		if (name != null) {
			final int idx= name.lastIndexOf('.');
			if (idx >= 0) {
				final String ext= name.substring(idx + 1);
				if (this.validExtMatcher.reset(ext).matches()) {
					return ext;
				}
			}
		}
		return null;
	}
	
	
	@Override
	protected void addControls(final Composite parent) {
		{	final Composite group= createIOGroup(parent);
			group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}
		{	final Composite group= createOperationGroup(parent);
			group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		}
	}
	
	protected Composite createIOGroup(final Composite parent) {
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
		
		{	final Label label= new Label(group, SWT.NONE);
			label.setText(Messages.StepTab_Out_label);
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		}
		{	{	final Label label= new Label(group, SWT.NONE);
				label.setText(Messages.StepTab_Out_Format_label + ':');
				label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			}
			
			final Composite composite= new Composite(group, SWT.NONE);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
			composite.setLayout(LayoutUtil.createCompositeGrid(3));
			
			{	final ComboViewer viewer= new ComboViewer(composite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
				final Combo combo= viewer.getCombo();
				final LabelProvider labelProvider= new LabelProvider();
				
				viewer.setContentProvider(new ArrayContentProvider());
				viewer.setLabelProvider(labelProvider);
				viewer.setInput(this.availableOutputFormats);
				
				final GridData gd= new GridData(SWT.FILL, SWT.CENTER, false, false);
				gd.widthHint= LayoutUtil.hintWidth(combo, this.availableOutputFormats, labelProvider);
				combo.setLayoutData(gd);
				this.outputViewer= viewer;
			}
			{	final Label label= new Label(composite, SWT.NONE);
				label.setText(Messages.StepTab_Out_FileExt_label + " (" + OUT_FILE_EXT_VAR_NAME + "):"); //$NON-NLS-1$ //$NON-NLS-2$
				label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
			}
			{	final Text text= new Text(composite, SWT.BORDER);
				text.setEditable(false);
				
				final GridData gd= new GridData(SWT.LEFT, SWT.CENTER, true, false);
				gd.widthHint= LayoutUtil.hintWidth(text, 25);
				text.setLayoutData(gd);
				this.outputExtControl= text;
			}
		}
		LayoutUtil.addGDDummy(group); // To
		{	final Label label= new Label(group, SWT.NONE);
			label.setText(Messages.StepTab_Out_FilePath_label + ':');
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		}
		{	final ResourceInputComposite input= new ResourceInputComposite(group,
					ResourceInputComposite.STYLE_TEXT,
					ResourceInputComposite.MODE_FILE | ResourceInputComposite.MODE_WS_ONLY, 
					Messages.StepTab_Out_FilePath_label );
			
			input.setShowInsertVariable(true, DialogUtil.DEFAULT_INTERACTIVE_RESOURCE_FILTERS, null);
			input.getValidator().setOnExisting(IStatus.OK);
			input.getValidator().setOnLateResolve(IStatus.OK);
			input.getValidator().setRelative(getMainTab().getWorkingDirectoryPathVariable(), -1);
			input.getValidator().setIgnoreRelative(true);
			
			final Map<String, IStringVariable> variables= new HashMap<>();
			variables.putAll(getStepVariables());
			variables.remove(DocProcessingConfig.OUT_FILE_PATH_VAR_NAME);
			input.getValidator().setVariableResolver(new VariableText2(variables));
			
			input.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			
			this.outputPathControl= input;
			
			this.outputPathResourceValue= input.getValidator().getWorkspaceResourceObservable();
			this.outputPathResourceValue.addValueChangeListener(this);
		}
		{	final Label label= new Label(group, SWT.NONE);
			label.setText("(relative to working directory)");
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		}
		
		return group;
	}
	
	protected void updateInputText() {
		final Format inputFormat= getInputFormat();
		if (!UIAccess.isOkToUse(this.inputControl) || inputFormat == null) {
			return;
		}
		this.inputControl.setText(inputFormat.getInfoLabel());
	}
	
	@Override
	protected void addBindings(final DataBindingContext dbc) {
		super.addBindings(dbc);
		
		final IViewerObservableValue outputFormatObservable= ViewersObservables.observeSingleSelection(this.outputViewer);
		dbc.bindValue(outputFormatObservable, this.outputFormatValue,
				null,
				new UpdateValueStrategy().setConverter(new IConverter() {
					@Override
					public Object getToType() {
						return Format.class;
					}
					@Override
					public Object getFromType() {
						return Format.class;
					}
					@Override
					public Object convert(final Object fromObject) {
						if (fromObject != null) {
							return DocProcessingConfig.getFormat(
									DocProcessingConfigIOStepTab.this.availableOutputFormats,
									((Format) fromObject).getKey() );
						}
						return null;
					}
				}));
		
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(this.outputExtControl),
				this.outputFileExtValue,
				new UpdateValueStrategy(), //?
				null );
		
		dbc.bindValue(this.outputPathControl.getObservable(),
				this.outputFilePathValue,
				new UpdateValueStrategy().setAfterGetValidator(
						new UpdateableErrorValidator(this.outputPathControl.getValidator()) ),
				null );
	}
	
	
	@Override
	public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
		super.setDefaults(configuration);
		
		configuration.setAttribute(this.outputFormatAttrName, this.defaultOutputFormat.getKey());
		configuration.setAttribute(this.outputFilePathAttrName, OUTPUT_PATH_DEFAULT_VALUE);
	}
	
	@Override
	protected void doInitialize(final ILaunchConfiguration configuration) {
		super.doInitialize(configuration);
		
		{	String key= this.defaultOutputFormat.getKey();
			try {
				key= configuration.getAttribute(this.outputFormatAttrName, key);
			}
			catch (final CoreException e) {
				logReadingError(e);
			}
			final Format format= getOutputFormat(key, true);
			this.outputFormatValue.setValue(format);
		}
		{	String path= OUTPUT_PATH_DEFAULT_VALUE;
			try {
				path= configuration.getAttribute(this.outputFilePathAttrName, path);
			}
			catch (final CoreException e) {
				logReadingError(e);
			}
			this.outputFilePathValue.setValue(path);
		}
	}
	
	@Override
	protected void doSave(final ILaunchConfigurationWorkingCopy configuration) {
		super.doSave(configuration);
		
		{	final Format format= getOutputFormat();
			configuration.setAttribute(this.outputFormatAttrName, format.getKey());
		}
		{	final String path= (String) this.outputFilePathValue.getValue();
			configuration.setAttribute(this.outputFilePathAttrName, path);
		}
	}
	
	
}
