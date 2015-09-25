/*=============================================================================#
 # Copyright (c) 2008-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.wikitext.internal.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.walware.ecommons.collections.ImList;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistProposalCollector;
import de.walware.ecommons.ltk.ui.sourceediting.assist.IAssistCompletionProposal;
import de.walware.ecommons.ltk.ui.sourceediting.assist.IQuickAssistComputer;

import de.walware.docmlet.wikitext.core.ast.WikitextAst;
import de.walware.docmlet.wikitext.core.ast.WikitextAstNode;
import de.walware.docmlet.wikitext.core.model.WikitextNameAccess;


public class WikitextQuickRenameComputer implements IQuickAssistComputer {
	
	
	public WikitextQuickRenameComputer() {
	}
	
	
	@Override
	public IStatus computeAssistProposals(final AssistInvocationContext context,
			final AssistProposalCollector<IAssistCompletionProposal> proposals,
			final IProgressMonitor monitor) {
		if (!(context.getAstSelection().getCovering() instanceof WikitextAstNode)) {
			return Status.OK_STATUS;
		}
		final WikitextAstNode node= (WikitextAstNode) context.getAstSelection().getCovering();
		
		if (node.getNodeType() == WikitextAst.NodeType.LABEL) {
			WikitextAstNode candidate= node;
			SEARCH_ACCESS : while (candidate != null) {
				for (final Object attachment : candidate.getAttachments()) {
					if (attachment instanceof WikitextNameAccess) {
						WikitextNameAccess access= (WikitextNameAccess) attachment; 
						SUB: while (access != null) {
							if (access.getSegmentName() == null) {
								break SUB;
							}
							if (access.getNameNode() == node) {
								addAccessAssistProposals(context, access, proposals);
								break SEARCH_ACCESS;
							}
//							access= access.getNextSegment();
							access= null;
						}
					}
				}
				candidate= candidate.getWikitextParent();
			}
		}
		return Status.OK_STATUS;
	}
	
	protected void addAccessAssistProposals(final AssistInvocationContext context,
			final WikitextNameAccess access,
			final AssistProposalCollector<IAssistCompletionProposal> proposals) {
		final ImList<? extends WikitextNameAccess> accessList= access.getAllInUnit();
		
		proposals.add(new WikitextLinkedNamesAssistProposal(WikitextLinkedNamesAssistProposal.IN_FILE, context, access));
//		
//		if (accessList.length > 2) {
//			Arrays.sort(accessList, RElementAccess.NAME_POSITION_COMPARATOR);
//			
//			int current= 0;
//			for (; current < accessList.length; current++) {
//				if (access == accessList[current]) {
//					break;
//				}
//			}
//			if (current > 0 && current < accessList.length-1) {
//				proposals.add(new LinkedNamesAssistProposal(LinkedNamesAssistProposal.IN_FILE_PRECEDING, context, access));
//				proposals.add(new LinkedNamesAssistProposal(LinkedNamesAssistProposal.IN_FILE_FOLLOWING, context, access));
//			}
//		}
	}
	
}
