/*******************************************************************************
 * Copyright (c) 2009-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

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

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.core.TexCore;
import de.walware.docmlet.tex.core.model.ILtxSourceUnit;
import de.walware.docmlet.tex.core.model.TexModel;


public final class LtxEditorUriSourceUnit extends GenericUriSourceUnit implements ILtxSourceUnit {
	
	
	public LtxEditorUriSourceUnit(final String id, final IFileStore store) {
		super(id, store);
	}
	
	
	@Override
	public String getModelTypeId() {
		return TexModel.LTX_TYPE_ID;
	}
	
	@Override
	public String getContentTypeId() {
		return TexCore.LTX_CONTENT_ID;
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
	protected IWorkingBuffer createWorkingBuffer(final SubMonitor progress) {
		return new FileBufferWorkingBuffer(this);
	}
	
	
	@Override
	public ITexCoreAccess getTexCoreAccess() {
		return TexCore.getWorkbenchAccess();
	}
	
}
