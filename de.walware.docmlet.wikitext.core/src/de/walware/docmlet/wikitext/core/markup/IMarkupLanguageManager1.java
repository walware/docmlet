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

package de.walware.docmlet.wikitext.core.markup;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;


public interface IMarkupLanguageManager1 extends IMarkupLanguageManager {
	
	
	interface IMarkupConfigChangedListener {
		
		void configChanged(Map<String, List<IProject>> languages, IProgressMonitor monitor) throws CoreException;
		
		void configChanged(IFile file, IProgressMonitor monitor) throws CoreException;
		
	}
	
	
	void addConfigChangedListener(IMarkupConfigChangedListener listener);
	void removeConfigChangedListern(IMarkupConfigChangedListener listener);
	
//	void setConfig(String languageName, IMarkupConfig config);
	IMarkupConfig getConfig(String languageName);
	
//	void setConfig(final IProject project, String languageName, IMarkupConfig config);
//	IMarkupConfig getConfig(final IProject project, String languageName);
	
	void setConfig(IFile file, IMarkupConfig config);
	IMarkupConfig getConfig(IFile file, String languageName);
	
	IMarkupLanguage getLanguage(IFile file, String languageName, boolean inherit);
	
}
