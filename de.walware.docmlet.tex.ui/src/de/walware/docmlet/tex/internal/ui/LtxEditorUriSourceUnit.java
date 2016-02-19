/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.SubMonitor;

import de.walware.ecommons.ltk.LTK;
import de.walware.ecommons.ltk.SourceDocumentRunnable;
import de.walware.ecommons.ltk.WorkingContext;
import de.walware.ecommons.ltk.core.impl.GenericUriSourceUnit;
import de.walware.ecommons.ltk.core.impl.IWorkingBuffer;
import de.walware.ecommons.ltk.ui.FileBufferWorkingBuffer;
import de.walware.ecommons.text.core.sections.IDocContentSections;

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.core.TexCore;
import de.walware.docmlet.tex.core.model.ITexSourceUnit;
import de.walware.docmlet.tex.core.model.TexModel;
import de.walware.docmlet.tex.core.source.LtxDocumentContentInfo;


public final class LtxEditorUriSourceUnit extends GenericUriSourceUnit implements ITexSourceUnit {
	
	
	public LtxEditorUriSourceUnit(final String id, final IFileStore store) {
		super(id, store);
	}
	
	
	@Override
	public String getModelTypeId() {
		return TexModel.LTX_TYPE_ID;
	}
	
	@Override
	public IDocContentSections getDocumentContentInfo() {
		return LtxDocumentContentInfo.INSTANCE;
	}
	
	@Override
	public WorkingContext getWorkingContext() {
		return LTK.EDITOR_CONTEXT;
	}
	
	
	@Override
	public void syncExec(final SourceDocumentRunnable runnable) throws InvocationTargetException {
		FileBufferWorkingBuffer.syncExec(runnable);
	}
	
	
	@Override
	protected IWorkingBuffer createWorkingBuffer(final SubMonitor m) {
		return new FileBufferWorkingBuffer(this);
	}
	
	
	@Override
	public ITexCoreAccess getTexCoreAccess() {
		return TexCore.getWorkbenchAccess();
	}
	
}
