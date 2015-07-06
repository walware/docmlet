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

package de.walware.docmlet.base.internal.ui.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.walware.ecommons.collections.ImList;
import de.walware.ecommons.debug.core.util.LaunchConfigurationCollector;
import de.walware.ecommons.io.win.DDE;
import de.walware.ecommons.io.win.DDEClient;

import de.walware.docmlet.base.internal.ui.viewer.DocViewerLaunchConfig.DDETask;
import de.walware.docmlet.base.ui.viewer.DocViewerConfig;


public class DocViewerCloseDelegate extends LaunchConfigurationCollector {
	
	
	public static boolean isAvailable() {
		return DDE.isSupported();
	}
	
	
	private class RestoreRunnable implements Runnable {
		
		private static final int CHECK= 0;
		private static final int RESTORE= 1;
		private static final int DISPOSED= 2;
		
		private final Display display;
		
		private volatile int run= 0;
		
		private long lastTime= 0;
		
		private Shell activeShell;
		
		public RestoreRunnable(final Display display) {
			this.display= display;
		}
		
		@Override
		public void run() {
			switch (this.run) {
			case CHECK:
				this.run= RESTORE;
				this.activeShell= this.display.getActiveShell();
				return;
			case RESTORE: {
				final long diff= (System.nanoTime() - this.lastTime) / 1000000; // milli
				if (diff < 500 && this.display.getActiveShell() == null) {
					Shell shell= null;
					if (!this.activeShell.isDisposed()) {
						shell= this.activeShell;
					}
					else {
						final IWorkbenchWindow activeWindow= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
						if (activeWindow == null) {
							dispose(true);
							return;
						}
						shell= activeWindow.getShell();
					}
					if (shell != null && !shell.isDisposed()) {
						shell.forceActive();
					}
				}
				else if (diff < 200) {
					this.display.timerExec(50, this);
					return;
				}
				dispose(true);
				return;
			}
			default:
				return;
			}
		}
		
		public void runCheck() {
			if (this.display != null) {
				this.display.syncExec(this);
			}
		}
		
		public void setLastOK(final long time) {
			this.lastTime= time;
		}
		
		public void dispose(final boolean remove) {
			this.run= DISPOSED;
			if (remove) {
				synchronized (DocViewerCloseDelegate.this.restoreRunnables) {
					DocViewerCloseDelegate.this.restoreRunnables.remove(this);
				}
			}
		}
		
		public void scheduleRestore() {
			if (this.display != null && this.run > 0
					&& this.lastTime != 0 && this.activeShell != null) {
				synchronized (DocViewerCloseDelegate.this.restoreRunnables) {
					DocViewerCloseDelegate.this.restoreRunnables.add(this);
				}
				this.display.asyncExec(this);
			}
		}
		
	}
	
	
	private final List<RestoreRunnable> restoreRunnables= new ArrayList<>();
	
	
	public DocViewerCloseDelegate() {
		super(DocViewerConfig.TYPE_ID);
	}
	
	
	@Override
	protected boolean include(final ILaunchConfiguration configuration) throws CoreException {
		return (super.include(configuration)
				&& !configuration.getAttribute(DocViewerConfig.TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER + '/' +
						DocViewerConfig.DDE_COMMAND_ATTR_KEY, "" ).isEmpty() ); //$NON-NLS-1$
	}
	
	
	public void run(final DocViewerLaunchConfig config, final SubMonitor m) throws CoreException {
		final ImList<ILaunchConfiguration> configurations= getConfigurations();
		if (configurations.isEmpty()) {
			return;
		}
		
		int workRemaining= configurations.size() * 2;
		m.beginTask(Messages.PreProduceOutput_task, workRemaining);
		
		final RestoreRunnable runnable= new RestoreRunnable(Display.getDefault());
		runnable.runCheck();
		
		for (final ILaunchConfiguration configuration : configurations) {
			m.setWorkRemaining(workRemaining);
			workRemaining-= 2;
			if (m.isCanceled()) {
				throw new CoreException(Status.CANCEL_STATUS);
			}
			try {
				final DDETask ddeTask= config.loadDDETask(configuration,
						DocViewerConfig.TASK_PRE_PRODUCE_OUTPUT_ATTR_QUALIFIER,
						Messages.DDE_PreProduceOutput_label, m.newChild(1) );
				if (ddeTask != null) {
					m.newChild(1);
					try {
						ddeTask.exec();
						runnable.setLastOK(System.nanoTime());
					}
					catch (final CoreException e) {
						switch (e.getStatus().getCode()) {
						case DDEClient.INIT_FAILED:
							return;
						case DDEClient.CONNECT_FAILED:
							continue;
						default:
							throw e;
						}
					}
				}
			}
			catch (final CoreException e) {
			}
		}
		
		runnable.scheduleRestore();
	}
	
	
	public void cancelFocus() {
		synchronized (this.restoreRunnables) {
			for (final RestoreRunnable runnable : this.restoreRunnables) {
				runnable.dispose(false);
			}
			this.restoreRunnables.clear();
		}
	}
	
}
