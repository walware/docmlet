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

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.ltk.ast.IAstNode;
import de.walware.ecommons.string.IStringFactory;
import de.walware.ecommons.string.StringFactory;
import de.walware.ecommons.text.core.input.TextParserInput;

import de.walware.docmlet.tex.core.ast.TexAst.NodeType;
import de.walware.docmlet.tex.core.commands.Argument;
import de.walware.docmlet.tex.core.commands.IEnvDefinitions;
import de.walware.docmlet.tex.core.commands.LtxCommandDefinitions;
import de.walware.docmlet.tex.core.commands.TexCommand;
import de.walware.docmlet.tex.core.commands.TexCommandSet;
import de.walware.docmlet.tex.core.commands.TexEmbedCommand;
import de.walware.docmlet.tex.core.parser.ICustomScanner;
import de.walware.docmlet.tex.core.parser.LtxLexer;


public class LtxParser {
	
	
	private static final byte ST_ROOT=                      0;
	private static final byte ST_CURLY=                     1;
	private static final byte ST_SQUARED=                   2;
	private static final byte ST_BEGINEND=                  3;
	private static final byte ST_MATHROUND=                 4;
	private static final byte ST_MATHSQUARED=               5;
	private static final byte ST_MATHDOLLAR=                6;
	private static final byte ST_MATHDOLLARDOLLAR=          7;
	
	private static final String LABEL_TEXT= new String("LABEL"); //$NON-NLS-1$
	private static final String NUM_TEXT= new String("NUM"); //$NON-NLS-1$
	
	
	private final LtxLexer lexer;
	
	private TexCommandSet commandSet;
	
	private Map<String, TexCommand> customCommands;
	private Map<String, TexCommand> customEnvs;
	
	private byte[] stackTypes= new byte[32];
	private String[] stackEndKeys= new String[32];
	private int stackPos= -1;
	private int foundEndStackPos;
	private int foundEndOffset;
	private TexAstNode foundEndNode;
	private boolean inMath;
	private final Text whitespace= new Text(null, -1, -1);
	private boolean wasLinebreak;
	
	private List<Embedded> embeddedList;
	
	private final IStringFactory symbolTextFactory;
	private final IStringFactory otherTextFactory;
	
	
	public LtxParser() {
		this(null, null);
	}
	
	public LtxParser(final LtxLexer lexer, final IStringFactory textCache) {
		this.lexer= (lexer != null) ? lexer : new LtxLexer();
		this.lexer.setReportSquaredBrackets(true);
		if (textCache != null) {
			this.symbolTextFactory= textCache;
			this.otherTextFactory= textCache;
		}
		else {
			this.symbolTextFactory= StringFactory.INSTANCE;
			this.otherTextFactory= StringFactory.INSTANCE;
		}
	}
	
	private TexCommand getCommand(final String controlWord) {
		if (controlWord == "end") { //$NON-NLS-1$
			return LtxCommandDefinitions.GENERICENV_end_COMMAND;
		}
		TexCommand command;
		if (this.inMath) {
			command= this.commandSet.getLtxMathCommandMap().get(controlWord);
		}
		else {
			command= this.commandSet.getLtxPreambleCommandMap().get(controlWord);
			if (command == null) {
				command= this.commandSet.getLtxTextCommandMap().get(controlWord);
			}
		}
		if (command != null) {
			return command;
		}
		if (this.customCommands != null) {
			return this.customCommands.get(controlWord);
		}
		return null;
	}
	
	private TexCommand getEnv(final String name) {
		TexCommand command= this.commandSet.getLtxInternEnvMap().get(name);
		if (command != null) {
			return command;
		}
		if (this.inMath) {
			command= this.commandSet.getLtxMathEnvMap().get(name);
		}
		else {
			command= this.commandSet.getLtxTextEnvMap().get(name);
		}
		if (command != null) {
			return command;
		}
		if (this.customEnvs != null) {
			return this.customEnvs.get(name);
		}
		return null;
	}
	
	
	public void setCollectEmebeddedNodes(final boolean enable) {
		this.embeddedList= (enable) ? new ArrayList<Embedded>(32) : null;
	}
	
