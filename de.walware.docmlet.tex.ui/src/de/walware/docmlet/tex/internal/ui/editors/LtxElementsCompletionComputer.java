/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.docmlet.tex.internal.ui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IRegion;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.ltk.LTKUtil;
import de.walware.ecommons.ltk.core.model.IModelElement;
import de.walware.ecommons.ltk.core.model.INameAccessSet;
import de.walware.ecommons.ltk.core.model.ISourceStructElement;
import de.walware.ecommons.ltk.core.model.ISourceUnit;
import de.walware.ecommons.ltk.ui.sourceediting.ISourceEditor;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistInvocationContext;
import de.walware.ecommons.ltk.ui.sourceediting.assist.AssistProposalCollector;
import de.walware.ecommons.ltk.ui.sourceediting.assist.ContentAssist;
import de.walware.ecommons.ltk.ui.sourceediting.assist.IContentAssistComputer;

import de.walware.docmlet.tex.core.ITexCoreAccess;
import de.walware.docmlet.tex.core.ITexProblemConstants;
import de.walware.docmlet.tex.core.TexCore;
import de.walware.docmlet.tex.core.ast.ControlNode;
import de.walware.docmlet.tex.core.ast.ITexAstStatusConstants;
import de.walware.docmlet.tex.core.ast.TexAst;
import de.walware.docmlet.tex.core.ast.TexAst.NodeType;
import de.walware.docmlet.tex.core.ast.TexAstNode;
import de.walware.docmlet.tex.core.commands.Argument;
import de.walware.docmlet.tex.core.commands.IEnvDefinitions;
import de.walware.docmlet.tex.core.commands.IPreambleDefinitions;
import de.walware.docmlet.tex.core.commands.LtxCommandDefinitions;
import de.walware.docmlet.tex.core.commands.TexCommand;
import de.walware.docmlet.tex.core.commands.TexCommandSet;
import de.walware.docmlet.tex.core.model.ILtxModelInfo;
import de.walware.docmlet.tex.core.model.ITexSourceElement;
import de.walware.docmlet.tex.core.model.ITexSourceUnit;
import de.walware.docmlet.tex.core.model.TexNameAccess;
import de.walware.docmlet.tex.internal.ui.sourceediting.LtxAssistInvocationContext;


public abstract class LtxElementsCompletionComputer implements IContentAssistComputer {
	
	
	public static class Math extends LtxElementsCompletionComputer {
		
		
		public Math() {
		}
		
		
		@Override
		protected boolean isMath() {
			return true;
		}
		
	}
	
	public static class Default extends LtxElementsCompletionComputer {
		
		
		public Default() {
		}
		
		
		@Override
		protected boolean isMath() {
			return false;
		}
		
	}
	
	private static List<TexCommand> PREAMBLE_DOCU_COMMANDS= ImCollections.newList(
			IPreambleDefinitions.PREAMBLE_documentclass_COMMAND
	);
	
	
	private ITexCoreAccess fTexCoreAccess;
	
	
	protected LtxElementsCompletionComputer() {
	}
	
	
	@Override
	public void sessionStarted(final ISourceEditor editor, final ContentAssist assist) {
		final ISourceUnit su = editor.getSourceUnit();
		if (su instanceof ITexSourceUnit) {
			fTexCoreAccess = ((ITexSourceUnit) su).getTexCoreAccess();
		}
	}
	
	@Override
	public void sessionEnded() {
		fTexCoreAccess = null;
	}
	
	
	protected final ITexCoreAccess getTexCoreAccess() {
		return (fTexCoreAccess != null) ? fTexCoreAccess : TexCore.getWorkbenchAccess();
	}
	
