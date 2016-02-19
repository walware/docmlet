/*=============================================================================#
 # Copyright (c) 2008-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.base.ui.processing;

import static de.walware.docmlet.base.ui.processing.DocProcessingUI.PREVIEW_OUTPUT_STEP;
import static de.walware.docmlet.base.ui.processing.DocProcessingUI.PRODUCE_OUTPUT_STEP;
import static de.walware.docmlet.base.ui.processing.DocProcessingUI.WEAVE_STEP;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

import de.walware.jcommons.collections.CopyOnWriteIdentityListSet;
import de.walware.jcommons.collections.IdentityCollection;
import de.walware.jcommons.collections.IdentitySet;
import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.ICommonStatusConstants;
import de.walware.ecommons.IDisposable;
import de.walware.ecommons.debug.core.util.OverlayLaunchConfiguration;
import de.walware.ecommons.debug.ui.config.LaunchConfigUtils;
import de.walware.ecommons.ui.SharedUIResources;
import de.walware.ecommons.ui.util.DialogUtil;
import de.walware.ecommons.ui.util.MessageUtil;
import de.walware.ecommons.ui.util.UIAccess;

import de.walware.docmlet.base.internal.ui.DocBaseUIPlugin;
import de.walware.docmlet.base.internal.ui.processing.DocProcessingRegistry;
import de.walware.docmlet.base.internal.ui.processing.Messages;
import de.walware.docmlet.base.ui.DocBaseUI;
import de.walware.docmlet.base.ui.DocBaseUIResources;


/**
 * Manages configuration of a document processing type.
 */
