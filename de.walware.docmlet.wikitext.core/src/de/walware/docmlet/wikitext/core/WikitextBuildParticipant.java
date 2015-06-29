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

package de.walware.docmlet.wikitext.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import de.walware.docmlet.wikitext.core.model.IWikidocWorkspaceSourceUnit;
import de.walware.docmlet.wikitext.internal.core.builder.WikitextBuildParticipantInternal;


public class WikitextBuildParticipant extends WikitextBuildParticipantInternal {
	
	
	public WikitextBuildParticipant() {
	}
	
	
	public final IWikitextProject getWikitextProject() {
		return this.wikitextProject;
	}
	
	public final int getBuildType() {
		return this.buildType;
	}
	
	@Override
	public void init() {
	}
	
	protected final void setEnabled(final boolean enabled) {
		this.enabled= enabled;
	}
	
	public final boolean isEnabled() {
		return this.enabled;
	}
	
	/**
	 * @param file the file to clear
	 * @throws CoreException
	 */
	public void clear(final IFile file) throws CoreException {
	}
	
	/**
	 * @param unit the added/changed source unit
	 * @param monitor SubMonitor-recommended
	 */
	public void docUnitUpdated(final IWikidocWorkspaceSourceUnit unit,
			final IProgressMonitor monitor) throws CoreException {
		// update index
	}
	
	/**
	 * @param file the removed resource
	 * @param monitor SubMonitor-recommended
	 */
	public void docUnitRemoved(final IFile file,
			final IProgressMonitor monitor) throws CoreException {
		// remove from index
	}
	
	public void docFinished(final IProgressMonitor monitor) throws CoreException {
	}
	
}
