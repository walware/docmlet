/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.core.model;

import static de.walware.docmlet.tex.core.model.ILtxModelProblemConstants.STATUS2_LABEL_UNDEFINED;

import java.util.ArrayList;
import java.util.List;

import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.MessageBuilder;
import de.walware.ecommons.ltk.IProblem;
import de.walware.ecommons.ltk.IProblemRequestor;
import de.walware.ecommons.ltk.core.SourceContent;
import de.walware.ecommons.ltk.core.impl.Problem;
import de.walware.ecommons.ltk.core.model.INameAccessSet;
import de.walware.ecommons.ltk.core.model.ISourceUnit;
import de.walware.ecommons.text.core.ILineInformation;

import de.walware.docmlet.tex.core.ast.TexAstNode;
import de.walware.docmlet.tex.core.model.ILtxModelInfo;
import de.walware.docmlet.tex.core.model.ITexSourceUnit;
import de.walware.docmlet.tex.core.model.TexModel;
import de.walware.docmlet.tex.core.model.TexNameAccess;


public class LtxProblemModelCheck {
	
	
	private static final int REF_LABEL_LIMIT = 50;
	private static final int BUFFER_SIZE = 100;
	
	
	private ISourceUnit fCurrentUnit;
	private String fCurrentText;
	private ILineInformation fCurrentLines;
	private IProblemRequestor fCurrentRequestor;
	
	private final MessageBuilder fMessageBuilder = new MessageBuilder();
	private final List<IProblem> fProblemBuffer = new ArrayList<>(BUFFER_SIZE);
	
	private final int fLevelRefUndefined = IProblem.SEVERITY_WARNING;
	
	
	public void run(final ITexSourceUnit su, final SourceContent content,
			final ILtxModelInfo model, final IProblemRequestor requestor) {
		try {
			fCurrentUnit = su;
			fCurrentText = content.getText();
			fCurrentLines = content.getLines();
			
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
		final INameAccessSet<TexNameAccess> labelSet = model.getLabels();
		final List<String> labels = labelSet.getNames();
		ITER_LABELS: for (final String label : labels) {
			if (label != null && label.length() > 0) {
				final ImList<TexNameAccess> accessList= labelSet.getAllInUnit(label);
				for (final TexNameAccess access : accessList) {
					if (access.isWriteAccess()) {
						continue ITER_LABELS;
					}
				}
				for (final TexNameAccess access : accessList) {
					final TexAstNode nameNode = access.getNameNode();
					addProblem(fLevelRefUndefined, STATUS2_LABEL_UNDEFINED,
							fMessageBuilder.bind(ProblemMessages.Labels_UndefinedRef_message, access.getDisplayName()),
							nameNode.getOffset(), nameNode.getEndOffset() );
				}
			}
		}
	}
	
	protected final void addProblem(final int severity, final int code, final String message,
			int startOffset, int stopOffset) {
		if (startOffset < 0) {
			startOffset = 0;
		}
		if (stopOffset > fCurrentText.length()) {
			stopOffset = fCurrentText.length();
		}
		fProblemBuffer.add(new Problem(TexModel.LTX_TYPE_ID, severity, code, message, fCurrentUnit,
				startOffset, stopOffset ));
		if (fProblemBuffer.size() >= BUFFER_SIZE) {
			fCurrentRequestor.acceptProblems(TexModel.LTX_TYPE_ID, fProblemBuffer);
			fProblemBuffer.clear();
		}
	}
	
}