	protected abstract boolean isMath();
	
	
	@Override
	public IStatus computeCompletionProposals(final AssistInvocationContext context, final int mode,
			final AssistProposalCollector proposals, final IProgressMonitor monitor) {
		final String prefix = context.getIdentifierPrefix();
		final ILtxModelInfo modelInfo= (context.getModelInfo() instanceof ILtxModelInfo) ?
				(ILtxModelInfo) context.getModelInfo() : null;
		final TexCommandSet commandSet = getTexCoreAccess().getTexCommandSet();
		
		if (prefix.length() > 0 && prefix.charAt(0) == '\\') {
			final int offset = context.getInvocationOffset() - prefix.length() + 1;
			addCommands(context, prefix, (isMath()) ?
					commandSet.getLtxMathCommandsASorted() : commandSet.getLtxTextCommandsASorted(),
					(modelInfo != null) ? modelInfo.getCustomCommandMap().values() : null,
					proposals );
			if (modelInfo != null && !isMath()) {
				if (modelInfo.getSourceElement() != null) {
					final List<? extends ISourceStructElement> elements = modelInfo
							.getSourceElement().getSourceChildren(null);
					final ISourceStructElement element = LTKUtil.getCoveringSourceElement(elements, offset);
					if (element != null
							&& (element.getElementType() & IModelElement.MASK_C2) == ITexSourceElement.C2_PREAMBLE) {
						addCommands(context, prefix,
								commandSet.getLtxPreambleCommandsASorted(), null,
								proposals );
					}
					else if (prefix.startsWith("\\docu") //$NON-NLS-1$
							&& (elements.size() == 0 || offset < elements.get(0).getSourceRange().getOffset()) ) {
						addCommands(context, prefix, PREAMBLE_DOCU_COMMANDS, null, proposals);
					}
					if (!isMath() && context.getAstSelection().getCovering() instanceof TexAstNode) {
						TexAstNode texNode = (TexAstNode) context.getAstSelection().getCovering();
						while (texNode != null) {
							TexCommand command;
							if (texNode.getNodeType() == TexAst.NodeType.CONTROL
									&& (command = ((ControlNode) texNode).getCommand()) != null
									&& (command.getType() & TexCommand.MASK_C2) == TexCommand.C2_PREAMBLE_CONTROLDEF) {
								addCommands(context, prefix,
										commandSet.getLtxMathCommandsASorted(), null, proposals );
								break;
							}
							texNode= texNode.getTexParent();
						}
					}
				}
			}
		}
		else if (context instanceof LtxAssistInvocationContext) {
			final LtxAssistInvocationContext texContext = (LtxAssistInvocationContext) context;
			final int argIdx = texContext.getInvocationArgIdx();
			if (argIdx >= 0) {
				final TexCommand command = texContext.getInvocationControlNode().getCommand();
				final Argument argDef = command.getArguments().get(argIdx);
				final TexAstNode argNode = texContext.getInvocationArgNodes()[argIdx];
				final int offset = texContext.getInvocationOffset() - prefix.length();
				final IRegion region = TexAst.getInnerRegion(argNode);
				if (region != null && region.getOffset() >= offset && offset <= region.getOffset()+region.getLength()) {
					if (argIdx == 0
							&& ((command.getType() & TexCommand.MASK_MAIN) == TexCommand.GENERICENV
									|| (command.getType() & TexCommand.MASK_MAIN) == TexCommand.ENV )) {
						final List<String> prefered = new ArrayList<>();
						if (command == IEnvDefinitions.GENERICENV_end_COMMAND) {
							TexAstNode node = texContext.getInvocationControlNode();
							while (node != null) {
								if (node.getNodeType() == NodeType.ENVIRONMENT
										&& (prefered.isEmpty()
												|| (node.getStatusCode() & ITexProblemConstants.MASK_12) == ITexAstStatusConstants.STATUS2_ENV_NOT_CLOSED )) {
									final String name = node.getText();
									if (!name.isEmpty() && !prefered.contains(name)) {
										prefered.add(name);
									}
								}
								node= node.getTexParent();
							}
						}
						addEnvs(context, prefix, (isMath()) ?
								commandSet.getLtxMathEnvsASorted() : commandSet.getLtxTextEnvsASorted(),
								(modelInfo != null) ? modelInfo.getCustomEnvMap().values() : null,
								prefered, proposals );
					}
					else {
						switch (argDef.getContent()) {
						case Argument.LABEL_REFLABEL_DEF:
							if (modelInfo != null) {
								final INameAccessSet<TexNameAccess> labels = modelInfo.getLabels();
								LABELS: for (final String label : labels.getNames()) {
									final ImList<TexNameAccess> accessList= labels.getAllInUnit(label);
									boolean isDef = false;
									for (final TexNameAccess access : accessList) {
										if (access.isWriteAccess()) {
											if (isDef(access, offset)) {
												isDef = true;
											}
											else {
												proposals.add(new TexLabelCompletionProposal(context,
														offset, accessList.get(0), 94));
												continue LABELS;
											}
										}
									}
									if (isDef) {
										continue LABELS;
									}
									proposals.add(new TexLabelCompletionProposal(context,
											offset, accessList.get(0), 95));
								}
							}
							break;
						case Argument.LABEL_REFLABEL_REF:
							if (modelInfo != null) {
								final INameAccessSet<TexNameAccess> labels = modelInfo.getLabels();
								LABELS: for (final String label : labels.getNames()) {
									final ImList<TexNameAccess> accessList= labels.getAllInUnit(label);
									for (final TexNameAccess access : accessList) {
										if (access.isWriteAccess()) {
											proposals.add(new TexLabelCompletionProposal(context,
													offset, accessList.get(0), 95));
											continue LABELS;
										}
									}
									if (accessList.size() == 1 && isDef(accessList.get(0), offset)) {
										continue LABELS;
									}
									proposals.add(new TexLabelCompletionProposal(context,
											offset, accessList.get(0), 94));
								}
							}
						}
					}
				}
			}
		}
		
		return Status.OK_STATUS;
	}
	
