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

import java.util.Arrays;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.link.LinkedPositionGroup;

import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.ltk.core.util.NameUtils;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;
import de.walware.ecommons.ltk.ui.sourceediting.assist.LinkedNamesAssistProposal;

import de.walware.docmlet.wikitext.core.model.WikitextNameAccess;


public class WikitextLinkedNamesAssistProposal extends LinkedNamesAssistProposal {
	
	
	public static final int IN_FILE= 1;
//	public static final int IN_FILE_PRECEDING= 2;
//	public static final int IN_FILE_FOLLOWING= 3;
	
	
	private final WikitextNameAccess access;
	
	private final int mode;
	
	
	public WikitextLinkedNamesAssistProposal(final int mode,
			final AssistInvocationContext invocationContext, final WikitextNameAccess access) {
		super(invocationContext);
		this.mode= mode;
		switch (mode) {
		case IN_FILE:
			init(Messages.Proposal_RenameInFile_label,
					Messages.Proposal_RenameInFile_description,
					90 );
			break;
		default:
			throw new IllegalArgumentException();
		}
		this.access= access;
	}
	
	
	@Override
	protected void collectPositions(final IDocument document, final LinkedPositionGroup group)
			throws BadLocationException {
		final WikitextNameAccess[] all;
		{	final ImList<? extends WikitextNameAccess> accessList= this.access.getAllInUnit();
			all= accessList.toArray(new WikitextNameAccess[accessList.size()]);
			Arrays.sort(all, NameUtils.NAME_POSITION_COMPARATOR);
		}
		int current= -1;
		for (int i= 0; i < all.length; i++) {
			if (this.access == all[i]) {
				current= i;
				break;
			}
		}
		if (current < 0) {
			return;
		}
		int idx= 0;
		idx= addPosition(group, document, getPosition(all[current]), idx);
		if (this.mode == IN_FILE) {
			for (int i= current+1; i < all.length; i++) {
				idx= addPosition(group, document, getPosition(all[i]), idx);
			}
		}
		if (this.mode == IN_FILE) {
			for (int i= 0; i < current; i++) {
				idx= addPosition(group, document, getPosition(all[i]), idx);
			}
		}
	}
	
	private Position getPosition(final WikitextNameAccess access) {
		return WikitextNameAccess.getTextPosition(access.getNameNode());
	}
	
}
