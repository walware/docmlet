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

package de.walware.docmlet.tex.internal.core.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import de.walware.docmlet.tex.core.TexCore;
import de.walware.docmlet.tex.internal.core.TexProject;


public class TexProjectBuilder extends IncrementalProjectBuilder {
	
	
	public static String BUILDER_ID= "de.walware.docmlet.tex.builders.Tex"; //$NON-NLS-1$
	
	
	private TexProject texProject;
	
	
	public TexProjectBuilder() {
	}
	
	
	public TexProject getTexProject() {
		return this.texProject;
	}
	
	@Override
	protected void startupOnInitialize() {
		super.startupOnInitialize();
		
		this.texProject= TexProject.getTexProject(getProject());
	}
	
	private void check(final SubMonitor progress) throws CoreException {
		if (this.texProject == null) {
			throw new CoreException(new Status(IStatus.ERROR, TexCore.PLUGIN_ID,
					"TeX project nature is missing."));
		}
	}
	
	
	@Override
	protected IProject[] build(final int kind, final Map<String, String> args,
			final IProgressMonitor monitor) throws CoreException {
		final SubMonitor m= SubMonitor.convert(monitor, 5 + 100);
		try {
			check(m.newChild(5, SubMonitor.SUPPRESS_NONE));
			
			final TexProjectBuild texProjectBuild= new TexProjectBuild(this);
			texProjectBuild.build(kind, m.newChild(100, SubMonitor.SUPPRESS_NONE));
			
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
			check(m.newChild(5, SubMonitor.SUPPRESS_NONE));
			
			final TexProjectClean texProjectBuild= new TexProjectClean(this);
			texProjectBuild.clean(m.newChild(100, SubMonitor.SUPPRESS_NONE));
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
