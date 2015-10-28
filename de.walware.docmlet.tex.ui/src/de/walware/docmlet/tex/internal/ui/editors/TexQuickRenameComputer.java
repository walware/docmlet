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

package de.walware.docmlet.tex.internal.ui.editors;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistProposalCollector;
import de.walware.ecommons.ltk.ui.sourceediting.assist.IAssistCompletionProposal;
import de.walware.ecommons.ltk.ui.sourceediting.assist.IQuickAssistComputer;

import de.walware.docmlet.tex.core.ast.TexAst;
import de.walware.docmlet.tex.core.ast.TexAstNode;
import de.walware.docmlet.tex.core.model.TexNameAccess;


public class TexQuickRenameComputer implements IQuickAssistComputer {
	
	
	public TexQuickRenameComputer() {
	}
	
	
	@Override
	public IStatus computeAssistProposals(final AssistInvocationContext context,
			final AssistProposalCollector<IAssistCompletionProposal> proposals,
			final IProgressMonitor monitor) {
		if (!(context.getAstSelection().getCovering() instanceof TexAstNode)) {
			return Status.OK_STATUS;
		}
		final TexAstNode node = (TexAstNode) context.getAstSelection().getCovering();
		
		if (node.getNodeType() == TexAst.NodeType.LABEL) {
			TexAstNode candidate = node;
			SEARCH_ACCESS : while (candidate != null) {
				final List<Object> attachments= candidate.getAttachments();
				for (final Object attachment : attachments) {
					if (attachment instanceof TexNameAccess) {
						TexNameAccess access= (TexNameAccess) attachment; 
						SUB: while (access != null) {
							if (access.getSegmentName() == null) {
								break SUB;
							}
							if (access.getNameNode() == node) {
								addAccessAssistProposals(context, access, proposals);
								break SEARCH_ACCESS;
							}
//							access = access.getNextSegment();
							access = null;
						}
					}
				}
				candidate= candidate.getTexParent();
			}
		}
		return Status.OK_STATUS;
	}
	
	protected void addAccessAssistProposals(final AssistInvocationContext context,
			final TexNameAccess access,
			final AssistProposalCollector<IAssistCompletionProposal> proposals) {
		final ImList<? extends TexNameAccess> accessList= access.getAllInUnit();
		
		proposals.add(new TexLinkedNamesAssistProposal(TexLinkedNamesAssistProposal.IN_FILE, context, access));
//		
//		if (accessList.length > 2) {
//			Arrays.sort(accessList, RElementAccess.NAME_POSITION_COMPARATOR);
//			
//			int current = 0;
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
