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

package de.walware.docmlet.tex.core.ast;

import static de.walware.docmlet.tex.core.ast.ITexAstStatusConstants.STATUS2_GROUP_NOT_CLOSED;

import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

import de.walware.docmlet.tex.core.ITexProblemConstants;
import de.walware.docmlet.tex.core.commands.Argument;
import de.walware.docmlet.tex.core.commands.IEnvDefinitions;


public class TexAst {
	
	
	/**
	 * Definitions of LaTeX AST node types
	 */
	public enum NodeType {
		
		
		SOURCELINES,
		COMMENT,
		ERROR,
		EMBEDDED,
		
		GROUP,
		ENVIRONMENT,
		VERBATIM,
		MATH,
		
		CONTROL,
		LABEL,
		TEXT,
		
	}
	
	
	/**
	 * Resolves the argument values (AST nodes) to the arguments defined by the command.
	 * The command have to be provided by the control node ({@link ControlNode#getCommand()}).
	 * 
	 * @param node the control node
	 * @return array with the resolved arguments (items can be <code>null</code>)
	 */
	public static TexAstNode[] resolveArguments(final ControlNode node) {
		final List<Argument> arguments = node.getCommand().getArguments();
		final TexAstNode[] resolved = new TexAstNode[arguments.size()];
		int idxArgs = 0, idxValues = 0;
		while (idxArgs < resolved.length && idxValues < node.getChildCount()) {
			final TexAstNode child = node.getChild(idxValues);
			if ((arguments.get(idxArgs).getType() & Argument.OPTIONAL) != 0) {
				if (child.getText() == "[") {
					idxValues++;
					resolved[idxArgs++] = child;
					continue;
				}
				else {
					idxArgs++;
					continue;
				}
			}
			else {
				if (child.getText() == "[") {
					idxValues++;
					continue;
				}
				else {
					idxValues++;
					resolved[idxArgs++] = child;
				}
			}
		}
		return resolved;
	}
	
	/**
	 * Returns the index in the array for the node at the specified offset
	 * 
	 * @param nodes array with nodes (items can be <code>null</code>)
	 * @param offset the offset of the searched node
	 * @return the index in the array if found, otherwise -1
	 */
	public static int getIndexAt(final TexAstNode[] nodes, final int offset) {
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] != null) {
				if (offset < nodes[i].getOffset()) {
					return -1;
				}
				if (offset <= nodes[i].getStopOffset()) {
					return i;
				}
			}
		}
		return -1;
	}
	
	/**
	 * At moment only for groups
	 * 
	 * @param node
	 * @return
	 */
	public static IRegion getInnerRegion(final TexAstNode node) {
		if ((node.getStatusCode() & ITexProblemConstants.MASK_12) == STATUS2_GROUP_NOT_CLOSED) {
			return new Region(node.getOffset()+1, node.getLength()-1);
		}
		else {
			return new Region(node.getOffset()+1, node.getLength()-2);
		}
	}
	
	public static Environment getDocumentNode(final TexAstNode node) {
		final int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			final TexAstNode child = node.getChild(i);
			if (child.getNodeType() == NodeType.ENVIRONMENT
					&& ((Environment) node).getBeginNode().getCommand() == IEnvDefinitions.ENV_document_BEGIN) {
				return (Environment) child;
			}
		}
		return null;
	}
	
	
}