public class DocProcessingManager extends DocProcessingRegistry.TypeElement
		implements ILaunchConfigurationListener, IDisposable {
	
	
	private static final Comparator<ILaunchConfiguration> CONFIG_COMPARATOR= new LaunchConfigUtils.LaunchConfigurationComparator();
	
	private static final String ACTIVE_CONFIG_KEY= "activeConfig"; //$NON-NLS-1$
	
	
	protected static final byte WEAVE_BIT=                  0b0_00000010;
	protected static final byte PRODUCE_OUTPUT_BIT=         0b0_00001000;
	protected static final byte OPEN_OUTPUT_BIT=            0b0_00100000;
	
	
	public static interface IProcessingListener {
		
		public void activeConfigChanged(ILaunchConfiguration config);
		public void availableConfigChanged(ImList<ILaunchConfiguration> configs);
		
	}
	
	
	private String contentTypeId;
	
	private final CopyOnWriteIdentityListSet<IProcessingListener> listenerSet= new CopyOnWriteIdentityListSet<>();
	
	private ILaunchConfigurationType configType;
	private ImList<ILaunchConfiguration> currentConfigs;
	private ILaunchConfiguration activeConfig;
	
	private String configImageKey;
	private String activeConfigImageKey;
	
	
	public DocProcessingManager() {
	}
	
	@Override
	protected void init(final String contentTypeId, final String configTypeId) {
		this.contentTypeId= contentTypeId;
		
		final ILaunchManager launchManager= DebugPlugin.getDefault().getLaunchManager();
		this.configType= launchManager.getLaunchConfigurationType(configTypeId);
		if (this.configType == null) {
			throw new IllegalArgumentException("configTypeId= " + configTypeId); //$NON-NLS-1$
		}
		
		launchManager.addLaunchConfigurationListener(this);
		
		final IDialogSettings settings= getDialogSettings();
		final String s= settings.get(ACTIVE_CONFIG_KEY);
		if (s != null && !s.isEmpty()) {
			for (final ILaunchConfiguration config : getAvailableConfigs()) {
				if (s.equals(config.getName())) {
					setActiveConfig(config);
					break;
				}
			}
		}
		
		initImages();
	}
	
	
	private void initImages() {
		this.configImageKey= this.configType.getIdentifier();
		this.activeConfigImageKey= this.configImageKey + "_ActiveConfig"; //$NON-NLS-1$
		
		final ImageRegistry imageRegistry= DocBaseUIPlugin.getInstance().getImageRegistry();
		
		final Image image= DebugUITools.getImage(this.configType.getIdentifier());
		
		if (imageRegistry.getDescriptor(this.configImageKey) == null) {
			imageRegistry.put(this.configImageKey, image);
		}
		
		if (imageRegistry.getDescriptor(this.activeConfigImageKey) == null) {
			final ImageDescriptor activeConfigImageDescriptor= new DecorationOverlayIcon(image,
					new ImageDescriptor[] {
							null, null, null, SharedUIResources.getImages().getDescriptor(SharedUIResources.OVR_DEFAULT_MARKER_IMAGE_ID),
							null },
					new Point(image.getBounds().width, image.getBounds().height) );
			imageRegistry.put(this.activeConfigImageKey, activeConfigImageDescriptor);
		}
	}
	
	
	public String getContentTypeId() {
		return this.contentTypeId;
	}
	
	protected IDialogSettings getDialogSettings() {
		return DialogUtil.getDialogSettings(DocBaseUIPlugin.getInstance(), this.contentTypeId);
	}
	
	@Override
	public void dispose() {
		final ILaunchManager launchManager= DebugPlugin.getDefault().getLaunchManager();
		if (launchManager != null) {
			launchManager.removeLaunchConfigurationListener(this);
		}
		
		final IDialogSettings settings= getDialogSettings();
		settings.put(ACTIVE_CONFIG_KEY, (this.activeConfig != null) ? this.activeConfig.getName() : null);
	}
	
	
	protected byte getBits(final IdentityCollection<String> flags) {
		byte bits;
		if (flags.contains(DocProcessingUI.PROCESSING_STEPS_FLAG)) {
			bits= WEAVE_BIT | PRODUCE_OUTPUT_BIT;
		}
		else {
			bits= 0;
			if (flags.contains(WEAVE_STEP)) {
				bits|= WEAVE_BIT;
			}
			if (flags.contains(PRODUCE_OUTPUT_STEP)) {
				bits|= PRODUCE_OUTPUT_BIT;
			}
			if (flags.contains(PREVIEW_OUTPUT_STEP)) {
				bits|= OPEN_OUTPUT_BIT;
			}
		}
		return bits;
	}
	
	
	@Override
	public void launchConfigurationAdded(final ILaunchConfiguration configuration) {
		try {
			if (configuration.getType() == this.configType) {
				if (DebugPlugin.getDefault().getLaunchManager().getMovedFrom(configuration) == this.activeConfig) {
					update(true, true, configuration);
				}
				else {
					update(true, false, null);
				}
			}
		} catch (final CoreException e) {
		}
	}
	
	@Override
	public void launchConfigurationChanged(final ILaunchConfiguration configuration) {
		try {
			if (configuration.getType() == this.configType && !configuration.isWorkingCopy()) {
				if (DebugPlugin.getDefault().getLaunchManager().getMovedFrom(configuration) == this.activeConfig) {
					update(true, true, configuration);
				}
				else {
					update(true, false, null);
				}
			}
		} catch (final CoreException e) {
		}
	}
	
	@Override
	public void launchConfigurationRemoved(final ILaunchConfiguration configuration) {
		try {
			// no possible to test for type (exception)
			if (configuration == this.activeConfig) {
				update(true, true, null);
			}
			else {
				final ImList<ILaunchConfiguration> configs= this.currentConfigs;
				if (configs != null) {
					for (final ILaunchConfiguration config : configs) {
						if (config == configuration) {
							update(true, false, null);
							break;
						}
					}
				}
			}
		} catch (final CoreException e) {
		}
	}
	
	private synchronized void update(final boolean updateList, boolean updateActive, final ILaunchConfiguration newActive) throws CoreException {
		if (updateActive && this.activeConfig == newActive) {
			updateActive= false;
		}
		
		final ImList<IProcessingListener> listeners= this.listenerSet.toList();
		if (updateActive) {
			this.activeConfig= newActive;
		}
		if (updateList) {
			if (!listeners.isEmpty()) {
				final ImList<ILaunchConfiguration> configs= updateAvailableConfigs();
				for (final IProcessingListener listener : listeners) {
					try {
						listener.availableConfigChanged(configs);
					}
					catch (final Exception e) {
						DocBaseUIPlugin.log(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, 0,
								"An error occurred while notifiying a listener.", e ));
					}
				}
			}
			else {
				this.currentConfigs= null;
			}
		}
		if (updateActive) {
			for (final IProcessingListener listener : listeners) {
				try {
					listener.activeConfigChanged(newActive);
				}
				catch (final Exception e) {
					DocBaseUIPlugin.log(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, 0,
							"An error occurred while notifiying a listener.", e ));
				}
			}
		}
	}
	
	private ImList<ILaunchConfiguration> updateAvailableConfigs() throws CoreException {
		return this.currentConfigs= ImCollections.newList(
				DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations(this.configType),
				CONFIG_COMPARATOR );
	}
	
	public void addProcessingListener(final IProcessingListener listener) {
		this.listenerSet.add(listener);
	}
	
	public void removeProcessingListener(final IProcessingListener listener) {
		this.listenerSet.remove(listener);
	}
	
	public ILaunchConfigurationType getConfigurationType() {
		return this.configType;
	}
	
	public ImList<ILaunchConfiguration> getAvailableConfigs() {
		ImList<ILaunchConfiguration> configs= this.currentConfigs;
		if (configs == null) {
			try {
				configs= updateAvailableConfigs();
			}
			catch (final CoreException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
						-1, "Loading available configurations failed.", e)); //$NON-NLS-1$
				configs= ImCollections.emptyList();
			}
		}
		return configs;
	}
	
	public void setActiveConfig(final ILaunchConfiguration configuration) {
		try {
			update(false, true, configuration);
		}
		catch (final CoreException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
					-1, "Setting configuration as default failed.", e)); //$NON-NLS-1$
		}
	}
	
	public ILaunchConfiguration getActiveConfig() {
		return this.activeConfig;
	}
	
	
	public void openConfigurationDialog(final Shell shell, IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			selection= new StructuredSelection(this.configType);
		}
		DebugUITools.openLaunchConfigurationDialogOnGroup(shell,
				selection, "org.eclipse.ui.externaltools.launchGroup"); //$NON-NLS-1$
	}
	
	public void launch(final ILaunchConfiguration configuration, final IFile target,
			final IdentitySet<String> flags) {
		final String label= getLabel(configuration, flags, true);
		UIAccess.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				final IWorkbenchPage page= UIAccess.getActiveWorkbenchPage(false);
				page.activate(page.getActiveEditor());
			}
		});
		final IRunnableWithProgress runnable= new IRunnableWithProgress() {
			@Override
			public void run(final IProgressMonitor monitor) throws InvocationTargetException {
				monitor.beginTask(label, 1);
				try {
					final ILaunchConfiguration config= new OverlayLaunchConfiguration(configuration,
							createRunAttributes(target, flags) );
					final String mode= ILaunchManager.RUN_MODE;
					final byte bits= getBits(flags);
					if (bits == 0 || (bits & WEAVE_BIT) != 0) {
						config.launch(mode, new SubProgressMonitor(monitor, 1), true, false);
					}
					else {
						config.launch(mode, new SubProgressMonitor(monitor, 1), false, false);
					}
				}
				catch (final CoreException e) {
					throw new InvocationTargetException(e);
				}
			}
		};
		try {
			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(runnable);
		}
		catch (final InvocationTargetException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID,
					ICommonStatusConstants.LAUNCHING,
					Messages.Processing_Launch_error_message + label, e.getTargetException()),
					StatusManager.LOG | StatusManager.SHOW);
		}
		catch (final InterruptedException e) {
		}
	}
	
	protected Map<String, Object> createRunAttributes(final IFile target,
			final IdentitySet<String> flags) {
		final Map<String, Object> map= new IdentityHashMap<>(4);
		map.put(DocProcessingUI.CONTENT_TYPE_ID_ATTR_NAME, getContentTypeId());
		if (flags != null) {
			map.put(DocProcessingUI.RUN_STEPS_ATTR_NAME, flags);
		}
		if (target != null) {
			map.put(DocProcessingUI.TARGET_PATH_ATTR_NAME, target.getFullPath().toPortableString());
		}
		return map;
	}
	
	
	public ImageDescriptor getImageDescriptor(final ILaunchConfiguration configuration) {
		return DocBaseUIPlugin.getInstance().getImageRegistry().getDescriptor(
				(this.activeConfig == configuration) ?
						this.activeConfigImageKey :
						this.configImageKey );
	}
	
	public Image getImage(final ILaunchConfiguration configuration) {
		return DocBaseUIPlugin.getInstance().getImageRegistry().get(
				(this.activeConfig == configuration) ?
						this.activeConfigImageKey :
						this.configImageKey );
	}
	
	public String getLabel(final ILaunchConfiguration configuration,
			final IdentityCollection<String> flags, final boolean noMnemonics) {
		String label= getActionLabel(flags);
		if (configuration != null) {
			label += " '" + configuration.getName() + "'"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return (noMnemonics) ? MessageUtil.removeMnemonics(label) : label;
	}
	
	public Image getActionImage(final IdentityCollection<String> flags) {
		return getActionImage(getBits(flags));
	}
	
	protected Image getActionImage(final byte bits) {
		switch (bits) {
		case 0:
		case WEAVE_BIT | PRODUCE_OUTPUT_BIT | OPEN_OUTPUT_BIT:
			return DocBaseUIResources.INSTANCE.getImage(DocBaseUIResources.TOOL_PROCESSANDPREVIEW_IMAGE_ID);
		case WEAVE_BIT | PRODUCE_OUTPUT_BIT:
			return DocBaseUIResources.INSTANCE.getImage(DocBaseUIResources.TOOL_PROCESS_IMAGE_ID);
		case PRODUCE_OUTPUT_BIT:
			return DocBaseUIResources.INSTANCE.getImage(DocBaseUIResources.TOOL_PREVIEW_IMAGE_ID);
		default:
			return null;
		}
	}
	
	public String getActionLabel(final IdentityCollection<String> flags) {
		return getActionLabel(getBits(flags));
	}
	
	protected String getActionLabel(final byte bits) {
		switch (bits) {
		case 0:
		case WEAVE_BIT | PRODUCE_OUTPUT_BIT | OPEN_OUTPUT_BIT:
			return Messages.ProcessingAction_ProcessAndPreview_label;
		case WEAVE_BIT | PRODUCE_OUTPUT_BIT:
			return Messages.ProcessingAction_ProcessDoc_label;
		case WEAVE_BIT:
			return Messages.ProcessingAction_Weave_label;
		case PRODUCE_OUTPUT_BIT:
			return Messages.ProcessingAction_ProduceOutput_label;
		case OPEN_OUTPUT_BIT:
			return Messages.ProcessingAction_PreviewOutput_label;
		default:
			throw new IllegalArgumentException();
		}
	}
	
}
