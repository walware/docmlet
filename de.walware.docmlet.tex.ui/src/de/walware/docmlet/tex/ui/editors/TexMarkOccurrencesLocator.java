/*=============================================================================#
 # Copyright (c) 2011-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

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

import de.walware.ecommons.ltk.ast.AstSelection;
import de.walware.ecommons.ltk.core.model.ISourceUnitModelInfo;
import de.walware.ecommons.ltk.ui.sourceediting.AbstractMarkOccurrencesProvider.RunData;
import de.walware.ecommons.text.ui.presentation.ITextPresentationConstants;

import de.walware.docmlet.tex.core.ast.TexAst;
import de.walware.docmlet.tex.core.ast.TexAstNode;
import de.walware.docmlet.tex.core.model.TexNameAccess;


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
			for (final Object attachment : node.getAttachments()) {
				if (attachment instanceof TexNameAccess) {
					final TexNameAccess access = (TexNameAccess) attachment;
					final Map<Annotation, Position> annotations = checkDefault(run, access);
					
					if (annotations != null) {
						run.set(annotations);
						return true;
					}
				}
			}
			node= node.getTexParent();
		} while (node != null);
		
		return false;
	}
	
	private Map<Annotation, Position> checkDefault(final RunData run, TexNameAccess access) throws BadLocationException {
		while (access != null) {
			final TexAstNode nameNode = access.getNameNode();
			if (nameNode == null) {
				return null;
			}
			if (run.accept(new Point(nameNode.getOffset(), nameNode.getEndOffset()))) {
				final List<? extends TexNameAccess> accessList= access.getAllInUnit();
				final Map<Annotation, Position> annotations = new LinkedHashMap<>(accessList.size());
				for (final TexNameAccess occurrence : accessList) {
					final String message = run.doc.get(occurrence.getNode().getOffset(), occurrence.getNode().getLength());
					annotations.put(
							new Annotation(occurrence.isWriteAccess() ? 
									ITextPresentationConstants.ANNOTATIONS_WRITE_OCCURRENCES_TYPE:
									ITextPresentationConstants.ANNOTATIONS_COMMON_OCCURRENCES_TYPE,
									false, message),
							TexNameAccess.getTextPosition(occurrence.getNameNode()) );
				}
				return annotations;
			}
//			access = access.getNextSegment();
			access = null;
		}
		return null;
	}
	
}
