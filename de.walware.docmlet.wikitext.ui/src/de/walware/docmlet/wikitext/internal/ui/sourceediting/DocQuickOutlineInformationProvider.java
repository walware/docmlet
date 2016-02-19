/*=============================================================================#
 # Copyright (c) 2013-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.ui.sourceediting;

import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.swt.widgets.Shell;

import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.QuickInformationProvider;

import de.walware.docmlet.wikitext.core.model.WikitextModel;


public class DocQuickOutlineInformationProvider extends QuickInformationProvider {
	
	
	public DocQuickOutlineInformationProvider(final ISourceEditor editor, final int viewerOperation) {
		super(editor, WikitextModel.WIKIDOC_TYPE_ID, viewerOperation);
	}
	
	
	@Override
	public IInformationControlCreator createInformationPresenterControlCreator() {
		return new IInformationControlCreator() {
			@Override
			public IInformationControl createInformationControl(final Shell parent) {
				return new DocQuickOutlineInformationControl(parent, getCommandId());
			}
		};
	}
	
}
