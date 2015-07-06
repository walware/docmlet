/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.base.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.walware.ecommons.ICommonStatusConstants;
import de.walware.ecommons.IDisposable;
import de.walware.ecommons.ui.util.ImageRegistryUtil;

import de.walware.docmlet.base.internal.ui.markuphelp.MarkupHelpManager;
import de.walware.docmlet.base.internal.ui.processing.DocProcessingRegistry;
import de.walware.docmlet.base.internal.ui.viewer.DocViewerCloseDelegate;
import de.walware.docmlet.base.ui.DocBaseUI;
import de.walware.docmlet.base.ui.DocBaseUIResources;


public class DocBaseUIPlugin extends AbstractUIPlugin {
	
	
	private static DocBaseUIPlugin instance;
	
	
	public static DocBaseUIPlugin getInstance() {
		return DocBaseUIPlugin.instance;
	}
	
	public static void log(final IStatus status) {
		final Plugin plugin= getInstance();
		if (plugin != null) {
			plugin.getLog().log(status);
		}
	}
	
	
	private boolean started;
	
	private List<IDisposable> disposables;
	
	private MarkupHelpManager markupHelpManager;
	
	private DocViewerCloseDelegate docViewerCloseDelegate;
	private DocProcessingRegistry docProcessingRegistry;
	
	
	public DocBaseUIPlugin() {
	}
	
	
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		instance= this;
		
		this.disposables= new ArrayList<>();
		
		this.started= true;
	}
	
	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			synchronized (this) {
				this.started= false;
				
				this.docProcessingRegistry= null;
			}
			
			for (final IDisposable listener : this.disposables) {
				try {
					listener.dispose();
				}
				catch (final Throwable e) {
					getLog().log(new Status(IStatus.ERROR, DocBaseUI.PLUGIN_ID, ICommonStatusConstants.INTERNAL_PLUGGED_IN,
							"Error occured while dispose module", e )); 
				}
			}
			this.disposables= null;
		}
		finally {
			instance= null;
			super.stop(context);
		}
	}
	
	
	@Override
	protected void initializeImageRegistry(final ImageRegistry reg) {
		if (!this.started) {
			throw new IllegalStateException("Plug-in is not started.");
		}
		final ImageRegistryUtil util= new ImageRegistryUtil(this);
		
		util.register(DocBaseUIResources.OBJ_PREAMBLE_IMAGE_ID, ImageRegistryUtil.T_OBJ, "preamble.png"); //$NON-NLS-1$
		util.register(DocBaseUIResources.OBJ_HEADING1_IMAGE_ID, ImageRegistryUtil.T_OBJ, "sectioning-1.png"); //$NON-NLS-1$
		util.register(DocBaseUIResources.OBJ_HEADING2_IMAGE_ID, ImageRegistryUtil.T_OBJ, "sectioning-2.png"); //$NON-NLS-1$
		util.register(DocBaseUIResources.OBJ_HEADING3_IMAGE_ID, ImageRegistryUtil.T_OBJ, "sectioning-3.png"); //$NON-NLS-1$
		util.register(DocBaseUIResources.OBJ_HEADING4_IMAGE_ID, ImageRegistryUtil.T_OBJ, "sectioning-4.png"); //$NON-NLS-1$
		util.register(DocBaseUIResources.OBJ_HEADING5_IMAGE_ID, ImageRegistryUtil.T_OBJ, "sectioning-5.png"); //$NON-NLS-1$
		util.register(DocBaseUIResources.OBJ_HEADING6_IMAGE_ID, ImageRegistryUtil.T_OBJ, "sectioning-6.png"); //$NON-NLS-1$
		
		util.register(DocBaseUIResources.VIEW_MARKUP_HELP_IMAGE_ID, ImageRegistryUtil.T_VIEW, "markup_help.png"); //$NON-NLS-1$
		
		util.register(DocBaseUIResources.TOOL_PROCESS_IMAGE_ID, ImageRegistryUtil.T_TOOL, "process.png"); //$NON-NLS-1$
		util.register(DocBaseUIResources.TOOL_PROCESSANDPREVIEW_IMAGE_ID, ImageRegistryUtil.T_TOOL, "process_and_preview.png"); //$NON-NLS-1$
		util.register(DocBaseUIResources.TOOL_PREVIEW_IMAGE_ID, ImageRegistryUtil.T_TOOL, "preview.png"); //$NON-NLS-1$
	}
	
	
	public synchronized MarkupHelpManager getMarkupHelpManager() {
		if (this.markupHelpManager == null) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.markupHelpManager= new MarkupHelpManager();
		}
		return this.markupHelpManager;
	}
	
	public synchronized DocViewerCloseDelegate getDocViewerCloseDelegate() {
		if (this.docViewerCloseDelegate == null) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.docViewerCloseDelegate= new DocViewerCloseDelegate();
			this.disposables.add(this.docViewerCloseDelegate);
		}
		return this.docViewerCloseDelegate;
	}
	
	public synchronized DocProcessingRegistry getDocProcessingRegistry() {
		if (this.docProcessingRegistry == null) {
			if (!this.started) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			this.docProcessingRegistry= new DocProcessingRegistry();
			this.disposables.add(this.docProcessingRegistry);
		}
		return this.docProcessingRegistry;
	}
	
}
