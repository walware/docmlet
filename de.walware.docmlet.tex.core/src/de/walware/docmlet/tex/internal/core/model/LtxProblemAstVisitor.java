/*=============================================================================#
 # Copyright (c) 2012-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.core.model;

import static de.walware.docmlet.tex.core.ITexProblemConstants.MASK_12;
import static de.walware.docmlet.tex.core.ast.ITexAstStatusConstants.STATUS2_ENV_MISSING_NAME;
import static de.walware.docmlet.tex.core.ast.ITexAstStatusConstants.STATUS2_ENV_NOT_CLOSED;
import static de.walware.docmlet.tex.core.ast.ITexAstStatusConstants.STATUS2_ENV_NOT_OPENED;
import static de.walware.docmlet.tex.core.ast.ITexAstStatusConstants.STATUS2_GROUP_NOT_CLOSED;
import static de.walware.docmlet.tex.core.ast.ITexAstStatusConstants.STATUS2_GROUP_NOT_OPENED;
import static de.walware.docmlet.tex.core.ast.ITexAstStatusConstants.STATUS2_MATH_NOT_CLOSED;
import static de.walware.docmlet.tex.core.ast.ITexAstStatusConstants.STATUS2_VERBATIM_INLINE_C_MISSING;
import static de.walware.docmlet.tex.core.ast.ITexAstStatusConstants.STATUS2_VERBATIM_INLINE_NOT_CLOSED;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import de.walware.ecommons.MessageBuilder;
import de.walware.ecommons.ltk.IProblem;
import de.walware.ecommons.ltk.IProblemRequestor;
import de.walware.ecommons.ltk.ISourceUnit;
import de.walware.ecommons.ltk.SourceContent;
import de.walware.ecommons.ltk.core.impl.Problem;
import de.walware.ecommons.text.ILineInformation;

import de.walware.docmlet.tex.core.ast.Comment;
import de.walware.docmlet.tex.core.ast.ControlNode;
import de.walware.docmlet.tex.core.ast.Dummy;
import de.walware.docmlet.tex.core.ast.Embedded;
import de.walware.docmlet.tex.core.ast.Environment;
import de.walware.docmlet.tex.core.ast.Group;
import de.walware.docmlet.tex.core.ast.Label;
import de.walware.docmlet.tex.core.ast.Math;
import de.walware.docmlet.tex.core.ast.SourceComponent;
import de.walware.docmlet.tex.core.ast.TexAstNode;
import de.walware.docmlet.tex.core.ast.TexAstVisitor;
import de.walware.docmlet.tex.core.ast.Text;
import de.walware.docmlet.tex.core.ast.Verbatim;
import de.walware.docmlet.tex.core.model.ILtxSourceUnit;
import de.walware.docmlet.tex.core.model.TexModel;


public class LtxProblemAstVisitor extends TexAstVisitor {
	
	
	private static final int ENV_LABEL_LIMIT = 20;
	private static final int BUFFER_SIZE = 100;
	
	
	private ISourceUnit fCurrentUnit;
	private String fCurrentText;
	private ILineInformation fCurrentLines;
	private IProblemRequestor fCurrentRequestor;
	
	private final MessageBuilder fMessageBuilder = new MessageBuilder();
	private final List<IProblem> fProblemBuffer = new ArrayList<>(BUFFER_SIZE);
	
	
	public void run(final ILtxSourceUnit su, final SourceContent content,
			final TexAstNode node, final IProblemRequestor requestor) {
		try {
			fCurrentUnit = su;
			fCurrentText = content.getText();
			fCurrentLines = content.getLines();
			
			fCurrentRequestor = requestor;
			node.acceptInTex(this);
			if (fProblemBuffer.size() > 0) {
				fCurrentRequestor.acceptProblems(TexModel.LTX_TYPE_ID, fProblemBuffer);
			}
		}
		catch (final InvocationTargetException e) {}
		finally {
			fProblemBuffer.clear();
			fCurrentUnit = null;
			fCurrentRequestor = null;
		}
	}
	
	
	@Override
	public void visit(final SourceComponent node) throws InvocationTargetException {
		node.acceptInTexChildren(this);
	}
	
	@Override
	public void visit(final Group node) throws InvocationTargetException {
		final int code = (node.getStatusCode() & MASK_12);
		if (code == STATUS2_GROUP_NOT_CLOSED) {
			if (node.getParent() instanceof ControlNode) {
				addProblem(IProblem.SEVERITY_ERROR, code, (node.getText() == "{") ? //$NON-NLS-1$
						ProblemMessages.Ast_ReqArgument_NotClosed_message :
						ProblemMessages.Ast_OptArgument_NotClosed_Opt_message,
						node.getOffset(), node.getOffset()+1 );
			}
			else {
				addProblem(IProblem.SEVERITY_ERROR, code, (node.getText() == "{") ? //$NON-NLS-1$
						ProblemMessages.Ast_CurlyBracket_NotClosed_message :
						ProblemMessages.Ast_SquareBracket_NotClosed_message,
						node.getOffset(), node.getOffset()+1 );
			}
		}
		
		node.acceptInTexChildren(this);
	}
	
	@Override
	public void visit(final Environment node) throws InvocationTargetException {
		final int code = (node.getStatusCode() & MASK_12);
		if (code == STATUS2_ENV_NOT_CLOSED) {
			final TexAstNode beginNode = node.getBeginNode();
			addProblem(IProblem.SEVERITY_ERROR, code,
					fMessageBuilder.bind(ProblemMessages.Ast_Env_NotClosed_message,
							limit(node.getText(), ENV_LABEL_LIMIT) ),
					beginNode.getOffset(), beginNode.getStopOffset() );
		}
		
		node.acceptInTexChildren(this);
	}
	
	@Override
	public void visit(final ControlNode node) throws InvocationTargetException {
		final int code = (node.getStatusCode() & MASK_12);
		switch (code) {
		case STATUS2_ENV_MISSING_NAME:
			addProblem(IProblem.SEVERITY_ERROR, code, (node.getText() == "begin") ? //$NON-NLS-1$
					ProblemMessages.Ast_Env_MissingName_Begin_message :
					ProblemMessages.Ast_Env_MissingName_End_message,
					node.getOffset(), node.getStopOffset() );
			break;
		case STATUS2_ENV_NOT_OPENED:
			addProblem(IProblem.SEVERITY_ERROR, code,
					fMessageBuilder.bind(ProblemMessages.Ast_Env_NotOpened_message,
							limit(node.getChild(0).getChild(0).getText(), ENV_LABEL_LIMIT) ),
					node.getOffset(), node.getStopOffset() );
			break;
		case STATUS2_VERBATIM_INLINE_C_MISSING:
			addProblem(IProblem.SEVERITY_ERROR, code,
					ProblemMessages.Ast_Verbatim_MissingSep_message,
					node.getStopOffset()-1, node.getStopOffset() );
			break;
		}
		
		node.acceptInTexChildren(this);
	}
	
	@Override
	public void visit(final Text node) throws InvocationTargetException {
	}
	
	@Override
	public void visit(final Label node) throws InvocationTargetException {
	}
	
	@Override
	public void visit(final Math node) throws InvocationTargetException {
		final int code = (node.getStatusCode() & MASK_12);
		switch (code) {
		case STATUS2_MATH_NOT_CLOSED:
			addProblem(IProblem.SEVERITY_ERROR, code,
					fMessageBuilder.bind(ProblemMessages.Ast_Math_NotClosed_message,
							node.getText() ),
					node.getStopOffset()-1, node.getStopOffset() );
			break;
		}
		node.acceptInTexChildren(this);
	}
	
	@Override
	public void visit(final Verbatim node) throws InvocationTargetException {
		final int code = (node.getStatusCode() & MASK_12);
		switch (code) {
		case STATUS2_VERBATIM_INLINE_NOT_CLOSED:
			addProblem(IProblem.SEVERITY_ERROR, code,
					fMessageBuilder.bind(ProblemMessages.Ast_Verbatim_NotClosed_message,
							new String(fCurrentText.substring(node.getOffset()-1, node.getOffset())) ),
					node.getStopOffset()-1, node.getStopOffset() );
			break;
		}
	}
	
	@Override
	public void visit(final Comment node) throws InvocationTargetException {
	}
	
	@Override
	public void visit(final Dummy node) throws InvocationTargetException {
		final int code = (node.getStatusCode() & MASK_12);
		switch (code) {
		case STATUS2_GROUP_NOT_OPENED:
			addProblem(IProblem.SEVERITY_ERROR, code, (node.getText() == "{") ? //$NON-NLS-1$
					ProblemMessages.Ast_CurlyBracket_NotOpened_message :
					ProblemMessages.Ast_SquareBracket_NotOpened_message,
					node.getOffset(), node.getOffset()+1 );
			break;
		}
	}
	
	@Override
	public void visit(final Embedded node) throws InvocationTargetException {
	}
	
	
	protected final String limit(final String label, final int limit) {
		if (label.length() > limit) {
			return label.substring(0, limit) + '…';
		}
		return label;
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
