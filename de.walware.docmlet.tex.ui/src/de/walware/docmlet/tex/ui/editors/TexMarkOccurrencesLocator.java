/*******************************************************************************
 * Copyright (c) 2011-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.ui.editors;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Point;

import de.walware.ecommons.ltk.ISourceUnitModelInfo;
import de.walware.ecommons.ltk.ast.AstSelection;
import de.walware.ecommons.ltk.ui.sourceediting.AbstractMarkOccurrencesProvider.RunData;
import de.walware.ecommons.text.ui.presentation.ITextPresentationConstants;

import de.walware.docmlet.tex.core.ast.TexAst;
import de.walware.docmlet.tex.core.ast.TexAstNode;
import de.walware.docmlet.tex.core.model.TexLabelAccess;


public class TexMarkOccurrencesLocator {
	
	
	public void run(final RunData run, final ISourceUnitModelInfo info,
			final AstSelection astSelection, final ITextSelection orgSelection)
			throws BadLocationException, BadPartitioningException, UnsupportedOperationException {
		final TexAstNode node = (TexAstNode) astSelection.getCovering();
		if (checkForAccess(run, node)) {
			return;
		}
	}
	
	private boolean checkForAccess(final RunData run, TexAstNode node) throws BadLocationException {
		if (node == null || !(node.getNodeType() == TexAst.NodeType.LABEL)) {
			return false;
		}
		do {
			final Object[] attachments = node.getAttachments();
			for (int i = 0; i < attachments.length; i++) {
				if (attachments[i] instanceof TexLabelAccess) {
					final TexLabelAccess access = (TexLabelAccess) attachments[i];
					final Map<Annotation, Position> annotations = checkDefault(run, access);
					
					if (annotations != null) {
						run.set(annotations);
						return true;
					}
				}
			}
			node = node.getParent();
		} while (node != null);
		
		return false;
	}
	
	private Map<Annotation, Position> checkDefault(final RunData run, TexLabelAccess access) throws BadLocationException {
		while (access != null) {
			final TexAstNode nameNode = access.getNameNode();
			if (nameNode == null) {
				return null;
			}
			if (run.accept(new Point(nameNode.getOffset(), nameNode.getStopOffset()))) {
				final List<? extends TexLabelAccess> accessList = access.getAllInUnit();
				final Map<Annotation, Position> annotations = new LinkedHashMap<Annotation, Position>(accessList.size());
				for (final TexLabelAccess item : accessList) {
					final String message = run.doc.get(item.getNode().getOffset(), item.getNode().getLength());
					annotations.put(
							new Annotation(item.isWriteAccess() ? 
									ITextPresentationConstants.ANNOTATIONS_WRITE_OCCURRENCES_TYPE:
									ITextPresentationConstants.ANNOTATIONS_COMMON_OCCURRENCES_TYPE,
									false, message),
							TexLabelAccess.getTextPosition(item.getNameNode()) );
				}
				return annotations;
			}
//			access = access.getNextSegment();
			access = null;
		}
		return null;
	}
	
}
