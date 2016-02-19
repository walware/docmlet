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

package de.walware.docmlet.base.internal.ui.viewer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.databinding.core.conversion.StringTrimConverter;
import de.walware.ecommons.databinding.core.util.UpdateableErrorValidator;
import de.walware.ecommons.debug.core.variables.ResourceVariableResolver;
import de.walware.ecommons.debug.core.variables.ResourceVariables;
import de.walware.ecommons.debug.ui.config.InputArgumentsComposite;
import de.walware.ecommons.debug.ui.config.LaunchConfigPresets;
import de.walware.ecommons.debug.ui.config.LaunchConfigTabWithPresets;
import de.walware.ecommons.io.win.DDE;
import de.walware.ecommons.ui.SharedMessages;
import de.walware.ecommons.ui.SharedUIResources;
import de.walware.ecommons.ui.util.DialogUtil;
import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.ui.workbench.ResourceInputComposite;
import de.walware.ecommons.ui.workbench.ResourceVariableUtil;
import de.walware.ecommons.variables.core.VariableText2;
import de.walware.ecommons.variables.core.VariableTextValidator;
import de.walware.ecommons.variables.core.VariableUtils;

import de.walware.docmlet.base.ui.processing.DocProcessingConfig;
import de.walware.docmlet.base.ui.viewer.DocViewerConfig;


public class DocViewerMainTab extends LaunchConfigTabWithPresets {
	
	
	private static class DDETask {
		
		final String attrQualifier;
		
		final String title;
		final String label;
		
		final WritableValue commandValue;
		final WritableValue applicationValue;
		final WritableValue topicValue;
		
		InputArgumentsComposite commandControl;
		Text applicationControl;
		Text topicControl;
		
		public DDETask(final String attrQualifier, final Realm realm,
				final String title, final String label) {
			this.attrQualifier= attrQualifier;
			this.title= title;
			this.label= label;
			
			this.commandValue= new WritableValue(realm, null, String.class);
			this.applicationValue= new WritableValue(realm, null, String.class);
			this.topicValue= new WritableValue(realm, null, String.class);
		}
		
	}
	
	
	private final ResourceVariableUtil resolvedSourceFileVariableUtil;
	private final Map<String, IStringVariable> resolvedSourceFileVariables= new HashMap<>();
	
	private final WritableValue programFileValue;
	private final WritableValue programArgumentsValue;
	
	private ResourceInputComposite programFileControl;
	private InputArgumentsComposite programArgumentsControl;
	
	private final ImList<DDETask> ddeTasks;
	
	
	public DocViewerMainTab(final LaunchConfigPresets presets) {
		
		final Realm realm= getRealm();
		this.programFileValue= new WritableValue(realm, null, String.class);
		this.programArgumentsValue= new WritableValue(realm, null, String.class);
		
		setPresets(presets);
		
		this.resolvedSourceFileVariableUtil= new ResourceVariableUtil() {
			@Override
			protected IResource fetchResource() {
				final IResource resource= super.fetchResource();
				return (resource instanceof IFile) ? resource : null;
			}
		};
		
		if (DDE.isSupported()) {
			this.ddeTasks= ImCollections.newList(
					new DDETask(DocViewerConfig.TASK_VIEW_OUTPUT_ATTR_QUALIFIER,
							realm,
							Messages.MainTab_DDE_ViewOutput_label,
							Messages.DDE_ViewOutput_label ),
					new DDETask(DocViewerConfig.TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER,
							realm,
							Messages.MainTab_DDE_PreProduceOutput_label,
							Messages.DDE_PreProduceOutput_label ));
		}
		else {
			this.ddeTasks= ImCollections.emptyList();
		}
	}
	
	
	@Override
	public Image getImage() {
		return SharedUIResources.getImages().get(SharedUIResources.OBJ_MAIN_TAB_ID);
	}
	
	@Override
	public String getName() {
		return Messages.MainTab_name;
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
	
	public Map<String, IStringVariable> getSourceFileVariables() {
		return this.resolvedSourceFileVariables;
	}
	
	
	@Override
	public void createControl(final Composite parent) {
		final Composite mainComposite= new Composite(parent, SWT.NONE);
		setControl(mainComposite);
		mainComposite.setLayout(LayoutUtil.createTabGrid(1));
		
		addPresetsButton(mainComposite);
		
		{	final Composite composite= createProgramGroup(mainComposite);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}
		
		LayoutUtil.addSmallFiller(mainComposite, false);
		
		for (final DDETask ddeTask : this.ddeTasks) {
			final Composite composite= createDDEGroup(mainComposite, ddeTask);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		}
		
		LayoutUtil.addSmallFiller(mainComposite, true);
		
		{	final Label note= new Label(mainComposite, SWT.WRAP);
			note.setText(SharedMessages.Note_label + ": " + this.programArgumentsControl.getNoteText()); //$NON-NLS-1$
			note.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true));
		}
		