	@Override
	public IStatus computeInformationProposals(final AssistInvocationContext context,
			final AssistProposalCollector tenders, final IProgressMonitor monitor) {
		return null;
	}
	
	
	private void addCommands(final AssistInvocationContext context, final String prefix,
			final List<TexCommand> commands, final Collection<TexCommand> commands2,
			final AssistProposalCollector proposals) {
		final int offset = context.getInvocationOffset() - prefix.length() + 1;
		final int length = prefix.length() - 1;
		for (final TexCommand command : commands) {
			if ((prefix.length() == 1 || command.getControlWord().regionMatches(true, 0, prefix, 1, length))
					&& (command.getType() & TexCommand.MASK_C2) != TexCommand.C2_SYMBOL_CHAR) {
				proposals.add(new LtxCommandCompletionProposal(context, offset, command));
			}
		}
		if (commands2 != null) {
			for (final TexCommand command : commands2) {
				if ((prefix.length() == 1 || command.getControlWord().regionMatches(true, 0, prefix, 1, length))
						&& (command.getType() & TexCommand.MASK_C2) != TexCommand.C2_SYMBOL_CHAR) {
					proposals.add(new LtxCommandCompletionProposal(context, offset, command));
				}
			}
		}
	}
	
	private void addEnvs(final AssistInvocationContext context, final String prefix,
			final List<TexCommand> envs, final Collection<TexCommand> envs2,
			final List<String> prefered,
			final AssistProposalCollector proposals) {
		final int offset = context.getInvocationOffset() - prefix.length();
		final int length = prefix.length();
		final List<String> addedPrefered = new ArrayList<>(prefered.size());
		for (final TexCommand env : envs) {
			if (prefix.length() == 0 || env.getControlWord().regionMatches(true, 0, prefix, 0, length)) {
				final int idx = prefered.indexOf(env.getControlWord());
				proposals.add(new LtxCommandCompletionProposal.Env(context, offset, env,
						(idx >= 0 && idx < 5) ? 5-idx : 0));
				if (idx >= 0) {
					addedPrefered.add(env.getControlWord());
				}
			}
		}
		if (envs2 != null) {
			for (final TexCommand env : envs2) {
				if (prefix.length() == 0 || env.getControlWord().regionMatches(true, 0, prefix, 0, length)) {
					final int idx = prefered.indexOf(env.getControlWord());
					proposals.add(new LtxCommandCompletionProposal.Env(context, offset, env,
							(idx >= 0 && idx < 5) ? 5-idx : 0));
					if (idx >= 0) {
						addedPrefered.add(env.getControlWord());
					}
				}
			}
		}
		for (final String name : prefered) {
			if ((prefix.length() == 0 || name.regionMatches(true, 0, prefix, 0, length))
					&& !addedPrefered.contains(name) ) {
				final int idx = prefered.indexOf(name);
				TexCommand env = LtxCommandDefinitions.getEnv(name);
				if (env == null) {
					env = new TexCommand(TexCommand.C2_ENV_OTHER_BEGIN, name, "(open environment)");
				}
				proposals.add(new LtxCommandCompletionProposal.Env(context, offset, env,
						(idx >= 0 && idx < 5) ? 5-idx : 0));
			}
		}
	}
	
	private boolean isDef(final TexNameAccess access, final int offset) {
		final TexAstNode nameNode = access.getNameNode();
		return (nameNode != null
				&& nameNode.getOffset() <= offset
				&& nameNode.getOffset() + nameNode.getLength() >= offset);
	}
	
}
