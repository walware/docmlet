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

package de.walware.docmlet.wikitext.internal.core.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import de.walware.docmlet.wikitext.core.WikitextCore;
import de.walware.docmlet.wikitext.internal.core.WikitextProject;


public class WikitextProjectBuilder extends IncrementalProjectBuilder {
	
	
	public static String BUILDER_ID= "de.walware.docmlet.wikitext.builders.Wikitext"; //$NON-NLS-1$
	
	
	private WikitextProject wikitextProject;
	
	
	public WikitextProjectBuilder() {
	}
	
	
	public WikitextProject getWikitextProject() {
		return this.wikitextProject;
	}
	
	@Override
	protected void startupOnInitialize() {
		super.startupOnInitialize();
		
		this.wikitextProject= WikitextProject.getWikitextProject(getProject());
	}
	
	private void check(final SubMonitor m) throws CoreException {
		if (this.wikitextProject == null) {
			throw new CoreException(new Status(IStatus.ERROR, WikitextCore.PLUGIN_ID,
					"Wikitext project nature is missing."));
		}
	}
	
	
	@Override
	protected IProject[] build(final int kind, final Map<String, String> args,
			final IProgressMonitor monitor) throws CoreException {
		final SubMonitor m= SubMonitor.convert(monitor, 5 + 100);
		try {
			check(m.newChild(5, SubMonitor.SUPPRESS_NONE));
			
			final WikitextProjectBuild wikitextProjectBuild= new WikitextProjectBuild(this);
			wikitextProjectBuild.build(kind, m.newChild(100, SubMonitor.SUPPRESS_NONE));
			
			return null;
		}
		catch (final CoreException e) {
			if (e.getStatus().getSeverity() == IStatus.CANCEL) {
				throw new OperationCanceledException();
			}
			throw e;
		}
		finally {
			m.done();
		}
	}
	
	@Override
	protected void clean(final IProgressMonitor monitor) throws CoreException {
		final SubMonitor m= SubMonitor.convert(monitor, 5 + 100);
		try {
			check(m.newChild(5));
			
			final WikitextProjectClean wikitextProjectBuild= new WikitextProjectClean(this);
			wikitextProjectBuild.clean(m.newChild(100));
		}
		catch (final CoreException e) {
			if (e.getStatus().getSeverity() == IStatus.CANCEL) {
				throw new OperationCanceledException();
			}
			throw e;
		}
		finally {
			m.done();
		}
	}
	
}