		Dialog.applyDialogFont(parent);
		
		initBindings();
	}
	
	protected Composite createProgramGroup(final Composite parent) {
		final Group group= new Group(parent, SWT.NONE);
		group.setLayout(LayoutUtil.createGroupGrid(1));
		group.setText(Messages.MainTab_Program_label + ':');
		
		final ResourceInputComposite pathInput= new ResourceInputComposite(group,
				ResourceInputComposite.STYLE_TEXT,
				ResourceInputComposite.MODE_FILE | ResourceInputComposite.MODE_OPEN,
				Messages.MainTab_ProgramPath_label );
		pathInput.getValidator().setResourceLabel(Messages.MainTab_ProgramPath_name);
		pathInput.getValidator().setVariableResolver(new VariableText2(getSourceFileVariables()));
		pathInput.setShowInsertVariable(true, DialogUtil.DEFAULT_INTERACTIVE_FILTERS, null);
		
		pathInput.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		this.programFileControl= pathInput;
		
		LayoutUtil.addSmallFiller(group, false);
		final InputArgumentsComposite argsInput= new InputArgumentsComposite(group,
				Messages.MainTab_ProgramArgs_label + ':' );
		
		argsInput.setVariableResolver(new VariableText2(getSourceFileVariables()));
		argsInput.setVariableFilter(DialogUtil.DEFAULT_INTERACTIVE_FILTERS);
		
		argsInput.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		this.programArgumentsControl= argsInput;
		
		return group;
	}
	
	protected Composite createDDEGroup(final Composite parent, final DDETask task) {
		final Group group= new Group(parent, SWT.NONE);
		group.setLayout(LayoutUtil.createGroupGrid(4));
		group.setText(task.title + ':');
		
		{	final Label label= new Label(group, SWT.NONE);
			label.setText(Messages.MainTab_DDECommand_label + ':');
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		}
		{	final InputArgumentsComposite input= new InputArgumentsComposite(group,
					InputArgumentsComposite.STYLE_SINGLE, null );
			input.setVariableResolver(new VariableText2(getSourceFileVariables()));
			input.setVariableFilter(DialogUtil.DEFAULT_INTERACTIVE_FILTERS);
			
			input.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
			task.commandControl= input;
		}
		{	final Label label= new Label(group, SWT.NONE);
			label.setText(Messages.MainTab_DDEApplication_label + ':');
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		}
		{	final Text text= new Text(group, SWT.BORDER);
			
			final GridData gd= new GridData(SWT.FILL, SWT.CENTER, true, false);
			gd.widthHint= LayoutUtil.hintWidth(text, 40);
			text.setLayoutData(gd);
			task.applicationControl= text;
		}
		{	final Label label= new Label(group, SWT.NONE);
			label.setText(Messages.MainTab_DDETopic_label + ':');
			label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		}
		{	final Text text= new Text(group, SWT.BORDER);
			
			final GridData gd= new GridData(SWT.FILL, SWT.CENTER, true, false);
			gd.widthHint= LayoutUtil.hintWidth(text, 40);
			text.setLayoutData(gd);
			task.topicControl= text;
		}
		
		return group;
	}
	
	
	@Override
	protected void addBindings(final DataBindingContext dbc) {
		dbc.bindValue(this.programFileControl.getObservable(),
				this.programFileValue,
				new UpdateValueStrategy().setAfterGetValidator(
						new UpdateableErrorValidator(this.programFileControl.getValidator()) ),
				null);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(
						this.programArgumentsControl.getTextControl() ),
				this.programArgumentsValue,
				new UpdateValueStrategy().setAfterGetValidator(
						new UpdateableErrorValidator(new VariableTextValidator(
								this.programArgumentsControl.getVariableResolver(),
								Messages.ProgramArgs_error_Other_message ))),
				null);
		
		for (final DDETask ddeTask : this.ddeTasks) {
			dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(ddeTask.commandControl.getTextControl()),
					ddeTask.commandValue,
					new UpdateValueStrategy().setAfterGetValidator(
							new UpdateableErrorValidator(new VariableTextValidator(
									ddeTask.commandControl.getVariableResolver(),
									NLS.bind(Messages.DDECommand_error_Other_message,
											ddeTask.label, "{0}" )))), //$NON-NLS-1$
					null);
			dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(ddeTask.applicationControl),
					ddeTask.applicationValue,
					new UpdateValueStrategy()
							.setConverter(StringTrimConverter.INSTANCE)
							.setAfterConvertValidator(
									new UpdateableErrorValidator(new VariableTextValidator(
											ddeTask.commandControl.getVariableResolver(),
											NLS.bind(Messages.DDEApplication_error_Other_message,
													ddeTask.label, "{0}" )))), //$NON-NLS-1$
					null );
			dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(ddeTask.topicControl),
					ddeTask.topicValue,
					new UpdateValueStrategy()
							.setConverter(StringTrimConverter.INSTANCE)
							.setAfterConvertValidator(
									new UpdateableErrorValidator(new VariableTextValidator(
											ddeTask.commandControl.getVariableResolver(),
											NLS.bind(Messages.DDETopic_error_Other_message,
													ddeTask.label, "{0}" )))), //$NON-NLS-1$
					null );
		}
	}
	
	
	@Override
	public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
	}
	
	@Override
	public void initializeFrom(final ILaunchConfiguration configuration) {
		super.initializeFrom(configuration);
		
		if (((String) this.programFileValue.getValue()).isEmpty()) {
			setErrorMessage(null);
		}
	}
	
	@Override
	protected void doInitialize(final ILaunchConfiguration configuration) {
		{	String path= ""; //$NON-NLS-1$
			try {
				path= configuration.getAttribute(DocViewerConfig.PROGRAM_FILE_ATTR_NAME, path);
			}
			catch (final CoreException e) {
				logReadingError(e);
			}
			this.programFileValue.setValue(path);
		}
		{	String s= ""; //$NON-NLS-1$
			try {
				s= configuration.getAttribute(DocViewerConfig.PROGRAM_ARGUMENTS_ATTR_NAME, s);
			}
			catch (final CoreException e) {
				logReadingError(e);
			}
			this.programArgumentsValue.setValue(s);
		}
		
		for (final DDETask ddeTask : this.ddeTasks) {
			{	String message= ""; //$NON-NLS-1$
				try {
					message= configuration.getAttribute(ddeTask.attrQualifier + '/' + DocViewerConfig.DDE_COMMAND_ATTR_KEY,
							message );
				}
				catch (final CoreException e) {
					logReadingError(e);
				}
				ddeTask.commandValue.setValue(message);
			}
			{	String id= ""; //$NON-NLS-1$
				try {
					id= configuration.getAttribute(ddeTask.attrQualifier + '/' + DocViewerConfig.DDE_APPLICATION_ATTR_KEY,
							id );
				}
				catch (final CoreException e) {
					logReadingError(e);
				}
				ddeTask.applicationValue.setValue(id);
			}
			{	String id= ""; //$NON-NLS-1$
				try {
					id= configuration.getAttribute(ddeTask.attrQualifier + '/' + DocViewerConfig.DDE_TOPIC_ATTR_KEY,
							id );
				}
				catch (final CoreException e) {
					logReadingError(e);
				}
				ddeTask.topicValue.setValue(id);
			}
		}
	}
	
	@Override
	protected void doSave(final ILaunchConfigurationWorkingCopy configuration) {
		{	final String path= (String) this.programFileValue.getValue();
			configuration.setAttribute(DocViewerConfig.PROGRAM_FILE_ATTR_NAME, path);
		}
		{	final String s= (String) this.programArgumentsValue.getValue();
			configuration.setAttribute(DocViewerConfig.PROGRAM_ARGUMENTS_ATTR_NAME, s);
		}
		
		for (final DDETask ddeTask : this.ddeTasks) {
			String message;
			{	message= (String) ddeTask.commandValue.getValue();
				if (message.isEmpty()) {
					message= null;
				}
				configuration.setAttribute(ddeTask.attrQualifier + '/' + DocViewerConfig.DDE_COMMAND_ATTR_KEY,
						message );
			}
			{	String name= (String) ddeTask.applicationValue.getValue();
				if (message == null && name.isEmpty()) {
					name= null;
				}
				configuration.setAttribute(ddeTask.attrQualifier + '/' + DocViewerConfig.DDE_APPLICATION_ATTR_KEY,
						name );
			}
			{	String name= (String) ddeTask.topicValue.getValue();
				if (message == null && name.isEmpty()) {
					name= null;
				}
				configuration.setAttribute(ddeTask.attrQualifier + '/' + DocViewerConfig.DDE_TOPIC_ATTR_KEY,
						name );
			}
		}
	}
	
}
