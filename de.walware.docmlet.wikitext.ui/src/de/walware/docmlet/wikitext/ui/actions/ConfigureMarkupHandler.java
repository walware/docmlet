/*=============================================================================#
 # Copyright (c) 2014-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.statushandlers.StatusManager;

import de.walware.ecommons.workbench.ui.WorkbenchUIUtil;

import de.walware.docmlet.wikitext.core.WikitextCore;
import de.walware.docmlet.wikitext.core.markup.IMarkupConfig;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguage;
import de.walware.docmlet.wikitext.core.markup.IMarkupLanguageManager1;
import de.walware.docmlet.wikitext.core.model.IWikidocWorkspaceSourceUnit;
import de.walware.docmlet.wikitext.core.model.IWikitextSourceUnit;
import de.walware.docmlet.wikitext.ui.WikitextUI;
import de.walware.docmlet.wikitext.ui.config.IMarkupConfigUIAdapter;
import de.walware.docmlet.wikitext.ui.editors.IWikidocEditor;


public class ConfigureMarkupHandler extends AbstractHandler {
	
	
	private static class ApplyRunnable implements IRunnableWithProgress {
		
		
		private final IWikidocWorkspaceSourceUnit sourceUnit;
		
		private final IMarkupConfig markupConfig;
		
		
		public ApplyRunnable(final IWikidocWorkspaceSourceUnit su,
				final IMarkupConfig markupConfig) {
			this.sourceUnit= su;
			this.markupConfig= markupConfig;
		}
		
		@Override
		public void run(final IProgressMonitor monitor) throws InvocationTargetException,
				InterruptedException {
			final SubMonitor m= SubMonitor.convert(monitor, "Applying markup configuration...", 10);
			try {
				
				final IFile file= (IFile) this.sourceUnit.getResource();
				
				WikitextCore.getMarkupLanguageManager().setConfig(file, this.markupConfig);
				m.worked(10);
			}
			finally {
				m.done();
			}
		}
		
	}
	
	
	
	public ConfigureMarkupHandler() {
	}
	
	
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IWorkbenchPart activePart= WorkbenchUIUtil.getActivePart(event.getApplicationContext());
		
		IWikitextSourceUnit su= null;
		if (activePart instanceof IWikidocEditor) {
			final IWikidocEditor editor= (IWikidocEditor) activePart;
			
			su= editor.getSourceUnit();
		}
		
		if (!(su instanceof IWikidocWorkspaceSourceUnit)) {
			MessageDialog.openInformation(getShell(activePart), "Configure Markup",
					"The operation only supported for Wikitext documents in the workspace." );
			return null;
		}
		
		final IFile file= (IFile) su.getResource();
		
		final IMarkupLanguageManager1 markupLanguageManager= WikitextCore.getMarkupLanguageManager();
		final IMarkupLanguage activeLanguage= markupLanguageManager.getLanguage(file, null, true);
		
		if (activeLanguage != null) {
			final IMarkupConfig activeConfig= activeLanguage.getMarkupConfig();
			final IMarkupConfig fileConfig= markupLanguageManager.getConfig(file, null);
			
			final IMarkupConfigUIAdapter ui= (activeConfig != null) ?
					(IMarkupConfigUIAdapter) Platform.getAdapterManager().loadAdapter(
							activeConfig, IMarkupConfigUIAdapter.class.getName()) :
					null;
			if (ui == null) {
				MessageDialog.openInformation(getShell(activePart), "Configure Markup",
						NLS.bind("The operation is not supported for {0}.",
								activeLanguage.getName() ));
				return null;
			}
			final IMarkupConfig config= activeConfig.clone();
			final AtomicBoolean enabled= new AtomicBoolean(fileConfig != null);
			
			if (ui.edit("document", enabled, config, getShell(activePart))) {
				try {
					activePart.getSite().getWorkbenchWindow().run(true, true,
							new ApplyRunnable((IWikidocWorkspaceSourceUnit) su,
							(enabled.get()) ? config : null ));
				}
				catch (final InterruptedException e) {}
				catch (final Exception e) {
					StatusManager.getManager().handle(new Status(IStatus.ERROR, WikitextUI.PLUGIN_ID,
							"An error occurred when setting markup configuration.",
							e ));
				}
			}
		}
		
		return null;
	}
	
	protected Shell getShell(final IWorkbenchPart part) {
		if (part != null) {
			final IWorkbenchPartSite site= part.getSite();
			if (site != null) {
				return site.getShell();
			}
		}
		return null;
	}
	
	
}