	public List<Embedded> getEmbeddedNodes() {
		return this.embeddedList;
	}
	
	public Map<String, TexCommand> getCustomCommandMap() {
		return this.customCommands;
	}
	
	public Map<String, TexCommand> getCustomEnvMap() {
		return this.customEnvs;
	}
	
	
	public SourceComponent parse(final TextParserInput input, final TexCommandSet commandSet) {
		if (this.embeddedList != null) {
			this.embeddedList.clear();
		}
		this.customCommands= null;
		this.customEnvs= null;
		this.commandSet= commandSet;
		this.lexer.reset(input);
		this.foundEndStackPos= -1;
		this.stackPos= -1;
		this.inMath= false;
		
		this.lexer.setReport$$(true);
		this.lexer.setReportAsterisk(false);
		this.lexer.setReportSquaredBrackets(true);
		
		final SourceComponent node= new SourceComponent();
		if (this.lexer.next() != LtxLexer.EOF) {
			node.startOffset= this.lexer.getOffset();
		}
		putToStack(ST_ROOT, null);
		parseGroup(node);
		node.stopOffset= this.lexer.getStopOffset();
		
		return node;
	}
	
	public SourceComponent parse(final TextParserInput input, final IAstNode parent,
			final TexCommandSet commandSet) {
		if (this.embeddedList != null) {
			this.embeddedList.clear();
		}
		this.customCommands= null;
		this.customEnvs= null;
		this.commandSet= commandSet;
		this.lexer.reset(input);
		this.foundEndStackPos= -1;
		this.stackPos= -1;
		this.inMath= false;
		
		this.lexer.setReport$$(true);
		this.lexer.setReportAsterisk(false);
		this.lexer.setReportSquaredBrackets(true);
		
		final SourceComponent node= new SourceComponent(parent,
				input.getStartIndex(), input.getStopIndex() );
		if (this.lexer.next() != LtxLexer.EOF) {
			node.startOffset= this.lexer.getOffset();
		}
		putToStack(ST_ROOT, null);
		parseGroup(node);
		node.stopOffset= this.lexer.getStopOffset();
		
		return node;
	}
	
	
	private void putToStack(final byte type, final String key) {
		if (++this.stackPos >= this.stackTypes.length) {
			final byte[] newTypes= new byte[this.stackEndKeys.length+16];
			System.arraycopy(this.stackTypes, 0, newTypes, 0, this.stackPos);
			this.stackTypes= newTypes;
			final String[] newKeys= new String[this.stackEndKeys.length+16];
			System.arraycopy(this.stackEndKeys, 0, newKeys, 0, this.stackPos);
			this.stackEndKeys= newKeys;
		}
		this.stackTypes[this.stackPos]= type;
		this.stackEndKeys[this.stackPos]= key;
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
		final List<TexAstNode> children= new ArrayList<>();
		Text textNode= null;
		
		GROUP: while (this.foundEndStackPos < 0) {
			switch (this.lexer.pop()) {
			case LtxLexer.EOF:
				group.stopOffset= this.lexer.getStopOffset();
				group.status= STATUS2_GROUP_NOT_CLOSED;
				this.foundEndStackPos= 0;
				this.foundEndNode= null;
				break GROUP;
			case LtxLexer.CONTROL_WORD:
				{	final TexAstNode node= createAndParseWord();
					if (node != null) {
						node.texParent= group;
						children.add(node);
						textNode= null;
						continue GROUP;
					}
					else {
						break GROUP;
					}
				}
			case LtxLexer.CONTROL_CHAR:
				this.wasLinebreak= false;
				{	final ControlNode.Char node= new ControlNode.Char(
						this.lexer.getText(this.symbolTextFactory) );
					node.startOffset= this.lexer.getOffset();
					node.stopOffset= this.lexer.getStopOffset();
					if (this.inMath) {
						if (node.getText() == ")") { //$NON-NLS-1$
							int endPos= this.stackPos;
							while (endPos >= 0) {
								if (this.stackTypes[endPos] == ST_MATHROUND) {
									node.fCommand= IEnvDefinitions.ENV_math_END_SHORTHAND;
									this.foundEndStackPos= endPos;
									this.foundEndNode= node;
									this.lexer.consume();
									break GROUP;
								}
								endPos--;
							}
						}
						else if (node.getText() == "]") { //$NON-NLS-1$
							int endPos= this.stackPos;
							while (endPos >= 0) {
								if (this.stackTypes[endPos] == ST_MATHSQUARED) {
									node.fCommand= IEnvDefinitions.ENV_displaymath_END_SHORTHAND;
									this.foundEndStackPos= endPos;
									this.foundEndNode= node;
									this.lexer.consume();
									break GROUP;
								}
								endPos--;
							}
						}
						node.texParent= group;
						children.add(node);
						this.lexer.consume();
						textNode= null;
						continue GROUP;
					}
					else {
						if (node.getText() == "(") { //$NON-NLS-1$
							final Environment env= new Environment.MathLatexShorthand(group, node);
							children.add(env);
							this.lexer.consume();
							node.fCommand= IEnvDefinitions.ENV_math_BEGIN_SHORTHAND;
							node.texParent= env;
							putToStack(ST_MATHROUND, null);
							this.inMath= true;
							parseGroup(env);
							this.inMath= false;
							textNode= null;
							continue GROUP;
						}
						else if (node.getText() == "[") { //$NON-NLS-1$
							final Environment env= new Environment.MathLatexShorthand(group, node);
							children.add(env);
							this.lexer.consume();
							node.fCommand= IEnvDefinitions.ENV_displaymath_BEGIN_SHORTHAND;
							node.texParent= env;
							putToStack(ST_MATHSQUARED, null);
							this.inMath= true;
							parseGroup(env);
							this.inMath= false;
							textNode= null;
							continue GROUP;
						}
						node.texParent= group;
						children.add(node);
						this.lexer.consume();
						textNode= null;
						continue GROUP;
					}
				}
			case LtxLexer.CURLY_BRACKET_OPEN:
				this.wasLinebreak= false;
				{	final Group node= new Group.Bracket(group, this.lexer.getOffset(), this.lexer.getStopOffset());
					node.startOffset= this.lexer.getOffset();
					node.stopOffset= this.lexer.getStopOffset();
					children.add(node);
					this.lexer.consume();
					putToStack(ST_CURLY, null);
					parseGroup(node);
					node.texParent= group;
					textNode= null;
					continue GROUP;
				}
			case LtxLexer.CURLY_BRACKET_CLOSE:
				this.wasLinebreak= false;
				if (this.stackTypes[this.stackPos] == ST_CURLY) {
					this.foundEndStackPos= this.stackPos;
					this.foundEndOffset= this.lexer.getStopOffset();
					this.lexer.consume();
					textNode= null;
					break GROUP;
				}
				else {
					final Dummy node= new Dummy();
					node.status= STATUS2_GROUP_NOT_OPENED;
					node.startOffset= this.lexer.getOffset();
					node.stopOffset= this.lexer.getStopOffset();
					node.texParent= group;
					children.add(node);
					this.lexer.consume();
					textNode= null;
					continue GROUP;
				}
			case LtxLexer.SQUARED_BRACKET_CLOSE:
				this.wasLinebreak= false;
				if (this.stackTypes[this.stackPos] == ST_SQUARED) {
					this.foundEndStackPos= this.stackPos;
					this.foundEndOffset= this.lexer.getStopOffset();
					this.lexer.consume();
					break GROUP;
				}
				if (textNode == null) {
					textNode= new Text(group, this.lexer.getOffset(), this.lexer.getStopOffset());
					children.add(textNode);
					this.lexer.consume();
					continue GROUP;
				}
				else if (textNode == this.whitespace) {
					textNode= new Text(group, textNode.startOffset, this.lexer.getStopOffset());
					children.add(textNode);
					this.lexer.consume();
					continue GROUP;
				}
				else {
					textNode.stopOffset= this.lexer.getStopOffset();
					this.lexer.consume();
					continue GROUP;
				}
			case LtxLexer.WHITESPACE:
				if (textNode == null) {
					if (children.size() > 0) {
						textNode= this.whitespace;
						textNode.startOffset= this.lexer.getOffset();
						this.lexer.consume();
						continue GROUP;
					}
					this.lexer.consume();
					continue GROUP;
				}
				else if (textNode == this.whitespace) {
					this.lexer.consume();
					continue GROUP;
				}
				else {
					textNode.stopOffset= this.lexer.getStopOffset();
					this.lexer.consume();
					continue GROUP;
				}
			case LtxLexer.DEFAULT_TEXT:
				this.wasLinebreak= false;
				if (this.stackEndKeys[this.stackPos] == LABEL_TEXT && children.isEmpty()) {
					final Label node= new Label(group, this.lexer.getOffset(), this.lexer.getStopOffset(),
							this.lexer.getFullText(this.symbolTextFactory) );
					node.startOffset= this.lexer.getOffset();
					node.stopOffset= this.lexer.getStopOffset();
					node.texParent= group;
					children.add(node);
					this.lexer.consume();
					continue GROUP;
				}
				if (this.stackEndKeys[this.stackPos] == NUM_TEXT && children.isEmpty()) {
					final Text node= new Text.Num(group, this.lexer.getOffset(), this.lexer.getStopOffset(),
							checkNum(this.lexer.getFullText(this.otherTextFactory)) );
					node.startOffset= this.lexer.getOffset();
					node.stopOffset= this.lexer.getStopOffset();
					node.texParent= group;
					children.add(node);
					this.lexer.consume();
					continue GROUP;
				}
				if (textNode == null) {
					textNode= new Text(group, this.lexer.getOffset(), this.lexer.getStopOffset());
					children.add(textNode);
					this.lexer.consume();
					continue GROUP;
				}
				else if (textNode == this.whitespace) {
					textNode= new Text(group, textNode.startOffset, this.lexer.getStopOffset());
					children.add(textNode);
					this.lexer.consume();
					continue GROUP;
				}
				else {
					textNode.stopOffset= this.lexer.getStopOffset();
					this.lexer.consume();
					continue GROUP;
				}
			case LtxLexer.CONTROL_NONE:
			case LtxLexer.SQUARED_BRACKET_OPEN:
				this.wasLinebreak= false;
				if (textNode == null) {
					textNode= new Text(group, this.lexer.getOffset(), this.lexer.getStopOffset());
					children.add(textNode);
					this.lexer.consume();
					continue GROUP;
				}
				else {
					textNode.stopOffset= this.lexer.getStopOffset();
					this.lexer.consume();
					continue GROUP;
				}
			case LtxLexer.LINEBREAK:
				this.wasLinebreak= true;
				this.lexer.consume();
				textNode= null;
				continue GROUP;
			case LtxLexer.MATH_$:
				this.wasLinebreak= false;
				if (this.inMath) {
					int endPos= this.stackPos;
					while (endPos >= 0) {
						if (this.stackTypes[endPos] == ST_MATHDOLLAR) {
							this.foundEndStackPos= endPos;
							this.foundEndOffset= this.lexer.getStopOffset();
							this.lexer.consume();
							break GROUP;
						}
						endPos--;
					}
					if (textNode == null || textNode == this.whitespace) {
						textNode= new Text(group, this.lexer.getOffset(), this.lexer.getStopOffset());
						children.add(textNode);
						this.lexer.consume();
						continue GROUP;
					}
					textNode.stopOffset= this.lexer.getStopOffset();
					this.lexer.consume();
					continue GROUP;
				}
				else {
					final Math node= new Math.SingleDollar(group, this.lexer.getOffset(), this.lexer.getStopOffset());
					children.add(node);
					this.lexer.consume();
					putToStack(ST_MATHDOLLAR, null);
					this.inMath= true;
					this.lexer.setReport$$(false);
					parseGroup(node);
					this.inMath= false;
					this.lexer.setReport$$(true);
					textNode= null;
					continue GROUP;
				}
			case LtxLexer.MATH_$$:
				this.wasLinebreak= false;
				if (this.inMath) {
					int endPos= this.stackPos;
					while (endPos >= 0) {
						if (this.stackTypes[endPos] == ST_MATHDOLLARDOLLAR) {
							this.foundEndStackPos= endPos;
							this.foundEndOffset= this.lexer.getStopOffset();
							this.lexer.consume();
							break GROUP;
						}
						endPos--;
					}
					group.stopOffset= this.lexer.getStopOffset();
					if (textNode == null || textNode == this.whitespace) {
						textNode= new Text(group, this.lexer.getOffset(), this.lexer.getStopOffset());
						children.add(textNode);
						this.lexer.consume();
						continue GROUP;
					}
					textNode.stopOffset= this.lexer.getStopOffset();
					this.lexer.consume();
					continue GROUP;
				}
				else {
					final Math node= new Math.DoubleDollar(group, this.lexer.getOffset(), this.lexer.getStopOffset());
					children.add(node);
					this.lexer.consume();
					putToStack(ST_MATHDOLLARDOLLAR, null);
					this.inMath= true;
					parseGroup(node);
					this.inMath= false;
					textNode= null;
					continue GROUP;
				}
			case LtxLexer.LINE_COMMENT:
				handleComment();
				continue GROUP;
			case LtxLexer.VERBATIM_TEXT:
				this.wasLinebreak= false;
				{	final Verbatim node= new Verbatim();
					node.texParent= group;
					node.startOffset= this.lexer.getOffset();
					node.stopOffset= this.lexer.getStopOffset();
					children.add(node);
					this.lexer.consume();
					textNode= null;
					continue GROUP;
				}
			case LtxLexer.EMBEDDED:
				this.wasLinebreak= false;
				{	final Embedded node= new Embedded(group, this.lexer.getOffset(), this.lexer.getStopOffset(),
							this.lexer.getText().intern() );
					children.add(node);
					this.lexer.consume();
					if (this.embeddedList != null) {
						this.embeddedList.add(node);
					}
					textNode= null;
					continue GROUP;
				}
			case LtxLexer.ASTERISK:
			default:
				throw new IllegalStateException("type= " + this.lexer.getType() + ", offset= "+this.lexer.getOffset()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		if (!children.isEmpty()) {
			group.children= children.toArray(new TexAstNode[children.size()]);
		}
		
//		if (fFoundEndStackPos >= 0) {
			if (this.foundEndStackPos == this.stackPos) {
				group.setEndNode(this.foundEndOffset, this.foundEndNode);
				this.foundEndStackPos= -1;
			}
			else {
				group.setMissingEnd();
			}
//		}
		this.stackPos--;
	}
	
	private TexAstNode createAndParseWord() {
		String label= null;
		this.wasLinebreak= false;
		final ControlNode.Word controlNode= new ControlNode.Word(
				label= this.lexer.getText(this.symbolTextFactory) );
		controlNode.startOffset= this.lexer.getOffset();
		controlNode.stopOffset= this.lexer.getStopOffset();
		this.lexer.consume();
		
		TexCommand command= getCommand(label);
		if (command == null) {
			if (this.lexer.pop() == LtxLexer.WHITESPACE) {
				this.lexer.consume();
			}
			return controlNode;
		}
		
		if (command.supportAsterisk()) {
			this.lexer.setReportAsterisk(true);
			ASTERISK: while (true) {
				switch (this.lexer.pop()) {
				case LtxLexer.WHITESPACE:
					this.lexer.consume();
					continue ASTERISK;
				case LtxLexer.ASTERISK:
					this.wasLinebreak= false;
					this.lexer.consume();
					break ASTERISK;
				default:
					break ASTERISK;
				}
			}
			this.lexer.setReportAsterisk(false);
		}
		List<Argument> arguments;
		if (!(arguments= command.getArguments()).isEmpty()) {
			int nextArg= 0;
			final List<TexAstNode> children= new ArrayList<>();
			ARGUMENTS: while (this.foundEndStackPos < 0 && nextArg < arguments.size()) {
				Argument argument= arguments.get(nextArg);
				boolean optional= ((argument.getType() & Argument.OPTIONAL) != 0);
				final ContainerNode argNode;
				switch (this.lexer.pop()) {
				case LtxLexer.WHITESPACE:
					this.lexer.consume();
					continue ARGUMENTS;
				case LtxLexer.LINEBREAK:
					if (this.wasLinebreak) {
						break ARGUMENTS;
					}
					this.wasLinebreak= true;
					this.lexer.consume();
					continue ARGUMENTS;
				case LtxLexer.SQUARED_BRACKET_OPEN:
					if (!optional) {
						break ARGUMENTS;
					}
					
					this.wasLinebreak= false;
					argNode= new Group.Square(controlNode, this.lexer.getOffset(), this.lexer.getStopOffset());
					children.add(argNode);
					this.lexer.consume();
					putToStack(ST_SQUARED, argument.getContent());
					if (argument.getContent() == Argument.EMBEDDED) {
						consumeEmbedGroup(argNode, (TexEmbedCommand) command, nextArg);
					}
					else {
						parseGroup(argNode);
					}
					controlNode.stopOffset= argNode.stopOffset;
					
					nextArg++;
					continue ARGUMENTS;
				case LtxLexer.CURLY_BRACKET_OPEN:
					while (optional) {
						if (++nextArg >= arguments.size()) {
							break ARGUMENTS;
						}
						argument= arguments.get(nextArg);
						optional= (argument.getType() == Argument.OPTIONAL);
					}
					if (argument.getType() != Argument.REQUIRED) {
						break ARGUMENTS;
					}
					
					this.wasLinebreak= false;
					argNode= new Group.Bracket(controlNode, this.lexer.getOffset(), this.lexer.getStopOffset());
					children.add(argNode);
					this.lexer.consume();
					putToStack(ST_CURLY, argument.getContent());
					if (argument.getContent() == Argument.EMBEDDED) {
						consumeEmbedGroup(argNode, (TexEmbedCommand) command, nextArg);
					}
					else {
						parseGroup(argNode);
					}
					controlNode.stopOffset= argNode.stopOffset;
					
					if (command.getType() == TexCommand.C2_GENERICENV_BEGIN) {
						if (argNode.status == 0 && argNode.children.length == 1
								&& argNode.children[0].getNodeType() == NodeType.LABEL) {
							final TexCommand envCommand= getEnv(
									label= argNode.children[0].getText() );
							if (envCommand != null) {
								command= envCommand;
								arguments= command.getArguments();
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
			
			controlNode.arguments= children.toArray(new TexAstNode[children.size()]);
		}
		
		controlNode.fCommand= command;
		
		if (this.foundEndStackPos >= 0) {
			return controlNode;
		}
		
		switch (command.getType() & TexCommand.MASK_C2) {
		case TexCommand.C2_ENV_VERBATIM_BEGIN:
		case TexCommand.C2_ENV_COMMENT_BEGIN:
			if (this.lexer.getType() != 0) {
				break;
			}
			this.lexer.setModeVerbatimEnv(("end{" + label + "}").toCharArray()); //$NON-NLS-1$ //$NON-NLS-2$
			if (this.lexer.next() == LtxLexer.VERBATIM_TEXT) {
				final Environment envNode= new Environment.Word(null, controlNode);
				putToStack(ST_BEGINEND, label);
				parseGroup(envNode);
				return envNode;
			}
			else {
				throw new IllegalStateException();
			}
		case TexCommand.VERBATIM_INLINE:
			if (this.lexer.getType() != 0) {
				break;
			}
			this.lexer.setModeVerbatimLine();
			if (this.lexer.next() == LtxLexer.VERBATIM_TEXT) {
				this.wasLinebreak= false;
				final TexAstNode verbatimNode;
				switch (this.lexer.getFlags()) {
				case LtxLexer.SUB_OPEN_MISSING:
//					verbatimNode= new Dummy();
//					verbatimNode.fParent= controlNode;
//					verbatimNode.fStatus= STATUS2_VERBATIM_INLINE_C_MISSING;
//					verbatimNode.fStartOffset= verbatimNode.fStopOffset= controlNode.fStopOffset =
//							fLexer.getStopOffset();
					controlNode.status= STATUS2_VERBATIM_INLINE_C_MISSING;
					this.lexer.consume();
					return controlNode;
				case LtxLexer.SUB_CLOSE_MISSING:
					verbatimNode= new Verbatim();
					verbatimNode.texParent= controlNode;
					verbatimNode.status= STATUS2_VERBATIM_INLINE_NOT_CLOSED;
					verbatimNode.startOffset= this.lexer.getOffset() + 1;
					verbatimNode.stopOffset= controlNode.stopOffset =
							this.lexer.getStopOffset();
					this.lexer.consume();
					break;
				default:
					verbatimNode= new Verbatim();
					verbatimNode.texParent= controlNode;
					verbatimNode.startOffset= this.lexer.getOffset() + 1;
					verbatimNode.stopOffset= controlNode.stopOffset =
							this.lexer.getStopOffset() - 1;
					this.lexer.consume();
					break;
				}
				controlNode.arguments= new TexAstNode[] { verbatimNode };
				
				return controlNode;
			}
			else {
				throw new IllegalStateException();
			}
		case TexCommand.C2_ENV_MATH_BEGIN:
			{
				final Environment envNode= new Environment.Word(null, controlNode);
				controlNode.texParent= envNode;
				putToStack(ST_BEGINEND, label);
				this.inMath= true;
				parseGroup(envNode);
				this.inMath= false;
				return envNode;
			}
		case TexCommand.C2_GENERICENV_BEGIN:
		case TexCommand.C2_ENV_DOCUMENT_BEGIN:
		case TexCommand.C2_ENV_ELEMENT_BEGIN:
		case TexCommand.C2_ENV_MATHCONTENT_BEGIN:
		case TexCommand.C2_ENV_OTHER_BEGIN:
			if (label != null && label != "begin") { //$NON-NLS-1$
				final Environment envNode= new Environment.Word(null, controlNode);
				controlNode.texParent= envNode;
				putToStack(ST_BEGINEND, label);
				parseGroup(envNode);
				return envNode;
			}
			else {
				controlNode.status= STATUS2_ENV_MISSING_NAME;
			}
			return controlNode;
		case TexCommand.C2_GENERICENV_END:
			if (controlNode.arguments.length == 1) {
				final Group argNode= (Group) controlNode.arguments[0];
				if (argNode.status == 0 && argNode.children.length == 1
						&& argNode.children[0].getNodeType() == NodeType.LABEL) {
					label= argNode.children[0].getText();
					int endPos= this.stackPos;
					while (endPos >= 0) {
						if (this.stackEndKeys[endPos] == label) {
							this.foundEndStackPos= endPos;
							this.foundEndNode= controlNode;
							return null;
						}
						endPos--;
					}
//					if (fStackEndKeys[fStackPos].length() > 1) {
//						fFoundEndStackPos= fStackPos;
//						fFoundEndNode= controlNode;
//						return null;
//					}
					controlNode.status= STATUS2_ENV_NOT_OPENED;
				}
			}
			controlNode.status= STATUS2_ENV_MISSING_NAME;
			return controlNode;
		case TexCommand.C2_PREAMBLE_CONTROLDEF:
			if (controlNode.arguments.length > 0) {
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
		for (int i= 0; i < text.length(); i++) {
			final char c= text.charAt(i);
			if (c < '0' || c > '9') {
				return null;
			}
		}
		return text;
	}
	
	private void parseDef(final ControlNode node, final TexCommand command) {
		final int type;
		switch ((command.getType() & TexCommand.MASK_C3)) {
		case TexCommand.C3_PREAMBLE_CONTROLDEF_COMMAND:
			type= 0;
			break;
		case TexCommand.C3_PREAMBLE_CONTROLDEF_ENV:
			type= 1;
			break;
		default:
			return;
		}
		
		final String controlWord;
		final TexAstNode[] argNodes= TexAst.resolveArguments(node);
		TexAstNode argNode;
		if (argNodes[0] == null || argNodes[0].status != 0 || argNodes[0].getChildCount() != 1) {
			return;
		}
		argNode= argNodes[0].getChild(0);
		if (type == 1 && argNode.getNodeType() == NodeType.LABEL
				&& argNode.getText().length() > 0) {
			controlWord= argNode.getText();
		}
		else if (argNode.getNodeType() == NodeType.CONTROL
				&& argNode.getText().length() > 0) {
			controlWord= argNode.getText();
		}
		else {
			return;
		}
		int optionalArgs= 0;
		int requiredArgs= 0;
		if (argNodes[1] != null && argNodes[1].status == 0 && argNodes[1].getChildCount() == 1
				&& (argNode= argNodes[1].getChild(0)).getNodeType() == NodeType.TEXT
				&& argNode.getText() != null) {
			try {
				requiredArgs= Integer.parseInt(argNode.getText());
			}
			catch (final NumberFormatException e) {}
		}
		if (argNodes[2] != null && argNodes[2].status == 0) {
			optionalArgs= 1;
			requiredArgs -= 1;
		}
		if (requiredArgs < 0) {
			requiredArgs= 0;
		}
		final Argument[] args= new Argument[optionalArgs + requiredArgs];
		{	int i= 0;
			while (optionalArgs-- > 0) {
				args[i++]= new Argument(Argument.OPTIONAL, Argument.NONE);
			}
			while (requiredArgs-- > 0) {
				args[i++]= new Argument(Argument.REQUIRED, Argument.NONE);
			}
		}
		{	final Map<String, TexCommand> map;
			if (type == 1) {
				if (this.customEnvs == null) {
					this.customEnvs= new HashMap<>();
				}
				map= this.customEnvs;
			}
			else {
				if (this.customCommands == null) {
					this.customCommands= new HashMap<>();
				}
				map= this.customCommands;
			}
			map.put(controlWord, new TexCommand(0, controlWord, false,
					ImCollections.newList(args), "(custom)"));
		}
	}
	
	private void handleComment() {
		this.wasLinebreak= false;
		this.lexer.consume();
	}
	
	private void consumeEmbedGroup(final ContainerNode group, final TexEmbedCommand command, final int argIdx) {
		final Embedded embedded= new Embedded.Inline(group, this.lexer.getStopOffset(), command.getEmbeddedType(argIdx));
		
		this.wasLinebreak= false;
		
		final ICustomScanner scanner= command.getArgumentScanner(argIdx);
		byte s= (scanner != null) ? scanner.consume(this.lexer) : consumeEmbeddedDefault();
		if (s == 0) {
			s= this.lexer.pop();
		}
		
		switch (s) {
		case LtxLexer.EOF:
			this.foundEndStackPos= 0;
			this.foundEndNode= null;
			break;
		case LtxLexer.LINEBREAK:
			break;
		case LtxLexer.CURLY_BRACKET_CLOSE:
			if (this.stackTypes[this.stackPos] == ST_CURLY) {
				this.foundEndStackPos= this.stackPos;
				this.foundEndOffset= this.lexer.getStopOffset();
			}
			break;
		case LtxLexer.SQUARED_BRACKET_CLOSE:
			if (this.stackTypes[this.stackPos] == ST_SQUARED) {
				this.foundEndStackPos= this.stackPos;
				this.foundEndOffset= this.lexer.getStopOffset();
			}
			break;
		default:
			break;
		}
		
		embedded.stopOffset= this.lexer.getOffset();
		group.children= new TexAstNode[] { embedded };
		if (this.embeddedList != null) {
			this.embeddedList.add(embedded);
		}
		
		if (this.foundEndStackPos == this.stackPos) {
			group.setEndNode(this.foundEndOffset, this.foundEndNode);
			this.foundEndStackPos= -1;
		}
		else {
			group.setMissingEnd();
		}
		this.stackPos--;
	}
	
	
	public byte consumeEmbeddedDefault() {
		final TextParserInput input= lexer.getInput();
		lexer.consume(true);
		
		final int endChar;
		final byte endReturn;
		switch (this.stackTypes[this.stackPos]) {
		case ST_SQUARED:
			endChar= ']';
			endReturn= LtxLexer.CURLY_BRACKET_CLOSE;
			break;
		case ST_CURLY:
			endChar= '}';
			endReturn= LtxLexer.SQUARED_BRACKET_CLOSE;
			break;
		default:
			throw new IllegalStateException("stateType= " + this.stackTypes[this.stackPos]); //$NON-NLS-1$
		}
		
		int offset= 0;
		while (true) {
			final int c= input.get(offset++);
			if (c < 0x20) {
				input.consume(offset - 1);
				lexer.consume(true);
				return 0;
			}
			if (c == endChar) {
				input.consume(offset);
				lexer.consume(true);
				return endReturn;
			}
		}
	}
	
}
