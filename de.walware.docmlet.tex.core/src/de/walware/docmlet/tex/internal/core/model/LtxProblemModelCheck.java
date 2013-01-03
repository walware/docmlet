/*******************************************************************************
 * Copyright (c) 2012-2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.internal.core.model;

import static de.walware.docmlet.tex.core.model.ILtxModelProblemConstants.STATUS2_LABEL_UNDEFINED;

import java.util.ArrayList;
import java.util.List;

import de.walware.ecommons.MessageBuilder;
import de.walware.ecommons.ltk.IProblem;
import de.walware.ecommons.ltk.IProblemRequestor;
import de.walware.ecommons.ltk.ISourceUnit;
import de.walware.ecommons.ltk.SourceContent;
import de.walware.ecommons.ltk.SourceContentLines;
import de.walware.ecommons.ltk.core.impl.Problem;
import de.walware.ecommons.text.ILineInformation;

import de.walware.docmlet.tex.core.ast.TexAstNode;
import de.walware.docmlet.tex.core.model.ILtxModelInfo;
import de.walware.docmlet.tex.core.model.ILtxSourceUnit;
import de.walware.docmlet.tex.core.model.ITexLabelSet;
import de.walware.docmlet.tex.core.model.TexLabelAccess;
import de.walware.docmlet.tex.core.model.TexModel;


public class LtxProblemModelCheck {
	
	
	private static final int REF_LABEL_LIMIT = 50;
	private static final int BUFFER_SIZE = 100;
	
	
	private ISourceUnit fCurrentUnit;
	private SourceContent fCurrentContent;
	private ILineInformation fCurrentLines;
	private IProblemRequestor fCurrentRequestor;
	
	private final MessageBuilder fMessageBuilder = new MessageBuilder();
	private final List<IProblem> fProblemBuffer = new ArrayList<IProblem>(BUFFER_SIZE);
	
	private final int fLevelRefUndefined = IProblem.SEVERITY_WARNING;
	
	
	public void run(final ILtxSourceUnit su, final SourceContentLines content,
			final ILtxModelInfo model, final IProblemRequestor requestor) {
		try {
			fCurrentUnit = su;
			fCurrentContent = content;
			fCurrentLines = content.lines;
			
			fCurrentRequestor = requestor;
			checkLabels(model);
			if (fProblemBuffer.size() > 0) {
				fCurrentRequestor.acceptProblems(TexModel.LTX_TYPE_ID, fProblemBuffer);
			}
		}
//		catch (final InvocationTargetException e) {}
		finally {
			fProblemBuffer.clear();
			fCurrentUnit = null;
			fCurrentRequestor = null;
		}
	}
	
	
	private void checkLabels(final ILtxModelInfo model) {
		final ITexLabelSet labelSet = model.getLabels();
		final List<String> labels = labelSet.getAccessLabels();
		ITER_LABELS: for (final String label : labels) {
			if (label != null && label.length() > 0) {
				final List<TexLabelAccess> all = labelSet.getAllAccessOf(label);
				for (final TexLabelAccess access : all) {
					if (access.isWriteAccess()) {
						continue ITER_LABELS;
					}
				}
				for (final TexLabelAccess access : all) {
					final TexAstNode nameNode = access.getNameNode();
					addProblem(fLevelRefUndefined, STATUS2_LABEL_UNDEFINED,
							fMessageBuilder.bind(ProblemMessages.Labels_UndefinedRef_message, access.getDisplayName()),
							nameNode.getOffset(), nameNode.getStopOffset() );
				}
			}
		}
	}
	
	protected final void addProblem(final int severity, final int code, final String message,
			int startOffset, int stopOffset) {
		if (startOffset < 0) {
			startOffset = 0;
		}
		if (stopOffset > fCurrentContent.text.length()) {
			stopOffset = fCurrentContent.text.length();
		}
		fProblemBuffer.add(new Problem(TexModel.LTX_TYPE_ID, severity, code, message, fCurrentUnit,
				startOffset, stopOffset ));
		if (fProblemBuffer.size() >= BUFFER_SIZE) {
			fCurrentRequestor.acceptProblems(TexModel.LTX_TYPE_ID, fProblemBuffer);
			fProblemBuffer.clear();
		}
	}
	
}
