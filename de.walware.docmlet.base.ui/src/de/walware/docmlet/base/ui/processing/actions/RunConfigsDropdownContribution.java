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
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

import de.walware.jcommons.collections.CollectionUtils;
import de.walware.jcommons.collections.IdentityCollection;
import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImIdentitySet;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.ui.actions.ListContributionItem;
import de.walware.ecommons.ui.actions.SimpleContributionItem;
import de.walware.ecommons.ui.util.MessageUtil;

import de.walware.docmlet.base.ui.DocBaseUI;
import de.walware.docmlet.base.ui.processing.DocProcessingManager;


public class RunConfigsDropdownContribution extends ListContributionItem
		implements IWorkbenchContribution, IExecutableExtension {
	
	
	private static class Data {
		
		private final DocProcessingManager manager;
		
		private final IFile file;
		
		
		public Data(final DocProcessingManager manager, final IFile file) {
			this.manager= manager;
			this.file= file;
		}
		
	}
	
	
	protected class ConfigContribution extends SimpleContributionItem {
		
		
		private Data data;
		
		private final ILaunchConfiguration configuration;
		
		
		public ConfigContribution(final ImageDescriptor icon,
				final String label, final String mnemonic,
				final ILaunchConfiguration configuration) {
			super(icon, null, label, mnemonic);
			
			this.configuration= configuration;
		}
		
		
		@Override
		protected void execute(final Event event) throws ExecutionException {
			this.data.manager.setActiveConfig(this.configuration);
			this.data.manager.launch(this.configuration, this.data.file, RunConfigsDropdownContribution.this.launchFlags);
		}
		
	}
	
	
	private final ActionUtil util= new ActionUtil(ActionUtil.ACTIVE_EDITOR_MODE);
	
	private ImIdentitySet<String> launchFlags;
	
	private final StringBuilder sBuilder= new StringBuilder(32);
	
	
	public RunConfigsDropdownContribution(final IContentType contentType,
			final IdentityCollection<String> launchFlags) {
		this.util.setContentType(contentType);
		this.launchFlags= ImCollections.toIdentitySet(launchFlags);
	}
	
	/** For instantiation via plugin.xml */
	public RunConfigsDropdownContribution() {
	}
	
	
	@Override
	public void initialize(final IServiceLocator serviceLocator) {
		this.util.setServiceLocator(serviceLocator);
	}
	
	@Override
	public void setInitializationData(final IConfigurationElement config,
			final String propertyName, final Object data) throws CoreException {
		if (data instanceof Map) {
			try {
				final Map<String, String> parameters= (Map<String, String>) data;
				{	final String s= parameters.get(ActionUtil.CONTENT_TYPE_PAR_NAME);
					if (s != null) {
						this.util.setContentType(Platform.getContentTypeManager().getContentType(s));
					}
				}
				{	final String s= parameters.get(ActionUtil.LAUNCH_FLAGS_PAR_NAME);
					if (s != null) {
						this.launchFlags= CollectionUtils.toIdentifierSet(s.split(";")); //$NON-NLS-1$
					}
				}
			}
			catch (final IllegalArgumentException e) {
				throw new CoreException(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, 0,
						NLS.bind("Invalid declaration of contribution by ''{0}''.", //$NON-NLS-1$
								config.getContributor().getName() ),
						e ));
			}
		}
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
		final Data data= new Data(manager, file);
		
		int i= 0;
		for (int num= 1; i < configs.size(); i++, num++) {
			final ILaunchConfiguration configuration= configs.get(i);
			
			final ImageDescriptor icon= manager.getImageDescriptor(configuration);
			String mnemonic= null;
			final StringBuilder label= getStringBuilder();
			if (num > 0 && num <= 10) {
				mnemonic= Integer.toString((num % 10));
				label.append(mnemonic);
				label.append(' ');
			}
			label.append(MessageUtil.escapeForMenu(configuration.getName()));
			
			final ConfigContribution item= createConfigContribution(icon, label, mnemonic,
					configuration );
			item.data= data;
			
			items.add(item);
		}
	}
	
	
	protected ConfigContribution createConfigContribution(
			final ImageDescriptor icon, final StringBuilder label, final String mnemonic,
			final ILaunchConfiguration configuration) {
		return new ConfigContribution(icon, label.toString(), mnemonic, configuration);
	}
	
}
