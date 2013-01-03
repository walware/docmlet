/*******************************************************************************
 * Copyright (c) 2009-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.docmlet.tex.core.ast;

import static de.walware.docmlet.tex.core.ast.ITexAstStatusConstants.STATUS2_ENV_MISSING_NAME;
import static de.walware.docmlet.tex.core.ast.ITexAstStatusConstants.STATUS2_ENV_NOT_OPENED;
import static de.walware.docmlet.tex.core.ast.ITexAstStatusConstants.STATUS2_GROUP_NOT_CLOSED;
import static de.walware.docmlet.tex.core.ast.ITexAstStatusConstants.STATUS2_GROUP_NOT_OPENED;
import static de.walware.docmlet.tex.core.ast.ITexAstStatusConstants.STATUS2_VERBATIM_INLINE_C_MISSING;
import static de.walware.docmlet.tex.core.ast.ITexAstStatusConstants.STATUS2_VERBATIM_INLINE_NOT_CLOSED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.walware.ecommons.collections.ConstList;
import de.walware.ecommons.text.IStringCache;
import de.walware.ecommons.text.InternStringCache;
import de.walware.ecommons.text.NoStringCache;
import de.walware.ecommons.text.SourceParseInput;

import de.walware.docmlet.tex.core.ast.TexAst.NodeType;
import de.walware.docmlet.tex.core.commands.Argument;
import de.walware.docmlet.tex.core.commands.LtxCommandDefinitions;
import de.walware.docmlet.tex.core.commands.TexCommand;
import de.walware.docmlet.tex.core.commands.TexCommandSet;
import de.walware.docmlet.tex.core.commands.TexEmbedCommand;
import de.walware.docmlet.tex.core.parser.LtxLexer;


public class LtxParser {
	
	
	private static final byte ST_ROOT = 0;
	private static final byte ST_CURLY = 1;
	private static final byte ST_SQUARED = 2;
	private static final byte ST_BEGINEND = 3;
	private static final byte ST_MATHROUND = 4;
	private static final byte ST_MATHSQUARED = 5;
	private static final byte ST_MATHDOLLAR = 6;
	private static final byte ST_MATHDOLLARDOLLAR = 7;
	
	private static final String LABEL_TEXT = new String("LABEL");
	private static final String NUM_TEXT = new String("NUM");
	
	
	private final LtxLexer fLexer;
	
	private TexCommandSet fCommandSet;
	
	private Map<String, TexCommand> fCustomCommands;
	private Map<String, TexCommand> fCustomEnvs;
	
	private byte[] fStackTypes = new byte[32];
	private String[] fStackEndKeys = new String[32];
	private int fStackPos = -1;
	private int fFoundEndStackPos;
	private int fFoundEndOffset;
	private TexAstNode fFoundEndNode;
	private boolean fInMath;
	private final Text fWhitespace = new Text(null, -1, -1);
	private boolean fWasLinebreak;
	
	private List<Embedded> fEmbeddedList;
	
	private final IStringCache fLabelFactory;
	private final IStringCache fOtherFactory;
	
	
	public LtxParser(final LtxLexer lexer) {
		this(lexer, null);
	}
	
	public LtxParser(final LtxLexer lexer, final IStringCache labelFactory) {
		fLexer = (lexer != null) ? lexer : new LtxLexer();
		fLexer.setReportSquaredBrackets(true);
		fLexer.setCreateControlTexts(true);
		fLabelFactory = (labelFactory != null) ? labelFactory : InternStringCache.INSTANCE;
		fOtherFactory = (labelFactory != null) ? labelFactory : NoStringCache.INSTANCE;
	}
	
	private TexCommand getCommand(final String controlWord) {
		if (controlWord == "end") { //$NON-NLS-1$
			return LtxCommandDefinitions.GENERICENV_end_COMMAND;
		}
		TexCommand command;
		if (fInMath) {
			command = fCommandSet.getLtxMathCommandMap().get(controlWord);
		}
		else {
			command = fCommandSet.getLtxPreambleCommandMap().get(controlWord);
			if (command == null) {
				command = fCommandSet.getLtxTextCommandMap().get(controlWord);
			}
		}
		if (command != null) {
			return command;
		}
		if (fCustomCommands != null) {
			return fCustomCommands.get(controlWord);
		}
		return null;
	}
	
	private TexCommand getEnv(final String name) {
		TexCommand command = fCommandSet.getLtxInternEnvMap().get(name);
		if (command != null) {
			return command;
		}
		if (fInMath) {
			command = fCommandSet.getLtxMathEnvMap().get(name);
		}
		else {
			command = fCommandSet.getLtxTextEnvMap().get(name);
		}
		if (command != null) {
			return command;
		}
		if (fCustomEnvs != null) {
			return fCustomEnvs.get(name);
		}
		return null;
	}
	
	
	public void setCollectEmebeddedNodes(final boolean enable) {
		fEmbeddedList = (enable) ? new ArrayList<Embedded>(32) : null;
	}
	
	public List<Embedded> getEmbeddedNodes() {
		return fEmbeddedList;
	}
	
	public Map<String, TexCommand> getCustomCommandMap() {
		return fCustomCommands;
	}
	
	public Map<String, TexCommand> getCustomEnvMap() {
		return fCustomEnvs;
	}
	
	
	public SourceComponent parse(final SourceParseInput input, final TexCommandSet commandSet) {
		if (fEmbeddedList != null) {
			fEmbeddedList.clear();
		}
		fCustomCommands = null;
		fCustomEnvs = null;
		fCommandSet = commandSet;
		fLexer.setInput(input);
		fLexer.setFull();
		fFoundEndStackPos = -1;
		fStackPos = -1;
		fInMath = false;
		
		fLexer.setReport$$(true);
		fLexer.setReportAsterisk(false);
		fLexer.setReportSquaredBrackets(true);
		
		final SourceComponent node = new SourceComponent();
		if (fLexer.next() != LtxLexer.EOF) {
			node.fStartOffset = fLexer.getOffset();
		}
		putToStack(ST_ROOT, null);
		parseGroup(node);
		node.fStopOffset = fLexer.getStopOffset();
		
		return node;
	}
	
	
	private void putToStack(final byte type, final String key) {
		if (++fStackPos >= fStackTypes.length) {
			final byte[] newTypes = new byte[fStackEndKeys.length+16];
			System.arraycopy(fStackTypes, 0, newTypes, 0, fStackPos);
			fStackTypes = newTypes;
			final String[] newKeys = new String[fStackEndKeys.length+16];
			System.arraycopy(fStackEndKeys, 0, newKeys, 0, fStackPos);
			fStackEndKeys = newKeys;
		}
		fStackTypes[fStackPos] = type;
		fStackEndKeys[fStackPos] = key;
	}
	
	private void putToStack(final byte type, final byte argContent) {
		switch (argContent & 0xf0) {
		case Argument.CONTROLWORD:
		case Argument.LABEL:
			putToStack(type, LABEL_TEXT);
			break;
		case Argument.NUM:
			putToStack(type, NUM_TEXT);
			break;
		default:
			putToStack(type, null);
			break;
		}
	}
	
	private void parseGroup(final ContainerNode group) {
		final List<TexAstNode> children = new ArrayList<TexAstNode>();
		Text textNode = null;
		
		GROUP: while (fFoundEndStackPos < 0) {
			switch (fLexer.pop()) {
			case LtxLexer.EOF:
				group.fStopOffset = fLexer.getStopOffset();
				group.fStatus = STATUS2_GROUP_NOT_CLOSED;
				fFoundEndStackPos = 0;
				fFoundEndNode = null;
				break GROUP;
			case LtxLexer.CONTROL_WORD:
				{	final TexAstNode node = createAndParseWord();
					if (node != null) {
						node.fParent = group;
						children.add(node);
						textNode = null;
						continue GROUP;
					}
					else {
						break GROUP;
					}
				}
			case LtxLexer.CONTROL_CHAR:
				fWasLinebreak = false;
				fLexer.consume();
				{	final ControlNode.Char node = new ControlNode.Char(fLexer.getText());
					node.fStartOffset = fLexer.getOffset();
					node.fStopOffset = fLexer.getStopOffset();
					if (fInMath) {
						if (node.getText() == ")") {
							int endPos = fStackPos;
							while (endPos >= 0) {
								if (fStackTypes[endPos] == ST_MATHROUND) {
									fFoundEndStackPos = endPos;
									fFoundEndNode = node;
									break GROUP;
								}
								endPos--;
							}
						}
						else if (node.getText() == "]") {
							int endPos = fStackPos;
							while (endPos >= 0) {
								if (fStackTypes[endPos] == ST_MATHSQUARED) {
									fFoundEndStackPos = endPos;
									fFoundEndNode = node;
									break GROUP;
								}
								endPos--;
							}
						}
						node.fParent = group;
						children.add(node);
						textNode = null;
						continue GROUP;
					}
					else {
						if (node.getText() == "(") {
							final Environment env = new Environment();
							env.fParent = group;
							env.fStartOffset = node.getOffset();
							env.fBegin = node;
							node.fParent = env;
							putToStack(ST_MATHROUND, null);
							parseGroup(env);
							textNode = null;
							continue GROUP;
						}
						else if (node.getText() == "[") {
							final Environment env = new Environment();
							env.fParent = group;
							env.fStartOffset = node.getOffset();
							env.fBegin = node;
							node.fParent = env;
							putToStack(ST_MATHSQUARED, null);
							parseGroup(env);
							textNode = null;
							continue GROUP;
						}
						node.fParent = group;
						children.add(node);
						textNode = null;
						continue GROUP;
					}
				}
			case LtxLexer.CURLY_BRACKET_OPEN:
				fWasLinebreak = false;
				fLexer.consume();
				{	final Group node = new Group.Bracket(group, fLexer.getOffset(), fLexer.getStopOffset());
					node.fStartOffset = fLexer.getOffset();
					node.fStopOffset = fLexer.getStopOffset();
					putToStack(ST_CURLY, null);
					parseGroup(node);
					node.fParent = group;
					children.add(node);
					textNode = null;
					continue GROUP;
				}
			case LtxLexer.CURLY_BRACKET_CLOSE:
				fWasLinebreak = false;
				fLexer.consume();
				if (fStackTypes[fStackPos] == ST_CURLY) {
					fFoundEndStackPos = fStackPos;
					fFoundEndOffset = fLexer.getStopOffset();
					textNode = null;
					break GROUP;
				}
				else {
					final Dummy node = new Dummy();
					node.fStatus = STATUS2_GROUP_NOT_OPENED;
					node.fStartOffset = fLexer.getOffset();
					node.fStopOffset = fLexer.getStopOffset();
					node.fParent = group;
					children.add(node);
					textNode = null;
					continue GROUP;
				}
			case LtxLexer.SQUARED_BRACKET_CLOSE:
				fWasLinebreak = false;
				fLexer.consume();
				if (fStackTypes[fStackPos] == ST_SQUARED) {
					fFoundEndStackPos = fStackPos;
					fFoundEndOffset = fLexer.getStopOffset();
					break GROUP;
				}
				if (textNode == null) {
					textNode = new Text(group, fLexer.getOffset(), fLexer.getStopOffset());
					children.add(textNode);
					continue GROUP;
				}
				else if (textNode == fWhitespace) {
					textNode = new Text(group, textNode.fStartOffset, fLexer.getStopOffset());
					children.add(textNode);
				}
				else {
					textNode.fStopOffset = fLexer.getStopOffset();
					continue GROUP;
				}
			case LtxLexer.WHITESPACE:
				fLexer.consume();
				if (textNode == null) {
					if (children.size() > 0) {
						textNode = fWhitespace;
						textNode.fStartOffset = fLexer.getOffset();
						continue GROUP;
					}
					continue GROUP;
				}
				else if (textNode != fWhitespace) {
					textNode.fStopOffset = fLexer.getStopOffset();
					continue GROUP;
				}
				else {
					continue GROUP;
				}
			case LtxLexer.DEFAULT_TEXT:
				fWasLinebreak = false;
				fLexer.consume();
				if (fStackEndKeys[fStackPos] == LABEL_TEXT && children.isEmpty()) {
					final Label node = new Label(group, fLexer.getOffset(), fLexer.getStopOffset(),
							fLexer.getFullText(fLabelFactory) );
					node.fStartOffset = fLexer.getOffset();
					node.fStopOffset = fLexer.getStopOffset();
					node.fParent = group;
					children.add(node);
					continue GROUP;
				}
				if (fStackEndKeys[fStackPos] == NUM_TEXT && children.isEmpty()) {
					final Text node = new Text.Num(group, fLexer.getOffset(), fLexer.getStopOffset(),
							checkNum(fLexer.getFullText(fOtherFactory)) );
					node.fStartOffset = fLexer.getOffset();
					node.fStopOffset = fLexer.getStopOffset();
					node.fParent = group;
					children.add(node);
					continue GROUP;
				}
				if (textNode == null) {
					textNode = new Text(group, fLexer.getOffset(), fLexer.getStopOffset());
					children.add(textNode);
					continue GROUP;
				}
				else if (textNode == fWhitespace) {
					textNode = new Text(group, textNode.fStartOffset, fLexer.getStopOffset());
					children.add(textNode);
					continue GROUP;
				}
				else {
					textNode.fStopOffset = fLexer.getStopOffset();
					continue GROUP;
				}
			case LtxLexer.CONTROL_NONE:
			case LtxLexer.SQUARED_BRACKET_OPEN:
				fWasLinebreak = false;
				fLexer.consume();
				if (textNode == null) {
					textNode = new Text(group, fLexer.getOffset(), fLexer.getStopOffset());
					children.add(textNode);
					continue GROUP;
				}
				else {
					textNode.fStopOffset = fLexer.getStopOffset();
					continue GROUP;
				}
			case LtxLexer.LINEBREAK:
				fWasLinebreak = true;
				fLexer.consume();
				textNode = null;
				continue GROUP;
			case LtxLexer.MATH_$:
				fWasLinebreak = false;
				fLexer.consume();
				if (fInMath) {
					int endPos = fStackPos;
					while (endPos >= 0) {
						if (fStackTypes[endPos] == ST_MATHDOLLAR) {
							fFoundEndStackPos = endPos;
							fFoundEndOffset = fLexer.getStopOffset();
							break GROUP;
						}
						endPos--;
					}
					if (textNode == null || textNode == fWhitespace) {
						textNode = new Text(group, fLexer.getOffset(), fLexer.getStopOffset());
						children.add(textNode);
						continue GROUP;
					}
					textNode.fStopOffset = fLexer.getStopOffset();
					continue GROUP;
				}
				else {
					putToStack(ST_MATHDOLLAR, null);
					fInMath = true;
					fLexer.setReport$$(false);
					final Math node = new Math.SingleDollar(group, fLexer.getOffset(), fLexer.getStopOffset());
					parseGroup(node);
					fInMath = false;
					fLexer.setReport$$(true);
					children.add(node);
					textNode = null;
					continue GROUP;
				}
			case LtxLexer.MATH_$$:
				fWasLinebreak = false;
				fLexer.consume();
				if (fInMath) {
					int endPos = fStackPos;
					while (endPos >= 0) {
						if (fStackTypes[endPos] == ST_MATHDOLLARDOLLAR) {
							fFoundEndStackPos = endPos;
							fFoundEndOffset = fLexer.getStopOffset();
							break GROUP;
						}
						endPos--;
					}
					group.fStopOffset = fLexer.getStopOffset();
					if (textNode == null || textNode == fWhitespace) {
						textNode = new Text(group, fLexer.getOffset(), fLexer.getStopOffset());
						children.add(textNode);
						continue GROUP;
					}
					textNode.fStopOffset = fLexer.getStopOffset();
					continue GROUP;
				}
				else {
					putToStack(ST_MATHDOLLARDOLLAR, null);
					fInMath = true;
					final Math node = new Math.DoubleDollar(group, fLexer.getOffset(), fLexer.getStopOffset());
					parseGroup(node);
					fInMath = false;
					children.add(node);
					textNode = null;
					continue GROUP;
				}
			case LtxLexer.LINE_COMMENT:
				handleComment();
				continue GROUP;
			case LtxLexer.VERBATIM_TEXT:
				fWasLinebreak = false;
				fLexer.consume();
				{	final Verbatim node = new Verbatim();
					node.fParent = group;
					node.fStartOffset = fLexer.getOffset();
					node.fStopOffset = fLexer.getStopOffset();
					children.add(node);
					textNode = null;
					continue GROUP;
				}
			case LtxLexer.EMBEDDED:
				fWasLinebreak = false;
				fLexer.consume();
				{	final Embedded node = new Embedded(group, fLexer.getOffset(), fLexer.getStopOffset(),
							fLexer.getText().intern() );
					children.add(node);
					if (fEmbeddedList != null) {
						fEmbeddedList.add(node);
					}
					textNode = null;
					continue GROUP;
				}
			case LtxLexer.ASTERISK:
			default:
				throw new IllegalStateException("type="+fLexer.getType()+",offset="+fLexer.getOffset());
			}
		}
		
		if (!children.isEmpty()) {
			group.fChildren = children.toArray(new TexAstNode[children.size()]);
		}
		
//		if (fFoundEndStackPos >= 0) {
			if (fFoundEndStackPos == fStackPos) {
				group.setEndNode(fFoundEndOffset, fFoundEndNode);
				fFoundEndStackPos = -1;
			}
			else {
				group.setMissingEnd();
			}
//		}
		fStackPos--;
	}
	
	private TexAstNode createAndParseWord() {
		String label = null;
		fWasLinebreak = false;
		fLexer.consume();
		final ControlNode.Word controlNode = new ControlNode.Word(label = fLexer.getText());
		controlNode.fStartOffset = fLexer.getOffset();
		controlNode.fStopOffset = fLexer.getStopOffset();
		
		TexCommand command = getCommand(label);
		if (command == null) {
			if (fLexer.pop() == LtxLexer.WHITESPACE) {
				fLexer.consume();
			}
			return controlNode;
		}
		
		if (command.supportAsterisk()) {
			fLexer.setReportAsterisk(true);
			ASTERISK: while (true) {
				switch (fLexer.pop()) {
				case LtxLexer.WHITESPACE:
					fLexer.consume();
					continue ASTERISK;
				case LtxLexer.ASTERISK:
					fWasLinebreak = false;
					fLexer.consume();
					break ASTERISK;
				default:
					break ASTERISK;
				}
			}
			fLexer.setReportAsterisk(false);
		}
		List<Argument> arguments;
		if (!(arguments = command.getArguments()).isEmpty()) {
			int nextArg = 0;
			final List<TexAstNode> children = new ArrayList<TexAstNode>();
			ARGUMENTS: while (fFoundEndStackPos < 0 && nextArg < arguments.size()) {
				Argument argument = arguments.get(nextArg);
				boolean optional = ((argument.getType() & Argument.OPTIONAL) != 0);
				final ContainerNode argNode;
				switch (fLexer.pop()) {
				case LtxLexer.WHITESPACE:
					fLexer.consume();
					continue ARGUMENTS;
				case LtxLexer.LINEBREAK:
					if (fWasLinebreak) {
						break ARGUMENTS;
					}
					fWasLinebreak = true;
					fLexer.consume();
					continue ARGUMENTS;
				case LtxLexer.SQUARED_BRACKET_OPEN:
					if (!optional) {
						break ARGUMENTS;
					}
					
					fWasLinebreak = false;
					fLexer.consume();
					argNode = new Group.Square(controlNode, fLexer.getOffset(), fLexer.getStopOffset());
					putToStack(ST_SQUARED, argument.getContent());
					if (argument.getContent() == Argument.EMBEDDED) {
						parseEmbedGroup(argNode, ((TexEmbedCommand) command).getEmbeddedType());
					}
					else {
						parseGroup(argNode);
					}
					controlNode.fStopOffset = argNode.fStopOffset;
					children.add(argNode);
					
					nextArg++;
					continue ARGUMENTS;
				case LtxLexer.CURLY_BRACKET_OPEN:
					while (optional) {
						if (++nextArg >= arguments.size()) {
							break ARGUMENTS;
						}
						argument = arguments.get(nextArg);
						optional = (argument.getType() == Argument.OPTIONAL);
					}
					if (argument.getType() != Argument.REQUIRED) {
						break ARGUMENTS;
					}
					
					fWasLinebreak = false;
					fLexer.consume();
					argNode = new Group.Bracket(controlNode, fLexer.getOffset(), fLexer.getStopOffset());
					putToStack(ST_CURLY, argument.getContent());
					if (argument.getContent() == Argument.EMBEDDED) {
						parseEmbedGroup(argNode, ((TexEmbedCommand) command).getEmbeddedType());
					}
					else {
						parseGroup(argNode);
					}
					controlNode.fStopOffset = argNode.fStopOffset;
					children.add(argNode);
					
					if (command.getType() == TexCommand.C2_GENERICENV_BEGIN) {
						if (argNode.fStatus == 0 && argNode.fChildren.length == 1
								&& argNode.fChildren[0].getNodeType() == NodeType.LABEL) {
							final TexCommand envCommand = getEnv(label = argNode.fChildren[0].getText());
							if (envCommand != null) {
								command = envCommand;
								arguments = command.getArguments();
							}
						}
						else {
							break ARGUMENTS;
						}
					}
					
					nextArg++;
					continue ARGUMENTS;
				default:
					break ARGUMENTS;
				}
			}
			
			controlNode.fArguments = children.toArray(new TexAstNode[children.size()]);
		}
		
		controlNode.fCommand = command;
		
		if (fFoundEndStackPos >= 0) {
			return controlNode;
		}
		
		switch (command.getType() & TexCommand.MASK_C2) {
		case TexCommand.C2_ENV_VERBATIM_BEGIN:
		case TexCommand.C2_ENV_COMMENT_BEGIN:
			if (fLexer.getType() != 0) {
				break;
			}
			fLexer.setModeVerbatimEnv(("end{"+label+"}").toCharArray());
			if (fLexer.next() == LtxLexer.VERBATIM_TEXT) {
				final Environment envNode = new Environment();
				envNode.fBegin = controlNode;
				envNode.fStartOffset = controlNode.fStartOffset;
				putToStack(ST_BEGINEND, label);
				parseGroup(envNode);
				return envNode;
			}
			else {
				throw new IllegalStateException();
			}
		case TexCommand.VERBATIM_INLINE:
			if (fLexer.getType() != 0) {
				break;
			}
			fLexer.setModeVerbatimLine();
			if (fLexer.next() == LtxLexer.VERBATIM_TEXT) {
				fWasLinebreak = false;
				fLexer.consume();
				final TexAstNode verbatimNode;
				switch (fLexer.getSubtype()) {
				case LtxLexer.SUB_OPEN_MISSING:
//					verbatimNode = new Dummy();
//					verbatimNode.fParent = controlNode;
//					verbatimNode.fStatus = STATUS2_VERBATIM_INLINE_C_MISSING;
//					verbatimNode.fStartOffset = verbatimNode.fStopOffset = controlNode.fStopOffset =
//							fLexer.getStopOffset();
					controlNode.fStatus = STATUS2_VERBATIM_INLINE_C_MISSING;
					return controlNode;
				case LtxLexer.SUB_CLOSE_MISSING:
					verbatimNode = new Verbatim();
					verbatimNode.fParent = controlNode;
					verbatimNode.fStatus = STATUS2_VERBATIM_INLINE_NOT_CLOSED;
					verbatimNode.fStartOffset = fLexer.getOffset() + 1;
					verbatimNode.fStopOffset = controlNode.fStopOffset =
							fLexer.getStopOffset();
					break;
				default:
					verbatimNode = new Verbatim();
					verbatimNode.fParent = controlNode;
					verbatimNode.fStartOffset = fLexer.getOffset() + 1;
					verbatimNode.fStopOffset = controlNode.fStopOffset =
							fLexer.getStopOffset() - 1;
					break;
				}
				controlNode.fArguments = new TexAstNode[] { verbatimNode };
				
				return controlNode;
			}
			else {
				throw new IllegalStateException();
			}
		case TexCommand.C2_ENV_MATH_BEGIN:
			{
				final Environment envNode = new Environment();
				controlNode.fParent = envNode;
				envNode.fStartOffset = controlNode.fStartOffset;
				envNode.fBegin = controlNode;
				putToStack(ST_BEGINEND, label);
				fInMath = true;
				parseGroup(envNode);
				fInMath = false;
				return envNode;
			}
		case TexCommand.C2_GENERICENV_BEGIN:
		case TexCommand.C2_ENV_DOCUMENT_BEGIN:
		case TexCommand.C2_ENV_ELEMENT_BEGIN:
		case TexCommand.C2_ENV_MATHCONTENT_BEGIN:
		case TexCommand.C2_ENV_OTHER_BEGIN:
			if (label != null && label != "begin") {
				final Environment envNode = new Environment();
				controlNode.fParent = envNode;
				envNode.fStartOffset = controlNode.fStartOffset;
				envNode.fBegin = controlNode;
				putToStack(ST_BEGINEND, label);
				parseGroup(envNode);
				return envNode;
			}
			else {
				controlNode.fStatus = STATUS2_ENV_MISSING_NAME;
			}
			return controlNode;
		case TexCommand.C2_GENERICENV_END:
			if (controlNode.fArguments.length == 1) {
				final Group argNode = (Group) controlNode.fArguments[0];
				if (argNode.fStatus == 0 && argNode.fChildren.length == 1
						&& argNode.fChildren[0].getNodeType() == NodeType.LABEL) {
					label = argNode.fChildren[0].getText();
					int endPos = fStackPos;
					while (endPos >= 0) {
						if (fStackEndKeys[endPos] == label) {
							fFoundEndStackPos = endPos;
							fFoundEndNode = controlNode;
							return null;
						}
						endPos--;
					}
//					if (fStackEndKeys[fStackPos].length() > 1) {
//						fFoundEndStackPos = fStackPos;
//						fFoundEndNode = controlNode;
//						return null;
//					}
					controlNode.fStatus = STATUS2_ENV_NOT_OPENED;
				}
			}
			controlNode.fStatus = STATUS2_ENV_MISSING_NAME;
			return controlNode;
		case TexCommand.C2_PREAMBLE_CONTROLDEF:
			if (controlNode.fArguments.length > 0) {
				parseDef(controlNode, command);
			}
			return controlNode;
		default:
			break;
		}
		return controlNode;
	}
	
	private String checkNum(final String text) {
		if (text.isEmpty()) {
			return null;
		}
		for (int i = 0; i < text.length(); i++) {
			final char c = text.charAt(i);
			if (c < '0' || c > '9') {
				return null;
			}
		}
		return text;
	}
	
	private void parseDef(final ControlNode node, final TexCommand command) {
		int type = 0;
		if ((command.getType() & TexCommand.MASK_C3) == TexCommand.C3_PREAMBLE_CONTROLDEF_ENV) {
			type = 1;
		}
		final String controlWord;
		final TexAstNode[] argNodes = TexAst.resolveArguments(node);
		TexAstNode argNode;
		if (argNodes[0] == null || argNodes[0].fStatus != 0 || argNodes[0].getChildCount() != 1) {
			return;
		}
		argNode = argNodes[0].getChild(0);
		if (type == 1 && argNode.getNodeType() == NodeType.LABEL
				&& argNode.getText().length() > 0) {
			controlWord = argNode.getText();
		}
		else if (argNode.getNodeType() == NodeType.CONTROL
				&& argNode.getText().length() > 0) {
			controlWord = argNode.getText();
		}
		else {
			return;
		}
		int optionalArgs = 0;
		int requiredArgs = 0;
		if (argNodes[1] != null && argNodes[1].fStatus == 0 && argNodes[1].getChildCount() == 1
				&& (argNode = argNodes[1].getChild(0)).getNodeType() == NodeType.TEXT
				&& argNode.getText() != null) {
			try {
				requiredArgs = Integer.parseInt(argNode.getText());
			}
			catch (final NumberFormatException e) {}
		}
		if (argNodes[2] != null && argNodes[2].fStatus == 0) {
			optionalArgs = 1;
			requiredArgs -= 1;
		}
		if (requiredArgs < 0) {
			requiredArgs = 0;
		}
		final Argument[] args = new Argument[optionalArgs + requiredArgs];
		{	int i = 0;
			while (optionalArgs-- > 0) {
				args[i++] = new Argument(Argument.OPTIONAL, Argument.NONE);
			}
			while (requiredArgs-- > 0) {
				args[i++] = new Argument(Argument.REQUIRED, Argument.NONE);
			}
		}
		{	final Map<String, TexCommand> map;
			if (type == 1) {
				if (fCustomEnvs == null) {
					fCustomEnvs = new HashMap<String, TexCommand>();
				}
				map = fCustomEnvs;
			}
			else {
				if (fCustomCommands == null) {
					fCustomCommands = new HashMap<String, TexCommand>();
				}
				map = fCustomCommands;
			}
			map.put(controlWord, new TexCommand(0, controlWord, false,
					new ConstList<Argument>(args), "(custom)"));
		}
	}
	
	private void handleComment() {
		fWasLinebreak = false;
		fLexer.consume();
	}
	
	private void parseEmbedGroup(final ContainerNode group, final String type) {
		final Embedded embedded = new Embedded.Inline(group, fLexer.getStopOffset(), type);
		GROUP: while (fFoundEndStackPos < 0) {
			switch (fLexer.pop()) {
			case LtxLexer.EOF:
				fFoundEndStackPos = 0;
				fFoundEndNode = null;
				break GROUP;
			case LtxLexer.LINEBREAK:
				break GROUP;
			case LtxLexer.CURLY_BRACKET_CLOSE:
				fWasLinebreak = false;
				fLexer.consume();
				if (fStackTypes[fStackPos] == ST_CURLY) {
					fFoundEndStackPos = fStackPos;
					fFoundEndOffset = fLexer.getStopOffset();
					break GROUP;
				}
				continue GROUP;
			case LtxLexer.SQUARED_BRACKET_CLOSE:
				fWasLinebreak = false;
				fLexer.consume();
				if (fStackTypes[fStackPos] == ST_SQUARED) {
					fFoundEndStackPos = fStackPos;
					fFoundEndOffset = fLexer.getStopOffset();
					break GROUP;
				}
				continue GROUP;
			default:
				fWasLinebreak = false;
				fLexer.consume();
				continue GROUP;
			}
		}
		
		embedded.fStopOffset = fLexer.getOffset();
		group.fChildren = new TexAstNode[] { embedded };
		if (fEmbeddedList != null) {
			fEmbeddedList.add(embedded);
		}
		
		if (fFoundEndStackPos == fStackPos) {
			group.setEndNode(fFoundEndOffset, fFoundEndNode);
			fFoundEndStackPos = -1;
		}
		else {
			group.setMissingEnd();
		}
		fStackPos--;
	}
	
}
