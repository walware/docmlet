/*=============================================================================#
 # Copyright (c) 2011-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.ui.editors;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.graphics.Point;

import de.walware.ecommons.collections.ImList;
import de.walware.ecommons.ltk.ast.AstSelection;
import de.walware.ecommons.ltk.core.model.ISourceUnitModelInfo;
import de.walware.ecommons.ltk.ui.sourceediting.AbstractMarkOccurrencesProvider.RunData;
import de.walware.ecommons.text.ui.presentation.ITextPresentationConstants;

import de.walware.docmlet.wikitext.core.ast.WikitextAst;
import de.walware.docmlet.wikitext.core.ast.WikitextAstNode;
import de.walware.docmlet.wikitext.core.model.WikitextNameAccess;


public class WikitextMarkOccurrencesLocator {
	
	
	public void run(final RunData run, final ISourceUnitModelInfo info,
			final AstSelection astSelection, final ITextSelection orgSelection)
			throws BadLocationException, BadPartitioningException, UnsupportedOperationException {
		final WikitextAstNode node= (WikitextAstNode) astSelection.getCovering();
		if (checkForAccess(run, node)) {
			return;
		}
	}
	
	private boolean checkForAccess(final RunData run, WikitextAstNode node) throws BadLocationException {
		if (node == null || !(node.getNodeType() == WikitextAst.NodeType.LABEL)) {
			return false;
		}
		do {
			for (final Object attachment : node.getAttachments()) {
				if (attachment instanceof WikitextNameAccess) {
					final WikitextNameAccess access= (WikitextNameAccess) attachment;
					final Map<Annotation, Position> annotations= checkDefault(run, access);
					
					if (annotations != null) {
						run.set(annotations);
						return true;
					}
				}
			}
			node= node.getWikitextParent();
		} while (node != null);
		
		return false;
	}
	
	private Map<Annotation, Position> checkDefault(final RunData run, WikitextNameAccess access) throws BadLocationException {
		while (access != null) {
			final WikitextAstNode nameNode= access.getNameNode();
			if (nameNode == null) {
				return null;
			}
			if (run.accept(new Point(nameNode.getOffset(), nameNode.getEndOffset()))) {
				final ImList<? extends WikitextNameAccess> accessList= access.getAllInUnit();
				final Map<Annotation, Position> annotations= new LinkedHashMap<>(accessList.size());
				for (final WikitextNameAccess occurrence : accessList) {
					final String message= run.doc.get(occurrence.getNode().getOffset(), occurrence.getNode().getLength());
					annotations.put(
							new Annotation(occurrence.isWriteAccess() ? 
									ITextPresentationConstants.ANNOTATIONS_WRITE_OCCURRENCES_TYPE:
									ITextPresentationConstants.ANNOTATIONS_COMMON_OCCURRENCES_TYPE,
									false, message),
							WikitextNameAccess.getTextPosition(occurrence.getNameNode()) );
				}
				return annotations;
			}
//			access= access.getNextSegment();
			access= null;
		}
		return null;
	}
	
}
