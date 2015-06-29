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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;


public abstract class DocProcessingToolOperationContext {
	
	
	private static final byte S_CANCELED=                   0b0_0000_0001;
	
	private static final byte S_START=                      0b0_0001_0000;
	private static final byte S_PROCESSING=                 0b0_0010_0000;
	private static final byte S_FINISHED=                   0b0_0111_0000;
	
	
	private volatile byte state;
	
	private Runnable runnable;
	
	private Exception error;
	
	
	public DocProcessingToolOperationContext() {
	}
	
	
	public synchronized void start(final DocProcessingToolProcess toolProcess, final Runnable runnable,
			final SubMonitor m) throws Exception {
		if (toolProcess == null) {
			throw new NullPointerException("tool"); //$NON-NLS-1$
		}
		if (runnable == null) {
			throw new NullPointerException("runnable"); //$NON-NLS-1$
		}
		
		this.state= S_START;
		this.runnable= runnable;
		this.error= null;
		try {
			start(toolProcess, m);
			
			RUN: while (this.state < S_FINISHED) {
				try {
					switch (this.state) {
					case (S_START | S_CANCELED):
						canceling(false);
						break RUN;
					case (S_PROCESSING | S_CANCELED):
						canceling(true);
						break;
					default:
						break;
					}
					
					notifyAll();
					
					wait(100);
				}
				catch (final InterruptedException e) {
				}
			}
			
			if (this.error != null) {
				throw this.error;
			}
			if (isCanceled()) {
				throw new CoreException(Status.CANCEL_STATUS);
			}
		}
		finally {
			this.runnable= null;
			this.error= null;
		}
	}
	
	public final synchronized void cancel() {
		this.state|= S_CANCELED;
		
		notifyAll();
	}
	
	public boolean isCanceled() {
		return ((this.state & S_CANCELED) != 0);
	}
	
	
	public abstract String getId();
	
	public abstract String getLabel();
	
	protected abstract void start(DocProcessingToolProcess toolProcess,
			SubMonitor m) throws CoreException;
	
	protected void canceling(final boolean running) {
	}
	
	protected void runInContext() {
		final Runnable runnable;
		synchronized (this) {
			if (this.state != S_START) {
				return;
			}
			this.state= S_PROCESSING;
			runnable= this.runnable;
		}
		try {
			runnable.run();
		}
		catch (final Exception e) {
			this.error= e;
		}
		finally {
			synchronized (this) {
				this.state= S_FINISHED;
				notifyAll();
			}
		}
		
	}
	
}
