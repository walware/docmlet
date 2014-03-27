/*=============================================================================#
 # Copyright (c) 2013-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.ui.sourceediting;

import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.swt.widgets.Shell;

import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.QuickInformationProvider;

import de.walware.docmlet.tex.core.model.TexModel;
import de.walware.docmlet.tex.core.source.LtxHeuristicTokenScanner;


public class LtxQuickOutlineInformationProvider extends QuickInformationProvider {
	
	
	private LtxHeuristicTokenScanner scanner;
	
	
	public LtxQuickOutlineInformationProvider(final ISourceEditor editor, final int viewerOperation) {
		super(editor, TexModel.LTX_TYPE_ID, viewerOperation);
	}
	
	
//	@Override
//	public IRegion getSubject(final ITextViewer textViewer, final int offset) {
//		if (this.scanner == null) {
//			this.scanner= (LtxHeuristicTokenScanner) LTK.getModelAdapter(
//					getEditor().getModelTypeId(), LtxHeuristicTokenScanner.class );
//		}
//		try {
//			final IDocument document= getEditor().getViewer().getDocument();
//			this.scanner.configure(document);
//			final IRegion word= this.scanner.findCommonWord(offset);
//			if (word != null) {
//				return word;
//			}
//		}
//		catch (final Exception e) {
//		}
//		return super.getSubject(textViewer, offset);
//	}
	
	@Override
	public IInformationControlCreator createInformationPresenterControlCreator() {
		return new IInformationControlCreator() {
			@Override
			public IInformationControl createInformationControl(final Shell parent) {
				return new LtxQuickOutlineInformationControl(parent, getCommandId());
			}
		};
	}
	
}
