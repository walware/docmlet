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

package de.walware.docmlet.wikitext.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import de.walware.ecommons.ltk.AstInfo;
import de.walware.ecommons.ltk.core.ElementSet;
import de.walware.ecommons.ltk.core.model.IModelElement;
import de.walware.ecommons.ltk.core.model.ISourceStructElement;
import de.walware.ecommons.ltk.core.model.ISourceUnit;
import de.walware.ecommons.ltk.core.util.ElementComparator;
import de.walware.ecommons.ltk.ui.sourceediting.actions.AbstractSourceDocumentHandler;
import de.walware.ecommons.text.IndentUtil;

import de.walware.docmlet.wikitext.core.model.IWikitextSourceUnit;
import de.walware.docmlet.wikitext.core.source.HardLineWrap;
import de.walware.docmlet.wikitext.internal.ui.editors.Messages;


public class WikidocCorrectLineWrapHandler extends AbstractSourceDocumentHandler<IWikitextSourceUnit> {
	
	
	public WikidocCorrectLineWrapHandler() {
	}
	
	
	@Override
	protected String getTaskLabel() {
		return Messages.CorrectLineWrap_task;
	}
	
	@Override
	protected boolean isEditTask() {
		return true;
	}
	
	@Override
	protected boolean isSourceUnitSupported(final ISourceUnit sourceUnit) {
		return (sourceUnit instanceof IWikitextSourceUnit);
	}
	
	@Override
	protected void doExecute(final ExecData data,
			final IProgressMonitor monitor) throws Exception {
		final List<IWikitextSourceUnit> connectedSourceUnits= new ArrayList<>();
		try {
			final byte mode= HardLineWrap.SELECTION_MERGE;
			if (data.getTextSelection() != null) {
				final ITextSelection selection= data.getTextSelection();
				final IWikitextSourceUnit sourceUnit= data.getSourceUnits().get(0);
				sourceUnit.connect(monitor);
				connectedSourceUnits.add(sourceUnit);
				
				final AstInfo ast= sourceUnit.getAstInfo(null, true, monitor);
				if (ast == null) {
					return;
				}
				
				final AbstractDocument doc= sourceUnit.getDocument(monitor);
				final IRegion lineInfo= doc.getLineInformationOfOffset(selection.getOffset());
				final IRegion region= new Region(lineInfo.getOffset(),
						(selection.getLength() == 0) ?
								lineInfo.getLength() :
									selection.getOffset() + selection.getLength() - lineInfo.getOffset() );
				
				final HardLineWrap hardLineWrap= new HardLineWrap(sourceUnit.getDocumentContentInfo(),
						sourceUnit.getWikitextCoreAccess() );
				final TextEdit textEdit= hardLineWrap.createTextEdit(doc, ast, region, mode, null);
				
				apply(doc, textEdit);
			}
			else if (data.getSourceEditor() != null) {
				final ElementSet sourceElements= data.getElementSelection();
				sourceElements.removeElementsWithAncestorsOnList();
				Collections.sort(sourceElements.getModelElements(), new ElementComparator());
				final IWikitextSourceUnit sourceUnit= data.getSourceUnits().get(0);
				sourceUnit.connect(monitor);
				connectedSourceUnits.add(sourceUnit);
				
				final AstInfo ast= sourceUnit.getAstInfo(null, true, monitor);
				if (ast == null) {
					return;
				}
				
				final AbstractDocument doc= sourceUnit.getDocument(monitor);
				final HardLineWrap hardLineWrap= new HardLineWrap(sourceUnit.getDocumentContentInfo(),
						sourceUnit.getWikitextCoreAccess() );
				final IndentUtil indentUtil= new IndentUtil(doc,
						hardLineWrap.getWikitextCoreAccess().getWikitextCodeStyle() );
				
				final List<IModelElement> modelElements= sourceElements.getModelElements();
				final MultiTextEdit textEdit= new MultiTextEdit();
				for (int i= 0; i < modelElements.size(); i++) {
					final ISourceStructElement element= (ISourceStructElement) modelElements.get(i);
					hardLineWrap.addTextEdits(doc, ast, element.getSourceRange(), mode,
							textEdit, indentUtil );
				}
				
				apply(doc, textEdit);
			}
			
		}
		finally {
			for (final IWikitextSourceUnit sourceUnit : connectedSourceUnits) {
				sourceUnit.disconnect(monitor);
			}
		}
	}
	
	private void apply(final AbstractDocument doc, final TextEdit textEdit)
			throws MalformedTreeException, BadLocationException {
		if (textEdit != null && textEdit.getChildrenSize() > 0) {
			final DocumentRewriteSession rewriteSession= doc.startRewriteSession(
					DocumentRewriteSessionType.STRICTLY_SEQUENTIAL );
			try {
				textEdit.apply(doc, TextEdit.NONE);
			}
			finally {
				doc.stopRewriteSession(rewriteSession);
			}
		}
	}
	
}
