/*=============================================================================#
 # Copyright (c) 2007-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.base.ui.processing.actions;

import java.util.List;
import java.util.regex.Matcher;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

import de.walware.jcommons.collections.IdentitySet;
import de.walware.jcommons.collections.ImIdentitySet;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.ui.actions.ListContributionItem;
import de.walware.ecommons.ui.actions.SimpleContributionItem;
import de.walware.ecommons.ui.actions.SubMenuContributionItem;
import de.walware.ecommons.ui.util.MessageUtil;

import de.walware.docmlet.base.internal.ui.processing.Messages;
import de.walware.docmlet.base.ui.processing.DocProcessingConfig;
import de.walware.docmlet.base.ui.processing.DocProcessingConfig.Format;
import de.walware.docmlet.base.ui.processing.DocProcessingManager;
import de.walware.docmlet.base.ui.processing.DocProcessingUI;


public class RunConfigsMenuContribution extends ListContributionItem
		implements IWorkbenchContribution, IExecutableExtension {
	
	
	private static class Data {
		
		private final IWorkbenchWindow window;
		
		private final IFile file;
		
		private final DocProcessingManager manager;
		
		private final IBindingService bindingService;
		
		private final ILaunchConfiguration activeConfig;
		
		private final IWorkbenchHelpSystem helpSystem;
		
		private Matcher validExtMatcher;
		
		
		public Data(final IWorkbenchWindow window, final IServiceLocator serviceLocator,
				final IFile file, final DocProcessingManager manager) {
			this.window= window;
			this.file= file;
			this.manager= manager;
			this.activeConfig= manager.getActiveConfig();
			this.bindingService= (serviceLocator != null) ?
					(IBindingService) serviceLocator.getService(IBindingService.class) : null;
			this.helpSystem= PlatformUI.getWorkbench().getHelpSystem();
		}
		
	}
	
	protected class ConfigContribution extends SubMenuContributionItem implements SelectionListener {
		
		
		private static final String ACTIVATE= "activate"; //$NON-NLS-1$
		private static final String EDIT= "edit"; //$NON-NLS-1$
		
		
		private final Image icon;
		
		private final String label;
		
		private final ILaunchConfiguration config;
		
		private Data data;
		
		
		public ConfigContribution(final Image icon, final String label,
				final ILaunchConfiguration configuration) {
			super();
			
			this.icon= icon;
			this.label= label;
			this.config= configuration;
		}
		
		
		protected IWorkbenchWindow getWindow() {
			return this.data.window;
		}
		
		protected IFile getFile() {
			return this.data.file;
		}
		
		protected DocProcessingManager getManager() {
			return this.data.manager;
		}
		
		@Override
		protected Image getImage() {
			return this.icon;
		}
		
		@Override
		protected String getLabel() {
			return this.label;
		}
		
		public ILaunchConfiguration getConfiguration() {
			return this.config;
		}
		
		protected boolean isActive() {
			return (this.config == this.data.activeConfig);
		}
		
		@Override
		protected void fillMenu(final Menu menu) {
			addLaunchItems(menu);
			
			{	final MenuItem item= new MenuItem(menu, SWT.RADIO);
				item.setText(Messages.ProcessingAction_ActivateConfig_label);
				item.setData(ACTIVATE);
				item.addSelectionListener(this);
				item.setSelection(isActive());
				if (this.data.helpSystem != null) {
					this.data.helpSystem.setHelp(item, DocProcessingUI.ACTIONS_ACTIVATE_CONFIG_HELP_CONTEXT_ID);
				}
			}
			{	final MenuItem item= new MenuItem(menu, SWT.PUSH);
				item.setText(Messages.ProcessingAction_EditConfig_label);
				item.setData(EDIT);
				item.addSelectionListener(this);
				if (this.data.helpSystem != null) {
					this.data.helpSystem.setHelp(item, DocProcessingUI.ACTIONS_EDIT_CONFIG_HELP_CONTEXT_ID);
				}
			}
		}
		
		protected void addLaunchItems(final Menu menu) {
		}
		
		protected void addLaunchItem(final Menu menu, 
				final ImIdentitySet<String> launchFlags,
				final String actionDetailInfo, final boolean enabled,
				String commandId, final String helpContextId) {
			if (getMode() != ActionUtil.ACTIVE_EDITOR_MODE) {
				commandId= null;
			}
			
			final StringBuilder label= getStringBuilder();
			label.append(this.data.manager.getActionLabel(launchFlags));
			if (actionDetailInfo != null) {
				label.append(actionDetailInfo);
			}
			if (isActive() && commandId != null && this.data.bindingService != null) {
				final TriggerSequence binding= this.data.bindingService.getBestActiveBindingFor(commandId);
				if (binding != null) {
					label.append('\t');
					label.append(binding.format());
				}
			}
			
			final MenuItem item= new MenuItem(menu, SWT.PUSH);
			item.setText(label.toString());
			item.setImage(this.data.manager.getActionImage(launchFlags));
			item.setData(launchFlags);
			item.addSelectionListener(this);
			item.setEnabled(enabled);
			if (this.data.helpSystem != null && helpContextId != null) {
				this.data.helpSystem.setHelp(item, helpContextId);
			}
		}
		
		
		@Override
		public void widgetDefaultSelected(final SelectionEvent e) {
		}
		
		@Override
		public void widgetSelected(final SelectionEvent e) {
			final DocProcessingManager manager= getManager();
			
			final Object data= e.widget.getData();
			if (data instanceof ImIdentitySet) {
				manager.launch(this.config, getFile(), (ImIdentitySet<String>) data);
				return;
			}
			if (data == ACTIVATE) {
				manager.setActiveConfig(this.config);
				return;
			}
			if (data == EDIT) {
				final IWorkbenchWindow window= getWindow();
				if (getMode() == ActionUtil.ACTIVE_EDITOR_MODE) {
					ActionUtil.activateActiveEditor(window);
				}
				manager.openConfigurationDialog(window.getShell(),
						new StructuredSelection(this.config) );
				return;
			}
		}
		
		
		private Matcher getValidExtMatcher(final String ext) {
			if (this.data.validExtMatcher == null) {
				this.data.validExtMatcher= DocProcessingConfig.VALID_EXT_PATTERN.matcher(ext);
			}
			else {
				this.data.validExtMatcher.reset(ext);
			}
			return this.data.validExtMatcher;
		}
		
		protected String resolveFormatExt(final Format format, String inputExt) {
			if (format == null) {
				return null;
			}
			if (inputExt != null && (inputExt.isEmpty() || !getValidExtMatcher(inputExt).matches())) {
				inputExt= null;
			}
			return format.getExt(inputExt);
		}
		
		protected String createDetail(final String inputExt, final String outputExt) {
			if (inputExt == null || outputExt == null) {
				return null;
			}
			final StringBuilder sb= getStringBuilder();
			sb.append("\u2002["); //$NON-NLS-1$
			sb.append(inputExt);
			sb.append("\u2002\u2192\u2002"); //$NON-NLS-1$
			sb.append(outputExt);
			sb.append("]"); //$NON-NLS-1$
			
			return sb.toString();
		}
		
	}
	
	private class ConfigureContribution extends SimpleContributionItem {
		
		
		private final Data data;
		
		
		public ConfigureContribution(final Data data) {
			super(Messages.ProcessingAction_CreateEditConfigs_label, null);
			
			this.data= data;
		}
		
		
		protected IWorkbenchWindow getWindow() {
			return this.data.window;
		}
		
		protected DocProcessingManager getManager() {
			return this.data.manager;
		}
		
		@Override
		protected void execute(final Event event) throws ExecutionException {
			final IWorkbenchWindow window= getWindow();
			if (getMode() == ActionUtil.ACTIVE_EDITOR_MODE) {
				ActionUtil.activateActiveEditor(window);
			}
			getManager().openConfigurationDialog(window.getShell(), null);
		}
		
	}
	
	private class ShortcutContribution extends SimpleContributionItem {
		
		
		private Data data;
		
		private final IdentitySet<String> launchFlags;
		
		
		public ShortcutContribution(final ImageDescriptor icon, final String label,
				final IdentitySet<String> launchFlags) {
			super(icon, null, label, null);
			
			this.launchFlags= launchFlags;
		}
		
		
		protected DocProcessingManager getManager() {
			return this.data.manager;
		}
		
		protected IFile getFile() {
			return this.data.file;
		}
		
		@Override
		protected void execute(final Event event) throws ExecutionException {
			getManager().launch(this.data.activeConfig, getFile(), this.launchFlags);
		}
		
	}
	
	
	private final ActionUtil util= new ActionUtil(ActionUtil.ACTIVE_MENU_SELECTION_MODE);
	
	private final StringBuilder sBuilder= new StringBuilder(32);
	
	
	public RunConfigsMenuContribution(final IContentType contentType) {
		this.util.setContentType(contentType);
	}
	
	/** For instantiation via plugin.xml */
	public RunConfigsMenuContribution() {
	}
	
	
	@Override
	public void setInitializationData(final IConfigurationElement config, final String propertyName,
			final Object data) throws CoreException {
		if (data instanceof String) {
			switch ((String) data) {
			case "activeEditor":
				setMode(ActionUtil.ACTIVE_EDITOR_MODE);
				break;
			case "activeMenuSelection":
				setMode(ActionUtil.ACTIVE_MENU_SELECTION_MODE);
				break;
			default:
				break;
			}
		}
	}
	
	protected void setMode(final byte mode) {
		this.util.setMode(mode);;
	}
	
	protected byte getMode() {
		return this.util.getMode();
	}
	
	@Override
	public void initialize(final IServiceLocator serviceLocator) {
		this.util.setServiceLocator(serviceLocator);
	}
	
	
	private StringBuilder getStringBuilder() {
		this.sBuilder.setLength(0);
		return this.sBuilder;
	}
	
	
	@Override
	protected void createContributionItems(final List<IContributionItem> items) {
		final IWorkbenchWindow window= this.util.getWindow();
		final IFile file= this.util.getFile(window);
		final DocProcessingManager manager= this.util.getManager(this.util.getContentType(window, file));
		if (manager == null) {
			return;
		}
		
		final ImList<ILaunchConfiguration> configs= manager.getAvailableConfigs();
		final Data data= new Data(window, this.util.getServiceLocator(), file, manager);
		
		if (getMode() != ActionUtil.ACTIVE_EDITOR_MODE && data.activeConfig != null) {
			items.add(createContextShortcut(data, DocProcessingUI.CommonFlags.PROCESS_AND_PREVIEW));
			items.add(createContextShortcut(data, DocProcessingUI.CommonFlags.PROCESS));
			items.add(new Separator());
		}
		
		int i= 0;
		for (int num= 1; i < configs.size(); i++, num++) {
			final ILaunchConfiguration configuration= configs.get(i);
			
			final Image image= manager.getImage(configuration);
			String mnemonic= null;
			final StringBuilder label= getStringBuilder();
			if (num > 0 && num <= 10) {
				mnemonic= Integer.toString((num % 10));
				label.append('&');
				label.append(mnemonic);
				label.append(' ');
			}
			label.append(MessageUtil.escapeForMenu(configuration.getName()));
			
			final ConfigContribution item= createConfigContribution(image, label, configuration);
			item.data= data;
			
			items.add(item);
		}
		
		if (getMode() == ActionUtil.ACTIVE_EDITOR_MODE || configs.isEmpty()) {
			items.add(new ConfigureContribution(data));
		}
	}
	
	protected ConfigContribution createConfigContribution(
			final Image icon, final StringBuilder label,
			final ILaunchConfiguration configuration) {
		return new ConfigContribution(icon, label.toString(), configuration);
	}
	
	private ShortcutContribution createContextShortcut(final Data data,
			final IdentitySet<String> launchFlags) {
		final Image icon= data.manager.getActionImage(launchFlags);
		final String label= data.manager.getLabel(data.activeConfig, launchFlags, false);
		return new ShortcutContribution(
				(icon != null) ? ImageDescriptor.createFromImage(icon) : null, label,
				launchFlags );
	}
	
}
