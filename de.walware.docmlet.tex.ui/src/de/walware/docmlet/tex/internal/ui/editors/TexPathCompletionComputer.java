/*=============================================================================#
 # Copyright (c) 2007-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.ui.editors;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;

import de.walware.ecommons.ltk.core.model.ISourceUnit;
import de.walware.ecommons.ltk.core.model.IWorkspaceSourceUnit;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssist;
import de.walware.ecommons.ltk.ui.sourceediting.assist.IContentAssistComputer;
import de.walware.ecommons.ltk.ui.sourceediting.assist.PathCompletionComputor;

import de.walware.docmlet.tex.core.ast.TexAst;
import de.walware.docmlet.tex.core.ast.TexAstNode;
import de.walware.docmlet.tex.core.commands.Argument;
import de.walware.docmlet.tex.core.commands.TexCommand;
import de.walware.docmlet.tex.internal.ui.sourceediting.LtxAssistInvocationContext;
import de.walware.docmlet.tex.ui.TexUI;


/**
 * Completion computer for path in R code.
 * 
 * Supports workspace properties of a R tool process
 */
public class TexPathCompletionComputer extends PathCompletionComputor {
	
	
	private IContainer baseResource;
	private IFileStore baseFileStore;
	
	
	public TexPathCompletionComputer() {
	}
	
	
	@Override
	public String getPluginId() {
		return TexUI.PLUGIN_ID;
	}
	
	@Override
	public void sessionStarted(final ISourceEditor editor, final ContentAssist assist) {
		this.baseResource= null;
		this.baseFileStore= null;
		{
			final ISourceUnit su= editor.getSourceUnit();
			if (su instanceof IWorkspaceSourceUnit) {
				final IResource resource= ((IWorkspaceSourceUnit) su).getResource();
				if (this.baseResource == null) {
					this.baseResource= resource.getParent();
				}
				if (this.baseResource != null) {
					try {
						this.baseFileStore= EFS.getStore(this.baseResource.getLocationURI());
					}
					catch (final CoreException e) {
					}
				}
			}
		}
		
		super.sessionStarted(editor, assist);
	}
	
	@Override
	public void sessionEnded() {
		super.sessionEnded();
	}
	
	@Override
	protected char getDefaultFileSeparator() {
		return '/';
	}
	
	@Override
	protected IRegion getContentRange(final AssistInvocationContext context, final int mode)
			throws BadLocationException {
		if (context instanceof LtxAssistInvocationContext) {
			final LtxAssistInvocationContext texContext= (LtxAssistInvocationContext) context;
			final int argIdx= texContext.getInvocationArgIdx();
			if (argIdx >= 0) {
				final TexCommand command= texContext.getInvocationControlNode().getCommand();
				final Argument argDef= command.getArguments().get(argIdx);
				final TexAstNode argNode= texContext.getInvocationArgNodes()[argIdx];
				final int offset= texContext.getInvocationOffset();
				if (mode == IContentAssistComputer.SPECIFIC_MODE
								|| (argDef.getContent() & 0xf0) == Argument.RESOURCE ) {
					final IRegion region= TexAst.getInnerRegion(argNode);
					if (region != null
							&& region.getOffset() >= offset && offset <= region.getOffset() + region.getLength() ) {
						return region;
					}
				}
			}
		}
		return null;
	}
	
	@Override
	protected IPath getRelativeBasePath() {
		if (this.baseResource != null) {
			return this.baseResource.getLocation();
		}
		return null;
	}
	
	@Override
	protected IFileStore getRelativeBaseStore() {
		if (this.baseFileStore != null) {
			return this.baseFileStore;
		}
		return null;
	}
	
}
